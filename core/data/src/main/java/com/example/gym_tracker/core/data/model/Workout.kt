package com.example.gym_tracker.core.data.model

import java.time.Duration
import java.time.Instant

/**
 * Domain model for Workout
 */
data class Workout(
    val id: String,
    val name: String,
    val templateId: String?,
    val startTime: Instant,
    val endTime: Instant?,
    val notes: String = "",
    val rating: Int? = null, // 1-5 stars
    val totalVolume: Double = 0.0,
    val averageRestTime: Duration = Duration.ZERO
)

/**
 * Workout with exercise instances (basic info)
 */
data class WorkoutWithDetails(
    val workout: Workout,
    val exerciseInstances: List<ExerciseInstance>
)

/**
 * Workout with complete exercise details including sets
 */
data class WorkoutWithCompleteDetails(
    val workout: Workout,
    val exerciseInstances: List<ExerciseInstanceWithDetails>
)

/**
 * Exercise instance within a workout
 */
data class ExerciseInstance(
    val id: String,
    val workoutId: String,
    val exerciseId: String,
    val orderInWorkout: Int,
    val notes: String = ""
)

/**
 * Exercise instance with complete details
 */
data class ExerciseInstanceWithDetails(
    val exerciseInstance: ExerciseInstance,
    val exercise: Exercise,
    val sets: List<ExerciseSet>
)