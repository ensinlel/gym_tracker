package com.example.gym_tracker.core.data.mapper

import com.example.gym_tracker.core.data.model.UserProfile
import com.example.gym_tracker.core.database.entity.UserProfileEntity

/**
 * Convert database UserProfileEntity to domain UserProfile model
 */
fun UserProfileEntity.toDomainModel(): UserProfile {
    return UserProfile(
        id = id,
        age = age,
        weight = weight,
        height = height,
        fitnessLevel = fitnessLevel,
        goals = goals,
        limitations = limitations,
        preferredEquipment = preferredEquipment,
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
        fitnessLevel = fitnessLevel,
        goals = goals,
        limitations = limitations,
        preferredEquipment = preferredEquipment,
        trainingFrequency = trainingFrequency,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}