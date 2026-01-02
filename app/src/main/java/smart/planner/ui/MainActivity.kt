package smart.planner

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import smart.planner.data.entity.Subject
import smart.planner.data.database.AppDatabase
import smart.planner.ui.adapter.TaskAdapter
import smart.planner.data.entity.Task
import smart.planner.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {

    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)

        /* ===================== INSERT DEMO DATA (GIỮ LẠI, SỬA ĐÚNG ENTITY) ===================== */
        lifecycleScope.launch {
            // Insert subject demo
            db.subjectDao().insert(
                Subject(name = "Mobile Programming", code = "MOB101", teacher = "Mr A")
            )

            val subject = db.subjectDao().getAll().first()

            // 🔥 INSERT TASK THEO ENTITY MỚI
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

        // 🔥 Adapter mới (có callback checkbox)
        val taskAdapter = TaskAdapter { task, isDone ->
            taskViewModel.updateTaskDone(task, isDone)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        /* ===================== OBSERVE TASK LIST ===================== */
        taskViewModel.taskList.observe(this) { tasks ->
            Log.d("MainActivity", "Loaded tasks: ${tasks.size}")
            taskAdapter.submitList(tasks)
        }

        /* ===================== LOAD TASK ===================== */
        lifecycleScope.launch {
            val subject = db.subjectDao().getAll().first()
            taskViewModel.loadFromRealtimeDatabase()
// 🔥 String
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
}
