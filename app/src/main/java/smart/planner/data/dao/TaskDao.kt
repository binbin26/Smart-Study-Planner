package smart.planner.data.dao

import androidx.room.*
import smart.planner.data.entity.Task

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM tasks WHERE subjectId = :subjectId")
    suspend fun getTasksBySubject(subjectId: String): List<Task>
}
