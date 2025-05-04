package com.example.pillreminder.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pillreminder.model.reminder.Reminder
import java.time.DayOfWeek
import java.time.LocalTime

@Composable
fun PillInformationCard(reminder : Reminder, showCard: Boolean, onDismiss: () -> Unit) {
    if (showCard) {
        // Fullscreen background to catch outside clicks
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)) // Dim background
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onDismiss()
                }
        ) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        // Do nothing to consume click and prevent dismissal
                    },
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val pillName: String = reminder.pillName
                    val time: LocalTime = reminder.time
                    val daysOfWeek: Set<DayOfWeek> = reminder.daysOfWeek
                    Text(pillName)
                    Text(time.toString())
                    Text(daysOfWeek.toString())
                    Button(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }
    }
}
