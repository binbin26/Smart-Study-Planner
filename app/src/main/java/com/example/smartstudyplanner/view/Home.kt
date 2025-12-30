package smart.planner.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import smart.planner.adapter.UpcomingTaskAdapter

import smart.planner.data.Task
import kotlinx.coroutines.launch
import smart.planner.R
import smart.planner.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var taskAdapter: UpcomingTaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeData()
        setupBottomNavigation()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarHome)
    }

    private fun setupRecyclerView() {
        taskAdapter = UpcomingTaskAdapter(
            onTaskClick = { task ->
                // Click vào task → hiện detail hoặc navigate
                Toast.makeText(this, "Clicked: ${task.title}", Toast.LENGTH_SHORT).show()

                // Nếu bạn đã có TaskListActivity thì uncomment dòng dưới:
                // val intent = Intent(this, TaskListActivity::class.java)
                // intent.putExtra("TASK_ID", task.id)
                // intent.putExtra("SUBJECT_ID", task.subjectId)
                // startActivity(intent)
            },
            onCheckboxClick = { task, isChecked ->
                // Handle checkbox click - update task status
                Toast.makeText(
                    this,
                    if (isChecked) "Đã hoàn thành: ${task.title}" else "Chưa hoàn thành: ${task.title}",
                    Toast.LENGTH_SHORT
                ).show()

                // TODO: Update task status in database/viewmodel
                // viewModel.updateTaskStatus(task.id, isChecked)
            }
        )

        binding.rvUpcomingTasks.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = taskAdapter
        }
    }

    private fun observeData() {
        // Observe subjects và tasks từ ViewModel hoặc Repository
        lifecycleScope.launch {
            // Nếu bạn có ViewModel với Flow/StateFlow:
            // viewModel.upcomingTasks.collect { tasks ->
            //     taskAdapter.submitList(tasks)
            //     updateDashboard(tasks)
            // }

            // Example với mock data để test
            val mockTasks = listOf(
                Task(
                    id = 1,
                    title = "Bài tập Android",
                    description = "Hoàn thành bài tập chương 3",
                    subjectId = 1,
                    deadline = System.currentTimeMillis() + 86400000,
                    isCompleted = false
                ),
                Task(
                    id = 2,
                    title = "Đọc SGK Văn",
                    description = "Đọc bài thơ Đất Nước",
                    subjectId = 2,
                    deadline = System.currentTimeMillis() + 172800000,
                    isCompleted = false
                ),
                Task(
                    id = 3,
                    title = "Lab Vật Lý",
                    description = "Chuẩn bị báo cáo thí nghiệm",
                    subjectId = 3,
                    deadline = System.currentTimeMillis() + 259200000,
                    isCompleted = true
                )
            )
            taskAdapter.submitList(mockTasks)

            // Update dashboard
            updateDashboard(mockTasks)
        }
    }

    private fun updateDashboard(tasks: List<Task>) {
        // Update số lượng tasks
        val urgentCount = tasks.filter {
            it.deadline - System.currentTimeMillis() < 2 * 86400000
        }.size
        val completedCount = tasks.count { it.isCompleted }
        val progress = if (tasks.isNotEmpty()) (completedCount * 100) / tasks.size else 0

        binding.tvSubtitle.text = "Hôm nay bạn có ${tasks.size} bài tập cần hoàn thành"
        binding.progressTask.progress = progress
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Already on home
                    true
                }
                R.id.nav_add -> {
                    // Navigate to Task List
                    Toast.makeText(this, "Task List", Toast.LENGTH_SHORT).show()
                    // Nếu đã có TaskListActivity thì uncomment:
                    // startActivity(Intent(this, TaskListActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    // Navigate to Settings
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                    // Nếu đã có SettingsActivity thì uncomment:
                    // startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}