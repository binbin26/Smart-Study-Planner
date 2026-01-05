package smart.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import smart.planner.data.repository.UserRepository

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository = UserRepository(application)

    fun getCurrentUserId(): Int? {
        return repository.getCurrentUserId()
    }

    suspend fun getCurrentUserIdAsync(): Int? {
        return withContext(Dispatchers.IO) {
            repository.getCurrentUserId()
        }
    }
}

