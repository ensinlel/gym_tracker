package com.example.gym_tracker.core.data.model

import java.time.Instant

/**
 * Domain model for User Profile
 */
data class UserProfile(
    val id: String,
    val age: Int,
    val weight: Double,
    val height: Double,
    val fitnessLevel: FitnessLevel,
    val goals: List<FitnessGoal>,
    val limitations: List<String> = emptyList(),
    val preferredEquipment: List<Equipment> = emptyList(),
    val trainingFrequency: Int = 3, // days per week
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class FitnessLevel {
    BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
}

enum class FitnessGoal {
    STRENGTH, MUSCLE_BUILDING, WEIGHT_LOSS, ENDURANCE, GENERAL_FITNESS, POWERLIFTING, BODYBUILDING
}