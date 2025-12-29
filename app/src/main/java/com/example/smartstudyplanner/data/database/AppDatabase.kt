package com.example.smartstudyplanner.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.smartstudyplanner.data.dao.SubjectDao
import com.example.smartstudyplanner.data.dao.TaskDao
import com.example.smartstudyplanner.data.entity.Subject
import com.example.smartstudyplanner.data.entity.Task

@Database(
    entities = [Subject::class, Task::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_study_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
