package com.example.gym_tracker.feature.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.data.model.ExerciseInstanceWithDetails
import com.example.gym_tracker.core.data.model.ExerciseSet
import com.example.gym_tracker.core.data.repository.ExerciseSetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for Exercise Tracking Screen
 */
@HiltViewModel
class ExerciseTrackingViewModel @Inject constructor(
    private val exerciseSetRepository: ExerciseSetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseTrackingUiState())
    val uiState: StateFlow<ExerciseTrackingUiState> = _uiState.asStateFlow()

    fun loadExerciseInstance(exerciseInstanceWithDetails: ExerciseInstanceWithDetails) {
        _uiState.update { 
            it.copy(
                exerciseInstanceWithDetails = exerciseInstanceWithDetails,
                sets = exerciseInstanceWithDetails.sets
            )
        }
    }

    fun addSet() {
        val currentSets = _uiState.value.sets
        val newSetNumber = currentSets.size + 1
        val exerciseInstanceId = _uiState.value.exerciseInstanceWithDetails?.exerciseInstance?.id ?: return

        val newSet = ExerciseSet(
            id = UUID.randomUUID().toString(),
            exerciseInstanceId = exerciseInstanceId,
            setNumber = newSetNumber,
            weight = 0.0,
            reps = 0
        )

        _uiState.update { 
            it.copy(sets = it.sets + newSet)
        }
    }

    fun updateSet(updatedSet: ExerciseSet) {
        viewModelScope.launch {
            try {
                exerciseSetRepository.updateSet(updatedSet)
                _uiState.update { state ->
                    state.copy(
                        sets = state.sets.map { set ->
                            if (set.id == updatedSet.id) updatedSet else set
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to update set: ${e.message}")
                }
            }
        }
    }

    fun deleteSet(setToDelete: ExerciseSet) {
        viewModelScope.launch {
            try {
                exerciseSetRepository.deleteSet(setToDelete)
                _uiState.update { state ->
                    state.copy(
                        sets = state.sets.filter { it.id != setToDelete.id }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to delete set: ${e.message}")
                }
            }
        }
    }

    fun applyQuickAdd(weight: Double, reps: Int) {
        val currentSets = _uiState.value.sets
        if (currentSets.isNotEmpty()) {
            val lastSet = currentSets.last()
            val updatedSet = lastSet.copy(weight = weight, reps = reps)
            updateSet(updatedSet)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class ExerciseTrackingUiState(
    val exerciseInstanceWithDetails: ExerciseInstanceWithDetails? = null,
    val sets: List<ExerciseSet> = emptyList(),
    val error: String? = null
)