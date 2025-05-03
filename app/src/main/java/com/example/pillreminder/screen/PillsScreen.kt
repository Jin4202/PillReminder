package com.example.pillreminder.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.pillreminder.card.PillItem
import com.example.pillreminder.model.reminder.Reminder

@Composable
fun PillsScreen(reminders: List<Reminder>) {
    LazyColumn {
        items(reminders) { reminder ->
            PillItem(reminder)
        }
    }
}
