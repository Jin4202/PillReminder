package com.example.pillreminder.model.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Calendar

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
            val today = LocalDate.now()
            val startDate = rangeFrom.coerceAtLeast(today)
            val daysToAdd = (dayOfWeek.value - startDate.dayOfWeek.value + 7) % 7
            val firstOccurrence = startDate.plusDays(daysToAdd.toLong())

            if (rangeTo != null && firstOccurrence.isAfter(rangeTo)) {
                return@forEach
            }

            val triggerAt = nextOccurrenceMillis(dayOfWeek, time.hour, time.minute)

            val requestCode = makeRequestCode(
                reminderId,
                pillName,
                toCalendarDow(dayOfWeek),
                time.hour,
                time.minute
            )

            val intent = Intent(intentBase).apply {
                putExtra("DAY_OF_WEEK", toCalendarDow(dayOfWeek))
                putExtra("HOUR", time.hour)
                putExtra("MINUTE", time.minute)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            Log.d("ReminderSchedule", "Scheduled '$pillName' at $dayOfWeek ${time.hour}:${time.minute} → $triggerAt")

            val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
            if (canExact) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
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
    val reminderId = reminder.getId()

    daysOfWeek.forEach { dayOfWeek ->
        times.forEach { time ->
            val requestCode = makeRequestCode(
                reminderId,
                pillName,
                toCalendarDow(dayOfWeek),
                time.hour,
                time.minute
            )
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

fun toCalendarDow(dayOfWeek: DayOfWeek): Int =
    when (dayOfWeek) {
        DayOfWeek.SUNDAY -> Calendar.SUNDAY   // 1
        DayOfWeek.MONDAY -> Calendar.MONDAY
        DayOfWeek.TUESDAY -> Calendar.TUESDAY
        DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
        DayOfWeek.THURSDAY -> Calendar.THURSDAY
        DayOfWeek.FRIDAY -> Calendar.FRIDAY
        DayOfWeek.SATURDAY -> Calendar.SATURDAY // 7
    }

fun nextOccurrenceMillis(dow: DayOfWeek, hour: Int, minute: Int, nowMillis: Long = System.currentTimeMillis()): Long {
    val cal = Calendar.getInstance().apply {
        timeInMillis = nowMillis
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        set(Calendar.DAY_OF_WEEK, toCalendarDow(dow))
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }
    if (cal.timeInMillis <= nowMillis) {
        cal.add(Calendar.WEEK_OF_YEAR, 1)
    }
    return cal.timeInMillis
}

fun makeRequestCode(
    reminderId: Int,
    pillName: String,
    calDow: Int,
    hour: Int,
    minute: Int
): Int {
    return "$reminderId#$pillName#$calDow#$hour:$minute".hashCode()
}