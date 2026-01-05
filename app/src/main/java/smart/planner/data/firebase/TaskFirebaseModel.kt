package smart.planner.data.firebase

data class TaskFirebaseModel(
    var title: String = "",
    var description: String = "",
    var createdAt: Long = 0L,
    var deadline: Long = 0L,
    var isDone: Boolean = false,
    var subjectId: String = "",
    var subjectName: String = ""
)
