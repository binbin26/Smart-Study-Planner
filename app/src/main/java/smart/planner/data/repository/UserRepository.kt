package smart.planner.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import smart.planner.data.local.AppDatabase
import smart.planner.data.local.UserDao
import smart.planner.data.model.User

/**
 * Repository cho User Management với đầy đủ CRUD operations
 */
class UserRepository(private val context: Context) {
    
    private val userDao: UserDao = AppDatabase.getDatabase(context).userDao()
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    
    private companion object {
        const val KEY_USER_ID = "current_user_id"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    // ==================== CREATE ====================
    
    /**
     * Đăng ký tài khoản mới
     */
    suspend fun register(email: String, password: String, fullName: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                // Validation
                if (email.isBlank()) {
                    return@withContext Result.failure(Exception("Email cannot be empty"))
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    return@withContext Result.failure(Exception("Invalid email format"))
                }
                if (password.isBlank() || password.length < 6) {
                    return@withContext Result.failure(Exception("Password must be at least 6 characters"))
                }
                if (fullName.isBlank()) {
                    return@withContext Result.failure(Exception("Full name cannot be empty"))
                }
                
                // Kiểm tra email đã tồn tại chưa
                val existingUser = userDao.getUserByEmail(email.trim().lowercase())
                if (existingUser != null) {
                    return@withContext Result.failure(Exception("Email already exists"))
                }
                
                // Tạo user mới
                val newUser = User(
                    email = email.trim().lowercase(),
                    password = password, // Nên hash password trong thực tế
                    fullName = fullName.trim(),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                
                val userId = userDao.insertUser(newUser)
                val createdUser = userDao.getUserById(userId.toInt())
                
                if (createdUser != null) {
                    Result.success(createdUser)
                } else {
                    Result.failure(Exception("Failed to create user"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    // ==================== READ ====================
    
    /**
     * Đăng nhập
     */
    suspend fun login(email: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                // Validation
                if (email.isBlank()) {
                    return@withContext Result.failure(Exception("Email cannot be empty"))
                }
                if (password.isBlank()) {
                    return@withContext Result.failure(Exception("Password cannot be empty"))
                }
                
                val user = userDao.getUserByEmailAndPassword(email.trim().lowercase(), password)
                if (user != null) {
                    // Lưu thông tin đăng nhập
                    saveCurrentUserId(user.id)
                    Result.success(user)
                } else {
                    Result.failure(Exception("Invalid email or password"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Lấy thông tin user hiện tại
     */
    suspend fun getCurrentUser(userId: Int): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val user = userDao.getUserById(userId)
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("User not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Lấy user theo email
     */
    suspend fun getUserByEmail(email: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val user = userDao.getUserByEmail(email.trim().lowercase())
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("User not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    // ==================== UPDATE ====================
    
    /**
     * Cập nhật thông tin cá nhân
     */
    suspend fun updateProfile(
        userId: Int,
        fullName: String? = null,
        email: String? = null,
        phone: String? = null,
        avatarUrl: String? = null
    ): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val user = userDao.getUserById(userId)
                if (user == null) {
                    return@withContext Result.failure(Exception("User not found"))
                }
                
                // Validation
                if (email != null) {
                    if (email.isBlank()) {
                        return@withContext Result.failure(Exception("Email cannot be empty"))
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        return@withContext Result.failure(Exception("Invalid email format"))
                    }
                    // Kiểm tra email trùng (trừ chính user hiện tại)
                    val existingUser = userDao.getUserByEmail(email.trim().lowercase())
                    if (existingUser != null && existingUser.id != userId) {
                        return@withContext Result.failure(Exception("Email already exists"))
                    }
                }
                
                if (fullName != null && fullName.isBlank()) {
                    return@withContext Result.failure(Exception("Full name cannot be empty"))
                }
                
                val updatedUser = user.copy(
                    fullName = fullName?.trim() ?: user.fullName,
                    email = email?.trim()?.lowercase() ?: user.email,
                    phone = phone?.trim() ?: user.phone,
                    avatarUrl = avatarUrl?.trim() ?: user.avatarUrl,
                    updatedAt = System.currentTimeMillis()
                )
                
                userDao.updateUser(updatedUser)
                Result.success(updatedUser)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    // ==================== DELETE ====================
    
    /**
     * Xóa tài khoản
     */
    suspend fun deleteUser(userId: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val user = userDao.getUserById(userId)
                if (user == null) {
                    return@withContext Result.failure(Exception("User not found"))
                }
                
                userDao.deleteUser(user)
                clearCurrentUser()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Đăng xuất
     */
    suspend fun logout(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                clearCurrentUser()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Kiểm tra trạng thái đăng nhập
     */
    suspend fun isLoggedIn(): Boolean {
        return withContext(Dispatchers.IO) {
            prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        }
    }
    
    /**
     * Lấy ID của user hiện tại
     */
    fun getCurrentUserId(): Int? {
        val userId = prefs.getInt(KEY_USER_ID, -1)
        return if (userId != -1) userId else null
    }
    
    private fun saveCurrentUserId(userId: Int) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, userId)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    private fun clearCurrentUser() {
        prefs.edit().apply {
            remove(KEY_USER_ID)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }
}
