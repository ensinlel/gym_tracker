package com.example.gym_tracker.core.data.repository

import com.example.gym_tracker.core.data.model.Workout
import com.example.gym_tracker.core.data.repository.impl.WorkoutRepositoryImpl
import com.example.gym_tracker.core.database.dao.WorkoutDao
import com.example.gym_tracker.core.database.entity.WorkoutEntity
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Duration
import java.time.Instant

class WorkoutRepositoryImplTest {

    private lateinit var workoutDao: WorkoutDao
    private lateinit var repository: WorkoutRepositoryImpl

    @Before
    fun setup() {
        workoutDao = mockk()
        repository = WorkoutRepositoryImpl(workoutDao)
    }

    @Test
    fun `getAllWorkouts returns mapped domain models`() = runTest {
        // Given
        val dbWorkouts = listOf(
            createDbWorkout(name = "Push Day"),
            createDbWorkout(name = "Pull Day")
        )
        every { workoutDao.getAllWorkouts() } returns flowOf(dbWorkouts)

        // When
        val result = repository.getAllWorkouts().first()

        // Then
        assertThat(result).hasSize(2)
        assertThat(result.map { it.name }).containsExactly("Push Day", "Pull Day")
    }

    @Test
    fun `getWorkoutById returns mapped domain model`() = runTest {
        // Given
        val dbWorkout = createDbWorkout(name = "Push Day")
        coEvery { workoutDao.getWorkoutById("test-id") } returns dbWorkout

        // When
        val result = repository.getWorkoutById("test-id")

        // Then
        assertThat(result).isNotNull()
        assertThat(result?.name).isEqualTo("Push Day")
        assertThat(result?.totalVolume).isEqualTo(1000.0)
    }

    @Test
    fun `getWorkoutById returns null when not found`() = runTest {
        // Given
        coEvery { workoutDao.getWorkoutById("non-existent") } returns null

        // When
        val result = repository.getWorkoutById("non-existent")

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `insertWorkout calls dao with mapped entity`() = runTest {
        // Given
        val workout = createDomainWorkout(name = "New Workout")
        coEvery { workoutDao.insertWorkout(any()) } returns Unit

        // When
        repository.insertWorkout(workout)

        // Then
        coVerify { workoutDao.insertWorkout(any()) }
    }

    @Test
    fun `getWorkoutsByTemplate returns filtered results`() = runTest {
        // Given
        val templateId = "template-123"
        val dbWorkouts = listOf(createDbWorkout(templateId = templateId))
        every { workoutDao.getWorkoutsByTemplate(templateId) } returns flowOf(dbWorkouts)

        // When
        val result = repository.getWorkoutsByTemplate(templateId).first()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result.first().templateId).isEqualTo(templateId)
    }

    @Test
    fun `getWorkoutCount returns correct count`() = runTest {
        // Given
        every { workoutDao.getWorkoutCount() } returns flowOf(5)

        // When
        val result = repository.getWorkoutCount().first()

        // Then
        assertThat(result).isEqualTo(5)
    }

    private fun createDbWorkout(
        id: String = "test-id",
        name: String = "Test Workout",
        templateId: String? = null,
        totalVolume: Double = 1000.0
    ) = WorkoutEntity(
        id = id,
        name = name,
        templateId = templateId,
        startTime = Instant.now(),
        endTime = null,
        notes = "",
        rating = null,
        totalVolume = totalVolume,
        averageRestTime = Duration.ZERO
    )

    private fun createDomainWorkout(
        id: String = "test-id",
        name: String = "Test Workout",
        templateId: String? = null,
        totalVolume: Double = 1000.0
    ) = Workout(
        id = id,
        name = name,
        templateId = templateId,
        startTime = Instant.now(),
        endTime = null,
        notes = "",
        rating = null,
        totalVolume = totalVolume,
        averageRestTime = Duration.ZERO
    )
}