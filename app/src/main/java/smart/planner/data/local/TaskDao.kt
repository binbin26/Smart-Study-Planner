package smart.planner.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import smart.planner.data.model.Task

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    fun getAllTasks(): LiveData<List<Task>>

    @Delete
    suspend fun deleteTask(task: Task)
}