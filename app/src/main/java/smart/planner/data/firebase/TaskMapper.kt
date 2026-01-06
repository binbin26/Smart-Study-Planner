package smart.planner.data.firebase

import smart.planner.data.entity.Task

fun TaskFirebaseModel.toEntity(firebaseId: String): Task {
    return Task(
        firebaseId = firebaseId,
        title = title,
        description = description,
        createdAt = createdAt,
        deadline = deadline,
        isDone = isDone,
        subjectId = subjectId,
        subjectName = subjectName
    )
}
