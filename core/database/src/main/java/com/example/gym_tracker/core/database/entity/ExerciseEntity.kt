package com.example.gym_tracker.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gym_tracker.core.common.enums.Equipment
import com.example.gym_tracker.core.common.enums.ExerciseCategory
import com.example.gym_tracker.core.common.enums.MuscleGroup
import java.time.Instant
import java.util.UUID

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: ExerciseCategory,
    val muscleGroups: List<MuscleGroup>,
    val equipment: Equipment,
    val instructions: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isCustom: Boolean = false,
    val isStarMarked: Boolean = false
)