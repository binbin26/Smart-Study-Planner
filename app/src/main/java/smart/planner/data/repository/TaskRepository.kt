package smart.planner.data.repository

import androidx.lifecycle.LiveData
import smart.planner.data.local.TaskDao
import smart.planner.data.model.Task // Phải import đúng đường dẫn này

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }
}