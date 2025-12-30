package smart.planner.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import smart.planner.data.database.AppDatabase
import smart.planner.data.entity.Task

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val _taskList = MutableLiveData<List<Task>>()
    val taskList: LiveData<List<Task>> = _taskList

    private val _taskStatus = MutableLiveData<String>()
    val taskStatus: LiveData<String> = _taskStatus

    private val db = AppDatabase.getDatabase(application)

    // Hàm lấy danh sách công việc từ cơ sở dữ liệu
    fun loadTasks(subjectId: Int) {
        viewModelScope.launch {
            try {
                val tasks = db.taskDao().getTasksBySubject(subjectId)
                _taskList.postValue(tasks)
            } catch (e: Exception) {
                _taskList.postValue(emptyList())
            }
        }
    }

    // Hàm cập nhật trạng thái công việc
    fun updateTaskStatus(task: Task, newStatus: String) {
        viewModelScope.launch {
            try {
                val updatedTask = task.copy(status = newStatus)
                db.taskDao().insert(updatedTask)
                _taskStatus.postValue("Task status updated")
            } catch (e: Exception) {
                _taskStatus.postValue("Failed to update task status")
            }
        }
    }

    // Hàm xóa công việc
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                db.taskDao().delete(task)  // Gọi phương thức xóa trong Dao
                _taskStatus.postValue("Task deleted")
            } catch (e: Exception) {
                _taskStatus.postValue("Failed to delete task")
            }
        }
    }
}


