package com.example.pillreminder.card

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import kotlin.text.lowercase
import kotlin.text.replaceFirstChar
import kotlin.text.uppercase

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarComponent(
    onDateSelected: (LocalDate) -> Unit
) {
    var selectedYear by remember { mutableIntStateOf(YearMonth.now().year) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now().month) }
    var selectedDay by remember { mutableIntStateOf(LocalDate.now().dayOfMonth) }

    val currentYearMonth = YearMonth.of(selectedYear, selectedMonth)
    val daysInMonth = currentYearMonth.lengthOfMonth()

    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val itemSize: Dp = 50.dp
    val itemSpacing: Dp = 8.dp

    LaunchedEffect(selectedDay, selectedMonth, selectedYear) {
        val index = (selectedDay - 1).coerceAtLeast(0)
        listState.animateScrollToItem(index)
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${selectedMonth.name.lowercase().replaceFirstChar { it.uppercase() }} $selectedYear",
                style = MaterialTheme.typography.titleLarge
            )
            Row (

            ) {
                IconButton(
                    onClick = {
                        if (selectedMonth == Month.JANUARY) {
                            selectedYear.minus(1)
                        }
                        selectedMonth = selectedMonth.minus(1)
                        val lastDayOfMonth = YearMonth.of(selectedYear, selectedMonth).lengthOfMonth()
                        if (selectedDay > lastDayOfMonth) {
                            selectedDay = lastDayOfMonth
                        }
                        onDateSelected(LocalDate.of(selectedYear, selectedMonth, selectedDay))
                    }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Month"
                    )
                }
                IconButton(
                    onClick = {
                        if (selectedMonth == Month.DECEMBER) {
                            selectedYear.plus(1)
                        }
                        selectedMonth = selectedMonth.plus(1)
                        val lastDayOfMonth = YearMonth.of(selectedYear, selectedMonth).lengthOfMonth()
                        if (selectedDay > lastDayOfMonth) {
                            selectedDay = lastDayOfMonth
                        }
                        onDateSelected(LocalDate.of(selectedYear, selectedMonth, selectedDay))
                    }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Month"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        BoxWithConstraints {
            val horizontalCenterPad = ((maxWidth - itemSize) / 2).coerceAtLeast(0.dp)

            LazyRow(
                state = listState,
                flingBehavior = flingBehavior,
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                contentPadding = PaddingValues(horizontal = horizontalCenterPad)
            ) {
                items(daysInMonth) { i ->
                    val day = i + 1
                    val date = LocalDate.of(selectedYear, selectedMonth, day)
                    DateItem(
                        day = day,
                        date = date,
                        isSelected = selectedDay == day,
                        onClick = {
                            selectedDay = day
                            onDateSelected(LocalDate.of(selectedYear, selectedMonth, selectedDay))
                        },
                        sizeOfDayItem = itemSize.value.toInt()
                    )
                }
            }
        }
    }
}

@Composable
fun DateItem(
    day: Int,
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit,
    sizeOfDayItem: Int = 50
) {
    val isToday = date == LocalDate.now()

    val containerColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else       -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        else       -> MaterialTheme.colorScheme.onSurface
    }

    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val baseElevation = when {
        isSelected -> 6.dp
        else       -> 2.dp
    }
    val targetElevation = when {
        isPressed  -> baseElevation + 2.dp
        else       -> baseElevation
    }
    val tonalElevation by animateDpAsState(targetElevation, label = "tonalElevation")

    Surface (
        modifier = Modifier
            .size(sizeOfDayItem.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            ),
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = 0.dp,
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    color =
                        if (isSelected)
                            contentColor
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = day.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
                if (!isSelected && isToday) {
                    val todayDotColor = MaterialTheme.colorScheme.tertiary
                    Canvas(
                        modifier = Modifier.size(6.dp)
                    ) {
                        drawCircle(todayDotColor)
                    }
                }
            }
        }
    }

}
