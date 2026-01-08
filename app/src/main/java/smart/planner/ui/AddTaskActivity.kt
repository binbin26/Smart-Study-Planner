package smart.planner.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import smart.planner.R
import smart.planner.data.model.Subject
import smart.planner.ui.viewmodel.TaskViewModel
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    private lateinit var taskViewModel: TaskViewModel

    // Mock subjects (tạm thời cho đến khi có Subject management screen)
    private val subjects = listOf(
        Subject(id = 1, name = "Toán", userId = 1, color = "#FF5722"),
        Subject(id = 2, name = "Văn", userId = 1, color = "#4CAF50"),
        Subject(id = 3, name = "Lý", userId = 1, color = "#2196F3"),
        Subject(id = 4, name = "Hóa", userId = 1, color = "#9C27B0"),
        Subject(id = 5, name = "Anh", userId = 1, color = "#FF9800")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // Khởi tạo ViewModel
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        taskViewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]

        // Views
        val etTaskName = findViewById<TextInputEditText>(R.id.etTaskName)
        val etDescription = findViewById<TextInputEditText>(R.id.etDescription)
        val spinnerSubject = findViewById<Spinner>(R.id.spinnerSubject)
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val btnSave = findViewById<Button>(R.id.btnSaveTask)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnReviewTasks = findViewById<Button>(R.id.btnReviewTasks)

        // Setup Spinner với mock subjects
        val subjectNames = subjects.map { it.name }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            subjectNames
        )
        spinnerSubject.adapter = adapter

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
}