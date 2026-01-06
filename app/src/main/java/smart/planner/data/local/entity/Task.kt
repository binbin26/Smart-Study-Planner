package smart.planner.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "tasks",
    // Khóa ngoại: Đảm bảo toàn vẹn dữ liệu
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE // Xóa Môn học -> Xóa hết Task của môn đó
        ),
        ForeignKey(
            entity = Motivation::class,
            parentColumns = ["id"],
            childColumns = ["motivationId"],
            onDelete = ForeignKey.SET_NULL // Xóa Quote -> Task vẫn còn, chỉ mất link quote
        )
    ],
    // Index giúp tìm kiếm nhanh hơn
    indices = [
        Index("subjectId"),
        Index("motivationId"),
        Index("status")
    ]
)
data class Task(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val title: String,
    val description: String,
    val deadline: Long,

    // Sử dụng Enum thay vì String thường
    val status: TaskStatus = TaskStatus.TODO,

    val subjectId: String,

    // Nullable như thiết kế: Có thể có quote thưởng hoặc không
    val motivationId: String? = null,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    // Constructor rỗng cho Firebase
    constructor() : this(
        id = "", title = "", description = "", deadline = 0L,
        subjectId = ""
    )

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "title" to title,
            "description" to description,
            "deadline" to deadline,
            "status" to status.name, // Lưu enum dưới dạng String
            "subjectId" to subjectId,
            "motivationId" to motivationId,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
}