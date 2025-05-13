package com.example.pillreminder.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pillreminder.card.CalendarComponent
import com.example.pillreminder.card.PillInformationCard
import com.example.pillreminder.card.ReminderItem
import com.example.pillreminder.model.reminder.Reminder
import com.example.pillreminder.model.reminder.ReminderManager
import java.time.LocalDate

@Composable
fun ReminderScreen() {
    var reminders by remember { mutableStateOf(ReminderManager.getInstance().getReminders()) }
    var selectedDayOfWeek by remember { mutableStateOf(LocalDate.now().dayOfWeek) }
    var selectedReminder by remember { mutableStateOf(Reminder("Not Selected", java.time.LocalTime.now(), emptySet())) }
    var showCard by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CalendarComponent(onSelectedDayChange = { selectedDayOfWeek = it })
            LazyColumn {
                val filteredReminders = reminders.filter { it.daysOfWeek.contains(selectedDayOfWeek) }
                items(filteredReminders, key = { it.getId() }) { reminder ->
                    ReminderItem(reminder, onClick = {
                        selectedReminder = reminder
                        showCard = true
                    })
                }
            }
        }
        PillInformationCard(
            reminder = selectedReminder,
            showCard = showCard,
            onDismiss = { showCard = false },
            onUpdate = {reminders = ReminderManager.getInstance().getReminders()}
        )
    }
}
