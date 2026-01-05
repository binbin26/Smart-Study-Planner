package smart.planner.data.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import smart.planner.data.local.TaskDao
import smart.planner.data.model.Task

/**
 * Repository cho Task Management với đầy đủ CRUD operations
 */
class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    // ==================== CREATE ====================
    
    /**
     * Tạo mới task
     */
    suspend fun createTask(
        name: String,
        subject: String,
        deadline: Long,
        description: String
    ): Result<Task> {
        return withContext(Dispatchers.IO) {
            try {
                // Validation
                if (name.isBlank()) {
                    return@withContext Result.failure(Exception("Task name cannot be empty"))
                }
                if (subject.isBlank()) {
                    return@withContext Result.failure(Exception("Subject cannot be empty"))
                }
                if (deadline <= 0) {
                    return@withContext Result.failure(Exception("Invalid deadline"))
                }

                val newTask = Task(
                    name = name.trim(),
                    subject = subject.trim(),
                    deadline = deadline,
                    description = description.trim()
                )

                val taskId = taskDao.insertTask(newTask)
                val createdTask = taskDao.getTaskById(taskId.toInt())

                if (createdTask != null) {
                    Result.success(createdTask)
                } else {
                    Result.failure(Exception("Failed to create task"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Thêm task (legacy method)
     */
    suspend fun insert(task: Task): Result<Long> {
        return withContext(Dispatchers.IO) {
            try {
                val taskId = taskDao.insertTask(task)
                Result.success(taskId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ==================== READ ====================
    
    /**
     * Lấy tất cả tasks
     */
    suspend fun getAllTasks(): Result<List<Task>> {
        return withContext(Dispatchers.IO) {
            try {
                val tasks = taskDao.getAllTasksSync()
                Result.success(tasks)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Lấy task theo ID
     */
    suspend fun getTaskById(taskId: Int): Result<Task> {
        return withContext(Dispatchers.IO) {
            try {
                val task = taskDao.getTaskById(taskId)
                if (task != null) {
                    Result.success(task)
                } else {
                    Result.failure(Exception("Task not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Lấy tasks theo subject
     */
    suspend fun getTasksBySubject(subject: String): Result<List<Task>> {
        return withContext(Dispatchers.IO) {
            try {
                val tasks = taskDao.getTasksBySubject(subject)
                Result.success(tasks)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Lấy tasks theo khoảng thời gian
     */
    suspend fun getTasksByTimeRange(startTime: Long, endTime: Long): Result<List<Task>> {
        return withContext(Dispatchers.IO) {
            try {
                val tasks = taskDao.getTasksByTimeRange(startTime, endTime)
                Result.success(tasks)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ==================== UPDATE ====================
    
    /**
     * Cập nhật task
     */
    suspend fun updateTask(
        taskId: Int,
        name: String? = null,
        subject: String? = null,
        deadline: Long? = null,
        description: String? = null
    ): Result<Task> {
        return withContext(Dispatchers.IO) {
            try {
                val task = taskDao.getTaskById(taskId)
                if (task == null) {
                    return@withContext Result.failure(Exception("Task not found"))
                }

                // Validation
                if (name != null && name.isBlank()) {
                    return@withContext Result.failure(Exception("Task name cannot be empty"))
                }
                if (subject != null && subject.isBlank()) {
                    return@withContext Result.failure(Exception("Subject cannot be empty"))
                }
                if (deadline != null && deadline <= 0) {
                    return@withContext Result.failure(Exception("Invalid deadline"))
                }

                val updatedTask = task.copy(
                    name = name?.trim() ?: task.name,
                    subject = subject?.trim() ?: task.subject,
                    deadline = deadline ?: task.deadline,
                    description = description?.trim() ?: task.description
                )

                taskDao.updateTask(updatedTask)
                Result.success(updatedTask)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Cập nhật deadline
     */
    suspend fun updateDeadline(taskId: Int, deadline: Long): Result<Task> {
        return withContext(Dispatchers.IO) {
            try {
                val task = taskDao.getTaskById(taskId)
                if (task == null) {
                    return@withContext Result.failure(Exception("Task not found"))
                }

                if (deadline <= 0) {
                    return@withContext Result.failure(Exception("Invalid deadline"))
                }

                taskDao.updateTaskDeadline(taskId, deadline)
                val updatedTask = taskDao.getTaskById(taskId)

                if (updatedTask != null) {
                    Result.success(updatedTask)
                } else {
                    Result.failure(Exception("Failed to update deadline"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ==================== DELETE ====================
    
    /**
     * Xóa task
     */
    suspend fun deleteTask(taskId: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val task = taskDao.getTaskById(taskId)
                if (task == null) {
                    return@withContext Result.failure(Exception("Task not found"))
                }

                taskDao.deleteTaskById(taskId)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Xóa task (legacy method)
     */
    suspend fun delete(task: Task): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                taskDao.deleteTask(task)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Xóa tất cả tasks
     */
    suspend fun deleteAllTasks(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                taskDao.deleteAllTasks()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
