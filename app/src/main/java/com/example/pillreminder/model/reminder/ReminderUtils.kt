package com.example.pillreminder.model.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.time.LocalDate

fun createNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        "pill_reminder_channel",
        "Pill Reminder Channel",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Channel for pill reminder notifications"
    }
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(channel)
}

fun scheduleReminder(context: Context, reminder: Reminder) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val pillName = reminder.pillName
    val times = reminder.times
    val daysOfWeek = reminder.daysOfWeek
    val reminderId = reminder.getId()
    val rangeFrom = reminder.rangeFrom ?: LocalDate.now()
    val rangeTo = reminder.rangeTo

    val intentBase = Intent(context, ReminderBroadcastReceiver::class.java).apply {
        putExtra("PILL_NAME", pillName)
        putExtra("NOTIFICATION_ID", reminderId)
        putExtra("REPEAT", true)
    }

    daysOfWeek.forEach { dayOfWeek ->
        times.forEach { time ->
            val calendar = Calendar.getInstance().apply {
                val today = LocalDate.now()
                val startDate = rangeFrom.coerceAtLeast(today)
                val daysToAdd = (dayOfWeek.value - startDate.dayOfWeek.value + 7) % 7
                val firstOccurrence = startDate.plusDays(daysToAdd.toLong())

                if (rangeTo != null && firstOccurrence.isAfter(rangeTo)) return@forEach

                set(Calendar.YEAR, firstOccurrence.year)
                set(Calendar.MONTH, firstOccurrence.monthValue - 1)
                set(Calendar.DAY_OF_MONTH, firstOccurrence.dayOfMonth)
                set(Calendar.HOUR_OF_DAY, time.hour)
                set(Calendar.MINUTE, time.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            var triggerAtMillis = calendar.timeInMillis
            val now = System.currentTimeMillis()

            if (triggerAtMillis <= now) {
                calendar.add(Calendar.DAY_OF_YEAR, 7)
                triggerAtMillis = calendar.timeInMillis
                Log.w("ReminderSchedule", "Past time detected. Rescheduled to next week: $dayOfWeek $time → $triggerAtMillis")
            }

            val requestCode = (pillName + dayOfWeek.name + time.toString()).hashCode()

            val intent = Intent(intentBase).apply {
                putExtra("DAY_OF_WEEK", dayOfWeek.value)
                putExtra("HOUR", time.hour)
                putExtra("MINUTE", time.minute)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            Log.d("ReminderSchedule", "Scheduled alarm for '$pillName' at $dayOfWeek $time → timeInMillis=${calendar.timeInMillis}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.parse("package:" + context.packageName)
                    }
                    context.startActivity(intent)
                    return
                }
            }
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
}

fun cancelReminder(context: Context, reminder: Reminder) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
        putExtra("PILL_NAME", reminder.pillName)
        putExtra("NOTIFICATION_ID", reminder.getId())
    }

    val pillName = reminder.pillName
    val times = reminder.times
    val daysOfWeek = reminder.daysOfWeek

    daysOfWeek.forEach { dayOfWeek ->
        times.forEach { time ->
            val requestCode = (pillName + dayOfWeek.name + time.toString()).hashCode()

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
        }
    }

}
