package smart.planner.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import smart.planner.data.local.entity.Motivation

@Dao
interface MotivationDao {

    // ==================== READ OPERATIONS ====================

    /**
     * Lấy tất cả motivations đang active
     * Sắp xếp theo thời gian tạo (mới nhất trước)
     */
    @Query("SELECT * FROM motivations WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllActiveMotivations(): Flow<List<Motivation>>

    /**
     * Lấy tất cả motivations (bao gồm cả inactive)
     */
    @Query("SELECT * FROM motivations ORDER BY createdAt DESC")
    fun getAllMotivations(): Flow<List<Motivation>>

    /**
     * Lấy motivation theo ID
     */
    @Query("SELECT * FROM motivations WHERE id = :id")
    suspend fun getMotivationById(id: String): Motivation?

    /**
     * Lấy motivations theo ngôn ngữ (vi hoặc en)
     */
    @Query("""
        SELECT * FROM motivations 
        WHERE language = :language 
        AND isActive = 1
        ORDER BY createdAt DESC
    """)
    fun getMotivationsByLanguage(language: String): Flow<List<Motivation>>

    /**
     * Lấy một motivation ngẫu nhiên
     * Chỉ lấy những motivation đang active
     */
    @Query("""
        SELECT * FROM motivations 
        WHERE isActive = 1 
        ORDER BY RANDOM() 
        LIMIT 1
    """)
    suspend fun getRandomMotivation(): Motivation?

    /**
     * Lấy motivation ngẫu nhiên theo ngôn ngữ
     */
    @Query("""
        SELECT * FROM motivations 
        WHERE language = :language 
        AND isActive = 1 
        ORDER BY RANDOM() 
        LIMIT 1
    """)
    suspend fun getRandomMotivationByLanguage(language: String): Motivation?

    /**
     * Lấy N motivations ngẫu nhiên
     * Dùng cho daily quotes hoặc recommendations
     */
    @Query("""
        SELECT * FROM motivations 
        WHERE isActive = 1 
        ORDER BY RANDOM() 
        LIMIT :count
    """)
    suspend fun getRandomMotivations(count: Int): List<Motivation>

    /**
     * Tìm kiếm motivation theo keyword trong quote
     */
    @Query("""
        SELECT * FROM motivations 
        WHERE quote LIKE '%' || :keyword || '%' 
        AND isActive = 1
        ORDER BY createdAt DESC
    """)
    fun searchMotivations(keyword: String): Flow<List<Motivation>>

    /**
     * Đếm số motivations đang active
     */
    @Query("SELECT COUNT(*) FROM motivations WHERE isActive = 1")
    suspend fun countActiveMotivations(): Int

    // ==================== CREATE OPERATIONS ====================

    /**
     * Thêm một motivation mới
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMotivation(motivation: Motivation)

    /**
     * Thêm nhiều motivations cùng lúc (bulk insert)
     * Dùng khi import data từ API hoặc file
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMotivations(motivations: List<Motivation>)

    // ==================== UPDATE OPERATIONS ====================

    /**
     * Cập nhật toàn bộ motivation
     */
    @Update
    suspend fun updateMotivation(motivation: Motivation)

    /**
     * Toggle active/inactive status
     */
    @Query("UPDATE motivations SET isActive = :isActive WHERE id = :id")
    suspend fun toggleMotivationActive(id: String, isActive: Boolean)

    /**
     * Cập nhật quote text
     */
    @Query("UPDATE motivations SET quote = :newQuote WHERE id = :id")
    suspend fun updateMotivationQuote(id: String, newQuote: String)

    /**
     * Deactivate tất cả motivations (dùng khi refresh data)
     */
    @Query("UPDATE motivations SET isActive = 0")
    suspend fun deactivateAllMotivations()

    // ==================== DELETE OPERATIONS ====================

    /**
     * Xóa một motivation
     */
    @Delete
    suspend fun deleteMotivation(motivation: Motivation)

    /**
     * Xóa motivation theo ID
     */
    @Query("DELETE FROM motivations WHERE id = :id")
    suspend fun deleteMotivationById(id: String)

    /**
     * Xóa tất cả motivations inactive
     * Dùng để cleanup database
     */
    @Query("DELETE FROM motivations WHERE isActive = 0")
    suspend fun deleteInactiveMotivations()

    /**
     * Xóa toàn bộ motivations (dùng cho testing hoặc reset)
     */
    @Query("DELETE FROM motivations")
    suspend fun deleteAllMotivations()
}