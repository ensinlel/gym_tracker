package com.example.gym_tracker.feature.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for workout-specific exercise management
 */
@HiltViewModel
class WorkoutExercisesViewModel @Inject constructor(
    // TODO: Inject actual repositories when available
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkoutExercisesUiState>(WorkoutExercisesUiState.Loading)
    val uiState: StateFlow<WorkoutExercisesUiState> = _uiState.asStateFlow()

    /**
     * Load exercises for a specific workout
     */
    fun loadWorkoutExercises(workoutId: String) {
        viewModelScope.launch {
            _uiState.value = WorkoutExercisesUiState.Loading
            try {
                // TODO: Replace with actual data loading from repositories
                // For now, generate sample data
                val sampleData = generateSampleWorkoutData(workoutId)
                _uiState.value = WorkoutExercisesUiState.Success(
                    workoutName = sampleData.workoutName,
                    exercises = sampleData.exercises
                )
            } catch (e: Exception) {
                _uiState.value = WorkoutExercisesUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Add an exercise to the workout
     */
    fun addExerciseToWorkout(exerciseId: String) {
        viewModelScope.launch {
            // TODO: Implement actual exercise addition
            // For now, reload the workout to show the change
            val currentState = _uiState.value
            if (currentState is WorkoutExercisesUiState.Success) {
                // Add a new exercise instance
                val newExercise = WorkoutExerciseInstanceData(
                    exerciseInstance = ExerciseInstanceData(
                        id = "new_${System.currentTimeMillis()}",
                        exerciseId = exerciseId,
                        workoutId = "workout1"
                    ),
                    exercise = ExerciseData(
                        id = exerciseId,
                        name = getExerciseName(exerciseId)
                    ),
                    sets = emptyList(),
                    lastPerformed = null
                )
                
                _uiState.value = currentState.copy(
                    exercises = currentState.exercises + newExercise
                )
            }
        }
    }

    /**
     * Add a set to an exercise
     */
    fun addSetToExercise(exerciseInstanceId: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is WorkoutExercisesUiState.Success) {
                val updatedExercises = currentState.exercises.map { exercise ->
                    if (exercise.exerciseInstance.id == exerciseInstanceId) {
                        val newSet = WorkoutSetData(
                            id = "set_${System.currentTimeMillis()}",
                            weight = 0.0,
                            reps = 0
                        )
                        exercise.copy(sets = exercise.sets + newSet)
                    } else {
                        exercise
                    }
                }
                
                _uiState.value = currentState.copy(exercises = updatedExercises)
            }
        }
    }

    /**
     * Update a set's weight and reps
     */
    fun updateSet(setId: String, weight: Double, reps: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is WorkoutExercisesUiState.Success) {
                val updatedExercises = currentState.exercises.map { exercise ->
                    val updatedSets = exercise.sets.map { set ->
                        if (set.id == setId) {
                            set.copy(weight = weight, reps = reps)
                        } else {
                            set
                        }
                    }
                    exercise.copy(sets = updatedSets)
                }
                
                _uiState.value = currentState.copy(exercises = updatedExercises)
            }
        }
    }

    /**
     * Generate sample data for demonstration
     * TODO: Replace with actual data loading
     */
    private fun generateSampleWorkoutData(workoutId: String): SampleWorkoutData {
        val workoutName = when (workoutId) {
            "1" -> "Push Day"
            "2" -> "Pull Day"
            "3" -> "Leg Day"
            else -> "Workout"
        }
        
        // Generate sample exercises for the workout
        val exercises = listOf(
            WorkoutExerciseInstanceData(
                exerciseInstance = ExerciseInstanceData(
                    id = "instance1",
                    exerciseId = "1",
                    workoutId = workoutId
                ),
                exercise = ExerciseData(
                    id = "1",
                    name = "Bench Press"
                ),
                sets = listOf(
                    WorkoutSetData("set1", 60.0, 10),
                    WorkoutSetData("set2", 65.0, 8),
                    WorkoutSetData("set3", 70.0, 6)
                ),
                lastPerformed = LastPerformedData(
                    weight = 70.0,
                    reps = 8,
                    date = "2024-01-10"
                )
            ),
            WorkoutExerciseInstanceData(
                exerciseInstance = ExerciseInstanceData(
                    id = "instance2",
                    exerciseId = "2",
                    workoutId = workoutId
                ),
                exercise = ExerciseData(
                    id = "2",
                    name = "Incline Dumbbell Press"
                ),
                sets = listOf(
                    WorkoutSetData("set4", 25.0, 12),
                    WorkoutSetData("set5", 30.0, 10)
                ),
                lastPerformed = LastPerformedData(
                    weight = 30.0,
                    reps = 10,
                    date = "2024-01-08"
                )
            )
        )
        
        return SampleWorkoutData(
            workoutName = workoutName,
            exercises = exercises
        )
    }

    private fun getExerciseName(exerciseId: String): String {
        return when (exerciseId) {
            "1" -> "Bench Press"
            "2" -> "Incline Dumbbell Press"
            "3" -> "Shoulder Press"
            "4" -> "Tricep Dips"
            "5" -> "Push-ups"
            else -> "Exercise"
        }
    }
}

/**
 * UI state for workout exercises
 */
sealed class WorkoutExercisesUiState {
    object Loading : WorkoutExercisesUiState()
    
    data class Success(
        val workoutName: String?,
        val exercises: List<WorkoutExerciseInstanceData>
    ) : WorkoutExercisesUiState()
    
    data class Error(val message: String) : WorkoutExercisesUiState()
}

/**
 * Sample data structure
 */
private data class SampleWorkoutData(
    val workoutName: String,
    val exercises: List<WorkoutExerciseInstanceData>
)