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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.YearMonth

@Composable
fun CalendarComponent() {
    val currentMonth = remember { YearMonth.now() }
    val daysInMonth = currentMonth.lengthOfMonth()
    val year = currentMonth.year
    val month = currentMonth.month

    Column(modifier = Modifier.padding(16.dp)) {
        // Title: Month and Year
        Text(
            text = "${month.name.lowercase().replaceFirstChar { it.uppercase() }} $year",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal list of days
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(daysInMonth) { day ->
                DateItem(day = day + 1) // day is zero-based index
            }
        }
    }
}

@Composable
fun DateItem(day: Int) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
            .clickable { /* handle click */ },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$day")
    }
}