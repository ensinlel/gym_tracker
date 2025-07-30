package com.example.gym_tracker.feature.workout.util

import com.example.gym_tracker.feature.workout.WorkoutExercisesUiState
import com.example.gym_tracker.feature.workout.WorkoutExercisesViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

/**
 * Utility class for validating UI behavior consistency across different persistence states
 */
class UIBehaviorValidator {
    
    /**
     * Validate that UI state transitions work correctly with database persistence
     */
    suspend fun validateUIStateTransitions(
        viewModel: WorkoutExercisesViewModel,
        workoutId: String
    ): UIValidationResult {
        val results = mutableListOf<String>()
        var success = true
        
        try {
            // Test 1: Initial loading state
            viewModel.loadWorkoutExercises(workoutId)
            val initialState = viewModel.uiState.first()
            
            when (initialState) {
                is WorkoutExercisesUiState.Loading -> {
                    results.add("✓ Initial loading state handled correctly")
                }
                is WorkoutExercisesUiState.Success -> {
                    results.add("✓ Direct success state (cached or immediate load)")
                }
                is WorkoutExercisesUiState.Error -> {
                    results.add("⚠ Initial error state - may indicate database issues")
                }
            }
            
            // Test 2: Success state validation
            val finalState = viewModel.uiState.first()
            if (finalState is WorkoutExercisesUiState.Success) {
                results.add("✓ Success state achieved")
                
                // Validate workout name
                if (finalState.workoutName != null && finalState.workoutName.isNotEmpty()) {
                    results.add("✓ Workout name displayed correctly")
                } else {
                    results.add("⚠ Workout name missing or empty")
                }
                
                // Validate exercises list
                results.add("✓ Exercises list: ${finalState.exercises.size} exercises")
                
            } else {
                results.add("✗ Failed to reach success state")
                success = false
            }
            
        } catch (e: Exception) {
            results.add("✗ Exception during UI state validation: ${e.message}")
            success = false
        }
        
        return UIValidationResult(success, results)
    }
    
    /**
     * Validate exercise addition behavior
     */
    suspend fun validateExerciseAddition(
        viewModel: WorkoutExercisesViewModel,
        workoutId: String,
        exerciseId: String
    ): UIValidationResult {
        val results = mutableListOf<String>()
        var success = true
        
        try {
            // Load initial state
            viewModel.loadWorkoutExercises(workoutId)
            val initialState = viewModel.uiState.first()
            
            val initialExerciseCount = if (initialState is WorkoutExercisesUiState.Success) {
                initialState.exercises.size
            } else {
                results.add("✗ Could not get initial exercise count")
                return UIValidationResult(false, results)
            }
            
            // Add exercise
            viewModel.addExerciseToWorkout(exerciseId)
            val updatedState = viewModel.uiState.first()
            
            if (updatedState is WorkoutExercisesUiState.Success) {
                val newExerciseCount = updatedState.exercises.size
                
                if (newExerciseCount == initialExerciseCount + 1) {
                    results.add("✓ Exercise added successfully (count: $initialExerciseCount → $newExerciseCount)")
                    
                    // Validate the added exercise
                    val addedExercise = updatedState.exercises.find { it.exercise.id == exerciseId }
                    if (addedExercise != null) {
                        results.add("✓ Added exercise found in list")
                        results.add("✓ Exercise ID: ${addedExercise.exercise.id}")
                        results.add("✓ Exercise name: ${addedExercise.exercise.name}")
                        results.add("✓ Initial sets: ${addedExercise.sets.size}")
                    } else {
                        results.add("✗ Added exercise not found in list")
                        success = false
                    }
                } else {
                    results.add("✗ Exercise count incorrect after addition (expected: ${initialExerciseCount + 1}, actual: $newExerciseCount)")
                    success = false
                }
            } else {
                results.add("✗ UI state not success after exercise addition")
                success = false
            }
            
        } catch (e: Exception) {
            results.add("✗ Exception during exercise addition validation: ${e.message}")
            success = false
        }
        
        return UIValidationResult(success, results)
    }
    
    /**
     * Validate set management behavior
     */
    suspend fun validateSetManagement(
        viewModel: WorkoutExercisesViewModel,
        workoutId: String,
        exerciseId: String
    ): UIValidationResult {
        val results = mutableListOf<String>()
        var success = true
        
        try {
            // Setup: Load workout and add exercise
            viewModel.loadWorkoutExercises(workoutId)
            viewModel.addExerciseToWorkout(exerciseId)
            
            val stateWithExercise = viewModel.uiState.first()
            if (stateWithExercise !is WorkoutExercisesUiState.Success) {
                return UIValidationResult(false, listOf("✗ Could not setup exercise for set management test"))
            }
            
            val exercise = stateWithExercise.exercises.find { it.exercise.id == exerciseId }
            if (exercise == null) {
                return UIValidationResult(false, listOf("✗ Exercise not found for set management test"))
            }
            
            val exerciseInstanceId = exercise.exerciseInstance.id
            val initialSetCount = exercise.sets.size
            
            // Test 1: Add set
            viewModel.addSetToExercise(exerciseInstanceId)
            val stateWithSet = viewModel.uiState.first()
            
            if (stateWithSet is WorkoutExercisesUiState.Success) {
                val updatedExercise = stateWithSet.exercises.find { it.exercise.id == exerciseId }
                if (updatedExercise != null && updatedExercise.sets.size == initialSetCount + 1) {
                    results.add("✓ Set added successfully (count: $initialSetCount → ${updatedExercise.sets.size})")
                    
                    val addedSet = updatedExercise.sets.last()
                    results.add("✓ Set ID: ${addedSet.id}")
                    results.add("✓ Initial weight: ${addedSet.weight}")
                    results.add("✓ Initial reps: ${addedSet.reps}")
                    
                    // Test 2: Update set
                    val newWeight = 100.0
                    val newReps = 10
                    viewModel.updateSet(addedSet.id, newWeight, newReps)
                    
                    val stateWithUpdatedSet = viewModel.uiState.first()
                    if (stateWithUpdatedSet is WorkoutExercisesUiState.Success) {
                        val exerciseWithUpdatedSet = stateWithUpdatedSet.exercises.find { it.exercise.id == exerciseId }
                        val updatedSet = exerciseWithUpdatedSet?.sets?.find { it.id == addedSet.id }
                        
                        if (updatedSet != null && updatedSet.weight == newWeight && updatedSet.reps == newReps) {
                            results.add("✓ Set updated successfully (weight: ${updatedSet.weight}, reps: ${updatedSet.reps})")
                        } else {
                            results.add("✗ Set update failed")
                            success = false
                        }
                    } else {
                        results.add("✗ UI state not success after set update")
                        success = false
                    }
                } else {
                    results.add("✗ Set addition failed")
                    success = false
                }
            } else {
                results.add("✗ UI state not success after set addition")
                success = false
            }
            
        } catch (e: Exception) {
            results.add("✗ Exception during set management validation: ${e.message}")
            success = false
        }
        
        return UIValidationResult(success, results)
    }
    
    /**
     * Validate persistence across simulated app restart
     */
    suspend fun validatePersistenceAcrossRestart(
        viewModel: WorkoutExercisesViewModel,
        workoutId: String,
        exerciseId: String,
        cache: com.example.gym_tracker.core.data.cache.ExerciseInstanceCache
    ): UIValidationResult {
        val results = mutableListOf<String>()
        var success = true
        
        try {
            // Setup: Add exercise and set
            viewModel.loadWorkoutExercises(workoutId)
            viewModel.addExerciseToWorkout(exerciseId)
            
            val stateBeforeRestart = viewModel.uiState.first()
            if (stateBeforeRestart !is WorkoutExercisesUiState.Success) {
                return UIValidationResult(false, listOf("✗ Could not setup data before restart test"))
            }
            
            val exerciseBeforeRestart = stateBeforeRestart.exercises.find { it.exercise.id == exerciseId }
            if (exerciseBeforeRestart == null) {
                return UIValidationResult(false, listOf("✗ Exercise not found before restart test"))
            }
            
            // Add a set
            viewModel.addSetToExercise(exerciseBeforeRestart.exerciseInstance.id)
            val stateWithSet = viewModel.uiState.first()
            
            if (stateWithSet !is WorkoutExercisesUiState.Success) {
                return UIValidationResult(false, listOf("✗ Could not add set before restart test"))
            }
            
            val exerciseWithSet = stateWithSet.exercises.find { it.exercise.id == exerciseId }
            val setCount = exerciseWithSet?.sets?.size ?: 0
            
            results.add("✓ Pre-restart state: ${stateWithSet.exercises.size} exercises, $setCount sets")
            
            // Simulate app restart by clearing cache
            cache.clearAll()
            results.add("✓ Cache cleared (simulating app restart)")
            
            // Reload data
            viewModel.loadWorkoutExercises(workoutId)
            val stateAfterRestart = viewModel.uiState.first()
            
            if (stateAfterRestart is WorkoutExercisesUiState.Success) {
                val exerciseAfterRestart = stateAfterRestart.exercises.find { it.exercise.id == exerciseId }
                
                if (exerciseAfterRestart != null) {
                    results.add("✓ Exercise persisted after restart")
                    results.add("✓ Post-restart sets: ${exerciseAfterRestart.sets.size}")
                    
                    if (exerciseAfterRestart.sets.size == setCount) {
                        results.add("✓ Set count matches after restart")
                    } else {
                        results.add("✗ Set count mismatch after restart (expected: $setCount, actual: ${exerciseAfterRestart.sets.size})")
                        success = false
                    }
                } else {
                    results.add("✗ Exercise not found after restart")
                    success = false
                }
                
                results.add("✓ Post-restart state: ${stateAfterRestart.exercises.size} exercises")
            } else {
                results.add("✗ UI state not success after restart")
                success = false
            }
            
        } catch (e: Exception) {
            results.add("✗ Exception during persistence validation: ${e.message}")
            success = false
        }
        
        return UIValidationResult(success, results)
    }
    
    /**
     * Validate UI performance with various data sizes
     */
    suspend fun validateUIPerformance(
        viewModel: WorkoutExercisesViewModel,
        workoutId: String,
        exerciseIds: List<String>
    ): UIValidationResult {
        val results = mutableListOf<String>()
        var success = true
        
        try {
            val startTime = System.currentTimeMillis()
            
            // Load initial workout
            viewModel.loadWorkoutExercises(workoutId)
            val loadTime = System.currentTimeMillis() - startTime
            results.add("✓ Initial load time: ${loadTime}ms")
            
            // Add exercises and measure performance
            val addStartTime = System.currentTimeMillis()
            exerciseIds.forEach { exerciseId ->
                viewModel.addExerciseToWorkout(exerciseId)
            }
            val addTime = System.currentTimeMillis() - addStartTime
            results.add("✓ Added ${exerciseIds.size} exercises in ${addTime}ms")
            
            // Verify final state
            val finalState = viewModel.uiState.first()
            if (finalState is WorkoutExercisesUiState.Success) {
                results.add("✓ Final state: ${finalState.exercises.size} exercises")
                
                // Performance benchmarks
                if (loadTime < 1000) {
                    results.add("✓ Load performance acceptable (< 1s)")
                } else {
                    results.add("⚠ Load performance slow (> 1s)")
                }
                
                if (addTime < 5000) {
                    results.add("✓ Add performance acceptable (< 5s for ${exerciseIds.size} exercises)")
                } else {
                    results.add("⚠ Add performance slow (> 5s for ${exerciseIds.size} exercises)")
                }
            } else {
                results.add("✗ Final state not success")
                success = false
            }
            
        } catch (e: Exception) {
            results.add("✗ Exception during performance validation: ${e.message}")
            success = false
        }
        
        return UIValidationResult(success, results)
    }
    
    data class UIValidationResult(
        val success: Boolean,
        val details: List<String>
    ) {
        fun printResults() {
            println("UI Validation Result: ${if (success) "SUCCESS" else "FAILURE"}")
            details.forEach { println("  $it") }
        }
    }
}