package com.example.gym_tracker.core.data.model

import java.time.Duration

/**
 * Domain model for Exercise Set
 */
data class ExerciseSet(
    val id: String,
    val exerciseInstanceId: String,
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val restTime: Duration = Duration.ZERO,
    val rpe: Int? = null, // Rate of Perceived Exertion (1-10)
    val tempo: String? = null, // e.g., "3-1-2-1"
    val isWarmup: Boolean = false,
    val isFailure: Boolean = false,
    val notes: String = ""
)