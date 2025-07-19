package com.example.gym_tracker.core.data.model

import java.time.LocalDate

/**
 * Enum representing the direction of a trend (positive, negative, or stable)
 */
enum class TrendDirection {
    UP, DOWN, STABLE
}

/**
 * Data class representing workout streak information
 */
data class WorkoutStreak(
    val currentStreak: Int,
    val longestStreak: Int,
    val daysSinceLastWorkout: Int,
    val streakType: StreakType,
    val encouragingMessage: String? = null
)

/**
 * Enum representing the type of streak measurement
 */
enum class StreakType {
    DAYS, WEEKS
}

/**
 * Data class representing monthly workout statistics
 */
data class MonthlyWorkoutStats(
    val currentMonthWorkouts: Int,
    val previousMonthWorkouts: Int,
    val weeklyAverageCurrentMonth: Double,
    val weeklyAveragePreviousMonth: Double,
    val trendDirection: TrendDirection,
    val percentageChange: Double
)

/**
 * Data class representing volume progress information
 */
data class VolumeProgress(
    val totalVolumeThisMonth: Double,
    val totalVolumePreviousMonth: Double,
    val trendDirection: TrendDirection,
    val percentageChange: Double,
    val mostImprovedExercise: ExerciseImprovement?
)

/**
 * Data class representing exercise improvement information
 */
data class ExerciseImprovement(
    val exerciseName: String,
    val previousBestWeight: Double,
    val currentBestWeight: Double,
    val improvementPercentage: Double
)

/**
 * Data class representing personal record information
 */
data class PersonalRecord(
    val exerciseName: String,
    val weight: Double,
    val reps: Int,
    val achievedDate: LocalDate,
    val isRecent: Boolean = false // within last 30 days
)

/**
 * Data class representing weight progress information
 */
data class WeightProgress(
    val currentWeight: Double?,
    val weightThirtyDaysAgo: Double?,
    val weightChange: Double?,
    val trendDirection: TrendDirection,
    val isStable: Boolean, // within 2 lbs threshold
    val hasRecentData: Boolean
)