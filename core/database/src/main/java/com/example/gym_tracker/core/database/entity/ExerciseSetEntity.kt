package com.example.gym_tracker.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.Duration
import java.util.UUID

@Entity(
    tableName = "exercise_sets",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseInstanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseInstanceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        androidx.room.Index(value = ["exerciseInstanceId"]),
        androidx.room.Index(value = ["exerciseInstanceId", "setNumber"]),
        androidx.room.Index(value = ["exerciseInstanceId", "isWarmup"])
    ]
)
data class ExerciseSetEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val exerciseInstanceId: String,
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val restTime: Duration = Duration.ZERO,
    val rpe: Int? = null, // Rate of Perceived Exertion (1-10)
    val tempo: String? = null, // e.g., "3-1-2-1"
    val isWarmup: Boolean = false,
    val isFailure: Boolean = false,
    val notes: String = ""
)