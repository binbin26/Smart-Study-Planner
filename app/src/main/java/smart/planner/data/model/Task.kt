package smart.planner.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var firebaseId: String = "", // Đã đổi thành var để có thể cập nhật sau khi lưu vào Firebase
    val title: String,
    val description: String,
    val createdAt: Long,
    val deadline: Long,
    val status: String = "TODO", // Đã thay isDone bằng status
    val subjectId: String,       // Đã thay subjectName bằng subjectId
    val updatedAt: Long          // Đã thêm updatedAt
)
