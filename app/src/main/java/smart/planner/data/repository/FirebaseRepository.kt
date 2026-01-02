package smart.planner.data.repository

import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import smart.planner.data.model.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance().apply {
        // Enable offline persistence
        setPersistenceEnabled(true)
    }

    private val tasksRef: DatabaseReference = database.getReference("tasks").apply {
        // Keep data synced even when offline
        keepSynced(true)
    }

    init {
        // Enable disk persistence for offline support
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        } catch (e: DatabaseException) {
            // Already enabled
        }
    }

    /**
     * Sync tasks from local to Firebase
     * Upload tất cả tasks lên Firebase
     */
    suspend fun syncTasks(tasks: List<Task>) {
        try {
            // Clear existing tasks
            tasksRef.removeValue().await()

            // Upload new tasks with timestamp for conflict resolution
            tasks.forEach { task ->
                val taskWithTimestamp = mapOf(
                    "id" to task.id,
                    "name" to task.name,
                    "description" to task.description,
                    "subject" to task.subject,
                    "deadline" to task.deadline,
                    "lastModified" to ServerValue.TIMESTAMP // Server timestamp
                )
                tasksRef.child(task.id.toString()).setValue(taskWithTimestamp).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Failed to sync tasks: ${e.message}")
        }
    }

    /**
     * Sync single task with conflict resolution
     * Data mới nhất (theo timestamp) sẽ được giữ
     */
    suspend fun syncTask(task: Task) = suspendCoroutine<Unit> { continuation ->
        try {
            val taskRef = tasksRef.child(task.id.toString())

            // Use transaction for conflict resolution
            taskRef.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val existingTask = currentData.getValue(TaskWithTimestamp::class.java)
                    val newTimestamp = System.currentTimeMillis()

                    // If no existing data OR new data is newer, update
                    if (existingTask == null || newTimestamp > (existingTask.lastModified ?: 0)) {
                        currentData.value = mapOf(
                            "id" to task.id,
                            "name" to task.name,
                            "description" to task.description,
                            "subject" to task.subject,
                            "deadline" to task.deadline,
                            "lastModified" to newTimestamp
                        )
                    }
                    // Else keep existing data (it's newer)

                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    snapshot: DataSnapshot?
                ) {
                    if (error != null) {
                        continuation.resumeWithException(error.toException())
                    } else {
                        continuation.resume(Unit)
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            continuation.resumeWithException(Exception("Failed to sync task: ${e.message}"))
        }
    }

    /**
     * Observe tasks from Firebase using Flow
     * ✓ Realtime updates khi có thay đổi
     * ✓ Offline: dùng cached data
     * ✓ Online: auto sync
     */
    fun observeTasks(): Flow<List<Task>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasks = mutableListOf<Task>()

                snapshot.children.forEach { childSnapshot ->
                    try {
                        val task = childSnapshot.getValue(TaskWithTimestamp::class.java)?.toTask()
                        task?.let { tasks.add(it) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // Sort by lastModified (newest first)
                val sortedTasks = tasks.sortedByDescending { it.deadline }

                // Emit list to Flow
                trySend(sortedTasks)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                close(error.toException())
            }
        }

        // Attach listener
        tasksRef.addValueEventListener(listener)

        // Remove listener when Flow is closed
        awaitClose {
            tasksRef.removeEventListener(listener)
        }
    }

    /**
     * Get tasks once (not realtime)
     * Works offline with cached data
     */
    suspend fun getTasks(): List<Task> {
        return try {
            val snapshot = tasksRef.get().await()
            val tasks = mutableListOf<Task>()

            snapshot.children.forEach { childSnapshot ->
                try {
                    val task = childSnapshot.getValue(TaskWithTimestamp::class.java)?.toTask()
                    task?.let { tasks.add(it) }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            tasks
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Add new task with timestamp
     */
    suspend fun addTask(task: Task) {
        try {
            val taskWithTimestamp = mapOf(
                "id" to task.id,
                "name" to task.name,
                "description" to task.description,
                "subject" to task.subject,
                "deadline" to task.deadline,
                "lastModified" to ServerValue.TIMESTAMP
            )
            tasksRef.child(task.id.toString()).setValue(taskWithTimestamp).await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Failed to add task: ${e.message}")
        }
    }

    /**
     * Update task with conflict resolution
     */
    suspend fun updateTask(task: Task) {
        syncTask(task) // Use syncTask which has conflict resolution
    }

    /**
     * Delete task
     * Works offline, syncs when online
     */
    suspend fun deleteTask(taskId: Int) {
        try {
            tasksRef.child(taskId.toString()).removeValue().await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Failed to delete task: ${e.message}")
        }
    }

    /**
     * Update task (Note: Task entity doesn't have isCompleted field, removed this method)
     * Use updateTask() instead
     */

    /**
     * Check connection status
     */
    fun observeConnectionStatus(): Flow<Boolean> = callbackFlow {
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                trySend(connected)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        connectedRef.addValueEventListener(listener)

        awaitClose {
            connectedRef.removeEventListener(listener)
        }
    }
}

// Helper data class with timestamp
private data class TaskWithTimestamp(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val subject: String = "",
    val deadline: Long = 0,
    val lastModified: Long? = null
) {
    fun toTask() = Task(
        id = id,
        name = name,
        description = description,
        subject = subject,
        deadline = deadline
    )
}