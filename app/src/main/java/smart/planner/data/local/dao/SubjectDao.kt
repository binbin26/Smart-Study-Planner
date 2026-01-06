package smart.planner.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import smart.planner.data.local.entity.Subject

@Dao
interface SubjectDao {

    // ===== READ =====
    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: String): Subject?

    @Query("SELECT * FROM subjects WHERE code = :code")
    suspend fun getSubjectByCode(code: String): Subject?

    // ===== CREATE =====
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<Subject>)

    // ===== UPDATE =====
    @Update
    suspend fun updateSubject(subject: Subject)

    @Query("UPDATE subjects SET updatedAt = :timestamp WHERE id = :id")
    suspend fun updateTimestamp(id: String, timestamp: Long)

    // ===== DELETE =====
    @Delete
    suspend fun deleteSubject(subject: Subject)

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteSubjectById(id: String)

    @Query("DELETE FROM subjects")
    suspend fun deleteAllSubjects()
}