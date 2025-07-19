package com.example.gym_tracker.core.data.usecase

import com.example.gym_tracker.core.data.model.MonthlyWorkoutStats
import com.example.gym_tracker.core.data.model.TrendDirection
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetMonthlyStatsUseCaseTest {

    private lateinit var analyticsRepository: AnalyticsRepository
    private lateinit var getMonthlyStatsUseCase: GetMonthlyStatsUseCase

    @Before
    fun setup() {
        analyticsRepository = mockk()
        getMonthlyStatsUseCase = GetMonthlyStatsUseCase(analyticsRepository)
    }

    @Test
    fun `invoke returns enhanced monthly stats with improved trend direction`() = runTest {
        // Given
        val baseStats = MonthlyWorkoutStats(
            currentMonthWorkouts = 12,
            previousMonthWorkouts = 8,
            weeklyAverageCurrentMonth = 3.0,
            weeklyAveragePreviousMonth = 2.0,
            trendDirection = TrendDirection.UP,
            percentageChange = 50.0
        )
        
        coEvery { analyticsRepository.getMonthlyWorkoutStats() } returns baseStats
        coEvery { analyticsRepository.getWorkoutCountForDateRange(any(), any()) } returns 12

        // When
        val result = getMonthlyStatsUseCase()

        // Then
        assertEquals(12, result.currentMonthWorkouts)
        assertEquals(8, result.previousMonthWorkouts)
        assertEquals(TrendDirection.UP, result.trendDirection)
        assertEquals(50.0, result.percentageChange, 0.01)
        assertTrue(result.weeklyAverageCurrentMonth > 0)
        
        coVerify { analyticsRepository.getMonthlyWorkoutStats() }
    }

    @Test
    fun `trend direction is UP when current month exceeds previous month`() = runTest {
        // Given - Requirement 2.4: Display positive trend indicator if current month exceeds previous
        val baseStats = MonthlyWorkoutStats(
            currentMonthWorkouts = 15,
            previousMonthWorkouts = 10,
            weeklyAverageCurrentMonth = 3.75,
            weeklyAveragePreviousMonth = 2.5,
            trendDirection = TrendDirection.STABLE,
            percentageChange = 0.0
        )
        
        coEvery { analyticsRepository.getMonthlyWorkoutStats() } returns baseStats
        coEvery { analyticsRepository.getWorkoutCountForDateRange(any(), any()) } returns 15

        // When
        val result = getMonthlyStatsUseCase()

        // Then
        assertEquals(TrendDirection.UP, result.trendDirection)
        assertEquals(50.0, result.percentageChange, 0.01) // (15-10)/10 * 100 = 50%
    }

    @Test
    fun `trend direction is DOWN when current month is less than previous month`() = runTest {
        // Given - Requirement 2.3: Show difference as positive or negative indicator
        val baseStats = MonthlyWorkoutStats(
            currentMonthWorkouts = 6,
            previousMonthWorkouts = 12,
            weeklyAverageCurrentMonth = 1.5,
            weeklyAveragePreviousMonth = 3.0,
            trendDirection = TrendDirection.STABLE,
            percentageChange = 0.0
        )
        
        coEvery { analyticsRepository.getMonthlyWorkoutStats() } returns baseStats
        coEvery { analyticsRepository.getWorkoutCountForDateRange(any(), any()) } returns 6

        // When
        val result = getMonthlyStatsUseCase()

        // Then
        assertEquals(TrendDirection.DOWN, result.trendDirection)
        assertEquals(-50.0, result.percentageChange, 0.01) // (6-12)/12 * 100 = -50%
    }

    @Test
    fun `trend direction is STABLE when workouts are equal`() = runTest {
        // Given
        val baseStats = MonthlyWorkoutStats(
            currentMonthWorkouts = 10,
            previousMonthWorkouts = 10,
            weeklyAverageCurrentMonth = 2.5,
            weeklyAveragePreviousMonth = 2.5,
            trendDirection = TrendDirection.UP,
            percentageChange = 25.0
        )
        
        coEvery { analyticsRepository.getMonthlyWorkoutStats() } returns baseStats
        coEvery { analyticsRepository.getWorkoutCountForDateRange(any(), any()) } returns 10

        // When
        val result = getMonthlyStatsUseCase()

        // Then
        assertEquals(TrendDirection.STABLE, result.trendDirection)
        assertEquals(0.0, result.percentageChange, 0.01)
    }

    @Test
    fun `handles zero previous month workouts correctly`() = runTest {
        // Given - Edge case: previous month had no workouts
        val baseStats = MonthlyWorkoutStats(
            currentMonthWorkouts = 8,
            previousMonthWorkouts = 0,
            weeklyAverageCurrentMonth = 2.0,
            weeklyAveragePreviousMonth = 0.0,
            trendDirection = TrendDirection.STABLE,
            percentageChange = 0.0
        )
        
        coEvery { analyticsRepository.getMonthlyWorkoutStats() } returns baseStats
        coEvery { analyticsRepository.getWorkoutCountForDateRange(any(), any()) } returns 8

        // When
        val result = getMonthlyStatsUseCase()

        // Then
        assertEquals(TrendDirection.UP, result.trendDirection)
        assertEquals(100.0, result.percentageChange, 0.01) // 100% improvement from 0
    }

    @Test
    fun `handles both months with zero workouts`() = runTest {
        // Given - Edge case: both months have no workouts
        val baseStats = MonthlyWorkoutStats(
            currentMonthWorkouts = 0,
            previousMonthWorkouts = 0,
            weeklyAverageCurrentMonth = 0.0,
            weeklyAveragePreviousMonth = 0.0,
            trendDirection = TrendDirection.UP,
            percentageChange = 50.0
        )
        
        coEvery { analyticsRepository.getMonthlyWorkoutStats() } returns baseStats
        coEvery { analyticsRepository.getWorkoutCountForDateRange(any(), any()) } returns 0

        // When
        val result = getMonthlyStatsUseCase()

        // Then
        assertEquals(TrendDirection.STABLE, result.trendDirection)
        assertEquals(0.0, result.percentageChange, 0.01)
    }

    @Test
    fun `calculates weekly average for incomplete current month`() = runTest {
        // Given - Requirement 2.6: Handle incomplete months with projected averages
        val today = LocalDate.now()
        val baseStats = MonthlyWorkoutStats(
            currentMonthWorkouts = 4,
            previousMonthWorkouts = 12,
            weeklyAverageCurrentMonth = 1.0,
            weeklyAveragePreviousMonth = 3.0,
            trendDirection = TrendDirection.DOWN,
            percentageChange = -66.67
        )
        
        coEvery { analyticsRepository.getMonthlyWorkoutStats() } returns baseStats
        coEvery { analyticsRepository.getWorkoutCountForDateRange(any(), any()) } returns 4

        // When
        val result = getMonthlyStatsUseCase()

        // Then
        // Weekly average should be calculated based on elapsed time in current month
        assertTrue("Weekly average should be positive", result.weeklyAverageCurrentMonth > 0)
        assertEquals(TrendDirection.DOWN, result.trendDirection)
        
        coVerify { analyticsRepository.getWorkoutCountForDateRange(any(), any()) }
    }

    @Test
    fun `preserves original data while enhancing calculations`() = runTest {
        // Given - Ensure original data is preserved (Requirements 2.1, 2.2)
        val baseStats = MonthlyWorkoutStats(
            currentMonthWorkouts = 14,
            previousMonthWorkouts = 11,
            weeklyAverageCurrentMonth = 3.5,
            weeklyAveragePreviousMonth = 2.75,
            trendDirection = TrendDirection.STABLE,
            percentageChange = 0.0
        )
        
        coEvery { analyticsRepository.getMonthlyWorkoutStats() } returns baseStats
        coEvery { analyticsRepository.getWorkoutCountForDateRange(any(), any()) } returns 14

        // When
        val result = getMonthlyStatsUseCase()

        // Then - Requirements 2.1, 2.2: Display current and previous month totals
        assertEquals(14, result.currentMonthWorkouts)
        assertEquals(11, result.previousMonthWorkouts)
        assertEquals(TrendDirection.UP, result.trendDirection)
        
        // Percentage change should be calculated: (14-11)/11 * 100 ≈ 27.27%
        assertEquals(27.27, result.percentageChange, 0.01)
    }
}