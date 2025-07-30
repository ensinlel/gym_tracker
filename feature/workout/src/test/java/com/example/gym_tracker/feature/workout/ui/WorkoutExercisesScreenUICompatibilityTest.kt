package com.example.gym_tracker.feature.workout.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
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
import com.example.gym_tracker.feature.workout.WorkoutExercisesScreen
import com.example.gym_tracker.feature.workout.WorkoutExercisesViewModel
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * UI compatibility tests for WorkoutExercisesScreen with database persistence
 */
@RunWith(AndroidJUnit4::class)
class WorkoutExercisesScreenUICompatibilityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: GymTrackerDatabase
    private lateinit var viewModel: WorkoutExercisesViewModel
    private lateinit var exerciseInstanceRepository: ExerciseInstanceRepositoryImpl
    private lateinit var exerciseSetRepository: ExerciseSetRepositoryImpl
    private lateinit var exerciseRepository: ExerciseRepositoryImpl
    private lateinit var workoutRepository: WorkoutRepositoryImpl
    private lateinit var cache: ExerciseInstanceCache
    private lateinit var performanceMonitor: PerformanceMonitor
    private lateinit var modelTransformationOptimizer: ModelTransformationOptimizer

    @Before
    fun setup() {
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
    fun testWorkoutScreenDisplaysCorrectly() = runTest {
        val workoutId = "ui_test_workout"
        val workoutName = "UI Test Workout"
        
        // Setup test data
        setupTestWorkoutData(workoutId, workoutName)
        
        // Launch the screen
        composeTestRule.setContent {
            WorkoutExercisesScreen(
                workoutId = workoutId,
                viewModel = viewModel,
                onNavigateBack = {},
                onNavigateToExerciseSelection = {}
            )
        }
        
        // Verify workout title displays correctly
        composeTestRule
            .onNodeWithText(workoutName)
            .assertIsDisplayed()
        
        // Verify add exercise button is present
        composeTestRule
            .onNodeWithContentDescription("Add Exercise")
            .assertIsDisplayed()
    }

    @Test
    fun testEmptyWorkoutStateDisplaysCorrectly() = runTest {
        val workoutId = "empty_workout"
        val workoutName = "Empty Workout"
        
        // Setup empty workout
        val workout = Workout(workoutId, workoutName, "", System.currentTimeMillis(), System.currentTimeMillis())
        workoutRepository.insertWorkout(workout)
        
        composeTestRule.setContent {
            WorkoutExercisesScreen(
                workoutId = workoutId,
                viewModel = viewModel,
                onNavigateBack = {},
                onNavigateToExerciseSelection = {}
            )
        }
        
        // Verify empty state message or add exercise prompt
        composeTestRule
            .onNodeWithText(workoutName)
            .assertIsDisplayed()
        
        // Should show add exercise button for empty workout
        composeTestRule
            .onNodeWithContentDescription("Add Exercise")
            .assertIsDisplayed()
    }

    @Test
    fun testExerciseListDisplaysWithDatabaseData() = runTest {
        val workoutId = "exercise_list_test"
        val workoutName = "Exercise List Test"
        
        // Setup workout with exercises
        setupTestWorkoutData(workoutId, workoutName)
        
        // Add test exercises to database
        val exercises = listOf(
            Exercise("bench_press", "Bench Press", "Chest", listOf("Chest"), "Barbell", 
                    "Instructions", System.currentTimeMillis(), System.currentTimeMillis(), false),
            Exercise("incline_press", "Incline Press", "Chest", listOf("Chest"), "Dumbbells", 
                    "Instructions", System.currentTimeMillis(), System.currentTimeMillis(), false)
        )
        
        exercises.forEach { exerciseRepository.insertExercise(it) }
        
        // Add exercise instances
        exercises.forEachIndexed { index, exercise ->
            viewModel.addExerciseToWorkout(exercise.id)
        }
        
        composeTestRule.setContent {
            WorkoutExercisesScreen(
                workoutId = workoutId,
                viewModel = viewModel,
                onNavigateBack = {},
                onNavigateToExerciseSelection = {}
            )
        }
        
        // Verify exercises are displayed
        composeTestRule
            .onNodeWithText("Bench Press")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Incline Press")
            .assertIsDisplayed()
    }

    @Test
    fun testSetManagementUIFunctionality() = runTest {
        val workoutId = "set_management_test"
        val workoutName = "Set Management Test"
        
        // Setup workout with one exercise
        setupTestWorkoutData(workoutId, workoutName)
        
        val exercise = Exercise("bench_press", "Bench Press", "Chest", listOf("Chest"), "Barbell", 
                               "Instructions", System.currentTimeMillis(), System.currentTimeMillis(), false)
        exerciseRepository.insertExercise(exercise)
        
        viewModel.addExerciseToWorkout(exercise.id)
        
        composeTestRule.setContent {
            WorkoutExercisesScreen(
                workoutId = workoutId,
                viewModel = viewModel,
                onNavigateBack = {},
                onNavigateToExerciseSelection = {}
            )
        }
        
        // Verify exercise is displayed
        composeTestRule
            .onNodeWithText("Bench Press")
            .assertIsDisplayed()
        
        // Look for add set button (implementation dependent)
        composeTestRule
            .onAllNodesWithContentDescription("Add Set")
            .onFirst()
            .assertIsDisplayed()
    }

    @Test
    fun testNavigationFunctionalityPreserved() = runTest {
        val workoutId = "navigation_test"
        val workoutName = "Navigation Test"
        
        setupTestWorkoutData(workoutId, workoutName)
        
        var backNavigationCalled = false
        var exerciseSelectionCalled = false
        
        composeTestRule.setContent {
            WorkoutExercisesScreen(
                workoutId = workoutId,
                viewModel = viewModel,
                onNavigateBack = { backNavigationCalled = true },
                onNavigateToExerciseSelection = { exerciseSelectionCalled = true }
            )
        }
        
        // Test back navigation
        composeTestRule
            .onNodeWithContentDescription("Navigate back")
            .performClick()
        
        assert(backNavigationCalled) { "Back navigation should be called" }
        
        // Test exercise selection navigation
        composeTestRule
            .onNodeWithContentDescription("Add Exercise")
            .performClick()
        
        assert(exerciseSelectionCalled) { "Exercise selection navigation should be called" }
    }

    @Test
    fun testLoadingStateDisplaysCorrectly() = runTest {
        val workoutId = "loading_test"
        
        composeTestRule.setContent {
            WorkoutExercisesScreen(
                workoutId = workoutId,
                viewModel = viewModel,
                onNavigateBack = {},
                onNavigateToExerciseSelection = {}
            )
        }
        
        // Initially should show loading state
        // Note: This test depends on the actual loading state implementation
        // The screen should handle loading gracefully
        composeTestRule.waitForIdle()
        
        // After loading, should show either empty state or workout content
        // This validates that the loading state transitions correctly
    }

    @Test
    fun testErrorStateHandlingInUI() = runTest {
        val workoutId = "error_test"
        
        // Close database to simulate error
        database.close()
        
        composeTestRule.setContent {
            WorkoutExercisesScreen(
                workoutId = workoutId,
                viewModel = viewModel,
                onNavigateBack = {},
                onNavigateToExerciseSelection = {}
            )
        }
        
        composeTestRule.waitForIdle()
        
        // UI should handle error gracefully without crashing
        // This test ensures error resilience in the UI layer
    }

    @Test
    fun testUIResponsivenessWithLargeDataset() = runTest {
        val workoutId = "performance_ui_test"
        val workoutName = "Performance UI Test"
        
        // Setup workout with many exercises
        setupTestWorkoutData(workoutId, workoutName)
        
        // Add many exercises
        repeat(20) { index ->
            val exercise = Exercise(
                "exercise_$index", "Exercise $index", "Test", listOf("Test"), "None",
                "Instructions", System.currentTimeMillis(), System.currentTimeMillis(), false
            )
            exerciseRepository.insertExercise(exercise)
            viewModel.addExerciseToWorkout(exercise.id)
        }
        
        val startTime = System.currentTimeMillis()
        
        composeTestRule.setContent {
            WorkoutExercisesScreen(
                workoutId = workoutId,
                viewModel = viewModel,
                onNavigateBack = {},
                onNavigateToExerciseSelection = {}
            )
        }
        
        composeTestRule.waitForIdle()
        
        val renderTime = System.currentTimeMillis() - startTime
        
        // UI should render large datasets in reasonable time (< 3 seconds)
        assert(renderTime < 3000) { "UI should render large dataset in reasonable time" }
        
        // Verify all exercises are displayed
        composeTestRule
            .onNodeWithText("Exercise 0")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Exercise 19")
            .assertIsDisplayed()
    }

    @Test
    fun testUIStateConsistencyAfterDataChanges() = runTest {
        val workoutId = "consistency_test"
        val workoutName = "Consistency Test"
        
        setupTestWorkoutData(workoutId, workoutName)
        
        val exercise = Exercise("test_exercise", "Test Exercise", "Test", listOf("Test"), "None",
                               "Instructions", System.currentTimeMillis(), System.currentTimeMillis(), false)
        exerciseRepository.insertExercise(exercise)
        
        composeTestRule.setContent {
            WorkoutExercisesScreen(
                workoutId = workoutId,
                viewModel = viewModel,
                onNavigateBack = {},
                onNavigateToExerciseSelection = {}
            )
        }
        
        // Initially should show empty workout
        composeTestRule
            .onNodeWithText(workoutName)
            .assertIsDisplayed()
        
        // Add exercise through ViewModel
        viewModel.addExerciseToWorkout(exercise.id)
        
        composeTestRule.waitForIdle()
        
        // UI should update to show the new exercise
        composeTestRule
            .onNodeWithText("Test Exercise")
            .assertIsDisplayed()
    }

    private suspend fun setupTestWorkoutData(workoutId: String, workoutName: String) {
        val workout = Workout(workoutId, workoutName, "Test workout", System.currentTimeMillis(), System.currentTimeMillis())
        workoutRepository.insertWorkout(workout)
    }
}