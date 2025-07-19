package com.example.gym_tracker.core.data.usecase

import com.example.gym_tracker.core.data.model.StreakType
import com.example.gym_tracker.core.data.model.WorkoutStreak
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for calculating workout streak information including current streak,
 * longest streak, days since last workout, and encouraging messages.
 * 
 * This use case encapsulates the business logic for:
 * - Calculating current and longest workout streaks
 * - Determining days since last workout
 * - Providing encouraging messages for inactive periods
 * - Detecting streak type (days vs weeks)
 */
@Singleton
class GetWorkoutStreakUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    
    /**
     * Executes the use case to get comprehensive workout streak information
     * 
     * @return WorkoutStreak containing current streak, longest streak, days since last workout,
     *         streak type, and encouraging message
     */
    suspend operator fun invoke(): WorkoutStreak {
        val baseStreak = analyticsRepository.getWorkoutStreak()
        
        // Enhanced streak type detection and encouraging message logic
        val enhancedStreak = enhanceStreakData(baseStreak)
        
        return enhancedStreak
    }
    
    /**
     * Enhances the streak data with improved logic for streak type detection
     * and more comprehensive encouraging messages
     */
    private fun enhanceStreakData(streak: WorkoutStreak): WorkoutStreak {
        // Determine streak type based on streak length
        val streakType = determineStreakType(streak.currentStreak, streak.longestStreak)
        
        // Generate enhanced encouraging message
        val encouragingMessage = generateEncouragingMessage(
            streak.daysSinceLastWorkout,
            streak.currentStreak,
            streak.longestStreak
        )
        
        return streak.copy(
            streakType = streakType,
            encouragingMessage = encouragingMessage
        )
    }
    
    /**
     * Determines the appropriate streak type (DAYS or WEEKS) based on streak lengths
     */
    private fun determineStreakType(currentStreak: Int, longestStreak: Int): StreakType {
        // Use weeks if either current or longest streak is 14+ days
        return if (currentStreak >= 14 || longestStreak >= 14) {
            StreakType.WEEKS
        } else {
            StreakType.DAYS
        }
    }
    
    /**
     * Generates encouraging messages based on workout patterns and inactivity periods
     */
    private fun generateEncouragingMessage(
        daysSinceLastWorkout: Int,
        currentStreak: Int,
        longestStreak: Int
    ): String {
        return when {
            // Active today
            daysSinceLastWorkout == 0 -> {
                when {
                    currentStreak >= 7 -> "Amazing! You're on a ${currentStreak}-day streak! 🔥"
                    currentStreak >= 3 -> "Great job working out today! Keep the momentum going! 💪"
                    else -> "Great job working out today! 🎯"
                }
            }
            
            // Active yesterday
            daysSinceLastWorkout == 1 -> {
                when {
                    currentStreak >= 7 -> "You worked out yesterday and have a ${currentStreak}-day streak! Don't break it now! 🔥"
                    else -> "You worked out yesterday. Keep the momentum going! 💪"
                }
            }
            
            // 2-3 days inactive (requirement: over 3 days)
            daysSinceLastWorkout in 2..3 -> {
                "It's been $daysSinceLastWorkout days. Time to get back to it! 🎯"
            }
            
            // 4-7 days inactive (requirement: over 3 days)
            daysSinceLastWorkout in 4..7 -> {
                if (longestStreak > 0) {
                    "You've got this! Your longest streak was $longestStreak days. Time to start a new one! 🚀"
                } else {
                    "You've got this! It's been a week, but you can start your streak today! 🚀"
                }
            }
            
            // 1-2 weeks inactive
            daysSinceLastWorkout in 8..14 -> {
                if (longestStreak >= 7) {
                    "Remember your ${longestStreak}-day streak? You can do it again! Every day is a fresh start! 🌟"
                } else {
                    "It's been over a week, but every day is a new opportunity! Let's restart! 🌟"
                }
            }
            
            // Over 2 weeks inactive
            else -> {
                if (longestStreak > 0) {
                    "Every journey starts with a single step. You've done it before, you can do it again! 💫"
                } else {
                    "Every day is a new opportunity to start fresh! Your fitness journey begins now! 💫"
                }
            }
        }
    }
}