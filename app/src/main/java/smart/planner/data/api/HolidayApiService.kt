package smart.planner.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import smart.planner.data.model.Holiday

/**
 * API Service interface cho Holiday API (date.nager.at/Api)
 * Cung cấp thông tin về các ngày lễ để hỗ trợ lập kế hoạch học tập
 */
interface HolidayApiService {
    
    /**
     * Lấy danh sách các ngày lễ của một quốc gia trong một năm cụ thể
     * @param year Năm cần lấy (ví dụ: 2024)
     * @param countryCode Mã quốc gia (ví dụ: VN cho Việt Nam, US cho Mỹ)
     * @return Danh sách các ngày lễ
     */
    @GET("PublicHolidays/{year}/{countryCode}")
    suspend fun getPublicHolidays(
        @Path("year") year: Int,
        @Path("countryCode") countryCode: String
    ): Response<List<Holiday>>
    
    /**
     * Lấy danh sách các ngày lễ của một quốc gia trong năm hiện tại
     * @param countryCode Mã quốc gia
     * @return Danh sách các ngày lễ
     */
    @GET("NextPublicHolidays/{countryCode}")
    suspend fun getNextPublicHolidays(
        @Path("countryCode") countryCode: String
    ): Response<List<Holiday>>
    
    /**
     * Lấy danh sách các ngày lễ toàn cầu trong năm hiện tại
     * @return Danh sách các ngày lễ
     */
    @GET("NextPublicHolidaysWorldwide")
    suspend fun getNextPublicHolidaysWorldwide(): Response<List<Holiday>>
}

