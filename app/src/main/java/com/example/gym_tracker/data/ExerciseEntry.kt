package com.example.gym_tracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class ExerciseEntry(
    @PrimaryKey(autoGenerate = true) val entryId: Long = 0,
    val exerciseId: Long,            // Verknüpft mit `Exercise` über dessen ID
    val date: String                 // Datum des Eintrags, z.B. als `YYYY-MM-DD`
)
