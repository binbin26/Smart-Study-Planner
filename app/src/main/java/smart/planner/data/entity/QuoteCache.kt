package smart.planner.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuoteCache(
    @PrimaryKey val id: Int = 1,   // chỉ lưu 1 quote
    val content: String,
    val author: String?,
    val timestamp: Long
)
