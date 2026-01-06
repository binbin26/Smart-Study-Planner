package smart.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import smart.planner.data.local.AppDatabase
import smart.planner.data.local.TaskRealtimeDao
import smart.planner.data.model.Task
import smart.planner.data.repository.TaskRepository

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskDao = AppDatabase.getDatabase(application).taskDao()
    private val repository: TaskRepository = TaskRepository(taskDao)
    private val realtimeDao = TaskRealtimeDao()

    val taskList: LiveData<List<Task>> = repository.allTasks
    val allTasks: LiveData<List<Task>> = repository.allTasks

    fun addTask(title: String, subject: String, deadline: Long, desc: String) {
        // Lấy ID từ Firebase trước khi lưu
        val firebaseId = realtimeDao.getNewKey() ?: ""

        val newTask = Task(
            title = title,
            subjectName = subject,
            deadline = deadline,
            description = desc,
            createdAt = System.currentTimeMillis(),
            isDone = false,
            firebaseId = firebaseId
        )

        // Lưu đồng bộ cả hai nơi
        realtimeDao.saveTask(firebaseId, newTask)
        viewModelScope.launch {
            repository.insert(newTask)
        }
    }

    fun fetchTasks() {
        realtimeDao.getAllTasks { remoteTasks ->
            viewModelScope.launch {
                remoteTasks.forEach { remoteTask ->
                    repository.insert(remoteTask)
                }
            }
        }
    }

    fun deleteTask(task: Task) {
        // Chỉ thực hiện xóa nếu firebaseId hợp lệ
        if (task.firebaseId.isNotEmpty()) {
            realtimeDao.deleteTask(task.firebaseId) { success ->
                if (success) {
                    viewModelScope.launch {
                        repository.delete(task)
                    }
                }
            }
        } else {
            // Trường hợp task chỉ tồn tại local
            viewModelScope.launch {
                repository.delete(task)
            }
        }
    }
}