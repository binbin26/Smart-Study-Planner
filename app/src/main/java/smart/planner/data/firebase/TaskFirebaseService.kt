package smart.planner.data.firebase

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

object TaskFirebaseService {

    // Trỏ cứng vào DB ở Singapore
    private val db = FirebaseDatabase
        .getInstance("https://ltmobile-c9240-default-rtdb.asia-southeast1.firebasedatabase.app")
        .getReference("tasks")

    /**
     * Thêm một task mới và trả về key của nó trong callback
     */
    fun addNewTask(taskModel: TaskFirebaseModel, onComplete: (String?) -> Unit) {
        val newKey = db.push().key

        if (newKey == null) {
            onComplete(null)
            return
        }

        db.child(newKey).setValue(taskModel)
            .addOnSuccessListener { 
                // Gửi lại key khi thành công
                onComplete(newKey) 
            }
            .addOnFailureListener { 
                // Gửi null khi thất bại
                onComplete(null) 
            }
    }

    /**
     * Cập nhật task trên Firebase
     */
    fun updateTask(task: smart.planner.data.model.Task) {
        if (task.firebaseId.isEmpty()) {
            println("⚠️ Task không có firebaseId, không thể update")
            return
        }

        val updatedData = TaskFirebaseModel(
            id = task.firebaseId,
            title = task.title,
            description = task.description,
            createdAt = task.createdAt,
            deadline = task.deadline,
            status = task.status,
            subjectId = task.subjectId,
            updatedAt = System.currentTimeMillis()
        )

        db.child(task.firebaseId)
            .setValue(updatedData)
            .addOnSuccessListener {
                println("✅ Cập nhật task thành công trên Firebase: ${task.firebaseId}")
            }
            .addOnFailureListener {
                println("❌ Cập nhật task thất bại: ${it.message}")
            }
    }

    /**
     * Xóa một task theo ID
     */
    fun deleteTask(firebaseId: String, onComplete: (Boolean) -> Unit) {
        if (firebaseId.isNotEmpty()) {
            db.child(firebaseId).removeValue()
                .addOnCompleteListener { task ->
                    onComplete(task.isSuccessful)
                }
        }
    }
}
