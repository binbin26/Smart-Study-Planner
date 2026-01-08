package smart.planner.ui.screen

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import smart.planner.R
import smart.planner.ui.viewmodel.SubjectViewModel

class AddEditSubjectActivity : AppCompatActivity() {

    private lateinit var subjectViewModel: SubjectViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private var currentUserId: Int = 1
    private var subjectId: Int = -1 // -1 means creating new subject
    private var selectedColor: String = "#3F51B5" // Default blue

    // Views
    private lateinit var tvTitle: TextView
    private lateinit var etSubjectName: TextInputEditText
    private lateinit var etSubjectCode: TextInputEditText
    private lateinit var etTeacher: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    // Color buttons
    private lateinit var color1: View
    private lateinit var color2: View
    private lateinit var color3: View
    private lateinit var color4: View
    private lateinit var color5: View

    private val colors = listOf("#F44336", "#2196F3", "#4CAF50", "#FF9800", "#9C27B0")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_subject)

        // Get current user
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        currentUserId = sharedPreferences.getInt("userId", 1)

        // Initialize ViewModel
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        subjectViewModel = ViewModelProvider(this, factory)[SubjectViewModel::class.java]

        // Initialize views
        initViews()

        // Check if editing or creating
        subjectId = intent.getIntExtra("subject_id", -1)

        if (subjectId != -1) {
            // Edit mode
            tvTitle.text = "Sửa Môn học"
            loadSubjectData()
        } else {
            // Create mode
            tvTitle.text = "Thêm Môn học"
        }

        // Setup color picker
        setupColorPicker()

        // Button listeners
        btnSave.setOnClickListener { saveSubject() }
        btnCancel.setOnClickListener { finish() }
    }

    private fun initViews() {
        tvTitle = findViewById(R.id.tvTitle)
        etSubjectName = findViewById(R.id.etSubjectName)
        etSubjectCode = findViewById(R.id.etSubjectCode)
        etTeacher = findViewById(R.id.etTeacher)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        color1 = findViewById(R.id.color1)
        color2 = findViewById(R.id.color2)
        color3 = findViewById(R.id.color3)
        color4 = findViewById(R.id.color4)
        color5 = findViewById(R.id.color5)
    }

    private fun loadSubjectData() {
        etSubjectName.setText(intent.getStringExtra("subject_name"))
        etSubjectCode.setText(intent.getStringExtra("subject_code"))
        etTeacher.setText(intent.getStringExtra("subject_teacher"))
        selectedColor = intent.getStringExtra("subject_color") ?: "#3F51B5"
    }

    private fun setupColorPicker() {
        val colorViews = listOf(color1, color2, color3, color4, color5)

        colorViews.forEachIndexed { index, colorView ->
            colorView.setOnClickListener {
                selectedColor = colors[index]
                highlightSelectedColor(colorView, colorViews)
                Toast.makeText(this, "Đã chọn màu", Toast.LENGTH_SHORT).show()
            }
        }

        // Highlight first color by default
        highlightSelectedColor(color1, colorViews)
    }

    private fun highlightSelectedColor(selected: View, allColors: List<View>) {
        allColors.forEach { it.alpha = 0.5f }
        selected.alpha = 1.0f
        selected.scaleX = 1.2f
        selected.scaleY = 1.2f

        allColors.filter { it != selected }.forEach {
            it.scaleX = 1.0f
            it.scaleY = 1.0f
        }
    }

    private fun saveSubject() {
        val name = etSubjectName.text.toString().trim()
        val code = etSubjectCode.text.toString().trim()
        val teacher = etTeacher.text.toString().trim()

        // Validation
        if (name.isEmpty()) {
            etSubjectName.error = "Tên môn học không được để trống"
            etSubjectName.requestFocus()
            return
        }

        if (subjectId == -1) {
            // Create new subject
            subjectViewModel.addSubject(
                name = name,
                code = code.ifEmpty { null },
                teacher = teacher.ifEmpty { null },
                color = selectedColor,
                userId = currentUserId
            ) { result ->
                result.onSuccess {
                    Toast.makeText(this, "✅ Đã thêm môn học", Toast.LENGTH_SHORT).show()
                    finish()
                }.onFailure { error ->
                    Toast.makeText(this, "❌ Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Update existing subject
            subjectViewModel.updateSubject(
                subjectId = subjectId,
                name = name,
                code = code.ifEmpty { null },
                teacher = teacher.ifEmpty { null },
                color = selectedColor
            ) { result ->
                result.onSuccess {
                    Toast.makeText(this, "✅ Đã cập nhật môn học", Toast.LENGTH_SHORT).show()
                    finish()
                }.onFailure { error ->
                    Toast.makeText(this, "❌ Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
