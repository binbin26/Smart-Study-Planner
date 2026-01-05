package smart.planner.ui.task

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.livedata.observeAsState
import smart.planner.data.entity.Task
import smart.planner.ui.TaskDetailActivity // nếu chưa có sẽ sửa ở Bước 5
import smart.planner.viewmodel.SyncState
import smart.planner.viewmodel.TaskViewModel

/**
 * Screen dùng ViewModel: observe taskList + syncState
 * - Hiển thị loading khi Syncing
 * - TaskList = LazyColumn items(tasks)
 * - Checkbox -> updateTaskDone
 * - Click item -> navigate
 */
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel
) {
    val context = LocalContext.current

    // LiveData -> Compose
    val tasks by viewModel.taskList.observeAsState(emptyList())

    // StateFlow -> Compose (có lifecycle)
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {

        TaskList(
            tasks = tasks,
            onCheckedChange = { task, checked ->
                viewModel.updateTaskDone(task, checked)
            },
            onItemClick = { task ->
                // Navigate: bạn có thể đổi sang Activity khác nếu chưa có TaskDetailActivity
                val intent = Intent(context, TaskDetailActivity::class.java)
                intent.putExtra("firebaseId", task.firebaseId)
                context.startActivity(intent)
            }
        )

        // Loading overlay
        if (syncState is SyncState.Syncing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

/**
 * Yêu cầu đề bài:
 * @Composable TaskList(tasks)
 * LazyColumn { items(tasks) }
 * Checkbox, Text, Icon
 * onClick -> navigate (qua onItemClick)
 */
@Composable
fun TaskList(
    tasks: List<Task>,
    onCheckedChange: (Task, Boolean) -> Unit,
    onItemClick: (Task) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp)
    ) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onCheckedChange = { checked ->
                    onCheckedChange(task, checked)
                },
                onClick = {
                    onItemClick(task)
                }
            )
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = onCheckedChange
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Go detail"
            )
        }
    }
}
