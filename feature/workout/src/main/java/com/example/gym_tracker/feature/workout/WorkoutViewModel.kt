package com.example.gym_tracker.feature.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.data.model.*
import com.example.gym_tracker.core.data.repository.WorkoutRepository
import com.example.gym_tracker.core.data.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for Workout management screens
 */
@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    init {
        loadWorkouts()
    }

    private fun loadWorkouts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            workoutRepository.getAllWorkoutsWithDetails()
                .catch { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Unknown error occurred"
                        )
                    }
                }
                .collect { workouts ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            workouts = workouts,
                            error = null
                        )
                    }
                }
        }
    }

    fun createNewWorkout(name: String) {
        viewModelScope.launch {
            val newWorkout = Workout(
                id = UUID.randomUUID().toString(),
                name = name,
                templateId = null,
                startTime = Instant.now(),
                endTime = null
            )
            
            try {
                workoutRepository.insertWorkout(newWorkout)
                _uiState.update { it.copy(showCreateDialog = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to create workout: ${e.message}")
                }
            }
        }
    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            try {
                workoutRepository.deleteWorkout(workout)
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to delete workout: ${e.message}")
                }
            }
        }
    }

    fun showCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = true) }
    }

    fun hideCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = false) }
    }

    fun toggleViewMode() {
        _uiState.update { it.copy(isGridView = !it.isGridView) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class WorkoutUiState(
    val isLoading: Boolean = false,
    val workouts: List<WorkoutWithDetails> = emptyList(),
    val isGridView: Boolean = false,
    val showCreateDialog: Boolean = false,
    val error: String? = null
)