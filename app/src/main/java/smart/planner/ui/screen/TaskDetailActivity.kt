package smart.planner.ui.screen

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import smart.planner.R
import smart.planner.data.model.Task
import smart.planner.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var taskViewModel: TaskViewModel
    private var currentTask: Task? = null
    private var taskId: Int = -1
    private var newDeadline: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        // Setup ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Chi tiết Task"

        // Initialize ViewModel
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        taskViewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]

        // Get task ID from intent
        taskId = intent.getIntExtra("taskId", -1)

        if (taskId == -1) {
            Toast.makeText(this, "❌ Task không tồn tại", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        loadTaskData()
    }

    private fun loadTaskData() {
        taskViewModel.allTasks.observe(this) { tasks ->
            currentTask = tasks.find { it.id == taskId }
            currentTask?.let { displayTask(it) }
        }
    }

    private fun displayTask(task: Task) {
        // Hiển thị Title
        findViewById<TextInputEditText>(R.id.etTaskTitle).setText(task.title)

        // Hiển thị Description
        findViewById<TextInputEditText>(R.id.etTaskDescription).setText(task.description)

        // Hiển thị Deadline
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        findViewById<TextView>(R.id.tvDeadlineValue).text = sdf.format(Date(task.deadline))
        newDeadline = task.deadline

        // Setup Status Spinner
        setupStatusSpinner(task.status)
    }

    private fun setupStatusSpinner(currentStatus: String) {
        val spinner = findViewById<Spinner>(R.id.spinnerStatus)

        // 3 trạng thái
        val statuses = arrayOf(
            "TODO" to "📝 Chưa làm",
            "IN_PROGRESS" to "⏳ Đang làm",
            "DONE" to "✅ Hoàn thành"
        )

        val displayNames = statuses.map { it.second }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, displayNames)
        spinner.adapter = adapter

        // Set vị trí hiện tại
        val currentIndex = statuses.indexOfFirst { it.first == currentStatus }
        if (currentIndex >= 0) {
            spinner.setSelection(currentIndex)
        }
    }

    private fun setupUI() {
        // Nút "Đổi" deadline
        findViewById<Button>(R.id.btnChangeDeadline).setOnClickListener {
            showDatePicker()
        }

        // Nút "Lưu"
        findViewById<Button>(R.id.btnSaveChanges).setOnClickListener {
            saveChanges()
        }

        // Nút "Hủy"
        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            finish()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = newDeadline

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay, 23, 59, 59)
                newDeadline = calendar.timeInMillis

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                findViewById<TextView>(R.id.tvDeadlineValue).text = sdf.format(calendar.time)

                Toast.makeText(this, "📅 Đã đổi deadline", Toast.LENGTH_SHORT).show()
            },
            year,
            month,
            day
        ).show()
    }

    private fun saveChanges() {
        val task = currentTask ?: return

        // Lấy dữ liệu mới
        val newTitle = findViewById<TextInputEditText>(R.id.etTaskTitle).text.toString().trim()
        val newDescription = findViewById<TextInputEditText>(R.id.etTaskDescription).text.toString().trim()
        val spinner = findViewById<Spinner>(R.id.spinnerStatus)
        val selectedPosition = spinner.selectedItemPosition

        // Map position → status
        val newStatus = when (selectedPosition) {
            0 -> "TODO"
            1 -> "IN_PROGRESS"
            2 -> "DONE"
            else -> "TODO"
        }

        // Validation
        if (newTitle.isEmpty()) {
            Toast.makeText(this, "⚠️ Tiêu đề không được để trống", Toast.LENGTH_SHORT).show()
            findViewById<TextInputEditText>(R.id.etTaskTitle).requestFocus()
            return
        }

        // Tạo task mới với dữ liệu updated
        val updatedTask = task.copy(
            title = newTitle,
            description = newDescription,
            status = newStatus,
            deadline = newDeadline,
            updatedAt = System.currentTimeMillis()
        )

        taskViewModel.updateTask(updatedTask)

        Toast.makeText(this, "✅ Đã lưu thay đổi!", Toast.LENGTH_SHORT).show()

        // Log để debug
        android.util.Log.d("TaskDetail", "Updated: $updatedTask")

        finish()
        setResult(RESULT_OK)  // Báo cho HomeActivity biết đã update
        finish()
    }

    // Handle back button
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}