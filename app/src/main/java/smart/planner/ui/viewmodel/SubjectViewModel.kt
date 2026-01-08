package smart.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import smart.planner.data.local.AppDatabase
import smart.planner.data.model.Subject
import smart.planner.data.repository.SubjectRepository

class SubjectViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SubjectRepository = SubjectRepository(application)
    private val subjectDao = AppDatabase.getDatabase(application).subjectDao()

    fun getSubjectsByUserId(userId: Int): LiveData<List<Subject>> {
        return subjectDao.getSubjectsByUserIdLiveData(userId)
    }

    suspend fun getAllSubjects(userId: Int): List<Subject> {
        return repository.getAllSubjects(userId).getOrElse { emptyList() }
    }

    /**
     * Thêm môn học mới
     */
    fun addSubject(
        name: String,
        code: String? = null,
        teacher: String? = null,
        color: String? = null,
        userId: Int,
        onResult: (Result<Subject>) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.addSubject(name, code, teacher, color, userId)
            onResult(result)
        }
    }

    /**
     * Cập nhật môn học
     */
    fun updateSubject(
        subjectId: Int,
        name: String? = null,
        code: String? = null,
        teacher: String? = null,
        color: String? = null,
        onResult: (Result<Subject>) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.updateSubject(subjectId, name, code, teacher, color)
            onResult(result)
        }
    }

    /**
     * Xóa môn học
     */
    fun deleteSubject(subjectId: Int, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteSubject(subjectId)
            onResult(result)
        }
    }
}

