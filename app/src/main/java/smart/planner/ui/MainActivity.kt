package smart.planner

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Observer
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

        // ✅ 1. LẤY DATABASE (Singleton)
        lifecycleScope.launch {
            // ✅ 2. INSERT TEST (CHỈ ĐỂ KIỂM TRA DATABASE)
            db.subjectDao().insert(
                Subject(name = "Mobile Programming", code = "MOB101", teacher = "Mr A")
            )

            // Lấy subjectId từ database (vì Subject vừa được insert)
            val subject = db.subjectDao().getAll().first()

            // Chèn dữ liệu công việc mẫu
            db.taskDao().insert(
                Task(title = "Complete Assignment", description = "Finish mobile app assignment", deadline = 1680000000000, status = "Pending", subjectId = subject.id)
            )
        }

        // ✅ 3. UI
        setContentView(R.layout.activity_task_list)

        val taskAdapter = TaskAdapter()

        // Thiết lập RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        // Đăng ký Observer để theo dõi sự thay đổi của danh sách công việc
        taskViewModel.taskList.observe(this, Observer { tasks ->
            // Log số lượng công việc đã tải và hiển thị trong RecyclerView
            Log.d("TaskListActivity", "Loaded tasks: ${tasks.size}")
            taskAdapter.submitList(tasks)
        })

        // Load các công việc từ ViewModel (theo subjectId đã lấy)
        lifecycleScope.launch {
            val subject = db.subjectDao().getAll().first()
            taskViewModel.loadTasks(subject.id)
        }

        // Cài đặt ItemTouchHelper cho RecyclerView (Swipe to delete)
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Xử lý khi vuốt để xóa công việc
                val task = taskAdapter.getTaskAt(viewHolder.adapterPosition)
                taskViewModel.deleteTask(task)  // Gọi hàm xóa trong ViewModel
                Log.d("TaskListActivity", "Task deleted: ${task.title}")
            }
        })

        // Gắn ItemTouchHelper vào RecyclerView
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}

