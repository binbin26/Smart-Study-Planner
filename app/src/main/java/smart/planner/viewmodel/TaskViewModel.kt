package smart.planner.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import smart.planner.data.database.AppDatabase
import smart.planner.data.entity.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import smart.planner.data.entity.QuoteCache

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    /* ===================== CŨ – GIỮ NGUYÊN ===================== */

    private val _taskList = MutableLiveData<List<Task>>()
    val taskList: LiveData<List<Task>> = _taskList

    private val _taskStatus = MutableLiveData<String>()
    val taskStatus: LiveData<String> = _taskStatus
    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState
    private val db = AppDatabase.getDatabase(application)

    /* ===================== MỚI – LOADING INDICATOR ===================== */



    /* ===================== LOAD TASK ===================== */

    fun loadTasks(subjectId: Int) {
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing   // 🔵 BẮT ĐẦU SYNC

            try {
                delay(1500) // giả lập sync để nhìn thấy ProgressBar (demo)

                val tasks = db.taskDao().getTasksBySubject(subjectId)
                _taskList.postValue(tasks)

                _syncState.value = SyncState.Success
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Load tasks failed", e)
                _taskList.postValue(emptyList())
                _syncState.value = SyncState.Error("Load tasks failed")
            }
        }
    }


    /* ===================== UPDATE TASK STATUS ===================== */

    fun updateTaskStatus(task: Task, newStatus: String) {
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing

            try {
                delay(800)
                val updatedTask = task.copy(status = newStatus)
                db.taskDao().insert(updatedTask)
                _taskStatus.postValue("Task status updated")
                _syncState.value = SyncState.Success
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Update task failed", e)
                _taskStatus.postValue("Failed to update task status")
                _syncState.value = SyncState.Error("Update task failed")
            }
        }
    }
    /* ===================== Load TASK ===================== */
    fun loadQuote() {
        viewModelScope.launch {
            val dao = db.quoteCacheDao()
            val cache = dao.getCache()

            val CACHE_EXPIRE = 24 * 60 * 60 * 1000 // 24h

            if (cache != null && System.currentTimeMillis() - cache.timestamp < CACHE_EXPIRE) {
                Log.d("Quote", "Load from cache: ${cache.content}")
            } else {
                // MOCK API (đủ để báo cáo)
                val responseContent = "Stay hungry, stay foolish"
                val responseAuthor = "Steve Jobs"

                dao.insert(
                    QuoteCache(
                        content = responseContent,
                        author = responseAuthor,
                        timestamp = System.currentTimeMillis()
                    )
                )
                Log.d("Quote", "Load from API & cache")
            }
        }
    }

    /* ===================== DELETE TASK ===================== */

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            _syncState.value = SyncState.Syncing

            try {
                delay(800)
                db.taskDao().delete(task)
                _taskStatus.postValue("Task deleted")
                _syncState.value = SyncState.Success
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Delete task failed", e)
                _taskStatus.postValue("Failed to delete task")
                _syncState.value = SyncState.Error("Delete task failed")
            }
        }
    }

}
