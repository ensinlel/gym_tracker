package com.example.gym_tracker.feature.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.data.model.Exercise
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import com.example.gym_tracker.core.data.repository.ExerciseRepository
import com.example.gym_tracker.core.common.enums.ExerciseCategory
import com.example.gym_tracker.core.common.enums.MuscleGroup
import com.example.gym_tracker.core.common.enums.Equipment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the Exercise Selection Screen
 */
@HiltViewModel
class ExerciseSelectionViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExerciseSelectionUiState>(ExerciseSelectionUiState.Loading)
    val uiState: StateFlow<ExerciseSelectionUiState> = _uiState.asStateFlow()
    
    private var allExercises: List<Exercise> = emptyList()
    private var currentSearchQuery: String = ""

    init {
        seedDatabaseAndLoadExercises()
    }
    
    /**
     * Seed database with sample exercises and then load them
     */
    private fun seedDatabaseAndLoadExercises() {
        viewModelScope.launch {
            try {
                // First, seed the database with sample exercises if it's empty
                exerciseRepository.seedSampleExercises()
                // Then load the exercises
                loadExercises()
            } catch (e: Exception) {
                // If seeding fails, still try to load existing exercises
                loadExercises()
            }
        }
    }

    /**
     * Load all exercises from the repository
     */
    fun loadExercises() {
        viewModelScope.launch {
            _uiState.value = ExerciseSelectionUiState.Loading
            try {
                allExercises = exerciseRepository.getAllExercises().first()
                filterExercises(currentSearchQuery)
            } catch (e: Exception) {
                _uiState.value = ExerciseSelectionUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Search exercises by name
     */
    fun searchExercises(query: String) {
        currentSearchQuery = query
        filterExercises(query)
    }

    /**
     * Toggle the star status of an exercise
     */
    fun toggleExerciseStar(exerciseId: String, isStarred: Boolean) {
        viewModelScope.launch {
            try {
                analyticsRepository.updateExerciseStarStatus(exerciseId, isStarred)
                
                // Update the local list
                allExercises = allExercises.map { exercise ->
                    if (exercise.id == exerciseId) {
                        exercise.copy(isStarMarked = isStarred)
                    } else {
                        exercise
                    }
                }
                
                // Refresh the filtered list
                filterExercises(currentSearchQuery)
            } catch (e: Exception) {
                // Handle error - could show a snackbar or toast
                // For now, just reload the exercises to revert the change
                loadExercises()
            }
        }
    }

    /**
     * Create a new exercise
     */
    fun createExercise(
        name: String,
        category: ExerciseCategory,
        muscleGroups: List<MuscleGroup>,
        equipment: Equipment
    ) {
        viewModelScope.launch {
            try {
                val newExercise = Exercise(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    category = category,
                    muscleGroups = muscleGroups,
                    equipment = equipment,
                    instructions = emptyList(), // Can be added later
                    createdAt = Instant.now(),
                    updatedAt = Instant.now(),
                    isCustom = true,
                    isStarMarked = false
                )
                
                exerciseRepository.insertExercise(newExercise)
                
                // Reload exercises to include the new one
                loadExercises()
            } catch (e: Exception) {
                // Handle error - could show a snackbar or toast
                _uiState.value = ExerciseSelectionUiState.Error("Failed to create exercise: ${e.message}")
            }
        }
    }

    /**
     * Filter exercises based on search query
     */
    private fun filterExercises(query: String) {
        val filteredExercises = if (query.isBlank()) {
            allExercises
        } else {
            allExercises.filter { exercise ->
                exercise.name.contains(query, ignoreCase = true) ||
                exercise.category.name.contains(query, ignoreCase = true) ||
                exercise.muscleGroups.any { it.name.contains(query, ignoreCase = true) }
            }
        }
        
        _uiState.value = ExerciseSelectionUiState.Success(filteredExercises)
    }
}

/**
 * UI state for the Exercise Selection screen
 */
sealed class ExerciseSelectionUiState {
    object Loading : ExerciseSelectionUiState()
    data class Success(val exercises: List<Exercise>) : ExerciseSelectionUiState()
    data class Error(val message: String) : ExerciseSelectionUiState()
}