package smart.planner.data.local.entity

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    DONE,
    OVERDUE;

    companion object {
        fun fromString(value: String): TaskStatus {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                TODO
            }
        }
    }
}
