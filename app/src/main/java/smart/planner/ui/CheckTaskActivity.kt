package smart.planner.ui

import android.os.Bundle
import android.widget.Button // Import Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import smart.planner.R
import smart.planner.ui.viewmodel.TaskViewModel

class CheckTaskActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_task)

        // 1. Xử lý nút Quay lại
        val btnBack = findViewById<Button>(R.id.btnBackToAddTask)
        btnBack.setOnClickListener {
            finish() // Đóng CheckTaskActivity để quay lại AddTaskActivity
        }

        // 2. Khởi tạo RecyclerView
        val rvTasks = findViewById<RecyclerView>(R.id.rvCheckTasks)
        rvTasks.layoutManager = LinearLayoutManager(this)

        // Khởi tạo Adapter với callback xóa
        adapter = TaskAdapter(emptyList()) { task ->
            viewModel.deleteTask(task)
        }
        rvTasks.adapter = adapter

        // 3. Khởi tạo ViewModel
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[TaskViewModel::class.java]

        // 4. Quan sát dữ liệu
        viewModel.allTasks.observe(this) { tasks ->
            adapter.updateData(tasks)
        }
    }
}