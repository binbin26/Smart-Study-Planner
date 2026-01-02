package smart.planner.data.model

<<<<<<< HEAD
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val subject: String,
    val deadline: Long, // Lưu timestamp
    val description: String
=======
data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val subjectId: Int,
    val deadline: Long,
    var isCompleted: Boolean = false
>>>>>>> origin/Tuấn
)