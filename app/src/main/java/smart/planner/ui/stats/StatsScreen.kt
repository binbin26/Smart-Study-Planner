package smart.planner.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import smart.planner.data.entity.Task
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun StatsScreen(tasks: List<Task>) {

    val total = tasks.size
    val doneCount = tasks.count { it.isDone }
    val percentDone = if (total > 0) (doneCount * 100 / total) else 0
    val progress = if (total > 0) doneCount.toFloat() / total else 0f

    val bySubject = tasks.groupBy { it.subjectName.ifBlank { "Unknown" } }
    val trend7Days = buildTrendLast7Days(tasks)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Text(
                text = "📊 Stats",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // % hoàn thành
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "✅ Hoàn thành",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Done: $doneCount / $total  ($percentDone%)")
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Tasks theo subject
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📚 Tasks theo môn",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))

                    if (bySubject.isEmpty()) {
                        Text("Chưa có task")
                    } else {
                        bySubject.entries
                            .sortedByDescending { it.value.size }
                            .forEach { (subjectName, list) ->
                                val done = list.count { it.isDone }
                                Text("• $subjectName: ${list.size} tasks (done $done)")
                                Spacer(Modifier.height(6.dp))
                            }
                    }
                }
            }
        }

        // Trend 7 ngày (text)
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🗓 Trend 7 ngày gần nhất",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))

                    if (trend7Days.isEmpty()) {
                        Text("Chưa có dữ liệu")
                    } else {
                        trend7Days.forEachIndexed { index, row ->
                            Text("${row.dayLabel}: ${row.count} tasks")
                            if (index != trend7Days.lastIndex) {
                                Divider(modifier = Modifier.padding(vertical = 6.dp))
                            }
                        }
                    }
                }
            }
        }
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

    for (i in 6 downTo 0) {
        val date = today.minusDays(i.toLong())
        val label = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        val count = mapCount[date] ?: 0
        rows.add(TrendRow(label, count))
    }

    return rows
}

private data class TrendRow(
    val dayLabel: String,
    val count: Int
)
