package smart.planner.ui.helper

import smart.planner.data.model.Subject
import smart.planner.data.model.Task

/**
 * Helper class để kết hợp Task với Subject name
 */
data class TaskWithSubject(
    val task: Task,
    val subjectName: String?
) {
    companion object {
        /**
         * Map list Task với list Subject để tạo TaskWithSubject
         */
        fun mapTasksWithSubjects(tasks: List<Task>, subjects: List<Subject>): List<TaskWithSubject> {
            val subjectMap = subjects.associateBy { it.id }
            return tasks.map { task ->
                TaskWithSubject(
                    task = task,
                    subjectName = subjectMap[task.subjectId]?.name
                )
            }
        }
    }
}

