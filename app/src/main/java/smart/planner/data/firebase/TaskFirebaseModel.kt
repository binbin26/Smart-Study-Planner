package smart.planner.data.firebase

/**
 * Model dùng để lưu trữ và đọc dữ liệu từ Firebase Realtime Database.
 * Các thuộc tính phải khớp với các key được lưu trên Firebase.
 */
data class TaskFirebaseModel(
    var title: String = "",
    var description: String = "",
    var createdAt: Long = 0L,
    var deadline: Long = 0L,
    var isDone: Boolean = false,
    var subjectId: String = "",
    var subjectName: String = ""
)