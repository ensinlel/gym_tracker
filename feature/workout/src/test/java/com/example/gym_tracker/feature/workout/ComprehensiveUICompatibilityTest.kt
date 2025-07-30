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
import com.example.gym_tracker.feature.workout.util.UICompatibilityReportGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Comprehensive UI compatibility test that generates a full compatibility report
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ComprehensiveUICompatibilityTest {

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
    private lateinit var reportGenerator: UICompatibilityReportGenerator

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
        reportGenerator = UICompatibilityReportGenerator()
        
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
    fun generateComprehensiveUICompatibilityReport() = runTest {
        // Setup comprehensive test data
        setupComprehensiveTestData()
        
        // Define comprehensive test scenarios
        val testScenarios = listOf(
            // Basic UI state transitions
            UICompatibilityReportGenerator.UITestScenario(
                name = "Empty Workout State Transitions",
                type = UICompatibilityReportGenerator.UITestType.STATE_TRANSITIONS,
                workoutId = "empty_workout",
                description = "Test UI state transitions with empty workout"
            ),
            
            UICompatibilityReportGenerator.UITestScenario(
                name = "Populated Workout State Transitions",
                type = UICompatibilityReportGenerator.UITestType.STATE_TRANSITIONS,
                workoutId = "populated_workout",
                description = "Test UI state transitions with pre-populated workout"
            ),
            
            // Exercise addition scenarios
            UICompatibilityReportGenerator.UITestScenario(
                name = "Single Exercise Addition",
                type = UICompatibilityReportGenerator.UITestType.EXERCISE_ADDITION,
                workoutId = "exercise_add_test",
                exerciseId = "bench_press",
                description = "Test adding a single exercise to empty workout"
            ),
            
            UICompatibilityReportGenerator.UITestScenario(
                name = "Multiple Exercise Addition",
                type = UICompatibilityReportGenerator.UITestType.EXERCISE_ADDITION,
                workoutId = "multi_exercise_test",
                exerciseId = "incline_press",
                description = "Test adding exercise to workout with existing exercises"
            ),
            
            // Set management scenarios
            UICompatibilityReportGenerator.UITestScenario(
                name = "Basic Set Management",
                type = UICompatibilityReportGenerator.UITestType.SET_MANAGEMENT,
                workoutId = "set_management_basic",
                exerciseId = "bench_press",
                description = "Test adding and updating sets for an exercise"
            ),
            
            UICompatibilityReportGenerator.UITestScenario(
                name = "Complex Set Management",
                type = UICompatibilityReportGenerator.UITestType.SET_MANAGEMENT,
                workoutId = "set_management_complex",
                exerciseId = "shoulder_press",
                description = "Test set management with multiple sets and updates"
            ),
            
            // Persistence scenarios
            UICompatibilityReportGenerator.UITestScenario(
                name = "Simple Persistence Test",
                type = UICompatibilityReportGenerator.UITestType.PERSISTENCE_RESTART,
                workoutId = "persistence_simple",
                exerciseId = "bench_press",
                description = "Test data persistence across simulated app restart"
            ),
            
            UICompatibilityReportGenerator.UITestScenario(
                name = "Complex Persistence Test",
                type = UICompatibilityReportGenerator.UITestType.PERSISTENCE_RESTART,
                workoutId = "persistence_complex",
                exerciseId = "incline_press",
                description = "Test persistence with multiple exercises and sets"
            ),
            
            // Performance scenarios
            UICompatibilityReportGenerator.UITestScenario(
                name = "Small Dataset Performance",
                type = UICompatibilityReportGenerator.UITestType.PERFORMANCE,
                workoutId = "performance_small",
                exerciseIds = listOf("bench_press", "incline_press", "shoulder_press"),
                description = "Test UI performance with small dataset (3 exercises)"
            ),
            
            UICompatibilityReportGenerator.UITestScenario(
                name = "Medium Dataset Performance",
                type = UICompatibilityReportGenerator.UITestType.PERFORMANCE,
                workoutId = "performance_medium",
                exerciseIds = (1..10).map { "exercise_$it" },
                description = "Test UI performance with medium dataset (10 exercises)"
            ),
            
            UICompatibilityReportGenerator.UITestScenario(
                name = "Large Dataset Performance",
                type = UICompatibilityReportGenerator.UITestType.PERFORMANCE,
                workoutId = "performance_large",
                exerciseIds = (1..25).map { "exercise_$it" },
                description = "Test UI performance with large dataset (25 exercises)"
            )
        )
        
        // Generate comprehensive report
        val report = reportGenerator.generateCompatibilityReport(viewModel, cache, testScenarios)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Format and print report
        val formattedReport = reportGenerator.formatReport(report)
        println(formattedReport)
        
        // Validate overall results
        val successRate = (report.passedTests.toDouble() / report.totalTests) * 100
        
        // Assert minimum success rate (should be high for good compatibility)
        assert(successRate >= 80.0) { 
            "UI compatibility success rate should be at least 80%, but was ${String.format("%.1f", successRate)}%" 
        }
        
        // Assert no critical failures
        val criticalFailures = report.results.filter { 
            !it.success && (it.scenario.type == UICompatibilityReportGenerator.UITestType.STATE_TRANSITIONS ||
                           it.scenario.type == UICompatibilityReportGenerator.UITestType.PERSISTENCE_RESTART)
        }
        
        assert(criticalFailures.isEmpty()) {
            "Critical UI compatibility failures detected: ${criticalFailures.map { it.scenario.name }}"
        }
        
        // Assert reasonable performance
        val performanceResults = report.results.filter { 
            it.scenario.type == UICompatibilityReportGenerator.UITestType.PERFORMANCE 
        }
        
        val slowPerformanceTests = performanceResults.filter { it.executionTimeMs > 5000 }
        assert(slowPerformanceTests.isEmpty()) {
            "Performance tests taking too long (>5s): ${slowPerformanceTests.map { "${it.scenario.name}: ${it.executionTimeMs}ms" }}"
        }
        
        println("\n✅ UI Compatibility Report Generated Successfully!")
        println("📊 Success Rate: ${String.format("%.1f", successRate)}%")
        println("⏱️ Total Execution Time: ${report.totalExecutionTimeMs}ms")
        println("🧪 Total Tests: ${report.totalTests} (${report.passedTests} passed, ${report.failedTests} failed)")
    }

    private suspend fun setupComprehensiveTestData() {
        // Create test workouts
        val workouts = listOf(
            "empty_workout" to "Empty Workout",
            "populated_workout" to "Populated Workout",
            "exercise_add_test" to "Exercise Addition Test",
            "multi_exercise_test" to "Multi Exercise Test",
            "set_management_basic" to "Basic Set Management",
            "set_management_complex" to "Complex Set Management",
            "persistence_simple" to "Simple Persistence",
            "persistence_complex" to "Complex Persistence",
            "performance_small" to "Small Performance Test",
            "performance_medium" to "Medium Performance Test",
            "performance_large" to "Large Performance Test"
        )
        
        workouts.forEach { (id, name) ->
            val workout = Workout(id, name, "Test workout", System.currentTimeMillis(), System.currentTimeMillis())
            workoutRepository.insertWorkout(workout)
        }
        
        // Create test exercises
        val exercises = listOf(
            "bench_press" to "Bench Press",
            "incline_press" to "Incline Press",
            "shoulder_press" to "Shoulder Press",
            "tricep_dips" to "Tricep Dips",
            "push_ups" to "Push-ups"
        )
        
        exercises.forEach { (id, name) ->
            val exercise = Exercise(
                id, name, "Test", listOf("Test"), "None",
                "Test instructions", System.currentTimeMillis(), System.currentTimeMillis(), false
            )
            exerciseRepository.insertExercise(exercise)
        }
        
        // Create additional exercises for performance testing
        (1..25).forEach { index ->
            val exercise = Exercise(
                "exercise_$index", "Exercise $index", "Test", listOf("Test"), "None",
                "Test instructions", System.currentTimeMillis(), System.currentTimeMillis(), false
            )
            exerciseRepository.insertExercise(exercise)
        }
        
        // Pre-populate some workouts for testing
        viewModel.loadWorkoutExercises("populated_workout")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.addExerciseToWorkout("bench_press")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.addExerciseToWorkout("incline_press")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Add some sets to the populated workout
        val populatedState = viewModel.uiState.value as? WorkoutExercisesUiState.Success
        populatedState?.exercises?.forEach { exercise ->
            viewModel.addSetToExercise(exercise.exerciseInstance.id)
            testDispatcher.scheduler.advanceUntilIdle()
        }
    }
}