package com.example.smartstudyplanner.data.dao

import androidx.room.*
import com.example.smartstudyplanner.data.entity.Task

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(task: Task)

    @Query("SELECT * FROM Task WHERE subjectId = :subjectId")
    suspend fun getTasksBySubject(subjectId: Int): List<Task>
}
