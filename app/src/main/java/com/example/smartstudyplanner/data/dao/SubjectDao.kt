package com.example.smartstudyplanner.data.dao

import androidx.room.*
import com.example.smartstudyplanner.data.entity.Subject

@Dao
interface SubjectDao {

    @Insert
    suspend fun insert(subject: Subject)

    @Query("SELECT * FROM Subject")
    suspend fun getAll(): List<Subject>
}
