package com.example.pillreminder.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pillreminder.card.AddPillButton
import com.example.pillreminder.card.AddPillCard
import com.example.pillreminder.card.CalendarComponent
import com.example.pillreminder.card.EditPillCard
import com.example.pillreminder.card.LabeledDivider
import com.example.pillreminder.card.ReminderItem
import com.example.pillreminder.card.SectionCard
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

    val filteredReminders by remember(reminders, selectedDate) {
        derivedStateOf {
            reminders.filter { reminder ->
                val isDayMatch = reminder.daysOfWeek.contains(selectedDate.dayOfWeek)
                val inRange = if (reminder.rangeFrom != null && reminder.rangeTo != null) {
                    val from = reminder.rangeFrom!!
                    val to = reminder.rangeTo!!
                    !selectedDate.isBefore(from) && !selectedDate.isAfter(to)
                } else {
                    true
                }
                isDayMatch && inRange
            }
        }
    }

    LaunchedEffect(refreshKey) {
        reminders = ReminderManager.getInstance().getReminders()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            CalendarComponent(
                onDateSelected = { date -> selectedDate = date }
            )

            Spacer(Modifier.padding(vertical = 12.dp))
            LabeledDivider(label = "Medications / Supplements")
            Spacer(Modifier.padding(vertical = 12.dp))

            SectionCard {
                // Empty List
                if (filteredReminders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No reminders for this date",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        items(filteredReminders) { reminder ->
                            ReminderItem(reminder) {
                                selectedReminder = reminder
                                showEditCard = true
                            }
                        }
                        item { Spacer(Modifier.height(8.dp)) }
                        item {
                            AddPillButton { showAddCard = true }
                        }
                    }
                }
            }
        }
        EditPillCard(
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
