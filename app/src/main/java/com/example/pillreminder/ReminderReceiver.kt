package com.example.pillreminder

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class ReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val pillName = inputData.getString("PILL_NAME") ?: "Unknown Pill"
        sendNotification(applicationContext, pillName)
        return Result.success()
    }

    private fun sendNotification(context: Context, pillName: String) {
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}

fun scheduleReminder(context: Context, pillName: String, time: LocalTime) {
    val now = LocalDateTime.now()
    val reminderDateTime = now.withHour(time.hour).withMinute(time.minute).withSecond(0)

    val delay = Duration.between(now, reminderDateTime).toMillis()
    if (delay < 0) return // Prevent scheduling past times

    val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(workDataOf("PILL_NAME" to pillName))
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)
}