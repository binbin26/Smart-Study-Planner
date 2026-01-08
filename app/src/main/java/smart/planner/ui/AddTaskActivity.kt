package smart.planner.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import smart.planner.R
import smart.planner.data.model.Subject
import smart.planner.ui.screen.SubjectListActivity
import smart.planner.ui.viewmodel.SubjectViewModel
import smart.planner.ui.viewmodel.TaskViewModel
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var subjectViewModel: SubjectViewModel
    private var subjects: List<Subject> = emptyList()
    private var currentUserId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // Get current user
        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        currentUserId = sharedPreferences.getInt("userId", 1)

        // Khởi tạo ViewModels
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        taskViewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]
        subjectViewModel = ViewModelProvider(this, factory)[SubjectViewModel::class.java]

        // Views
        val etTaskName = findViewById<TextInputEditText>(R.id.etTaskName)
        val etDescription = findViewById<TextInputEditText>(R.id.etDescription)
        val spinnerSubject = findViewById<Spinner>(R.id.spinnerSubject)
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val btnSave = findViewById<Button>(R.id.btnSaveTask)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnReviewTasks = findViewById<Button>(R.id.btnReviewTasks)

        // Load subjects from database
        loadSubjects(spinnerSubject)

        // Save button
        btnSave.setOnClickListener {
            val name = etTaskName.text.toString().trim()
            val desc = etDescription.text.toString().trim()
            val subjectIndex = spinnerSubject.selectedItemPosition

            // Validation
            if (name.isEmpty()) {
                etTaskName.error = "Tên bài tập không được để trống"
                Toast.makeText(this, "Vui lòng nhập tên bài tập", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (subjectIndex < 0 || subjectIndex >= subjects.size) {
                Toast.makeText(this, "Vui lòng chọn môn học", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get deadline from DatePicker
            val calendar = Calendar.getInstance().apply {
                set(datePicker.year, datePicker.month, datePicker.dayOfMonth, 23, 59, 59)
            }
            val deadline = calendar.timeInMillis

            val selectedSubject = subjects[subjectIndex]

            // Add task
            taskViewModel.addTask(
                title = name,
                subjectId = selectedSubject.id.toString(),
                deadline = deadline,
                description = desc
            )

            Toast.makeText(this, "✅ Đã thêm task thành công!", Toast.LENGTH_SHORT).show()

            // Reset form
            etTaskName.text?.clear()
            etDescription.text?.clear()
            spinnerSubject.setSelection(0)
            etTaskName.requestFocus()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnReviewTasks.setOnClickListener {
            startActivity(Intent(this, TaskListActivity::class.java))
        }
    }

    private fun loadSubjects(spinnerSubject: Spinner) {
        subjectViewModel.getSubjectsByUserId(currentUserId).observe(this) { subjectList ->
            subjects = subjectList

            if (subjects.isEmpty()) {
                // Show message and button to add subjects
                Toast.makeText(
                    this,
                    "⚠️ Chưa có môn học nào. Hãy thêm môn học trước!",
                    Toast.LENGTH_LONG
                ).show()

                // Optionally navigate to SubjectListActivity
                val intent = Intent(this, SubjectListActivity::class.java)
                startActivity(intent)
            } else {
                // Setup spinner with subjects from database
                val subjectNames = subjects.map { it.name }
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    subjectNames
                )
                spinnerSubject.adapter = adapter
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh subjects when returning from SubjectListActivity
        val spinnerSubject = findViewById<Spinner>(R.id.spinnerSubject)
        loadSubjects(spinnerSubject)
    }
}