package smart.planner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quote_cache")
data class QuoteCache(
    @PrimaryKey
    val id: String,
    val text: String,
    val author: String,
    val updatedAt: Long = System.currentTimeMillis()
)