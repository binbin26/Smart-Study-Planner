package smart.planner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import smart.planner.data.model.Holiday
import smart.planner.data.model.QuoteResponse
import smart.planner.data.repository.HolidayRepository
import smart.planner.data.repository.QuoteRepository

/**
 * Example ViewModel để minh họa cách sử dụng API trong MVVM architecture
 * 
 * ViewModel này quản lý state và logic cho việc lấy dữ liệu từ API
 * Có thể sử dụng trong Compose UI hoặc traditional View system
 */
class ApiExampleViewModel : ViewModel() {
    
    private val holidayRepository = HolidayRepository()
    private val quoteRepository = QuoteRepository()
    
    // State cho Holidays
    private val _holidays = MutableStateFlow<List<Holiday>>(emptyList())
    val holidays: StateFlow<List<Holiday>> = _holidays.asStateFlow()
    
    private val _holidaysLoading = MutableStateFlow(false)
    val holidaysLoading: StateFlow<Boolean> = _holidaysLoading.asStateFlow()
    
    private val _holidaysError = MutableStateFlow<String?>(null)
    val holidaysError: StateFlow<String?> = _holidaysError.asStateFlow()
    
    // State cho Quote
    private val _quote = MutableStateFlow<QuoteResponse?>(null)
    val quote: StateFlow<QuoteResponse?> = _quote.asStateFlow()
    
    private val _quoteLoading = MutableStateFlow(false)
    val quoteLoading: StateFlow<Boolean> = _quoteLoading.asStateFlow()
    
    private val _quoteError = MutableStateFlow<String?>(null)
    val quoteError: StateFlow<String?> = _quoteError.asStateFlow()
    
    /**
     * Load danh sách ngày lễ của Việt Nam năm hiện tại
     * Sử dụng viewModelScope và đảm bảo state updates trên Main thread
     */
    fun loadHolidays(countryCode: String = "VN") {
        viewModelScope.launch {
            try {
                // Update loading state trên Main thread
                _holidaysLoading.value = true
                _holidaysError.value = null
                
                // Repository tự động sử dụng Dispatchers.IO cho network operations
                val result = holidayRepository.getPublicHolidaysForCurrentYear(countryCode)
                
                // Update state trên Main thread (viewModelScope mặc định là Main)
                if (result.isSuccess) {
                    _holidays.value = result.getOrNull() ?: emptyList()
                    _holidaysLoading.value = false
                } else {
                    _holidaysError.value = result.exceptionOrNull()?.message ?: "Unknown error"
                    _holidaysLoading.value = false
                }
            } catch (e: Exception) {
                // Extra safety: catch any unexpected errors
                _holidaysError.value = e.message ?: "Unexpected error occurred"
                _holidaysLoading.value = false
            }
        }
    }
    
    /**
     * Load câu trích dẫn ngẫu nhiên
     * Sử dụng viewModelScope và đảm bảo state updates trên Main thread
     */
    fun loadRandomQuote() {
        viewModelScope.launch {
            try {
                // Update loading state trên Main thread
                _quoteLoading.value = true
                _quoteError.value = null
                
                // Repository tự động sử dụng Dispatchers.IO cho operations
                val result = quoteRepository.getRandomQuote()
                
                // Update state trên Main thread (viewModelScope mặc định là Main)
                if (result.isSuccess) {
                    _quote.value = result.getOrNull()
                    _quoteLoading.value = false
                } else {
                    _quoteError.value = result.exceptionOrNull()?.message ?: "Unknown error"
                    _quoteLoading.value = false
                }
            } catch (e: Exception) {
                // Extra safety: catch any unexpected errors
                _quoteError.value = e.message ?: "Unexpected error occurred"
                _quoteLoading.value = false
            }
        }
    }
    
    /**
     * Load câu trích dẫn động viên học tập
     * Sử dụng viewModelScope và đảm bảo state updates trên Main thread
     */
    fun loadMotivationalQuote() {
        viewModelScope.launch {
            try {
                // Update loading state trên Main thread
                _quoteLoading.value = true
                _quoteError.value = null
                
                // Repository tự động sử dụng Dispatchers.IO cho operations
                val result = quoteRepository.getMotivationalStudyQuote()
                
                // Update state trên Main thread (viewModelScope mặc định là Main)
                if (result.isSuccess) {
                    _quote.value = result.getOrNull()
                    _quoteLoading.value = false
                } else {
                    _quoteError.value = result.exceptionOrNull()?.message ?: "Unknown error"
                    _quoteLoading.value = false
                }
            } catch (e: Exception) {
                // Extra safety: catch any unexpected errors
                _quoteError.value = e.message ?: "Unexpected error occurred"
                _quoteLoading.value = false
            }
        }
    }
}

