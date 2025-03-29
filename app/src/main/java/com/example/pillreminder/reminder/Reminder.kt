package com.example.pillreminder.reminder

import java.time.DayOfWeek
import java.time.LocalTime

data class Reminder(
    val pillName: String,
    val time: LocalTime,
    val daysOfWeek: Set<DayOfWeek>
)
