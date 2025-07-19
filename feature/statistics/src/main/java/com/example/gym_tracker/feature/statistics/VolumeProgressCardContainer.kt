package com.example.gym_tracker.feature.statistics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gym_tracker.core.ui.components.MostImprovedExercise

/**
 * Container composable for the VolumeProgressCard that handles the ViewModel integration
 */
@Composable
fun VolumeProgressCardContainer(
    onViewDetailsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: VolumeProgressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is VolumeProgressUiState.Loading -> {
            com.example.gym_tracker.core.ui.components.VolumeProgressCard(
                totalVolumeThisMonth = 0.0,
                totalVolumePreviousMonth = 0.0,
                percentageChange = 0.0,
                isLoading = true,
                modifier = modifier
            )
        }
        
        is VolumeProgressUiState.Success -> {
            val volumeProgress = state.volumeProgress
            com.example.gym_tracker.core.ui.components.VolumeProgressCard(
                totalVolumeThisMonth = volumeProgress.totalVolumeThisMonth,
                totalVolumePreviousMonth = volumeProgress.totalVolumePreviousMonth,
                percentageChange = volumeProgress.percentageChange,
                trendDirection = volumeProgress.trendDirection.name,
                mostImprovedExercise = volumeProgress.mostImprovedExercise?.let {
                    MostImprovedExercise(
                        exerciseName = it.exerciseName,
                        previousWeight = it.previousBestWeight,
                        currentWeight = it.currentBestWeight,
                        improvementPercentage = it.improvementPercentage
                    )
                },
                onViewDetailsClick = onViewDetailsClick,
                modifier = modifier
            )
        }
        
        is VolumeProgressUiState.Error -> {
            com.example.gym_tracker.core.ui.components.VolumeProgressCard(
                totalVolumeThisMonth = 0.0,
                totalVolumePreviousMonth = 0.0,
                percentageChange = 0.0,
                modifier = modifier
            )
        }
    }
}