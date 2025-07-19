package com.example.gym_tracker.core.data.usecase

import com.example.gym_tracker.core.data.model.Exercise
import com.example.gym_tracker.core.data.model.PersonalRecord
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetPersonalRecordsUseCaseTest {

    private lateinit var analyticsRepository: AnalyticsRepository
    private lateinit var getPersonalRecordsUseCase: GetPersonalRecordsUseCase

    @Before
    fun setup() {
        analyticsRepository = mockk()
        getPersonalRecordsUseCase = GetPersonalRecordsUseCase(analyticsRepository)
    }

    @Test
    fun `invoke returns sorted personal records when star-marked exercises exist`() = runTest {
        // Given - Requirement 4.1: Display PR values for star-marked exercises
        val today = LocalDate.now()
        val personalRecords = listOf(
            PersonalRecord(
                exerciseName = "Bench Press",
                weight = 225.0,
                reps = 5,
                achievedDate = today.minusDays(10),
                isRecent = true
            ),
            PersonalRecord(
                exerciseName = "Squat",
                weight = 315.0,
                reps = 3,
                achievedDate = today.minusDays(20),
                isRecent = true
            ),
            PersonalRecord(
                exerciseName = "Deadlift",
                weight = 405.0,
                reps = 1,
                achievedDate = today.minusDays(40),
                isRecent = false
            )
        )
        coEvery { analyticsRepository.getPersonalRecords() } returns personalRecords

        // When
        val result = getPersonalRecordsUseCase()

        // Then
        assertEquals(3, result.records.size)
        assertFalse(result.shouldSuggestStarMarking)
        assertTrue(result.suggestedExercises.isEmpty())
        
        // Verify records are sorted by recency first, then by weight
        assertTrue(result.records[0].isRecent)
        assertTrue(result.records[1].isRecent)
        assertEquals("Squat", result.records[0].exerciseName) // Heaviest recent exercise
        assertEquals("Bench Press", result.records[1].exerciseName) // Second heaviest recent exercise
        assertEquals("Deadlift", result.records[2].exerciseName) // Non-recent exercise
    }

    @Test
    fun `invoke suggests star-marking when no personal records exist and no exercises are star-marked`() = runTest {
        // Given - Requirement 4.4: When no exercises are star-marked, suggest marking key exercises
        coEvery { analyticsRepository.getPersonalRecords() } returns emptyList()
        coEvery { analyticsRepository.getStarMarkedExercises() } returns flowOf(emptyList())

        // When
        val result = getPersonalRecordsUseCase()

        // Then
        assertTrue(result.records.isEmpty())
        assertTrue(result.shouldSuggestStarMarking)
        assertTrue(result.suggestedExercises.isNotEmpty())
        assertTrue(result.suggestedExercises.contains("Bench Press"))
        assertTrue(result.suggestedExercises.contains("Squat"))
        assertTrue(result.suggestedExercises.contains("Deadlift"))
    }

    @Test
    fun `invoke does not suggest star-marking when no personal records exist but exercises are star-marked`() = runTest {
        // Given - Exercises are star-marked but no PRs recorded yet
        coEvery { analyticsRepository.getPersonalRecords() } returns emptyList()
        coEvery { analyticsRepository.getStarMarkedExercises() } returns flowOf(
            listOf(
                Exercise(id = "1", name = "Bench Press", isStarMarked = true),
                Exercise(id = "2", name = "Squat", isStarMarked = true)
            )
        )

        // When
        val result = getPersonalRecordsUseCase()

        // Then
        assertTrue(result.records.isEmpty())
        assertFalse(result.shouldSuggestStarMarking)
        assertTrue(result.suggestedExercises.isEmpty())
    }

    @Test
    fun `invoke handles mix of recent and old personal records`() = runTest {
        // Given
        val today = LocalDate.now()
        val personalRecords = listOf(
            PersonalRecord(
                exerciseName = "Bench Press",
                weight = 225.0,
                reps = 5,
                achievedDate = today.minusDays(5),
                isRecent = true
            ),
            PersonalRecord(
                exerciseName = "Squat",
                weight = 315.0,
                reps = 3,
                achievedDate = today.minusDays(60),
                isRecent = false
            )
        )
        coEvery { analyticsRepository.getPersonalRecords() } returns personalRecords

        // When
        val result = getPersonalRecordsUseCase()

        // Then
        assertEquals(2, result.records.size)
        assertEquals("Bench Press", result.records[0].exerciseName) // Recent comes first
        assertEquals("Squat", result.records[1].exerciseName) // Non-recent comes second
    }

    @Test
    fun `invoke prioritizes weight when recency is the same`() = runTest {
        // Given - Requirement 4.2: Show only the weight value for simplicity
        val today = LocalDate.now()
        val personalRecords = listOf(
            PersonalRecord(
                exerciseName = "Bench Press",
                weight = 225.0,
                reps = 5,
                achievedDate = today.minusDays(5),
                isRecent = true
            ),
            PersonalRecord(
                exerciseName = "Squat",
                weight = 315.0,
                reps = 3,
                achievedDate = today.minusDays(10),
                isRecent = true
            ),
            PersonalRecord(
                exerciseName = "Deadlift",
                weight = 405.0,
                reps = 1,
                achievedDate = today.minusDays(15),
                isRecent = true
            )
        )
        coEvery { analyticsRepository.getPersonalRecords() } returns personalRecords

        // When
        val result = getPersonalRecordsUseCase()

        // Then
        assertEquals(3, result.records.size)
        assertEquals("Deadlift", result.records[0].exerciseName) // Heaviest weight
        assertEquals("Squat", result.records[1].exerciseName) // Second heaviest
        assertEquals("Bench Press", result.records[2].exerciseName) // Lightest weight
    }
}