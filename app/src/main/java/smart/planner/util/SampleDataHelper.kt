package smart.planner.util

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import smart.planner.data.local.AppDatabase
import smart.planner.data.model.Subject
import smart.planner.data.model.Task
import smart.planner.data.firebase.TaskFirebaseModel
import java.util.concurrent.TimeUnit

class SampleDataHelper(private val context: Context) {

    private val firebaseDatabase = FirebaseDatabase
        .getInstance("https://ltmobile-c9240-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val subjectsRef = firebaseDatabase.getReference("subjects")
    private val tasksRef = firebaseDatabase.getReference("tasks")

    suspend fun insertSampleData(database: AppDatabase) = withContext(Dispatchers.IO) {
        try {
            val now = System.currentTimeMillis()
            val oneDayMs = TimeUnit.DAYS.toMillis(1)

            // Clear existing data (optional - comment out if you want to keep existing data)
            // database.subjectDao().deleteAll()
            // database.taskDao().deleteAll()

            // Insert Subjects
            val subjects = listOf(
                Subject(0, "Lập Trình Mobile", "CS301", "TS. Nguyễn Văn A", "#2196F3", 1, now, now),
                Subject(0, "Cơ Sở Dữ Liệu", "CS302", "PGS. Trần Thị B", "#4CAF50", 1, now, now),
                Subject(0, "Mạng Máy Tính", "CS303", "ThS. Lê Văn C", "#FF9800", 1, now, now),
                Subject(0, "Trí Tuệ Nhân Tạo", "CS304", "GS. Phạm Thị D", "#9C27B0", 1, now, now),
                Subject(0, "An Toàn Thông Tin", "CS305", "TS. Hoàng Văn E", "#F44336", 1, now, now)
            )

            val insertedSubjectIds = mutableListOf<Long>()
            val insertedSubjects = mutableListOf<Subject>()
            subjects.forEach {
                val id = database.subjectDao().insertSubject(it)
                insertedSubjectIds.add(id)
                // Get the inserted subject with actual ID
                database.subjectDao().getSubjectById(id.toInt())?.let { subject ->
                    insertedSubjects.add(subject)
                }
            }

            // Sync subjects to Firebase
            insertedSubjects.forEach { subject ->
                val subjectMap = mapOf(
                    "code" to subject.code,
                    "name" to subject.name,
                    "teacher" to subject.teacher
                )
                subjectsRef.child(subject.id.toString()).setValue(subjectMap).await()
            }

            // Use actual inserted IDs for tasks
            val subjectIds = insertedSubjectIds.map { it.toString() }

            // Insert Tasks - distributed over 7 days
            val tasks = listOf(
                // Today (5 tasks)
                Task(0, "", "Hoàn thiện UI Dashboard", "Thiết kế và implement giao diện dashboard", now, now + oneDayMs, "DONE", subjectIds.getOrElse(0) { "1" }, now),
                Task(0, "", "Thiết kế ERD Database", "Vẽ sơ đồ Entity-Relationship", now, now + 2*oneDayMs, "IN_PROGRESS", subjectIds.getOrElse(1) { "2" }, now),
                Task(0, "", "Cấu hình Firewall", "Thiết lập quy tắc firewall", now, now + 3*oneDayMs, "TODO", subjectIds.getOrElse(2) { "3" }, now),
                Task(0, "", "Nghiên cứu Neural Networks", "Đọc tài liệu về mạng neural", now, now + oneDayMs, "DONE", subjectIds.getOrElse(3) { "4" }, now),
                Task(0, "", "Phân tích lỗ hổng bảo mật", "Kiểm tra và báo cáo lỗ hổng", now, now + 5*oneDayMs, "TODO", subjectIds.getOrElse(4) { "5" }, now),

                // 1 day ago (4 tasks)
                Task(0, "", "Code Firebase Integration", "Tích hợp Firebase vào app", now - oneDayMs, now + 2*oneDayMs, "DONE", subjectIds.getOrElse(0) { "1" }, now - oneDayMs),
                Task(0, "", "Viết SQL Queries", "Thực hành SELECT, JOIN", now - oneDayMs, now + 3*oneDayMs, "DONE", subjectIds.getOrElse(1) { "2" }, now - oneDayMs),
                Task(0, "", "Lab Wireshark", "Phân tích gói tin mạng", now - oneDayMs, now + 4*oneDayMs, "IN_PROGRESS", subjectIds.getOrElse(2) { "3" }, now - oneDayMs),
                Task(0, "", "Implement Decision Tree", "Lập trình cây quyết định", now - oneDayMs, now + 5*oneDayMs, "TODO", subjectIds.getOrElse(3) { "4" }, now - oneDayMs),

                // 2 days ago (3 tasks)
                Task(0, "", "Test Navigation Components", "Kiểm tra navigation", now - 2*oneDayMs, now + 3*oneDayMs, "DONE", subjectIds.getOrElse(0) { "1" }, now - 2*oneDayMs),
                Task(0, "", "Database Normalization", "Chuẩn hóa lên 3NF", now - 2*oneDayMs, now + 5*oneDayMs, "DONE", subjectIds.getOrElse(1) { "2" }, now - 2*oneDayMs),
                Task(0, "", "Mã hóa RSA", "Implement thuật toán RSA", now - 2*oneDayMs, now + 6*oneDayMs, "TODO", subjectIds.getOrElse(4) { "5" }, now - 2*oneDayMs),

                // 3 days ago (4 tasks)
                Task(0, "", "Design Pattern Study", "MVVM, Repository Pattern", now - 3*oneDayMs, now + 2*oneDayMs, "DONE", subjectIds.getOrElse(0) { "1" }, now - 3*oneDayMs),
                Task(0, "", "Setup Network Topology", "Thiết kế LAN/WAN", now - 3*oneDayMs, now + 5*oneDayMs, "IN_PROGRESS", subjectIds.getOrElse(2) { "3" }, now - 3*oneDayMs),
                Task(0, "", "Machine Learning Review", "Ôn tập thuật toán ML", now - 3*oneDayMs, now + 6*oneDayMs, "DONE", subjectIds.getOrElse(3) { "4" }, now - 3*oneDayMs),
                Task(0, "", "Security Audit Report", "Báo cáo kiểm tra bảo mật", now - 3*oneDayMs, now + 7*oneDayMs, "TODO", subjectIds.getOrElse(4) { "5" }, now - 3*oneDayMs),

                // 4 days ago (3 tasks)
                Task(0, "", "Optimize Database Queries", "Tối ưu performance", now - 4*oneDayMs, now + 5*oneDayMs, "DONE", subjectIds.getOrElse(1) { "2" }, now - 4*oneDayMs),
                Task(0, "", "Config Router Cisco", "Cấu hình router cơ bản", now - 4*oneDayMs, now + 6*oneDayMs, "DONE", subjectIds.getOrElse(2) { "3" }, now - 4*oneDayMs),
                Task(0, "", "Deep Learning Paper", "Đọc paper CNN và RNN", now - 4*oneDayMs, now + 7*oneDayMs, "TODO", subjectIds.getOrElse(3) { "4" }, now - 4*oneDayMs),

                // 5 days ago (3 tasks)
                Task(0, "", "Build RecyclerView Adapter", "Tạo adapter cho tasks", now - 5*oneDayMs, now + 3*oneDayMs, "DONE", subjectIds.getOrElse(0) { "1" }, now - 5*oneDayMs),
                Task(0, "", "Transaction Management", "ACID và transaction", now - 5*oneDayMs, now + 6*oneDayMs, "DONE", subjectIds.getOrElse(1) { "2" }, now - 5*oneDayMs),
                Task(0, "", "Penetration Testing", "Kiểm tra xâm nhập", now - 5*oneDayMs, now + 8*oneDayMs, "IN_PROGRESS", subjectIds.getOrElse(4) { "5" }, now - 5*oneDayMs),

                // 6 days ago (3 tasks)
                Task(0, "", "Study Kotlin Coroutines", "Async programming", now - 6*oneDayMs, now + 4*oneDayMs, "DONE", subjectIds.getOrElse(0) { "1" }, now - 6*oneDayMs),
                Task(0, "", "OSI Model Review", "Ôn tập 7 tầng OSI", now - 6*oneDayMs, now + 7*oneDayMs, "DONE", subjectIds.getOrElse(2) { "3" }, now - 6*oneDayMs),
                Task(0, "", "Reinforcement Learning", "Q-Learning algorithm", now - 6*oneDayMs, now + 9*oneDayMs, "TODO", subjectIds.getOrElse(3) { "4" }, now - 6*oneDayMs)
            )

            // Insert tasks to SQLite and sync to Firebase
            var successCount = 0
            tasks.forEach { task ->
                val taskId = database.taskDao().insertTask(task)

                // Get the inserted task with actual ID
                database.taskDao().getTaskById(taskId.toInt())?.let { insertedTask ->
                    // Sync to Firebase
                    val firebaseTask = TaskFirebaseModel(
                        id = insertedTask.id.toString(),
                        title = insertedTask.title,
                        description = insertedTask.description,
                        createdAt = insertedTask.createdAt,
                        deadline = insertedTask.deadline,
                        status = insertedTask.status,
                        subjectId = insertedTask.subjectId,
                        updatedAt = insertedTask.updatedAt
                    )

                    // Use push() to generate unique Firebase key
                    val firebaseKey = tasksRef.push().key ?: return@let
                    tasksRef.child(firebaseKey).setValue(firebaseTask).await()
                    successCount++
                }
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "✅ Đã thêm ${insertedSubjects.size} môn học và $successCount tasks (SQLite + Firebase)", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "❌ Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
