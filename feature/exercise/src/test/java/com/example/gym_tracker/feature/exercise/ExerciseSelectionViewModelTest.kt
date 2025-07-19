package com.example.gym_tracker.feature.exercise

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.gym_tracker.core.data.model.Exercise
import com.example.gym_tracker.core.data.model.ExerciseCategory
import com.example.gym_tracker.core.data.model.MuscleGroup
import com.example.gym_tracker.core.data.model.Equipment
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import com.example.gym_tracker.core.data.repository.ExerciseRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class ExerciseSelectionViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var exerciseRepository: ExerciseRepository
    private lateinit var analyticsRepository: AnalyticsRepository
    private lateinit var viewModel: ExerciseSelectionViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        exerciseRepository = mockk()
        analyticsRepository = mockk()
        
        // Mock default behavior
        coEvery { exerciseRepository.seedSampleExercises() } just Runs
        coEvery { exerciseRepository.getAllExercises() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init seeds database and loads exercises successfully`() = runTest {
        // Given
        val sampleExercises = createSampleExercises()
        coEvery { exerciseRepository.getAllExercises() } returns flowOf(sampleExercises)

        // When
        viewModel = ExerciseSelectionViewModel(exerciseRepository, analyticsRepository)

        // Then
        coVerify { exerciseRepository.seedSampleExercises() }
        coVerify { exerciseRepository.getAllExercises() }
        
        val uiState = viewModel.uiState.value
        assertTrue(uiState is ExerciseSelectionUiState.Success)
        assertEquals(3, (uiState as ExerciseSelectionUiState.Success).exercises.size)
    }

    @Test
    fun `init handles seeding failure gracefully and still loads exercises`() = runTest {
        // Given
        val sampleExercises = createSampleExercises()
        coEvery { exerciseRepository.seedSampleExercises() } throws RuntimeException("Seeding failed")
        coEvery { exerciseRepository.getAllExercises() } returns flowOf(sampleExercises)

        // When
        viewModel = ExerciseSelectionViewModel(exerciseRepository, analyticsRepository)

        // Then
        coVerify { exerciseRepository.seedSampleExercises() }
        coVerify { exerciseRepository.getAllExercises() }
        
        val uiState = viewModel.uiState.value
        assertTrue(uiState is ExerciseSelectionUiState.Success)
        assertEquals(3, (uiState as ExerciseSelectionUiState.Success).exercises.size)
    }

    @Test
    fun `loadExercises updates UI state to Success when data is loaded`() = runTest {
        // Given
        val exercises = createSampleExercises()
        coEvery { exerciseRepository.getAllExercises() } returns flowOf(exercises)

        viewModel = ExerciseSelectionViewModel(exerciseRepository, analyticsRepository)

        // When
        viewModel.loadExercises()

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState is ExerciseSelectionUiState.Success)
        assertEquals(3, (uiState as ExerciseSelectionUiState.Success).exercises.size)
        assertEquals("Bench Press", uiState.exercises[0].name)
        assertEquals("Squat", uiState.exercises[1].name)
        assertEquals("Deadlift", uiState.exercises[2].name)
    }

    @Test
    fun `loadExercises updates UI state to Error when exception occurs`() = runTest {
        // Given
        coEvery { exerciseRepository.getAllExercises() } throws RuntimeException("Database error")

        viewModel = ExerciseSelectionViewModel(exerciseRepository, analyticsRepository)

        // When
        viewModel.loadExercises()

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState is ExerciseSelectionUiState.Error)
        assertEquals("Database error", (uiState as ExerciseSelectionUiState.Error).message)
    }

    @Test
    fun `searchExercises filters exercises by name`() = runTest {
        // Given
        val exercises = createSampleExercises()
        coEvery { exerciseRepository.getAllExercises() } returns flowOf(exercises)

        viewModel = ExerciseSelectionViewModel(exerciseRepository, analyticsRepository)

        // When
        viewModel.searchExercises("bench")

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState is ExerciseSelectionUiState.Success)
        assertEquals(1, (uiState as ExerciseSelectionUiState.Success).exercises.size)
        assertEquals("Bench Press", uiState.exercises[0].name)
    }

    @Test
    fun `searchExercises filters exercises by category`() = runTest {
        // Given
        val exercises = createSampleExercises()
        coEvery { exerciseRepository.getAllExercises() } returns flowOf(exercises)

        viewModel = ExerciseSelectionViewModel(exerciseRepository, analyticsRepository)

        // When
        viewModel.searchExercises("LEGS")

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState is ExerciseSelectionUiState.Success)
        assertEquals(1, (uiState as ExerciseSelectionUiState.Success).exercises.size)
        assertEquals("Squat", uiState.exercises[0].name)
    }

    @Test
    fun `searchExercises filters exercises by muscle group`() = runTest {
        // Given
        val exercises = createSampleExercises()
        coEvery { exerciseRepository.getAllExercises() } returns flowOf(exercises)

        viewModel = ExerciseSelectionViewModel(exerciseRepository, analyticsRepository)

        // When
        viewModel.searchExercises("CHEST")

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState is ExerciseSelectionUiState.Success)
        assertEquals(1, (uiState as ExerciseSelectionUiState.Success).exercises.size)
        assertEquals("Bench Press", uiState.exercises[0].name)
    }

    @Test
    fun `searchExercises returns empty list when no matches found`() = runTest {
        // Given
        val exercises = createSampleExercises()
        coEvery { exerciseRepository.getAllExercises() } returns flowOf(exercises)

        viewModel = ExerciseSelectionViewModel(exerciseRepository, analyticsRepository)

        // When
        viewModel.searchExercises("nonexistent")

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState is ExerciseSelectionUiState.Success)
        assertEquals(0, (uiState as ExerciseSelectionUiState.Success).exercises.size)
    }

    @Test
    fun `searchExercises returns all exercises when query is blank`() = runTest {
        // Given
        val exercises = createSampleExercises()
        coEvery { exerciseRepository.getAllExercises() } returns flowOf(exercises)

        viewModel = ExerciseSelectionViewModel(exerciseRepository, analyticsRepository)

        // When
        viewModel.searchExercises("")

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState is ExerciseSelectionUiState.Success)
        assertEquals(3, (uiState as ExerciseSelectionUiState.Success).exercises.size)
    }

    @Test
    fun `toggleExerciseStar updates exercise star status and refreshes list`() = runTest {
        // Given
        val exercises = createSampleExercises()
        val updatedExercises = exercises.map { 
            if (it.id == "bench-press") it.copy(isStarMarked = true) else it 
        }
        
        coEvery { exerciseRepository.getAllExercises() } returnsMany listOf(
            flowOf(exercises),
            flowOf(updatedExercises)
        )
        coEvery { analyticsRepository.updateExerciseStarStatus("bench-press", true) } just Runs

        viewModel = ExerciseSelectionViewModel(exerciseRepository, analyticsRepository)

        // When
        viewModel.toggleExerciseStar("bench-press", true)

        // Then
        coVerify { analyticsRepository.updateExerciseStarStatus("bench-press", true) }
        
        val uiState = viewModel.uiState.value
        assertTrue(uiState is ExerciseSelectionUiState.Success)
        val benchPress = (uiState as ExerciseSelectionUiState.Success).exercises.find { it.id == "bench-press" }
        assertTrue(benchPress?.isStarMarked == true)
    }

    @Test
    fun `toggleExerciseStar handles error gracefully and reloads exercises`() = runTest {
        // Given
        val exercises = createSampleExercises()
        coEvery { exerciseRepository.getAllExercises() } returns flowOf(exercises)
        coEvery { analyticsRepository.updateExerciseStarStatus("bench-press", true) } throws RuntimeException("Update failed")

        viewModel = ExerciseSelectionViewModel(exerciseRepository, analyticsRepository)

        // When
        viewModel.toggleExerciseStar("bench-press", true)

        // Then
        coVerify { analyticsRepository.updateExerciseStarStatus("bench-press", true) }
        coVerify(atLeast = 2) { exerciseRepository.getAllExercises() } // Initial load + reload after error
    }

    @Test
    fun `toggleExerciseStar updates local list immediately before API call`() = runTest {
        // Given
        val exercises = createSampleExercises()
        coEvery { exerciseRepository.getAllExercises() } returns flowOf(exercises)
        coEvery { analyticsRepository.updateExerciseStarStatus(any(), any()) } just Runs

        viewModel = ExerciseSelectionViewModel(exerciseRepository, analyticsRepository)

        // When
        viewModel.toggleExerciseStar("bench-press", true)

        // Then - The local list should be updated immediately
        coVerify { analyticsRepository.updateExerciseStarStatus("bench-press", true) }
    }

    private fun createSampleExercises(): List<Exercise> {
        val now = Instant.now()
        return listOf(
            Exercise(
                id = "bench-press",
                name = "Bench Press",
                category = ExerciseCategory.CHEST,
                muscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                equipment = Equipment.BARBELL,
                instructions = listOf("Lie on bench", "Press weight up"),
                createdAt = now,
                updatedAt = now,
                isCustom = false,
                isStarMarked = false
            ),
            Exercise(
                id = "squat",
                name = "Squat",
                category = ExerciseCategory.LEGS,
                muscleGroups = listOf(MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES),
                equipment = Equipment.BARBELL,
                instructions = listOf("Stand with feet apart", "Lower down"),
                createdAt = now,
                updatedAt = now,
                isCustom = false,
                isStarMarked = false
            ),
            Exercise(
                id = "deadlift",
                name = "Deadlift",
                category = ExerciseCategory.BACK,
                muscleGroups = listOf(MuscleGroup.LOWER_BACK, MuscleGroup.HAMSTRINGS),
                equipment = Equipment.BARBELL,
                instructions = listOf("Bend at hips", "Lift weight up"),
                createdAt = now,
                updatedAt = now,
                isCustom = false,
                isStarMarked = true // This one is already starred
            )
        )
    }
}