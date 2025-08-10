package com.example.pillreminder.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pillreminder.card.AddPillButton
import com.example.pillreminder.card.AddPillCard
import com.example.pillreminder.card.CalendarComponent
import com.example.pillreminder.card.EditPillCard
import com.example.pillreminder.card.ReminderItem
import com.example.pillreminder.model.reminder.Reminder
import com.example.pillreminder.model.reminder.ReminderManager
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun ReminderScreen(
    updateData: () -> Unit,
    refreshKey: Int
) {
    var reminders by remember { mutableStateOf(ReminderManager.getInstance().getReminders()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedReminder by remember { mutableStateOf(Reminder("Not Selected", listOf(LocalTime.of(8,0)), emptySet())) }
    var showEditCard by remember { mutableStateOf(false) }
    var showAddCard by remember { mutableStateOf(false) }

    LaunchedEffect(refreshKey) {
        reminders = ReminderManager.getInstance().getReminders()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CalendarComponent(
                onDateSelected = { date ->
                    selectedDate = date
                },
            )
            LazyColumn {
                val filteredReminders = reminders.filter { reminder ->
                    val isDayMatch = reminder.daysOfWeek.contains(selectedDate.dayOfWeek)
                    var isDateInRange = true
                    if (reminder.rangeFrom != null && reminder.rangeTo != null) {
                        val fromDate = reminder.rangeFrom ?: selectedDate
                        val toDate = reminder.rangeTo ?: selectedDate.plusYears(100)
                        isDateInRange = selectedDate.isEqual(fromDate) || (selectedDate.isAfter(fromDate) && selectedDate.isBefore(toDate))
                    }

                    isDayMatch && isDateInRange
                }
                items(filteredReminders) { reminder ->
                    ReminderItem(reminder, onClick = {
                        selectedReminder = reminder
                        showEditCard = true
                    })
                }
            }
            AddPillButton(onClick = {
                showAddCard = true
            })
        }
        EditPillCard (
            reminder = selectedReminder,
            showCard = showEditCard,
            onDismiss = { showEditCard = false },
            onUpdate = {
                reminders = ReminderManager.getInstance().getReminders()
                showEditCard = false
                updateData()
            }
        )
        AddPillCard(
            showCard = showAddCard,
            onDismiss = { showAddCard = false },
            onUpdate = {
                reminders = ReminderManager.getInstance().getReminders()
                showAddCard = false
                updateData()
            }
        )
    }
}
