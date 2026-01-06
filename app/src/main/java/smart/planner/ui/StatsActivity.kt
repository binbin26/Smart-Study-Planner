package smart.planner.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import smart.planner.ui.stats.StatsScreen
import smart.planner.viewmodel.TaskViewModel
import smart.planner.ui.theme.SmartStudyPlannerTheme

class StatsActivity : ComponentActivity() {

    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmartStudyPlannerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {

                    // Lấy list task từ ViewModel (LiveData)
                    val tasks by taskViewModel.taskList.observeAsState(emptyList())

                    // Load data từ Firebase 1 lần
                    LaunchedEffect(Unit) {
                        taskViewModel.loadFromRealtimeDatabase()
                    }

                    StatsScreen(tasks = tasks)
                }
            }
        }
    }
}
