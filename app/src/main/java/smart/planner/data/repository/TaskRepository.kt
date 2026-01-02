package smart.planner.data.repository

import kotlinx.coroutines.flow.Flow
import smart.planner.data.local.dao.TaskDao
import smart.planner.data.local.entity.Task
import smart.planner.data.local.entity.TaskStatus
import smart.planner.data.sync.TaskSyncService

class TaskRepository(
    private val taskDao: TaskDao,
    private val syncService: TaskSyncService
) {

    // ==================== READ ====================

    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
    }

    fun getTasksBySubject(subjectId: String): Flow<List<Task>> {
        return taskDao.getTasksBySubject(subjectId)
    }

    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>> {
        return taskDao.getTasksByStatus(status)
    }

    fun getUncompletedTasks(): Flow<List<Task>> {
        return taskDao.getUncompletedTasks()
    }

    fun getOverdueTasks(): Flow<List<Task>> {
        return taskDao.getOverdueTasks()
    }

    suspend fun getTaskById(id: String): Task? {
        return taskDao.getTaskById(id)
    }

    // ==================== CREATE ====================

    suspend fun addTask(task: Task): Result<Unit> {
        return try {
            taskDao.insertTask(task)
            syncService.pushTask(task)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== UPDATE ====================

    suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            val updatedTask = task.copy(
                updatedAt = System.currentTimeMillis()
            )
            taskDao.updateTask(updatedTask)
            syncService.pushTask(updatedTask)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Result<Unit> {
        return try {
            val timestamp = System.currentTimeMillis()
            taskDao.updateTaskStatus(taskId, status, timestamp)

            val task = taskDao.getTaskById(taskId)
            if (task != null) {
                syncService.pushTask(task)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeTask(taskId: String): Result<Unit> {
        return updateTaskStatus(taskId, TaskStatus.DONE)
    }

    suspend fun updateTaskDeadline(taskId: String, newDeadline: Long): Result<Unit> {
        return try {
            val timestamp = System.currentTimeMillis()
            taskDao.updateTaskDeadline(taskId, newDeadline, timestamp)

            val task = taskDao.getTaskById(taskId)
            if (task != null) {
                syncService.pushTask(task)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== DELETE ====================

    suspend fun deleteTask(task: Task): Result<Unit> {
        return try {
            taskDao.deleteTask(task)
            syncService.deleteTask(task.id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTaskById(id: String): Result<Unit> {
        return try {
            taskDao.deleteTaskById(id)
            syncService.deleteTask(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCompletedTasks(): Result<Unit> {
        return try {
            taskDao.deleteCompletedTasks()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== SYNC ====================

    suspend fun sync(): Result<Unit> {
        return syncService.initialSync()
    }

    suspend fun updateOverdueTasks(): Result<Unit> {
        return try {
            taskDao.markOverdueTasks()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}