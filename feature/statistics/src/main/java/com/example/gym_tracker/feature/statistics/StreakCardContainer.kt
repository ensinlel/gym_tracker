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
import com.example.gym_tracker.core.ui.components.StreakCard

/**
 * Container composable for the StreakCard that handles the ViewModel integration
 */
@Composable
fun StreakCardContainer(
    onViewHistoryClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: StreakViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is StreakUiState.Loading -> {
            StreakCard(
                currentStreak = 0,
                longestStreak = 0,
                daysSinceLastWorkout = 0,
                isLoading = true,
                modifier = modifier
            )
        }
        
        is StreakUiState.Success -> {
            val streak = state.streak
            StreakCard(
                currentStreak = streak.currentStreak,
                longestStreak = streak.longestStreak,
                daysSinceLastWorkout = streak.daysSinceLastWorkout,
                streakType = streak.streakType.name,
                encouragingMessage = streak.encouragingMessage,
                onViewHistoryClick = onViewHistoryClick,
                modifier = modifier
            )
        }
        
        is StreakUiState.Error -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Error loading streak data. Please try again.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}