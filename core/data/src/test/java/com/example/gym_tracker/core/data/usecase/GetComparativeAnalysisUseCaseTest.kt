package com.example.gym_tracker.core.data.usecase

import com.example.gym_tracker.core.data.model.*
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate

/**
 * Unit tests for GetComparativeAnalysisUseCase
 */
class GetComparativeAnalysisUseCaseTest {
    
    private lateinit var analyticsRepository: AnalyticsRepository
    private lateinit var useCase: GetComparativeAnalysisUseCase
    
    @Before
    fun setup() {
        analyticsRepository = mockk()
        useCase = GetComparativeAnalysisUseCase(analyticsRepository)
    }
    
    @Test
    fun `invoke returns comprehensive comparative analysis data`() = runTest {
        // Given
        val mockPeriodData = ComparisonPeriod(
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 1, 31),
            totalVolume = 5000.0,
            averageWeight = 100.0,
            workoutCount = 12,
            exerciseCount = 8
        )
        
        val mockMuscleGroupDistribution = listOf(
            MuscleGroupDistribution(
                muscleGroup = com.example.gym_tracker.core.common.enums.MuscleGroup.CHEST,
                exerciseCount = 3,
                totalVolume = 1500.0,
                percentage = 30.0,
                color = "#FF6B6B"
            ),
            MuscleGroupDistribution(
                muscleGroup = com.example.gym_tracker.core.common.enums.MuscleGroup.UPPER_BACK,
                exerciseCount = 2,
                totalVolume = 1200.0,
                percentage = 24.0,
                color = "#4ECDC4"
            )
        )
        
        val mockStarMarkedExercises = listOf(
            Exercise(
                id = "1",
                name = "Bench Press",
                category = com.example.gym_tracker.core.common.enums.ExerciseCategory.CHEST,
                muscleGroups = listOf(com.example.gym_tracker.core.common.enums.MuscleGroup.CHEST),
                equipment = com.example.gym_tracker.core.common.enums.Equipment.BARBELL,
                instructions = emptyList(),
                createdAt = java.time.Instant.now(),
                updatedAt = java.time.Instant.now(),
                isCustom = false,
                isStarMarked = true
            )
        )
        
        val mockPersonalRecords = listOf(
            PersonalRecord(
                exerciseName = "Bench Press",
                weight = 100.0,
                reps = 8,
                achievedDate = LocalDate.of(2024, 1, 15),
                isRecent = true
            ),
            PersonalRecord(
                exerciseName = "Bench Press",
                weight = 95.0,
                reps = 10,
                achievedDate = LocalDate.of(2024, 1, 10),
                isRecent = true
            )
        )
        
        coEvery { analyticsRepository.getPeriodData(any(), any()) } returns mockPeriodData
        coEvery { analyticsRepository.getMuscleGroupDistribution() } returns mockMuscleGroupDistribution
        coEvery { analyticsRepository.getStarMarkedExercises() } returns kotlinx.coroutines.flow.flowOf(mockStarMarkedExercises)
        coEvery { analyticsRepository.getPersonalRecordHistory("1") } returns mockPersonalRecords
        
        // When
        val result = useCase()
        
        // Then
        assertNotNull(result)
        assertTrue(result.beforeAfterComparisons.isNotEmpty())
        assertEquals(2, result.muscleGroupDistribution.size)
        assertTrue(result.personalRecordsTimelines.isNotEmpty())
        
        // Verify muscle group distribution is sorted by percentage
        assertEquals(com.example.gym_tracker.core.common.enums.MuscleGroup.CHEST, result.muscleGroupDistribution[0].muscleGroup)
        assertEquals(com.example.gym_tracker.core.common.enums.MuscleGroup.UPPER_BACK, result.muscleGroupDistribution[1].muscleGroup)
        
        // Verify personal records timeline
        val timeline = result.personalRecordsTimelines[0]
        assertEquals("Bench Press", timeline.exerciseName)
        assertEquals(2, timeline.records.size)
        assertTrue(timeline.totalImprovement > 0)
    }
    
    @Test
    fun `invoke handles empty data gracefully`() = runTest {
        // Given
        coEvery { analyticsRepository.getPeriodData(any(), any()) } returns null
        coEvery { analyticsRepository.getMuscleGroupDistribution() } returns emptyList()
        coEvery { analyticsRepository.getStarMarkedExercises() } returns kotlinx.coroutines.flow.flowOf(emptyList())
        
        // When
        val result = useCase()
        
        // Then
        assertNotNull(result)
        assertTrue(result.beforeAfterComparisons.isEmpty())
        assertTrue(result.muscleGroupDistribution.isEmpty())
        assertTrue(result.personalRecordsTimelines.isEmpty())
    }
    
    @Test
    fun `generateBeforeAfterComparisons calculates improvement percentage correctly`() = runTest {
        // Given
        val beforePeriod = ComparisonPeriod(
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 1, 31),
            totalVolume = 4000.0,
            averageWeight = 80.0,
            workoutCount = 10,
            exerciseCount = 6
        )
        
        val afterPeriod = ComparisonPeriod(
            startDate = LocalDate.of(2024, 2, 1),
            endDate = LocalDate.of(2024, 2, 29),
            totalVolume = 5000.0,
            averageWeight = 100.0,
            workoutCount = 12,
            exerciseCount = 8
        )
        
        coEvery { analyticsRepository.getPeriodData(any(), any()) } returnsMany listOf(afterPeriod, beforePeriod)
        coEvery { analyticsRepository.getMuscleGroupDistribution() } returns emptyList()
        coEvery { analyticsRepository.getStarMarkedExercises() } returns kotlinx.coroutines.flow.flowOf(emptyList())
        
        // When
        val result = useCase()
        
        // Then
        assertTrue(result.beforeAfterComparisons.isNotEmpty())
        val comparison = result.beforeAfterComparisons[0]
        assertEquals(25.0, comparison.improvementPercentage, 0.1) // (5000-4000)/4000 * 100 = 25%
        assertEquals(ComparisonType.MONTHLY, comparison.comparisonType)
    }
    
    @Test
    fun `generatePersonalRecordsTimelines calculates trend direction correctly`() = runTest {
        // Given
        val mockStarMarkedExercises = listOf(
            Exercise(
                id = "1",
                name = "Squat",
                category = com.example.gym_tracker.core.common.enums.ExerciseCategory.LEGS,
                muscleGroups = listOf(com.example.gym_tracker.core.common.enums.MuscleGroup.QUADRICEPS),
                equipment = com.example.gym_tracker.core.common.enums.Equipment.BARBELL,
                instructions = emptyList(),
                createdAt = java.time.Instant.now(),
                updatedAt = java.time.Instant.now(),
                isCustom = false,
                isStarMarked = true
            )
        )
        
        val mockPersonalRecords = listOf(
            PersonalRecord(
                exerciseName = "Squat",
                weight = 80.0,
                reps = 5,
                achievedDate = LocalDate.of(2024, 1, 1),
                isRecent = false
            ),
            PersonalRecord(
                exerciseName = "Squat",
                weight = 90.0,
                reps = 5,
                achievedDate = LocalDate.of(2024, 1, 15),
                isRecent = true
            ),
            PersonalRecord(
                exerciseName = "Squat",
                weight = 100.0,
                reps = 5,
                achievedDate = LocalDate.of(2024, 2, 1),
                isRecent = true
            )
        )
        
        coEvery { analyticsRepository.getPeriodData(any(), any()) } returns null
        coEvery { analyticsRepository.getMuscleGroupDistribution() } returns emptyList()
        coEvery { analyticsRepository.getStarMarkedExercises() } returns kotlinx.coroutines.flow.flowOf(mockStarMarkedExercises)
        coEvery { analyticsRepository.getPersonalRecordHistory("1") } returns mockPersonalRecords
        
        // When
        val result = useCase()
        
        // Then
        assertTrue(result.personalRecordsTimelines.isNotEmpty())
        val timeline = result.personalRecordsTimelines[0]
        assertEquals("Squat", timeline.exerciseName)
        assertEquals(TrendDirection.UP, timeline.trendDirection)
        assertEquals(25.0, timeline.totalImprovement, 0.1) // (100-80)/80 * 100 = 25%
        assertEquals(3, timeline.records.size)
        
        // Verify records are sorted by date
        assertEquals(LocalDate.of(2024, 1, 1), timeline.records[0].date)
        assertEquals(LocalDate.of(2024, 2, 1), timeline.records[2].date)
    }
}