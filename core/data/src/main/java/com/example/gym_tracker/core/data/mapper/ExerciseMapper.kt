package com.example.gym_tracker.core.data.mapper

import com.example.gym_tracker.core.data.model.Exercise
import com.example.gym_tracker.core.data.model.ExerciseCategory
import com.example.gym_tracker.core.data.model.MuscleGroup
import com.example.gym_tracker.core.data.model.Equipment
import com.example.gym_tracker.core.database.entity.ExerciseEntity
import com.example.gym_tracker.core.database.entity.ExerciseCategory as EntityExerciseCategory
import com.example.gym_tracker.core.database.entity.MuscleGroup as EntityMuscleGroup
import com.example.gym_tracker.core.database.entity.Equipment as EntityEquipment

/**
 * Convert ExerciseEntity to Exercise domain model
 */
fun ExerciseEntity.toDomainModel(): Exercise {
    return Exercise(
        id = id,
        name = name,
        category = category.toDomainModel(),
        muscleGroups = muscleGroups.map { it.toDomainModel() },
        equipment = equipment.toDomainModel(),
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
        category = category.toEntity(),
        muscleGroups = muscleGroups.map { it.toEntity() },
        equipment = equipment.toEntity(),
        instructions = instructions,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isCustom = isCustom,
        isStarMarked = isStarMarked
    )
}

// ExerciseCategory mappers
fun EntityExerciseCategory.toDomainModel(): ExerciseCategory {
    return when (this) {
        EntityExerciseCategory.CHEST -> ExerciseCategory.CHEST
        EntityExerciseCategory.BACK -> ExerciseCategory.BACK
        EntityExerciseCategory.SHOULDERS -> ExerciseCategory.SHOULDERS
        EntityExerciseCategory.ARMS -> ExerciseCategory.ARMS
        EntityExerciseCategory.LEGS -> ExerciseCategory.LEGS
        EntityExerciseCategory.CORE -> ExerciseCategory.CORE
        EntityExerciseCategory.CARDIO -> ExerciseCategory.CARDIO
        EntityExerciseCategory.FULL_BODY -> ExerciseCategory.FULL_BODY
    }
}

fun ExerciseCategory.toEntity(): EntityExerciseCategory {
    return when (this) {
        ExerciseCategory.CHEST -> EntityExerciseCategory.CHEST
        ExerciseCategory.BACK -> EntityExerciseCategory.BACK
        ExerciseCategory.SHOULDERS -> EntityExerciseCategory.SHOULDERS
        ExerciseCategory.ARMS -> EntityExerciseCategory.ARMS
        ExerciseCategory.LEGS -> EntityExerciseCategory.LEGS
        ExerciseCategory.CORE -> EntityExerciseCategory.CORE
        ExerciseCategory.CARDIO -> EntityExerciseCategory.CARDIO
        ExerciseCategory.FULL_BODY -> EntityExerciseCategory.FULL_BODY
    }
}

// MuscleGroup mappers
fun EntityMuscleGroup.toDomainModel(): MuscleGroup {
    return when (this) {
        EntityMuscleGroup.CHEST -> MuscleGroup.CHEST
        EntityMuscleGroup.UPPER_BACK -> MuscleGroup.UPPER_BACK
        EntityMuscleGroup.LOWER_BACK -> MuscleGroup.LOWER_BACK
        EntityMuscleGroup.FRONT_DELTS -> MuscleGroup.FRONT_DELTS
        EntityMuscleGroup.SIDE_DELTS -> MuscleGroup.SIDE_DELTS
        EntityMuscleGroup.REAR_DELTS -> MuscleGroup.REAR_DELTS
        EntityMuscleGroup.BICEPS -> MuscleGroup.BICEPS
        EntityMuscleGroup.TRICEPS -> MuscleGroup.TRICEPS
        EntityMuscleGroup.FOREARMS -> MuscleGroup.FOREARMS
        EntityMuscleGroup.QUADRICEPS -> MuscleGroup.QUADRICEPS
        EntityMuscleGroup.HAMSTRINGS -> MuscleGroup.HAMSTRINGS
        EntityMuscleGroup.GLUTES -> MuscleGroup.GLUTES
        EntityMuscleGroup.CALVES -> MuscleGroup.CALVES
        EntityMuscleGroup.ABS -> MuscleGroup.ABS
        EntityMuscleGroup.OBLIQUES -> MuscleGroup.OBLIQUES
        EntityMuscleGroup.LOWER_BACK_MUSCLES -> MuscleGroup.LOWER_BACK_MUSCLES
    }
}

fun MuscleGroup.toEntity(): EntityMuscleGroup {
    return when (this) {
        MuscleGroup.CHEST -> EntityMuscleGroup.CHEST
        MuscleGroup.UPPER_BACK -> EntityMuscleGroup.UPPER_BACK
        MuscleGroup.LOWER_BACK -> EntityMuscleGroup.LOWER_BACK
        MuscleGroup.FRONT_DELTS -> EntityMuscleGroup.FRONT_DELTS
        MuscleGroup.SIDE_DELTS -> EntityMuscleGroup.SIDE_DELTS
        MuscleGroup.REAR_DELTS -> EntityMuscleGroup.REAR_DELTS
        MuscleGroup.BICEPS -> EntityMuscleGroup.BICEPS
        MuscleGroup.TRICEPS -> EntityMuscleGroup.TRICEPS
        MuscleGroup.FOREARMS -> EntityMuscleGroup.FOREARMS
        MuscleGroup.QUADRICEPS -> EntityMuscleGroup.QUADRICEPS
        MuscleGroup.HAMSTRINGS -> EntityMuscleGroup.HAMSTRINGS
        MuscleGroup.GLUTES -> EntityMuscleGroup.GLUTES
        MuscleGroup.CALVES -> EntityMuscleGroup.CALVES
        MuscleGroup.ABS -> EntityMuscleGroup.ABS
        MuscleGroup.OBLIQUES -> EntityMuscleGroup.OBLIQUES
        MuscleGroup.LOWER_BACK_MUSCLES -> EntityMuscleGroup.LOWER_BACK_MUSCLES
    }
}

// Equipment mappers
fun EntityEquipment.toDomainModel(): Equipment {
    return when (this) {
        EntityEquipment.BARBELL -> Equipment.BARBELL
        EntityEquipment.DUMBBELL -> Equipment.DUMBBELL
        EntityEquipment.CABLE -> Equipment.CABLE
        EntityEquipment.MACHINE -> Equipment.MACHINE
        EntityEquipment.BODYWEIGHT -> Equipment.BODYWEIGHT
        EntityEquipment.RESISTANCE_BAND -> Equipment.RESISTANCE_BAND
        EntityEquipment.KETTLEBELL -> Equipment.KETTLEBELL
        EntityEquipment.OTHER -> Equipment.OTHER
    }
}

fun Equipment.toEntity(): EntityEquipment {
    return when (this) {
        Equipment.BARBELL -> EntityEquipment.BARBELL
        Equipment.DUMBBELL -> EntityEquipment.DUMBBELL
        Equipment.CABLE -> EntityEquipment.CABLE
        Equipment.MACHINE -> EntityEquipment.MACHINE
        Equipment.BODYWEIGHT -> EntityEquipment.BODYWEIGHT
        Equipment.RESISTANCE_BAND -> EntityEquipment.RESISTANCE_BAND
        Equipment.KETTLEBELL -> EntityEquipment.KETTLEBELL
        Equipment.OTHER -> EntityEquipment.OTHER
    }
}