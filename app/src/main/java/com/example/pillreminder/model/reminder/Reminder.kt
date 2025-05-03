package com.example.pillreminder.model.reminder

import java.time.DayOfWeek
import java.time.LocalTime

data class Reminder(
    val pillName: String,
    val time: LocalTime,
    val daysOfWeek: Set<DayOfWeek>
)
