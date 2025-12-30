package smart.planner.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import smart.planner.data.model.Holiday
import smart.planner.data.model.QuoteResponse
import smart.planner.data.repository.HolidayRepository
import smart.planner.data.repository.QuoteRepository

/**
 * Màn hình test để hiển thị Quotes và Holidays
 * Dùng để kiểm tra API calls và hiển thị dữ liệu
 */
@Composable
fun TestScreen() {
    val holidayRepository = remember { HolidayRepository() }
    val quoteRepository = remember { QuoteRepository() }
    val coroutineScope = rememberCoroutineScope()
    
    var holidays by remember { mutableStateOf<List<Holiday>>(emptyList()) }
    var currentQuote by remember { mutableStateOf<QuoteResponse?>(null) }
    var isLoadingHolidays by remember { mutableStateOf(false) }
    var isLoadingQuote by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Load dữ liệu khi màn hình được tạo
    LaunchedEffect(Unit) {
        // Load holidays
        coroutineScope.launch {
            isLoadingHolidays = true
            errorMessage = null
            holidayRepository.getPublicHolidaysForCurrentYear("VN")
                .onSuccess { 
                    holidays = it.sortedBy { it.date }
                    isLoadingHolidays = false
                }
                .onFailure { 
                    errorMessage = "Lỗi load ngày lễ: ${it.message}"
                    isLoadingHolidays = false
                }
        }
        
        // Load quote
        coroutineScope.launch {
            isLoadingQuote = true
            quoteRepository.getRandomQuote()
                .onSuccess { 
                    currentQuote = it
                    isLoadingQuote = false
                }
                .onFailure { 
                    errorMessage = "Lỗi load quote: ${it.message}"
                    isLoadingQuote = false
                }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Smart Study Planner - Test Screen",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        
        // Quote Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💬 Random Quote",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isLoadingQuote = true
                                quoteRepository.getRandomQuote()
                                    .onSuccess { 
                                        currentQuote = it
                                        isLoadingQuote = false
                                    }
                                    .onFailure { 
                                        errorMessage = "Lỗi load quote: ${it.message}"
                                        isLoadingQuote = false
                                    }
                            }
                        },
                        enabled = !isLoadingQuote
                    ) {
                        if (isLoadingQuote) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Refresh")
                        }
                    }
                }
                
                if (isLoadingQuote) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (currentQuote != null) {
                    Text(
                        text = "\"${currentQuote!!.content}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "— ${currentQuote!!.author}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                } else {
                    Text(
                        text = "Không có quote",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        
        // Holidays Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📅 Ngày Lễ Việt Nam (${holidays.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isLoadingHolidays = true
                                errorMessage = null
                                holidayRepository.getPublicHolidaysForCurrentYear("VN")
                                    .onSuccess { 
                                        holidays = it.sortedBy { it.date }
                                        isLoadingHolidays = false
                                    }
                                    .onFailure { 
                                        errorMessage = "Lỗi load ngày lễ: ${it.message}"
                                        isLoadingHolidays = false
                                    }
                            }
                        },
                        enabled = !isLoadingHolidays
                    ) {
                        if (isLoadingHolidays) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Refresh")
                        }
                    }
                }
                
                if (isLoadingHolidays) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    if (holidays.isEmpty()) {
                        Text(
                            text = "Không có ngày lễ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(holidays) { holiday ->
                                HolidayItem(holiday = holiday)
                            }
                        }
                    }
                }
            }
        }
        
        // Error message
        if (errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "❌ $errorMessage",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun HolidayItem(holiday: Holiday) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = holiday.localName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = holiday.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = holiday.date,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

