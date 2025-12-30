package smart.planner.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import smart.planner.R
import smart.planner.viewmodel.TaskViewModel
import smart.planner.ui.adapter.TaskAdapter

class TaskListActivity : ComponentActivity() {

    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lấy subjectId từ Intent
        val subjectId = intent.getIntExtra("subjectId", 0)

        // Khởi tạo adapter
        val taskAdapter = TaskAdapter()

        // Đăng ký Observer để theo dõi sự thay đổi của danh sách công việc
        taskViewModel.taskList.observe(this, Observer { tasks ->
            // Cập nhật danh sách công việc trong RecyclerView
            taskAdapter.submitList(tasks)
        })

        // Load các công việc từ ViewModel
        taskViewModel.loadTasks(subjectId)

        // Sử dụng đúng layout XML
        setContentView(R.layout.activity_task_list)

        // Thiết lập RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter
    }
}
