package smart.planner.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import smart.planner.data.dao.SubjectDao
import smart.planner.data.dao.TaskDao
import smart.planner.data.entity.Subject
import smart.planner.data.entity.Task

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
