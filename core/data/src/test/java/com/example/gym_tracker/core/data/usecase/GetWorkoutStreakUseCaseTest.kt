package com.example.gym_tracker.core.data.usecase

import com.example.gym_tracker.core.data.model.StreakType
import com.example.gym_tracker.core.data.model.WorkoutStreak
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetWorkoutStreakUseCaseTest {

    private lateinit var analyticsRepository: AnalyticsRepository
    private lateinit var getWorkoutStreakUseCase: GetWorkoutStreakUseCase

    @Before
    fun setup() {
        analyticsRepository = mockk()
        getWorkoutStreakUseCase = GetWorkoutStreakUseCase(analyticsRepository)
    }

    @Test
    fun `invoke returns enhanced streak data with days type for short streaks`() = runTest {
        // Given
        val baseStreak = WorkoutStreak(
            currentStreak = 5,
            longestStreak = 8,
            daysSinceLastWorkout = 0,
            streakType = StreakType.DAYS,
            encouragingMessage = "Basic message"
        )
        coEvery { analyticsRepository.getWorkoutStreak() } returns baseStreak

        // When
        val result = getWorkoutStreakUseCase()

        // Then
        assertEquals(5, result.currentStreak)
        assertEquals(8, result.longestStreak)
        assertEquals(0, result.daysSinceLastWorkout)
        assertEquals(StreakType.DAYS, result.streakType)
        assertTrue(result.encouragingMessage!!.contains("Great job working out today!"))
    }

    @Test
    fun `invoke returns enhanced streak data with weeks type for long streaks`() = runTest {
        // Given
        val baseStreak = WorkoutStreak(
            currentStreak = 15,
            longestStreak = 20,
            daysSinceLastWorkout = 1,
            streakType = StreakType.DAYS,
            encouragingMessage = "Basic message"
        )
        coEvery { analyticsRepository.getWorkoutStreak() } returns baseStreak

        // When
        val result = getWorkoutStreakUseCase()

        // Then
        assertEquals(15, result.currentStreak)
        assertEquals(20, result.longestStreak)
        assertEquals(1, result.daysSinceLastWorkout)
        assertEquals(StreakType.WEEKS, result.streakType)
        assertTrue(result.encouragingMessage!!.contains("15-day streak"))
    }

    @Test
    fun `invoke provides encouraging message for inactive periods over 3 days`() = runTest {
        // Given
        val baseStreak = WorkoutStreak(
            currentStreak = 0,
            longestStreak = 10,
            daysSinceLastWorkout = 5,
            streakType = StreakType.DAYS,
            encouragingMessage = "Basic message"
        )
        coEvery { analyticsRepository.getWorkoutStreak() } returns baseStreak

        // When
        val result = getWorkoutStreakUseCase()

        // Then
        assertEquals(0, result.currentStreak)
        assertEquals(10, result.longestStreak)
        assertEquals(5, result.daysSinceLastWorkout)
        assertTrue(result.encouragingMessage!!.contains("longest streak was 10 days"))
    }

    @Test
    fun `invoke handles new user with no workout history`() = runTest {
        // Given
        val baseStreak = WorkoutStreak(
            currentStreak = 0,
            longestStreak = 0,
            daysSinceLastWorkout = 0,
            streakType = StreakType.DAYS,
            encouragingMessage = "Start your fitness journey today!"
        )
        coEvery { analyticsRepository.getWorkoutStreak() } returns baseStreak

        // When
        val result = getWorkoutStreakUseCase()

        // Then
        assertEquals(0, result.currentStreak)
        assertEquals(0, result.longestStreak)
        assertEquals(0, result.daysSinceLastWorkout)
        assertEquals(StreakType.DAYS, result.streakType)
        assertTrue(result.encouragingMessage!!.contains("fitness journey begins now"))
    }

    @Test
    fun `invoke provides motivational message for long inactive periods`() = runTest {
        // Given
        val baseStreak = WorkoutStreak(
            currentStreak = 0,
            longestStreak = 0,
            daysSinceLastWorkout = 30,
            streakType = StreakType.DAYS,
            encouragingMessage = "Basic message"
        )
        coEvery { analyticsRepository.getWorkoutStreak() } returns baseStreak

        // When
        val result = getWorkoutStreakUseCase()

        // Then
        assertEquals(0, result.currentStreak)
        assertEquals(0, result.longestStreak)
        assertEquals(30, result.daysSinceLastWorkout)
        assertTrue(result.encouragingMessage!!.contains("new opportunity to start fresh"))
    }
}