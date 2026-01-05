package smart.planner.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text

class TaskDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseId = intent.getStringExtra("firebaseId") ?: ""

        setContent {
            Text(text = "Task detail firebaseId = $firebaseId")
        }
    }
}
