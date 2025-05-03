package com.example.pillreminder.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pillreminder.card.CalendarComponent
import com.example.pillreminder.card.ReminderItem
import com.example.pillreminder.model.reminder.Reminder

@Composable
fun ReminderScreen(reminders: List<Reminder>) {
    Column(modifier = Modifier.padding(16.dp)) {
        CalendarComponent()
        LazyColumn {
            items(reminders) { reminder ->
                ReminderItem(reminder)
            }
        }
    }
}
