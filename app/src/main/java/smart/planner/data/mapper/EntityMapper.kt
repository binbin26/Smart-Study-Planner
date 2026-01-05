package smart.planner.data.mapper

import smart.planner.data.local.entity.HolidayCache
import smart.planner.data.local.entity.QuoteCache
import smart.planner.data.model.Holiday
import smart.planner.data.model.Quote
import smart.planner.data.model.QuoteResponse

/**
 * Extension functions để convert giữa API models và Cache entities
 */

/**
 * Convert Holiday (API model) sang HolidayCache (Room entity)
 */
fun Holiday.toCache(): HolidayCache {
    return HolidayCache(
        id = "${countryCode}_${date}", // Unique ID: VN_2025-01-01
        name = localName, // Ưu tiên tên địa phương
        date = date,
        description = "$name (${countryCode})", // Name + country code
        updatedAt = System.currentTimeMillis()
    )
}

/**
 * Convert List<Holiday> sang List<HolidayCache>
 */
fun List<Holiday>.toHolidayCacheList(): List<HolidayCache> {
    return this.map { it.toCache() }
}

/**
 * Convert Quote (API model) sang QuoteCache (Room entity)
 */
fun Quote.toCache(): QuoteCache {
    return QuoteCache(
        id = id, // Sử dụng ID từ API
        text = content, // Content của quote
        author = author,
        updatedAt = System.currentTimeMillis()
    )
}

/**
 * Convert QuoteResponse sang QuoteCache
 */
fun QuoteResponse.toCache(): QuoteCache {
    return QuoteCache(
        id = id,
        text = content,
        author = author,
        updatedAt = System.currentTimeMillis()
    )
}

/**
 * Convert List<Quote> sang List<QuoteCache>
 */
fun List<Quote>.toQuoteCacheList(): List<QuoteCache> {
    return this.map { it.toCache() }
}

/**
 * Convert HolidayCache sang Holiday (để hiển thị)
 * Lưu ý: Một số fields sẽ bị mất vì cache đơn giản hóa
 */
fun HolidayCache.toHoliday(): Holiday {
    // Extract country code từ ID (format: VN_2025-01-01)
    val countryCode = id.split("_").firstOrNull() ?: "VN"

    return Holiday(
        date = date,
        localName = name,
        name = description.substringBefore(" ("),
        countryCode = countryCode,
        fixed = false, // Default
        global = false, // Default
        counties = null,
        launchYear = null,
        types = emptyList()
    )
}

/**
 * Convert QuoteCache sang Quote (để hiển thị)
 */
fun QuoteCache.toQuote(): Quote {
    return Quote(
        id = id,
        content = text,
        author = author,
        tags = null,
        authorSlug = null,
        length = text.length,
        dateAdded = null,
        dateModified = null
    )
}