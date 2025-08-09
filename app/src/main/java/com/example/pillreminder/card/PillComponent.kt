package com.example.pillreminder.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pillreminder.model.reminder.Reminder
import java.time.format.DateTimeFormatter

@Composable
fun PillItem(reminder: Reminder, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = reminder.pillName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            if (reminder.rangeFrom != null && reminder.rangeTo != null) {
                val start = reminder.rangeFrom!!.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
                val end = reminder.rangeTo!!.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))

                Text(
                    text = "Starting Date: $start",
                )

                Text(
                    text = "Ending Date: $end",
                )
            } else {
                Text(
                    text = "Long-Term Medication / Supplement",
                )
            }
        }
    }
}