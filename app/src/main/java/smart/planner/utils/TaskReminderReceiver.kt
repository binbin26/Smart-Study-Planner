package smart.planner.utils

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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

        val detailIntent = Intent(context, TaskDetailActivity::class.java).apply {
            putExtra("taskId", taskId)
        }

        val pendingDetailIntent = PendingIntent.getActivity(
            context,
            taskId,
            detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "task_reminder")
            .setSmallIcon(R.drawable.ic_notification) // icon của bạn
            .setContentTitle("Task Reminder")
            .setContentText("Bạn có một task sắp đến hạn!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingDetailIntent) // deep link
            .setAutoCancel(true)
            .build()

        val manager = NotificationManagerCompat.from(context)
        manager.notify(taskId, notification)
    }
}
