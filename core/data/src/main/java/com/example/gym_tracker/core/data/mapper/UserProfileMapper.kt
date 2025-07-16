package com.example.gym_tracker.core.data.mapper

import com.example.gym_tracker.core.data.model.FitnessGoal
import com.example.gym_tracker.core.data.model.FitnessLevel
import com.example.gym_tracker.core.data.model.UserProfile
import com.example.gym_tracker.core.database.entity.UserProfileEntity
import com.example.gym_tracker.core.database.entity.FitnessGoal as DbFitnessGoal
import com.example.gym_tracker.core.database.entity.FitnessLevel as DbFitnessLevel

/**
 * Convert database UserProfileEntity to domain UserProfile model
 */
fun UserProfileEntity.toDomainModel(): UserProfile {
    return UserProfile(
        id = id,
        age = age,
        weight = weight,
        height = height,
        fitnessLevel = fitnessLevel.toDomainModel(),
        goals = goals.map { it.toDomainModel() },
        limitations = limitations,
        preferredEquipment = preferredEquipment.map { it.toDomainModel() },
        trainingFrequency = trainingFrequency,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Convert domain UserProfile model to database UserProfileEntity
 */
fun UserProfile.toEntity(): UserProfileEntity {
    return UserProfileEntity(
        id = id,
        age = age,
        weight = weight,
        height = height,
        fitnessLevel = fitnessLevel.toEntity(),
        goals = goals.map { it.toEntity() },
        limitations = limitations,
        preferredEquipment = preferredEquipment.map { it.toEntity() },
        trainingFrequency = trainingFrequency,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Convert database FitnessLevel to domain FitnessLevel
 */
fun DbFitnessLevel.toDomainModel(): FitnessLevel {
    return when (this) {
        DbFitnessLevel.BEGINNER -> FitnessLevel.BEGINNER
        DbFitnessLevel.INTERMEDIATE -> FitnessLevel.INTERMEDIATE
        DbFitnessLevel.ADVANCED -> FitnessLevel.ADVANCED
        DbFitnessLevel.EXPERT -> FitnessLevel.EXPERT
    }
}

/**
 * Convert domain FitnessLevel to database FitnessLevel
 */
fun FitnessLevel.toEntity(): DbFitnessLevel {
    return when (this) {
        FitnessLevel.BEGINNER -> DbFitnessLevel.BEGINNER
        FitnessLevel.INTERMEDIATE -> DbFitnessLevel.INTERMEDIATE
        FitnessLevel.ADVANCED -> DbFitnessLevel.ADVANCED
        FitnessLevel.EXPERT -> DbFitnessLevel.EXPERT
    }
}

/**
 * Convert database FitnessGoal to domain FitnessGoal
 */
fun DbFitnessGoal.toDomainModel(): FitnessGoal {
    return when (this) {
        DbFitnessGoal.STRENGTH -> FitnessGoal.STRENGTH
        DbFitnessGoal.MUSCLE_BUILDING -> FitnessGoal.MUSCLE_BUILDING
        DbFitnessGoal.WEIGHT_LOSS -> FitnessGoal.WEIGHT_LOSS
        DbFitnessGoal.ENDURANCE -> FitnessGoal.ENDURANCE
        DbFitnessGoal.GENERAL_FITNESS -> FitnessGoal.GENERAL_FITNESS
        DbFitnessGoal.POWERLIFTING -> FitnessGoal.POWERLIFTING
        DbFitnessGoal.BODYBUILDING -> FitnessGoal.BODYBUILDING
    }
}

/**
 * Convert domain FitnessGoal to database FitnessGoal
 */
fun FitnessGoal.toEntity(): DbFitnessGoal {
    return when (this) {
        FitnessGoal.STRENGTH -> DbFitnessGoal.STRENGTH
        FitnessGoal.MUSCLE_BUILDING -> DbFitnessGoal.MUSCLE_BUILDING
        FitnessGoal.WEIGHT_LOSS -> DbFitnessGoal.WEIGHT_LOSS
        FitnessGoal.ENDURANCE -> DbFitnessGoal.ENDURANCE
        FitnessGoal.GENERAL_FITNESS -> DbFitnessGoal.GENERAL_FITNESS
        FitnessGoal.POWERLIFTING -> DbFitnessGoal.POWERLIFTING
        FitnessGoal.BODYBUILDING -> DbFitnessGoal.BODYBUILDING
    }
}