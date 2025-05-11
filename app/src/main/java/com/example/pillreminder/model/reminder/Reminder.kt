package com.example.pillreminder.model.reminder

import java.time.DayOfWeek
import java.time.LocalTime

class Reminder (
    var pillName: String,
    var time: LocalTime,
    var daysOfWeek: Set<DayOfWeek>,
    private val id: Int = generate_reminder_ID()
) {
    companion object {
        private var id_counter = 0

        fun generate_reminder_ID(): Int {
            synchronized(this) {
                id_counter++
                return id_counter
            }
        }
    }

    fun getId(): Int {
        return id
    }

    fun getHour12(): Int {
        return if (time.hour % 12 == 0) 12 else time.hour % 12
    }

    fun getMinute(): Int {
        return time.minute
    }

    fun getPeriod(): String {
        return if (time.hour < 12) "AM" else "PM"
    }
}
