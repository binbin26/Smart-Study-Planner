package smart.planner.data.test

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * File test để kiểm tra Dispatchers.IO hoạt động đúng cách
 * 
 * Cách sử dụng:
 * 1. Trong MainActivity.onCreate():
 *    CoroutinesIOTest.testDispatchersIO()
 * 
 * 2. Hoặc trong ViewModel:
 *    viewModelScope.launch {
 *        CoroutinesIOTest.testDispatchersIO()
 *    }
 */
object CoroutinesIOTest {
    
    private const val TAG = "CoroutinesIOTest"
    
    /**
     * Test 1: Kiểm tra Dispatchers.IO cho các operations
     * Test này sẽ log thread name để verify operations chạy trên IO thread
     */
    fun testDispatchersIO() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "=== Testing Dispatchers.IO ===")
            
            // Test 1: Verify đang chạy trên IO thread
            val currentThread = Thread.currentThread().name
            Log.d(TAG, "✅ Current thread: $currentThread")
            Log.d(TAG, "✅ Is IO thread: ${currentThread.contains("DefaultDispatcher-worker", ignoreCase = true)}")
            
            // Test 2: Simulate Room Database operation
            testRoomOperation()
            
            // Test 3: Simulate Firebase operation
            testFirebaseOperation()
            
            // Test 4: Simulate Network operation
            testNetworkOperation()
            
            // Test 5: Test với withContext
            testWithContext()
        }
    }
    
    /**
     * Test 2: Simulate Room Database operation
     */
    private suspend fun testRoomOperation() {
        Log.d(TAG, "\n--- Test Room Database Operation ---")
        
        val result = withContext(Dispatchers.IO) {
            val threadName = Thread.currentThread().name
            Log.d(TAG, "  Thread trong withContext(Dispatchers.IO): $threadName")
            
            // Simulate database query
            Thread.sleep(100) // Simulate database read
            
            "Room data loaded successfully"
        }
        
        Log.d(TAG, "  ✅ Result: $result")
        Log.d(TAG, "  ✅ Thread sau withContext: ${Thread.currentThread().name}")
    }
    
    /**
     * Test 3: Simulate Firebase operation
     */
    private suspend fun testFirebaseOperation() {
        Log.d(TAG, "\n--- Test Firebase Operation ---")
        
        val result = withContext(Dispatchers.IO) {
            val threadName = Thread.currentThread().name
            Log.d(TAG, "  Thread trong withContext(Dispatchers.IO): $threadName")
            
            // Simulate Firebase network call
            Thread.sleep(150) // Simulate network delay
            
            "Firebase data loaded successfully"
        }
        
        Log.d(TAG, "  ✅ Result: $result")
        Log.d(TAG, "  ✅ Thread sau withContext: ${Thread.currentThread().name}")
    }
    
    /**
     * Test 4: Simulate Network operation
     */
    private suspend fun testNetworkOperation() {
        Log.d(TAG, "\n--- Test Network Operation ---")
        
        val result = withContext(Dispatchers.IO) {
            val threadName = Thread.currentThread().name
            Log.d(TAG, "  Thread trong withContext(Dispatchers.IO): $threadName")
            
            // Simulate API call
            Thread.sleep(200) // Simulate network delay
            
            "Network data loaded successfully"
        }
        
        Log.d(TAG, "  ✅ Result: $result")
        Log.d(TAG, "  ✅ Thread sau withContext: ${Thread.currentThread().name}")
    }
    
    /**
     * Test 5: Test với withContext - verify thread switching
     */
    private suspend fun testWithContext() {
        Log.d(TAG, "\n--- Test WithContext Thread Switching ---")
        
        // Start trên IO thread
        val initialThread = Thread.currentThread().name
        Log.d(TAG, "  Initial thread: $initialThread")
        
        // Switch to IO explicitly
        val result = withContext(Dispatchers.IO) {
            val ioThread = Thread.currentThread().name
            Log.d(TAG, "  Thread trong withContext(Dispatchers.IO): $ioThread")
            
            // Simulate operation
            Thread.sleep(50)
            
            "Operation completed"
        }
        
        val finalThread = Thread.currentThread().name
        Log.d(TAG, "  Final thread: $finalThread")
        Log.d(TAG, "  ✅ Result: $result")
        Log.d(TAG, "  ✅ Thread switching successful: ${initialThread != finalThread}")
    }
    
    /**
     * Test 6: Test error handling với Dispatchers.IO
     */
    fun testErrorHandling() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "\n=== Testing Error Handling ===")
            
            val result = withContext(Dispatchers.IO) {
                try {
                    // Simulate operation that might fail
                    if (System.currentTimeMillis() % 2 == 0L) {
                        throw Exception("Simulated error")
                    }
                    "Success"
                } catch (e: Exception) {
                    Log.e(TAG, "  ❌ Error caught: ${e.message}")
                    "Error handled"
                }
            }
            
            Log.d(TAG, "  ✅ Result: $result")
        }
    }
    
    /**
     * Test 7: Test multiple operations cùng lúc
     */
    fun testMultipleOperations() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "\n=== Testing Multiple Operations ===")
            
            val results = mutableListOf<String>()
            
            // Launch multiple operations
            val job1 = launch {
                val result = withContext(Dispatchers.IO) {
                    Thread.sleep(100)
                    "Operation 1 completed"
                }
                results.add(result)
                Log.d(TAG, "  ✅ $result")
            }
            
            val job2 = launch {
                val result = withContext(Dispatchers.IO) {
                    Thread.sleep(150)
                    "Operation 2 completed"
                }
                results.add(result)
                Log.d(TAG, "  ✅ $result")
            }
            
            val job3 = launch {
                val result = withContext(Dispatchers.IO) {
                    Thread.sleep(200)
                    "Operation 3 completed"
                }
                results.add(result)
                Log.d(TAG, "  ✅ $result")
            }
            
            // Wait for all to complete
            job1.join()
            job2.join()
            job3.join()
            
            Log.d(TAG, "  ✅ All operations completed: ${results.size}/3")
        }
    }
    
    /**
     * Test tất cả
     */
    fun testAll() {
        testDispatchersIO()
        testErrorHandling()
        testMultipleOperations()
    }
}

