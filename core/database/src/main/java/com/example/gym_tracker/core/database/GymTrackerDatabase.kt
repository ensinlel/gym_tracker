package com.example.gym_tracker.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gym_tracker.core.database.dao.ExerciseDao
import com.example.gym_tracker.core.database.dao.WorkoutDao
import com.example.gym_tracker.core.database.entity.ExerciseEntity
import com.example.gym_tracker.core.database.entity.WorkoutEntity

@Database(
    entities = [
        WorkoutEntity::class,
        ExerciseEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GymTrackerDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
}