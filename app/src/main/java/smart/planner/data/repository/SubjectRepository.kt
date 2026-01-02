package smart.planner.data.repository

import kotlinx.coroutines.flow.Flow
import smart.planner.data.local.dao.SubjectDao
import smart.planner.data.local.entity.Subject
import smart.planner.data.sync.SubjectSyncService

class SubjectRepository(
    private val subjectDao: SubjectDao,
    private val syncService: SubjectSyncService
) {

    // ==================== READ ====================

    fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects()
    }

    suspend fun getSubjectById(id: String): Subject? {
        return subjectDao.getSubjectById(id)
    }

    suspend fun getSubjectByCode(code: String): Subject? {
        return subjectDao.getSubjectByCode(code)
    }

    // ==================== CREATE ====================

    suspend fun addSubject(subject: Subject): Result<Unit> {
        return try {
            subjectDao.insertSubject(subject)
            syncService.pushSubject(subject)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== UPDATE ====================

    suspend fun updateSubject(subject: Subject): Result<Unit> {
        return try {
            val updatedSubject = subject.copy(
                updatedAt = System.currentTimeMillis()
            )
            subjectDao.updateSubject(updatedSubject)
            syncService.pushSubject(updatedSubject)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== DELETE ====================

    suspend fun deleteSubject(subject: Subject): Result<Unit> {
        return try {
            subjectDao.deleteSubject(subject)
            syncService.deleteSubject(subject.id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSubjectById(id: String): Result<Unit> {
        return try {
            subjectDao.deleteSubjectById(id)
            syncService.deleteSubject(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== SYNC ====================

    suspend fun sync(): Result<Unit> {
        return syncService.initialSync()
    }
}