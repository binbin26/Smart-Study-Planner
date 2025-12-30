package smart.planner.data.repository

import smart.planner.data.api.QuoteApiService
import smart.planner.data.api.RetrofitClient
import smart.planner.data.local.VietnameseQuotes
import smart.planner.data.model.QuoteResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository cho Quote API
 * Quản lý các API calls liên quan đến câu trích dẫn động viên
 * 
 * Repository pattern giúp tách biệt logic API calls khỏi ViewModel
 * 
 * Sử dụng quotes tiếng Việt local để đảm bảo có nội dung phù hợp
 */
class QuoteRepository {
    
    private val apiService: QuoteApiService = RetrofitClient.quoteApiService
    
    /**
     * Lấy một câu trích dẫn ngẫu nhiên bằng Tiếng Việt
     * Sử dụng local data thay vì API để đảm bảo có quotes tiếng Việt
     * @return Result chứa câu trích dẫn hoặc error
     */
    suspend fun getRandomQuote(): Result<QuoteResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Sử dụng quotes tiếng Việt local
                val quote = VietnameseQuotes.getRandomQuote()
                Result.success(quote)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Lấy một câu trích dẫn ngẫu nhiên với tags cụ thể (từ API tiếng Anh)
     * Sử dụng Dispatchers.IO cho network operations
     * 
     * @param tags Danh sách tags (ví dụ: "motivational,success,education")
     * @return Result chứa câu trích dẫn hoặc error
     */
    suspend fun getRandomQuoteByTags(tags: String): Result<QuoteResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRandomQuoteByTags(tags)
                
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch quote by tags: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Lấy một câu trích dẫn động viên học tập bằng Tiếng Việt
     * Sử dụng local data để đảm bảo có quotes tiếng Việt phù hợp
     * @return Result chứa câu trích dẫn hoặc error
     */
    suspend fun getMotivationalStudyQuote(): Result<QuoteResponse> {
        return getRandomQuote() // Sử dụng quotes tiếng Việt local
    }
}

