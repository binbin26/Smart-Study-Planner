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
    var id: String? = null,
    var title: String? = null,
    var description: String? = null,
    var createdAt: Long? = null,
    var deadline: Long? = null,
    var status: String? = "TODO",        // ← ĐỔI từ isDone
    var subjectId: String? = null,       // ← GIỮ LẠI
    var updatedAt: Long? = null          // ← GIỮ LẠI
    // XÓA subjectName
)

object TaskFirebaseService {

    private val database = FirebaseDatabase
        .getInstance()
        .getReference("tasks")

    fun addNewTask(task: TaskFirebaseModel, onComplete: (String?) -> Unit) {
        val newRef = database.push()
        val taskWithId = task.copy(id = newRef.key)

        newRef.setValue(taskWithId)
            .addOnSuccessListener {
                println("✅ Lưu task thành công: ${newRef.key}")
                onComplete(newRef.key)
            }
            .addOnFailureListener {
                println("❌ Lưu task thất bại: ${it.message}")
                onComplete(null)
            }
    }

    fun deleteTask(firebaseId: String, callback: (Boolean) -> Unit) {
        database.child(firebaseId)
            .removeValue()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun getTasks(): Flow<List<TaskFirebaseModel>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasks = snapshot.children.mapNotNull { snap ->
                    snap.getValue(TaskFirebaseModel::class.java)
                }
                trySend(tasks)
            }

            override fun onCancelled(error: DatabaseError) {
                println("❌ Lỗi lấy dữ liệu: ${error.message}")
                close(error.toException())
            }
        }

        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }
}