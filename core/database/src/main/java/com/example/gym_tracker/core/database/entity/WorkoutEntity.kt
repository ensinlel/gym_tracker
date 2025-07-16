package com.example.gym_tracker.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val templateId: String?,
    val startTime: Instant,
    val endTime: Instant?,
    val notes: String = "",
    val rating: Int? = null, // 1-5 stars
    val totalVolume: Double = 0.0,
    val averageRestTime: Duration = Duration.ZERO
)

data class WorkoutWithDetails(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutId"
    )
    val exerciseInstances: List<ExerciseInstanceEntity>
)