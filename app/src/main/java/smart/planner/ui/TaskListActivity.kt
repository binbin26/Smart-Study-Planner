package smart.planner.ui

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import smart.planner.R
import smart.planner.ui.adapter.TaskAdapter
import smart.planner.viewmodel.TaskViewModel
import smart.planner.viewmodel.SyncState

class TaskListActivity : ComponentActivity() {

    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sử dụng đúng layout XML
        setContentView(R.layout.activity_task_list)

        // Lấy subjectId từ Intent (giữ nguyên)
        val subjectId = intent.getIntExtra("subjectId", 0)

        // ===================== ADAPTER =====================
        // ✅ SỬA TÊN CALLBACK CHO ĐÚNG VỚI TaskAdapter
        val taskAdapter = TaskAdapter(
            onCheckedChange = { task, isChecked ->
                taskViewModel.updateTaskDone(task, isChecked)
            }
        )

        // ===================== RECYCLER VIEW =====================
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        // ===================== PROGRESS BAR =====================
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        /* ===================== OBSERVE DATA ===================== */

        // Observe danh sách task
        taskViewModel.taskList.observe(this) { tasks ->
            taskAdapter.submitList(tasks)
        }

        // Observe sync state
        lifecycleScope.launch {
            taskViewModel.syncState.collect { state ->
                when (state) {
                    is SyncState.Syncing -> {
                        progressBar.visibility = View.VISIBLE
                    }

                    is SyncState.Success,
                    is SyncState.Idle -> {
                        progressBar.visibility = View.GONE
                    }

                    is SyncState.Error -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@TaskListActivity,
                            state.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        /* ===================== LOAD DATA ===================== */

        // Load các công việc từ Firebase Realtime DB
        taskViewModel.loadFromRealtimeDatabase()
    }
}
