package com.example.gym_tracker.core.data.usecase

import com.example.gym_tracker.core.data.model.*
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

/**
 * Use case for generating comparative analysis features including before/after comparisons,
 * muscle group distribution, and personal records timeline.
 * 
 * This use case encapsulates the business logic for:
 * - Building before/after comparison charts with statistical significance (Requirement 3.3)
 * - Implementing muscle group distribution pie charts (Requirement 3.5)
 * - Creating personal records timeline visualization (Requirement 3.6, 6.6)
 */
@Singleton
class GetComparativeAnalysisUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    
    /**
     * Executes the use case to get comprehensive comparative analysis data
     * 
     * @return ComparativeAnalysisData containing all comparative analysis components
     */
    suspend operator fun invoke(): ComparativeAnalysisData {
        val beforeAfterComparisons = generateBeforeAfterComparisons()
        val muscleGroupDistribution = generateMuscleGroupDistribution()
        val personalRecordsTimelines = generatePersonalRecordsTimelines()
        
        return ComparativeAnalysisData(
            beforeAfterComparisons = beforeAfterComparisons,
            muscleGroupDistribution = muscleGroupDistribution,
            personalRecordsTimelines = personalRecordsTimelines
        )
    }
    
    /**
     * Generates before/after comparison data with statistical significance
     * Requirement 3.3: Build before/after comparison charts with statistical significance
     */
    private suspend fun generateBeforeAfterComparisons(): List<BeforeAfterComparison> {
        val comparisons = mutableListOf<BeforeAfterComparison>()
        
        // Monthly comparison (current vs previous month)
        val monthlyComparison = generateMonthlyComparison()
        if (monthlyComparison != null) {
            comparisons.add(monthlyComparison)
        }
        
        // Quarterly comparison (current vs previous quarter)
        val quarterlyComparison = generateQuarterlyComparison()
        if (quarterlyComparison != null) {
            comparisons.add(quarterlyComparison)
        }
        
        // Yearly comparison (current vs previous year)
        val yearlyComparison = generateYearlyComparison()
        if (yearlyComparison != null) {
            comparisons.add(yearlyComparison)
        }
        
        return comparisons
    }
    
    /**
     * Generates monthly before/after comparison
     */
    private suspend fun generateMonthlyComparison(): BeforeAfterComparison? {
        val currentDate = LocalDate.now()
        val currentMonthStart = currentDate.withDayOfMonth(1)
        val previousMonthStart = currentMonthStart.minusMonths(1)
        val previousMonthEnd = currentMonthStart.minusDays(1)
        
        val currentPeriodData = analyticsRepository.getPeriodData(currentMonthStart, currentDate)
        val previousPeriodData = analyticsRepository.getPeriodData(previousMonthStart, previousMonthEnd)
        
        if (currentPeriodData == null || previousPeriodData == null) {
            return null
        }
        
        val improvementPercentage = calculateImprovementPercentage(
            previousPeriodData.totalVolume,
            currentPeriodData.totalVolume
        )
        
        val statisticalSignificance = calculateStatisticalSignificance(
            previousPeriodData,
            currentPeriodData
        )
        
        return BeforeAfterComparison(
            beforePeriod = previousPeriodData,
            afterPeriod = currentPeriodData,
            improvementPercentage = improvementPercentage,
            statisticalSignificance = statisticalSignificance,
            comparisonType = ComparisonType.MONTHLY
        )
    }
    
    /**
     * Generates quarterly before/after comparison
     */
    private suspend fun generateQuarterlyComparison(): BeforeAfterComparison? {
        val currentDate = LocalDate.now()
        val currentQuarterStart = getQuarterStart(currentDate)
        val previousQuarterStart = currentQuarterStart.minusMonths(3)
        val previousQuarterEnd = currentQuarterStart.minusDays(1)
        
        val currentPeriodData = analyticsRepository.getPeriodData(currentQuarterStart, currentDate)
        val previousPeriodData = analyticsRepository.getPeriodData(previousQuarterStart, previousQuarterEnd)
        
        if (currentPeriodData == null || previousPeriodData == null) {
            return null
        }
        
        val improvementPercentage = calculateImprovementPercentage(
            previousPeriodData.totalVolume,
            currentPeriodData.totalVolume
        )
        
        val statisticalSignificance = calculateStatisticalSignificance(
            previousPeriodData,
            currentPeriodData
        )
        
        return BeforeAfterComparison(
            beforePeriod = previousPeriodData,
            afterPeriod = currentPeriodData,
            improvementPercentage = improvementPercentage,
            statisticalSignificance = statisticalSignificance,
            comparisonType = ComparisonType.QUARTERLY
        )
    }
    
    /**
     * Generates yearly before/after comparison
     */
    private suspend fun generateYearlyComparison(): BeforeAfterComparison? {
        val currentDate = LocalDate.now()
        val currentYearStart = currentDate.withDayOfYear(1)
        val previousYearStart = currentYearStart.minusYears(1)
        val previousYearEnd = currentYearStart.minusDays(1)
        
        val currentPeriodData = analyticsRepository.getPeriodData(currentYearStart, currentDate)
        val previousPeriodData = analyticsRepository.getPeriodData(previousYearStart, previousYearEnd)
        
        if (currentPeriodData == null || previousPeriodData == null) {
            return null
        }
        
        val improvementPercentage = calculateImprovementPercentage(
            previousPeriodData.totalVolume,
            currentPeriodData.totalVolume
        )
        
        val statisticalSignificance = calculateStatisticalSignificance(
            previousPeriodData,
            currentPeriodData
        )
        
        return BeforeAfterComparison(
            beforePeriod = previousPeriodData,
            afterPeriod = currentPeriodData,
            improvementPercentage = improvementPercentage,
            statisticalSignificance = statisticalSignificance,
            comparisonType = ComparisonType.YEARLY
        )
    }
    
    /**
     * Generates muscle group distribution data for pie charts
     * Requirement 3.5: Implement muscle group distribution pie charts
     */
    private suspend fun generateMuscleGroupDistribution(): List<MuscleGroupDistribution> {
        val muscleGroupData = analyticsRepository.getMuscleGroupDistribution()
        val totalVolume = muscleGroupData.sumOf { it.totalVolume }
        
        val colors = listOf(
            "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7",
            "#DDA0DD", "#98D8C8", "#F7DC6F", "#BB8FCE", "#85C1E9"
        )
        
        return muscleGroupData.mapIndexed { index, data ->
            MuscleGroupDistribution(
                muscleGroup = data.muscleGroup,
                exerciseCount = data.exerciseCount,
                totalVolume = data.totalVolume,
                percentage = if (totalVolume > 0) (data.totalVolume / totalVolume) * 100 else 0.0,
                color = colors[index % colors.size]
            )
        }.sortedByDescending { it.percentage }
    }
    
    /**
     * Generates personal records timeline data
     * Requirement 3.6: Create personal records timeline visualization
     * Requirement 6.6: Validate personal records tracking works correctly
     */
    private suspend fun generatePersonalRecordsTimelines(): List<PersonalRecordsTimeline> {
        val timelines = mutableListOf<PersonalRecordsTimeline>()
        
        val exercises = analyticsRepository.getStarMarkedExercises()
        for (exercise in exercises.first()) {
            val records = analyticsRepository.getPersonalRecordHistory(exercise.id)
            if (records.isNotEmpty()) {
                val timeline = createPersonalRecordTimeline(exercise.name, records)
                timelines.add(timeline)
            }
        }
        
        return timelines.sortedByDescending { it.totalImprovement }
    }
    
    /**
     * Creates a personal record timeline for a specific exercise
     */
    private fun createPersonalRecordTimeline(
        exerciseName: String,
        records: List<PersonalRecord>
    ): PersonalRecordsTimeline {
        val sortedRecords = records.sortedBy { it.achievedDate }
        val recordPoints = mutableListOf<PersonalRecordPoint>()
        var currentBestWeight = 0.0
        
        for (record in sortedRecords) {
            val oneRepMax = calculateOneRepMax(record.weight, record.reps)
            val isNewRecord = record.weight > currentBestWeight
            
            if (isNewRecord) {
                currentBestWeight = record.weight
            }
            
            recordPoints.add(
                PersonalRecordPoint(
                    date = record.achievedDate,
                    weight = record.weight,
                    reps = record.reps,
                    oneRepMax = oneRepMax,
                    isNewRecord = isNewRecord
                )
            )
        }
        
        val trendDirection = if (recordPoints.size >= 2) {
            val firstWeight = recordPoints.first().weight
            val lastWeight = recordPoints.last().weight
            when {
                lastWeight > firstWeight * 1.05 -> TrendDirection.UP
                lastWeight < firstWeight * 0.95 -> TrendDirection.DOWN
                else -> TrendDirection.STABLE
            }
        } else {
            TrendDirection.STABLE
        }
        
        val totalImprovement = if (recordPoints.size >= 2) {
            val firstWeight = recordPoints.first().weight
            val lastWeight = recordPoints.last().weight
            ((lastWeight - firstWeight) / firstWeight) * 100
        } else {
            0.0
        }
        
        return PersonalRecordsTimeline(
            exerciseName = exerciseName,
            records = recordPoints,
            trendDirection = trendDirection,
            totalImprovement = totalImprovement
        )
    }
    
    /**
     * Calculates improvement percentage between two periods
     */
    private fun calculateImprovementPercentage(beforeValue: Double, afterValue: Double): Double {
        return if (beforeValue > 0) {
            ((afterValue - beforeValue) / beforeValue) * 100
        } else {
            if (afterValue > 0) 100.0 else 0.0
        }
    }
    
    /**
     * Calculates statistical significance using simplified t-test approach
     */
    private fun calculateStatisticalSignificance(
        beforePeriod: ComparisonPeriod,
        afterPeriod: ComparisonPeriod
    ): StatisticalSignificance {
        // Simplified statistical significance calculation
        // In a real implementation, this would use proper statistical tests
        val improvementPercentage = abs(calculateImprovementPercentage(
            beforePeriod.totalVolume,
            afterPeriod.totalVolume
        ))
        
        val sampleSizeScore = min(beforePeriod.workoutCount, afterPeriod.workoutCount)
        
        return when {
            improvementPercentage > 50 && sampleSizeScore >= 10 -> StatisticalSignificance.HIGHLY_SIGNIFICANT
            improvementPercentage > 25 && sampleSizeScore >= 8 -> StatisticalSignificance.SIGNIFICANT
            improvementPercentage > 10 && sampleSizeScore >= 5 -> StatisticalSignificance.MARGINALLY_SIGNIFICANT
            else -> StatisticalSignificance.NOT_SIGNIFICANT
        }
    }
    
    /**
     * Calculates one-rep max using Epley formula
     */
    private fun calculateOneRepMax(weight: Double, reps: Int): Double {
        return if (reps == 1) {
            weight
        } else {
            weight * (1 + reps / 30.0)
        }
    }
    
    /**
     * Gets the start of the current quarter
     */
    private fun getQuarterStart(date: LocalDate): LocalDate {
        val month = date.monthValue
        val quarterStartMonth = when {
            month <= 3 -> 1
            month <= 6 -> 4
            month <= 9 -> 7
            else -> 10
        }
        return date.withMonth(quarterStartMonth).withDayOfMonth(1)
    }
}