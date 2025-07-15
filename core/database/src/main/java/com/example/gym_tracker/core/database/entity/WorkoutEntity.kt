package com.example.gym_tracker.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val startTime: Instant,
    val endTime: Instant?,
    val notes: String = ""
)