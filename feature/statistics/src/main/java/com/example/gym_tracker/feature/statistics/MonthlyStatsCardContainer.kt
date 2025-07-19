package com.example.gym_tracker.feature.statistics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gym_tracker.core.ui.components.MonthlyStatsCard

/**
 * Container composable for the MonthlyStatsCard that handles the ViewModel integration
 */
@Composable
fun MonthlyStatsCardContainer(
    onViewDetailsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: MonthlyStatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is MonthlyStatsUiState.Loading -> {
            MonthlyStatsCard(
                currentMonthWorkouts = 0,
                previousMonthWorkouts = 0,
                weeklyAverageCurrentMonth = 0.0,
                weeklyAveragePreviousMonth = 0.0,
                percentageChange = 0.0,
                isLoading = true,
                modifier = modifier
            )
        }
        
        is MonthlyStatsUiState.Success -> {
            val stats = state.stats
            MonthlyStatsCard(
                currentMonthWorkouts = stats.currentMonthWorkouts,
                previousMonthWorkouts = stats.previousMonthWorkouts,
                weeklyAverageCurrentMonth = stats.weeklyAverageCurrentMonth,
                weeklyAveragePreviousMonth = stats.weeklyAveragePreviousMonth,
                percentageChange = stats.percentageChange,
                currentMonth = state.currentMonth,
                previousMonth = state.previousMonth,
                onViewDetailsClick = onViewDetailsClick,
                modifier = modifier
            )
        }
        
        is MonthlyStatsUiState.Error -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Error loading monthly stats. Please try again.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}