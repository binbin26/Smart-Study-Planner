package smart.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import smart.planner.data.model.User
import smart.planner.data.repository.UserRepository

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository = UserRepository(application)

    // ========== LOGIN & AUTH ==========

    suspend fun login(email: String, password: String): User? {
        return withContext(Dispatchers.IO) {
            val result = repository.login(email, password)
            result.getOrNull()
        }
    }

    suspend fun register(email: String, password: String, fullName: String): User? {
        return withContext(Dispatchers.IO) {
            val result = repository.register(email, password, fullName)
            result.getOrNull()
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return withContext(Dispatchers.IO) {
            val result = repository.getUserByEmail(email)
            result.getOrNull()
        }
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            repository.logout()
        }
    }

    // ========== SESSION ==========

    fun getCurrentUserId(): Int? {
        return repository.getCurrentUserId()
    }

    suspend fun getCurrentUserIdAsync(): Int? {
        return withContext(Dispatchers.IO) {
            repository.getCurrentUserId()
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return withContext(Dispatchers.IO) {
            repository.isLoggedIn()
        }
    }
}