package com.example.pillreminder.model.reminder

import android.Manifest
import android.R
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.time.DayOfWeek
import java.util.Calendar

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pillName = intent.getStringExtra("PILL_NAME") ?: return
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)
        val dayOfWeekValue = intent.getIntExtra("DAY_OF_WEEK", -1)
        val hour = intent.getIntExtra("HOUR", -1)
        val minute = intent.getIntExtra("MINUTE", -1)
        val repeat = intent.getBooleanExtra("REPEAT", false)

        Log.d("ReminderTrigger", "Alarm triggered! Pill: $pillName, ID: $notificationId")

        val notification = NotificationCompat.Builder(context, "pill_reminder_channel")
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle("Pill Reminder")
            .setContentText("Time to take your $pillName!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED)
            return
        notificationManager.notify(notificationId, notification)

        // Repeating Alarm Logic
        if (repeat && dayOfWeekValue in Calendar.SUNDAY..Calendar.SATURDAY && hour in 0..23 && minute in 0..59) {
            val now = System.currentTimeMillis()
            val nextCal = Calendar.getInstance().apply {
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                set(Calendar.DAY_OF_WEEK, dayOfWeekValue)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                if (timeInMillis <= now) add(Calendar.WEEK_OF_YEAR, 1)
            }
            val nextMillis = nextCal.timeInMillis

            val newIntent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                putExtra("PILL_NAME", pillName)
                putExtra("NOTIFICATION_ID", notificationId)
                putExtra("DAY_OF_WEEK", dayOfWeekValue)
                putExtra("HOUR", hour)
                putExtra("MINUTE", minute)
                putExtra("REPEAT", true)
            }

            val requestCode = makeRequestCode(
                notificationId,
                pillName,
                dayOfWeekValue,
                hour,
                minute
            )

            val pi = PendingIntent.getBroadcast(
                context,
                requestCode,
                newIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || am.canScheduleExactAlarms()
            if (canExact) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextMillis, pi)
            } else {
                am.set(AlarmManager.RTC_WAKEUP, nextMillis, pi)
            }

            Log.d("ReminderReschedule", "Next alarm for '$pillName' → $hour:$minute (DoW=$dayOfWeekValue) @ $nextMillis")
        }
    }
}