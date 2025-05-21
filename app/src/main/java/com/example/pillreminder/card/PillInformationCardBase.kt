package com.example.pillreminder.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pillreminder.model.reminder.Reminder
import com.example.pillreminder.model.reminder.ReminderManager
import java.time.DayOfWeek
import java.time.LocalTime

@Composable
fun PillInformationCardBase(
    initialReminder: Reminder,
    onDismiss: () -> Unit,
    confirmButtonText: String,
    onConfirm: (pillName: String, time: List<LocalTime>, days: Set<DayOfWeek>) -> Unit
) {
    val scrollState = rememberScrollState()

    var pillName by remember { mutableStateOf(initialReminder.pillName) }
    var selectedDays by remember { mutableStateOf(initialReminder.daysOfWeek.toSet()) }
    val times = remember { mutableStateListOf<LocalTime>().apply { addAll(initialReminder.times) } }
    var usageText by remember { mutableStateOf(initialReminder.usage) }
    var cautionsText by remember { mutableStateOf(initialReminder.cautions) }

    val textFieldMaxLines = 5


    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Pill Name
        EditablePillName(pillName = pillName) { pillName = it }

        // Day Frequency {Mon, Tue, Wed, Thu, Fri, Sat, Sun}
        DaySelector(
            selectedDays = selectedDays,
            onDayToggle = { day ->
                selectedDays = if (selectedDays.contains(day)) selectedDays - day else selectedDays + day
            }
        )

        // TimeSelector
        TimeSelectorColumn(
            times = times
        )

        // DateRangeSelector
        var isRange by remember { mutableStateOf(false) }
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = !isRange, onClick = { isRange = false })
                Text("Everyday", modifier = Modifier.padding(end = 16.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = isRange, onClick = { isRange = true })
                Text("Range")
            }
        }
        if (isRange) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                DatePickerLabel("From")
                DatePickerLabel("To")
            }
        }

        // Usage & Cautions Text Fields
        TextField(text = usageText, onValueChange = { usageText = it }, label = "Usage", maxLines = textFieldMaxLines)

        TextField(text = cautionsText, onValueChange = { cautionsText = it }, label = "Cautions", maxLines = textFieldMaxLines)

        // Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onDismiss) { Text("Cancel") }

            Button(onClick = {
                onConfirm(
                    pillName,
                    times,
                    selectedDays.toSet()
                )
            }) { Text(confirmButtonText) }
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
        label = { Text("Pill Name") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}
@Composable
fun DaySelector(
    selectedDays: Set<DayOfWeek>,
    onDayToggle: (DayOfWeek) -> Unit
) {
    val days = listOf(
        DayOfWeek.SUNDAY,
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    )
    val daySummary = if (selectedDays.size == 7) {
        "Daily"
    } else {
        days.filter { it in selectedDays }
            .joinToString(", ") { it.name.take(3).lowercase().replaceFirstChar { c -> c.uppercase() } }
    }

    Text(text = "Frequency: $daySummary", fontSize = 14.sp, color = Color.Gray)

    Row(modifier = Modifier.padding(8.dp)) {
        days.forEach { day ->
            val isSelected = selectedDays.contains(day)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.Blue else Color.LightGray)
                    .clickable { onDayToggle(day) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.name.take(1),
                    color = if (isSelected) Color.White else Color.Black
                )
            }
        }
    }
}


@Composable
fun TimeSelectorColumn(times: MutableList<LocalTime>) {
    val stateList = remember { mutableStateListOf<LocalTime>().apply { addAll(times) } }
    var showDialog by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf(-1) }
    var initialTime by remember { mutableStateOf(LocalTime.of(8, 0)) }

    Column {
        stateList.forEachIndexed { index, time ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ReminderManager.getInstance().getTimeString(time),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            editingIndex = index
                            initialTime = time
                            showDialog = true
                        }
                        .padding(8.dp)


                )
                IconButton(onClick = {
                    stateList.removeAt(index)
                    times.clear()
                    times.addAll(stateList)
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                }
            }
        }

        Button(onClick = {
            editingIndex = -1
            initialTime = LocalTime.of(8, 0)
            showDialog = true
        }) {
            Text("+ Add Time")
        }

        if (showDialog) {
            TimeSelector(
                initialTime = initialTime,
                onDismiss = { showDialog = false },
                onConfirm = { selected ->
                    if (editingIndex == -1) {
                        stateList.add(selected)
                    } else {
                        stateList[editingIndex] = selected
                    }
                    times.clear()
                    times.addAll(stateList)
                    showDialog = false
                }
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

@Composable
fun DatePickerLabel(label: String) {
    Column {
        Text("$label:")
        Box(
            modifier = Modifier
                .background(Color.LightGray)
                .padding(8.dp)
        ) {
            Text("Aug 1, 2025") // Replace with actual state
        }
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
            .padding(vertical = 8.dp),
        maxLines = maxLines
    )
}


@Composable
fun AlarmSelector(
    initialSelection: String = "Notification Only",
    onSelectionChange: (String) -> Unit = {}
) {
    val options = listOf("Notification Only", "Alarm & Notification")
    var selectedOption by remember { mutableStateOf(initialSelection) }

    Column(modifier = Modifier.padding(16.dp)) {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (option == selectedOption),
                        onClick = {
                            selectedOption = option
                            onSelectionChange(option)
                        }
                    )
                    .padding(vertical = 8.dp)
            ) {
                RadioButton(
                    selected = (option == selectedOption),
                    onClick = null
                )
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

