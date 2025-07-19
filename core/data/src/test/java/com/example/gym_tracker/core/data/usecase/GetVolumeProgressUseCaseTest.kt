package com.example.gym_tracker.core.data.usecase

import com.example.gym_tracker.core.data.model.ExerciseImprovement
import com.example.gym_tracker.core.data.model.TrendDirection
import com.example.gym_tracker.core.data.model.VolumeProgress
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetVolumeProgressUseCaseTest {

    private lateinit var analyticsRepository: AnalyticsRepository
    private lateinit var getVolumeProgressUseCase: GetVolumeProgressUseCase

    @Before
    fun setup() {
        analyticsRepository = mockk()
        getVolumeProgressUseCase = GetVolumeProgressUseCase(analyticsRepository)
    }

    @Test
    fun `invoke returns enhanced volume progress with upward trend for significant improvement`() = runTest {
        // Given - Requirement 3.1: Display volume trend showing if total weight lifted is trending up
        val baseVolumeProgress = VolumeProgress(
            totalVolumeThisMonth = 12000.0,
            totalVolumePreviousMonth = 10000.0,
            trendDirection = TrendDirection.UP,
            percentageChange = 20.0,
            mostImprovedExercise = ExerciseImprovement(
                exerciseName = "Bench Press",
                previousBestWeight = 200.0,
                currentBestWeight = 220.0,
                improvementPercentage = 10.0
            )
        )
        coEvery { analyticsRepository.getVolumeProgress() } returns baseVolumeProgress
        coEvery { analyticsRepository.getMostImprovedExercise() } returns baseVolumeProgress.mostImprovedExercise

        // When
        val result = getVolumeProgressUseCase()

        // Then - Requirement 3.3: Compare current month's total volume to previous month
        assertEquals(12000.0, result.totalVolumeThisMonth, 0.01)
        assertEquals(10000.0, result.totalVolumePreviousMonth, 0.01)
        assertEquals(TrendDirection.UP, result.trendDirection)
        assertEquals(20.0, result.percentageChange, 0.01)
        assertNotNull(result.mostImprovedExercise)
        assertEquals("Bench Press", result.mostImprovedExercise!!.exerciseName)
    }

    @Test
    fun `invoke returns enhanced volume progress with downward trend for significant decline`() = runTest {
        // Given - Requirement 3.1: Display volume trend showing if total weight lifted is trending down
        val baseVolumeProgress = VolumeProgress(
            totalVolumeThisMonth = 8000.0,
            totalVolumePreviousMonth = 10000.0,
            trendDirection = TrendDirection.DOWN,
            percentageChange = -20.0,
            mostImprovedExercise = null
        )
        coEvery { analyticsRepository.getVolumeProgress() } returns baseVolumeProgress
        coEvery { analyticsRepository.getMostImprovedExercise() } returns null

        // When
        val result = getVolumeProgressUseCase()

        // Then
        assertEquals(8000.0, result.totalVolumeThisMonth, 0.01)
        assertEquals(10000.0, result.totalVolumePreviousMonth, 0.01)
        assertEquals(TrendDirection.DOWN, result.trendDirection)
        assertEquals(-20.0, result.percentageChange, 0.01)
        assertNull(result.mostImprovedExercise)
    }

    @Test
    fun `invoke returns stable trend for small volume changes`() = runTest {
        // Given - Small change should result in stable trend
        val baseVolumeProgress = VolumeProgress(
            totalVolumeThisMonth = 10100.0,
            totalVolumePreviousMonth = 10000.0,
            trendDirection = TrendDirection.STABLE,
            percentageChange = 1.0,
            mostImprovedExercise = null
        )
        coEvery { analyticsRepository.getVolumeProgress() } returns baseVolumeProgress
        coEvery { analyticsRepository.getMostImprovedExercise() } returns null

        // When
        val result = getVolumeProgressUseCase()

        // Then
        assertEquals(10100.0, result.totalVolumeThisMonth, 0.01)
        assertEquals(10000.0, result.totalVolumePreviousMonth, 0.01)
        assertEquals(TrendDirection.STABLE, result.trendDirection)
        assertEquals(1.0, result.percentageChange, 0.01)
        assertNull(result.mostImprovedExercise)
    }

    @Test
    fun `invoke handles zero previous month volume correctly`() = runTest {
        // Given - Previous month had no volume (new user scenario)
        val baseVolumeProgress = VolumeProgress(
            totalVolumeThisMonth = 5000.0,
            totalVolumePreviousMonth = 0.0,
            trendDirection = TrendDirection.UP,
            percentageChange = 100.0,
            mostImprovedExercise = ExerciseImprovement(
                exerciseName = "Squat",
                previousBestWeight = 0.0,
                currentBestWeight = 150.0,
                improvementPercentage = 100.0
            )
        )
        coEvery { analyticsRepository.getVolumeProgress() } returns baseVolumeProgress
        coEvery { analyticsRepository.getMostImprovedExercise() } returns baseVolumeProgress.mostImprovedExercise

        // When
        val result = getVolumeProgressUseCase()

        // Then
        assertEquals(5000.0, result.totalVolumeThisMonth, 0.01)
        assertEquals(0.0, result.totalVolumePreviousMonth, 0.01)
        assertEquals(TrendDirection.UP, result.trendDirection)
        assertEquals(100.0, result.percentageChange, 0.01)
        assertNotNull(result.mostImprovedExercise)
        assertEquals(100.0, result.mostImprovedExercise!!.improvementPercentage, 0.01)
    }

    @Test
    fun `invoke handles both months with zero volume`() = runTest {
        // Given - Both months have no volume (inactive user)
        val baseVolumeProgress = VolumeProgress(
            totalVolumeThisMonth = 0.0,
            totalVolumePreviousMonth = 0.0,
            trendDirection = TrendDirection.STABLE,
            percentageChange = 0.0,
            mostImprovedExercise = null
        )
        coEvery { analyticsRepository.getVolumeProgress() } returns baseVolumeProgress
        coEvery { analyticsRepository.getMostImprovedExercise() } returns null

        // When
        val result = getVolumeProgressUseCase()

        // Then
        assertEquals(0.0, result.totalVolumeThisMonth, 0.01)
        assertEquals(0.0, result.totalVolumePreviousMonth, 0.01)
        assertEquals(TrendDirection.STABLE, result.trendDirection)
        assertEquals(0.0, result.percentageChange, 0.01)
        assertNull(result.mostImprovedExercise)
    }

    @Test
    fun `invoke enhances exercise improvement percentage calculation`() = runTest {
        // Given - Requirement 3.4: Calculate percentage improvement in weight or reps
        val exerciseImprovement = ExerciseImprovement(
            exerciseName = "Deadlift",
            previousBestWeight = 300.0,
            currentBestWeight = 330.0,
            improvementPercentage = 8.0 // This should be recalculated
        )
        val baseVolumeProgress = VolumeProgress(
            totalVolumeThisMonth = 11000.0,
            totalVolumePreviousMonth = 10000.0,
            trendDirection = TrendDirection.UP,
            percentageChange = 10.0,
            mostImprovedExercise = exerciseImprovement
        )
        coEvery { analyticsRepository.getVolumeProgress() } returns baseVolumeProgress
        coEvery { analyticsRepository.getMostImprovedExercise() } returns exerciseImprovement

        // When
        val result = getVolumeProgressUseCase()

        // Then - Requirement 3.2: Identify and display the most improved exercise
        assertNotNull(result.mostImprovedExercise)
        assertEquals("Deadlift", result.mostImprovedExercise!!.exerciseName)
        assertEquals(300.0, result.mostImprovedExercise!!.previousBestWeight, 0.01)
        assertEquals(330.0, result.mostImprovedExercise!!.currentBestWeight, 0.01)
        // Should recalculate: (330-300)/300 * 100 = 10%
        assertEquals(10.0, result.mostImprovedExercise!!.improvementPercentage, 0.01)
    }

    @Test
    fun `invoke handles exercise improvement with zero previous weight`() = runTest {
        // Given - Exercise with no previous weight (first time doing exercise)
        val exerciseImprovement = ExerciseImprovement(
            exerciseName = "Pull-ups",
            previousBestWeight = 0.0,
            currentBestWeight = 25.0,
            improvementPercentage = 50.0 // This should be recalculated to 100%
        )
        val baseVolumeProgress = VolumeProgress(
            totalVolumeThisMonth = 8000.0,
            totalVolumePreviousMonth = 7000.0,
            trendDirection = TrendDirection.UP,
            percentageChange = 14.3,
            mostImprovedExercise = exerciseImprovement
        )
        coEvery { analyticsRepository.getVolumeProgress() } returns baseVolumeProgress
        coEvery { analyticsRepository.getMostImprovedExercise() } returns exerciseImprovement

        // When
        val result = getVolumeProgressUseCase()

        // Then
        assertNotNull(result.mostImprovedExercise)
        assertEquals("Pull-ups", result.mostImprovedExercise!!.exerciseName)
        assertEquals(0.0, result.mostImprovedExercise!!.previousBestWeight, 0.01)
        assertEquals(25.0, result.mostImprovedExercise!!.currentBestWeight, 0.01)
        // Should be 100% for new exercise
        assertEquals(100.0, result.mostImprovedExercise!!.improvementPercentage, 0.01)
    }

    @Test
    fun `invoke calculates trend direction with enhanced thresholds`() = runTest {
        // Given - Test moderate improvement (5% increase)
        val baseVolumeProgress = VolumeProgress(
            totalVolumeThisMonth = 10500.0,
            totalVolumePreviousMonth = 10000.0,
            trendDirection = TrendDirection.STABLE, // Repository might return stable
            percentageChange = 5.0,
            mostImprovedExercise = null
        )
        coEvery { analyticsRepository.getVolumeProgress() } returns baseVolumeProgress
        coEvery { analyticsRepository.getMostImprovedExercise() } returns null

        // When
        val result = getVolumeProgressUseCase()

        // Then - Should be UP for 5% improvement (>2% threshold)
        assertEquals(TrendDirection.UP, result.trendDirection)
        assertEquals(5.0, result.percentageChange, 0.01)
    }

    @Test
    fun `invoke calculates trend direction for moderate decline`() = runTest {
        // Given - Test moderate decline (5% decrease)
        val baseVolumeProgress = VolumeProgress(
            totalVolumeThisMonth = 9500.0,
            totalVolumePreviousMonth = 10000.0,
            trendDirection = TrendDirection.STABLE, // Repository might return stable
            percentageChange = -5.0,
            mostImprovedExercise = null
        )
        coEvery { analyticsRepository.getVolumeProgress() } returns baseVolumeProgress
        coEvery { analyticsRepository.getMostImprovedExercise() } returns null

        // When
        val result = getVolumeProgressUseCase()

        // Then - Should be DOWN for 5% decline (>2% threshold)
        assertEquals(TrendDirection.DOWN, result.trendDirection)
        assertEquals(-5.0, result.percentageChange, 0.01)
    }

    @Test
    fun `invoke maintains stable trend for minimal changes`() = runTest {
        // Given - Test minimal change (1% increase)
        val baseVolumeProgress = VolumeProgress(
            totalVolumeThisMonth = 10100.0,
            totalVolumePreviousMonth = 10000.0,
            trendDirection = TrendDirection.STABLE,
            percentageChange = 1.0,
            mostImprovedExercise = null
        )
        coEvery { analyticsRepository.getVolumeProgress() } returns baseVolumeProgress
        coEvery { analyticsRepository.getMostImprovedExercise() } returns null

        // When
        val result = getVolumeProgressUseCase()

        // Then - Should remain STABLE for 1% change (<2% threshold)
        assertEquals(TrendDirection.STABLE, result.trendDirection)
        assertEquals(1.0, result.percentageChange, 0.01)
    }
}