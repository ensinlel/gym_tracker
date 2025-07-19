package com.example.gym_tracker.feature.statistics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gym_tracker.core.ui.components.PersonalRecord

/**
 * Container composable for the PersonalRecordsCard that handles the ViewModel integration
 */
@Composable
fun PersonalRecordsCardContainer(
    onExerciseStarClick: (String, Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
    viewModel: PersonalRecordsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is PersonalRecordsUiState.Loading -> {
            com.example.gym_tracker.core.ui.components.PersonalRecordsCard(
                isLoading = true,
                modifier = modifier
            )
        }
        
        is PersonalRecordsUiState.Success -> {
            val result = state.personalRecordsResult
            com.example.gym_tracker.core.ui.components.PersonalRecordsCard(
                personalRecords = result.records.map { record ->
                    PersonalRecord(
                        exerciseName = record.exerciseName,
                        weight = record.weight,
                        achievedDate = record.achievedDate,
                        isRecent = record.isRecent
                    )
                },
                shouldSuggestStarMarking = result.shouldSuggestStarMarking,
                suggestedExercises = result.suggestedExercises,
                onStarExercise = { 
                    // In a real app, we would pass the actual exercise ID
                    onExerciseStarClick("exercise_id", true)
                },
                modifier = modifier
            )
        }
        
        is PersonalRecordsUiState.Error -> {
            com.example.gym_tracker.core.ui.components.PersonalRecordsCard(
                personalRecords = emptyList(),
                modifier = modifier
            )
        }
    }
}