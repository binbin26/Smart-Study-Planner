package smart.planner.utils

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import smart.planner.R
import smart.planner.ui.screen.TaskDetailActivity
import kotlin.jvm.java

class TaskReminderReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("taskId", -1)
        val action = intent.getStringExtra("action")

        when (action) {
            "COMPLETE" -> {
                // TODO: xử lý đánh dấu task hoàn thành (update DB, UI...)
                Toast.makeText(context, "Task #$taskId đã hoàn thành!", Toast.LENGTH_SHORT).show()
                return
            }
            "DETAIL" -> {
                // Mở màn hình chi tiết
                val detailIntent = Intent(context, TaskDetailActivity::class.java).apply {
                    putExtra("taskId", taskId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(detailIntent)
                return
            }
        }

        // Nếu không có action, hiển thị notification bình thường
        val detailIntent = Intent(context, TaskDetailActivity::class.java).apply {
            putExtra("taskId", taskId)
        }
        val pendingDetailIntent = PendingIntent.getActivity(
            context,
            taskId,
            detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // PendingIntent cho action "Hoàn thành"
        val completeIntent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra("taskId", taskId)
            putExtra("action", "COMPLETE")
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            taskId + 1000, // khác requestCode
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // PendingIntent cho action "Xem chi tiết"
        val detailActionIntent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra("taskId", taskId)
            putExtra("action", "DETAIL")
        }
        val detailActionPendingIntent = PendingIntent.getBroadcast(
            context,
            taskId + 2000,
            detailActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "task_reminder")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Task Reminder")
            .setContentText("Task #$taskId sắp đến hạn!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingDetailIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.ic_check, "Hoàn thành", completePendingIntent)
            .addAction(R.drawable.ic_info, "Xem chi tiết", detailActionPendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(taskId, notification)
    }
}

