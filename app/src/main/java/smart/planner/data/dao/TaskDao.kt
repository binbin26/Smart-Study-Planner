package smart.planner.data.dao

import androidx.room.*
import smart.planner.data.entity.Task

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(task: Task)
    @Delete
    suspend fun delete(task: Task)
    @Query("SELECT * FROM Task WHERE subjectId = :subjectId")
    suspend fun getTasksBySubject(subjectId: Int): List<Task>
}
