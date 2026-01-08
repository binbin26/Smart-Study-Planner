package smart.planner.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import kotlinx.coroutines.launch
import smart.planner.R
import smart.planner.data.model.Task
import smart.planner.databinding.ActivityHomeBinding
import smart.planner.ui.AddTaskActivity
import smart.planner.ui.LoginActivity
import smart.planner.ui.StatsActivity
import smart.planner.ui.adapter.UpcomingTaskAdapter
import smart.planner.ui.screen.TaskDetailActivity
import smart.planner.ui.viewmodel.TaskViewModel
import smart.planner.ui.viewmodel.UserViewModel
import smart.planner.ui.viewmodel.SubjectViewModel
import android.util.Log
import smart.planner.ui.SettingsActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var taskAdapter: UpcomingTaskAdapter
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var subjectViewModel: SubjectViewModel
    private var allTasks: List<Task> = emptyList()

    // Giữ decorator hiện tại để remove khi cập nhật
    private var currentDeadlineDecorator: DeadlineDecorator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        taskViewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
        subjectViewModel = ViewModelProvider(this, factory)[SubjectViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        observeData()
        setupCalendar()
        setupFilters()
        setupBottomNavigation()
        createNotificationChannel()
        loadUserInfo()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "task_reminder",
            "Task Reminder",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders for upcoming tasks"
        }

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarHome)
        supportActionBar?.title = "Smart Study Planner"
    }

    private fun setupRecyclerView() {
        taskAdapter = UpcomingTaskAdapter(
            onTaskClick = { task ->
                val intent = Intent(this, TaskDetailActivity::class.java).apply {
                    putExtra("taskId", task.id)
                    putExtra("taskTitle", task.title)
                    putExtra("taskDescription", task.description)
                    putExtra("taskDeadline", task.deadline)
                }
                startActivity(intent)
            },
            onCheckboxClick = { task, isChecked ->
                lifecycleScope.launch {
                    val newStatus = if (isChecked) "DONE" else "TODO"
                    val updatedTask = task.copy(
                        status = newStatus,
                        updatedAt = System.currentTimeMillis()
                    )

                    taskViewModel.updateTask(updatedTask)

                    Toast.makeText(
                        this@HomeActivity,
                        if (isChecked) "✅ Đã hoàn thành: ${task.title}"
                        else "⏳ Chưa hoàn thành: ${task.title}",
                        Toast.LENGTH_SHORT
                    ).show()

                    updateDashboard(taskAdapter.currentList)
                    applyDeadlineDecorators(calculateDeadlineDays(allTasks))
                }
            },
            // ✅ THÊM CALLBACK DELETE
            onDeleteClick = { task ->
                lifecycleScope.launch {
                    taskViewModel.deleteTask(task)
                    Toast.makeText(
                        this@HomeActivity,
                        "🗑️ Đã xóa: ${task.title}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        binding.rvUpcomingTasks.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = taskAdapter
            setPadding(0, 0, 0, 200)
            clipToPadding = false
        }
    }
    private fun setupFilters() {
        // Chip "Tất cả"
        binding.chipAll.setOnClickListener {
            taskAdapter.submitList(allTasks)
            updateDashboard(allTasks)
        }

        // Chip "Chưa hoàn thành" (TODO)
        binding.chipTodo.setOnClickListener {
            val filtered = allTasks.filter { it.status == "TODO" }
            taskAdapter.submitList(filtered)
            updateDashboard(filtered)
        }

        // Chip "Đang thực hiện" (IN_PROGRESS)
        binding.chipInProgress.setOnClickListener {
            val filtered = allTasks.filter { it.status == "IN_PROGRESS" }
            taskAdapter.submitList(filtered)
            updateDashboard(filtered)
        }

        // Chip "Đã hoàn thành" (DONE)
        binding.chipDone.setOnClickListener {
            val filtered = allTasks.filter { it.status == "DONE" }
            taskAdapter.submitList(filtered)
            updateDashboard(filtered)
        }
    }
    private fun observeData() {
        // Observe real tasks from ViewModel
        taskViewModel.allTasks.observe(this) { tasks ->
            if (tasks.isNotEmpty()) {
                allTasks = tasks
                taskAdapter.submitList(tasks)
                updateDashboard(tasks)
                applyDeadlineDecorators(calculateDeadlineDays(tasks))
            } else {
                // Empty state
                allTasks = emptyList()
                taskAdapter.submitList(emptyList())
                updateDashboard(emptyList())
                binding.tvSubtitle.text = "Chưa có task nào. Nhấn '+' để thêm task mới!"
            }
        }

        // Observe subjects to update subject count
        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val currentUserId = sharedPreferences.getInt("userId", 1)

        subjectViewModel.getSubjectsByUserId(currentUserId).observe(this) { subjects ->
            binding.tvSubjectCount?.text = "📚 ${subjects.size}\nMôn học"
        }
    }

    private fun setupCalendar() {
        binding.materialCalendarView.setOnDateChangedListener { _, date, _ ->
            val selectedTasks = allTasks.filter { task ->
                val cal = java.util.Calendar.getInstance().apply { timeInMillis = task.deadline }
                val taskDay = CalendarDay.from(cal)
                taskDay == date
            }

            if (selectedTasks.isNotEmpty()) {
                taskAdapter.submitList(selectedTasks)
                updateDashboard(selectedTasks)
                Toast.makeText(
                    this,
                    "📅 ${selectedTasks.size} task(s) vào ngày này",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Không có deadline ngày này", Toast.LENGTH_SHORT).show()
                // Reset to all tasks
                taskAdapter.submitList(allTasks)
                updateDashboard(allTasks)
            }
        }

        binding.materialCalendarView.setOnMonthChangedListener { _, _ ->
            binding.materialCalendarView.invalidateDecorators()
        }
    }

    private fun updateDashboard(tasks: List<Task>) {
        val now = System.currentTimeMillis()

        // ✅ Đếm urgent tasks (deadline < 2 ngày, chưa xong)
        val urgentCount = tasks.filter {
            (it.deadline - now < 2 * 86400000) && (it.status != "DONE")
        }.size

        // ✅ Đếm tasks đã hoàn thành
        val completedCount = tasks.count { it.status == "DONE" }

        // ✅ Đếm tasks đang làm
        val inProgressCount = tasks.count { it.status == "IN_PROGRESS" }

        // ✅ Đếm tasks chưa làm
        val todoCount = tasks.count { it.status == "TODO" }

        // ✅ Tính % hoàn thành
        val progress = if (tasks.isNotEmpty()) (completedCount * 100) / tasks.size else 0

        // ✅ Cập nhật subtitle với thông tin chi tiết
        binding.tvSubtitle.text = when {
            tasks.isEmpty() -> "Chưa có task nào. Nhấn '+' để thêm!"
            urgentCount > 0 -> "⚠️ Bạn có $urgentCount task gấp (< 2 ngày)"
            inProgressCount > 0 -> "⏳ Đang làm: $inProgressCount | Hoàn thành: $completedCount/$tasks.size"
            else -> "📚 Tổng: ${tasks.size} tasks | ✅ Xong: $completedCount | 📝 Còn lại: $todoCount"
        }

        // ✅ Cập nhật progress bar
        binding.progressTask.progress = progress

        // ✅ Cập nhật số urgent tasks
        binding.tvUrgentCount?.text = "⏰ $urgentCount\nSắp đến hạn"
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Already on Home, do nothing
                    true
                }

                R.id.nav_add -> {
                    // Navigate to Add Task
                    startActivity(Intent(this, AddTaskActivity::class.java))
                    // Don't finish() - let user come back
                    true
                }

                R.id.nav_subjects -> {
                    // Navigate to Subject Management
                    startActivity(Intent(this, smart.planner.ui.screen.SubjectListActivity::class.java))
                    true
                }

                R.id.nav_profile -> {
                    // Show profile menu
                    showProfileMenu()
                    true
                }

                else -> false
            }
        }
    }

    private fun showProfileMenu() {
        val options = arrayOf("📊 Thống kê", "⚙️ Cài đặt", "🚪 Đăng xuất")

        AlertDialog.Builder(this)
            .setTitle("Profile")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // Statistics
                        startActivity(Intent(this, StatsActivity::class.java))
                    }
                    1 -> {
                        // ✅ Settings - SỬA DÒNG NÀY
                        startActivity(Intent(this, SettingsActivity::class.java))
                    }
                    2 -> {
                        // Logout
                        showLogoutConfirmation()
                    }
                }
            }
            .show()
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc muốn đăng xuất?")
            .setPositiveButton("Đăng xuất") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun performLogout() {
        lifecycleScope.launch {
            // ✅ QUAN TRỌNG: Xóa TẤT CẢ SharedPreferences
            val appPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val userPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

            // Xóa cả 2
            appPrefs.edit().clear().apply()
            userPrefs.edit().clear().apply()

            Toast.makeText(this@HomeActivity, "Đã đăng xuất! 👋", Toast.LENGTH_SHORT).show()

            // Quay về màn hình đăng nhập
            val intent = Intent(this@HomeActivity, smart.planner.ui.LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    // Tính set ngày có deadline từ danh sách tasks
    private fun calculateDeadlineDays(tasks: List<Task>): Set<CalendarDay> {
        return tasks.filter { it.status != "DONE" }.map { task ->
            val cal = java.util.Calendar.getInstance().apply { timeInMillis = task.deadline }
            CalendarDay.from(cal)
        }.toSet()
    }

    // Áp dụng decorator
    private fun applyDeadlineDecorators(deadlineDays: Set<CalendarDay>) {
        currentDeadlineDecorator?.let { binding.materialCalendarView.removeDecorator(it) }
        val newDecorator = DeadlineDecorator(this, deadlineDays)
        binding.materialCalendarView.addDecorator(newDecorator)
        currentDeadlineDecorator = newDecorator
        binding.materialCalendarView.invalidateDecorators()
    }

    override fun onResume() {
        super.onResume()
        // ✅ Refresh data khi quay lại màn hình
        binding.bottomNavigation.selectedItemId = R.id.nav_home

        // ✅ Force reload tasks để cập nhật bộ đếm
        taskViewModel.allTasks.observe(this) { tasks ->
            if (tasks.isNotEmpty()) {
                allTasks = tasks
                taskAdapter.submitList(tasks)
                updateDashboard(tasks)
                applyDeadlineDecorators(calculateDeadlineDays(tasks))
            }
        }
    }

    // Decorator highlight ngày có deadline
    class DeadlineDecorator(
        private val context: Context,
        private val dates: Set<CalendarDay>
    ) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean = dates.contains(day)

        override fun decorate(view: DayViewFacade) {
            view.addSpan(android.text.style.ForegroundColorSpan(Color.RED))
            view.addSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD))
            view.setBackgroundDrawable(
                ContextCompat.getDrawable(context, R.drawable.bg_deadline_day)!!
            )
        }
    }
    private fun loadUserInfo() {
        lifecycleScope.launch {
            try {
                val userId = userViewModel.getCurrentUserId()

                if (userId != null && userId > 0) {
                    // Lấy thông tin user từ SharedPreferences tạm thời
                    val userPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

                    // Hoặc có thể lấy từ database
                    // val user = userViewModel.getUserById(userId)

                    // Tạm thời dùng tên mặc định, sau này sẽ load từ DB
                    val userName = "Bạn"  // TODO: Load từ database

                    // Cập nhật UI
                    runOnUiThread {
                        // Tìm TextView hiển thị greeting
                        // Nếu có binding.tvGreeting
                        try {
                            binding.tvGreeting?.text = "Chào $userName! 👋"
                        } catch (e: Exception) {
                            // Nếu không có tvGreeting trong binding
                            Log.d("HomeActivity", "tvGreeting not found: ${e.message}")
                        }
                    }
                } else {
                    // Không có user, quay về login
                    startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                    finish()
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Error loading user info: ${e.message}")
            }
        }
    }
}
