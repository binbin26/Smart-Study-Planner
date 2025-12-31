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
import com.google.android.material.textfield.TextInputEditText
import smart.planner.R
import smart.planner.ui.viewmodel.TaskViewModel
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // 1. Khởi tạo ViewModel cho AndroidViewModel
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[TaskViewModel::class.java]

        // 2. Ánh xạ View từ layout XML
        val etTaskName = findViewById<TextInputEditText>(R.id.etTaskName)
        val etDescription = findViewById<TextInputEditText>(R.id.etDescription)
        val spinnerSubject = findViewById<Spinner>(R.id.spinnerSubject)
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val btnSave = findViewById<Button>(R.id.btnSaveTask)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnReviewTasks = findViewById<Button>(R.id.btnReviewTasks) // Nút mới bổ sung

        // 3. Setup Spinner với danh sách môn học
        val subjects = arrayOf("Toán", "Văn", "Anh", "Lập trình", "Kỹ thuật phần mềm")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, subjects)
        spinnerSubject.adapter = adapter

        // 4. Xử lý nút Lưu Task
        btnSave.setOnClickListener {
            val name = etTaskName.text.toString().trim()
            val desc = etDescription.text.toString().trim()
            val subject = spinnerSubject.selectedItem.toString()

            // Lấy thời gian từ DatePicker chính xác
            val calendar = Calendar.getInstance()
            calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            val deadline = calendar.timeInMillis

            // Validate dữ liệu đầu vào
            if (name.isNotEmpty()) {
                viewModel.addTask(name, subject, deadline, desc)
                Toast.makeText(this, "Đã thêm task thành công!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                etTaskName.error = "Tên bài tập không được để trống"
                Toast.makeText(this, "Vui lòng nhập tên bài tập", Toast.LENGTH_SHORT).show()
            }
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