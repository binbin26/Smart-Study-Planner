package smart.planner.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import smart.planner.data.local.AppDatabase
import smart.planner.data.sync.SubjectSyncService
import smart.planner.data.sync.SyncManager
import smart.planner.data.sync.TaskSyncService

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

        Log.d(TAG, "========================================")
        Log.d(TAG, "🚀 App Starting...")
        Log.d(TAG, "========================================")

        smart.planner.data.test.CoroutinesIOTest.testDispatchersIO()

        setupSync()
        startSync()

        setContent {
            SmartStudyPlannerTheme {
                Greeting(name = "User")
            }
        }

        Log.d(TAG, "✅ App Started Successfully")
    }

    private fun setupSync() {
        Log.d(TAG, "📦 Setting up sync services...")

        try {
            val database = AppDatabase.getDatabase(applicationContext)
            Log.d(TAG, "  ✅ Room Database: OK")

            val firebaseDb = FirebaseDatabase.getInstance().reference
            Log.d(TAG, "  ✅ Firebase Database: OK")

            syncManager = SyncManager(applicationContext)
            Log.d(TAG, "  ✅ SyncManager: Created")

            subjectSyncService = SubjectSyncService(
                subjectDao = database.subjectDao(),
                firebaseDatabase = firebaseDb,
                syncManager = syncManager
            )
            Log.d(TAG, "  ✅ SubjectSyncService: Created")

            taskSyncService = TaskSyncService(
                taskDao = database.taskDao(),
                firebaseDatabase = firebaseDb,
                syncManager = syncManager
            )
            Log.d(TAG, "  ✅ TaskSyncService: Created")

            Log.d(TAG, "✅ All sync services initialized!")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error setting up sync: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun startSync() {
        Log.d(TAG, "🔄 Starting sync...")

        lifecycleScope.launch {
            try {
                Log.d(TAG, "  📡 Starting Firebase listeners...")
                subjectSyncService.startListening()
                taskSyncService.startListening()
                Log.d(TAG, "  ✅ Listeners started")

                Log.d(TAG, "  🔄 Running initial sync...")

                val subjectSyncResult = subjectSyncService.initialSync()
                if (subjectSyncResult.isSuccess) {
                    Log.d(TAG, "  ✅ Subjects synced")
                } else {
                    Log.w(TAG, "  ⚠️ Subject sync failed (may be offline)")
                }

                val taskSyncResult = taskSyncService.initialSync()
                if (taskSyncResult.isSuccess) {
                    Log.d(TAG, "  ✅ Tasks synced")
                } else {
                    Log.w(TAG, "  ⚠️ Task sync failed (may be offline)")
                }

                Log.d(TAG, "✅ Sync completed!")
                Log.d(TAG, "========================================")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error during sync: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "🛑 App Stopping...")

        try {
            syncManager.cleanup()
            subjectSyncService.cleanup()
            taskSyncService.cleanup()
            Log.d(TAG, "✅ Sync services cleaned up")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error cleaning up: ${e.message}")
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmartStudyPlannerTheme {
        Greeting("Android")
    }
}