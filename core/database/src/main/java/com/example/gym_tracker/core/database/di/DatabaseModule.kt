package com.example.gym_tracker.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.gym_tracker.core.database.GymTrackerDatabase
import com.example.gym_tracker.core.database.dao.ExerciseDao
import com.example.gym_tracker.core.database.dao.WorkoutDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGymTrackerDatabase(
        @ApplicationContext context: Context
    ): GymTrackerDatabase {
        return Room.databaseBuilder(
            context,
            GymTrackerDatabase::class.java,
            "gym_tracker_database"
        ).build()
    }

    @Provides
    fun provideWorkoutDao(database: GymTrackerDatabase): WorkoutDao {
        return database.workoutDao()
    }

    @Provides
    fun provideExerciseDao(database: GymTrackerDatabase): ExerciseDao {
        return database.exerciseDao()
    }
}