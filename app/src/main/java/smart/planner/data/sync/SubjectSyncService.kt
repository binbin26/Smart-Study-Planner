package smart.planner.data.sync

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import smart.planner.data.local.dao.SubjectDao
import smart.planner.data.local.entity.Subject

class SubjectSyncService(
    private val subjectDao: SubjectDao,
    private val firebaseDatabase: DatabaseReference,
    private val syncManager: SyncManager
) {
    private val TAG = "SubjectSyncService"
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val subjectsRef = firebaseDatabase.child("subjects")

    fun startListening() {
        scope.launch {
            syncManager.isOnline.collect { online ->
                if (online) {
                    Log.d(TAG, "Online - Starting Firebase listener")
                    observeFirebaseChanges()
                } else {
                    Log.d(TAG, "Offline - Stopped Firebase listener")
                }
            }
        }
    }

    private fun observeFirebaseChanges() {
        subjectsRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                handleFirebaseSubject(snapshot, "ADDED")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                handleFirebaseSubject(snapshot, "CHANGED")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                handleFirebaseSubject(snapshot, "REMOVED")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Firebase listener cancelled: ${error.message}")
            }
        })
    }

    private fun handleFirebaseSubject(snapshot: DataSnapshot, eventType: String) {
        scope.launch {
            try {
                val subjectId = snapshot.key ?: return@launch

                when (eventType) {
                    "ADDED", "CHANGED" -> {
                        val firebaseSubject = snapshot.getValue(Subject::class.java)?.copy(id = subjectId)
                        if (firebaseSubject != null) {
                            val localSubject = subjectDao.getSubjectById(subjectId)

                            if (localSubject == null || firebaseSubject.updatedAt >= localSubject.updatedAt) {
                                subjectDao.insertSubject(firebaseSubject)
                                Log.d(TAG, "Synced subject from Firebase: ${firebaseSubject.name}")
                            } else {
                                Log.d(TAG, "Local version is newer, skipping: ${localSubject.name}")
                            }
                        }
                    }

                    "REMOVED" -> {
                        subjectDao.deleteSubjectById(subjectId)
                        Log.d(TAG, "Deleted subject from local: $subjectId")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling Firebase subject: ${e.message}")
            }
        }
    }

    suspend fun pushSubject(subject: Subject): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!syncManager.isOnline.value) {
                Log.d(TAG, "Offline - Subject queued for sync: ${subject.name}")
                return@withContext Result.failure(Exception("Offline"))
            }

            syncManager.setSyncing(true)

            val subjectMap = mapOf(
                "name" to subject.name,
                "code" to subject.code,
                "teacher" to subject.teacher,
                "createdAt" to subject.createdAt,
                "updatedAt" to subject.updatedAt
            )

            subjectsRef.child(subject.id).setValue(subjectMap).await()

            Log.d(TAG, "Pushed subject to Firebase: ${subject.name}")
            syncManager.setSyncing(false)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error pushing subject: ${e.message}")
            syncManager.setSyncing(false)
            Result.failure(e)
        }
    }

    suspend fun deleteSubject(subjectId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!syncManager.isOnline.value) {
                return@withContext Result.failure(Exception("Offline"))
            }

            subjectsRef.child(subjectId).removeValue().await()
            Log.d(TAG, "Deleted subject from Firebase: $subjectId")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting subject: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun initialSync(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!syncManager.isOnline.value) {
                return@withContext Result.failure(Exception("Offline"))
            }

            syncManager.setSyncing(true)

            val snapshot = subjectsRef.get().await()
            val firebaseSubjects = snapshot.children.mapNotNull { child ->
                child.getValue(Subject::class.java)?.copy(id = child.key ?: "")
            }

            val localSubjects = subjectDao.getAllSubjects().first()

            val mergedSubjects = mergeSubjects(localSubjects, firebaseSubjects)

            subjectDao.insertSubjects(mergedSubjects)

            localSubjects.forEach { local ->
                if (firebaseSubjects.none { it.id == local.id }) {
                    pushSubject(local)
                }
            }

            Log.d(TAG, "Initial sync completed: ${mergedSubjects.size} subjects")
            syncManager.setSyncing(false)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Initial sync failed: ${e.message}")
            syncManager.setSyncing(false)
            Result.failure(e)
        }
    }

    private fun mergeSubjects(local: List<Subject>, remote: List<Subject>): List<Subject> {
        val merged = mutableMapOf<String, Subject>()

        remote.forEach { merged[it.id] = it }

        local.forEach { localSubject ->
            val remoteSubject = merged[localSubject.id]
            if (remoteSubject == null || localSubject.updatedAt > remoteSubject.updatedAt) {
                merged[localSubject.id] = localSubject
            }
        }

        return merged.values.toList()
    }

    fun cleanup() {
        scope.cancel()
    }
}