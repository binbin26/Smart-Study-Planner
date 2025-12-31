package smart.planner.data.model

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val subjectId: Int,
    val deadline: Long,
    var isCompleted: Boolean = false
)