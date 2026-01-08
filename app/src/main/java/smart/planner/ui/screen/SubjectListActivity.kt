package smart.planner.ui.screen

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import smart.planner.R
import smart.planner.data.model.Subject
import smart.planner.ui.adapter.SubjectAdapter
import smart.planner.ui.viewmodel.SubjectViewModel

class SubjectListActivity : AppCompatActivity() {

    private lateinit var subjectViewModel: SubjectViewModel
    private lateinit var subjectAdapter: SubjectAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: View
    private lateinit var sharedPreferences: SharedPreferences
    private var currentUserId: Int = 1 // Default user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_list)

        // Get current user from SharedPreferences
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        currentUserId = sharedPreferences.getInt("userId", 1)

        // Initialize ViewModel
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        subjectViewModel = ViewModelProvider(this, factory)[SubjectViewModel::class.java]

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewSubjects)
        emptyState = findViewById(R.id.emptyState)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val fabAddSubject = findViewById<FloatingActionButton>(R.id.fabAddSubject)

        // Setup RecyclerView
        setupRecyclerView()

        // Observe subjects
        observeSubjects()

        // Button listeners
        btnBack.setOnClickListener { finish() }

        fabAddSubject.setOnClickListener {
            val intent = Intent(this, AddEditSubjectActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        subjectAdapter = SubjectAdapter(
            subjects = emptyList(),
            onEditClick = { subject -> openEditSubject(subject) },
            onDeleteClick = { subject -> confirmDeleteSubject(subject) }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SubjectListActivity)
            adapter = subjectAdapter
        }
    }

    private fun observeSubjects() {
        subjectViewModel.getSubjectsByUserId(currentUserId).observe(this) { subjects ->
            if (subjects.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyState.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyState.visibility = View.GONE
                subjectAdapter.updateSubjects(subjects)
            }
        }
    }

    private fun openEditSubject(subject: Subject) {
        val intent = Intent(this, AddEditSubjectActivity::class.java).apply {
            putExtra("subject_id", subject.id)
            putExtra("subject_name", subject.name)
            putExtra("subject_code", subject.code)
            putExtra("subject_teacher", subject.teacher)
            putExtra("subject_color", subject.color)
        }
        startActivity(intent)
    }

    private fun confirmDeleteSubject(subject: Subject) {
        AlertDialog.Builder(this)
            .setTitle("Xóa môn học")
            .setMessage("Bạn có chắc muốn xóa môn học \"${subject.name}\"?\n\n⚠️ Tất cả tasks thuộc môn học này cũng sẽ bị xóa!")
            .setPositiveButton("Xóa") { _, _ ->
                deleteSubject(subject)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteSubject(subject: Subject) {
        subjectViewModel.deleteSubject(subject.id) { result ->
            result.onSuccess {
                Toast.makeText(this, "✅ Đã xóa môn học", Toast.LENGTH_SHORT).show()
            }.onFailure { error ->
                Toast.makeText(this, "❌ Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh list when returning from Add/Edit screen
        observeSubjects()
    }
}
