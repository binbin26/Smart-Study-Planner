package smart.planner.data.dao

import androidx.room.*
import smart.planner.data.entity.Subject

@Dao
interface SubjectDao {

    @Insert
    suspend fun insert(subject: Subject)

    @Query("SELECT * FROM Subject")
    suspend fun getAll(): List<Subject>
}
