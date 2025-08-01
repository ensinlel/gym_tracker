package com.example.gym_tracker.core.data.di

import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import com.example.gym_tracker.core.data.repository.ExerciseRepository
import com.example.gym_tracker.core.data.repository.ExerciseInstanceRepository
import com.example.gym_tracker.core.data.repository.ExerciseSetRepository
import com.example.gym_tracker.core.data.repository.GoalRepository
import com.example.gym_tracker.core.data.repository.UserProfileRepository
import com.example.gym_tracker.core.data.repository.WorkoutRepository
import com.example.gym_tracker.core.data.repository.WorkoutTemplateRepository
import com.example.gym_tracker.core.data.repository.WorkoutRoutineRepository
import com.example.gym_tracker.core.data.repository.impl.AnalyticsRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.ExerciseRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.ExerciseInstanceRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.ExerciseSetRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.GoalRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.UserProfileRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.WorkoutRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.WorkoutTemplateRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.WorkoutRoutineRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for binding repository interfaces to their implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(
        exerciseRepositoryImpl: ExerciseRepositoryImpl
    ): ExerciseRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(
        workoutRepositoryImpl: WorkoutRepositoryImpl
    ): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindExerciseInstanceRepository(
        exerciseInstanceRepositoryImpl: ExerciseInstanceRepositoryImpl
    ): ExerciseInstanceRepository

    @Binds
    @Singleton
    abstract fun bindExerciseSetRepository(
        exerciseSetRepositoryImpl: ExerciseSetRepositoryImpl
    ): ExerciseSetRepository

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        userProfileRepositoryImpl: UserProfileRepositoryImpl
    ): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(
        analyticsRepositoryImpl: AnalyticsRepositoryImpl
    ): AnalyticsRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutTemplateRepository(
        workoutTemplateRepositoryImpl: WorkoutTemplateRepositoryImpl
    ): WorkoutTemplateRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutRoutineRepository(
        workoutRoutineRepositoryImpl: WorkoutRoutineRepositoryImpl
    ): WorkoutRoutineRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(
        goalRepositoryImpl: GoalRepositoryImpl
    ): GoalRepository
}