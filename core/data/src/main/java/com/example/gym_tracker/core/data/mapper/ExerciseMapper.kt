package com.example.gym_tracker.core.data.mapper

import com.example.gym_tracker.core.data.model.Exercise
import com.example.gym_tracker.core.data.model.ExerciseCategory
import com.example.gym_tracker.core.data.model.Equipment
import com.example.gym_tracker.core.data.model.MuscleGroup
import com.example.gym_tracker.core.database.entity.ExerciseEntity
import com.example.gym_tracker.core.database.entity.ExerciseCategory as DbExerciseCategory
import com.example.gym_tracker.core.database.entity.Equipment as DbEquipment
import com.example.gym_tracker.core.database.entity.MuscleGroup as DbMuscleGroup

/**
 * Convert database ExerciseEntity to domain Exercise model
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
        isCustom = isCustom
    )
}

/**
 * Convert domain Exercise model to database ExerciseEntity
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
        isCustom = isCustom
    )
}

/**
 * Convert database ExerciseCategory to domain ExerciseCategory
 */
fun DbExerciseCategory.toDomainModel(): ExerciseCategory {
    return when (this) {
        DbExerciseCategory.CHEST -> ExerciseCategory.CHEST
        DbExerciseCategory.BACK -> ExerciseCategory.BACK
        DbExerciseCategory.SHOULDERS -> ExerciseCategory.SHOULDERS
        DbExerciseCategory.ARMS -> ExerciseCategory.ARMS
        DbExerciseCategory.LEGS -> ExerciseCategory.LEGS
        DbExerciseCategory.CORE -> ExerciseCategory.CORE
        DbExerciseCategory.CARDIO -> ExerciseCategory.CARDIO
        DbExerciseCategory.FULL_BODY -> ExerciseCategory.FULL_BODY
    }
}

/**
 * Convert domain ExerciseCategory to database ExerciseCategory
 */
fun ExerciseCategory.toEntity(): DbExerciseCategory {
    return when (this) {
        ExerciseCategory.CHEST -> DbExerciseCategory.CHEST
        ExerciseCategory.BACK -> DbExerciseCategory.BACK
        ExerciseCategory.SHOULDERS -> DbExerciseCategory.SHOULDERS
        ExerciseCategory.ARMS -> DbExerciseCategory.ARMS
        ExerciseCategory.LEGS -> DbExerciseCategory.LEGS
        ExerciseCategory.CORE -> DbExerciseCategory.CORE
        ExerciseCategory.CARDIO -> DbExerciseCategory.CARDIO
        ExerciseCategory.FULL_BODY -> DbExerciseCategory.FULL_BODY
    }
}

/**
 * Convert database MuscleGroup to domain MuscleGroup
 */
fun DbMuscleGroup.toDomainModel(): MuscleGroup {
    return when (this) {
        DbMuscleGroup.CHEST -> MuscleGroup.CHEST
        DbMuscleGroup.UPPER_BACK -> MuscleGroup.UPPER_BACK
        DbMuscleGroup.LOWER_BACK -> MuscleGroup.LOWER_BACK
        DbMuscleGroup.FRONT_DELTS -> MuscleGroup.FRONT_DELTS
        DbMuscleGroup.SIDE_DELTS -> MuscleGroup.SIDE_DELTS
        DbMuscleGroup.REAR_DELTS -> MuscleGroup.REAR_DELTS
        DbMuscleGroup.BICEPS -> MuscleGroup.BICEPS
        DbMuscleGroup.TRICEPS -> MuscleGroup.TRICEPS
        DbMuscleGroup.FOREARMS -> MuscleGroup.FOREARMS
        DbMuscleGroup.QUADRICEPS -> MuscleGroup.QUADRICEPS
        DbMuscleGroup.HAMSTRINGS -> MuscleGroup.HAMSTRINGS
        DbMuscleGroup.GLUTES -> MuscleGroup.GLUTES
        DbMuscleGroup.CALVES -> MuscleGroup.CALVES
        DbMuscleGroup.ABS -> MuscleGroup.ABS
        DbMuscleGroup.OBLIQUES -> MuscleGroup.OBLIQUES
        DbMuscleGroup.LOWER_BACK_MUSCLES -> MuscleGroup.LOWER_BACK_MUSCLES
    }
}

/**
 * Convert domain MuscleGroup to database MuscleGroup
 */
fun MuscleGroup.toEntity(): DbMuscleGroup {
    return when (this) {
        MuscleGroup.CHEST -> DbMuscleGroup.CHEST
        MuscleGroup.UPPER_BACK -> DbMuscleGroup.UPPER_BACK
        MuscleGroup.LOWER_BACK -> DbMuscleGroup.LOWER_BACK
        MuscleGroup.FRONT_DELTS -> DbMuscleGroup.FRONT_DELTS
        MuscleGroup.SIDE_DELTS -> DbMuscleGroup.SIDE_DELTS
        MuscleGroup.REAR_DELTS -> DbMuscleGroup.REAR_DELTS
        MuscleGroup.BICEPS -> DbMuscleGroup.BICEPS
        MuscleGroup.TRICEPS -> DbMuscleGroup.TRICEPS
        MuscleGroup.FOREARMS -> DbMuscleGroup.FOREARMS
        MuscleGroup.QUADRICEPS -> DbMuscleGroup.QUADRICEPS
        MuscleGroup.HAMSTRINGS -> DbMuscleGroup.HAMSTRINGS
        MuscleGroup.GLUTES -> DbMuscleGroup.GLUTES
        MuscleGroup.CALVES -> DbMuscleGroup.CALVES
        MuscleGroup.ABS -> DbMuscleGroup.ABS
        MuscleGroup.OBLIQUES -> DbMuscleGroup.OBLIQUES
        MuscleGroup.LOWER_BACK_MUSCLES -> DbMuscleGroup.LOWER_BACK_MUSCLES
    }
}

/**
 * Convert database Equipment to domain Equipment
 */
fun DbEquipment.toDomainModel(): Equipment {
    return when (this) {
        DbEquipment.BARBELL -> Equipment.BARBELL
        DbEquipment.DUMBBELL -> Equipment.DUMBBELL
        DbEquipment.CABLE -> Equipment.CABLE
        DbEquipment.MACHINE -> Equipment.MACHINE
        DbEquipment.BODYWEIGHT -> Equipment.BODYWEIGHT
        DbEquipment.RESISTANCE_BAND -> Equipment.RESISTANCE_BAND
        DbEquipment.KETTLEBELL -> Equipment.KETTLEBELL
        DbEquipment.OTHER -> Equipment.OTHER
    }
}

/**
 * Convert domain Equipment to database Equipment
 */
fun Equipment.toEntity(): DbEquipment {
    return when (this) {
        Equipment.BARBELL -> DbEquipment.BARBELL
        Equipment.DUMBBELL -> DbEquipment.DUMBBELL
        Equipment.CABLE -> DbEquipment.CABLE
        Equipment.MACHINE -> DbEquipment.MACHINE
        Equipment.BODYWEIGHT -> DbEquipment.BODYWEIGHT
        Equipment.RESISTANCE_BAND -> DbEquipment.RESISTANCE_BAND
        Equipment.KETTLEBELL -> DbEquipment.KETTLEBELL
        Equipment.OTHER -> DbEquipment.OTHER
    }
}