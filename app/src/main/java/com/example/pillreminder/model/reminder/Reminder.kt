package com.example.pillreminder.model.reminder

import java.time.DayOfWeek
import java.time.LocalTime

class Reminder(
    var pillName: String,
    var times: List<LocalTime>,
    var daysOfWeek: Set<DayOfWeek>,
    var usage: String = "",
    var cautions: String = "",
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
}
