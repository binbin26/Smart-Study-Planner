package smart.planner.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import smart.planner.data.model.Task

@Dao
interface TaskDao {
    // ==================== CREATE ====================
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    // ==================== READ ====================
    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    suspend fun getAllTasksSync(): List<Task>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): Task?

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskByIdLiveData(taskId: Int): LiveData<Task?>

    @Query("SELECT * FROM tasks WHERE subject = :subject ORDER BY deadline ASC")
    suspend fun getTasksBySubject(subject: String): List<Task>

    @Query("SELECT * FROM tasks WHERE subject = :subject ORDER BY deadline ASC")
    fun getTasksBySubjectLiveData(subject: String): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE deadline >= :startTime AND deadline <= :endTime ORDER BY deadline ASC")
    suspend fun getTasksByTimeRange(startTime: Long, endTime: Long): List<Task>

    @Query("SELECT * FROM tasks WHERE deadline < :currentTime ORDER BY deadline ASC")
    suspend fun getOverdueTasks(currentTime: Long): List<Task>

    @Query("SELECT * FROM tasks WHERE deadline >= :currentTime AND deadline <= :futureTime ORDER BY deadline ASC")
    suspend fun getUpcomingTasks(currentTime: Long, futureTime: Long): List<Task>

    // ==================== UPDATE ====================
    @Update
    suspend fun updateTask(task: Task)

    @Query("UPDATE tasks SET name = :name, description = :description WHERE id = :taskId")
    suspend fun updateTaskDetails(taskId: Int, name: String, description: String)

    @Query("UPDATE tasks SET deadline = :deadline WHERE id = :taskId")
    suspend fun updateTaskDeadline(taskId: Int, deadline: Long)

    // ==================== DELETE ====================
    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)

    @Query("DELETE FROM tasks WHERE subject = :subject")
    suspend fun deleteTasksBySubject(subject: String)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}