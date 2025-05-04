package com.example.pillreminder.model.reminder

import androidx.compose.runtime.remember
import java.time.DayOfWeek

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

    fun updateReminder(updatedReminder: Reminder) {
        val index = reminders.indexOfFirst { it.getId() == updatedReminder.getId() }
        if (index != -1) {
            reminders[index] = updatedReminder
        }
    }

    fun getRemindersForDayOfWeek(dayOfWeek: DayOfWeek): List<Reminder> {
        return reminders.filter { it.daysOfWeek.contains(dayOfWeek) }
    }
}