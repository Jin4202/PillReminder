package com.example.pillreminder.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

fun createNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        "pill_reminder_channel",
        "Pill Reminder",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Channel for pill reminders"
    }

    val notificationManager: NotificationManager =
        context.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(channel)
}
