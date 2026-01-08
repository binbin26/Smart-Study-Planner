package smart.planner.data.local

import com.google.firebase.database.*
import smart.planner.data.model.Task

class TaskRealtimeDao {
    // ✅ CẬP NHẬT: Trỏ đúng vào server Singapore
    private val db = FirebaseDatabase
        .getInstance("https://ltmobile-c9240-default-rtdb.asia-southeast1.firebasedatabase.app")
        .getReference("tasks")

    fun getNewKey(): String? = db.push().key

    fun saveTask(firebaseId: String, task: Task, callback: (Boolean) -> Unit = {}) {
        val key = if (firebaseId.isEmpty()) db.push().key ?: return else firebaseId
        
        val taskMap = mapOf(
            "title" to task.title,
            "description" to task.description,
            "deadline" to task.deadline,
            "subjectId" to task.subjectId,
            "status" to task.status,
            "createdAt" to task.createdAt,
            "updatedAt" to ServerValue.TIMESTAMP
        )
        db.child(key).setValue(taskMap).addOnCompleteListener { callback(it.isSuccessful) }
    }

    fun getAllTasks(onSuccess: (List<Task>) -> Unit) {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { child ->
                    Task(
                        firebaseId = child.key ?: "",
                        title = child.child("title").value?.toString() ?: "",
                        description = child.child("description").value?.toString() ?: "",
                        deadline = (child.child("deadline").value as? Long) ?: 0L,
                        subjectId = child.child("subjectId").value?.toString() ?: "",
                        status = child.child("status").value?.toString() ?: "TODO",
                        createdAt = (child.child("createdAt").value as? Long) ?: 0L,
                        updatedAt = (child.child("updatedAt").value as? Long) ?: 0L
                    )
                }
                onSuccess(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun deleteTask(firebaseId: String, callback: (Boolean) -> Unit = {}) {
        if (firebaseId.isNotEmpty()) {
            db.child(firebaseId).removeValue().addOnCompleteListener {
                callback(it.isSuccessful)
            }
        }
    }
}
