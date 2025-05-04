package com.example.pillreminder.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.pillreminder.card.PillItem
import com.example.pillreminder.model.reminder.Reminder
import com.example.pillreminder.model.reminder.ReminderManager

@Composable
fun PillsScreen() {
    var showCard by remember { mutableStateOf(false) }

    val reminders = ReminderManager.getInstance().getReminders()
    LazyColumn {
        items(reminders) { reminder ->
            PillItem(reminder,  onClick = {showCard = true})
        }
    }
}
