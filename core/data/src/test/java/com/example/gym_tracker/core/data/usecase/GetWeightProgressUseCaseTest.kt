package com.example.gym_tracker.core.data.usecase

import com.example.gym_tracker.core.data.model.TrendDirection
import com.example.gym_tracker.core.data.model.WeightProgress
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetWeightProgressUseCaseTest {

    private lateinit var analyticsRepository: AnalyticsRepository
    private lateinit var getWeightProgressUseCase: GetWeightProgressUseCase
    private val testUserId = "user123"

    @Before
    fun setup() {
        analyticsRepository = mockk()
        getWeightProgressUseCase = GetWeightProgressUseCase(analyticsRepository)
    }

    @Test
    fun `invoke returns weight trend with up arrow when weight increased by more than 1 kg`() = runTest {
        // Given - Requirement 5.1: Display weight trend with simple up/down arrow
        val weightProgress = WeightProgress(
            currentWeight = 75.0,
            weightThirtyDaysAgo = 72.0,
            weightChange = 3.0,
            trendDirection = TrendDirection.UP,
            isStable = false,
            hasRecentData = true
        )
        coEvery { analyticsRepository.getWeightProgress(testUserId) } returns weightProgress

        // When
        val result = getWeightProgressUseCase(testUserId)

        // Then
        assertEquals(TrendDirection.UP, result.weightProgress.trendDirection)
        assertEquals("+3.0 kg", result.weightChangeFormatted)
        assertEquals("Weight increased", result.weightChangeDescription)
        assertFalse(result.shouldPromptWeightTracking)
        assertFalse(result.shouldPromptForRecentData)
    }

    @Test
    fun `invoke returns weight trend with down arrow when weight decreased by more than 1 kg`() = runTest {
        // Given - Requirement 5.1: Display weight trend with simple up/down arrow
        val weightProgress = WeightProgress(
            currentWeight = 70.0,
            weightThirtyDaysAgo = 73.0,
            weightChange = -3.0,
            trendDirection = TrendDirection.DOWN,
            isStable = false,
            hasRecentData = true
        )
        coEvery { analyticsRepository.getWeightProgress(testUserId) } returns weightProgress

        // When
        val result = getWeightProgressUseCase(testUserId)

        // Then
        assertEquals(TrendDirection.DOWN, result.weightProgress.trendDirection)
        assertEquals("-3.0 kg", result.weightChangeFormatted)
        assertEquals("Weight decreased", result.weightChangeDescription)
        assertFalse(result.shouldPromptWeightTracking)
    }

    @Test
    fun `invoke returns stable indicator when weight change is within 1 kg`() = runTest {
        // Given - Requirement 5.4: When weight trend is stable (within 1 kg) display a stable indicator
        val weightProgress = WeightProgress(
            currentWeight = 74.5,
            weightThirtyDaysAgo = 74.0,
            weightChange = 0.5,
            trendDirection = TrendDirection.STABLE,
            isStable = true,
            hasRecentData = true
        )
        coEvery { analyticsRepository.getWeightProgress(testUserId) } returns weightProgress

        // When
        val result = getWeightProgressUseCase(testUserId)

        // Then
        assertEquals(TrendDirection.STABLE, result.weightProgress.trendDirection)
        assertEquals("+0.5 kg", result.weightChangeFormatted)
        assertEquals("Weight stable (±1 kg)", result.weightChangeDescription)
        assertFalse(result.shouldPromptWeightTracking)
    }

    @Test
    fun `invoke returns prompt flag when no weight data exists`() = runTest {
        // Given - Requirement 5.3: If no weight data exists, display a prompt to start tracking weight
        val weightProgress = WeightProgress(
            currentWeight = null,
            weightThirtyDaysAgo = null,
            weightChange = null,
            trendDirection = TrendDirection.STABLE,
            isStable = true,
            hasRecentData = false
        )
        coEvery { analyticsRepository.getWeightProgress(testUserId) } returns weightProgress

        // When
        val result = getWeightProgressUseCase(testUserId)

        // Then
        assertTrue(result.shouldPromptWeightTracking)
        assertNull(result.weightChangeFormatted)
        assertNull(result.weightChangeDescription)
    }

    @Test
    fun `invoke compares current weight to weight from 30 days ago`() = runTest {
        // Given - Requirement 5.2: Compare current weight to weight from 30 days ago
        val weightProgress = WeightProgress(
            currentWeight = 76.0,
            weightThirtyDaysAgo = 74.0,
            weightChange = 2.0,
            trendDirection = TrendDirection.UP,
            isStable = false,
            hasRecentData = true
        )
        coEvery { analyticsRepository.getWeightProgress(testUserId) } returns weightProgress

        // When
        val result = getWeightProgressUseCase(testUserId)

        // Then
        assertEquals(76.0, result.weightProgress.currentWeight)
        assertEquals(74.0, result.weightProgress.weightThirtyDaysAgo)
        assertEquals(2.0, result.weightProgress.weightChange)
    }

    @Test
    fun `invoke handles case with current weight but no historical data`() = runTest {
        // Given - User has current weight but no 30-day comparison
        val weightProgress = WeightProgress(
            currentWeight = 74.0,
            weightThirtyDaysAgo = null,
            weightChange = null,
            trendDirection = TrendDirection.STABLE,
            isStable = true,
            hasRecentData = true
        )
        coEvery { analyticsRepository.getWeightProgress(testUserId) } returns weightProgress

        // When
        val result = getWeightProgressUseCase(testUserId)

        // Then
        assertFalse(result.shouldPromptWeightTracking) // Don't prompt for tracking since we have current data
        assertNull(result.weightChangeFormatted) // No change to format
        assertNull(result.weightChangeDescription) // No change to describe
        assertEquals(TrendDirection.STABLE, result.weightProgress.trendDirection)
    }

    @Test
    fun `invoke suggests updating weight when data is not recent`() = runTest {
        // Given - User has weight data but it's not recent
        val weightProgress = WeightProgress(
            currentWeight = 74.0,
            weightThirtyDaysAgo = 75.0,
            weightChange = -1.0,
            trendDirection = TrendDirection.STABLE,
            isStable = true,
            hasRecentData = false // Data is not recent
        )
        coEvery { analyticsRepository.getWeightProgress(testUserId) } returns weightProgress

        // When
        val result = getWeightProgressUseCase(testUserId)

        // Then
        assertFalse(result.shouldPromptWeightTracking) // Don't prompt for tracking since we have data
        assertTrue(result.shouldPromptForRecentData) // But do prompt for more recent data
        assertEquals("-1.0 kg", result.weightChangeFormatted)
        assertEquals("Weight stable (±1 kg)", result.weightChangeDescription)
    }

    @Test
    fun `invoke formats weight change with one decimal place`() = runTest {
        // Given - Weight change with more decimal places
        val weightProgress = WeightProgress(
            currentWeight = 74.75,
            weightThirtyDaysAgo = 73.25,
            weightChange = 1.5,
            trendDirection = TrendDirection.UP,
            isStable = false,
            hasRecentData = true
        )
        coEvery { analyticsRepository.getWeightProgress(testUserId) } returns weightProgress

        // When
        val result = getWeightProgressUseCase(testUserId)

        // Then
        assertEquals("+1.5 kg", result.weightChangeFormatted) // Formatted to one decimal place
    }
}