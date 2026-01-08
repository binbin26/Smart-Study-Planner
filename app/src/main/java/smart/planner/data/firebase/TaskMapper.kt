package smart.planner.data.firebase

import smart.planner.data.model.Task

/**
 * Mapper giữa Task (Room) và TaskFirebaseModel
 */

// Convert Task entity → Firebase model
fun Task.toFirebaseModel(): TaskFirebaseModel {
    return TaskFirebaseModel(
        id = firebaseId.ifEmpty { null },
        title = title,
        description = description,
        deadline = deadline,
        status = status,
        subjectId = subjectId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

// Convert Firebase model → Task entity
fun TaskFirebaseModel.toTask(localId: Int = 0): Task {
    return Task(
        id = localId,
        firebaseId = this.id ?: "",
        title = this.title ?: "",
        description = this.description ?: "",
        createdAt = this.createdAt ?: System.currentTimeMillis(),
        deadline = this.deadline ?: System.currentTimeMillis(),
        status = this.status ?: "TODO",
        subjectId = this.subjectId ?: "",
        updatedAt = this.updatedAt ?: System.currentTimeMillis()
    )
}