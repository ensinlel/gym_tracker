package com.example.gym_tracker.core.data.model

import com.example.gym_tracker.core.common.enums.Equipment
import com.example.gym_tracker.core.common.enums.MuscleGroup
import java.time.Duration
import java.time.Instant

/**
 * Domain model for Workout Template
 * Represents a reusable workout structure that can be used to create workouts
 */
data class WorkoutTemplate(
    val id: String,
    val name: String,
    val description: String = "",
    val category: WorkoutCategory = WorkoutCategory.GENERAL,
    val difficulty: DifficultyLevel = DifficultyLevel.INTERMEDIATE,
    val estimatedDuration: Duration = Duration.ZERO,
    val targetMuscleGroups: List<MuscleGroup> = emptyList(),
    val requiredEquipment: List<Equipment> = emptyList(),
    val isPublic: Boolean = false,
    val createdBy: String? = null, // User ID who created the template
    val createdAt: Instant,
    val updatedAt: Instant,
    val usageCount: Int = 0, // How many times this template has been used
    val rating: Double = 0.0, // Average rating from users
    val tags: List<String> = emptyList()
)

/**
 * Template exercise - defines an exercise within a template
 */
data class TemplateExercise(
    val id: String,
    val templateId: String,
    val exerciseId: String,
    val orderInTemplate: Int,
    val targetSets: Int = 3,
    val targetReps: IntRange? = null, // e.g., 8..12 reps
    val targetWeight: Double? = null,
    val restTime: Duration = Duration.ofMinutes(2),
    val notes: String = "",
    val isSuperset: Boolean = false,
    val supersetGroup: Int? = null // Group ID for superset exercises
)

/**
 * Complete template with exercises
 */
data class WorkoutTemplateWithExercises(
    val template: WorkoutTemplate,
    val exercises: List<TemplateExerciseWithDetails>
)

/**
 * Template exercise with complete exercise details
 */
data class TemplateExerciseWithDetails(
    val templateExercise: TemplateExercise,
    val exercise: Exercise
)

/**
 * Workout routine - represents a scheduled workout plan
 */
data class WorkoutRoutine(
    val id: String,
    val name: String,
    val description: String = "",
    val isActive: Boolean = true,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * Routine schedule - defines when workouts should occur
 */
data class RoutineSchedule(
    val id: String,
    val routineId: String,
    val templateId: String,
    val dayOfWeek: Int, // 1=Monday, 7=Sunday
    val timeOfDay: String? = null, // HH:mm format
    val isActive: Boolean = true,
    val notes: String = ""
)

/**
 * Complete routine with schedules and templates
 */
data class WorkoutRoutineWithDetails(
    val routine: WorkoutRoutine,
    val schedules: List<RoutineScheduleWithTemplate>
)

/**
 * Routine schedule with template details
 */
data class RoutineScheduleWithTemplate(
    val schedule: RoutineSchedule,
    val template: WorkoutTemplate
)

/**
 * Workout categories for organizing templates
 */
enum class WorkoutCategory(val displayName: String) {
    STRENGTH("Strength Training"),
    CARDIO("Cardio"),
    HIIT("HIIT"),
    YOGA("Yoga"),
    PILATES("Pilates"),
    CROSSFIT("CrossFit"),
    BODYWEIGHT("Bodyweight"),
    POWERLIFTING("Powerlifting"),
    BODYBUILDING("Bodybuilding"),
    FUNCTIONAL("Functional Training"),
    REHABILITATION("Rehabilitation"),
    GENERAL("General")
}

/**
 * Difficulty levels for templates
 */
enum class DifficultyLevel(val displayName: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced"),
    EXPERT("Expert")
}

/**
 * Template sharing status
 */
enum class SharingStatus {
    PRIVATE,
    SHARED_WITH_FRIENDS,
    PUBLIC
}

/**
 * Template import/export data
 */
data class TemplateExportData(
    val template: WorkoutTemplate,
    val exercises: List<TemplateExerciseExportData>,
    val exportedAt: Instant,
    val exportedBy: String,
    val version: String = "1.0"
)

/**
 * Exercise data for template export
 */
data class TemplateExerciseExportData(
    val exerciseName: String,
    val exerciseCategory: String,
    val muscleGroups: List<String>,
    val equipment: String,
    val orderInTemplate: Int,
    val targetSets: Int,
    val targetReps: IntRange?,
    val targetWeight: Double?,
    val restTime: Duration,
    val notes: String,
    val isSuperset: Boolean,
    val supersetGroup: Int?
)