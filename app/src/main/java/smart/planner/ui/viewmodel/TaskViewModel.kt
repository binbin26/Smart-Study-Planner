package smart.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import smart.planner.data.local.AppDatabase
import smart.planner.data.model.Task
import smart.planner.data.repository.TaskRepository

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    // Khai báo công khai (không có private) để MainActivity và CheckTaskActivity có thể quan sát dữ liệu
    val allTasks: LiveData<List<Task>>

    init {
        // Khởi tạo cơ sở dữ liệu và repository
        val dao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(dao)

        // Gán LiveData từ repository vào allTasks để UI có thể theo dõi biến động dữ liệu
        allTasks = repository.allTasks
    }

    /**
     * Thêm task mới vào cơ sở dữ liệu Room
     * Chạy trên Dispatchers.IO để không gây lag giao diện (Main Thread)
     */
    fun addTask(name: String, subject: String, deadline: Long, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newTask = Task(
                name = name,
                subject = subject,
                deadline = deadline,
                description = description
            )
            repository.insert(newTask)
        }
    }

    /**
     * Xóa một task khỏi cơ sở dữ liệu
     * Sau khi xóa, LiveData (allTasks) sẽ tự động cập nhật và báo cho UI biến mất
     */
    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(task)
        }
    }
}