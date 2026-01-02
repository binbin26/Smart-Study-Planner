package smart.planner.data.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import smart.planner.data.local.AppDatabase
import smart.planner.data.local.TaskDao
import smart.planner.data.model.Task

/**
 * Repository cho Deadline Management với đầy đủ CRUD operations
 */
class DeadlineRepository(private val context: Context) {
    
    private val taskDao: TaskDao = AppDatabase.getDatabase(context).taskDao()
    
    // ==================== SET DEADLINE ====================
    
    /**
     * Thiết lập deadline cho bài tập
     */
    suspend fun setDeadline(
        taskId: Int,
        deadline: Long,
        reminderTime: Long? = null
    ): Result<Task> {
        return withContext(Dispatchers.IO) {
            try {
                val task = taskDao.getTaskById(taskId)
                if (task == null) {
                    return@withContext Result.failure(Exception("Task not found"))
                }
                
                // Validation
                if (deadline <= 0) {
                    return@withContext Result.failure(Exception("Invalid deadline: deadline must be greater than 0"))
                }
                
                // Validation: reminderTime phải trước deadline
                if (reminderTime != null) {
                    if (reminderTime < System.currentTimeMillis()) {
                        return@withContext Result.failure(Exception("Reminder time cannot be in the past"))
                    }
                    if (reminderTime >= deadline) {
                        return@withContext Result.failure(Exception("Reminder time must be before deadline"))
                    }
                }
                
                // Cập nhật deadline
                taskDao.updateTaskDeadline(taskId, deadline)
                val updatedTask = taskDao.getTaskById(taskId)
                
                if (updatedTask != null) {
                    // TODO: Lên lịch notification nếu có reminderTime
                    // Note: Hiện tại Task entity chưa có field reminderTime, cần thêm nếu muốn lưu
                    Result.success(updatedTask)
                } else {
                    Result.failure(Exception("Failed to set deadline"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Chỉnh sửa deadline
     */
    suspend fun updateDeadline(
        taskId: Int,
        deadline: Long,
        reminderTime: Long? = null
    ): Result<Task> {
        return setDeadline(taskId, deadline, reminderTime)
    }
    
    /**
     * Xóa deadline
     * Note: Set deadline = 0 để đánh dấu không có deadline
     * Validation trong setDeadline() yêu cầu deadline > 0, nên deadline = 0 là hợp lệ để xóa
     */
    suspend fun removeDeadline(taskId: Int): Result<Task> {
        return withContext(Dispatchers.IO) {
            try {
                val task = taskDao.getTaskById(taskId)
                if (task == null) {
                    return@withContext Result.failure(Exception("Task not found"))
                }
                
                // Set deadline = 0 để xóa deadline (0 = không có deadline)
                taskDao.updateTaskDeadline(taskId, 0)
                val updatedTask = taskDao.getTaskById(taskId)
                
                if (updatedTask != null) {
                    // TODO: Hủy notification schedule nếu có
                    Result.success(updatedTask)
                } else {
                    Result.failure(Exception("Failed to remove deadline"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    // ==================== FILTER BY TIME ====================
    
    /**
     * Lọc bài tập theo thời gian
     */
    suspend fun filterTasksByTime(
        startTime: Long? = null,
        endTime: Long? = null,
        filterType: String? = null
    ): Result<List<Task>> {
        return withContext(Dispatchers.IO) {
            try {
                val currentTime = System.currentTimeMillis()
                val tasks = when (filterType) {
                    "upcoming" -> {
                        val futureTime = endTime ?: (currentTime + 7 * 24 * 60 * 60 * 1000L)
                        taskDao.getUpcomingTasks(currentTime, futureTime)
                    }
                    "overdue" -> {
                        taskDao.getOverdueTasks(currentTime)
                    }
                    "today" -> {
                        val startOfDay = currentTime - (currentTime % (24 * 60 * 60 * 1000L))
                        val endOfDay = startOfDay + (24 * 60 * 60 * 1000L) - 1
                        taskDao.getTasksByTimeRange(startOfDay, endOfDay)
                    }
                    "this_week" -> {
                        val startOfWeek = currentTime - (currentTime % (7 * 24 * 60 * 60 * 1000L))
                        val endOfWeek = startOfWeek + (7 * 24 * 60 * 60 * 1000L) - 1
                        taskDao.getTasksByTimeRange(startOfWeek, endOfWeek)
                    }
                    else -> {
                        if (startTime != null && endTime != null) {
                            taskDao.getTasksByTimeRange(startTime, endTime)
                        } else {
                            taskDao.getAllTasksSync()
                        }
                    }
                }
                Result.success(tasks)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Lấy danh sách bài tập sắp đến deadline
     */
    suspend fun getUpcomingTasks(days: Int = 7): Result<List<Task>> {
        return withContext(Dispatchers.IO) {
            try {
                val currentTime = System.currentTimeMillis()
                val futureTime = currentTime + (days * 24 * 60 * 60 * 1000L)
                val tasks = taskDao.getUpcomingTasks(currentTime, futureTime)
                Result.success(tasks)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Lấy danh sách bài tập đã quá deadline
     */
    suspend fun getOverdueTasks(): Result<List<Task>> {
        return withContext(Dispatchers.IO) {
            try {
                val currentTime = System.currentTimeMillis()
                val tasks = taskDao.getOverdueTasks(currentTime)
                Result.success(tasks)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
