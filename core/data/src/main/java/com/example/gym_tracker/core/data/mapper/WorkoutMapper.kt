package com.example.gym_tracker.core.data.mapper

import com.example.gym_tracker.core.data.model.ExerciseInstance
import com.example.gym_tracker.core.data.model.ExerciseInstanceWithDetails
import com.example.gym_tracker.core.data.model.Workout
import com.example.gym_tracker.core.data.model.WorkoutWithDetails
import com.example.gym_tracker.core.database.entity.ExerciseInstanceEntity
import com.example.gym_tracker.core.database.entity.ExerciseInstanceWithDetails as DbExerciseInstanceWithDetails
import com.example.gym_tracker.core.database.entity.WorkoutEntity
import com.example.gym_tracker.core.database.entity.WorkoutWithDetails as DbWorkoutWithDetails

/**
 * Convert database WorkoutEntity to domain Workout model
 */
fun WorkoutEntity.toDomainModel(): Workout {
    return Workout(
        id = id,
        name = name,
        templateId = templateId,
        startTime = startTime,
        endTime = endTime,
        notes = notes,
        rating = rating,
        totalVolume = totalVolume,
        averageRestTime = averageRestTime
    )
}

/**
 * Convert domain Workout model to database WorkoutEntity
 */
fun Workout.toEntity(): WorkoutEntity {
    return WorkoutEntity(
        id = id,
        name = name,
        templateId = templateId,
        startTime = startTime,
        endTime = endTime,
        notes = notes,
        rating = rating,
        totalVolume = totalVolume,
        averageRestTime = averageRestTime
    )
}

/**
 * Convert database WorkoutWithDetails to domain WorkoutWithDetails
 */
fun DbWorkoutWithDetails.toDomainModel(): WorkoutWithDetails {
    return WorkoutWithDetails(
        workout = workout.toDomainModel(),
        exerciseInstances = exerciseInstances.map { it.toDomainModel() }
    )
}

/**
 * Convert database ExerciseInstanceEntity to domain ExerciseInstance
 */
fun ExerciseInstanceEntity.toDomainModel(): ExerciseInstance {
    return ExerciseInstance(
        id = id,
        workoutId = workoutId,
        exerciseId = exerciseId,
        orderInWorkout = orderInWorkout,
        notes = notes
    )
}

/**
 * Convert domain ExerciseInstance to database ExerciseInstanceEntity
 */
fun ExerciseInstance.toEntity(): ExerciseInstanceEntity {
    return ExerciseInstanceEntity(
        id = id,
        workoutId = workoutId,
        exerciseId = exerciseId,
        orderInWorkout = orderInWorkout,
        notes = notes
    )
}

/**
 * Convert database ExerciseInstanceWithDetails to domain ExerciseInstanceWithDetails
 */
fun DbExerciseInstanceWithDetails.toDomainModel(): ExerciseInstanceWithDetails {
    return ExerciseInstanceWithDetails(
        exerciseInstance = exerciseInstance.toDomainModel(),
        exercise = exercise.toDomainModel(),
        sets = sets.map { it.toDomainModel() }
    )
}