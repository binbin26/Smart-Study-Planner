package smart.planner.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import smart.planner.R
import smart.planner.data.model.Subject
import smart.planner.ui.viewmodel.SubjectViewModel
import smart.planner.ui.viewmodel.TaskViewModel
import smart.planner.ui.viewmodel.UserViewModel
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var subjectViewModel: SubjectViewModel
    private lateinit var userViewModel: UserViewModel

    private var userId: Int? = null
    private var subjects: List<Subject> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // 1. Khởi tạo ViewModels
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        taskViewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]
        subjectViewModel = ViewModelProvider(this, factory)[SubjectViewModel::class.java]
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        val etTaskName = findViewById<TextInputEditText>(R.id.etTaskName)
        val etDescription = findViewById<TextInputEditText>(R.id.etDescription)
        val spinnerSubject = findViewById<Spinner>(R.id.spinnerSubject)
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val btnSave = findViewById<Button>(R.id.btnSaveTask)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnReviewTasks = findViewById<Button>(R.id.btnReviewTasks)

        // 2. Load userId và danh sách môn học
        lifecycleScope.launch {
            userId = userViewModel.getCurrentUserIdAsync()

            if (userId == null) {
                Toast.makeText(this@AddTaskActivity, "Vui lòng đăng nhập trước", Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }

            subjectViewModel
                .getSubjectsByUserId(userId!!)
                .observe(this@AddTaskActivity) { loadedSubjects ->
                    subjects = loadedSubjects

                    if (subjects.isEmpty()) {
                        spinnerSubject.adapter = ArrayAdapter(
                            this@AddTaskActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            listOf("Chưa có môn học")
                        )
                    } else {
                        val subjectNames = subjects.map { it.name }
                        spinnerSubject.adapter = ArrayAdapter(
                            this@AddTaskActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            subjectNames
                        )
                    }
                }
        }

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

            if (userId == null) {
                Toast.makeText(this, "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (subjects.isEmpty() || subjectIndex !in subjects.indices) {
                Toast.makeText(this, "Vui lòng chọn môn học", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val calendar = Calendar.getInstance().apply {
                set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            }
            val deadline = calendar.timeInMillis

            val selectedSubject = subjects[subjectIndex]

            // Gọi addTask với subjectId thay vì subjectName
            taskViewModel.addTask(
                title = name,
                subjectId = selectedSubject.id.toString(),
                deadline = deadline,
                description = desc
            )

            Toast.makeText(this, "Đã thêm task thành công!", Toast.LENGTH_SHORT).show()

            // Reset form
            etTaskName.text?.clear()
            etDescription.text?.clear()
            etTaskName.requestFocus()
        }

        btnCancel.setOnClickListener { finish() }

        btnReviewTasks.setOnClickListener {
            startActivity(Intent(this, TaskListActivity::class.java))
        }
    }
}