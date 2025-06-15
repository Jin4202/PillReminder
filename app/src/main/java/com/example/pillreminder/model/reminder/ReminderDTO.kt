package com.example.pillreminder.model.reminder

data class ReminderDTO(
    var id: Int = 0,
    var pillName: String = "",
    var times: List<String> = emptyList(),           // LocalTime → String
    var daysOfWeek: List<String> = emptyList(),      // DayOfWeek → String
    var rangeFrom: String = "",                      // LocalDate → String
    var rangeTo: String = "",
    var usage: String = "",
    var cautions: String = ""
)
