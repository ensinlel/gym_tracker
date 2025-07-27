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
    
    // Keep track of current workout ID
    private var currentWorkoutId: String = ""
    
    companion object {
        // Static storage to persist across ViewModel instances until real persistence is implemented
        private val workoutExercisesStorage = mutableMapOf<String, MutableList<WorkoutExerciseInstanceData>>()
        
        // Static storage for workout names to persist across ViewModel instances
        private val workoutNamesStorage = mutableMapOf<String, String>()
        
        // Function to store workout name for an ID
        fun setWorkoutName(workoutId: String, workoutName: String) {
            workoutNamesStorage[workoutId] = workoutName
        }
    }

    /**
     * Load exercises for a specific workout
     */
    fun loadWorkoutExercises(workoutId: String) {
        viewModelScope.launch {
            _uiState.value = WorkoutExercisesUiState.Loading
            try {
                // Store current workout ID
                currentWorkoutId = workoutId
                
                // Get workout name
                val workoutName = getWorkoutName(workoutId)
                
                // Get persisted exercises for this workout, or empty list if none exist
                val exercises = workoutExercisesStorage[workoutId] ?: mutableListOf()
                
                // Debug logging
                println("DEBUG: Loading workout ID='$workoutId', workout name='$workoutName'")
                println("DEBUG: Found ${exercises.size} exercises")
                println("DEBUG: Storage contents: ${workoutExercisesStorage.keys}")
                
                _uiState.value = WorkoutExercisesUiState.Success(
                    workoutName = workoutName,
                    exercises = exercises.toList()
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
            val currentState = _uiState.value
            if (currentState is WorkoutExercisesUiState.Success) {
                // Create a new exercise instance
                val newExercise = WorkoutExerciseInstanceData(
                    exerciseInstance = ExerciseInstanceData(
                        id = "new_${System.currentTimeMillis()}",
                        exerciseId = exerciseId,
                        workoutId = currentWorkoutId
                    ),
                    exercise = ExerciseData(
                        id = exerciseId,
                        name = getExerciseName(exerciseId)
                    ),
                    sets = emptyList(),
                    lastPerformed = null
                )
                
                // Add to persistent storage
                val exercisesList = workoutExercisesStorage.getOrPut(currentWorkoutId) { mutableListOf() }
                exercisesList.add(newExercise)
                
                // Debug logging
                println("DEBUG: Added exercise ${getExerciseName(exerciseId)} to workout $currentWorkoutId")
                println("DEBUG: Workout $currentWorkoutId now has ${exercisesList.size} exercises")
                println("DEBUG: Storage contents: ${workoutExercisesStorage.keys}")
                
                // Update UI state
                _uiState.value = currentState.copy(
                    exercises = exercisesList.toList()
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
                
                // Update persistent storage
                val workoutId = currentState.exercises.firstOrNull()?.exerciseInstance?.workoutId
                if (workoutId != null) {
                    workoutExercisesStorage[workoutId] = updatedExercises.toMutableList()
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
                
                // Update persistent storage
                val workoutId = currentState.exercises.firstOrNull()?.exerciseInstance?.workoutId
                if (workoutId != null) {
                    workoutExercisesStorage[workoutId] = updatedExercises.toMutableList()
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
        
        // Start with empty workout - user can add exercises manually
        val exercises = emptyList<WorkoutExerciseInstanceData>()
        
        return SampleWorkoutData(
            workoutName = workoutName,
            exercises = exercises
        )
    }

    private fun getWorkoutName(workoutId: String): String {
        // First check if we have the name stored
        workoutNamesStorage[workoutId]?.let { return it }
        
        // Fallback to hardcoded mapping for backwards compatibility
        return when (workoutId) {
            "1" -> "Push Day"
            "2" -> "Pull Day"
            "3" -> "Leg Day"
            else -> "Workout"
        }
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