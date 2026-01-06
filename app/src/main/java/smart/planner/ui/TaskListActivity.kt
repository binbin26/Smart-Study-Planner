package smart.planner.ui

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import smart.planner.R
import smart.planner.ui.viewmodel.TaskViewModel

class TaskListActivity : AppCompatActivity() {

    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        // 1. Ánh xạ nút Quay lại trong headerBar
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish() // Quay lại màn hình AddTaskActivity
        }

        // 2. Thiết lập RecyclerView và Adapter
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Khởi tạo adapter với logic xóa
        adapter = TaskAdapter(
            onDeleteClick = { task ->
                viewModel.deleteTask(task) // Gọi hàm xóa trong ViewModel
            }
        )
        recyclerView.adapter = adapter

        // 3. Khởi tạo ViewModel
        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        // 4. Quan sát dữ liệu
        viewModel.taskList.observe(this) { tasks ->
            adapter.submitList(tasks)
        }

        // Tải dữ liệu từ Firebase/Local
        viewModel.fetchTasks()
    }
}