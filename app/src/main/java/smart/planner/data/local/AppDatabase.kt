package smart.planner.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import smart.planner.data.model.Task

@Database(entities = [Task::class], version = 2, exportSchema = false) // Tăng version lên 2
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_planner_database"
                )
                    .fallbackToDestructiveMigration() // Tự xóa data cũ để tạo schema mới không gây crash
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}