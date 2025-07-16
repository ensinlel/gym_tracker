package com.example.gym_tracker.core.data.mapper

import com.example.gym_tracker.core.data.model.ExerciseSet
import com.example.gym_tracker.core.database.entity.ExerciseSetEntity

/**
 * Convert database ExerciseSetEntity to domain ExerciseSet model
 */
fun ExerciseSetEntity.toDomainModel(): ExerciseSet {
    return ExerciseSet(
        id = id,
        exerciseInstanceId = exerciseInstanceId,
        setNumber = setNumber,
        weight = weight,
        reps = reps,
        restTime = restTime,
        rpe = rpe,
        tempo = tempo,
        isWarmup = isWarmup,
        isFailure = isFailure,
        notes = notes
    )
}

/**
 * Convert domain ExerciseSet model to database ExerciseSetEntity
 */
fun ExerciseSet.toEntity(): ExerciseSetEntity {
    return ExerciseSetEntity(
        id = id,
        exerciseInstanceId = exerciseInstanceId,
        setNumber = setNumber,
        weight = weight,
        reps = reps,
        restTime = restTime,
        rpe = rpe,
        tempo = tempo,
        isWarmup = isWarmup,
        isFailure = isFailure,
        notes = notes
    )
}