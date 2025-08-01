package com.example.gym_tracker.core.data.mapper

import com.example.gym_tracker.core.data.model.Goal
import com.example.gym_tracker.core.data.model.GoalType as DomainGoalType
import com.example.gym_tracker.core.database.entity.GoalEntity
import com.example.gym_tracker.core.database.entity.GoalType as EntityGoalType

/**
 * Mapper functions for Goal entities and domain models
 */

fun GoalEntity.toDomainModel(): Goal {
    return Goal(
        id = id,
        title = title,
        description = description,
        type = type.toDomainModel(),
        targetValue = targetValue,
        currentValue = currentValue,
        unit = unit,
        targetDate = targetDate,
        isCompleted = isCompleted,
        completedAt = completedAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive,
        linkedExerciseId = linkedExerciseId
    )
}

fun Goal.toEntity(): GoalEntity {
    return GoalEntity(
        id = id,
        title = title,
        description = description,
        type = type.toEntity(),
        targetValue = targetValue,
        currentValue = currentValue,
        unit = unit,
        targetDate = targetDate,
        isCompleted = isCompleted,
        completedAt = completedAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isActive = isActive,
        linkedExerciseId = linkedExerciseId
    )
}

fun EntityGoalType.toDomainModel(): DomainGoalType {
    return when (this) {
        EntityGoalType.WEIGHT_LOSS -> DomainGoalType.WEIGHT_LOSS
        EntityGoalType.WEIGHT_GAIN -> DomainGoalType.WEIGHT_GAIN
        EntityGoalType.STRENGTH -> DomainGoalType.STRENGTH
        EntityGoalType.CONSISTENCY -> DomainGoalType.CONSISTENCY
        EntityGoalType.VOLUME -> DomainGoalType.VOLUME
        EntityGoalType.PERSONAL_RECORD -> DomainGoalType.PERSONAL_RECORD
        EntityGoalType.HABIT -> DomainGoalType.HABIT
    }
}

fun DomainGoalType.toEntity(): EntityGoalType {
    return when (this) {
        DomainGoalType.WEIGHT_LOSS -> EntityGoalType.WEIGHT_LOSS
        DomainGoalType.WEIGHT_GAIN -> EntityGoalType.WEIGHT_GAIN
        DomainGoalType.STRENGTH -> EntityGoalType.STRENGTH
        DomainGoalType.CONSISTENCY -> EntityGoalType.CONSISTENCY
        DomainGoalType.VOLUME -> EntityGoalType.VOLUME
        DomainGoalType.PERSONAL_RECORD -> EntityGoalType.PERSONAL_RECORD
        DomainGoalType.HABIT -> EntityGoalType.HABIT
    }
}