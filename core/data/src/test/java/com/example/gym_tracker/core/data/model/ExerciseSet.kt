package com.example.gym_tracker.core.data.model

/**
 * Data class representing an exercise set in the test environment
 */
data class ExerciseSet(
    val id: String,
    val exerciseInstanceId: String,
    val weight: Double,
    val reps: Int,
    val isWarmup: Boolean = false
)