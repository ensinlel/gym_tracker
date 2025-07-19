package com.example.gym_tracker.core.data.usecase

import com.example.gym_tracker.core.data.model.TrendDirection
import com.example.gym_tracker.core.data.model.WeightProgress
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * Use case for calculating weight progress information including trend analysis,
 * 30-day comparisons, and data recency checks.
 * 
 * This use case encapsulates the business logic for:
 * - Displaying weight trend with simple up/down arrow (Requirement 5.1)
 * - Comparing current weight to weight from 30 days ago (Requirement 5.2)
 * - Handling missing weight data with prompts (Requirement 5.3)
 * - Detecting stable weight within 1 kg threshold (Requirement 5.4)
 */
@Singleton
class GetWeightProgressUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    
    /**
     * Executes the use case to get comprehensive weight progress information
     * 
     * @param userProfileId The ID of the user profile to get weight progress for
     * @return WeightProgressResult containing weight data, trend information, and UI guidance
     */
    suspend operator fun invoke(userProfileId: String): WeightProgressResult {
        val baseWeightProgress = analyticsRepository.getWeightProgress(userProfileId)
        
        // Enhanced weight progress with improved trend analysis and UI guidance
        return enhanceWeightProgress(baseWeightProgress, userProfileId)
    }
    
    /**
     * Enhances the weight progress data with improved trend analysis and UI guidance
     */
    private suspend fun enhanceWeightProgress(
        weightProgress: WeightProgress,
        userProfileId: String
    ): WeightProgressResult {
        // Check if we have any weight data
        val hasWeightData = weightProgress.currentWeight != null
        
        // If no weight data, return result with prompt flag
        if (!hasWeightData) {
            return WeightProgressResult(
                weightProgress = weightProgress,
                shouldPromptWeightTracking = true,
                weightChangeFormatted = null,
                weightChangeDescription = null
            )
        }
        
        // Calculate enhanced trend direction with more nuanced thresholds
        val enhancedTrendDirection = calculateWeightTrendDirection(
            weightProgress.weightChange,
            weightProgress.isStable
        )
        
        // Format weight change for display
        val weightChangeFormatted = formatWeightChange(weightProgress.weightChange)
        
        // Generate descriptive text for weight change
        val weightChangeDescription = generateWeightChangeDescription(
            weightProgress.weightChange,
            enhancedTrendDirection,
            weightProgress.isStable
        )
        
        // Check if we need to prompt for more recent data
        val shouldPromptForRecentData = !weightProgress.hasRecentData
        
        return WeightProgressResult(
            weightProgress = weightProgress.copy(trendDirection = enhancedTrendDirection),
            shouldPromptWeightTracking = false,
            weightChangeFormatted = weightChangeFormatted,
            weightChangeDescription = weightChangeDescription,
            shouldPromptForRecentData = shouldPromptForRecentData
        )
    }
    
    /**
     * Calculates weight trend direction with enhanced logic
     * Requirement 5.1: Display weight trend with simple up/down arrow
     * Requirement 5.4: When weight trend is stable (within 1 kg) display a stable indicator
     */
    private fun calculateWeightTrendDirection(
        weightChange: Double?,
        isStable: Boolean
    ): TrendDirection {
        return when {
            // No weight change data available
            weightChange == null -> TrendDirection.STABLE
            
            // Stable weight (within 1 kg threshold)
            isStable -> TrendDirection.STABLE
            
            // Weight gain (more than 1 kg)
            weightChange > 0 -> TrendDirection.UP
            
            // Weight loss (more than 1 kg)
            else -> TrendDirection.DOWN
        }
    }
    
    /**
     * Formats weight change for display
     */
    private fun formatWeightChange(weightChange: Double?): String? {
        if (weightChange == null) return null
        
        val prefix = if (weightChange > 0) "+" else ""
        return "$prefix${String.format("%.1f", weightChange)} kg"
    }
    
    /**
     * Generates descriptive text for weight change
     */
    private fun generateWeightChangeDescription(
        weightChange: Double?,
        trendDirection: TrendDirection,
        isStable: Boolean
    ): String? {
        if (weightChange == null) return null
        
        return when {
            isStable -> "Weight stable (±1 kg)"
            trendDirection == TrendDirection.UP -> "Weight increased"
            trendDirection == TrendDirection.DOWN -> "Weight decreased"
            else -> null
        }
    }
    
    /**
     * Data class representing the result of the GetWeightProgressUseCase
     */
    data class WeightProgressResult(
        val weightProgress: WeightProgress,
        val shouldPromptWeightTracking: Boolean,
        val weightChangeFormatted: String?,
        val weightChangeDescription: String?,
        val shouldPromptForRecentData: Boolean = false
    )
}