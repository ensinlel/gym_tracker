package com.example.gym_tracker.feature.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.gym_tracker.core.data.model.WorkoutStreak
import com.example.gym_tracker.core.data.model.MonthlyWorkoutStats
import com.example.gym_tracker.core.data.model.VolumeProgress
import com.example.gym_tracker.core.data.usecase.GetPersonalRecordsUseCase
import com.example.gym_tracker.core.data.usecase.GetWeightProgressUseCase
import com.example.gym_tracker.core.ui.theme.AccentPurple

/**
 * Container composable for the dashboard analytics
 * Displays all analytics cards based on the ViewModel state
 */
@Composable
fun DashboardAnalyticsContainer(
    onAddWeightClick: () -> Unit = {},
    onViewWeightHistoryClick: () -> Unit = {},
    onExerciseStarClick: (String, Boolean) -> Unit = { _, _ -> },
    onNavigateToExerciseSelection: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: DashboardAnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is DashboardAnalyticsUiState.Loading -> {
            // Loading state
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentPurple)
            }
        }
        
        is DashboardAnalyticsUiState.Success -> {
            // Success state - display all analytics cards
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Calendar Card - moved to top
                CalendarCardContainer(
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Streak Card
                StreakCardContainer(
                    workoutStreak = state.workoutStreak,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Monthly Stats Card
                MonthlyStatsCardContainer(
                    monthlyStats = state.monthlyStats,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Volume Progress Card
                VolumeProgressCardContainer(
                    volumeProgress = state.volumeProgress,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Personal Records Card
                PersonalRecordsCardContainer(
                    personalRecordsResult = state.personalRecordsResult,
                    onExerciseStarClick = onExerciseStarClick,
                    onNavigateToExerciseSelection = onNavigateToExerciseSelection,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Weight Progress Card
                WeightProgressCardContainer(
                    weightProgressResult = state.weightProgressResult,
                    onAddWeightClick = onAddWeightClick,
                    onViewHistoryClick = onViewWeightHistoryClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        is DashboardAnalyticsUiState.Error -> {
            // Error state
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Error loading analytics",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Container composable for the StreakCard
 */
@Composable
fun StreakCardContainer(
    workoutStreak: WorkoutStreak,
    modifier: Modifier = Modifier
) {
    // Use the existing StreakCard component
    com.example.gym_tracker.core.ui.components.StreakCard(
        currentStreak = workoutStreak.currentStreak,
        longestStreak = workoutStreak.longestStreak,
        daysSinceLastWorkout = workoutStreak.daysSinceLastWorkout,
        encouragingMessage = workoutStreak.encouragingMessage,
        modifier = modifier
    )
}

/**
 * Container composable for the MonthlyStatsCard
 */
@Composable
fun MonthlyStatsCardContainer(
    monthlyStats: MonthlyWorkoutStats,
    modifier: Modifier = Modifier
) {
    // Use the existing MonthlyStatsCard component
    com.example.gym_tracker.core.ui.components.MonthlyStatsCard(
        currentMonthWorkouts = monthlyStats.currentMonthWorkouts,
        previousMonthWorkouts = monthlyStats.previousMonthWorkouts,
        weeklyAverageCurrentMonth = monthlyStats.weeklyAverageCurrentMonth,
        weeklyAveragePreviousMonth = monthlyStats.weeklyAveragePreviousMonth,
        percentageChange = monthlyStats.percentageChange,
        modifier = modifier
    )
}

/**
 * Container composable for the VolumeProgressCard
 */
@Composable
fun VolumeProgressCardContainer(
    volumeProgress: VolumeProgress,
    modifier: Modifier = Modifier
) {
    // Use the existing VolumeProgressCard component
    com.example.gym_tracker.core.ui.components.VolumeProgressCard(
        totalVolumeThisMonth = volumeProgress.totalVolumeThisMonth,
        totalVolumePreviousMonth = volumeProgress.totalVolumePreviousMonth,
        trendDirection = volumeProgress.trendDirection.name,
        percentageChange = volumeProgress.percentageChange,
        mostImprovedExercise = volumeProgress.mostImprovedExercise?.let {
            com.example.gym_tracker.core.ui.components.MostImprovedExercise(
                exerciseName = it.exerciseName,
                previousWeight = it.previousBestWeight,
                currentWeight = it.currentBestWeight,
                improvementPercentage = it.improvementPercentage
            )
        },
        modifier = modifier
    )
}

/**
 * Container composable for the PersonalRecordsCard
 */
@Composable
fun PersonalRecordsCardContainer(
    personalRecordsResult: GetPersonalRecordsUseCase.PersonalRecordsResult,
    onExerciseStarClick: (String, Boolean) -> Unit = { _, _ -> },
    onNavigateToExerciseSelection: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Use the existing PersonalRecordsCard component
    com.example.gym_tracker.core.ui.components.PersonalRecordsCard(
        personalRecords = personalRecordsResult.records.map {
            com.example.gym_tracker.core.ui.components.PersonalRecord(
                exerciseName = it.exerciseName,
                weight = it.weight,
                achievedDate = it.achievedDate,
                isRecent = it.isRecent
            )
        },
        shouldSuggestStarMarking = personalRecordsResult.shouldSuggestStarMarking,
        suggestedExercises = personalRecordsResult.suggestedExercises,
        onStarExercise = onNavigateToExerciseSelection, // Navigate to exercise selection screen
        modifier = modifier
    )
}

/**
 * Container composable for the WeightProgressCard
 */
@Composable
fun WeightProgressCardContainer(
    weightProgressResult: GetWeightProgressUseCase.WeightProgressResult,
    onAddWeightClick: () -> Unit = {},
    onViewHistoryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Use the existing WeightProgressCard component
    com.example.gym_tracker.core.ui.components.WeightProgressCard(
        currentWeight = weightProgressResult.weightProgress.currentWeight,
        weightThirtyDaysAgo = weightProgressResult.weightProgress.weightThirtyDaysAgo,
        weightChange = weightProgressResult.weightProgress.weightChange,
        weightChangeFormatted = weightProgressResult.weightChangeFormatted,
        weightChangeDescription = weightProgressResult.weightChangeDescription,
        trendDirection = weightProgressResult.weightProgress.trendDirection.name,
        shouldPromptWeightTracking = weightProgressResult.shouldPromptWeightTracking,
        shouldPromptForRecentData = weightProgressResult.shouldPromptForRecentData,
        onAddWeightClick = onAddWeightClick,
        onViewHistoryClick = onViewHistoryClick,
        modifier = modifier
    )
}