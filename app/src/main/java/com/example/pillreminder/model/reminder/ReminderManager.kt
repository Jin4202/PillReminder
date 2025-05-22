package com.example.pillreminder.model.reminder

import java.time.DayOfWeek
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

    fun addReminder(reminder: Reminder) {
        reminders.add(reminder)
    }

    fun removeReminder(reminder: Reminder) {
        reminders.remove(reminder)
    }

    fun removeReminderById(id: Int) {
        reminders.removeAll { it.getId() == id }
    }

    fun getReminderById(id: Int): Reminder? {
        return reminders.find { it.getId() == id }
    }

    fun updateReminder(id: Int, newReminder: Reminder) {
        val index = reminders.indexOfFirst { it.getId() == id }
        if (index != -1) {
            reminders[index] = newReminder
        }
    }

    fun getRemindersForDayOfWeek(dayOfWeek: DayOfWeek): List<Reminder> {
        return reminders.filter { it.daysOfWeek.contains(dayOfWeek) }
    }

    fun getTimeString(localTime: LocalTime) : String {
        val hour = if (localTime.hour % 12 == 0) 12 else localTime.hour % 12
        val minute = String.format(Locale.US, "%02d", localTime.minute)
        val period = if (localTime.hour < 12) "AM" else "PM"
        return "$hour:$minute $period"
    }
}