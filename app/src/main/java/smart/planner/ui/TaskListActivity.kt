package smart.planner.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import smart.planner.data.entity.Task
import smart.planner.ui.task.TaskList
import smart.planner.viewmodel.SyncState
import smart.planner.viewmodel.TaskViewModel

class TaskListActivity : ComponentActivity() {

    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ GIỮ NGUYÊN: lấy subjectId (chưa dùng thì cứ giữ)
        val subjectId = intent.getIntExtra("subjectId", 0)

        // ✅ GIỮ NGUYÊN: load từ Firebase
        taskViewModel.loadFromRealtimeDatabase()

        setContent {

            val context = LocalContext.current

            // 🔁 Thay observe RecyclerView bằng Compose observe
            val tasks by taskViewModel.taskList.observeAsState(emptyList())

            // 🔁 Thay lifecycleScope.collect bằng Compose
            val syncState by taskViewModel.syncState.collectAsStateWithLifecycle()

            Box(modifier = Modifier.fillMaxSize()) {

                // ================= TASK LIST =================
                TaskList(
                    tasks = tasks,

                    // ✅ GIỮ NGUYÊN LOGIC
                    onCheckedChange = { task: Task, isChecked: Boolean ->
                        taskViewModel.updateTaskDone(task, isChecked)
                    },

                    // ✅ NAVIGATE (bạn có thể đổi Activity khác)
                    onItemClick = { task ->
                        val intent = Intent(context, StatsActivity::class.java)
                        intent.putExtra("firebaseId", task.firebaseId)
                        context.startActivity(intent)
                    }
                )

                // ================= LOADING =================
                if (syncState is SyncState.Syncing) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // ================= ERROR =================
                if (syncState is SyncState.Error) {
                    LaunchedEffect(syncState) {
                        Toast.makeText(
                            context,
                            (syncState as SyncState.Error).message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
