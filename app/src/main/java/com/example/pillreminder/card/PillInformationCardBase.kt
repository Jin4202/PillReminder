package com.example.pillreminder.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pillreminder.model.reminder.Reminder
import com.example.pillreminder.model.reminder.ReminderManager
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun PillInformationCardBase(
    cardTitle : String = "",
    initialReminder: Reminder,
    onDismiss: () -> Unit,
    confirmButtonText: String,
    onConfirm: (Int, Reminder) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val scroll = rememberScrollState()

    var pillName by remember { mutableStateOf(initialReminder.pillName) }
    var selectedDays by remember { mutableStateOf(initialReminder.daysOfWeek.toSet()) }
    val times = remember { mutableStateListOf<LocalTime>().apply { addAll(initialReminder.times) } }
    var rangeFrom by remember { mutableStateOf(initialReminder.rangeFrom) }
    var rangeTo by remember { mutableStateOf(initialReminder.rangeTo) }
    var usageText by remember { mutableStateOf(initialReminder.usage) }
    var cautionsText by remember { mutableStateOf(initialReminder.cautions) }

    var isRange by remember { mutableStateOf(initialReminder.isRangeOn()) }
    var isDaySelected by remember { mutableStateOf(selectedDays.isNotEmpty()) }

    Surface(
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scroll)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    cardTitle.ifBlank { "Edit Medication" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                if (onDelete != null) {
                    FilledTonalButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Delete")
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Pill name
            EditablePillName(pillName = pillName) { pillName = it }

            // Day Frequency {Mon, Tue, Wed, Thu, Fri, Sat, Sun}
            DaySelector(
                selectedDays = selectedDays,
                onDayToggle = { day ->
                    selectedDays = if (day in selectedDays) selectedDays - day else selectedDays + day
                    isDaySelected = selectedDays.isNotEmpty()
                }
            )
            if (!isDaySelected) {
                Text(
                    text = "Please select at least one day.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            // TimeSelector [8:00, 10:00, 12:00]
            TimeSelectorColumn(
                times = times
            )

            // Ongoing / Date range
            Spacer(Modifier.height(8.dp))
            Text("Schedule Duration", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(6.dp))

            RangeSegmented(
                isRange = isRange,
                onChange = { isRange = it }
            )

            val isFromMissing  = isRange && rangeFrom == null
            val isToMissing    = isRange && rangeTo == null
            val isOrderInvalid = isRange && rangeFrom != null && rangeTo != null &&
                    rangeTo!!.isBefore(rangeFrom)

            val isRangeSelected = !isRange || (!isFromMissing && !isToMissing && !isOrderInvalid)

            if (isRange) {
                Spacer(Modifier.height(8.dp))

                // From
                DateField(
                    label = "From",
                    selectedDate = rangeFrom,
                    onDateSelected = { picked -> rangeFrom = picked },
                    isError = isFromMissing,
                    supportingText = if (isFromMissing) "Please select a start date." else null
                )

                // To
                DateField(
                    label = "To",
                    selectedDate = rangeTo,
                    onDateSelected = { picked ->
                        rangeTo = picked
                    },
                    isError = isToMissing || isOrderInvalid,
                    supportingText = when {
                        isToMissing     -> "Please select an end date."
                        isOrderInvalid  -> "The end date cannot be before the start date."
                        else            -> null
                    }
                )
            } else {
                rangeFrom = null
                rangeTo = null
            }

            // Usage & cautions
            Spacer(Modifier.height(8.dp))
            TextField(
                text = usageText, onValueChange = { usageText = it },
                label = "Usage", maxLines = 5
            )
            TextField(
                text = cautionsText, onValueChange = { cautionsText = it },
                label = "Cautions", maxLines = 5
            )

            // Footer actions
            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        val newReminder = Reminder(
                            pillName = pillName,
                            times = times,
                            daysOfWeek = selectedDays.toSet(),
                            rangeFrom = rangeFrom,
                            rangeTo = rangeTo,
                            usage = usageText,
                            cautions = cautionsText
                        )
                        onConfirm(initialReminder.getId(), newReminder)
                    },
                    enabled = isRangeSelected && isDaySelected
                ) { Text(confirmButtonText) }
            }
        }
    }
}

@Composable
fun EditablePillName(
    pillName: String,
    onNameChange: (String) -> Unit
) {
    OutlinedTextField(
        value = pillName,
        onValueChange = onNameChange,
        label = { Text("Medication name") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DaySelector(
    selectedDays: Set<DayOfWeek>,
    onDayToggle: (DayOfWeek) -> Unit,
    minSize: Dp = 28.dp,
    maxSize: Dp = 48.dp,
    spacing: Dp = 8.dp
) {
    val daysOrdered = listOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
    )
    val all = selectedDays.size == DayOfWeek.entries.size
    val summary = if (all) "Daily" else
        daysOrdered.filter { it in selectedDays }
            .joinToString(", ") { it.getDisplayName(TextStyle.SHORT, Locale.getDefault()).replace(".", "") }

    Text(
        text = "Frequency: $summary",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(6.dp))
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val chipSize = ((maxWidth - spacing * 6) / 7f).coerceIn(minSize, maxSize)
        val labelLen = if (chipSize < 36.dp) 1 else 2
        val rippleRadius = chipSize / 2

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            daysOrdered.forEach { day ->
                val selected = day in selectedDays
                val label = dayLabel(day, labelLen)
                val interaction = remember { MutableInteractionSource() }

                Surface(
                    shape = CircleShape,
                    color = if (selected)
                        MaterialTheme.colorScheme.tertiaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selected)
                        MaterialTheme.colorScheme.onTertiaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    tonalElevation = if (selected) 1.dp else 0.dp,
                    shadowElevation = 0.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .size(chipSize)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = interaction,
                            indication = rememberRipple(
                                bounded = true,
                                radius = rippleRadius
                            ),
                            onClick = { onDayToggle(day) },
                            onClickLabel = day.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        )
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
private fun dayLabel(day: DayOfWeek, len: Int): String {
    val raw = day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val clean = raw.replace(".", "").trim()
    val cut = if (clean.length >= len) clean.substring(0, len) else clean
    return cut.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimeSelectorColumn(times: MutableList<LocalTime>) {
    val local = remember { mutableStateListOf<LocalTime>().apply { addAll(times) } }
    var showDialog by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableIntStateOf(-1) }
    var initialTime by remember { mutableStateOf(LocalTime.now()) }

    fun commit() { times.clear(); times.addAll(local) }

    Spacer(Modifier.height(12.dp))
    Text("Times", style = MaterialTheme.typography.titleSmall)
    Spacer(Modifier.height(6.dp))

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        local.forEachIndexed { idx, t ->
            TimePill(
                text = ReminderManager.getInstance().getTimeString(t),
                onClick = {
                    editingIndex = idx
                    initialTime = t
                    showDialog = true
                },
                onRemove = {
                    local.removeAt(idx); commit()
                }
            )
        }

        AddTimePill(
            onClick = {
                editingIndex = -1
                initialTime = LocalTime.now()
                showDialog = true
            }
        )
    }

    if (showDialog) {
        TimeSelector(
            initialTime = initialTime,
            onDismiss = { showDialog = false },
            onConfirm = { selected ->
                if (editingIndex == -1) local.add(selected) else local[editingIndex] = selected
                commit()
                showDialog = false
            }
        )
    }
}

@Composable
private fun TimePill(
    text: String,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
        ) {
            Text(text, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.width(4.dp))
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.size(16.dp)
                )
            }
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Remove",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AddTimePill(onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Add time",
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Add time",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelector(
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                onConfirm(selectedTime)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Select Time") },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RangeSegmented(
    isRange: Boolean,
    onChange: (Boolean) -> Unit
) {
    val options = listOf("Ongoing", "Range")
    val selectedIndex = if (isRange) 1 else 0

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                selected = index == selectedIndex,
                onClick = { onChange(index == 1) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                label = { Text(label) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    isError: Boolean = false,
    supportingText: String? = null
) {
    var showPicker by remember { mutableStateOf(false) }
    val fmt = remember { DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault()) }
    val valueText = selectedDate?.format(fmt)

    DateFieldButton(
        label = label,
        valueText = valueText,
        isError = isError,
        supportingText = supportingText,
        onClick = { showPicker = true }
    )

    if (showPicker) {
        val initialMillis = remember(selectedDate) {
            selectedDate
                ?.atStartOfDay(ZoneId.systemDefault())
                ?.toInstant()
                ?.toEpochMilli()
        }
        val state = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        val picked = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(picked)
                    }
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = state) }
    }
}


@Composable
fun TextField(text: String, onValueChange: (String) -> Unit, label: String, maxLines: Int) {
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        maxLines = maxLines
    )
}