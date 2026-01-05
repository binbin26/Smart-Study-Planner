package smart.planner.data.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import smart.planner.data.local.AppDatabase
import smart.planner.data.local.SubjectDao
import smart.planner.data.local.UserDao
import smart.planner.data.model.Subject

/**
 * Repository cho Subject Management với đầy đủ CRUD operations
 */
class SubjectRepository(private val context: Context) {
    
    private val subjectDao: SubjectDao = AppDatabase.getDatabase(context).subjectDao()
    private val userDao: UserDao = AppDatabase.getDatabase(context).userDao()
    
    // ==================== CREATE ====================
    
    /**
     * Thêm môn học mới
     */
    suspend fun addSubject(
        name: String,
        code: String? = null,
        description: String? = null,
        color: String? = null,
        userId: Int
    ): Result<Subject> {
        return withContext(Dispatchers.IO) {
            try {
                // Validation
                if (name.isBlank()) {
                    return@withContext Result.failure(Exception("Subject name cannot be empty"))
                }
                
                // Kiểm tra userId có tồn tại không
                val user = userDao.getUserById(userId)
                if (user == null) {
                    return@withContext Result.failure(Exception("User not found"))
                }
                
                val newSubject = Subject(
                    name = name.trim(),
                    code = code?.trim(),
                    description = description?.trim(),
                    color = color?.trim(),
                    userId = userId,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                
                val subjectId = subjectDao.insertSubject(newSubject)
                val createdSubject = subjectDao.getSubjectById(subjectId.toInt())
                
                if (createdSubject != null) {
                    Result.success(createdSubject)
                } else {
                    Result.failure(Exception("Failed to create subject"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    // ==================== READ ====================
    
    /**
     * Lấy tất cả môn học của user
     */
    suspend fun getAllSubjects(userId: Int): Result<List<Subject>> {
        return withContext(Dispatchers.IO) {
            try {
                val subjects = subjectDao.getSubjectsByUserId(userId)
                Result.success(subjects)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Lấy môn học theo ID
     */
    suspend fun getSubjectById(subjectId: Int): Result<Subject> {
        return withContext(Dispatchers.IO) {
            try {
                val subject = subjectDao.getSubjectById(subjectId)
                if (subject != null) {
                    Result.success(subject)
                } else {
                    Result.failure(Exception("Subject not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    // ==================== UPDATE ====================
    
    /**
     * Chỉnh sửa thông tin môn học
     */
    suspend fun updateSubject(
        subjectId: Int,
        name: String? = null,
        code: String? = null,
        description: String? = null,
        color: String? = null
    ): Result<Subject> {
        return withContext(Dispatchers.IO) {
            try {
                val subject = subjectDao.getSubjectById(subjectId)
                if (subject == null) {
                    return@withContext Result.failure(Exception("Subject not found"))
                }
                
                // Validation
                if (name != null && name.isBlank()) {
                    return@withContext Result.failure(Exception("Subject name cannot be empty"))
                }
                
                val updatedSubject = subject.copy(
                    name = name?.trim() ?: subject.name,
                    code = code?.trim() ?: subject.code,
                    description = description?.trim() ?: subject.description,
                    color = color?.trim() ?: subject.color,
                    updatedAt = System.currentTimeMillis()
                )
                
                subjectDao.updateSubject(updatedSubject)
                Result.success(updatedSubject)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    // ==================== DELETE ====================
    
    /**
     * Xóa môn học
     */
    suspend fun deleteSubject(subjectId: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val subject = subjectDao.getSubjectById(subjectId)
                if (subject == null) {
                    return@withContext Result.failure(Exception("Subject not found"))
                }
                
                subjectDao.deleteSubjectById(subjectId)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
