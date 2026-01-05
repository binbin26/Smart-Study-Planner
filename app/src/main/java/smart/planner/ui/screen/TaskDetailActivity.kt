package smart.planner.ui.screen

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import smart.planner.R

class TaskDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)
        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // đóng Activity, quay về màn hình trước
        }

        // Nhận dữ liệu từ Intent
        val taskId = intent.getIntExtra("taskId", -1)
        val taskTitle = intent.getStringExtra("taskTitle")
        val taskDescription = intent.getStringExtra("taskDescription")
        val taskDeadline = intent.getLongExtra("taskDeadline", -1)

        // Ánh xạ view
        val tvTitle = findViewById<TextView>(R.id.taskTitle)
        val tvDetail = findViewById<TextView>(R.id.taskDetailText)
        val tvDeadline = findViewById<TextView>(R.id.taskDeadline)

        // Gán dữ liệu
        tvTitle.text = taskTitle ?: "Task #$taskId"
        tvDetail.text = taskDescription ?: "Không có mô tả"
        if (taskDeadline > 0) {
            val formatted = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                .format(java.util.Date(taskDeadline))
            tvDeadline.text = "Deadline: $formatted"
        } else {
            tvDeadline.text = "Deadline: Không xác định"
        }
    }
}

