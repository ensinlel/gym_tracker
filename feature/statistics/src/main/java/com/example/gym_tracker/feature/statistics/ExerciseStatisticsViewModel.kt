package com.example.gym_tracker.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for exercise-specific statistics screen
 */
@HiltViewModel
class ExerciseStatisticsViewModel @Inject constructor(
    // TODO: Inject actual repositories when available
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExerciseStatisticsUiState>(ExerciseStatisticsUiState.Loading)
    val uiState: StateFlow<ExerciseStatisticsUiState> = _uiState.asStateFlow()

    /**
     * Load statistics for a specific exercise
     */
    fun loadExerciseStatistics(exerciseId: String) {
        viewModelScope.launch {
            _uiState.value = ExerciseStatisticsUiState.Loading
            try {
                // TODO: Replace with actual data loading from repositories
                // For now, generate sample data
                val sampleData = generateSampleExerciseData(exerciseId)
                _uiState.value = ExerciseStatisticsUiState.Success(
                    exerciseName = sampleData.exerciseName,
                    personalRecord = sampleData.personalRecord,
                    weightProgressionData = sampleData.weightProgressionData,
                    volumeProgressionData = sampleData.volumeProgressionData,
                    exerciseHistory = sampleData.exerciseHistory
                )
            } catch (e: Exception) {
                _uiState.value = ExerciseStatisticsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Generate sample data for demonstration
     * TODO: Replace with actual data loading
     */
    private fun generateSampleExerciseData(exerciseId: String): SampleExerciseData {
        val exerciseName = when (exerciseId) {
            "1" -> "Bench Press"
            "2" -> "Squat"
            "3" -> "Deadlift"
            else -> "Exercise"
        }
        
        // Generate sample weight progression data
        val weightProgressionData = ChartEntryModelProducer()
        val weightEntries = listOf(
            entryOf(0f, 60f),
            entryOf(1f, 65f),
            entryOf(2f, 70f),
            entryOf(3f, 72.5f),
            entryOf(4f, 75f),
            entryOf(5f, 77.5f),
            entryOf(6f, 80f),
            entryOf(7f, 82.5f)
        )
        weightProgressionData.setEntries(listOf(weightEntries))
        
        // Generate sample volume progression data
        val volumeProgressionData = ChartEntryModelProducer()
        val volumeEntries = listOf(
            entryOf(0f, 1800f), // 60kg * 10 reps * 3 sets
            entryOf(1f, 1950f), // 65kg * 10 reps * 3 sets
            entryOf(2f, 2100f), // 70kg * 10 reps * 3 sets
            entryOf(3f, 2175f), // 72.5kg * 10 reps * 3 sets
            entryOf(4f, 2250f), // 75kg * 10 reps * 3 sets
            entryOf(5f, 2325f), // 77.5kg * 10 reps * 3 sets
            entryOf(6f, 2400f), // 80kg * 10 reps * 3 sets
            entryOf(7f, 2475f)  // 82.5kg * 10 reps * 3 sets
        )
        volumeProgressionData.setEntries(listOf(volumeEntries))
        
        // Generate sample personal record
        val personalRecord = PersonalRecordData(
            weight = 82.5,
            reps = 10,
            date = "2024-01-15"
        )
        
        // Generate sample exercise history
        val exerciseHistory = listOf(
            ExerciseHistoryEntry("2024-01-15", 82.5, 10, 3),
            ExerciseHistoryEntry("2024-01-12", 80.0, 10, 3),
            ExerciseHistoryEntry("2024-01-10", 77.5, 10, 3),
            ExerciseHistoryEntry("2024-01-08", 75.0, 10, 3),
            ExerciseHistoryEntry("2024-01-05", 72.5, 10, 3),
            ExerciseHistoryEntry("2024-01-03", 70.0, 10, 3),
            ExerciseHistoryEntry("2024-01-01", 65.0, 10, 3),
            ExerciseHistoryEntry("2023-12-29", 60.0, 10, 3)
        )
        
        return SampleExerciseData(
            exerciseName = exerciseName,
            personalRecord = personalRecord,
            weightProgressionData = weightProgressionData,
            volumeProgressionData = volumeProgressionData,
            exerciseHistory = exerciseHistory
        )
    }
}

/**
 * UI state for exercise statistics
 */
sealed class ExerciseStatisticsUiState {
    object Loading : ExerciseStatisticsUiState()
    
    data class Success(
        val exerciseName: String?,
        val personalRecord: PersonalRecordData?,
        val weightProgressionData: ChartEntryModelProducer,
        val volumeProgressionData: ChartEntryModelProducer,
        val exerciseHistory: List<ExerciseHistoryEntry>
    ) : ExerciseStatisticsUiState()
    
    data class Error(val message: String) : ExerciseStatisticsUiState()
}

/**
 * Sample data structure
 */
private data class SampleExerciseData(
    val exerciseName: String,
    val personalRecord: PersonalRecordData,
    val weightProgressionData: ChartEntryModelProducer,
    val volumeProgressionData: ChartEntryModelProducer,
    val exerciseHistory: List<ExerciseHistoryEntry>
)