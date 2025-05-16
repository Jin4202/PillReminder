package com.example.pillreminder.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pillreminder.model.reminder.ReminderManager
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.text.lowercase
import kotlin.text.replaceFirstChar
import kotlin.text.uppercase

@Composable
fun CalendarComponent(onSelectedDayChange: (DayOfWeek) -> Unit) {
    var selectedYear by remember { mutableIntStateOf(YearMonth.now().year) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now().month) }
    var selectedDay by remember { mutableIntStateOf(LocalDate.now().dayOfMonth) }  // <--- 중요

    val currentYearMonth = YearMonth.of(selectedYear, selectedMonth)
    val daysInMonth = currentYearMonth.lengthOfMonth()

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { selectedMonth = selectedMonth.minus(1) }) {
                Text("<")
            }
            Text(
                text = "${selectedMonth.name.lowercase().replaceFirstChar { it.uppercase() }} $selectedYear",
                style = MaterialTheme.typography.titleLarge
            )
            Button(onClick = { selectedMonth = selectedMonth.plus(1) }) {
                Text(">")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(daysInMonth) { index ->
                val day = index + 1
                val date = LocalDate.of(selectedYear, selectedMonth, day)
                DateItem(
                    day = day,
                    date = date,
                    isSelected = selectedDay == day,
                    onClick = {
                        selectedDay = day
                        onSelectedDayChange(date.dayOfWeek)
                    }
                )
            }
        }
    }
}

@Composable
fun DateItem(day: Int, date: LocalDate, isSelected: Boolean, onClick: () -> Unit) {
    val dayOfWeekText = date.dayOfWeek.toString().lowercase().replaceFirstChar { it.uppercase() }.substring(0, 3)
    val backgroundColor = if (isSelected) Color.Blue else Color.LightGray
    val textColor = if (isSelected) Color.White else Color.Black

    Box(
        modifier = Modifier
            .size(50.dp)
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = dayOfWeekText, color = textColor)
            Text(text = "$day", color = textColor)
        }
    }
}
