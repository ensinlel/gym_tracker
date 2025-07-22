package com.example.gym_tracker.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gym_tracker.core.common.enums.Equipment
import com.example.gym_tracker.core.common.enums.FitnessGoal
import com.example.gym_tracker.core.common.enums.FitnessLevel
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