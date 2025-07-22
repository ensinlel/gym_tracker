package com.example.gym_tracker.core.data.model

import com.example.gym_tracker.core.common.enums.Equipment
import com.example.gym_tracker.core.common.enums.ExerciseCategory
import com.example.gym_tracker.core.common.enums.MuscleGroup
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