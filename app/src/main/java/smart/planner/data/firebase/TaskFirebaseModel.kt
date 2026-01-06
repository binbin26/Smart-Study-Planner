package smart.planner.data.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@IgnoreExtraProperties
data class TaskFirebaseModel(
    val title: String? = null,
    val description: String? = null,
    val createdAt: Long? = null,
    val deadline: Long? = null,
    val status: String? = null,
    val subjectId: String? = null,
    val updatedAt: Long? = null
)

object TaskFirebaseService {
    private val database = FirebaseDatabase.getInstance().getReference("tasks")

    // Đã cập nhật: Hàm này giờ có một callback để trả về ID của task mới
    fun addNewTask(task: TaskFirebaseModel, onComplete: (String?) -> Unit) {
        val newRef = database.push()
        newRef.setValue(task)
            .addOnSuccessListener {
                // Ghi thành công, trả về key của task mới
                println("Đã lưu dữ liệu thành công lên Firebase với ID: ${newRef.key}")
                onComplete(newRef.key)
            }
            .addOnFailureListener {
                // Ghi thất bại, trả về null
                println("Lưu dữ liệu thất bại. Lỗi: ${it.message}")
                onComplete(null)
            }
    }

    fun deleteTask(firebaseId: String, callback: (Boolean) -> Unit) {
        database.child(firebaseId).removeValue()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun getTasks(): Flow<List<TaskFirebaseModel>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasks = snapshot.children.mapNotNull { it.getValue(TaskFirebaseModel::class.java) }
                trySend(tasks)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Lấy dữ liệu thất bại: ${error.message}")
                close(error.toException())
            }
        }
        database.addValueEventListener(listener)

        awaitClose { database.removeEventListener(listener) }
    }
}
