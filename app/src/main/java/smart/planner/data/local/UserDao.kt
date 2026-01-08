package smart.planner.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import smart.planner.data.model.User

@Dao
interface UserDao {

    // ==================== CREATE ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    // ==================== READ ====================

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?

    @Query("SELECT * FROM users")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdLiveData(userId: Int): LiveData<User?>

    // ==================== UPDATE ====================

    @Update
    suspend fun updateUser(user: User)

    // ==================== DELETE ====================

    @Delete
    suspend fun deleteUser(user: User)
}