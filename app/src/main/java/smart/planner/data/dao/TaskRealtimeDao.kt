package smart.planner.data.dao

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import smart.planner.data.firebase.TaskFirebaseModel

class TaskRealtimeDao {

    private val db: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("tasks")

    fun getAllTasks(
        onSuccess: (List<Pair<String, TaskFirebaseModel>>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Pair<String, TaskFirebaseModel>>()

                for (child in snapshot.children) {
                    val task = child.getValue(TaskFirebaseModel::class.java)
                    val key = child.key

                    if (task != null && key != null) {
                        list.add(Pair(key, task))
                    }
                }
                onSuccess(list)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        })
    }

    fun saveTask(firebaseId: String, task: TaskFirebaseModel) {
        db.child(firebaseId).setValue(task)
    }

    fun updateTaskDone(firebaseId: String, isDone: Boolean) {
        db.child(firebaseId).child("isDone").setValue(isDone)
    }

    fun deleteTask(firebaseId: String) {
        db.child(firebaseId).removeValue()
    }
}
