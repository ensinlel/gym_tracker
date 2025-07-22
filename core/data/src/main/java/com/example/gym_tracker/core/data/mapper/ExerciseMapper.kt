package com.example.gym_tracker.core.data.mapper

import com.example.gym_tracker.core.data.model.Exercise
import com.example.gym_tracker.core.database.entity.ExerciseEntity

/**
 * Convert ExerciseEntity to Exercise domain model
 */
fun ExerciseEntity.toDomainModel(): Exercise {
    return Exercise(
        id = id,
        name = name,
        category = category,
        muscleGroups = muscleGroups,
        equipment = equipment,
        instructions = instructions,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isCustom = isCustom,
        isStarMarked = isStarMarked
    )
}

/**
 * Convert Exercise domain model to ExerciseEntity
 */
fun Exercise.toEntity(): ExerciseEntity {
    return ExerciseEntity(
        id = id,
        name = name,
        category = category,
        muscleGroups = muscleGroups,
        equipment = equipment,
        instructions = instructions,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isCustom = isCustom,
        isStarMarked = isStarMarked
    )
}