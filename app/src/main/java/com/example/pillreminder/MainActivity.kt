package com.example.pillreminder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pillreminder.reminder.Reminder
import com.example.pillreminder.reminder.createNotificationChannel
import com.example.pillreminder.reminder.scheduleReminder
import java.time.DayOfWeek
import java.time.LocalTime

val sampleDailyReminder1 = Reminder(
    "Vitamin A",
    LocalTime.of(10, 22),
    DayOfWeek.entries
)

val sampleDailyReminder2 = Reminder(
    "Vitamin B",
    LocalTime.of(9, 0),
    DayOfWeek.entries
)

val sampleDailyReminder3 = Reminder(
    "Vitamin C",
    LocalTime.of(16, 0),
    DayOfWeek.entries
)

val sampleWeeklyReminder = Reminder(
    "Aspirin",
    LocalTime.of(12, 0),
    listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
)

val reminders = listOf(sampleDailyReminder1, sampleDailyReminder2, sampleDailyReminder3, sampleWeeklyReminder)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        scheduleReminder(this, "Vitamin C", LocalTime.of(9, 30))

        setContent {
            MainScreen(reminders = reminders)
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(onClick = { testSendNotification(this, "Vitamin C") }) {
//                Text("Test Notification")
//            }
        }
        createNotificationChannel(this)


        testSendNotification(this, "Test Pill")
    }
}

fun testSendNotification(context: Context, pillName: String) {
    val notification = NotificationCompat.Builder(context, "pill_reminder_channel")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Pill Reminder")
        .setContentText("Time to take your $pillName!")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    val notificationManager = NotificationManagerCompat.from(context)
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    notificationManager.notify(System.currentTimeMillis().toInt(), notification)
}

