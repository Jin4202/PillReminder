package com.example.pillreminder.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.pillreminder.card.EditPillCard
import com.example.pillreminder.card.PillItem
import com.example.pillreminder.model.reminder.Reminder
import com.example.pillreminder.model.reminder.ReminderManager
import java.time.LocalTime

@Composable
fun PillsScreen() {
    var reminders = ReminderManager.getInstance().getReminders()
    var showEditCard by remember { mutableStateOf(false) }
    var selectedReminder by remember { mutableStateOf(Reminder("Not Selected", listOf(LocalTime.of(8,0)), emptySet())) }

    LazyColumn {
        items(reminders) { reminder ->
            PillItem(reminder,  onClick = {
                selectedReminder = reminder
                showEditCard = true
            })
        }
    }
    if (showEditCard) {
        // Show the PillCard here
        EditPillCard (
            reminder = selectedReminder,
            showCard = showEditCard,
            onDismiss = { showEditCard = false },
            onUpdate = {reminders = ReminderManager.getInstance().getReminders()}
        )
    }
}
