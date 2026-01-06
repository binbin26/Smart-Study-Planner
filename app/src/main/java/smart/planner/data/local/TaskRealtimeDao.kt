package smart.planner.data.local

import com.google.firebase.database.*
import smart.planner.data.model.Task

class TaskRealtimeDao {
    private val db = FirebaseDatabase.getInstance().getReference("tasks")

    fun getNewKey(): String? = db.push().key

    fun saveTask(firebaseId: String, task: Task, callback: (Boolean) -> Unit = {}) {
        val key = if (firebaseId.isEmpty()) db.push().key ?: return else firebaseId
        // Đã cập nhật để sử dụng đúng các trường mới của Task
        val taskMap = mapOf(
            "title" to task.title,
            "description" to task.description,
            "deadline" to task.deadline,
            "subjectId" to task.subjectId, // Sửa từ subjectName
            "status" to task.status,       // Sửa từ isDone
            "createdAt" to task.createdAt,
            "updatedAt" to ServerValue.TIMESTAMP
        )
        db.child(key).setValue(taskMap).addOnCompleteListener { callback(it.isSuccessful) }
    }

    fun getAllTasks(onSuccess: (List<Task>) -> Unit) {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { child ->
                    // Đã cập nhật để khớp với hàm khởi tạo mới của Task
                    Task(
                        firebaseId = child.key ?: "",
                        title = child.child("title").value?.toString() ?: "",
                        description = child.child("description").value?.toString() ?: "",
                        deadline = (child.child("deadline").value as? Long) ?: 0L,
                        subjectId = child.child("subjectId").value?.toString() ?: "", // Sửa từ subjectName
                        status = child.child("status").value?.toString() ?: "TODO",   // Sửa từ isDone
                        createdAt = (child.child("createdAt").value as? Long) ?: 0L,
                        updatedAt = (child.child("updatedAt").value as? Long) ?: 0L    // Thêm updatedAt
                    )
                }
                onSuccess(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun deleteTask(firebaseId: String, callback: (Boolean) -> Unit = {}) {
        if (firebaseId.isNotEmpty()) {
            // Xóa trực tiếp node trên Firebase
            db.child(firebaseId).removeValue().addOnCompleteListener {
                callback(it.isSuccessful)
            }
        }
    }
}
