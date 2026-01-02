package smart.planner.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import smart.planner.data.entity.QuoteCache

@Dao
interface QuoteCacheDao {

    @Query("SELECT * FROM QuoteCache LIMIT 1")
    suspend fun getCache(): QuoteCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cache: QuoteCache)
}
