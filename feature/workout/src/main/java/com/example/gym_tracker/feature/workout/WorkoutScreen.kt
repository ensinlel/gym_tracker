package com.example.gym_tracker.feature.workout

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Placeholder for WorkoutScreen - temporarily commented out for build testing
 */
@Composable
fun WorkoutScreen(
    modifier: Modifier = Modifier,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    // TODO: Implement workout screen functionality
    Text(
        text = "Workout Screen - Under Development",
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium
    )
}