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
     */
    fun loadHolidays(countryCode: String = "VN") {
        viewModelScope.launch {
            _holidaysLoading.value = true
            _holidaysError.value = null
            
            holidayRepository.getPublicHolidaysForCurrentYear(countryCode)
                .onSuccess { holidays ->
                    _holidays.value = holidays
                    _holidaysLoading.value = false
                }
                .onFailure { error ->
                    _holidaysError.value = error.message
                    _holidaysLoading.value = false
                }
        }
    }
    
    /**
     * Load câu trích dẫn ngẫu nhiên
     */
    fun loadRandomQuote() {
        viewModelScope.launch {
            _quoteLoading.value = true
            _quoteError.value = null
            
            quoteRepository.getRandomQuote()
                .onSuccess { quote ->
                    _quote.value = quote
                    _quoteLoading.value = false
                }
                .onFailure { error ->
                    _quoteError.value = error.message
                    _quoteLoading.value = false
                }
        }
    }
    
    /**
     * Load câu trích dẫn động viên học tập
     */
    fun loadMotivationalQuote() {
        viewModelScope.launch {
            _quoteLoading.value = true
            _quoteError.value = null
            
            quoteRepository.getMotivationalStudyQuote()
                .onSuccess { quote ->
                    _quote.value = quote
                    _quoteLoading.value = false
                }
                .onFailure { error ->
                    _quoteError.value = error.message
                    _quoteLoading.value = false
                }
        }
    }
}

