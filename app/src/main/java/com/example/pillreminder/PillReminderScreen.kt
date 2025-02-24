package com.example.pillreminder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PillReminderScreen(reminders: List<Reminder>) {
    Scaffold (
        topBar = {
            TopAppBar(title = { Text("Pill Reminder") })
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            items(reminders) { reminder ->
                ReminderItem(reminder)
            }
        }
    }
}

@Composable
fun ReminderItem(reminder: Reminder) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
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
            Text(
                text = "Days: ${
                    reminder.daysOfWeek.joinToString(", ") { it.name.take(3) }
                }",
                fontSize = 16.sp
            )
        }
    }
}