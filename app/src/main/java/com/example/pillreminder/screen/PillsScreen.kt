package com.example.pillreminder.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.pillreminder.card.EditPillCard
import com.example.pillreminder.card.LabeledDivider
import com.example.pillreminder.card.PillItem
import com.example.pillreminder.card.SectionCard
import com.example.pillreminder.model.reminder.Reminder
import com.example.pillreminder.model.reminder.ReminderManager
import java.time.LocalTime


enum class PillFilter { ALL, SHORT_TERM, LONG_TERM }

private fun isShortTerm(reminder: Reminder): Boolean {
    val from = reminder.rangeFrom
    val to = reminder.rangeTo
    return from != null && to != null
}

private fun isLongTerm(reminder: Reminder): Boolean = !isShortTerm(reminder)

@Composable
fun PillsScreen(
    updateData: () -> Unit
) {
    var reminders by remember { mutableStateOf(ReminderManager.getInstance().getReminders()) }
    var showEditCard by remember { mutableStateOf(false) }
    var selectedReminder by remember { mutableStateOf(Reminder("Not Selected", listOf(LocalTime.of(8,0)), emptySet())) }
    var showAddCard by remember { mutableStateOf(false) }

    var filter by remember { mutableStateOf(PillFilter.ALL) }

    val filteredSorted by remember(reminders, filter) {
        derivedStateOf {
            val base = when (filter) {
                PillFilter.ALL -> reminders
                PillFilter.SHORT_TERM -> reminders.filter(::isShortTerm)
                PillFilter.LONG_TERM -> reminders.filter(::isLongTerm)
            }
            base.sortedBy { it.pillName.lowercase() }
        }
    }


    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Meds & Supplements",
                style = MaterialTheme.typography.titleLarge
            )
            PillFilterSegmented(
                value = filter,
                onChange = { new -> filter = new },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
        }

        Spacer(Modifier.height(12.dp))
        LabeledDivider(label = "Medications / Supplements")
        Spacer(Modifier.height(12.dp))

        SectionCard {
            if (filteredSorted.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No pills found for this filter",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    items(filteredSorted) { reminder ->
                        PillItem(
                            reminder = reminder,
                            onClick = {
                                selectedReminder = reminder
                                showEditCard = true
                            }
                        )
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            AddPillButton { showAddCard = true }
                        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PillFilterSegmented(
    value: PillFilter,
    onChange: (PillFilter) -> Unit,
    modifier: Modifier = Modifier,
    labels: Map<PillFilter, String> = mapOf(
        PillFilter.ALL to "All",
        PillFilter.SHORT_TERM to "Course",
        PillFilter.LONG_TERM to "Ongoing"
    )
) {
    val options = listOf(PillFilter.ALL, PillFilter.SHORT_TERM, PillFilter.LONG_TERM)

    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option == value,
                onClick = { onChange(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                label = {
                    Text(
                        labels[option] ?: option.name,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )
        }
    }
}