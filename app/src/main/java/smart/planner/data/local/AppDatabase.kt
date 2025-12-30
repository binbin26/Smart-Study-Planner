package smart.planner.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import smart.planner.data.local.converter.Converters
import smart.planner.data.local.dao.MotivationDao
import smart.planner.data.local.dao.SubjectDao
import smart.planner.data.local.dao.TaskDao
import smart.planner.data.local.entity.Motivation
import smart.planner.data.local.entity.Subject
import smart.planner.data.local.entity.Task

/**
 * Room Database cho Smart Study Planner
 *
 * Entities:
 * - Subject: Môn học
 * - Task: Bài tập/công việc
 * - Motivation: Câu trích dẫn động lực
 *
 * Version: 1 (Initial version)
 *
 * Architecture:
 * - Singleton pattern để đảm bảo chỉ có 1 instance
 * - TypeConverters cho TaskStatus enum
 * - Foreign Keys: Task -> Subject, Task -> Motivation
 */
@Database(
    entities = [
        Subject::class,
        Task::class,
        Motivation::class
    ],
    version = 1,
    exportSchema = false // Set true nếu muốn export schema cho migration
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // ==================== DAO ABSTRACT METHODS ====================

    /**
     * DAO cho Subject operations
     */
    abstract fun subjectDao(): SubjectDao

    /**
     * DAO cho Task operations
     */
    abstract fun taskDao(): TaskDao

    /**
     * DAO cho Motivation operations
     */
    abstract fun motivationDao(): MotivationDao

    // ==================== SINGLETON PATTERN ====================

    companion object {
        /**
         * Volatile: Đảm bảo INSTANCE luôn được đọc từ main memory
         * Tránh thread caching
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Tên database file
         */
        private const val DATABASE_NAME = "smart_study_planner_database"

        /**
         * Get database instance (singleton)
         *
         * Thread-safe với synchronized block
         * Double-check locking pattern
         *
         * @param context Application context (không dùng Activity context!)
         * @return AppDatabase instance
         */
        fun getDatabase(context: Context): AppDatabase {
            // First check (không lock) - nhanh nếu đã có instance
            return INSTANCE ?: synchronized(this) {
                // Second check (có lock) - đảm bảo thread-safe
                val instance = INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }
                instance
            }
        }

        /**
         * Build database với configuration
         */
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext, // Dùng applicationContext, không phải activity context
                AppDatabase::class.java,
                DATABASE_NAME
            )
                // ===== MIGRATION STRATEGY =====
                // fallbackToDestructiveMigration: Xóa database cũ nếu version thay đổi
                // WARNING: Chỉ dùng trong development! Production cần migration proper
                .fallbackToDestructiveMigration()

                // ===== CALLBACK (Optional) =====
                // Thêm callback nếu cần pre-populate data
                // .addCallback(DatabaseCallback())

                // ===== BUILD =====
                .build()
        }

        /**
         * Destroy instance (dùng cho testing)
         * Không nên dùng trong production
         */
        @Synchronized
        fun destroyInstance() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }

    // ==================== DATABASE CALLBACK (Optional) ====================

    /**
     * Callback để pre-populate database với initial data
     * Uncomment nếu muốn dùng
     */
    /*
    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Chạy khi database được tạo lần đầu
            // Có thể insert initial data ở đây

            // Ví dụ: Insert motivations mặc định
            CoroutineScope(Dispatchers.IO).launch {
                val database = INSTANCE
                database?.let {
                    val motivationDao = it.motivationDao()

                    // Insert default motivations
                    val defaultMotivations = listOf(
                        Motivation(
                            quote = "Thành công là tổng của những nỗ lực nhỏ bé lặp đi lặp lại mỗi ngày",
                            author = "Robert Collier",
                            language = "vi"
                        ),
                        Motivation(
                            quote = "Học tập là kho báu sẽ theo chủ nhân suốt đời",
                            author = "Ngạn ngữ Trung Quốc",
                            language = "vi"
                        )
                    )

                    motivationDao.insertMotivations(defaultMotivations)
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            // Chạy mỗi khi database được mở
        }
    }
    */
}