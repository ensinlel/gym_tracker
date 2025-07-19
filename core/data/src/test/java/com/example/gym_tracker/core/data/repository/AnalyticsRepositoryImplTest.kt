package com.example.gym_tracker.core.data.repository

import com.example.gym_tracker.core.data.model.*
import com.example.gym_tracker.core.data.repository.impl.AnalyticsRepositoryImpl
import com.example.gym_tracker.core.database.dao.*
import com.example.gym_tracker.core.database.entity.*
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class AnalyticsRepositoryImplTest {

    private lateinit var workoutDao: WorkoutDao
    private lateinit var exerciseDao: ExerciseDao
    private lateinit var exerciseSetDao: ExerciseSetDao
    private lateinit var weightHistoryDao: WeightHistoryDao
    private lateinit var analyticsRepository: AnalyticsRepositoryImpl

    private val testUserId = "test-user"

    @Before
    fun setup() {
        workoutDao = mockk()
        exerciseDao = mockk()
        exerciseSetDao = mockk()
        weightHistoryDao = mockk()
        
        analyticsRepository = AnalyticsRepositoryImpl(
            workoutDao = workoutDao,
            exerciseDao = exerciseDao,
            exerciseSetDao = exerciseSetDao,
            weightHistoryDao = weightHistoryDao
        )
    }

    @Test
    fun `getWeightHistory returns mapped weight history for user`() = runTest {
        // Given
        val weightHistoryEntities = listOf(
            WeightHistoryEntity(
                id = "1",
                userProfileId = testUserId,
                weight = 74.0,
                recordedDate = LocalDate.now(),
                notes = "Current weight"
            ),
            WeightHistoryEntity(
                id = "2",
                userProfileId = testUserId,
                weight = 75.5,
                recordedDate = LocalDate.now().minusDays(7),
                notes = "Last week"
            )
        )
        coEvery { weightHistoryDao.getWeightHistoryForUser(testUserId) } returns flowOf(weightHistoryEntities)

        // When
        val result = analyticsRepository.getWeightHistory(testUserId).first()

        // Then
        assertEquals(2, result.size)
        assertEquals(74.0, result[0].weight)
        assertEquals("Current weight", result[0].notes)
        assertEquals(75.5, result[1].weight)
        assertEquals("Last week", result[1].notes)
    }

    @Test
    fun `addWeightEntry saves weight entry to database`() = runTest {
        // Given
        val weightHistory = WeightHistory(
            id = "new-entry",
            userProfileId = testUserId,
            weight = 73.5,
            recordedDate = LocalDate.now(),
            notes = "New weight"
        )
        coEvery { weightHistoryDao.insertWeightEntry(any()) } just Runs

        // When
        analyticsRepository.addWeightEntry(weightHistory)

        // Then
        coVerify {
            weightHistoryDao.insertWeightEntry(
                match { entity ->
                    entity.id == "new-entry" &&
                    entity.userProfileId == testUserId &&
                    entity.weight == 73.5 &&
                    entity.notes == "New weight"
                }
            )
        }
    }

    @Test
    fun `getLatestWeightEntry returns most recent weight entry`() = runTest {
        // Given
        val latestEntry = WeightHistoryEntity(
            id = "latest",
            userProfileId = testUserId,
            weight = 74.0,
            recordedDate = LocalDate.now(),
            notes = "Latest"
        )
        coEvery { weightHistoryDao.getLatestWeightEntry(testUserId) } returns latestEntry

        // When
        val result = analyticsRepository.getLatestWeightEntry(testUserId)

        // Then
        assertNotNull(result)
        assertEquals("latest", result?.id)
        assertEquals(74.0, result?.weight)
        assertEquals("Latest", result?.notes)
    }

    @Test
    fun `getLatestWeightEntry returns null when no entries exist`() = runTest {
        // Given
        coEvery { weightHistoryDao.getLatestWeightEntry(testUserId) } returns null

        // When
        val result = analyticsRepository.getLatestWeightEntry(testUserId)

        // Then
        assertNull(result)
    }

    @Test
    fun `getWeightEntryFromDaysAgo returns weight entry from specific days ago`() = runTest {
        // Given
        val thirtyDaysAgo = LocalDate.now().minusDays(30)
        val oldEntry = WeightHistoryEntity(
            id = "old",
            userProfileId = testUserId,
            weight = 77.0,
            recordedDate = thirtyDaysAgo,
            notes = "30 days ago"
        )
        coEvery { weightHistoryDao.getWeightEntryBeforeDate(testUserId, thirtyDaysAgo) } returns oldEntry

        // When
        val result = analyticsRepository.getWeightEntryFromDaysAgo(testUserId, 30)

        // Then
        assertNotNull(result)
        assertEquals("old", result?.id)
        assertEquals(77.0, result?.weight)
        assertEquals("30 days ago", result?.notes)
    }

    @Test
    fun `updateExerciseStarStatus updates exercise star marking`() = runTest {
        // Given
        val exerciseId = "bench-press"
        val existingExercise = ExerciseEntity(
            id = exerciseId,
            name = "Bench Press",
            category = com.example.gym_tracker.core.database.entity.ExerciseCategory.CHEST,
            muscleGroups = listOf(com.example.gym_tracker.core.database.entity.MuscleGroup.CHEST),
            equipment = com.example.gym_tracker.core.database.entity.Equipment.BARBELL,
            instructions = listOf("Press weight"),
            createdAt = java.time.Instant.now(),
            updatedAt = java.time.Instant.now(),
            isCustom = false,
            isStarMarked = false
        )
        val updatedExercise = existingExercise.copy(isStarMarked = true)

        coEvery { exerciseDao.getExerciseById(exerciseId) } returns existingExercise
        coEvery { exerciseDao.updateExercise(any()) } just Runs

        // When
        analyticsRepository.updateExerciseStarStatus(exerciseId, true)

        // Then
        coVerify {
            exerciseDao.updateExercise(
                match { exercise ->
                    exercise.id == exerciseId && exercise.isStarMarked == true
                }
            )
        }
    }

    @Test
    fun `updateExerciseStarStatus handles non-existent exercise gracefully`() = runTest {
        // Given
        val exerciseId = "non-existent"
        coEvery { exerciseDao.getExerciseById(exerciseId) } returns null

        // When
        analyticsRepository.updateExerciseStarStatus(exerciseId, true)

        // Then
        coVerify(exactly = 0) { exerciseDao.updateExercise(any()) }
    }

    @Test
    fun `getWeightProgress calculates weight progress correctly`() = runTest {
        // Given
        val currentWeight = WeightHistoryEntity(
            id = "current",
            userProfileId = testUserId,
            weight = 74.0,
            recordedDate = LocalDate.now(),
            notes = "Current"
        )
        val oldWeight = WeightHistoryEntity(
            id = "old",
            userProfileId = testUserId,
            weight = 77.0,
            recordedDate = LocalDate.now().minusDays(30),
            notes = "30 days ago"
        )

        coEvery { weightHistoryDao.getLatestWeightEntry(testUserId) } returns currentWeight
        coEvery { weightHistoryDao.getWeightEntryBeforeDate(testUserId, any()) } returns oldWeight

        // When
        val result = analyticsRepository.getWeightProgress(testUserId)

        // Then
        assertEquals(74.0, result.currentWeight)
        assertEquals(77.0, result.weightThirtyDaysAgo)
        assertEquals(-3.0, result.weightChange)
        assertEquals(TrendDirection.DOWN, result.trendDirection)
        assertFalse(result.isStable) // 3kg change is not stable (>1kg threshold)
        assertTrue(result.hasRecentData) // Current weight is from today
    }

    @Test
    fun `getWeightProgress handles missing current weight`() = runTest {
        // Given
        coEvery { weightHistoryDao.getLatestWeightEntry(testUserId) } returns null

        // When
        val result = analyticsRepository.getWeightProgress(testUserId)

        // Then
        assertNull(result.currentWeight)
        assertNull(result.weightThirtyDaysAgo)
        assertNull(result.weightChange)
        assertEquals(TrendDirection.STABLE, result.trendDirection)
        assertTrue(result.isStable)
        assertFalse(result.hasRecentData)
    }

    @Test
    fun `getWeightProgress handles missing historical weight`() = runTest {
        // Given
        val currentWeight = WeightHistoryEntity(
            id = "current",
            userProfileId = testUserId,
            weight = 74.0,
            recordedDate = LocalDate.now(),
            notes = "Current"
        )

        coEvery { weightHistoryDao.getLatestWeightEntry(testUserId) } returns currentWeight
        coEvery { weightHistoryDao.getWeightEntryBeforeDate(testUserId, any()) } returns null

        // When
        val result = analyticsRepository.getWeightProgress(testUserId)

        // Then
        assertEquals(74.0, result.currentWeight)
        assertNull(result.weightThirtyDaysAgo)
        assertNull(result.weightChange)
        assertEquals(TrendDirection.STABLE, result.trendDirection)
        assertTrue(result.isStable)
        assertTrue(result.hasRecentData)
    }

    @Test
    fun `getWeightProgress detects stable weight within 1kg threshold`() = runTest {
        // Given
        val currentWeight = WeightHistoryEntity(
            id = "current",
            userProfileId = testUserId,
            weight = 74.5,
            recordedDate = LocalDate.now(),
            notes = "Current"
        )
        val oldWeight = WeightHistoryEntity(
            id = "old",
            userProfileId = testUserId,
            weight = 74.0,
            recordedDate = LocalDate.now().minusDays(30),
            notes = "30 days ago"
        )

        coEvery { weightHistoryDao.getLatestWeightEntry(testUserId) } returns currentWeight
        coEvery { weightHistoryDao.getWeightEntryBeforeDate(testUserId, any()) } returns oldWeight

        // When
        val result = analyticsRepository.getWeightProgress(testUserId)

        // Then
        assertEquals(74.5, result.currentWeight)
        assertEquals(74.0, result.weightThirtyDaysAgo)
        assertEquals(0.5, result.weightChange)
        assertEquals(TrendDirection.STABLE, result.trendDirection)
        assertTrue(result.isStable) // 0.5kg change is stable (<1kg threshold)
    }

    @Test
    fun `getWeightProgress detects old data correctly`() = runTest {
        // Given
        val oldCurrentWeight = WeightHistoryEntity(
            id = "current",
            userProfileId = testUserId,
            weight = 74.0,
            recordedDate = LocalDate.now().minusDays(10), // 10 days old
            notes = "Old current"
        )

        coEvery { weightHistoryDao.getLatestWeightEntry(testUserId) } returns oldCurrentWeight
        coEvery { weightHistoryDao.getWeightEntryBeforeDate(testUserId, any()) } returns null

        // When
        val result = analyticsRepository.getWeightProgress(testUserId)

        // Then
        assertEquals(74.0, result.currentWeight)
        assertFalse(result.hasRecentData) // Data is more than 7 days old
    }
}