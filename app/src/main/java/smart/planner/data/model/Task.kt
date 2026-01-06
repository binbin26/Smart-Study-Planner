package smart.planner.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firebaseId: String = "",
    val title: String,
    val description: String,
    val createdAt: Long,
    val deadline: Long,
    val isDone: Boolean = false,
    val subjectName: String
)

