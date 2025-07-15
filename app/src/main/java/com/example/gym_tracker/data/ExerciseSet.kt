package com.example.gym_tracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "sets", foreignKeys = [ForeignKey(entity = ExerciseEntry::class, parentColumns = ["entryId"], childColumns = ["entryId"], onDelete = ForeignKey.CASCADE)]
)
data class ExerciseSet(
    @PrimaryKey(autoGenerate = true) val setId: Long = 0,
    val entryId: Long,           // Verknüpft mit `Entry` über dessen ID
    val weight: Float,           // Gewicht des Sets
    val reps: Int                // Wiederholungen des Sets
)
