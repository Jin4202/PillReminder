package com.example.pillreminder.model.reminder

import android.content.Context
import android.util.Log
import java.time.LocalTime
import java.util.Locale

class ReminderManager private constructor() {
    private val reminders = mutableListOf<Reminder>()

    companion object {
        @Volatile
        private var instance: ReminderManager? = null

        fun getInstance(): ReminderManager {
            return instance ?: synchronized(this) {
                instance ?: ReminderManager().also { instance = it }
            }
        }
    }

    fun getReminders(): List<Reminder> {
        return reminders.toList()
    }

    fun addReminder(context: Context, reminder: Reminder) {
        reminders.add(reminder)
        scheduleReminder(context, reminder)
    }

    fun removeReminder(context: Context, reminder: Reminder) {
        reminders.remove(reminder)
        cancelReminder(context, reminder)
    }

    fun updateReminder(context: Context, oldReminderId: Int, newReminder: Reminder) {
        val index = reminders.indexOfFirst { it.getId() == oldReminderId }
        if (index != -1) {
            val oldReminder = reminders[index]
            cancelReminder(context, oldReminder)
            reminders[index] = newReminder
            scheduleReminder(context, newReminder)
        }
    }

    fun getTimeString(localTime: LocalTime) : String {
        val hour = if (localTime.hour % 12 == 0) 12 else localTime.hour % 12
        val minute = String.format(Locale.US, "%02d", localTime.minute)
        val period = if (localTime.hour < 12) "AM" else "PM"
        return "$hour:$minute $period"
    }

    fun addReminderFromDTO(context: Context, dto: ReminderDTO): Boolean {
        return try {
            val reminder = DTOUtils.toReminder(dto)
            var isAdded = false
            for (existReminder in reminders) {
                if (existReminder.getId() == reminder.getId()) {
                    isAdded = true
                }
            }
            if (!isAdded) {
                addReminder(context, reminder)
            }
            true
        } catch (e: Exception) {
            Log.e("ReminderManager", "Error converting ReminderDTO: $dto", e)
            false
        }
    }

    fun getReminderDTOList(): List<ReminderDTO> {
        return reminders.map { DTOUtils.toDTO(it) }
    }
}