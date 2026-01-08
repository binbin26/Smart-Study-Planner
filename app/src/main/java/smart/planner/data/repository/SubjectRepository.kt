package smart.planner.data.repository

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import smart.planner.data.local.AppDatabase
import smart.planner.data.local.SubjectDao
import smart.planner.data.local.UserDao
import smart.planner.data.model.Subject
import smart.planner.data.sync.SyncManager

/**
 * Repository cho Subject Management với đầy đủ CRUD operations
 */
class SubjectRepository(private val context: Context) {

    private val subjectDao: SubjectDao = AppDatabase.getDatabase(context).subjectDao()
    private val userDao: UserDao = AppDatabase.getDatabase(context).userDao()
    private val firebaseDatabase = FirebaseDatabase.getInstance().getReference("subjects")
    private val syncManager = SyncManager(context)
    
    // ==================== CREATE ====================
    
    /**
     * Thêm môn học mới
     */
    suspend fun addSubject(
        name: String,
        code: String? = null,
        teacher: String? = null,
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
                    teacher = teacher?.trim(),
                    color = color?.trim(),
                    userId = userId,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                val subjectId = subjectDao.insertSubject(newSubject)
                val createdSubject = subjectDao.getSubjectById(subjectId.toInt())

                if (createdSubject != null) {
                    android.util.Log.d("SubjectRepository", "📝 Created subject in local DB: id=${createdSubject.id}, name=${createdSubject.name}")
                    // Sync to Firebase
                    syncSubjectToFirebase(createdSubject)
                    Result.success(createdSubject)
                } else {
                    android.util.Log.e("SubjectRepository", "❌ Failed to retrieve created subject from DB")
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
        teacher: String? = null,
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
                    teacher = teacher?.trim() ?: subject.teacher,
                    color = color?.trim() ?: subject.color,
                    updatedAt = System.currentTimeMillis()
                )

                subjectDao.updateSubject(updatedSubject)

                // Sync to Firebase
                syncSubjectToFirebase(updatedSubject)

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

                // Delete from local first
                subjectDao.deleteSubjectById(subjectId)

                // Delete from Firebase
                try {
                    firebaseDatabase.child(subject.id.toString()).removeValue().await()
                    android.util.Log.d("SubjectRepository", "✅ Deleted subject from Firebase: ${subject.id}")
                } catch (e: Exception) {
                    android.util.Log.e("SubjectRepository", "❌ Failed to delete from Firebase: ${e.message}")
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ==================== FIREBASE SYNC ====================

    /**
     * Sync subject to Firebase
     */
    private suspend fun syncSubjectToFirebase(subject: Subject) {
        try {
            android.util.Log.d("SubjectRepository", "🔄 Starting Firebase sync for subject: id=${subject.id}, name=${subject.name}")

            val subjectMap = mapOf(
                "code" to subject.code,
                "name" to subject.name,
                "teacher" to subject.teacher
            )

            android.util.Log.d("SubjectRepository", "📤 Sending to Firebase path: subjects/${subject.id}")
            android.util.Log.d("SubjectRepository", "📦 Data: $subjectMap")

            // Sử dụng subject.id (Int) làm key trong Firebase
            firebaseDatabase.child(subject.id.toString()).setValue(subjectMap).await()

            android.util.Log.d("SubjectRepository", "✅ Successfully synced subject to Firebase: ${subject.name}")
        } catch (e: Exception) {
            android.util.Log.e("SubjectRepository", "❌ Failed to sync subject: ${e.message}", e)
            android.util.Log.e("SubjectRepository", "❌ Exception type: ${e.javaClass.simpleName}")
        }
    }
}
