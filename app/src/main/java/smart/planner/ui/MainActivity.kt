package smart.planner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import smart.planner.data.model.Task
import smart.planner.data.sync.SubjectSyncService
import smart.planner.data.sync.SyncManager
import smart.planner.data.sync.TaskSyncService
import smart.planner.data.test.CoroutinesIOTest
import smart.planner.ui.AddTaskActivity
import smart.planner.ui.viewmodel.TaskViewModel

@Composable
fun SmartStudyPlannerTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"

    private lateinit var syncManager: SyncManager
    private lateinit var subjectSyncService: SubjectSyncService
    private lateinit var taskSyncService: TaskSyncService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Test API calls (có thể comment lại sau khi test xong)
        //smart.planner.data.api.ApiTestExample.testAllApis()
        CoroutinesIOTest.testDispatchersIO()
        
        setContent {
            SmartStudyPlannerTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: TaskViewModel = viewModel()) {
    val context = LocalContext.current
    val tasks by viewModel.allTasks.observeAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Tasks",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                val intent = Intent(context, AddTaskActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Mở Thêm Task")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (tasks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Chưa có task nào", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
                    TaskCard(
                        task = task,
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: Task, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Môn: ${task.subjectId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
                if (task.deadline > 0) {
                    Text(
                        text = "Deadline: ${java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date(task.deadline))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa Task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
