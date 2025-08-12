package com.example.pillreminder.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pillreminder.model.reminder.Reminder
import com.example.pillreminder.model.reminder.ReminderManager
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReminderItem(
    reminder: Reminder,
    onClick: () -> Unit
) {
    val allDaysSelected = reminder.daysOfWeek.size == DayOfWeek.entries.size

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading circular icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Medication,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(Modifier.width(12.dp))

            // Main content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.pillName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Times as chips
                if (reminder.times.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        reminder.times.forEach { t ->
                            val timeStr = ReminderManager.getInstance().getTimeString(t)
                            StaticPill(
                                text = timeStr,
                                bg = MaterialTheme.colorScheme.secondaryContainer,
                                fg = MaterialTheme.colorScheme.onSecondaryContainer,
                                border = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }

                // Days of week visualization (Mon..Sun)
                if (reminder.daysOfWeek.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (allDaysSelected) {
                            StaticPill(
                                text = "Daily",
                                bg = MaterialTheme.colorScheme.tertiaryContainer,
                                fg = MaterialTheme.colorScheme.onTertiaryContainer,
                                border = MaterialTheme.colorScheme.outlineVariant
                            )
                        } else {
                            DayOfWeek.entries
                                .filter { it in reminder.daysOfWeek }
                                .forEach { d ->
                                    StaticPill(
                                        text = d.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                                            .replace(".", ""),
                                        bg = MaterialTheme.colorScheme.surfaceVariant,
                                        fg = MaterialTheme.colorScheme.onSurfaceVariant,
                                        border = MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                        }
                    }
                }

                // Range info
                if (reminder.rangeFrom != null && reminder.rangeTo != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${reminder.rangeFrom} – ${reminder.rangeTo}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Clip
                    )
                }
            }
        }
    }
}

@Composable
private fun StaticPill(
    text: String,
    bg: Color,
    fg: Color,
    border: Color
) {
    Surface(
        color = bg,
        contentColor = fg,
        shape = RoundedCornerShape(999.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, border)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
