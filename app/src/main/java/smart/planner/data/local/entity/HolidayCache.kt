package smart.planner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity để cache holidays từ API
 * Cache tồn tại 24h trước khi refresh
 */
@Entity(tableName = "holiday_cache")
data class HolidayCache(
    @PrimaryKey
    val id: String,

    val name: String,
    val date: String, // Format: "yyyy-MM-dd"
    val description: String,

    val updatedAt: Long = System.currentTimeMillis() // Timestamp để check cache age
)