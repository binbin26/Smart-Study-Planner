package smart.planner.data.repository

import smart.planner.data.api.HolidayApiService
import smart.planner.data.api.RetrofitClient
import smart.planner.data.local.VietnameseHolidays
import smart.planner.data.model.Holiday
import java.util.Calendar

/**
 * Repository cho Holiday API
 * Quản lý các API calls liên quan đến ngày lễ
 * 
 * Repository pattern giúp tách biệt logic API calls khỏi ViewModel
 */
class HolidayRepository {
    
    private val apiService: HolidayApiService = RetrofitClient.holidayApiService
    
    /**
     * Lấy danh sách các ngày lễ của một quốc gia trong năm hiện tại
     * Đối với Việt Nam: Merge local data (đầy đủ) với API data
     * @param countryCode Mã quốc gia (ví dụ: "VN" cho Việt Nam, "US" cho Mỹ)
     * @return Result chứa danh sách ngày lễ hoặc error
     */
    suspend fun getPublicHolidaysForCurrentYear(countryCode: String): Result<List<Holiday>> {
        return try {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            
            // Đối với Việt Nam, sử dụng local data đầy đủ và merge với API
            if (countryCode == "VN") {
                val localHolidays = VietnameseHolidays.getHolidaysForYear(currentYear)
                
                // Thử lấy từ API để merge (nếu API có thêm ngày lễ khác)
                return try {
                    val response = apiService.getPublicHolidays(currentYear, countryCode)
                    if (response.isSuccessful && response.body() != null) {
                        val apiHolidays = response.body()!!
                        val mergedHolidays = VietnameseHolidays.mergeWithApiHolidays(apiHolidays, currentYear)
                        Result.success(mergedHolidays)
                    } else {
                        // Nếu API lỗi, vẫn trả về local data
                        Result.success(localHolidays)
                    }
                } catch (e: Exception) {
                    // Nếu API lỗi, vẫn trả về local data
                    Result.success(localHolidays)
                }
            }
            
            // Đối với các quốc gia khác, dùng API
            val response = apiService.getPublicHolidays(currentYear, countryCode)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch holidays: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Lấy danh sách các ngày lễ của một quốc gia trong năm cụ thể
     * Đối với Việt Nam: Merge local data (đầy đủ) với API data
     * @param year Năm cần lấy
     * @param countryCode Mã quốc gia
     * @return Result chứa danh sách ngày lễ hoặc error
     */
    suspend fun getPublicHolidays(year: Int, countryCode: String): Result<List<Holiday>> {
        return try {
            // Đối với Việt Nam, sử dụng local data đầy đủ và merge với API
            if (countryCode == "VN") {
                val localHolidays = VietnameseHolidays.getHolidaysForYear(year)
                
                // Thử lấy từ API để merge (nếu API có thêm ngày lễ khác)
                return try {
                    val response = apiService.getPublicHolidays(year, countryCode)
                    if (response.isSuccessful && response.body() != null) {
                        val apiHolidays = response.body()!!
                        val mergedHolidays = VietnameseHolidays.mergeWithApiHolidays(apiHolidays, year)
                        Result.success(mergedHolidays)
                    } else {
                        // Nếu API lỗi, vẫn trả về local data
                        Result.success(localHolidays)
                    }
                } catch (e: Exception) {
                    // Nếu API lỗi, vẫn trả về local data
                    Result.success(localHolidays)
                }
            }
            
            // Đối với các quốc gia khác, dùng API
            val response = apiService.getPublicHolidays(year, countryCode)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch holidays: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Lấy danh sách các ngày lễ sắp tới của một quốc gia
     * @param countryCode Mã quốc gia
     * @return Result chứa danh sách ngày lễ hoặc error
     */
    suspend fun getNextPublicHolidays(countryCode: String): Result<List<Holiday>> {
        return try {
            val response = apiService.getNextPublicHolidays(countryCode)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch next holidays: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Lấy danh sách các ngày lễ toàn cầu sắp tới
     * @return Result chứa danh sách ngày lễ hoặc error
     */
    suspend fun getNextPublicHolidaysWorldwide(): Result<List<Holiday>> {
        return try {
            val response = apiService.getNextPublicHolidaysWorldwide()
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch worldwide holidays: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

