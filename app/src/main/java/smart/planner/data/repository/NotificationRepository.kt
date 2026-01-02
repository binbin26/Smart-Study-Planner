package smart.planner.data.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import smart.planner.data.local.AppDatabase
import smart.planner.data.local.TaskDao
import smart.planner.data.model.Task

/**
 * Repository cho Notification Management với đầy đủ CRUD operations
 */
class NotificationRepository(private val context: Context) {
    
    private val taskDao: TaskDao = AppDatabase.getDatabase(context).taskDao()
    
    // ==================== REMINDER MANAGEMENT ====================
    
    /**
     * Bật nhắc nhở cho bài tập
     */
    suspend fun enableReminder(
        taskId: Int,
        reminderTime: Long,
        reminderType: String? = "deadline"
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val task = taskDao.getTaskById(taskId)
                if (task == null) {
                    return@withContext Result.failure(Exception("Task not found"))
                }
                
                // Validation
                val currentTime = System.currentTimeMillis()
                if (reminderTime < currentTime) {
                    return@withContext Result.failure(Exception("Reminder time cannot be in the past"))
                }
                
                if (task.deadline > 0 && reminderTime >= task.deadline) {
                    return@withContext Result.failure(Exception("Reminder time must be before deadline"))
                }
                
                // TODO: Lưu reminderTime vào database (có thể thêm field reminderTime vào Task entity)
                // TODO: Lên lịch notification với Android NotificationManager
                // notificationManager.scheduleReminder(taskId, reminderTime)
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Tắt nhắc nhở cho bài tập
     */
    suspend fun disableReminder(taskId: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val task = taskDao.getTaskById(taskId)
                if (task == null) {
                    return@withContext Result.failure(Exception("Task not found"))
                }
                
                // TODO: Xóa reminderTime khỏi database
                // TODO: Hủy notification đã lên lịch
                // notificationManager.cancelReminder(taskId)
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Cập nhật thời gian nhắc nhở
     */
    suspend fun updateReminderTime(
        taskId: Int,
        newReminderTime: Long
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val task = taskDao.getTaskById(taskId)
                if (task == null) {
                    return@withContext Result.failure(Exception("Task not found"))
                }
                
                // Validation
                val currentTime = System.currentTimeMillis()
                if (newReminderTime < currentTime) {
                    return@withContext Result.failure(Exception("Reminder time cannot be in the past"))
                }
                
                if (task.deadline > 0 && newReminderTime >= task.deadline) {
                    return@withContext Result.failure(Exception("Reminder time must be before deadline"))
                }
                
                // TODO: Cập nhật reminderTime trong database
                // TODO: Hủy notification cũ và lên lịch lại
                // notificationManager.cancelReminder(taskId)
                // notificationManager.scheduleReminder(taskId, newReminderTime)
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    // ==================== READ ====================
    
    /**
     * Lấy chi tiết bài tập từ thông báo
     */
    suspend fun getTaskDetailsFromNotification(
        notificationId: String,
        taskId: Int
    ): Result<Task> {
        return withContext(Dispatchers.IO) {
            try {
                val task = taskDao.getTaskById(taskId)
                if (task != null) {
                    // TODO: Đánh dấu notification đã đọc
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
     * Lấy danh sách tất cả reminders
     * Note: Hiện tại Task entity chưa có field reminderTime, nên trả về tất cả tasks có deadline
     */
    suspend fun getAllReminders(): Result<List<Task>> {
        return withContext(Dispatchers.IO) {
            try {
                val allTasks = taskDao.getAllTasksSync()
                // Lọc các task có deadline > 0 (có deadline)
                val tasksWithDeadline = allTasks.filter { it.deadline > 0 }
                Result.success(tasksWithDeadline)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Kiểm tra xem reminder có được bật cho bài tập không
     * Note: Hiện tại Task entity chưa có field reminderTime
     */
    suspend fun isReminderEnabled(taskId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val task = taskDao.getTaskById(taskId)
                // Tạm thời trả về true nếu task có deadline
                task?.deadline ?: 0 > 0
            } catch (e: Exception) {
                false
            }
        }
    }
}
