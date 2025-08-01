package com.example.gym_tracker.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

/**
 * Room entity for fitness goals
 */
@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val type: GoalType,
    val targetValue: Double,
    val currentValue: Double = 0.0,
    val unit: String, // kg, reps, workouts, etc.
    val targetDate: LocalDate?,
    val isCompleted: Boolean = false,
    val completedAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isActive: Boolean = true,
    val linkedExerciseId: String? = null // For PR goals linked to specific exercises
)

/**
 * Types of fitness goals
 */
enum class GoalType {
    WEIGHT_LOSS,
    WEIGHT_GAIN,
    STRENGTH, // e.g., bench press 100kg
    CONSISTENCY, // e.g., workout 3 times per week
    VOLUME, // e.g., total volume per week
    PERSONAL_RECORD, // beat current PR
    HABIT // e.g., workout for 30 days straight
}