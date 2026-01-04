package smart.planner.data.sync

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import smart.planner.data.local.dao.TaskDao
import smart.planner.data.local.entity.Task
import smart.planner.data.local.entity.TaskStatus

class TaskSyncService(
    private val taskDao: TaskDao,
    private val firebaseDatabase: DatabaseReference,
    private val syncManager: SyncManager
) {
    private val TAG = "TaskSyncService"
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val tasksRef = firebaseDatabase.child("tasks")

    fun startListening() {
        scope.launch {
            syncManager.isOnline.collect { online ->
                if (online) {
                    Log.d(TAG, "Online - Starting Firebase listener")
                    observeFirebaseChanges()
                } else {
                    Log.d(TAG, "Offline - Stopped Firebase listener")
                }
            }
        }
    }

    private fun observeFirebaseChanges() {
        tasksRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                handleFirebaseTask(snapshot, "ADDED")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                handleFirebaseTask(snapshot, "CHANGED")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                handleFirebaseTask(snapshot, "REMOVED")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase listener cancelled: ${error.message}")
            }
        })
    }

    private fun handleFirebaseTask(snapshot: DataSnapshot, eventType: String) {
        scope.launch {
            try {
                val taskId = snapshot.key ?: return@launch

                when (eventType) {
                    "ADDED", "CHANGED" -> {
                        val data = snapshot.value as? Map<*, *>
                        if (data != null) {
                            val firebaseTask = Task(
                                id = taskId,
                                title = data["title"] as? String ?: "",
                                description = data["description"] as? String ?: "",
                                deadline = (data["deadline"] as? Long) ?: 0L,
                                status = TaskStatus.fromString(data["status"] as? String ?: "TODO"),
                                subjectId = data["subjectId"] as? String ?: "",
                                motivationId = data["motivationId"] as? String,
                                createdAt = (data["createdAt"] as? Long) ?: 0L,
                                updatedAt = (data["updatedAt"] as? Long) ?: 0L
                            )

                            val localTask = taskDao.getTaskById(taskId)

                            if (localTask == null || firebaseTask.updatedAt >= localTask.updatedAt) {
                                taskDao.insertTask(firebaseTask)
                                Log.d(TAG, "Synced task from Firebase: ${firebaseTask.title}")
                            } else {
                                Log.d(TAG, "Local version is newer, skipping: ${localTask.title}")
                            }
                        }
                    }

                    "REMOVED" -> {
                        taskDao.deleteTaskById(taskId)
                        Log.d(TAG, "Deleted task from local: $taskId")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling Firebase task: ${e.message}")
            }
        }
    }

    suspend fun pushTask(task: Task): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!syncManager.isOnline.value) {
                Log.d(TAG, "Offline - Task queued for sync: ${task.title}")
                return@withContext Result.failure(Exception("Offline"))
            }

            syncManager.setSyncing(true)

            val taskMap = mutableMapOf<String, Any>(
                "title" to task.title,
                "description" to task.description,
                "deadline" to task.deadline,
                "status" to task.status.name,
                "subjectId" to task.subjectId,
                "createdAt" to task.createdAt,
                "updatedAt" to task.updatedAt
            )

            task.motivationId?.let {
                taskMap["motivationId"] = it
            }

            tasksRef.child(task.id).setValue(taskMap).await()

            Log.d(TAG, "Pushed task to Firebase: ${task.title}")
            syncManager.setSyncing(false)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error pushing task: ${e.message}")
            syncManager.setSyncing(false)
            Result.failure(e)
        }
    }

    suspend fun deleteTask(taskId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!syncManager.isOnline.value) {
                return@withContext Result.failure(Exception("Offline"))
            }

            tasksRef.child(taskId).removeValue().await()
            Log.d(TAG, "Deleted task from Firebase: $taskId")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting task: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun initialSync(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!syncManager.isOnline.value) {
                return@withContext Result.failure(Exception("Offline"))
            }

            syncManager.setSyncing(true)

            val snapshot = tasksRef.get().await()
            val firebaseTasks = snapshot.children.mapNotNull { child ->
                val data = child.value as? Map<*, *>
                if (data != null) {
                    Task(
                        id = child.key ?: "",
                        title = data["title"] as? String ?: "",
                        description = data["description"] as? String ?: "",
                        deadline = (data["deadline"] as? Long) ?: 0L,
                        status = TaskStatus.fromString(data["status"] as? String ?: "TODO"),
                        subjectId = data["subjectId"] as? String ?: "",
                        motivationId = data["motivationId"] as? String,
                        createdAt = (data["createdAt"] as? Long) ?: 0L,
                        updatedAt = (data["updatedAt"] as? Long) ?: 0L
                    )
                } else null
            }

            val localTasks = taskDao.getAllTasks().first()

            val mergedTasks = mergeTasks(localTasks, firebaseTasks)

            taskDao.insertTasks(mergedTasks)

            localTasks.forEach { local ->
                if (firebaseTasks.none { it.id == local.id }) {
                    pushTask(local)
                }
            }

            Log.d(TAG, "Initial sync completed: ${mergedTasks.size} tasks")
            syncManager.setSyncing(false)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Initial sync failed: ${e.message}")
            syncManager.setSyncing(false)
            Result.failure(e)
        }
    }

    private fun mergeTasks(local: List<Task>, remote: List<Task>): List<Task> {
        val merged = mutableMapOf<String, Task>()

        remote.forEach { merged[it.id] = it }

        local.forEach { localTask ->
            val remoteTask = merged[localTask.id]
            if (remoteTask == null || localTask.updatedAt > remoteTask.updatedAt) {
                merged[localTask.id] = localTask
            }
        }

        return merged.values.toList()
    }

    fun cleanup() {
        scope.cancel()
    }
}