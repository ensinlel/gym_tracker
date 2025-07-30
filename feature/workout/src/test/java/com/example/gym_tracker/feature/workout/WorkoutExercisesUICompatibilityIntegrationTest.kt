package com.example.gym_tracker.feature.workout

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gym_tracker.core.data.cache.ExerciseInstanceCache
import com.example.gym_tracker.core.data.model.Exercise
import com.example.gym_tracker.core.data.model.Workout
import com.example.gym_tracker.core.data.repository.impl.ExerciseInstanceRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.ExerciseRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.ExerciseSetRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.WorkoutRepositoryImpl
import com.example.gym_tracker.core.data.util.ModelTransformationOptimizer
import com.example.gym_tracker.core.data.util.PerformanceMonitor
import com.example.gym_tracker.core.database.GymTrackerDatabase
import com.example.gym_tracker.feature.workout.util.UIBehaviorValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Comprehensive UI compatibility integration tests for database persistence
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class WorkoutExercisesUICompatibilityIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: GymTrackerDatabase
    private lateinit var viewModel: WorkoutExercisesViewModel
    private lateinit var exerciseInstanceRepository: ExerciseInstanceRepositoryImpl
    private lateinit var exerciseSetRepository: ExerciseSetRepositoryImpl
    private lateinit var exerciseRepository: ExerciseRepositoryImpl
    private lateinit var workoutRepository: WorkoutRepositoryImpl
    private lateinit var cache: ExerciseInstanceCache
    private lateinit var performanceMonitor: PerformanceMonitor
    private lateinit var modelTransformationOptimizer: ModelTransformationOptimizer
    private lateinit var uiValidator: UIBehaviorValidator

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GymTrackerDatabase::class.java
        ).build()
        
        cache = ExerciseInstanceCache()
        performanceMonitor = PerformanceMonitor()
        modelTransformationOptimizer = ModelTransformationOptimizer()
        uiValidator = UIBehaviorValidator()
        
        exerciseInstanceRepository = ExerciseInstanceRepositoryImpl(
            database.exerciseInstanceDao(),
            cache,
            performanceMonitor
        )
        
        exerciseSetRepository = ExerciseSetRepositoryImpl(
            database.exerciseSetDao()
        )
        
        exerciseRepository = ExerciseRepositoryImpl(
            database.exerciseDao()
        )
        
        workoutRepository = WorkoutRepositoryImpl(
            database.workoutDao()
        )
        
        viewModel = WorkoutExercisesViewModel(
            exerciseInstanceRepository,
            exerciseSetRepository,
            exerciseRepository,
            workoutRepository,
            modelTransformationOptimizer,
            performanceMonitor
        )
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    fun testUIStateTransitionsWithDatabasePersistence() = runTest {
        val workoutId = "ui_state_test"
        val workoutName = "UI State Test Workout"
        
        // Setup test data
        setupTestWorkout(workoutId, workoutName)
        
        // Validate UI state transitions
        val result = uiValidator.validateUIStateTransitions(viewModel, workoutId)
        testDispatcher.scheduler.advanceUntilIdle()
        
        result.printResults()
        assertTrue("UI state transitions should work correctly", result.success)
        assertTrue("Should have validation details", result.details.isNotEmpty())
    }

    @Test
    fun testExerciseAdditionUIBehavior() = runTest {
        val workoutId = "exercise_add_test"
        val workoutName = "Exercise Addition Test"
        val exerciseId = "bench_press"
        
        // Setup test data
        setupTestWorkout(workoutId, workoutName)
        setupTestExercise(exerciseId, "Bench Press")
        
        // Validate exercise addition behavior
        val result = uiValidator.validateExerciseAddition(viewModel, workoutId, exerciseId)
        testDispatcher.scheduler.advanceUntilIdle()
        
        result.printResults()
        assertTrue("Exercise addition should work correctly", result.success)
        assertTrue("Should contain exercise addition details", 
                  result.details.any { it.contains("Exercise added successfully") })
    }

    @Test
    fun testSetManagementUIBehavior() = runTest {
        val workoutId = "set_management_test"
        val workoutName = "Set Management Test"
        val exerciseId = "bench_press"
        
        // Setup test data
        setupTestWorkout(workoutId, workoutName)
        setupTestExercise(exerciseId, "Bench Press")
        
        // Validate set management behavior
        val result = uiValidator.validateSetManagement(viewModel, workoutId, exerciseId)
        testDispatcher.scheduler.advanceUntilIdle()
        
        result.printResults()
        assertTrue("Set management should work correctly", result.success)
        assertTrue("Should contain set addition details", 
                  result.details.any { it.contains("Set added successfully") })
        assertTrue("Should contain set update details", 
                  result.details.any { it.contains("Set updated successfully") })
    }

    @Test
    fun testPersistenceAcrossSimulatedAppRestart() = runTest {
        val workoutId = "persistence_test"
        val workoutName = "Persistence Test"
        val exerciseId = "bench_press"
        
        // Setup test data
        setupTestWorkout(workoutId, workoutName)
        setupTestExercise(exerciseId, "Bench Press")
        
        // Validate persistence across restart
        val result = uiValidator.validatePersistenceAcrossRestart(viewModel, workoutId, exerciseId, cache)
        testDispatcher.scheduler.advanceUntilIdle()
        
        result.printResults()
        assertTrue("Data should persist across app restart", result.success)
        assertTrue("Should contain persistence validation details", 
                  result.details.any { it.contains("persisted after restart") })
    }

    @Test
    fun testUIPerformanceWithVariousDataSizes() = runTest {
        val workoutId = "performance_test"
        val workoutName = "Performance Test"
        
        // Setup test data
        setupTestWorkout(workoutId, workoutName)
        
        // Create multiple exercises for performance testing
        val exerciseIds = (1..10).map { index ->
            val exerciseId = "exercise_$index"
            setupTestExercise(exerciseId, "Exercise $index")
            exerciseId
        }
        
        // Validate UI performance
        val result = uiValidator.validateUIPerformance(viewModel, workoutId, exerciseIds)
        testDispatcher.scheduler.advanceUntilIdle()
        
        result.printResults()
        assertTrue("UI performance should be acceptable", result.success)
        assertTrue("Should contain performance metrics", 
                  result.details.any { it.contains("performance acceptable") })
    }

    @Test
    fun testUIBehaviorWithEmptyWorkout() = runTest {
        val workoutId = "empty_workout_test"
        val workoutName = "Empty Workout Test"
        
        // Setup empty workout
        setupTestWorkout(workoutId, workoutName)
        
        // Validate UI behavior with empty workout
        val result = uiValidator.validateUIStateTransitions(viewModel, workoutId)
        testDispatcher.scheduler.advanceUntilIdle()
        
        result.printResults()
        assertTrue("Empty workout should be handled correctly", result.success)
        
        // Verify empty state
        viewModel.loadWorkoutExercises(workoutId)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val uiState = viewModel.uiState.value
        assertTrue("Should be in success state", uiState is WorkoutExercisesUiState.Success)
        
        val successState = uiState as WorkoutExercisesUiState.Success
        assertEquals("Workout name should match", workoutName, successState.workoutName)
        assertTrue("Should have no exercises", successState.exercises.isEmpty())
    }

    @Test
    fun testUIBehaviorWithMultipleExercisesAndSets() = runTest {
        val workoutId = "complex_workout_test"
        val workoutName = "Complex Workout Test"
        
        // Setup complex workout
        setupTestWorkout(workoutId, workoutName)
        
        val exercises = listOf(
            "bench_press" to "Bench Press",
            "incline_press" to "Incline Press",
            "shoulder_press" to "Shoulder Press"
        )
        
        exercises.forEach { (id, name) ->
            setupTestExercise(id, name)
        }
        
        // Load workout
        viewModel.loadWorkoutExercises(workoutId)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Add all exercises
        exercises.forEach { (id, _) ->
            viewModel.addExerciseToWorkout(id)
            testDispatcher.scheduler.advanceUntilIdle()
        }
        
        // Verify all exercises added
        val stateWithExercises = viewModel.uiState.value as WorkoutExercisesUiState.Success
        assertEquals("Should have all exercises", exercises.size, stateWithExercises.exercises.size)
        
        // Add sets to each exercise
        stateWithExercises.exercises.forEach { exercise ->
            repeat(3) { setIndex ->
                viewModel.addSetToExercise(exercise.exerciseInstance.id)
                testDispatcher.scheduler.advanceUntilIdle()
                
                // Update the set with test data
                val currentState = viewModel.uiState.value as WorkoutExercisesUiState.Success
                val currentExercise = currentState.exercises.find { it.exercise.id == exercise.exercise.id }
                val lastSet = currentExercise?.sets?.lastOrNull()
                
                if (lastSet != null) {
                    viewModel.updateSet(lastSet.id, 80.0 + (setIndex * 10), 10 - setIndex)
                    testDispatcher.scheduler.advanceUntilIdle()
                }
            }
        }
        
        // Verify final state
        val finalState = viewModel.uiState.value as WorkoutExercisesUiState.Success
        assertEquals("Should have all exercises", exercises.size, finalState.exercises.size)
        
        finalState.exercises.forEach { exercise ->
            assertEquals("Each exercise should have 3 sets", 3, exercise.sets.size)
            
            // Verify set data
            exercise.sets.forEachIndexed { index, set ->
                assertTrue("Set weight should be positive", set.weight > 0)
                assertTrue("Set reps should be positive", set.reps > 0)
            }
        }
    }

    @Test
    fun testUIErrorHandlingWithDatabaseFailures() = runTest {
        val workoutId = "error_handling_test"
        
        // Close database to simulate failure
        database.close()
        
        // Attempt to load workout
        viewModel.loadWorkoutExercises(workoutId)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val uiState = viewModel.uiState.value
        
        // UI should handle error gracefully (either error state or fallback to static storage)
        assertTrue("UI should handle database failure gracefully", 
                  uiState is WorkoutExercisesUiState.Error || uiState is WorkoutExercisesUiState.Success)
        
        // If it's an error state, it should have a meaningful message
        if (uiState is WorkoutExercisesUiState.Error) {
            assertFalse("Error message should not be empty", uiState.message.isEmpty())
        }
    }

    @Test
    fun testUIConsistencyAcrossNavigationAndReloading() = runTest {
        val workoutId = "navigation_consistency_test"
        val workoutName = "Navigation Consistency Test"
        val exerciseId = "bench_press"
        
        // Setup test data
        setupTestWorkout(workoutId, workoutName)
        setupTestExercise(exerciseId, "Bench Press")
        
        // Load workout and add exercise
        viewModel.loadWorkoutExercises(workoutId)
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.addExerciseToWorkout(exerciseId)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val stateAfterAdd = viewModel.uiState.value as WorkoutExercisesUiState.Success
        val exerciseCount = stateAfterAdd.exercises.size
        
        // Simulate navigation away and back (reload)
        viewModel.loadWorkoutExercises(workoutId)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val stateAfterReload = viewModel.uiState.value as WorkoutExercisesUiState.Success
        
        // Verify consistency
        assertEquals("Exercise count should be consistent after reload", 
                    exerciseCount, stateAfterReload.exercises.size)
        assertEquals("Workout name should be consistent", 
                    stateAfterAdd.workoutName, stateAfterReload.workoutName)
        
        // Verify exercise data consistency
        val originalExercise = stateAfterAdd.exercises.find { it.exercise.id == exerciseId }
        val reloadedExercise = stateAfterReload.exercises.find { it.exercise.id == exerciseId }
        
        assertNotNull("Original exercise should exist", originalExercise)
        assertNotNull("Reloaded exercise should exist", reloadedExercise)
        
        assertEquals("Exercise names should match", 
                    originalExercise?.exercise?.name, reloadedExercise?.exercise?.name)
    }

    private suspend fun setupTestWorkout(workoutId: String, workoutName: String) {
        val workout = Workout(workoutId, workoutName, "Test workout", System.currentTimeMillis(), System.currentTimeMillis())
        workoutRepository.insertWorkout(workout)
    }

    private suspend fun setupTestExercise(exerciseId: String, exerciseName: String) {
        val exercise = Exercise(
            exerciseId, exerciseName, "Test", listOf("Test"), "None",
            "Test instructions", System.currentTimeMillis(), System.currentTimeMillis(), false
        )
        exerciseRepository.insertExercise(exercise)
    }
}