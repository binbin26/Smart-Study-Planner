package smart.planner.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModelProvider
import smart.planner.ui.stats.StatsScreen
import smart.planner.ui.theme.SmartStudyPlannerTheme
import smart.planner.ui.viewmodel.SubjectViewModel
import smart.planner.viewmodel.TaskViewModel

class StatsActivity : ComponentActivity() {

    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var subjectViewModel: SubjectViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get current user
        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val currentUserId = sharedPreferences.getInt("userId", 1)

        // Initialize SubjectViewModel
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        subjectViewModel = ViewModelProvider(this, factory)[SubjectViewModel::class.java]

        // Load tasks from Firebase
        taskViewModel.loadFromRealtimeDatabase()

        setContent {
            SmartStudyPlannerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {

                    // Observe data from ViewModels
                    val tasks by taskViewModel.taskList.observeAsState(emptyList())
                    val subjects by subjectViewModel.getSubjectsByUserId(currentUserId)
                        .observeAsState(emptyList())

                    StatsScreen(
                        tasks = tasks,
                        subjects = subjects,
                        onBackClick = { finish() }
                    )
                }
            }
        }
    }
}
