package com.example.gym_tracker.core.database.entity

/**
 * Data models for advanced analytics queries - Task 3.2
 */

/**
 * Volume progression data point for analytics charts
 */
data class VolumeProgressionPoint(
    val workout_date: String,
    val daily_volume: Double,
    val workout_count: Int,
    val avg_workout_volume: Double
)

/**
 * Strength progression data point for specific exercise
 */
data class StrengthProgressionPoint(
    val workout_date: String,
    val max_weight: Double,
    val estimated_1rm: Double,
    val avg_weight: Double,
    val total_volume: Double
)

/**
 * Workout frequency analysis data
 */
data class WorkoutFrequencyPoint(
    val day_of_week: String, // 0=Sunday, 1=Monday, etc.
    val week_year: String,   // Format: YYYY-WW
    val workout_count: Int,
    val avg_volume: Double,
    val avg_duration_minutes: Double
)

/**
 * Muscle group distribution data
 */
data class MuscleGroupDistribution(
    val muscleGroups: String,
    val exercise_count: Int,
    val total_volume: Double,
    val avg_weight: Double
)

/**
 * Workout consistency data for streak analysis
 */
data class WorkoutConsistencyPoint(
    val workout_date: String,
    val workouts_per_day: Int,
    val prev_date: String?
)

/**
 * Workout performance trend comparison
 */
data class WorkoutPerformanceTrend(
    val period: String, // 'current' or 'previous'
    val workout_count: Int,
    val avg_volume: Double,
    val max_volume: Double,
    val avg_duration_minutes: Double,
    val avg_rating: Double
)

/**
 * Volume progression data for analytics
 */
data class VolumeProgressionData(
    val workout_date: String,
    val total_volume: Double,
    val unique_exercises: Int,
    val total_sets: Int,
    val avg_weight: Double,
    val avg_reps: Double
)

/**
 * Exercise strength trend data
 */
data class ExerciseStrengthTrend(
    val exercise_name: String,
    val exerciseId: String,
    val workout_date: String,
    val max_weight: Double,
    val estimated_1rm: Double,
    val avg_weight: Double,
    val exercise_volume: Double
)

/**
 * Personal record progression data
 */
data class PersonalRecordData(
    val exercise_name: String,
    val exerciseId: String,
    val weight: Double,
    val reps: Int,
    val estimated_1rm: Double,
    val achieved_date: Long,
    val pr_rank: Int
)

/**
 * Training intensity analysis data
 */
data class IntensityAnalysisData(
    val rpe: Int?,
    val set_count: Int,
    val avg_weight: Double,
    val avg_reps: Double,
    val total_volume: Double,
    val month_year: String
)

/**
 * Rest time analysis data
 */
data class RestTimeAnalysisData(
    val exercise_name: String,
    val exerciseId: String,
    val avg_rest_time: Long,
    val min_rest_time: Long,
    val max_rest_time: Long,
    val set_count: Int,
    val avg_weight_with_rest: Double
)