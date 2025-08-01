package com.example.gym_tracker.core.export.model

import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * Complete export data structure containing all user data
 */
@Serializable
data class ExportData(
    val metadata: ExportMetadata,
    val exercises: List<ExportExercise>,
    val workouts: List<ExportWorkout>,
    val templates: List<ExportTemplate>,
    val goals: List<ExportGoal>,
    val personalRecords: List<ExportPersonalRecord>
)

/**
 * Export metadata information
 */
@Serializable
data class ExportMetadata(
    val version: String = "1.0",
    val exportDate: String, // ISO-8601 format
    val appVersion: String,
    val totalWorkouts: Int,
    val totalExercises: Int,
    val dateRange: ExportDateRange?
)

/**
 * Date range for filtered exports
 */
@Serializable
data class ExportDateRange(
    val startDate: String, // ISO-8601 format
    val endDate: String    // ISO-8601 format
)

/**
 * Exercise export model
 */
@Serializable
data class ExportExercise(
    val id: String,
    val name: String,
    val category: String,
    val muscleGroups: List<String>,
    val equipment: String,
    val instructions: List<String>,
    val createdAt: String, // ISO-8601 format
    val updatedAt: String, // ISO-8601 format
    val isCustom: Boolean,
    val isStarMarked: Boolean
)

/**
 * Workout export model
 */
@Serializable
data class ExportWorkout(
    val id: String,
    val name: String,
    val templateId: String?,
    val startTime: String, // ISO-8601 format
    val endTime: String?,  // ISO-8601 format
    val notes: String,
    val rating: Int?,
    val totalVolume: Double,
    val averageRestTimeSeconds: Long,
    val exerciseInstances: List<ExportExerciseInstance>
)

/**
 * Exercise instance export model
 */
@Serializable
data class ExportExerciseInstance(
    val id: String,
    val exerciseId: String,
    val exerciseName: String,
    val orderInWorkout: Int,
    val notes: String,
    val sets: List<ExportExerciseSet>
)

/**
 * Exercise set export model
 */
@Serializable
data class ExportExerciseSet(
    val id: String,
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val restTimeSeconds: Long,
    val rpe: Int?,
    val tempo: String?,
    val isWarmup: Boolean,
    val isFailure: Boolean,
    val notes: String
)

/**
 * Template export model
 */
@Serializable
data class ExportTemplate(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val estimatedDurationMinutes: Int,
    val difficulty: String,
    val createdAt: String, // ISO-8601 format
    val updatedAt: String, // ISO-8601 format
    val isPublic: Boolean,
    val exercises: List<ExportTemplateExercise>
)

/**
 * Template exercise export model
 */
@Serializable
data class ExportTemplateExercise(
    val id: String,
    val exerciseId: String,
    val exerciseName: String,
    val orderInTemplate: Int,
    val targetSets: Int,
    val targetReps: String,
    val targetWeight: Double?,
    val restTimeSeconds: Long,
    val notes: String
)

/**
 * Goal export model
 */
@Serializable
data class ExportGoal(
    val id: String,
    val type: String,
    val title: String,
    val description: String,
    val targetValue: Double,
    val currentValue: Double,
    val unit: String,
    val exerciseId: String?,
    val exerciseName: String?,
    val targetDate: String?, // ISO-8601 format
    val createdAt: String,   // ISO-8601 format
    val completedAt: String?, // ISO-8601 format
    val isCompleted: Boolean
)

/**
 * Personal record export model
 */
@Serializable
data class ExportPersonalRecord(
    val id: String,
    val exerciseId: String,
    val exerciseName: String,
    val weight: Double,
    val reps: Int,
    val oneRepMax: Double,
    val achievedAt: String, // ISO-8601 format
    val workoutId: String,
    val workoutName: String
)