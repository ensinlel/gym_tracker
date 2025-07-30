package com.example.gym_tracker.feature.workout

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gym_tracker.core.data.cache.ExerciseInstanceCache
import com.example.gym_tracker.core.data.model.Exercise
import com.example.gym_tracker.core.data.model.ExerciseInstance
import com.example.gym_tracker.core.data.model.Workout
import com.example.gym_tracker.core.data.repository.impl.ExerciseInstanceRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.ExerciseRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.ExerciseSetRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.WorkoutRepositoryImpl
import com.example.gym_tracker.core.data.util.ModelTransformationOptimizer
import com.example.gym_tracker.core.data.util.PerformanceMonitor
import com.example.gym_tracker.core.database.GymTrackerDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
 * Integration tests for WorkoutExercisesViewModel with database persistence
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class WorkoutExercisesViewModelIntegrationTest {

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
    fun testWorkoutLoadingWithDatabasePersistence() = runTest {
        val workoutId = "test_workout_1"
        val workoutName = "Test Workout"
        
        // Create workout in database
        val workout = Workout(
            id = workoutId,
            name = workoutName,
            description = "Test workout description",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        workoutRepository.insertWorkout(workout)

        // Create exercise in database
        val exercise = Exercise(
            id = "bench_press",
            name = "Bench Press",
            category = "Chest",
            muscleGroups = listOf("Chest", "Triceps"),
            equipment = "Barbell",
            instructions = "Bench press instructions",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isCustom = false
        )
        exerciseRepository.insertExercise(exercise)

        // Load workout exercises
        viewModel.loadWorkoutExercises(workoutId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify UI state
        val uiState = viewModel.uiState.first()
        assertTrue("UI state should be Success", uiState is WorkoutExercisesUiState.Success)
        
        val successState = uiState as WorkoutExercisesUiState.Success
        assertEquals(workoutName, successState.workoutName)
        assertTrue("Should start with empty exercises", successState.exercises.isEmpty())
    }

    @Test
    fun testAddExerciseWithDatabasePersistence() = runTest {
        val workoutId = "test_workout_2"
        val exerciseId = "bench_press"
        
        // Setup workout and exercise
        val workout = Workout(workoutId, "Test Workout", "", System.currentTimeMillis(), System.currentTimeMillis())
        workoutRepository.insertWorkout(workout)
        
        val exercise = Exercise(
            exerciseId, "Bench Press", "Chest", listOf("Chest"), "Barbell", 
            "Instructions", System.currentTimeMillis(), System.currentTimeMillis(), false
        )
        exerciseRepository.insertExercise(exercise)

        // Load workout
        viewModel.loadWorkoutExercises(workoutId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Add exercise
        viewModel.addExerciseToWorkout(exerciseId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify UI state updated
        val uiState = viewModel.uiState.first()
        assertTrue("UI state should be Success", uiState is WorkoutExercisesUiState.Success)
        
        val successState = uiState as WorkoutExercisesUiState.Success
        assertEquals(1, successState.exercises.size)
        assertEquals(exerciseId, successState.exercises.first().exercise.id)

        // Verify persistence by clearing cache and reloading
        cache.clearAll()
        viewModel.loadWorkoutExercises(workoutId)
        testDispatcher.scheduler.advanceUntilIdle()

        val persistedState = viewModel.uiState.first() as WorkoutExercisesUiState.Success
        assertEquals(1, persistedState.exercises.size)
        assertEquals(exerciseId, persistedState.exercises.first().exercise.id)
    }

    @Test
    fun testSetManagementWithDatabasePersistence() = runTest {
        val workoutId = "test_workout_3"
        val exerciseId = "bench_press"
        
        // Setup data
        val workout = Workout(workoutId, "Test Workout", "", System.currentTimeMillis(), System.currentTimeMillis())
        workoutRepository.insertWorkout(workout)
        
        val exercise = Exercise(
            exerciseId, "Bench Press", "Chest", listOf("Chest"), "Barbell", 
            "Instructions", System.currentTimeMillis(), System.currentTimeMillis(), false
        )
        exerciseRepository.insertExercise(exercise)

        // Load workout and add exercise
        viewModel.loadWorkoutExercises(workoutId)
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.addExerciseToWorkout(exerciseId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Get exercise instance ID
        val uiState = viewModel.uiState.first() as WorkoutExercisesUiState.Success
        val exerciseInstanceId = uiState.exercises.first().exerciseInstance.id

        // Add set
        viewModel.addSetToExercise(exerciseInstanceId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify set added
        val stateWithSet = viewModel.uiState.first() as WorkoutExercisesUiState.Success
        assertEquals(1, stateWithSet.exercises.first().sets.size)
        
        val setId = stateWithSet.exercises.first().sets.first().id

        // Update set
        viewModel.updateSet(setId, 100.0, 10)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify set updated
        val updatedState = viewModel.uiState.first() as WorkoutExercisesUiState.Success
        val updatedSet = updatedState.exercises.first().sets.first()
        assertEquals(100.0, updatedSet.weight, 0.01)
        assertEquals(10, updatedSet.reps)

        // Verify persistence
        cache.clearAll()
        viewModel.loadWorkoutExercises(workoutId)
        testDispatcher.scheduler.advanceUntilIdle()

        val persistedState = viewModel.uiState.first() as WorkoutExercisesUiState.Success
        val persistedSet = persistedState.exercises.first().sets.first()
        assertEquals(100.0, persistedSet.weight, 0.01)
        assertEquals(10, persistedSet.reps)
    }

    @Test
    fun testMultipleWorkoutsPersistence() = runTest {
        val workoutIds = listOf("workout_1", "workout_2", "workout_3")
        val exerciseId = "bench_press"
        
        // Setup exercise
        val exercise = Exercise(
            exerciseId, "Bench Press", "Chest", listOf("Chest"), "Barbell", 
            "Instructions", System.currentTimeMillis(), System.currentTimeMillis(), false
        )
        exerciseRepository.insertExercise(exercise)

        // Create workouts and add exercises
        workoutIds.forEachIndexed { index, workoutId ->
            val workout = Workout(workoutId, "Workout ${index + 1}", "", System.currentTimeMillis(), System.currentTimeMillis())
            workoutRepository.insertWorkout(workout)
            
            viewModel.loadWorkoutExercises(workoutId)
            testDispatcher.scheduler.advanceUntilIdle()
            
            // Add different number of exercises to each workout
            repeat(index + 1) {
                viewModel.addExerciseToWorkout(exerciseId)
                testDispatcher.scheduler.advanceUntilIdle()
            }
        }

        // Verify each workout has correct number of exercises
        workoutIds.forEachIndexed { index, workoutId ->
            viewModel.loadWorkoutExercises(workoutId)
            testDispatcher.scheduler.advanceUntilIdle()
            
            val uiState = viewModel.uiState.first() as WorkoutExercisesUiState.Success
            assertEquals("Workout ${index + 1} should have ${index + 1} exercises", 
                        index + 1, uiState.exercises.size)
        }

        // Clear cache and verify persistence
        cache.clearAll()
        
        workoutIds.forEachIndexed { index, workoutId ->
            viewModel.loadWorkoutExercises(workoutId)
            testDispatcher.scheduler.advanceUntilIdle()
            
            val persistedState = viewModel.uiState.first() as WorkoutExercisesUiState.Success
            assertEquals("Persisted workout ${index + 1} should have ${index + 1} exercises", 
                        index + 1, persistedState.exercises.size)
        }
    }

    @Test
    fun testErrorHandlingWithDatabaseFailures() = runTest {
        val workoutId = "error_test_workout"
        
        // Close database to simulate failure
        database.close()
        
        // Attempt to load workout
        viewModel.loadWorkoutExercises(workoutId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Should handle error gracefully
        val uiState = viewModel.uiState.first()
        // The ViewModel should either show an error state or fallback to static storage
        assertTrue("Should handle database failure gracefully", 
                  uiState is WorkoutExercisesUiState.Error || uiState is WorkoutExercisesUiState.Success)
    }

    @Test
    fun testPerformanceWithLargeDataset() = runTest {
        val workoutId = "performance_test_workout"
        val exerciseCount = 50
        
        // Setup workout
        val workout = Workout(workoutId, "Performance Test", "", System.currentTimeMillis(), System.currentTimeMillis())
        workoutRepository.insertWorkout(workout)
        
        // Create many exercises
        repeat(exerciseCount) { index ->
            val exercise = Exercise(
                "exercise_$index", "Exercise $index", "Test", listOf("Test"), "None",
                "Instructions", System.currentTimeMillis(), System.currentTimeMillis(), false
            )
            exerciseRepository.insertExercise(exercise)
        }

        // Pre-populate database with exercise instances
        repeat(exerciseCount) { index ->
            val exerciseInstance = ExerciseInstance(
                "instance_$index", workoutId, "exercise_$index", index + 1, "Test instance"
            )
            exerciseInstanceRepository.insertExerciseInstance(exerciseInstance)
        }

        val startTime = System.currentTimeMillis()
        
        // Load workout with large dataset
        viewModel.loadWorkoutExercises(workoutId)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val loadTime = System.currentTimeMillis() - startTime

        // Verify data loaded correctly
        val uiState = viewModel.uiState.first() as WorkoutExercisesUiState.Success
        assertEquals(exerciseCount, uiState.exercises.size)
        
        // Performance should be reasonable (less than 2 seconds for 50 exercises)
        assertTrue("Large dataset should load in reasonable time", loadTime < 2000)
    }
}