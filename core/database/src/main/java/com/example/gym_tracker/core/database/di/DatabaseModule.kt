package com.example.gym_tracker.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.gym_tracker.core.database.GymTrackerDatabase
import com.example.gym_tracker.core.database.dao.ExerciseDao
import com.example.gym_tracker.core.database.dao.ExerciseInstanceDao
import com.example.gym_tracker.core.database.dao.ExerciseSetDao
import com.example.gym_tracker.core.database.dao.GoalDao
import com.example.gym_tracker.core.database.dao.RoutineScheduleDao
import com.example.gym_tracker.core.database.dao.TemplateExerciseDao
import com.example.gym_tracker.core.database.dao.UserProfileDao
import com.example.gym_tracker.core.database.dao.WeightHistoryDao
import com.example.gym_tracker.core.database.dao.WorkoutDao
import com.example.gym_tracker.core.database.dao.WorkoutRoutineDao
import com.example.gym_tracker.core.database.dao.WorkoutTemplateDao
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
            "gym_tracker_database_v8"
        )
        .addMigrations(
            GymTrackerDatabase.MIGRATION_1_2,
            GymTrackerDatabase.MIGRATION_2_3,
            GymTrackerDatabase.MIGRATION_3_4,
            GymTrackerDatabase.MIGRATION_4_5,
            GymTrackerDatabase.MIGRATION_5_6,
            GymTrackerDatabase.MIGRATION_6_7,
            GymTrackerDatabase.MIGRATION_7_8
        )
        .fallbackToDestructiveMigration()
        .fallbackToDestructiveMigrationOnDowngrade()
        .build()
    }

    @Provides
    fun provideWorkoutDao(database: GymTrackerDatabase): WorkoutDao {
        return database.workoutDao()
    }

    @Provides
    fun provideExerciseDao(database: GymTrackerDatabase): ExerciseDao {
        return database.exerciseDao()
    }

    @Provides
    fun provideExerciseInstanceDao(database: GymTrackerDatabase): ExerciseInstanceDao {
        return database.exerciseInstanceDao()
    }

    @Provides
    fun provideExerciseSetDao(database: GymTrackerDatabase): ExerciseSetDao {
        return database.exerciseSetDao()
    }

    @Provides
    fun provideUserProfileDao(database: GymTrackerDatabase): UserProfileDao {
        return database.userProfileDao()
    }

    @Provides
    fun provideWeightHistoryDao(database: GymTrackerDatabase): WeightHistoryDao {
        return database.weightHistoryDao()
    }

    @Provides
    fun provideWorkoutTemplateDao(database: GymTrackerDatabase): WorkoutTemplateDao {
        return database.workoutTemplateDao()
    }

    @Provides
    fun provideTemplateExerciseDao(database: GymTrackerDatabase): TemplateExerciseDao {
        return database.templateExerciseDao()
    }

    @Provides
    fun provideWorkoutRoutineDao(database: GymTrackerDatabase): WorkoutRoutineDao {
        return database.workoutRoutineDao()
    }

    @Provides
    fun provideRoutineScheduleDao(database: GymTrackerDatabase): RoutineScheduleDao {
        return database.routineScheduleDao()
    }

    @Provides
    fun provideGoalDao(database: GymTrackerDatabase): GoalDao {
        return database.goalDao()
    }
}