package smart.planner.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
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
}

