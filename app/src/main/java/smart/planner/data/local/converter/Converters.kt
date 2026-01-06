package smart.planner.data.local.converter

import androidx.room.TypeConverter
import smart.planner.data.local.entity.TaskStatus

class Converters {
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String {
        return status.name
    }

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus {
        return TaskStatus.fromString(value)
    }
}