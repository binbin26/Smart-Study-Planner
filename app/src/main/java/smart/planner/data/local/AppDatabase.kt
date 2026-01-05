package smart.planner.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import smart.planner.data.local.converter.Converters
import smart.planner.data.local.dao.*
import smart.planner.data.local.entity.*
import smart.planner.data.model.Subject
import smart.planner.data.model.Task
import smart.planner.data.model.User

@Database(
    entities = [
        User::class,
        Subject::class,
        Task::class,
        Motivation::class,
        HolidayCache::class,
        QuoteCache::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao
    abstract fun motivationDao(): MotivationDao
    abstract fun holidayCacheDao(): HolidayCacheDao
    abstract fun quoteCacheDao(): QuoteCacheDao

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
                    "smart_planner_db"
                )
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}