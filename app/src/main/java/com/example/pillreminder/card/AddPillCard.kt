package com.example.pillreminder.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pillreminder.model.reminder.Reminder
import com.example.pillreminder.model.reminder.ReminderManager
import java.time.LocalTime

@Composable
fun AddPillCard(
    showCard: Boolean,
    onDismiss: () -> Unit,
    onUpdate: () -> Unit
) {
    if (showCard) {
        val defaultReminder = Reminder(
            pillName = "",
            times = listOf(LocalTime.of(8, 0)),
            daysOfWeek = emptySet()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                    onDismiss()
                }
        ) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {},
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                PillInformationCardBase(
                    initialReminder = defaultReminder,
                    onDismiss = onDismiss,
                    confirmButtonText = "Add",
                    onConfirm = { name, time, days ->
                        val newReminder = Reminder(name, time, days)
                        ReminderManager.getInstance().addReminder(newReminder)
                        onUpdate()
                        onDismiss()
                    }
                )
            }
        }
    }
}
