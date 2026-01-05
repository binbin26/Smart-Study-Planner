package smart.planner.data.cache

object CacheManager {
    const val CACHE_DURATION_24H = 24 * 60 * 60 * 1000L
    const val CACHE_DURATION_1H = 60 * 60 * 1000L
    const val CACHE_DURATION_7D = 7 * 24 * 60 * 60 * 1000L

    fun shouldRefresh(lastUpdated: Long, duration: Long = CACHE_DURATION_24H): Boolean {
        val now = System.currentTimeMillis()
        val cacheAge = now - lastUpdated
        return cacheAge > duration
    }

    fun isCacheValid(lastUpdated: Long, duration: Long = CACHE_DURATION_24H): Boolean {
        return !shouldRefresh(lastUpdated, duration)
    }

    fun getCacheRemainingTime(lastUpdated: Long, duration: Long = CACHE_DURATION_24H): Long {
        val now = System.currentTimeMillis()
        val expiryTime = lastUpdated + duration
        return maxOf(0, expiryTime - now)
    }

    fun getCacheRemainingTimeString(lastUpdated: Long, duration: Long = CACHE_DURATION_24H): String {
        val remaining = getCacheRemainingTime(lastUpdated, duration)
        val hours = remaining / (60 * 60 * 1000)
        val minutes = (remaining % (60 * 60 * 1000)) / (60 * 1000)
        return "${hours}h ${minutes}m"
    }
}