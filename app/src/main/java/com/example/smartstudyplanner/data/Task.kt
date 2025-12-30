package smart.planner.data

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val subjectId: Int,
    val deadline: Long,
    val isCompleted: Boolean = false
)