package smart.planner.utils

import java.util.Calendar
import java.util.GregorianCalendar

/**
 * Helper class để tính toán ngày Tết Âm Lịch (Lunar New Year)
 * Tết Âm Lịch thường rơi vào khoảng cuối tháng 1 đến giữa tháng 2 dương lịch
 */
object LunarCalendarHelper {
    
    /**
     * Danh sách ngày Tết Âm Lịch từ 2020-2030 (ngày mùng 1 Tết)
     * Format: YYYY-MM-DD
     */
    private val tetDates = mapOf(
        2020 to "2020-01-25",
        2021 to "2021-02-12",
        2022 to "2022-02-01",
        2023 to "2023-01-22",
        2024 to "2024-02-10",
        2025 to "2025-01-29",
        2026 to "2026-02-17",
        2027 to "2027-02-06",
        2028 to "2028-01-26",
        2029 to "2029-02-13",
        2030 to "2030-02-03"
    )
    
    /**
     * Lấy ngày Tết Âm Lịch cho một năm cụ thể
     * @param year Năm dương lịch
     * @return Ngày Tết dưới dạng String (YYYY-MM-DD) hoặc null nếu không tìm thấy
     */
    fun getTetDate(year: Int): String? {
        return tetDates[year]
    }
    
    /**
     * Lấy ngày Tết Âm Lịch cho năm hiện tại
     * @return Ngày Tết dưới dạng String (YYYY-MM-DD) hoặc null nếu không tìm thấy
     */
    fun getCurrentYearTetDate(): String? {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return getTetDate(currentYear)
    }
    
    /**
     * Lấy ngày Tết Âm Lịch cho năm tiếp theo
     * @return Ngày Tết dưới dạng String (YYYY-MM-DD) hoặc null nếu không tìm thấy
     */
    fun getNextYearTetDate(): String? {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return getTetDate(currentYear + 1)
    }
    
    /**
     * Kiểm tra xem một ngày có phải là Tết Âm Lịch không
     * @param date Ngày cần kiểm tra (YYYY-MM-DD)
     * @return true nếu là Tết, false nếu không
     */
    fun isTetDate(date: String): Boolean {
        val year = date.substring(0, 4).toIntOrNull() ?: return false
        val tetDate = getTetDate(year) ?: return false
        
        // So sánh ngày (chỉ so sánh tháng và ngày, không cần chính xác năm)
        val dateParts = date.split("-")
        val tetParts = tetDate.split("-")
        
        return dateParts[1] == tetParts[1] && dateParts[2] == tetParts[2]
    }
    
    /**
     * Tính toán ngày Tết cho năm bất kỳ (fallback method)
     * Sử dụng công thức ước tính dựa trên chu kỳ 19 năm
     */
    fun estimateTetDate(year: Int): String {
        // Công thức ước tính: Tết thường rơi vào khoảng 21/1 - 20/2
        // Sử dụng công thức Metonic cycle để ước tính
        val baseYear = 2024
        val baseTet = "2024-02-10"
        
        // Tính số năm chênh lệch
        val yearDiff = year - baseYear
        
        // Ước tính ngày Tết (không chính xác 100%, chỉ để tham khảo)
        // Trong thực tế, nên sử dụng thư viện lịch âm chuyên dụng
        val calendar = GregorianCalendar(year, Calendar.JANUARY, 20)
        
        // Điều chỉnh dựa trên năm
        val adjustment = (yearDiff % 19) * 11 % 30
        calendar.add(Calendar.DAY_OF_MONTH, adjustment)
        
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        return String.format("%04d-%02d-%02d", year, month, day)
    }
}

