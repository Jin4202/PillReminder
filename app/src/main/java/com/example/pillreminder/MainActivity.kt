package com.example.pillreminder

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pillreminder.reminder.Reminder
import com.example.pillreminder.reminder.ReminderBroadcastReceiver
import com.example.pillreminder.reminder.createNotificationChannel
import com.example.pillreminder.reminder.scheduleReminder
import java.time.DayOfWeek
import java.time.LocalTime

val sampleDailyReminder1 = Reminder(
    "Vitamin A",
    LocalTime.of(10, 22),
    DayOfWeek.entries.toSet()
)

val sampleDailyReminder2 = Reminder(
    "Vitamin B",
    LocalTime.of(9, 0),
    DayOfWeek.entries.toSet()
)

val sampleDailyReminder3 = Reminder(
    "Vitamin C",
    LocalTime.of(16, 0),
    DayOfWeek.entries.toSet()
)

val sampleWeeklyReminder = Reminder(
    "Aspirin",
    LocalTime.of(12, 0),
    setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
)

val reminders = listOf(sampleDailyReminder1, sampleDailyReminder2, sampleDailyReminder3, sampleWeeklyReminder)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()
        createNotificationChannel(this)
        enableEdgeToEdge()

        setContent {
            reminders.forEachIndexed { index, reminder ->
                scheduleReminder(this, reminder.pillName, reminder.time, reminder.daysOfWeek, index)
            }
            MainScreen(reminders = reminders)
            TestReminderButton(this)
        }
        createNotificationChannel(this)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }
}

@Composable
fun TestReminderButton(context: Context) {
    Button(onClick = {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Android 12+ check for exact alarm permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
            return@Button
        }

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("PILL_NAME", "Test Pill")
            putExtra("NOTIFICATION_ID", 12345)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            12345,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + 5000 // 5 seconds from now

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }) {
        Text("Send Test Reminder in 5 sec")
    }
}
