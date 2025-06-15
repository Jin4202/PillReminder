package com.example.pillreminder.model.reminder

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

fun ReminderDTO.toReminder(): Reminder {
    return Reminder(
        pillName = pillName,
        times = times.map { LocalTime.parse(it) },
        daysOfWeek = daysOfWeek.map { DayOfWeek.valueOf(it) }.toSet(),
        rangeFrom = if (rangeFrom.isNotEmpty()) LocalDate.parse(rangeFrom) else null,
        rangeTo = if (rangeTo.isNotEmpty()) LocalDate.parse(rangeTo) else null,
        usage = usage,
        cautions = cautions,
        id = id
    )
}

fun Reminder.toDTO(): ReminderDTO {
    return ReminderDTO(
        id = getId(),
        pillName = pillName,
        times = times.map { it.toString() },
        daysOfWeek = daysOfWeek.map { it.name },
        rangeFrom = rangeFrom?.toString() ?: "",
        rangeTo = rangeTo?.toString() ?: "",
        usage = usage,
        cautions = cautions
    )
}