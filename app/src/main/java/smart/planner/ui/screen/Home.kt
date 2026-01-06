package smart.planner.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import kotlinx.coroutines.launch
import smart.planner.R
import smart.planner.data.model.Task
import smart.planner.databinding.ActivityHomeBinding
import smart.planner.ui.adapter.UpcomingTaskAdapter
import smart.planner.ui.screen.TaskDetailActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var taskAdapter: UpcomingTaskAdapter
    private var allTasks: List<Task> = emptyList()

    // Giữ decorator hiện tại để remove khi cập nhật
    private var currentDeadlineDecorator: DeadlineDecorator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeData()
        setupCalendar()
        setupBottomNavigation()
        createNotificationChannel()
    }
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "task_reminder", // Channel ID
            "Task Reminder", // Channel Name
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
    }

    private fun setupRecyclerView() {
        taskAdapter = UpcomingTaskAdapter(
            onTaskClick = { task ->
                val intent = Intent(this, TaskDetailActivity::class.java).apply {
                    putExtra("taskId", task.id)
                    putExtra("taskTitle", task.name) // Đổi title → name
                    putExtra("taskDescription", task.description)
                    putExtra("taskDeadline", task.deadline)
                }
                startActivity(intent)
            },
            onCheckboxClick = { task, isChecked ->
                // Task entity không có isCompleted field, chỉ hiển thị toast
                Toast.makeText(
                    this,
                    if (isChecked) "Đã hoàn thành: ${task.name}" else "Chưa hoàn thành: ${task.name}",
                    Toast.LENGTH_SHORT
                ).show()
                updateDashboard(taskAdapter.currentList)

                // Nếu có logic thay đổi deadline hoặc lọc danh sách theo ngày, có thể cần re-decorate
                applyDeadlineDecorators(calculateDeadlineDays(allTasks))
            }
        )


        binding.rvUpcomingTasks.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = taskAdapter
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            val mockTasks = listOf(
                Task(
                    id = 1,
                    name = "Bài tập Android", // Đổi title → name
                    description = "Hoàn thành bài tập chương 3",
                    subject = "Lập trình", // Đổi subjectId → subject (String)
                    deadline = System.currentTimeMillis() + 86400000 // +1 ngày
                ),
                Task(
                    id = 2,
                    name = "Đọc SGK Văn", // Đổi title → name
                    description = "Đọc bài thơ Đất Nước",
                    subject = "Văn học", // Đổi subjectId → subject (String)
                    deadline = System.currentTimeMillis() + 172800000 // +2 ngày
                ),
                Task(
                    id = 3,
                    name = "Lab Vật Lý", // Đổi title → name
                    description = "Chuẩn bị báo cáo thí nghiệm",
                    subject = "Vật Lý", // Đổi subjectId → subject (String)
                    deadline = System.currentTimeMillis() + 259200000 // +3 ngày
                )
            )
            allTasks = mockTasks
            taskAdapter.submitList(mockTasks)
            updateDashboard(mockTasks)

            // Decorate ngày có deadline
            applyDeadlineDecorators(calculateDeadlineDays(allTasks))
        }
    }

    private fun setupCalendar() {
        binding.materialCalendarView.setOnDateChangedListener { _, date, _ ->
            val selectedTasks = allTasks.filter { task ->
                val cal = java.util.Calendar.getInstance().apply { timeInMillis = task.deadline }
                val taskDay = CalendarDay.from(cal) // KHÔNG cộng/trừ tháng thủ công
                taskDay == date
            }

            if (selectedTasks.isNotEmpty()) {
                taskAdapter.submitList(selectedTasks)
                updateDashboard(selectedTasks)
            } else {
                Toast.makeText(this, "Không có deadline ngày này", Toast.LENGTH_SHORT).show()
                taskAdapter.submitList(emptyList())
                updateDashboard(emptyList())
            }
        }

        // Nếu người dùng chuyển tháng, ensure redraw decorators
        binding.materialCalendarView.setOnMonthChangedListener { _, _ ->
            binding.materialCalendarView.invalidateDecorators()
        }
    }

    private fun updateDashboard(tasks: List<Task>) {
        val urgentCount = tasks.filter {
            it.deadline - System.currentTimeMillis() < 2 * 86400000
        }.size
        // Task entity không có isCompleted field, đếm tasks có deadline đã qua
        val currentTime = System.currentTimeMillis()
        val completedCount = tasks.count { it.deadline < currentTime }
        val progress = if (tasks.isNotEmpty()) (completedCount * 100) / tasks.size else 0

        binding.tvSubtitle.text = "Hôm nay bạn có ${tasks.size} bài tập cần hoàn thành"
        binding.progressTask.progress = progress
        // Bạn có thể hiển thị urgentCount ở card "Sắp đến hạn" nếu cần
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_add -> {
                    Toast.makeText(this, "Task List", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    // Tính set ngày có deadline từ danh sách tasks
    private fun calculateDeadlineDays(tasks: List<Task>): Set<CalendarDay> {
        return tasks.map { task ->
            val cal = java.util.Calendar.getInstance().apply { timeInMillis = task.deadline }
            CalendarDay.from(cal) // An toàn, không sai tháng
        }.toSet()
    }

    // Áp dụng decorator: remove cũ, add mới, invalidate để refresh
    private fun applyDeadlineDecorators(deadlineDays: Set<CalendarDay>) {
        currentDeadlineDecorator?.let { binding.materialCalendarView.removeDecorator(it) }
        val newDecorator = DeadlineDecorator(this, deadlineDays)
        binding.materialCalendarView.addDecorator(newDecorator)
        currentDeadlineDecorator = newDecorator
        binding.materialCalendarView.invalidateDecorators()
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
}
