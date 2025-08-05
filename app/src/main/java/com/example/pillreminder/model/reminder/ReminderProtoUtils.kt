package com.example.pillreminder.model.reminder

object ReminderProtoUtils {
    fun toProto(reminder: Reminder): ReminderMessage {
        return ReminderMessage.newBuilder()
            .setId(reminder.getId())
            .setPillName(reminder.pillName)
            .addAllTimes(reminder.times.map { it.toString() })
            .addAllDaysOfWeek(reminder.daysOfWeek.map { it.value })
            .setRangeFrom(reminder.rangeFrom?.toString() ?: "")
            .setRangeTo(reminder.rangeTo?.toString() ?: "")
            .setUsage(reminder.usage)
            .setCautions(reminder.cautions)
            .build()
    }

    fun fromProto(proto: ReminderMessage): Reminder {
        return Reminder(
            pillName = proto.pillName,
            times = proto.timesList.map { java.time.LocalTime.parse(it) },
            daysOfWeek = proto.daysOfWeekList.map { java.time.DayOfWeek.of(it) }.toSet(),
            rangeFrom = if (proto.rangeFrom.isNotEmpty()) java.time.LocalDate.parse(proto.rangeFrom) else null,
            rangeTo = if (proto.rangeTo.isNotEmpty()) java.time.LocalDate.parse(proto.rangeTo) else null,
            usage = proto.usage,
            cautions = proto.cautions,
            id = proto.id
        )
    }
}