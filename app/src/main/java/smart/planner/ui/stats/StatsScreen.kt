package smart.planner.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import smart.planner.data.model.Subject
import smart.planner.data.model.Task
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    tasks: List<Task>,
    subjects: List<Subject>,
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Thống kê") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->

        val total = tasks.size
        val doneCount = tasks.count { it.status == "DONE" }
        val pendingCount = total - doneCount
        val percentDone = if (total > 0) (doneCount * 100 / total) else 0

        // Map subjectId -> Subject
        val subjectMap = subjects.associateBy { it.id.toString() }

        // Group tasks by subject
        val tasksBySubject = tasks.groupBy { task ->
            subjectMap[task.subjectId]
        }

        // Trend 7 days
        val trend7Days = buildTrendLast7Days(tasks)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item { Spacer(Modifier.height(4.dp)) }

            // 1. DONUT CHART - Hoàn thành
            item {
                CompletionDonutCard(
                    total = total,
                    done = doneCount,
                    pending = pendingCount,
                    percent = percentDone
                )
            }

            // 2. BAR CHART - Trend 7 ngày
            item {
                Trend7DaysBarChart(trend7Days)
            }

            // 3. SUBJECT LIST - Tasks theo môn
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "📚",
                                fontSize = 24.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "Tasks theo môn",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        if (tasksBySubject.isEmpty()) {
                            Text(
                                "Chưa có task nào",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            tasksBySubject.entries
                                .sortedByDescending { it.value.size }
                                .forEach { (subject, taskList) ->
                                    if (subject != null) {
                                        SubjectTaskRow(
                                            subject = subject,
                                            taskList = taskList
                                        )
                                        Spacer(Modifier.height(12.dp))
                                    }
                                }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun CompletionDonutCard(
    total: Int,
    done: Int,
    pending: Int,
    percent: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "✅",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Hoàn thành",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Donut chart (Circular Progress)
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { if (total > 0) done.toFloat() / total else 0f },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 12.dp,
                        color = Color(0xFF4CAF50),
                        trackColor = Color(0xFFE0E0E0)
                    )
                    Text(
                        text = "$percent%",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.width(16.dp))

                // Stats column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatItem("Tổng", total.toString(), Color(0xFF2196F3))
                    StatItem("Đã xong", done.toString(), Color(0xFF4CAF50))
                    StatItem("Chờ", pending.toString(), Color(0xFFFF9800))
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun Trend7DaysBarChart(trend: List<TrendRow>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "📈",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Xu hướng 7 ngày",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(20.dp))

            if (trend.isEmpty()) {
                Text(
                    "Chưa có dữ liệu",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val maxCount = trend.maxOfOrNull { it.count } ?: 1

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    trend.forEachIndexed { index, row ->
                        BarColumn(
                            label = row.dayLabel,
                            count = row.count,
                            maxCount = maxCount,
                            isToday = index == trend.lastIndex
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BarColumn(
    label: String,
    count: Int,
    maxCount: Int,
    isToday: Boolean
) {
    val heightFraction = if (maxCount > 0) (count.toFloat() / maxCount) else 0f
    val barColor = if (isToday) Color(0xFF2196F3) else Color(0xFF9E9E9E)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.width(40.dp)
    ) {
        if (count > 0) {
            Text(
                text = count.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .width(28.dp)
                .fillMaxHeight(heightFraction.coerceAtLeast(0.05f))
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(barColor)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            color = if (isToday) MaterialTheme.colorScheme.primary
                   else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SubjectTaskRow(subject: Subject, taskList: List<Task>) {
    val done = taskList.count { it.status == "DONE" }
    val total = taskList.size
    val progress = if (total > 0) done.toFloat() / total else 0f
    val subjectColor = try {
        Color(android.graphics.Color.parseColor(subject.color ?: "#3F51B5"))
    } catch (e: Exception) {
        Color(0xFF3F51B5)
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(subjectColor)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = "$done/$total",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = subjectColor,
            trackColor = subjectColor.copy(alpha = 0.2f)
        )
    }
}

/* ===================== HELPER ===================== */

private fun buildTrendLast7Days(tasks: List<Task>): List<TrendRow> {
    val zone = ZoneId.systemDefault()
    val today = LocalDate.now(zone)

    val mapCount = tasks.groupingBy { task ->
        Instant.ofEpochMilli(task.createdAt)
            .atZone(zone)
            .toLocalDate()
    }.eachCount()

    val rows = mutableListOf<TrendRow>()
    val vietnameseDays = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")

    for (i in 6 downTo 0) {
        val date = today.minusDays(i.toLong())
        val dayOfWeekIndex = (date.dayOfWeek.value % 7) // Monday=1 -> index 1, Sunday=7 -> index 0
        val label = vietnameseDays[dayOfWeekIndex]
        val count = mapCount[date] ?: 0
        rows.add(TrendRow(label, count))
    }

    return rows
}

private data class TrendRow(
    val dayLabel: String,
    val count: Int
)
