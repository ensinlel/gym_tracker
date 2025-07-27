package com.example.gym_tracker.core.data.mapper

import com.example.gym_tracker.core.data.model.*
import com.example.gym_tracker.core.database.entity.*
import java.time.Duration

/**
 * Convert database WorkoutTemplateEntity to domain WorkoutTemplate model
 */
fun WorkoutTemplateEntity.toDomainModel(): WorkoutTemplate {
    return WorkoutTemplate(
        id = id,
        name = name,
        description = description,
        category = WorkoutCategory.valueOf(category),
        difficulty = DifficultyLevel.valueOf(difficulty),
        estimatedDuration = Duration.ofMillis(estimatedDuration),
        targetMuscleGroups = targetMuscleGroups,
        requiredEquipment = requiredEquipment,
        isPublic = isPublic,
        createdBy = createdBy,
        createdAt = createdAt,
        updatedAt = updatedAt,
        usageCount = usageCount,
        rating = rating,
        tags = tags
    )
}

/**
 * Convert domain WorkoutTemplate model to database WorkoutTemplateEntity
 */
fun WorkoutTemplate.toEntity(): WorkoutTemplateEntity {
    return WorkoutTemplateEntity(
        id = id,
        name = name,
        description = description,
        category = category.name,
        difficulty = difficulty.name,
        estimatedDuration = estimatedDuration.toMillis(),
        targetMuscleGroups = targetMuscleGroups,
        requiredEquipment = requiredEquipment,
        isPublic = isPublic,
        createdBy = createdBy,
        createdAt = createdAt,
        updatedAt = updatedAt,
        usageCount = usageCount,
        rating = rating,
        tags = tags
    )
}

/**
 * Convert database TemplateExerciseEntity to domain TemplateExercise model
 */
fun TemplateExerciseEntity.toDomainModel(): TemplateExercise {
    return TemplateExercise(
        id = id,
        templateId = templateId,
        exerciseId = exerciseId,
        orderInTemplate = orderInTemplate,
        targetSets = targetSets,
        targetReps = if (targetRepsMin != null && targetRepsMax != null) {
            targetRepsMin!!..targetRepsMax!!
        } else null,
        targetWeight = targetWeight,
        restTime = Duration.ofMillis(restTime),
        notes = notes,
        isSuperset = isSuperset,
        supersetGroup = supersetGroup
    )
}

/**
 * Convert domain TemplateExercise model to database TemplateExerciseEntity
 */
fun TemplateExercise.toEntity(): TemplateExerciseEntity {
    return TemplateExerciseEntity(
        id = id,
        templateId = templateId,
        exerciseId = exerciseId,
        orderInTemplate = orderInTemplate,
        targetSets = targetSets,
        targetRepsMin = targetReps?.first,
        targetRepsMax = targetReps?.last,
        targetWeight = targetWeight,
        restTime = restTime.toMillis(),
        notes = notes,
        isSuperset = isSuperset,
        supersetGroup = supersetGroup
    )
}

/**
 * Convert database WorkoutTemplateWithExercisesEntity to domain WorkoutTemplateWithExercises
 */
fun WorkoutTemplateWithExercisesEntity.toDomainModel(): WorkoutTemplateWithExercises {
    return WorkoutTemplateWithExercises(
        template = template.toDomainModel(),
        exercises = exercises.map { it.toDomainModel() }
    )
}

/**
 * Convert database TemplateExerciseWithDetailsEntity to domain TemplateExerciseWithDetails
 */
fun TemplateExerciseWithDetailsEntity.toDomainModel(): TemplateExerciseWithDetails {
    return TemplateExerciseWithDetails(
        templateExercise = templateExercise.toDomainModel(),
        exercise = exercise.toDomainModel()
    )
}

/**
 * Convert database WorkoutRoutineEntity to domain WorkoutRoutine model
 */
fun WorkoutRoutineEntity.toDomainModel(): WorkoutRoutine {
    return WorkoutRoutine(
        id = id,
        name = name,
        description = description,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Convert domain WorkoutRoutine model to database WorkoutRoutineEntity
 */
fun WorkoutRoutine.toEntity(): WorkoutRoutineEntity {
    return WorkoutRoutineEntity(
        id = id,
        name = name,
        description = description,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Convert database RoutineScheduleEntity to domain RoutineSchedule model
 */
fun RoutineScheduleEntity.toDomainModel(): RoutineSchedule {
    return RoutineSchedule(
        id = id,
        routineId = routineId,
        templateId = templateId,
        dayOfWeek = dayOfWeek,
        timeOfDay = timeOfDay,
        isActive = isActive,
        notes = notes
    )
}

/**
 * Convert domain RoutineSchedule model to database RoutineScheduleEntity
 */
fun RoutineSchedule.toEntity(): RoutineScheduleEntity {
    return RoutineScheduleEntity(
        id = id,
        routineId = routineId,
        templateId = templateId,
        dayOfWeek = dayOfWeek,
        timeOfDay = timeOfDay,
        isActive = isActive,
        notes = notes
    )
}

/**
 * Convert database WorkoutRoutineWithDetailsEntity to domain WorkoutRoutineWithDetails
 */
fun WorkoutRoutineWithDetailsEntity.toDomainModel(): WorkoutRoutineWithDetails {
    return WorkoutRoutineWithDetails(
        routine = routine.toDomainModel(),
        schedules = schedules.map { it.toDomainModel() }
    )
}

/**
 * Convert database RoutineScheduleWithTemplateEntity to domain RoutineScheduleWithTemplate
 */
fun RoutineScheduleWithTemplateEntity.toDomainModel(): RoutineScheduleWithTemplate {
    return RoutineScheduleWithTemplate(
        schedule = schedule.toDomainModel(),
        template = template.toDomainModel()
    )
}