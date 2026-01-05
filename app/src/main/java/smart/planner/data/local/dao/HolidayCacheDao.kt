package smart.planner.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import smart.planner.data.local.entity.HolidayCache

@Dao
interface HolidayCacheDao {

    @Query("SELECT * FROM holiday_cache ORDER BY date ASC")
    fun getAllHolidays(): Flow<List<HolidayCache>>

    @Query("SELECT * FROM holiday_cache WHERE id = :id")
    suspend fun getHolidayById(id: String): HolidayCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolidays(holidays: List<HolidayCache>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoliday(holiday: HolidayCache)

    @Query("DELETE FROM holiday_cache")
    suspend fun clearCache()

    @Query("SELECT updatedAt FROM holiday_cache ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getLastUpdateTime(): Long?

    @Query("SELECT COUNT(*) FROM holiday_cache")
    suspend fun getCount(): Int
}