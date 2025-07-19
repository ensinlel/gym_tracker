package com.example.gym_tracker.core.data.usecase

import com.example.gym_tracker.core.data.model.ExerciseImprovement
import com.example.gym_tracker.core.data.model.TrendDirection
import com.example.gym_tracker.core.data.model.VolumeProgress
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for calculating volume progress information including volume trend analysis,
 * most improved exercise identification, and percentage improvement calculations.
 * 
 * This use case encapsulates the business logic for:
 * - Displaying volume trend showing if total weight lifted is trending up or down (Requirement 3.1)
 * - Identifying and displaying the most improved exercise from the current month (Requirement 3.2)
 * - Comparing current month's total volume to previous month (Requirement 3.3)
 * - Calculating percentage improvement in weight or reps (Requirement 3.4)
 */
@Singleton
class GetVolumeProgressUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    
    /**
     * Executes the use case to get comprehensive volume progress information
     * 
     * @return VolumeProgress containing volume trends, percentage changes, and most improved exercise
     */
    suspend operator fun invoke(): VolumeProgress {
        val baseVolumeProgress = analyticsRepository.getVolumeProgress()
        
        // Enhanced volume progress with improved logic for trend analysis and exercise improvement
        val enhancedVolumeProgress = enhanceVolumeProgress(baseVolumeProgress)
        
        return enhancedVolumeProgress
    }
    
    /**
     * Enhances the volume progress data with improved logic for trend direction
     * and more sophisticated most improved exercise identification
     */
    private suspend fun enhanceVolumeProgress(volumeProgress: VolumeProgress): VolumeProgress {
        // Calculate enhanced trend direction with more nuanced thresholds
        val enhancedTrendDirection = calculateVolumeTrendDirection(
            volumeProgress.totalVolumeThisMonth,
            volumeProgress.totalVolumePreviousMonth
        )
        
        // Calculate more precise percentage change
        val enhancedPercentageChange = calculateVolumePercentageChange(
            volumeProgress.totalVolumeThisMonth,
            volumeProgress.totalVolumePreviousMonth
        )
        
        // Get enhanced most improved exercise with better logic
        val enhancedMostImprovedExercise = getEnhancedMostImprovedExercise()
        
        return volumeProgress.copy(
            trendDirection = enhancedTrendDirection,
            percentageChange = enhancedPercentageChange,
            mostImprovedExercise = enhancedMostImprovedExercise
        )
    }
    
    /**
     * Calculates volume trend direction with enhanced logic
     * Requirement 3.1: Display volume trend showing if total weight lifted is trending up or down
     * Requirement 3.3: Compare current month's total volume to previous month
     */
    private fun calculateVolumeTrendDirection(
        currentMonthVolume: Double,
        previousMonthVolume: Double
    ): TrendDirection {
        return when {
            // Handle case where previous month had no volume
            previousMonthVolume == 0.0 -> {
                if (currentMonthVolume > 0.0) TrendDirection.UP else TrendDirection.STABLE
            }
            
            // Calculate percentage change for trend determination
            else -> {
                val percentageChange = ((currentMonthVolume - previousMonthVolume) / previousMonthVolume) * 100
                
                when {
                    // Significant improvement (>10% increase)
                    percentageChange > 10.0 -> TrendDirection.UP
                    
                    // Significant decline (>10% decrease)
                    percentageChange < -10.0 -> TrendDirection.DOWN
                    
                    // Moderate improvement (>2% increase)
                    percentageChange > 2.0 -> TrendDirection.UP
                    
                    // Moderate decline (>2% decrease)
                    percentageChange < -2.0 -> TrendDirection.DOWN
                    
                    // Stable range (-2% to +2%)
                    else -> TrendDirection.STABLE
                }
            }
        }
    }
    
    /**
     * Calculates volume percentage change with improved precision and edge case handling
     * Requirement 3.3: Compare current month's total volume to previous month
     */
    private fun calculateVolumePercentageChange(
        currentMonthVolume: Double,
        previousMonthVolume: Double
    ): Double {
        return when {
            // Previous month had volume - calculate normal percentage
            previousMonthVolume > 0.0 -> {
                val change = currentMonthVolume - previousMonthVolume
                (change / previousMonthVolume) * 100.0
            }
            
            // Previous month had no volume but current month does - 100% improvement
            previousMonthVolume == 0.0 && currentMonthVolume > 0.0 -> 100.0
            
            // Both months have no volume - no change
            previousMonthVolume == 0.0 && currentMonthVolume == 0.0 -> 0.0
            
            // Fallback case
            else -> 0.0
        }
    }
    
    /**
     * Gets enhanced most improved exercise with better identification logic
     * Requirement 3.2: Identify and display the most improved exercise from the current month
     * Requirement 3.4: Calculate percentage improvement in weight or reps
     */
    private suspend fun getEnhancedMostImprovedExercise(): ExerciseImprovement? {
        // Get the base most improved exercise from repository
        val baseMostImproved = analyticsRepository.getMostImprovedExercise()
        
        // If no improvement found, return null
        if (baseMostImproved == null) {
            return null
        }
        
        // Enhance the exercise improvement with better calculations
        return enhanceExerciseImprovement(baseMostImproved)
    }
    
    /**
     * Enhances exercise improvement data with more accurate calculations
     * and better handling of edge cases
     */
    private fun enhanceExerciseImprovement(improvement: ExerciseImprovement): ExerciseImprovement {
        // Recalculate improvement percentage with better precision
        val enhancedPercentage = if (improvement.previousBestWeight > 0.0) {
            val weightIncrease = improvement.currentBestWeight - improvement.previousBestWeight
            (weightIncrease / improvement.previousBestWeight) * 100.0
        } else {
            // If no previous weight, consider it 100% improvement
            100.0
        }
        
        return improvement.copy(
            improvementPercentage = enhancedPercentage
        )
    }
    
    /**
     * Handles cases with insufficient data by providing meaningful fallback values
     * This method ensures the use case gracefully handles scenarios where:
     * - User has no workout history
     * - User has insufficient data for trend analysis
     * - No exercises show improvement
     */
    private fun handleInsufficientData(): VolumeProgress {
        return VolumeProgress(
            totalVolumeThisMonth = 0.0,
            totalVolumePreviousMonth = 0.0,
            trendDirection = TrendDirection.STABLE,
            percentageChange = 0.0,
            mostImprovedExercise = null
        )
    }
}