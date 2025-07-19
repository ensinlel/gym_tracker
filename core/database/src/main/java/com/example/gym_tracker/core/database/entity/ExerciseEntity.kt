package com.example.gym_tracker.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
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