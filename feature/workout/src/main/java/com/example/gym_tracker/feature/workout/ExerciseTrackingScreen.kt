package com.example.gym_tracker.feature.workout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Placeholder for ExerciseTrackingScreen - temporarily commented out for build testing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseTrackingScreen(
    exerciseWithDetails: Any, // Placeholder type
    onNavigateBack: () -> Unit,
    viewModel: ExerciseTrackingViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    // TODO: Temporarily commented out for build testing - placeholder implementation
    Text(
        text = "Exercise Tracking Screen - Under Development",
        modifier = modifier.fillMaxSize().wrapContentSize(),
        style = MaterialTheme.typography.headlineMedium
    )
}