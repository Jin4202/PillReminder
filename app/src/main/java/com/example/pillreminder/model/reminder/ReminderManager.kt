package com.example.pillreminder.model.reminder

import androidx.compose.runtime.remember
import java.time.DayOfWeek
import java.time.LocalTime

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

    fun updateReminder(id: Int, newName: String, newTime: LocalTime, newDays: Set<DayOfWeek>) {
        val index = reminders.indexOfFirst { it.getId() == id }
        if (index != -1) {
            val newReminder = Reminder(newName, newTime, newDays, id)
            reminders[index] = newReminder
        }
    }

    fun getRemindersForDayOfWeek(dayOfWeek: DayOfWeek): List<Reminder> {
        return reminders.filter { it.daysOfWeek.contains(dayOfWeek) }
    }
}