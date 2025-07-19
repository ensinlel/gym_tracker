package com.example.gym_tracker.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gym_tracker.core.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * Calendar card component that shows workout indicators and allows viewing workout summaries
 * 
 * This component displays:
 * - Calendar showing days with workout entries (Requirement 6.1)
 * - Visual indicators for days with workouts (Requirement 6.2)
 * - Current month by default (Requirement 6.3)
 * - Support for tapping a day to show workout summary (Requirement 6.4)
 */
@Composable
fun CalendarCard(
    selectedDate: LocalDate = LocalDate.now(),
    workoutDates: Set<LocalDate> = emptySet(),
    onDateSelected: (LocalDate) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    
    DashboardCard(
        title = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Month navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { 
                        currentMonth = currentMonth.minusMonths(1)
                        // Reset selected date when changing months
                        if (selectedDate.month != currentMonth.month) {
                            onDateSelected(currentMonth.atDay(1))
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous month",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Count only workouts in the current month
                val workoutsInCurrentMonth = workoutDates.count { 
                    YearMonth.from(it) == currentMonth 
                }
                
                Text(
                    text = "$workoutsInCurrentMonth workout${if (workoutsInCurrentMonth != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AccentPurple,
                    fontWeight = FontWeight.Medium
                )
                
                IconButton(
                    onClick = { 
                        currentMonth = currentMonth.plusMonths(1)
                        // Reset selected date when changing months
                        if (selectedDate.month != currentMonth.month) {
                            onDateSelected(currentMonth.atDay(1))
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next month",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            // Days of week header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Calendar grid
            CalendarGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                workoutDates = workoutDates,
                onDateSelected = onDateSelected
            )
        }
        },
        modifier = modifier
    )
}

@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    workoutDates: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val lastDayOfMonth = currentMonth.atEndOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()
    
    // Create list of dates including empty cells for proper alignment
    val calendarDates = mutableListOf<LocalDate?>()
    
    // Add empty cells for days before the first day of the month
    repeat(firstDayOfWeek) {
        calendarDates.add(null)
    }
    
    // Add all days of the month
    for (day in 1..daysInMonth) {
        calendarDates.add(currentMonth.atDay(day))
    }
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.height(200.dp)
    ) {
        items(calendarDates) { date ->
            CalendarDay(
                date = date,
                isSelected = date == selectedDate,
                hasWorkout = date != null && workoutDates.contains(date),
                isToday = date == LocalDate.now(),
                onClick = { date?.let(onDateSelected) }
            )
        }
    }
}

@Composable
private fun CalendarDay(
    date: LocalDate?,
    isSelected: Boolean,
    hasWorkout: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> AccentPurple
                    isToday -> AccentPurple.copy(alpha = 0.2f)
                    hasWorkout -> AccentGreen.copy(alpha = 0.3f)
                    else -> Color.Transparent
                }
            )
            .clickable(enabled = date != null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        date?.let {
            Text(
                text = it.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = when {
                    isSelected -> Color.White
                    isToday -> AccentPurple
                    hasWorkout -> AccentGreen
                    else -> MaterialTheme.colorScheme.onSurface
                },
                fontWeight = if (isSelected || isToday || hasWorkout) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarCardPreview() {
    GymTrackerTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            CalendarCard(
                selectedDate = LocalDate.now(),
                workoutDates = setOf(
                    LocalDate.now().minusDays(2),
                    LocalDate.now().minusDays(5),
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(3)
                )
            )
        }
    }
}