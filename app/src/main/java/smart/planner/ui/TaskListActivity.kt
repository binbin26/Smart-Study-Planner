package smart.planner.ui

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import smart.planner.R
import smart.planner.ui.adapter.TaskAdapter
import smart.planner.ui.viewmodel.TaskViewModel

class TaskListActivity : AppCompatActivity() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        // 1. Nút quay lại
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        // 2. RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TaskAdapter { task ->
            taskViewModel.deleteTask(task)
        }
        recyclerView.adapter = adapter

        // 3. ViewModel
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        // 4. Observe data
        taskViewModel.allTasks.observe(this) { tasks ->
            adapter.submitList(tasks)
        }
    }
}
