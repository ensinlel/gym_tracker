package com.example.gym_tracker.core.data.usecase

import com.example.gym_tracker.core.data.model.MonthlyWorkoutStats
import com.example.gym_tracker.core.data.model.TrendDirection
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for calculating monthly workout statistics including workout count comparisons,
 * weekly averages, and trend direction calculations.
 * 
 * This use case encapsulates the business logic for:
 * - Comparing current month workouts to previous month (Requirements 2.1, 2.2, 2.3)
 * - Calculating trend direction with positive/negative indicators (Requirement 2.4)
 * - Computing weekly averages for current month (Requirement 2.5)
 * - Handling incomplete months with projected averages (Requirement 2.6)
 */
@Singleton
class GetMonthlyStatsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    
    /**
     * Executes the use case to get comprehensive monthly workout statistics
     * 
     * @return MonthlyWorkoutStats containing current/previous month comparisons,
     *         weekly averages, trend direction, and percentage change
     */
    suspend operator fun invoke(): MonthlyWorkoutStats {
        val baseStats = analyticsRepository.getMonthlyWorkoutStats()
        
        // Enhanced monthly statistics with improved weekly average calculations
        val enhancedStats = enhanceMonthlyStats(baseStats)
        
        return enhancedStats
    }
    
    /**
     * Enhances the monthly statistics with improved logic for weekly averages
     * and trend direction calculations
     */
    private suspend fun enhanceMonthlyStats(stats: MonthlyWorkoutStats): MonthlyWorkoutStats {
        val now = LocalDate.now()
        val currentMonth = now.monthValue
        val currentYear = now.year
        
        // Calculate enhanced weekly averages with incomplete month handling
        val enhancedWeeklyAverage = calculateEnhancedWeeklyAverage(currentYear, currentMonth)
        
        // Calculate more precise trend direction
        val enhancedTrendDirection = calculateTrendDirection(
            stats.currentMonthWorkouts,
            stats.previousMonthWorkouts
        )
        
        // Calculate percentage change with better precision
        val enhancedPercentageChange = calculatePercentageChange(
            stats.currentMonthWorkouts,
            stats.previousMonthWorkouts
        )
        
        return stats.copy(
            weeklyAverageCurrentMonth = enhancedWeeklyAverage,
            trendDirection = enhancedTrendDirection,
            percentageChange = enhancedPercentageChange
        )
    }
    
    /**
     * Calculates enhanced weekly average for current month handling incomplete months
     * Requirement 2.5: Display average workouts per week for the current month
     * Requirement 2.6: Use completed weeks or show projected average if month is incomplete
     */
    private suspend fun calculateEnhancedWeeklyAverage(year: Int, month: Int): Double {
        val now = LocalDate.now()
        val startOfMonth = LocalDate.of(year, month, 1)
        val endOfMonth = startOfMonth.plusMonths(1).minusDays(1)
        
        // Check if we're calculating for the current month
        val isCurrentMonth = year == now.year && month == now.monthValue
        val calculationEndDate = if (isCurrentMonth) now else endOfMonth
        
        val totalWorkouts = analyticsRepository.getWorkoutCountForDateRange(startOfMonth, calculationEndDate)
        
        return if (isCurrentMonth) {
            // For current month, calculate based on elapsed days
            val daysElapsed = ChronoUnit.DAYS.between(startOfMonth, calculationEndDate) + 1
            val weeksElapsed = daysElapsed.toDouble() / 7.0
            
            if (weeksElapsed >= 1.0) {
                // If at least a week has passed, use actual average
                totalWorkouts.toDouble() / weeksElapsed
            } else {
                // For partial weeks, project based on current pace
                val dailyAverage = totalWorkouts.toDouble() / daysElapsed
                dailyAverage * 7.0
            }
        } else {
            // For complete months, use standard calculation
            val daysInMonth = endOfMonth.dayOfMonth
            val weeksInMonth = daysInMonth.toDouble() / 7.0
            totalWorkouts.toDouble() / weeksInMonth
        }
    }
    
    /**
     * Calculates trend direction with enhanced logic
     * Requirement 2.3: Show the difference as a positive or negative indicator
     * Requirement 2.4: Display a positive trend indicator if current month exceeds previous month
     */
    private fun calculateTrendDirection(currentMonthWorkouts: Int, previousMonthWorkouts: Int): TrendDirection {
        return when {
            // Clear improvement: current month has more workouts
            currentMonthWorkouts > previousMonthWorkouts -> TrendDirection.UP
            
            // Clear decline: current month has fewer workouts
            currentMonthWorkouts < previousMonthWorkouts -> TrendDirection.DOWN
            
            // Same number of workouts
            currentMonthWorkouts == previousMonthWorkouts -> {
                // If both are 0, consider stable rather than positive
                if (currentMonthWorkouts == 0) TrendDirection.STABLE else TrendDirection.STABLE
            }
            
            else -> TrendDirection.STABLE
        }
    }
    
    /**
     * Calculates percentage change with improved precision and edge case handling
     */
    private fun calculatePercentageChange(currentMonthWorkouts: Int, previousMonthWorkouts: Int): Double {
        return when {
            // Previous month had workouts - calculate normal percentage
            previousMonthWorkouts > 0 -> {
                val change = currentMonthWorkouts - previousMonthWorkouts
                (change.toDouble() / previousMonthWorkouts) * 100.0
            }
            
            // Previous month had no workouts but current month does - 100% improvement
            previousMonthWorkouts == 0 && currentMonthWorkouts > 0 -> 100.0
            
            // Both months have no workouts - no change
            previousMonthWorkouts == 0 && currentMonthWorkouts == 0 -> 0.0
            
            // Fallback case
            else -> 0.0
        }
    }
}