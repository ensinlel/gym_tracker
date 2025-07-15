package com.example.gym_tracker.data
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val exerciseId: Long = 0,
    val exerciseName: String,
    val workoutName: String,      // Zugehöriges Workout (z.B. "Push", "Pull", "Legs")
    val muscleGroup: String,        // Zugehörige Muskelgruppe (z.B. "Chest", "Back")
    val isVisible: Boolean = false
)