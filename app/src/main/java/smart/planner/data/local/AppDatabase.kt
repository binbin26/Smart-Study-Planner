package smart.planner.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import smart.planner.data.model.Subject
import smart.planner.data.model.Task
import smart.planner.data.model.User
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
@Database(
    entities = [User::class, Subject::class, Task::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                CREATE TABLE IF NOT EXISTS holiday_cache (
                    id TEXT PRIMARY KEY NOT NULL,
                    name TEXT NOT NULL,
                    date TEXT NOT NULL,
                    description TEXT NOT NULL,
                    updatedAt INTEGER NOT NULL
                )
            """.trimIndent())

                database.execSQL("""
                CREATE TABLE IF NOT EXISTS quote_cache (
                    id TEXT PRIMARY KEY NOT NULL,
                    text TEXT NOT NULL,
                    author TEXT NOT NULL,
                    updatedAt INTEGER NOT NULL
                )
            """.trimIndent())
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_planner_database"
                )
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Alias for getDatabase - used by some activities
        fun getInstance(context: Context): AppDatabase {
            return getDatabase(context)
        }
    }
}
