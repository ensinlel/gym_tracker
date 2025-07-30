package com.example.gym_tracker.feature.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.data.model.Exercise
import com.example.gym_tracker.core.data.model.ExerciseInstance
import com.example.gym_tracker.core.data.model.ExerciseInstanceWithDetails
import com.example.gym_tracker.core.data.model.ExerciseSet
import com.example.gym_tracker.core.data.repository.ExerciseInstanceRepository
import com.example.gym_tracker.core.data.repository.ExerciseRepository
import com.example.gym_tracker.core.data.repository.ExerciseSetRepository
import com.example.gym_tracker.core.data.repository.WorkoutRepository
import com.example.gym_tracker.core.data.util.ModelTransformationOptimizer
import com.example.gym_tracker.core.data.util.PerformanceMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * ViewModel for workout-specific exercise management
 */
@HiltViewModel
class WorkoutExercisesViewModel @Inject constructor(
    private val exerciseInstanceRepository: ExerciseInstanceRepository,
    private val exerciseSetRepository: ExerciseSetRepository,
    private val exerciseRepository: ExerciseRepository,
    private val workoutRepository: WorkoutRepository,
    private val modelTransformationOptimizer: ModelTransformationOptimizer,
    private val performanceMonitor: PerformanceMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkoutExercisesUiState>(WorkoutExercisesUiState.Loading)
    val uiState: StateFlow<WorkoutExercisesUiState> = _uiState.asStateFlow()
    
    // Keep track of current workout ID
    private var currentWorkoutId: String = ""
    
    // Migration flag to track if migration has been attempted
    private var migrationAttempted = false
    
    // Retry mechanism for database operations (simplified - no delays to avoid compilation issues)
    private suspend fun <T> retryDatabaseOperation(
        operation: suspend () -> T,
        maxRetries: Int = 3
    ): T? {
        repeat(maxRetries) { attempt ->
            try {
                return operation()
            } catch (e: Exception) {
                println("DEBUG: Database operation failed (attempt ${attempt + 1}/$maxRetries): ${e.message}")
                // Note: Removed delay to avoid Duration API compilation issues
            }
        }
        return null
    }
    
    /**
     * Migrate static storage data to database
     */
    private suspend fun migrateStaticDataToDatabase() {
        if (migrationAttempted) return
        migrationAttempted = true
        
        println("DEBUG: Starting data migration from static storage to database...")
        
        try {
            var totalMigrated = 0
            
            // Migrate each workout's exercises
            workoutExercisesStorage.forEach { (workoutId, exercises) ->
                println("DEBUG: Migrating workout $workoutId with ${exercises.size} exercises")
                
                exercises.forEach { exerciseData ->
                    try {
                        // Check if this exercise instance already exists in database
                        val existingInstance = exerciseInstanceRepository.getExerciseInstanceById(exerciseData.exerciseInstance.id)
                        
                        if (existingInstance == null) {
                            // Create exercise instance in database
                            val orderInWorkout = exerciseInstanceRepository.getNextOrderInWorkout(workoutId)
                            val exerciseInstance = ExerciseInstance(
                                id = exerciseData.exerciseInstance.id,
                                workoutId = workoutId,
                                exerciseId = exerciseData.exerciseInstance.exerciseId,
                                orderInWorkout = orderInWorkout,
                                notes = ""
                            )
                            
                            exerciseInstanceRepository.insertExerciseInstance(exerciseInstance)
                            println("DEBUG: Migrated exercise instance ${exerciseInstance.id}")
                            
                            // Migrate sets for this exercise
                            exerciseData.sets.forEachIndexed { index, setData ->
                                val exerciseSet = ExerciseSet(
                                    id = setData.id,
                                    exerciseInstanceId = exerciseData.exerciseInstance.id,
                                    setNumber = index + 1,
                                    weight = setData.weight,
                                    reps = setData.reps,
                                    restTime = Duration.ZERO, // Default rest time
                                    notes = ""
                                )
                                
                                exerciseSetRepository.insertSet(exerciseSet)
                                println("DEBUG: Migrated set ${setData.id} for exercise ${exerciseInstance.id}")
                            }
                            
                            totalMigrated++
                        } else {
                            println("DEBUG: Exercise instance ${exerciseData.exerciseInstance.id} already exists in database, skipping")
                        }
                        
                    } catch (e: Exception) {
                        println("DEBUG: Failed to migrate exercise ${exerciseData.exerciseInstance.id}: ${e.message}")
                    }
                }
            }
            
            println("DEBUG: Migration completed. Migrated $totalMigrated exercise instances")
            
            // After successful migration, we can optionally clear static storage
            // but we'll keep it for now as a backup until database is fully stable
            
        } catch (e: Exception) {
            println("DEBUG: Migration failed: ${e.message}")
            e.printStackTrace()
        }
    }
    
    companion object {
        // Temporary static storage - will be removed when database operations are implemented
        private val workoutExercisesStorage = mutableMapOf<String, MutableList<WorkoutExerciseInstanceData>>()
        private val workoutNamesStorage = mutableMapOf<String, String>()
        
        fun setWorkoutName(workoutId: String, workoutName: String) {
            workoutNamesStorage[workoutId] = workoutName
        }
    }

    /**
     * Load exercises for a specific workout
     */
    fun loadWorkoutExercises(workoutId: String) {
        loadWorkoutExercisesWithRetry(workoutId, maxRetries = 2)
    }
    
    /**
     * Load exercises with retry mechanism
     */
    private fun loadWorkoutExercisesWithRetry(workoutId: String, maxRetries: Int) {
        viewModelScope.launch {
            _uiState.value = WorkoutExercisesUiState.Loading
            try {
                // Store current workout ID
                currentWorkoutId = workoutId
                
                // Get workout name
                val workoutName = getWorkoutName(workoutId)
                
                // Attempt data migration from static storage to database
                migrateStaticDataToDatabase()
                
                println("DEBUG: Starting to load workout ID='$workoutId', workout name='$workoutName'")
                
                // Try database first, with proper fallback to static storage
                println("DEBUG: Attempting database integration...")
                
                try {
                    // Direct database loading - much faster and simpler
                    println("DEBUG: Loading from database...")
                    
                    val exerciseInstancesWithDetails = exerciseInstanceRepository
                        .getExerciseInstancesWithDetailsByWorkoutId(workoutId)
                        .first() // Get the first emission immediately
                    
                    println("DEBUG: Database returned ${exerciseInstancesWithDetails.size} exercise instances")
                    
                    // Transform to UI models
                    val dbExercises = exerciseInstancesWithDetails.map { instanceWithDetails ->
                        transformToWorkoutExerciseInstanceData(instanceWithDetails)
                    }
                    
                    println("DEBUG: Successfully transformed ${dbExercises.size} exercises from database")
                    
                    // Update static storage with database data for consistency
                    workoutExercisesStorage[workoutId] = dbExercises.toMutableList()
                    
                    val finalExercises = dbExercises
                    
                    _uiState.value = WorkoutExercisesUiState.Success(
                        workoutName = workoutName,
                        exercises = finalExercises
                    )
                    
                } catch (dbError: Exception) {
                    println("DEBUG: Database integration failed: ${dbError.message}")
                    println("DEBUG: Falling back to static storage...")
                    
                    // Fallback to static storage
                    val exercises = workoutExercisesStorage[workoutId] ?: mutableListOf()
                    
                    println("DEBUG: Static storage has ${exercises.size} exercises for workout $workoutId")
                    
                    _uiState.value = WorkoutExercisesUiState.Success(
                        workoutName = workoutName,
                        exercises = exercises.toList()
                    )
                }
                
            } catch (e: Exception) {
                println("DEBUG: Complete failure loading workout exercises: ${e.message}")
                println("DEBUG: Exception type: ${e.javaClass.simpleName}")
                e.printStackTrace()
                
                // Try one more time with just static storage as final fallback
                try {
                    println("DEBUG: Final fallback - using static storage only")
                    val fallbackWorkoutName = getWorkoutName(workoutId)
                    val exercises = workoutExercisesStorage[workoutId] ?: mutableListOf()
                    _uiState.value = WorkoutExercisesUiState.Success(
                        workoutName = fallbackWorkoutName,
                        exercises = exercises.toList()
                    )
                } catch (fallbackError: Exception) {
                    println("DEBUG: Even static storage failed: ${fallbackError.message}")
                    
                    // If we have retries left, try again (removed delay to avoid compilation issues)
                    if (maxRetries > 0) {
                        println("DEBUG: Retrying... ($maxRetries retries left)")
                        launch {
                            loadWorkoutExercisesWithRetry(workoutId, maxRetries - 1)
                        }
                    } else {
                        _uiState.value = WorkoutExercisesUiState.Error("Failed to load exercises: ${e.message}")
                    }
                }
            }
        }
    }
    
    /**
     * Retry loading exercises (public method for user-initiated retry)
     */
    fun retryLoadWorkoutExercises() {
        if (currentWorkoutId.isNotEmpty()) {
            println("DEBUG: User initiated retry for workout $currentWorkoutId")
            loadWorkoutExercises(currentWorkoutId)
        }
    }
    
    /**
     * Manually trigger data migration (for testing or user-initiated migration)
     */
    fun triggerDataMigration() {
        viewModelScope.launch {
            migrationAttempted = false // Reset flag to allow re-migration
            migrateStaticDataToDatabase()
            // Reload current workout to reflect migrated data
            if (currentWorkoutId.isNotEmpty()) {
                loadWorkoutExercises(currentWorkoutId)
            }
        }
    }

    /**
     * Add an exercise to the workout
     */
    fun addExerciseToWorkout(exerciseId: String) {
        viewModelScope.launch {
            try {
                println("DEBUG: Attempting to add exercise $exerciseId to workout $currentWorkoutId")
                
                val currentState = _uiState.value
                if (currentState is WorkoutExercisesUiState.Success) {
                    val exerciseInstanceId = java.util.UUID.randomUUID().toString()
                    
                    // Create UI model for immediate feedback
                    val newExercise = WorkoutExerciseInstanceData(
                        exerciseInstance = ExerciseInstanceData(
                            id = exerciseInstanceId,
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
                    
                    // PRIORITY: Save to database first to ensure persistence
                    try {
                        // First, verify the workout exists in the database, create if it doesn't
                        var workout = workoutRepository.getWorkoutById(currentWorkoutId)
                        if (workout == null) {
                            println("DEBUG: Workout with ID '$currentWorkoutId' does not exist, creating it...")
                            
                            // Create the workout if it doesn't exist (handles hardcoded IDs)
                            val workoutName = getWorkoutName(currentWorkoutId)
                            val newWorkout = com.example.gym_tracker.core.data.model.Workout(
                                id = currentWorkoutId,
                                name = workoutName,
                                templateId = null,
                                startTime = java.time.Instant.now(),
                                endTime = null
                            )
                            
                            try {
                                workoutRepository.insertWorkout(newWorkout)
                                workout = newWorkout
                                println("DEBUG: Successfully created workout: ${workout.name}")
                            } catch (insertError: Exception) {
                                println("DEBUG: Failed to create workout: ${insertError.message}")
                                _uiState.value = WorkoutExercisesUiState.Error("Failed to create workout. Please try again.")
                                return@launch
                            }
                        }
                        
                        println("DEBUG: Verified workout exists: ${workout.name}")
                        
                        // Ensure sample exercises are seeded in the database
                        exerciseRepository.seedSampleExercises()
                        
                        // Also verify the exercise exists in the database
                        val exercise = exerciseRepository.getExerciseById(exerciseId)
                        if (exercise == null) {
                            println("DEBUG: ERROR - Exercise with ID '$exerciseId' does not exist in database!")
                            println("DEBUG: Available exercises in database:")
                            try {
                                val allExercises = exerciseRepository.getAllExercises().first()
                                allExercises.forEach { ex ->
                                    println("DEBUG: - ID: '${ex.id}', Name: '${ex.name}'")
                                }
                            } catch (e: Exception) {
                                println("DEBUG: Failed to load exercises: ${e.message}")
                            }
                            _uiState.value = WorkoutExercisesUiState.Error("Exercise not found. Please try selecting a different exercise.")
                            return@launch
                        }
                        
                        println("DEBUG: Verified exercise exists: ${exercise.name}")
                        
                        val orderInWorkout = exerciseInstanceRepository.getNextOrderInWorkout(currentWorkoutId)
                        val exerciseInstance = ExerciseInstance(
                            id = exerciseInstanceId,
                            workoutId = currentWorkoutId,
                            exerciseId = exerciseId,
                            orderInWorkout = orderInWorkout,
                            notes = ""
                        )
                        
                        exerciseInstanceRepository.insertExerciseInstance(exerciseInstance)
                        println("DEBUG: Successfully saved exercise to database")
                        
                        // Update static storage for UI consistency (after database success)
                        val exercisesList = workoutExercisesStorage.getOrPut(currentWorkoutId) { mutableListOf() }
                        exercisesList.add(newExercise)
                        
                        // Update UI state after successful database save
                        _uiState.value = currentState.copy(
                            exercises = exercisesList.toList()
                        )
                        
                    } catch (dbError: Exception) {
                        println("DEBUG: Failed to save exercise to database: ${dbError.message}")
                        // Show error since database save failed
                        _uiState.value = WorkoutExercisesUiState.Error("Failed to save exercise. Please try again.")
                        return@launch
                    }
                }
                
            } catch (e: Exception) {
                println("DEBUG: Complete failure adding exercise: ${e.message}")
                e.printStackTrace()
                
                // Show error but don't crash the app
                _uiState.value = WorkoutExercisesUiState.Error("Failed to add exercise. Please try again.")
                
                // Restore the previous state (removed delay to avoid compilation issues)
                launch {
                    loadWorkoutExercises(currentWorkoutId) // Reload to restore previous state
                }
            }
        }
    }

    /**
     * Add a set to an exercise
     */
    fun addSetToExercise(exerciseInstanceId: String) {
        viewModelScope.launch {
            try {
                println("DEBUG: Adding set to exercise instance $exerciseInstanceId")
                
                val currentState = _uiState.value
                if (currentState is WorkoutExercisesUiState.Success) {
                    val setId = "set_${System.currentTimeMillis()}"
                    val updatedExercises = currentState.exercises.map { exercise ->
                        if (exercise.exerciseInstance.id == exerciseInstanceId) {
                            val newSet = WorkoutSetData(
                                id = setId,
                                weight = 0.0,
                                reps = 0
                            )
                            exercise.copy(sets = exercise.sets + newSet)
                        } else {
                            exercise
                        }
                    }
                    
                    // PRIORITY: Save to database first
                    try {
                        val exercise = updatedExercises.find { it.exerciseInstance.id == exerciseInstanceId }
                        val setNumber = exercise?.sets?.size ?: 1
                        
                        val exerciseSet = ExerciseSet(
                            id = setId,
                            exerciseInstanceId = exerciseInstanceId,
                            setNumber = setNumber,
                            weight = 0.0,
                            reps = 0,
                            restTime = Duration.ZERO,
                            notes = ""
                        )
                        
                        exerciseSetRepository.insertSet(exerciseSet)
                        println("DEBUG: Successfully saved set to database")
                        
                        // Update static storage after successful database save
                        workoutExercisesStorage[currentWorkoutId] = updatedExercises.toMutableList()
                        
                        // Update UI state after successful database save
                        _uiState.value = currentState.copy(exercises = updatedExercises)
                        
                        println("DEBUG: Set added successfully - database first, then UI update")
                        
                    } catch (dbError: Exception) {
                        println("DEBUG: Failed to save set to database: ${dbError.message}")
                        _uiState.value = WorkoutExercisesUiState.Error("Failed to save set. Please try again.")
                        return@launch
                    }
                }
                
            } catch (e: Exception) {
                println("DEBUG: Error adding set to exercise: ${e.message}")
                e.printStackTrace()
                // Try to recover current state
                val currentState = _uiState.value
                if (currentState is WorkoutExercisesUiState.Success) {
                    // Keep current state, just log the error
                    println("DEBUG: Maintaining current state despite set add error")
                } else {
                    _uiState.value = WorkoutExercisesUiState.Error("Failed to add set. Please try again.")
                }
            }
        }
    }

    /**
     * Update a set's weight and reps
     */
    fun updateSet(setId: String, weight: Double, reps: Int) {
        viewModelScope.launch {
            try {
                println("DEBUG: Updating set $setId - Weight: $weight, Reps: $reps")
                
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
                    
                    // PRIORITY: Save to database first
                    try {
                        // Find the exercise instance ID for this set
                        var exerciseInstanceId: String? = null
                        var setNumber = 1
                        
                        updatedExercises.forEach { exercise ->
                            exercise.sets.forEachIndexed { index, set ->
                                if (set.id == setId) {
                                    exerciseInstanceId = exercise.exerciseInstance.id
                                    setNumber = index + 1
                                }
                            }
                        }
                        
                        if (exerciseInstanceId != null) {
                            val exerciseSet = ExerciseSet(
                                id = setId,
                                exerciseInstanceId = exerciseInstanceId!!,
                                setNumber = setNumber,
                                weight = weight,
                                reps = reps,
                                restTime = Duration.ZERO,
                                notes = ""
                            )
                            
                            exerciseSetRepository.updateSet(exerciseSet)
                            println("DEBUG: Successfully saved set update to database")
                            
                            // Update static storage after successful database save
                            workoutExercisesStorage[currentWorkoutId] = updatedExercises.toMutableList()
                            
                            // Update UI state after successful database save
                            _uiState.value = currentState.copy(exercises = updatedExercises)
                            
                            println("DEBUG: Set updated successfully - database first, then UI update")
                        } else {
                            println("DEBUG: Could not find exercise instance ID for set $setId")
                            _uiState.value = WorkoutExercisesUiState.Error("Failed to update set. Please try again.")
                        }
                        
                    } catch (dbError: Exception) {
                        println("DEBUG: Failed to save set update to database: ${dbError.message}")
                        _uiState.value = WorkoutExercisesUiState.Error("Failed to save set update. Please try again.")
                        return@launch
                    }
                }
                
            } catch (e: Exception) {
                println("DEBUG: Error updating set: ${e.message}")
                e.printStackTrace()
                // Try to recover current state
                val currentState = _uiState.value
                if (currentState is WorkoutExercisesUiState.Success) {
                    // Keep current state, just log the error
                    println("DEBUG: Maintaining current state despite set update error")
                } else {
                    _uiState.value = WorkoutExercisesUiState.Error("Failed to update set. Please try again.")
                }
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

    /**
     * Transform database ExerciseInstanceWithDetails to UI WorkoutExerciseInstanceData
     * Note: This assumes the ExerciseInstanceWithDetails already includes sets from the database query
     */
    private fun transformToWorkoutExerciseInstanceData(
        instanceWithDetails: ExerciseInstanceWithDetails
    ): WorkoutExerciseInstanceData {
        // Transform sets from domain model to UI model
        val sets = instanceWithDetails.sets.map { exerciseSet ->
            WorkoutSetData(
                id = exerciseSet.id,
                weight = exerciseSet.weight,
                reps = exerciseSet.reps
            )
        }

        // Calculate last performed data from the most recent set
        val lastPerformed = if (sets.isNotEmpty()) {
            val lastSet = sets.last()
            LastPerformedData(
                weight = lastSet.weight,
                reps = lastSet.reps,
                date = "Recent" // TODO: Use actual date from database when available
            )
        } else null

        return WorkoutExerciseInstanceData(
            exerciseInstance = ExerciseInstanceData(
                id = instanceWithDetails.exerciseInstance.id,
                exerciseId = instanceWithDetails.exerciseInstance.exerciseId,
                workoutId = instanceWithDetails.exerciseInstance.workoutId
            ),
            exercise = ExerciseData(
                id = instanceWithDetails.exercise.id,
                name = instanceWithDetails.exercise.name
            ),
            sets = sets,
            lastPerformed = lastPerformed
        )
    }

    /**
     * Enhanced transformation with additional data processing
     */
    private fun transformExerciseInstanceWithSets(
        exerciseInstance: ExerciseInstance,
        exercise: com.example.gym_tracker.core.data.model.Exercise,
        sets: List<ExerciseSet>
    ): WorkoutExerciseInstanceData {
        // Transform sets to UI model
        val uiSets = sets.sortedBy { it.setNumber }.map { exerciseSet ->
            WorkoutSetData(
                id = exerciseSet.id,
                weight = exerciseSet.weight,
                reps = exerciseSet.reps
            )
        }

        // Calculate last performed data
        val lastPerformed = if (uiSets.isNotEmpty()) {
            val lastSet = uiSets.last()
            LastPerformedData(
                weight = lastSet.weight,
                reps = lastSet.reps,
                date = "Recent"
            )
        } else null

        return WorkoutExerciseInstanceData(
            exerciseInstance = ExerciseInstanceData(
                id = exerciseInstance.id,
                exerciseId = exerciseInstance.exerciseId,
                workoutId = exerciseInstance.workoutId
            ),
            exercise = ExerciseData(
                id = exercise.id,
                name = exercise.name
            ),
            sets = uiSets,
            lastPerformed = lastPerformed
        )
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