package smart.planner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "motivations")
data class Motivation(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val quote: String,
    val author: String? = null,
    val language: String = "vi", // Hỗ trợ đa ngôn ngữ sau này
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this(id = "", quote = "")

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "quote" to quote,
            "author" to author,
            "language" to language,
            "isActive" to isActive,
            "createdAt" to createdAt
        )
    }
}