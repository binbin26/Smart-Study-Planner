package smart.planner.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import smart.planner.data.local.entity.QuoteCache

@Dao
interface QuoteCacheDao {

    @Query("SELECT * FROM quote_cache")
    fun getAllQuotes(): Flow<List<QuoteCache>>

    @Query("SELECT * FROM quote_cache WHERE id = :id")
    suspend fun getQuoteById(id: String): QuoteCache?

    @Query("SELECT * FROM quote_cache ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuote(): QuoteCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<QuoteCache>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteCache)

    @Query("DELETE FROM quote_cache")
    suspend fun clearCache()

    @Query("SELECT updatedAt FROM quote_cache ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLastUpdateTime(): Long?

    @Query("SELECT COUNT(*) FROM quote_cache")
    suspend fun getCount(): Int
}