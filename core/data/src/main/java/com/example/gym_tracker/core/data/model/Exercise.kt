package com.example.gym_tracker.core.data.model

import java.time.Instant

/**
 * Domain model for Exercise
 */
data class Exercise(
    val id: String,
    val name: String,
    val category: ExerciseCategory,
    val muscleGroups: List<MuscleGroup>,
    val equipment: Equipment,
    val instructions: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isCustom: Boolean = false,
    val isStarMarked: Boolean = false
)

enum class ExerciseCategory {
    CHEST, BACK, SHOULDERS, ARMS, LEGS, CORE, CARDIO, FULL_BODY
}

enum class MuscleGroup {
    CHEST, UPPER_BACK, LOWER_BACK, FRONT_DELTS, SIDE_DELTS, REAR_DELTS,
    BICEPS, TRICEPS, FOREARMS, QUADRICEPS, HAMSTRINGS, GLUTES, CALVES,
    ABS, OBLIQUES, LOWER_BACK_MUSCLES
}

enum class Equipment {
    BARBELL, DUMBBELL, CABLE, MACHINE, BODYWEIGHT, RESISTANCE_BAND, KETTLEBELL, OTHER
}