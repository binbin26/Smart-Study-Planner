package smart.planner.data.local

import smart.planner.data.model.Holiday
import smart.planner.utils.LunarCalendarHelper
import java.util.Calendar

/**
 * Danh sách đầy đủ các ngày lễ chính thức của Việt Nam
 * Bao gồm cả ngày lễ cố định và ngày lễ theo lịch âm
 * 
 * Nguồn: Theo quy định của Chính phủ Việt Nam
 */
object VietnameseHolidays {
    
    /**
     * Lấy danh sách đầy đủ ngày lễ Việt Nam cho một năm cụ thể
     * Bao gồm cả ngày lễ cố định và ngày lễ theo lịch âm
     */
    fun getHolidaysForYear(year: Int): List<Holiday> {
        val holidays = mutableListOf<Holiday>()
        
        // 1. Tết Dương lịch (1/1) - Ngày lễ cố định
        holidays.add(
            Holiday(
                date = "$year-01-01",
                localName = "Tết Dương lịch",
                name = "New Year's Day",
                countryCode = "VN",
                fixed = true,
                global = true,
                counties = null,
                launchYear = null,
                types = listOf("Public", "National")
            )
        )
        
        // 2. Tết Nguyên Đán (Tết Âm Lịch) - Ngày lễ theo lịch âm
        val tetDate = LunarCalendarHelper.getTetDate(year)
        if (tetDate != null) {
            holidays.add(
                Holiday(
                    date = tetDate,
                    localName = "Tết Nguyên Đán",
                    name = "Lunar New Year",
                    countryCode = "VN",
                    fixed = false,
                    global = false,
                    counties = null,
                    launchYear = null,
                    types = listOf("Public", "Religious", "Traditional")
                )
            )
        }
        
        // 3. Giỗ Tổ Hùng Vương (10/3 Âm lịch) - Ngày lễ theo lịch âm
        val hungKingsDate = getHungKingsDate(year)
        if (hungKingsDate != null) {
            holidays.add(
                Holiday(
                    date = hungKingsDate,
                    localName = "Giỗ Tổ Hùng Vương",
                    name = "Hung Kings Festival",
                    countryCode = "VN",
                    fixed = false,
                    global = false,
                    counties = null,
                    launchYear = null,
                    types = listOf("Public", "Traditional", "National")
                )
            )
        }
        
        // 4. Ngày Giải phóng miền Nam, thống nhất đất nước (30/4) - Ngày lễ cố định
        holidays.add(
            Holiday(
                date = "$year-04-30",
                localName = "Ngày Giải phóng miền Nam, thống nhất đất nước",
                name = "Liberation Day / Reunification Day",
                countryCode = "VN",
                fixed = true,
                global = false,
                counties = null,
                launchYear = 1975,
                types = listOf("Public", "National")
            )
        )
        
        // 5. Ngày Quốc tế Lao động (1/5) - Ngày lễ cố định
        holidays.add(
            Holiday(
                date = "$year-05-01",
                localName = "Ngày Quốc tế Lao động",
                name = "Labour Day",
                countryCode = "VN",
                fixed = true,
                global = true,
                counties = null,
                launchYear = null,
                types = listOf("Public", "International")
            )
        )
        
        // 6. Quốc khánh (2/9) - Ngày lễ cố định
        holidays.add(
            Holiday(
                date = "$year-09-02",
                localName = "Quốc khánh",
                name = "National Day",
                countryCode = "VN",
                fixed = true,
                global = false,
                counties = null,
                launchYear = 1945,
                types = listOf("Public", "National")
            )
        )
        
        return holidays.sortedBy { it.date }
    }
    
    /**
     * Tính ngày Giỗ Tổ Hùng Vương (10/3 Âm lịch) cho một năm
     * Giỗ Tổ thường rơi vào khoảng cuối tháng 3 đến đầu tháng 4 dương lịch
     */
    private fun getHungKingsDate(year: Int): String? {
        // Ngày Giỗ Tổ Hùng Vương (10/3 Âm lịch) chuyển sang dương lịch
        // Dữ liệu từ 2020-2030
        val hungKingsDates = mapOf(
            2020 to "2020-04-02",
            2021 to "2021-04-21",
            2022 to "2022-04-10",
            2023 to "2023-03-29",
            2024 to "2024-04-18",
            2025 to "2025-04-07",
            2026 to "2026-03-27",
            2027 to "2027-04-15",
            2028 to "2028-04-04",
            2029 to "2029-03-24",
            2030 to "2030-04-12"
        )
        
        return hungKingsDates[year]
    }
    
    /**
     * Merge danh sách holidays từ API với local data
     * Loại bỏ trùng lặp và ưu tiên local data cho ngày lễ Việt Nam
     */
    fun mergeWithApiHolidays(apiHolidays: List<Holiday>, year: Int): List<Holiday> {
        val localHolidays = getHolidaysForYear(year)
        val mergedHolidays = mutableListOf<Holiday>()
        
        // Thêm tất cả local holidays (ưu tiên)
        mergedHolidays.addAll(localHolidays)
        
        // Thêm holidays từ API nếu chưa có trong local
        apiHolidays.forEach { apiHoliday ->
            val isDuplicate = localHolidays.any { local ->
                local.date == apiHoliday.date ||
                (local.localName.contains("Tết", ignoreCase = true) && 
                 apiHoliday.localName.contains("Tết", ignoreCase = true)) ||
                (local.name == apiHoliday.name && local.date == apiHoliday.date)
            }
            
            if (!isDuplicate) {
                mergedHolidays.add(apiHoliday)
            }
        }
        
        return mergedHolidays.sortedBy { it.date }
    }
}

