package smart.planner.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.Toast
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
        val viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        taskViewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]
        subjectViewModel = ViewModelProvider(this, viewModelFactory)[SubjectViewModel::class.java]
        userViewModel = ViewModelProvider(this, viewModelFactory)[UserViewModel::class.java]

        // 2. Ánh xạ View từ layout XML
        val etTaskName = findViewById<TextInputEditText>(R.id.etTaskName)
        val etDescription = findViewById<TextInputEditText>(R.id.etDescription)
        val spinnerSubject = findViewById<Spinner>(R.id.spinnerSubject)
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val btnSave = findViewById<Button>(R.id.btnSaveTask)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnReviewTasks = findViewById<Button>(R.id.btnReviewTasks)

        // 3. Load userId và subjects từ database
        lifecycleScope.launch {
            userId = userViewModel.getCurrentUserIdAsync()
            if (userId == null) {
                Toast.makeText(this@AddTaskActivity, "Vui lòng đăng nhập trước", Toast.LENGTH_LONG).show()
                finish()
                return@launch
            }

            subjectViewModel.getSubjectsByUserId(userId!!).observe(this@AddTaskActivity) { loadedSubjects ->
                subjects = loadedSubjects
                if (subjects.isEmpty()) {
                    Toast.makeText(this@AddTaskActivity, "Chưa có môn học nào. Vui lòng thêm môn học trước.", Toast.LENGTH_LONG).show()
                    spinnerSubject.adapter = ArrayAdapter(this@AddTaskActivity, android.R.layout.simple_spinner_dropdown_item, listOf("No Subjects"))
                } else {
                    val subjectNames = subjects.map { it.name }
                    val adapter = ArrayAdapter(this@AddTaskActivity, android.R.layout.simple_spinner_dropdown_item, subjectNames)
                    spinnerSubject.adapter = adapter
                }
            }
        }

        // 4. Xử lý nút Lưu Task
        btnSave.setOnClickListener {
            val name = etTaskName.text.toString().trim()
            val desc = etDescription.text.toString().trim()
            val subjectIndex = spinnerSubject.selectedItemPosition

            val calendar = Calendar.getInstance()
            calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            val deadline = calendar.timeInMillis

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

            if (subjects.isEmpty() || subjectIndex < 0 || subjectIndex >= subjects.size) {
                Toast.makeText(this, "Vui lòng chọn môn học", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedSubject = subjects[subjectIndex]
            val subjectName = selectedSubject.name

            taskViewModel.addTask(name, subjectName, deadline, desc)
            Toast.makeText(this, "Đã thêm task thành công!", Toast.LENGTH_SHORT).show()

            // Reset các trường nhập liệu
            etTaskName.text?.clear()
            etDescription.text?.clear()
            etTaskName.requestFocus()
        }

        // 5. Xử lý nút Hủy
        btnCancel.setOnClickListener {
            finish()
        }

        // 6. Xử lý nút Xem lại Task (Chuyển sang màn hình CheckTaskActivity)
        btnReviewTasks.setOnClickListener {
            val intent = Intent(this, CheckTaskActivity::class.java)
            startActivity(intent)
        }
    }
}