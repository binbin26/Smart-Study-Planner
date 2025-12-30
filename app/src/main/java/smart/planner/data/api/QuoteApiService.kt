package smart.planner.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import smart.planner.data.model.Quote
import smart.planner.data.model.QuoteResponse

/**
 * API Service interface cho Quote API (api.quotable.io)
 * Cung cấp các câu trích dẫn động viên để hỗ trợ tinh thần học tập
 */
interface QuoteApiService {
    
    /**
     * Lấy một câu trích dẫn ngẫu nhiên
     * @return Một câu trích dẫn ngẫu nhiên
     */
    @GET("random")
    suspend fun getRandomQuote(): Response<QuoteResponse>
    
    /**
     * Lấy một câu trích dẫn ngẫu nhiên với tags cụ thể
     * @param tags Danh sách tags (ví dụ: "motivational", "success", "education")
     * @return Một câu trích dẫn ngẫu nhiên
     */
    @GET("random")
    suspend fun getRandomQuoteByTags(
        @Query("tags") tags: String
    ): Response<QuoteResponse>
    
    /**
     * Lấy danh sách các câu trích dẫn
     * @param limit Số lượng câu trích dẫn muốn lấy (mặc định: 20, tối đa: 150)
     * @param skip Số lượng câu trích dẫn bỏ qua (để phân trang)
     * @return Danh sách các câu trích dẫn
     */
    @GET("quotes")
    suspend fun getQuotes(
        @Query("limit") limit: Int = 20,
        @Query("skip") skip: Int = 0
    ): Response<QuoteListResponse>
    
    /**
     * Lấy một câu trích dẫn theo ID
     * @param id ID của câu trích dẫn
     * @return Câu trích dẫn
     */
    @GET("quotes/{id}")
    suspend fun getQuoteById(
        @Path("id") id: String
    ): Response<QuoteResponse>
}

/**
 * Response wrapper cho danh sách quotes
 */
data class QuoteListResponse(
    val count: Int,
    val totalCount: Int,
    val page: Int,
    val totalPages: Int,
    val lastItemIndex: Int?,
    val results: List<Quote>
)

