package smart.planner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(), // Tự động sinh ID duy nhất
    val name: String,
    val code: String,
    val teacher: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    // Constructor rỗng bắt buộc cho Firebase
    constructor() : this(id = "", name = "", code = "", teacher = "")

    // Map cho Firebase
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "code" to code,
            "teacher" to teacher,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
}