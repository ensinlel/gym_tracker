package com.example.gym_tracker.core.data.model

import com.example.gym_tracker.core.common.enums.Equipment
import com.example.gym_tracker.core.common.enums.FitnessGoal
import com.example.gym_tracker.core.common.enums.FitnessLevel
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