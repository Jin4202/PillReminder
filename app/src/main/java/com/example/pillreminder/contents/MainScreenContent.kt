package com.example.pillreminder.contents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pillreminder.reminder.Reminder
import java.time.format.DateTimeFormatter

@Composable
fun MainScreenContent(reminders: List<Reminder>) {
    Column(modifier = Modifier.padding(16.dp)) {
        CalendarComponent()
        LazyColumn {
            items(reminders) { reminder ->
                InstanceReminderItem(reminder)
            }
        }
    }
}

@Composable
fun InstanceReminderItem(reminder: Reminder) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = reminder.pillName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Time: ${reminder.time.format(DateTimeFormatter.ofPattern("hh:mm a"))}",
                fontSize = 16.sp
            )
        }
    }
}