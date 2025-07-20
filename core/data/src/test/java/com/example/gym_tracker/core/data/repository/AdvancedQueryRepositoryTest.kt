package com.example.gym_tracker.core.data.repository

import com.example.gym_tracker.core.data.repository.impl.AdvancedQueryRepositoryImpl
import com.example.gym_tracker.core.database.dao.*
import com.example.gym_tracker.core.database.entity.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Unit tests for AdvancedQueryRepository - Task 3.2
 * Tests complex analytics queries, full-text search, and pagination
 */
class AdvancedQueryRepositoryTest {
    
    private lateinit var workoutDao: WorkoutDao
    private lateinit var exerciseDao: ExerciseDao
    private lateinit var exerciseSetDao: ExerciseSetDao
    private lateinit var exerciseInstanceDao: ExerciseInstanceDao
    private lateinit var repository: AdvancedQueryRepository
    
    @Before
    fun setup() {
        workoutDao = mockk()
        exerciseDao = mockk()
        exerciseSetDao = mockk()
        exerciseInstanceDao = mockk()
        
        repository = AdvancedQueryRepositoryImpl(
            workoutDao = workoutDao,
            exerciseDao = exerciseDao,
            exerciseSetDao = exerciseSetDao,
            exerciseInstanceDao = exerciseInstanceDao
        )
    }
    
    @Test
    fun `getVolumeProgressionData returns volume progression points`() = runTest {
        // Given
        val startTime = 1000L
        val endTime = 2000L
        val expectedData = listOf(
            VolumeProgressionPoint(
                workout_date = "2024-01-01",
                daily_volume = 1000.0,
                workout_count = 1,
                avg_workout_volume = 1000.0
            )
        )
        every { workoutDao.getVolumeProgressionData(startTime, endTime) } returns flowOf(expectedData)
        
        // When
        val result = repository.getVolumeProgressionData(startTime, endTime).first()
        
        // Then
        assertEquals(expectedData, result)
        assertEquals(1, result.size)
        assertEquals("2024-01-01", result[0].workout_date)
        assertEquals(1000.0, result[0].daily_volume)
    }
    
    @Test
    fun `getStrengthProgressionForExercise returns strength progression points`() = runTest {
        // Given
        val exerciseId = "exercise1"
        val startTime = 1000L
        val endTime = 2000L
        val expectedData = listOf(
            StrengthProgressionPoint(
                workout_date = "2024-01-01",
                max_weight = 100.0,
                estimated_1rm = 110.0,
                avg_weight = 95.0,
                total_volume = 2000.0
            )
        )
        every { workoutDao.getStrengthProgressionForExercise(exerciseId, startTime, endTime) } returns flowOf(expectedData)
        
        // When
        val result = repository.getStrengthProgressionForExercise(exerciseId, startTime, endTime).first()
        
        // Then
        assertEquals(expectedData, result)
        assertEquals(1, result.size)
        assertEquals(100.0, result[0].max_weight)
        assertEquals(110.0, result[0].estimated_1rm)
    }
    
    @Test
    fun `fullTextSearchExercises returns matching exercises`() = runTest {
        // Given
        val searchQuery = "bench press"
        val expectedExercises = listOf(
            ExerciseEntity(
                id = "1",
                name = "Bench Press",
                category = ExerciseCategory.STRENGTH,
                muscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                equipment = "Barbell",
                instructions = "Lie on bench and press weight up",
                isCustom = false,
                isStarMarked = false
            )
        )
        every { exerciseDao.fullTextSearchExercises(searchQuery) } returns flowOf(expectedExercises)
        
        // When
        val result = repository.fullTextSearchExercises(searchQuery).first()
        
        // Then
        assertEquals(expectedExercises, result)
        assertEquals(1, result.size)
        assertEquals("Bench Press", result[0].name)
    }
    
    @Test
    fun `searchExercisesPaginated returns paginated results`() = runTest {
        // Given
        val searchQuery = "squat"
        val limit = 10
        val offset = 0
        val expectedExercises = listOf(
            ExerciseEntity(
                id = "2",
                name = "Back Squat",
                category = ExerciseCategory.STRENGTH,
                muscleGroups = listOf(MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES),
                equipment = "Barbell",
                instructions = "Squat down and up",
                isCustom = false,
                isStarMarked = true
            )
        )
        coEvery { exerciseDao.searchExercisesPaginated(searchQuery, limit, offset) } returns expectedExercises
        
        // When
        val result = repository.searchExercisesPaginated(searchQuery, limit, offset)
        
        // Then
        assertEquals(expectedExercises, result)
        assertEquals(1, result.size)
        assertEquals("Back Squat", result[0].name)
    }
    
    @Test
    fun `getWorkoutsPaginated returns filtered workouts`() = runTest {
        // Given
        val startTime = 1000L
        val endTime = 2000L
        val minRating = 4
        val hasNotes = 1
        val limit = 5
        val offset = 0
        val expectedWorkouts = listOf(
            WorkoutEntity(
                id = "workout1",
                name = "Push Day",
                templateId = null,
                startTime = java.time.Instant.ofEpochMilli(1500L),
                endTime = java.time.Instant.ofEpochMilli(1600L),
                notes = "Great workout",
                rating = 5,
                totalVolume = 2500.0
            )
        )
        coEvery { workoutDao.getWorkoutsPaginated(startTime, endTime, minRating, hasNotes, limit, offset) } returns expectedWorkouts
        
        // When
        val result = repository.getWorkoutsPaginated(startTime, endTime, minRating, hasNotes, limit, offset)
        
        // Then
        assertEquals(expectedWorkouts, result)
        assertEquals(1, result.size)
        assertEquals("Push Day", result[0].name)
        assertEquals(5, result[0].rating)
    }
    
    @Test
    fun `getPersonalRecordsProgression returns PR data`() = runTest {
        // Given
        val startTime = 1000L
        val expectedData = listOf(
            PersonalRecordData(
                exercise_name = "Bench Press",
                exerciseId = "exercise1",
                weight = 225.0,
                reps = 5,
                estimated_1rm = 253.0,
                achieved_date = 1500L,
                pr_rank = 1
            )
        )
        every { exerciseSetDao.getPersonalRecordsProgression(startTime) } returns flowOf(expectedData)
        
        // When
        val result = repository.getPersonalRecordsProgression(startTime).first()
        
        // Then
        assertEquals(expectedData, result)
        assertEquals(1, result.size)
        assertEquals("Bench Press", result[0].exercise_name)
        assertEquals(225.0, result[0].weight)
        assertEquals(1, result[0].pr_rank)
    }
    
    @Test
    fun `getTrainingIntensityAnalysis returns intensity data`() = runTest {
        // Given
        val startTime = 1000L
        val expectedData = listOf(
            IntensityAnalysisData(
                rpe = 8,
                set_count = 15,
                avg_weight = 185.0,
                avg_reps = 8.5,
                total_volume = 12500.0,
                month_year = "2024-01"
            )
        )
        every { exerciseSetDao.getTrainingIntensityAnalysis(startTime) } returns flowOf(expectedData)
        
        // When
        val result = repository.getTrainingIntensityAnalysis(startTime).first()
        
        // Then
        assertEquals(expectedData, result)
        assertEquals(1, result.size)
        assertEquals(8, result[0].rpe)
        assertEquals(15, result[0].set_count)
    }
    
    @Test
    fun `getExerciseRecommendations returns recommended exercises`() = runTest {
        // Given
        val recentWorkoutThreshold = 1000L
        val targetMuscleGroup = "CHEST"
        val limit = 5
        val expectedExercises = listOf(
            ExerciseEntity(
                id = "3",
                name = "Incline Dumbbell Press",
                category = ExerciseCategory.STRENGTH,
                muscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS),
                equipment = "Dumbbells",
                instructions = "Press dumbbells on incline",
                isCustom = false,
                isStarMarked = false
            )
        )
        every { exerciseDao.getExerciseRecommendations(recentWorkoutThreshold, targetMuscleGroup, limit) } returns flowOf(expectedExercises)
        
        // When
        val result = repository.getExerciseRecommendations(recentWorkoutThreshold, targetMuscleGroup, limit).first()
        
        // Then
        assertEquals(expectedExercises, result)
        assertEquals(1, result.size)
        assertEquals("Incline Dumbbell Press", result[0].name)
    }
}