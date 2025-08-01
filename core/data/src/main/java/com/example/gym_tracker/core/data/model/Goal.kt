package com.example.gym_tracker.core.data.model

import java.time.Instant
import java.time.LocalDate

/**
 * Domain model for fitness goals
 */
data class Goal(
    val id: String,
    val title: String,
    val description: String,
    val type: GoalType,
    val targetValue: Double,
    val currentValue: Double,
    val unit: String,
    val targetDate: LocalDate?,
    val isCompleted: Boolean,
    val completedAt: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isActive: Boolean,
    val linkedExerciseId: String? = null,
    val progressPercentage: Double = if (targetValue > 0) (currentValue / targetValue * 100).coerceAtMost(100.0) else 0.0
)

/**
 * Types of fitness goals
 */
enum class GoalType {
    WEIGHT_LOSS,
    WEIGHT_GAIN,
    STRENGTH,
    CONSISTENCY,
    VOLUME,
    PERSONAL_RECORD,
    HABIT;
    
    fun getDisplayName(): String = when (this) {
        WEIGHT_LOSS -> "Weight Loss"
        WEIGHT_GAIN -> "Weight Gain"
        STRENGTH -> "Strength Goal"
        CONSISTENCY -> "Consistency Goal"
        VOLUME -> "Volume Goal"
        PERSONAL_RECORD -> "Personal Record"
        HABIT -> "Habit Goal"
    }
    
    fun getDefaultUnit(): String = when (this) {
        WEIGHT_LOSS, WEIGHT_GAIN -> "kg"
        STRENGTH -> "kg"
        CONSISTENCY -> "workouts"
        VOLUME -> "kg"
        PERSONAL_RECORD -> "kg"
        HABIT -> "days"
    }
}