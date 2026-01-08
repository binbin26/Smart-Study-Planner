package smart.planner.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import smart.planner.data.local.entity.Task
import smart.planner.data.local.entity.TaskStatus

@Dao
interface TaskDao {

    // ==================== READ OPERATIONS ====================

    /**
     * Lấy tất cả tasks, sắp xếp theo deadline tăng dần
     */
    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    fun getAllTasks(): Flow<List<Task>>

    /**
     * Lấy task theo ID
     */
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): Task?

    /**
     * Lấy tất cả tasks của một môn học
     * Sắp xếp theo deadline tăng dần
     */
    @Query("SELECT * FROM tasks WHERE subjectId = :subjectId ORDER BY deadline ASC")
    suspend fun getTasksBySubject(subjectId: String): List<Task>

    /**
     * Lấy tasks theo status (TODO, IN_PROGRESS, DONE, OVERDUE)
     */
    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY deadline ASC")
    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>>

    /**
     * Lấy tất cả tasks chưa hoàn thành (không phải DONE)
     */
    @Query("SELECT * FROM tasks WHERE status != 'DONE' ORDER BY deadline ASC")
    fun getUncompletedTasks(): Flow<List<Task>>

    /**
     * Lấy tasks trong khoảng thời gian
     * Params: startDate và endDate là timestamp (milliseconds)
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE deadline BETWEEN :startDate AND :endDate 
        ORDER BY deadline ASC
    """)
    fun getTasksByDateRange(startDate: Long, endDate: Long): Flow<List<Task>>
    @Query("SELECT * FROM tasks WHERE subjectId = :subjectId ORDER BY deadline ASC")
    fun getTasksBySubjectLiveData(subjectId: String): LiveData<List<Task>>
    @Query("UPDATE tasks SET title = :title, description = :description WHERE id = :taskId")
    suspend fun updateTaskDetails(taskId: Int, title: String, description: String)

    /**
     * Lấy tasks quá hạn (chưa DONE và deadline < hiện tại)
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE status != 'DONE' 
        AND deadline < :currentTime
        ORDER BY deadline ASC
    """)
    fun getOverdueTasks(currentTime: Long = System.currentTimeMillis()): Flow<List<Task>>

    /**
     * Lấy tasks sắp đến hạn (trong X ngày tới)
     */
    @Query("""
        SELECT * FROM tasks 
        WHERE status != 'DONE'
        AND deadline BETWEEN :currentTime AND :futureTime
        ORDER BY deadline ASC
    """)
    fun getUpcomingTasks(
        currentTime: Long = System.currentTimeMillis(),
        futureTime: Long
    ): Flow<List<Task>>

    /**
     * Đếm số tasks theo status
     */
    @Query("SELECT COUNT(*) FROM tasks WHERE status = :status")
    suspend fun countTasksByStatus(status: TaskStatus): Int

    /**
     * Đếm số tasks chưa hoàn thành của một môn học
     */
    @Query("""
        SELECT COUNT(*) FROM tasks 
        WHERE subjectId = :subjectId 
        AND status != 'DONE'
    """)
    suspend fun countUncompletedTasksBySubject(subjectId: String): Int

    // ==================== CREATE OPERATIONS ====================

    /**
     * Thêm một task mới
     * OnConflict: REPLACE - nếu ID trùng thì ghi đè
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    /**
     * Thêm nhiều tasks cùng lúc (bulk insert)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    // ==================== UPDATE OPERATIONS ====================

    /**
     * Cập nhật toàn bộ task
     */
    @Update
    suspend fun updateTask(task: Task)

    /**
     * Cập nhật status của task
     */
    @Query("""
        UPDATE tasks 
        SET status = :status, updatedAt = :timestamp 
        WHERE id = :taskId
    """)
    suspend fun updateTaskStatus(
        taskId: String,
        status: TaskStatus,
        timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Gán motivation cho task
     */
    @Query("UPDATE tasks SET motivationId = :motivationId WHERE id = :taskId")
    suspend fun updateTaskMotivation(taskId: String, motivationId: String?)

    /**
     * Cập nhật deadline của task
     */
    @Query("""
        UPDATE tasks 
        SET deadline = :newDeadline, updatedAt = :timestamp 
        WHERE id = :taskId
    """)
    suspend fun updateTaskDeadline(
        taskId: String,
        newDeadline: Long,
        timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Đánh dấu tasks quá hạn tự động
     * Chuyển status sang OVERDUE nếu deadline < hiện tại và status != DONE
     */
    @Query("""
        UPDATE tasks 
        SET status = 'OVERDUE', updatedAt = :timestamp
        WHERE status != 'DONE' 
        AND deadline < :currentTime
    """)
    suspend fun markOverdueTasks(
        currentTime: Long = System.currentTimeMillis(),
        timestamp: Long = System.currentTimeMillis()
    )

    // ==================== DELETE OPERATIONS ====================

    /**
     * Xóa một task
     */
    @Delete
    suspend fun deleteTask(task: Task)

    /**
     * Xóa task theo ID
     */
    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)

    /**
     * Xóa tất cả tasks của một môn học
     * Note: ForeignKey CASCADE sẽ tự động xóa, nhưng có thể dùng manual
     */
    @Query("DELETE FROM tasks WHERE subjectId = :subjectId")
    suspend fun deleteTasksBySubject(subjectId: String)

    /**
     * Xóa tất cả tasks đã hoàn thành (DONE)
     */
    @Query("DELETE FROM tasks WHERE status = 'DONE'")
    suspend fun deleteCompletedTasks()

    /**
     * Xóa toàn bộ tasks (dùng cho testing hoặc reset)
     */
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}