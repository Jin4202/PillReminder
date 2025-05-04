package com.example.pillreminder.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
fun CalendarComponent(onSelectedDayChange : (DayOfWeek) -> Unit) {
    var selectedYear by remember { mutableIntStateOf(YearMonth.now().year) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now().month) }
    val currentYearMonth = YearMonth.of(selectedYear, selectedMonth)
    val daysInMonth = currentYearMonth.lengthOfMonth()

    Column(modifier = Modifier.padding(16.dp)) {
        // Title: Month and Year
        Text(
            text = "${selectedMonth.name.lowercase().replaceFirstChar { it.uppercase() }} $selectedYear",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons for Year and Month Selection
        Button(onClick = { selectedYear-- }) {
            Text("Previous Year")
        }
        Button(onClick = { selectedYear++ }) {
            Text("Next Year")
        }
        Button(onClick = { selectedMonth = selectedMonth.minus(1) }) {
            Text("Previous Month")
        }
        Button(onClick = { selectedMonth = selectedMonth.plus(1) }) {
            Text("Next Month")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal list of days
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(daysInMonth) { day ->
                val date = LocalDate.of(selectedYear, selectedMonth, day + 1)
                DateItem(day = day + 1, date = date, onSelectedDayChange)
            }
        }
    }
}

@Composable
fun DateItem(day: Int, date: LocalDate, onSelectedDayChange: (DayOfWeek) -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .clickable {
                val dayOfWeek = date.dayOfWeek
                onSelectedDayChange(dayOfWeek)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$day")
    }
}