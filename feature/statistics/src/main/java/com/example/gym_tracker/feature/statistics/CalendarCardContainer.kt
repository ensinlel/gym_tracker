package com.example.gym_tracker.feature.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gym_tracker.core.ui.components.CalendarCard
import com.example.gym_tracker.core.ui.theme.GymTrackerShapes
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * Container composable for the CalendarCard that handles the ViewModel integration
 * and displays workout summaries when a day is selected
 */
@Composable
fun CalendarCardContainer(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showWorkoutSummary by remember { mutableStateOf(false) }
    var workoutSummary by remember { mutableStateOf<List<String>>(emptyList()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    
    // Effect to update the month when it changes
    LaunchedEffect(currentMonth) {
        viewModel.loadCalendarData(currentMonth)
    }
    
    Column(modifier = modifier) {
        // Calendar card
        when (val state = uiState) {
            is CalendarUiState.Loading -> {
                // Show loading state or a placeholder calendar
                CalendarCard(
                    selectedDate = selectedDate ?: LocalDate.now(),
                    workoutDates = emptySet(),
                    onDateSelected = { date ->
                        selectedDate = date
                        showWorkoutSummary = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            is CalendarUiState.Success -> {
                CalendarCard(
                    selectedDate = selectedDate ?: LocalDate.now(),
                    workoutDates = state.workoutDates,
                    onDateSelected = { date ->
                        selectedDate = date
                        
                        // Get workout summary for the selected date
                        val summaries = state.workoutSummaries[date]
                        if (!summaries.isNullOrEmpty()) {
                            workoutSummary = summaries
                            showWorkoutSummary = true
                        } else {
                            showWorkoutSummary = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Update current month when it changes in the state
                if (state.currentMonth != currentMonth) {
                    currentMonth = state.currentMonth
                }
            }
            
            is CalendarUiState.Error -> {
                // Show error state or a placeholder calendar
                CalendarCard(
                    selectedDate = selectedDate ?: LocalDate.now(),
                    workoutDates = emptySet(),
                    onDateSelected = { date ->
                        selectedDate = date
                        showWorkoutSummary = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Workout summary card
        if (showWorkoutSummary && workoutSummary.isNotEmpty() && selectedDate != null) {
            WorkoutSummaryCard(
                date = selectedDate!!,
                workoutSummary = workoutSummary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}

/**
 * Card that displays a summary of workouts for a selected date
 */
@Composable
private fun WorkoutSummaryCard(
    date: LocalDate,
    workoutSummary: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = GymTrackerShapes.DashboardCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Date header
            Text(
                text = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Workout summary
            workoutSummary.forEach { summary ->
                Text(
                    text = "• $summary",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}