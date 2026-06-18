package com.example.pillreminder.model.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_USER_UNLOCKED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                RescheduleAlarmsWork.enqueue(context)
            }
        }
    }
}

class RescheduleAlarmsWork(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val reminderManager = ReminderManager.getInstance()
        reminderManager.loadFromDataStore(applicationContext)
        reminderManager.getReminders().forEach { scheduleReminder(applicationContext, it) }
        return Result.success()
    }

    companion object {
        fun enqueue(ctx: Context) {
            WorkManager.getInstance(ctx).enqueueUniqueWork(
                "reschedule-alarms",
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<RescheduleAlarmsWork>().build()
            )
        }
    }
}