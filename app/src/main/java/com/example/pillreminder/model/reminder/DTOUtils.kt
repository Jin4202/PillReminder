package com.example.pillreminder.model.reminder

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

object DTOUtils {
    fun toReminder(reminderDTO: ReminderDTO): Reminder {
        return Reminder(
            id = reminderDTO.id,
            pillName = reminderDTO.pillName,
            times = reminderDTO.times.map { LocalTime.parse(it) },
            daysOfWeek = reminderDTO.daysOfWeek.map { DayOfWeek.valueOf(it) }.toSet(),
            rangeFrom = if (reminderDTO.rangeFrom.isNotEmpty()) LocalDate.parse(reminderDTO.rangeFrom) else null,
            rangeTo = if (reminderDTO.rangeTo.isNotEmpty()) LocalDate.parse(reminderDTO.rangeTo) else null,
            usage = reminderDTO.usage,
            cautions = reminderDTO.cautions
        )
    }

    fun toDTO(reminder: Reminder): ReminderDTO {
        return ReminderDTO(
            id = reminder.getId(),
            pillName = reminder.pillName,
            times = reminder.times.map { it.toString() },
            daysOfWeek = reminder.daysOfWeek.map { it.name },
            rangeFrom = reminder.rangeFrom?.toString() ?: "",
            rangeTo = reminder.rangeTo?.toString() ?: "",
            usage = reminder.usage,
            cautions = reminder.cautions
        )
    }
}

