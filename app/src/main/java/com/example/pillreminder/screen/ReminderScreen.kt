package com.example.pillreminder.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
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
import com.example.pillreminder.card.ReminderItem
import com.example.pillreminder.model.reminder.ReminderManager
import java.time.LocalDate

@Composable
fun ReminderScreen() {
    var selectedDayOfWeek by remember { mutableStateOf(LocalDate.now().dayOfWeek) }

    Column(modifier = Modifier.padding(16.dp)) {
        CalendarComponent(onSelectedDayChange = { selectedDayOfWeek = it })
        LazyColumn {
            items(ReminderManager.getInstance().getRemindersForDayOfWeek(selectedDayOfWeek)) { reminder ->
                ReminderItem(reminder)
            }
        }
    }
}
