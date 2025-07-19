package com.example.gym_tracker.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.UUID

@Entity(
    tableName = "weight_history",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["userProfileId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WeightHistoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userProfileId: String,
    val weight: Double,
    val recordedDate: LocalDate,
    val notes: String = ""
)