package smart.planner.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import smart.planner.data.dao.SubjectDao
import smart.planner.data.dao.TaskDao
import smart.planner.data.dao.QuoteCacheDao
import smart.planner.data.entity.Subject
import smart.planner.data.entity.Task
import smart.planner.data.entity.QuoteCache

@Database(
    entities = [
        Subject::class,
        Task::class,
        QuoteCache::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao
    abstract fun quoteCacheDao(): QuoteCacheDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_study_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
