package com.example.pillreminder.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pillreminder.model.reminder.Reminder
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PillItem(
    reminder: Reminder,
    onClick: () -> Unit
) {
    val isCourse = reminder.rangeFrom != null && reminder.rangeTo != null
    val fmtRange = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())
    val daysLabel = daysSummary(reminder.daysOfWeek)

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            // 좌측 컬러 스트립 (그라데이션)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Title
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = reminder.pillName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        StaticTag(
                            text = if (isCourse) "Course" else "Ongoing",
                            bg = if (isCourse)
                                MaterialTheme.colorScheme.tertiaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            fg = if (isCourse)
                                MaterialTheme.colorScheme.onTertiaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Days of week
                    if (daysLabel.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = daysLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Times
                    if (reminder.times.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        val timesText = reminder.times.joinToString("  •  ") {
                            com.example.pillreminder.model.reminder.ReminderManager
                                .getInstance().getTimeString(it)
                        }
                        Text(
                            text = timesText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Course
                    if (isCourse) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${reminder.rangeFrom!!.format(fmtRange)} – ${reminder.rangeTo!!.format(fmtRange)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StaticTag(
    text: String,
    bg: androidx.compose.ui.graphics.Color,
    fg: androidx.compose.ui.graphics.Color
) {
    Surface(
        color = bg,
        contentColor = fg,
        shape = RoundedCornerShape(999.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

private fun daysSummary(days: Set<DayOfWeek>): String {
    if (days.isEmpty()) return ""
    if (days.size == DayOfWeek.entries.size) return "Daily"
    val ordered = listOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
    )
    return ordered
        .filter { it in days }
        .joinToString(" ") {
            it.getDisplayName(TextStyle.SHORT, Locale.getDefault()).replace(".", "")
        }
}
