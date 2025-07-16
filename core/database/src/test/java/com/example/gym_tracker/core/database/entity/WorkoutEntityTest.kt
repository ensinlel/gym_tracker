package com.example.gym_tracker.core.database.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Duration
import java.time.Instant

class WorkoutEntityTest {

    @Test
    fun workoutEntity_createsWithCorrectDefaults() {
        // Given
        val startTime = Instant.now()
        val workout = WorkoutEntity(
            name = "Push Day",
            templateId = null,
            startTime = startTime,
            endTime = null
        )

        // Then
        assertThat(workout.name).isEqualTo("Push Day")
        assertThat(workout.templateId).isNull()
        assertThat(workout.startTime).isEqualTo(startTime)
        assertThat(workout.endTime).isNull()
        assertThat(workout.notes).isEmpty()
        assertThat(workout.rating).isNull()
        assertThat(workout.totalVolume).isEqualTo(0.0)
        assertThat(workout.averageRestTime).isEqualTo(Duration.ZERO)
        assertThat(workout.id).isNotEmpty()
    }

    @Test
    fun workoutEntity_withAllFields() {
        // Given
        val startTime = Instant.now()
        val endTime = startTime.plusSeconds(3600) // 1 hour later
        val restTime = Duration.ofMinutes(2)
        
        val workout = WorkoutEntity(
            name = "Full Body Workout",
            templateId = "template-123",
            startTime = startTime,
            endTime = endTime,
            notes = "Great workout today!",
            rating = 5,
            totalVolume = 1500.0,
            averageRestTime = restTime
        )

        // Then
        assertThat(workout.name).isEqualTo("Full Body Workout")
        assertThat(workout.templateId).isEqualTo("template-123")
        assertThat(workout.startTime).isEqualTo(startTime)
        assertThat(workout.endTime).isEqualTo(endTime)
        assertThat(workout.notes).isEqualTo("Great workout today!")
        assertThat(workout.rating).isEqualTo(5)
        assertThat(workout.totalVolume).isEqualTo(1500.0)
        assertThat(workout.averageRestTime).isEqualTo(restTime)
    }

    @Test
    fun workoutEntity_ratingValidation() {
        // Given
        val workout = WorkoutEntity(
            name = "Test Workout",
            templateId = null,
            startTime = Instant.now(),
            endTime = null,
            rating = 3
        )

        // Then
        assertThat(workout.rating).isEqualTo(3)
        assertThat(workout.rating).isAtLeast(1)
        assertThat(workout.rating).isAtMost(5)
    }
}