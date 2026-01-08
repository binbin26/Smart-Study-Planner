package smart.planner.data.firebase

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class TaskFirebaseModel(
    var id: String? = null,
    var title: String? = null,
    var description: String? = null,
    var createdAt: Long? = null,
    var deadline: Long? = null,
    var status: String? = "TODO",
    var subjectId: String? = null,
    var updatedAt: Long? = null
)