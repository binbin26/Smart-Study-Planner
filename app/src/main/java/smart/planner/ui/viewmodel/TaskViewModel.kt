package smart.planner.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import smart.planner.data.firebase.TaskFirebaseModel
import smart.planner.data.firebase.TaskFirebaseService
import smart.planner.data.local.AppDatabase
import smart.planner.data.model.Task
import smart.planner.data.repository.TaskRepository

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskDao = AppDatabase.getDatabase(application).taskDao()
    private val repository: TaskRepository = TaskRepository(taskDao)

    val allTasks: LiveData<List<Task>> = repository.allTasks

    fun addTask(title: String, description: String, deadline: Long, subjectId: String) {
        val currentTime = System.currentTimeMillis()

        val firebaseTask = TaskFirebaseModel(
            title = title,
            description = description,
            createdAt = currentTime,
            deadline = deadline,
            status = "TODO",
            subjectId = subjectId,
            updatedAt = currentTime
        )

        TaskFirebaseService.addNewTask(firebaseTask) { firebaseKey ->
            if (firebaseKey != null) {
                // ✅ ADD THIS LOG
                Log.d("FIREBASE_TEST", "🔥 Task Saved Successfully to Firebase! ID: $firebaseKey")

                val localTask = Task(
                    firebaseId = firebaseKey,
                    title = title,
                    description = description,
                    createdAt = currentTime,
                    deadline = deadline,
                    status = "TODO",
                    subjectId = subjectId,
                    updatedAt = currentTime
                )

                viewModelScope.launch {
                    repository.insert(localTask)
                }
            } else {
                 Log.e("FIREBASE_TEST", "❌ Failed to save task to Firebase")
            }
        }
    }

    fun deleteTask(task: Task) {
        if (task.firebaseId.isNotEmpty()) {
            TaskFirebaseService.deleteTask(task.firebaseId) { success ->
                if (success) {
                    viewModelScope.launch {
                        repository.delete(task)
                    }
                }
            }
        }
    }
    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.update(task)
        }
    }
}
