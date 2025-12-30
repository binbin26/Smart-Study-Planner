package smart.planner.data.api

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import smart.planner.data.repository.HolidayRepository
import smart.planner.data.repository.QuoteRepository

/**
 * Example class để test API calls
 * Có thể sử dụng trong MainActivity hoặc test cases
 * 
 * Cách sử dụng:
 * 1. Trong MainActivity.onCreate():
 *    ApiTestExample.testHolidayApi()
 *    ApiTestExample.testQuoteApi()
 * 
 * 2. Hoặc trong ViewModel:
 *    viewModelScope.launch {
 *        ApiTestExample.testHolidayApi()
 *    }
 */
object ApiTestExample {
    
    private const val TAG = "ApiTestExample"
    
    /**
     * Test Holiday API - Lấy ngày lễ của Việt Nam năm 2024
     */
    fun testHolidayApi() {
        CoroutineScope(Dispatchers.IO).launch {
            val repository = HolidayRepository()
            
            // Test 1: Lấy ngày lễ của Việt Nam năm hiện tại
            Log.d(TAG, "=== Testing Holiday API ===")
            repository.getPublicHolidaysForCurrentYear("VN")
                .onSuccess { holidays ->
                    Log.d(TAG, "✅ Successfully fetched ${holidays.size} holidays for Vietnam")
                    holidays.forEach { holiday ->
                        Log.d(TAG, "  - ${holiday.date}: ${holiday.localName} (${holiday.name})")
                    }
                }
                .onFailure { error ->
                    Log.e(TAG, "❌ Failed to fetch holidays: ${error.message}")
                }
            
            // Test 2: Lấy ngày lễ sắp tới của Việt Nam
            repository.getNextPublicHolidays("VN")
                .onSuccess { holidays ->
                    Log.d(TAG, "✅ Successfully fetched ${holidays.size} upcoming holidays")
                    holidays.take(5).forEach { holiday ->
                        Log.d(TAG, "  - ${holiday.date}: ${holiday.localName}")
                    }
                }
                .onFailure { error ->
                    Log.e(TAG, "❌ Failed to fetch upcoming holidays: ${error.message}")
                }
        }
    }
    
    /**
     * Test Quote API - Lấy câu trích dẫn ngẫu nhiên
     */
    fun testQuoteApi() {
        CoroutineScope(Dispatchers.IO).launch {
            val repository = QuoteRepository()
            
            Log.d(TAG, "=== Testing Quote API ===")
            
            // Test 1: Lấy câu trích dẫn ngẫu nhiên
            repository.getRandomQuote()
                .onSuccess { quote ->
                    Log.d(TAG, "✅ Successfully fetched random quote:")
                    Log.d(TAG, "  \"${quote.content}\"")
                    Log.d(TAG, "  - ${quote.author}")
                }
                .onFailure { error ->
                    Log.e(TAG, "❌ Failed to fetch random quote: ${error.message}")
                }
            
            // Test 2: Lấy câu trích dẫn động viên học tập
            repository.getMotivationalStudyQuote()
                .onSuccess { quote ->
                    Log.d(TAG, "✅ Successfully fetched motivational quote:")
                    Log.d(TAG, "  \"${quote.content}\"")
                    Log.d(TAG, "  - ${quote.author}")
                }
                .onFailure { error ->
                    Log.e(TAG, "❌ Failed to fetch motivational quote: ${error.message}")
                }
        }
    }
    
    /**
     * Test cả hai APIs cùng lúc
     */
    fun testAllApis() {
        testHolidayApi()
        testQuoteApi()
    }
}

