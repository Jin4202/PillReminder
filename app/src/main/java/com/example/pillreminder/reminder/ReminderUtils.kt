package com.example.pillreminder.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import java.time.DayOfWeek
import java.time.LocalTime

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

fun scheduleReminder(context: Context, pillName: String, time: LocalTime, daysOfWeek: Set<DayOfWeek>, reminderId: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
        putExtra("PILL_NAME", pillName)
        putExtra("NOTIFICATION_ID", reminderId)
    }

    daysOfWeek.forEach { dayOfWeek ->
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.DAY_OF_WEEK, dayOfWeek.toCalendarDayOfWeek())
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 7)
            }
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (pillName + dayOfWeek.name).hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
    }
}

fun DayOfWeek.toCalendarDayOfWeek(): Int {
    return when (this) {
        DayOfWeek.MONDAY -> Calendar.MONDAY
        DayOfWeek.TUESDAY -> Calendar.TUESDAY
        DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
        DayOfWeek.THURSDAY -> Calendar.THURSDAY
        DayOfWeek.FRIDAY -> Calendar.FRIDAY
        DayOfWeek.SATURDAY -> Calendar.SATURDAY
        DayOfWeek.SUNDAY -> Calendar.SUNDAY
    }
}