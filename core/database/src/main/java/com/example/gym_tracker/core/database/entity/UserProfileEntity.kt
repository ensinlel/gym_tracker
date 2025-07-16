package com.example.gym_tracker.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
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