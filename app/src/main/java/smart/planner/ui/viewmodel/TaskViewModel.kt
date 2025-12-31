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

    // SỬA TẠI ĐÂY: Phải khai báo là 'val' (không có private) để Activity truy cập được
    val allTasks: LiveData<List<Task>>

    init {
        val dao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(dao)
        // Gán dữ liệu từ repository vào biến allTasks
        allTasks = repository.allTasks
    }

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

    // Đảm bảo có hàm deleteTask để logic xóa trong Adapter hoạt động
    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(task)
        }
    }
}