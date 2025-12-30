package smart.planner.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data model cho Quote API (api.quotable.io)
 * Đại diện cho một câu trích dẫn động viên học tập
 */
data class Quote(
    @SerializedName("_id")
    val id: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("author")
    val author: String,
    
    @SerializedName("tags")
    val tags: List<String>?,
    
    @SerializedName("authorSlug")
    val authorSlug: String?,
    
    @SerializedName("length")
    val length: Int?,
    
    @SerializedName("dateAdded")
    val dateAdded: String?,
    
    @SerializedName("dateModified")
    val dateModified: String?
)

/**
 * Response wrapper cho Quote API khi lấy random quote
 */
data class QuoteResponse(
    @SerializedName("_id")
    val id: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("author")
    val author: String,
    
    @SerializedName("tags")
    val tags: List<String>?,
    
    @SerializedName("authorSlug")
    val authorSlug: String?,
    
    @SerializedName("length")
    val length: Int?,
    
    @SerializedName("dateAdded")
    val dateAdded: String?,
    
    @SerializedName("dateModified")
    val dateModified: String?
)

