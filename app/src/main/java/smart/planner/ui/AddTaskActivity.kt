package smart.planner.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import smart.planner.R
import smart.planner.ui.viewmodel.TaskViewModel
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        val etTaskName = findViewById<TextInputEditText>(R.id.etTaskName)
        val etDescription = findViewById<TextInputEditText>(R.id.etDescription)
        val spinnerSubject = findViewById<Spinner>(R.id.spinnerSubject)
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val btnSave = findViewById<Button>(R.id.btnSaveTask)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnReviewTasks = findViewById<Button>(R.id.btnReviewTasks)

        // Cập nhật danh sách môn học mới
        val subjects = arrayOf("AI", "LT Mobile", "UIUX", "PM", "Lập trình cơ bản")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, subjects)
        spinnerSubject.adapter = adapter

        btnSave.setOnClickListener {
            val name = etTaskName.text.toString().trim()
            val desc = etDescription.text.toString().trim()
            val subject = spinnerSubject.selectedItem.toString()

            val calendar = Calendar.getInstance()
            calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            val deadline = calendar.timeInMillis

            if (name.isNotEmpty()) {
                // Gọi hàm addTask đã gộp từ branch Phát vào cấu trúc Quyên
                viewModel.addTask(name, subject, deadline, desc)
                Toast.makeText(this, "Đã thêm task thành công!", Toast.LENGTH_SHORT).show()

                // Xóa dữ liệu cũ để tiếp tục thêm task mới (không gọi finish())
                etTaskName.text?.clear()
                etDescription.text?.clear()
                etTaskName.requestFocus()
            } else {
                etTaskName.error = "Tên bài tập không được để trống"
            }
        }

        btnCancel.setOnClickListener { finish() }

        // Sửa lỗi nút "Xem lại Task" không hoạt động
        btnReviewTasks.setOnClickListener {
            // Chuyển hướng chính xác sang TaskListActivity (sử dụng layout của Phát)
            val intent = Intent(this, TaskListActivity::class.java)
            startActivity(intent)
        }
    }
}