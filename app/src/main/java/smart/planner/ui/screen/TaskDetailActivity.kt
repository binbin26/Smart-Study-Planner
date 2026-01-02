package smart.planner.ui.screen

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import smart.planner.R

class TaskDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        val taskId = intent.getIntExtra("taskId", -1)

        // TODO: load dữ liệu chi tiết task từ DB hoặc API theo taskId
        val textView = findViewById<TextView>(R.id.taskDetailText)
        textView.text = "Chi tiết Task ID: $taskId"
    }
}
