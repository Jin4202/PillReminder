package com.example.pillreminder.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pillreminder.model.reminder.Reminder
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalTime

@Composable
fun PillInformationCardBase(
    initialReminder: Reminder,
    onDismiss: () -> Unit,
    confirmButtonText: String,
    onConfirm: (pillName: String, time: LocalTime, days: Set<DayOfWeek>) -> Unit
) {
    var pillName by remember { mutableStateOf(initialReminder.pillName) }
    var selectedDays by remember { mutableStateOf(initialReminder.daysOfWeek.map { it.value % 7 }) }
    var selectedHour by remember { mutableStateOf(initialReminder.getHour12()) }
    var selectedMinute by remember { mutableStateOf(initialReminder.getMinute()) }
    var selectedPeriod by remember { mutableStateOf(initialReminder.getPeriod()) }

    Column(modifier = Modifier.padding(16.dp)) {
        EditablePillName(pillName = pillName) { pillName = it }

        DaySelector(
            selectedDays = selectedDays,
            onDayToggle = { day ->
                selectedDays = if (selectedDays.contains(day)) selectedDays - day else selectedDays + day
            }
        )

        TimeSelector(
            initialHour = selectedHour,
            initialMinute = selectedMinute,
            initialPeriod = selectedPeriod,
            onTimeChange = { hour, minute, period ->
                selectedHour = hour
                selectedMinute = minute
                selectedPeriod = period
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onDismiss) { Text("Cancel") }

            Button(onClick = {
                val hour = if (selectedPeriod == "AM") selectedHour % 12 else (selectedHour % 12) + 12
                val time = LocalTime.of(hour, selectedMinute)
                val days = selectedDays.map { DayOfWeek.of(it + 1) }.toSet()
                onConfirm(pillName, time, days)
            }) { Text(confirmButtonText) }
        }
    }
}


@Composable
fun DaySelector(
    selectedDays: List<Int>,
    onDayToggle: (Int) -> Unit
) {
    val days = listOf("S", "M", "T", "W", "T", "F", "S")

    Row(modifier = Modifier.padding(8.dp)) {
        days.forEachIndexed { index, day ->
            val isSelected = selectedDays.contains(index)
            Box(
                modifier = Modifier
                    .weight(1f) // Equal width for each day
                    .aspectRatio(1f) // Make it a square
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.Blue else Color.LightGray)
                    .clickable { onDayToggle(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    color = if (isSelected) Color.White else Color.Black
                )
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
        label = { Text("Pill Name") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun TimeSelector(
    initialHour: Int = 8,
    initialMinute: Int = 30,
    initialPeriod: String = "AM",
    onTimeChange: (Int, Int, String) -> Unit = { _, _, _ -> }
) {
    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }
    var selectedPeriod by remember { mutableStateOf(initialPeriod) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Hour Picker
        TimePickerColumn(
            values = (1..12).toList(),
            selectedValue = selectedHour,
            onValueChange = {
                selectedHour = it
                onTimeChange(selectedHour, selectedMinute, selectedPeriod)
            }
        )

        Text(":", fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterVertically))

        // Minute Picker
        TimePickerColumn(
            values = (0..59).toList(),
            selectedValue = selectedMinute,
            onValueChange = {
                selectedMinute = it
                onTimeChange(selectedHour, selectedMinute, selectedPeriod)
            }
        )

        // AM/PM Picker
        TimePickerColumn(
            values = listOf("AM", "PM"),
            selectedValue = selectedPeriod,
            onValueChange = {
                selectedPeriod = it
                onTimeChange(selectedHour, selectedMinute, selectedPeriod)
            }
        )
    }
}

@Composable
fun <T> TimePickerColumn(
    values: List<T>,
    selectedValue: T,
    onValueChange: (T) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Show 3 items: selected in the center (index + 1)
    val itemHeight = 40.dp
    val visibleItems = 3
    val contentPadding = (itemHeight * (visibleItems / 2))
    val density = LocalDensity.current

    val selectedIndex = values.indexOf(selectedValue)

    // Scroll to selected index on first launch
    LaunchedEffect(Unit) {
        listState.scrollToItem(selectedIndex)
    }

    // When scroll stops, center the closest item
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val offset = listState.firstVisibleItemScrollOffset
            val index = listState.firstVisibleItemIndex
            val threshold = with(density) { itemHeight.toPx() } / 2

            val targetIndex = if (offset < threshold) index else index + 1

            if (targetIndex in values.indices) {
                coroutineScope.launch {
                    listState.animateScrollToItem(targetIndex)
                }
                onValueChange(values[targetIndex])
            }
        }
    }

    Box(
        modifier = Modifier
            .height(itemHeight * visibleItems)
            .width(60.dp)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = contentPadding)
        ) {
            itemsIndexed(values) { index, value ->
                val isSelected = value == selectedValue
                Text(
                    text = value.toString().padStart(2, '0'),
                    fontSize = if (isSelected) 24.sp else 18.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.Blue else Color.Gray,
                    modifier = Modifier
                        .height(itemHeight)
                        .padding(4.dp)
                )
            }
        }
    }
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

