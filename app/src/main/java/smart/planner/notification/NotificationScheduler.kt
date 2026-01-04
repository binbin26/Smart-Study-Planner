package smart.planner.notification

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object NotificationScheduler {

    fun scheduleTest(context: Context, taskId: String, title: String) {

        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra("task_id", taskId)
            putExtra("title", title)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode(), // ⚠️ nhớ cái này
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAt = System.currentTimeMillis() + 60_000 // 1 phút

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            pendingIntent
        )
    }

    // ✅ THÊM HÀM NÀY
    fun cancel(context: Context, taskId: String) {

        // 1️⃣ Cancel notification (nếu đã hiện)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(taskId.hashCode())

        // 2️⃣ Cancel alarm
        val intent = Intent(context, TaskReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode(), // ⚠️ PHẢI GIỐNG schedule
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)
    }
}
