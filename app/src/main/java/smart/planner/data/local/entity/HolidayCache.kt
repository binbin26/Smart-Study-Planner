package smart.planner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holiday_cache")
data class HolidayCache(
    @PrimaryKey
    val id: String,
    val name: String,
    val date: String,
    val description: String,
    val updatedAt: Long = System.currentTimeMillis()
)