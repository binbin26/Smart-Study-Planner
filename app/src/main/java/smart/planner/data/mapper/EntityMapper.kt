package smart.planner.data.mapper

import smart.planner.data.local.entity.HolidayCache
import smart.planner.data.local.entity.QuoteCache
import smart.planner.data.model.Holiday
import smart.planner.data.model.Quote

fun Holiday.toCache(): HolidayCache {
    return HolidayCache(
        id = "${countryCode}_${date}",
        name = localName,
        date = date,
        description = "$name (${countryCode})",
        updatedAt = System.currentTimeMillis()
    )
}

fun List<Holiday>.toHolidayCacheList(): List<HolidayCache> {
    return this.map { it.toCache() }
}

fun Quote.toCache(): QuoteCache {
    return QuoteCache(
        id = id,
        text = content,
        author = author,
        updatedAt = System.currentTimeMillis()
    )
}

fun List<Quote>.toQuoteCacheList(): List<QuoteCache> {
    return this.map { it.toCache() }
}

fun HolidayCache.toHoliday(): Holiday {
    val countryCode = id.split("_").firstOrNull() ?: "VN"
    return Holiday(
        date = date,
        localName = name,
        name = description.substringBefore(" ("),
        countryCode = countryCode,
        fixed = false,
        global = false,
        counties = null,
        launchYear = null,
        types = emptyList()
    )
}

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