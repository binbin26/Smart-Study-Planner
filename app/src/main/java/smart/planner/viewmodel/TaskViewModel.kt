package smart.planner.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import smart.planner.data.database.AppDatabase
import smart.planner.data.dao.TaskRealtimeDao
import smart.planner.data.model.Task  // ✅ Đổi từ entity → model
import smart.planner.data.entity.QuoteCache
import smart.planner.data.firebase.toTask  // ✅ Đổi từ toEntity → toTask

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    /* ===================== DATABASE (CHỈ DÙNG CHO QUOTE) ===================== */

    private val db = AppDatabase.getDatabase(application)

    /* ===================== REALTIME DATABASE DAO ===================== */

    private val realtimeDao = TaskRealtimeDao()

    /* ===================== STATE ===================== */

    private val _taskList = MutableLiveData<List<Task>>()
    val taskList: LiveData<List<Task>> = _taskList

    private val _taskStatus = MutableLiveData<String>()
    val taskStatus: LiveData<String> = _taskStatus

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState

    /* ==================================================
       🔥 LOAD TASK FROM FIREBASE REALTIME DATABASE
       ================================================== */

    fun loadFromRealtimeDatabase() {
        _syncState.value = SyncState.Syncing

        realtimeDao.getAllTasks(
            onSuccess = { list ->
                val tasks = list.map { (firebaseId, model) ->
                    model.toTask()  // ✅ Đổi từ toEntity → toTask
                }

                _taskList.postValue(tasks)
                _syncState.value = SyncState.Success

                Log.d("RTDB", "Loaded ${tasks.size} tasks from Realtime DB")
            },
            onError = { error ->
                Log.e("RTDB", "Load failed", error)
                _taskList.postValue(emptyList())
                _syncState.value = SyncState.Error("Load Realtime DB failed")
            }
        )
    }

    /* ==================================================
       🔄 UPDATE TASK DONE (CHECKBOX)
       ================================================== */

    fun updateTaskDone(task: Task, isDone: Boolean) {
        _syncState.value = SyncState.Syncing

        try {
            realtimeDao.updateTaskDone(task.firebaseId, isDone)

            // Update UI local
            val updatedList = _taskList.value
                ?.map {
                    if (it.firebaseId == task.firebaseId) {
                        // ✅ Đổi từ isDone → status
                        it.copy(status = if (isDone) "DONE" else "TODO")
                    } else it
                }
                ?: emptyList()

            _taskList.postValue(updatedList)
            _taskStatus.postValue("Task updated")
            _syncState.value = SyncState.Success
        } catch (e: Exception) {
            Log.e("RTDB", "Update task failed", e)
            _taskStatus.postValue("Failed to update task")
            _syncState.value = SyncState.Error("Update task failed")
        }
    }

    /* ==================================================
       🗑 DELETE TASK (SWIPE TO DELETE)
       ================================================== */

    fun deleteTask(task: Task) {
        _syncState.value = SyncState.Syncing

        try {
            realtimeDao.deleteTask(task.firebaseId)

            val updatedList = _taskList.value
                ?.filter { it.firebaseId != task.firebaseId }
                ?: emptyList()

            _taskList.postValue(updatedList)
            _taskStatus.postValue("Task deleted")
            _syncState.value = SyncState.Success
        } catch (e: Exception) {
            Log.e("RTDB", "Delete task failed", e)
            _taskStatus.postValue("Failed to delete task")
            _syncState.value = SyncState.Error("Delete task failed")
        }
    }


    /* ===================== QUOTE CACHE (GIỮ NGUYÊN) ===================== */

    fun loadQuote() {
        viewModelScope.launch {
            val dao = db.quoteCacheDao()
            val cache = dao.getCache()
            val CACHE_EXPIRE = 24 * 60 * 60 * 1000L

            if (cache != null && System.currentTimeMillis() - cache.timestamp < CACHE_EXPIRE) {
                Log.d("Quote", "Load from cache: ${cache.content}")
            } else {
                dao.insert(
                    QuoteCache(
                        content = "Stay hungry, stay foolish",
                        author = "Steve Jobs",
                        timestamp = System.currentTimeMillis()
                    )
                )
                Log.d("Quote", "Load from API & cache")
            }
        }
    }
}
