package smart.planner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import smart.planner.data.database.AppDatabase
import smart.planner.data.entity.Subject
import smart.planner.data.entity.Task
import smart.planner.notification.NotificationScheduler
import smart.planner.ui.adapter.TaskAdapter
import smart.planner.viewmodel.TaskViewModel
import android.content.Intent
import smart.planner.ui.StatsActivity

class MainActivity : ComponentActivity() {

    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)

        /* ===================== INSERT DEMO DATA (GIỮ NGUYÊN) ===================== */
        lifecycleScope.launch {
            db.subjectDao().insert(
                Subject(name = "Mobile Programming", code = "MOB101", teacher = "Mr A")
            )

            val subject = db.subjectDao().getAll().first()

            db.taskDao().insert(
                Task(
                    firebaseId = "local_${System.currentTimeMillis()}",
                    title = "Complete Assignment",
                    description = "Finish mobile app assignment",
                    createdAt = System.currentTimeMillis(),
                    deadline = 1680000000000,
                    isDone = false,
                    subjectId = subject.id.toString(),
                    subjectName = subject.name
                )
            )
        }

        /* ===================== UI ===================== */
        setContentView(R.layout.activity_task_list)

        /* ===================== TEST COMPOSE TASK LIST ===================== */
        //startActivity(Intent(this, smart.planner.ui.TaskListActivity::class.java))
        //finish()

        /* ===================== TEST STATS SCREEN ===================== */
        // startActivity(Intent(this, StatsActivity::class.java))

        /* ===================== PERMISSION (ANDROID 13+) ===================== */
        requestNotificationPermission()

        /* ===================== ADAPTER (🔥 KHAI BÁO SỚM) ===================== */
        val taskAdapter = TaskAdapter { task, isDone ->
            taskViewModel.updateTaskDone(task, isDone)
        }

        /* ===================== RECYCLER VIEW ===================== */
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        /* ===================== TEST NOTIFICATION (1 PHÚT) ===================== */
        taskViewModel.taskList.observe(this) { tasks ->
            taskAdapter.submitList(tasks)

            tasks
                .filter { it.isDone == false }
                .forEach { task ->
                    NotificationScheduler.scheduleTest(
                        context = this,
                        taskId = task.firebaseId,   // ⚠️ mỗi task 1 ID
                        title = task.title
                    )

                    Log.d("TEST", "Scheduled for ${task.firebaseId}")
                }
        }



        /* ===================== OBSERVE TASK LIST ===================== */
        taskViewModel.taskList.observe(this) { tasks ->
            Log.d("MainActivity", "Loaded tasks: ${tasks.size}")
            taskAdapter.submitList(tasks)
        }

        /* ===================== LOAD TASK FROM FIREBASE ===================== */
        lifecycleScope.launch {
            taskViewModel.loadFromRealtimeDatabase()
        }

        /* ===================== SWIPE TO DELETE ===================== */
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task = taskAdapter.getTaskAt(viewHolder.adapterPosition)
                taskViewModel.deleteTask(task)
                Log.d("MainActivity", "Task deleted: ${task.title}")
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    /* ===================== REQUEST NOTIFICATION PERMISSION ===================== */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
}
