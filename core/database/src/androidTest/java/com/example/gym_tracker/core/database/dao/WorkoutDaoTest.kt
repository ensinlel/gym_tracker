package com.example.gym_tracker.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gym_tracker.core.database.GymTrackerDatabase
import com.example.gym_tracker.core.database.entity.WorkoutEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@RunWith(AndroidJUnit4::class)
class WorkoutDaoTest {

    private lateinit var database: GymTrackerDatabase
    private lateinit var workoutDao: WorkoutDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GymTrackerDatabase::class.java
        ).allowMainThreadQueries().build()
        
        workoutDao = database.workoutDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertWorkout_retrievesCorrectly() = runTest {
        // Given
        val workout = createTestWorkout(name = "Push Day")

        // When
        workoutDao.insertWorkout(workout)
        val result = workoutDao.getWorkoutById(workout.id)

        // Then
        assertThat(result).isEqualTo(workout)
    }

    @Test
    fun getAllWorkouts_returnsWorkoutsOrderedByStartTimeDesc() = runTest {
        // Given
        val now = Instant.now()
        val workouts = listOf(
            createTestWorkout(name = "Workout 1", startTime = now.minus(2, ChronoUnit.DAYS)),
            createTestWorkout(name = "Workout 2", startTime = now.minus(1, ChronoUnit.DAYS)),
            createTestWorkout(name = "Workout 3", startTime = now)
        )

        // When
        workouts.forEach { workoutDao.insertWorkout(it) }
        val result = workoutDao.getAllWorkouts().first()

        // Then
        assertThat(result).hasSize(3)
        assertThat(result.map { it.name }).containsExactly("Workout 3", "Workout 2", "Workout 1").inOrder()
    }

    @Test
    fun getWorkoutsByTemplate_filtersCorrectly() = runTest {
        // Given
        val templateId = "template-123"
        val workoutWithTemplate = createTestWorkout(name = "Template Workout", templateId = templateId)
        val workoutWithoutTemplate = createTestWorkout(name = "Custom Workout", templateId = null)
        
        workoutDao.insertWorkout(workoutWithTemplate)
        workoutDao.insertWorkout(workoutWithoutTemplate)

        // When
        val result = workoutDao.getWorkoutsByTemplate(templateId).first()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result.first().name).isEqualTo("Template Workout")
    }

    @Test
    fun updateWorkout_updatesCorrectly() = runTest {
        // Given
        val workout = createTestWorkout(name = "Original Name")
        workoutDao.insertWorkout(workout)

        // When
        val updatedWorkout = workout.copy(name = "Updated Name", rating = 5)
        workoutDao.updateWorkout(updatedWorkout)
        val result = workoutDao.getWorkoutById(workout.id)

        // Then
        assertThat(result?.name).isEqualTo("Updated Name")
        assertThat(result?.rating).isEqualTo(5)
    }

    @Test
    fun deleteWorkout_removesFromDatabase() = runTest {
        // Given
        val workout = createTestWorkout(name = "To Delete")
        workoutDao.insertWorkout(workout)

        // When
        workoutDao.deleteWorkout(workout)
        val result = workoutDao.getWorkoutById(workout.id)

        // Then
        assertThat(result).isNull()
    }

    private fun createTestWorkout(
        id: String = "test-workout-${System.currentTimeMillis()}",
        name: String = "Test Workout",
        templateId: String? = null,
        startTime: Instant = Instant.now(),
        endTime: Instant? = null,
        notes: String = "",
        rating: Int? = null,
        totalVolume: Double = 0.0,
        averageRestTime: Duration = Duration.ZERO
    ) = WorkoutEntity(
        id = id,
        name = name,
        templateId = templateId,
        startTime = startTime,
        endTime = endTime,
        notes = notes,
        rating = rating,
        totalVolume = totalVolume,
        averageRestTime = averageRestTime
    )
}