package smart.planner.ui

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ProgressBar
import smart.planner.R
import smart.planner.ui.adapter.TaskAdapter
import smart.planner.viewmodel.TaskViewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.Toast
import smart.planner.viewmodel.SyncState

class TaskListActivity : ComponentActivity() {

    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sử dụng đúng layout XML
        setContentView(R.layout.activity_task_list)

        // Lấy subjectId từ Intent
        val subjectId = intent.getIntExtra("subjectId", 0)

        // Khởi tạo adapter
        val taskAdapter = TaskAdapter()

        // RecyclerView - sử dụng ID đúng từ layout
        val recyclerView = findViewById<RecyclerView>(R.id.rvTaskList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        // ProgressBar - layout không có ProgressBar, bỏ qua hoặc thêm vào layout nếu cần
        // val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        /* ===================== OBSERVE DATA ===================== */

        // Observe danh sách task
        taskViewModel.taskList.observe(this) { tasks ->
            taskAdapter.submitList(tasks)
        }
        lifecycleScope.launch {
            taskViewModel.syncState.collect { state ->
                when (state) {
                    is SyncState.Syncing -> {
                        // ProgressBar không có trong layout, có thể thêm loading indicator nếu cần
                        // progressBar.visibility = View.VISIBLE
                    }

                    is SyncState.Success,
                    is SyncState.Idle -> {
                        // progressBar.visibility = View.GONE
                    }

                    is SyncState.Error -> {
                        // progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@TaskListActivity,
                            state.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        // Observe loading state (Task 6)


        /* ===================== LOAD DATA ===================== */

        // Load các công việc từ ViewModel
        // 1️⃣ Sync Firebase → Room
        taskViewModel.loadFromRealtimeDatabase()


// 2️⃣ Load từ Room

    }
}
