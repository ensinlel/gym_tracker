package com.example.gym_tracker.core.data.model

import com.example.gym_tracker.core.common.enums.MuscleGroup
import java.time.LocalDate

/**
 * Data class representing before/after comparison data with statistical significance
 */
data class BeforeAfterComparison(
    val beforePeriod: ComparisonPeriod,
    val afterPeriod: ComparisonPeriod,
    val improvementPercentage: Double,
    val statisticalSignificance: StatisticalSignificance,
    val comparisonType: ComparisonType
)

/**
 * Data class representing a comparison period
 */
data class ComparisonPeriod(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalVolume: Double,
    val averageWeight: Double,
    val workoutCount: Int,
    val exerciseCount: Int
)

/**
 * Enum representing statistical significance levels
 */
enum class StatisticalSignificance {
    HIGHLY_SIGNIFICANT,    // p < 0.01
    SIGNIFICANT,           // p < 0.05
    MARGINALLY_SIGNIFICANT, // p < 0.1
    NOT_SIGNIFICANT        // p >= 0.1
}

/**
 * Enum representing types of comparisons
 */
enum class ComparisonType {
    MONTHLY,
    QUARTERLY,
    YEARLY,
    CUSTOM
}

/**
 * Data class representing muscle group distribution data
 */
data class MuscleGroupDistribution(
    val muscleGroup: MuscleGroup,
    val exerciseCount: Int,
    val totalVolume: Double,
    val percentage: Double,
    val color: String // Hex color for pie chart
)

/**
 * Extension function to get display name for muscle groups
 */
fun MuscleGroup.displayName(): String = when (this) {
    MuscleGroup.CHEST -> "Chest"
    MuscleGroup.UPPER_BACK -> "Upper Back"
    MuscleGroup.LOWER_BACK -> "Lower Back"
    MuscleGroup.FRONT_DELTS -> "Front Delts"
    MuscleGroup.SIDE_DELTS -> "Side Delts"
    MuscleGroup.REAR_DELTS -> "Rear Delts"
    MuscleGroup.BICEPS -> "Biceps"
    MuscleGroup.TRICEPS -> "Triceps"
    MuscleGroup.FOREARMS -> "Forearms"
    MuscleGroup.QUADRICEPS -> "Quadriceps"
    MuscleGroup.HAMSTRINGS -> "Hamstrings"
    MuscleGroup.GLUTES -> "Glutes"
    MuscleGroup.CALVES -> "Calves"
    MuscleGroup.ABS -> "Abs"
    MuscleGroup.OBLIQUES -> "Obliques"
    MuscleGroup.LOWER_BACK_MUSCLES -> "Lower Back"
}

/**
 * Data class representing personal records timeline data
 */
data class PersonalRecordsTimeline(
    val exerciseName: String,
    val records: List<PersonalRecordPoint>,
    val trendDirection: TrendDirection,
    val totalImprovement: Double // percentage improvement from first to latest
)

/**
 * Data class representing a single point in the PR timeline
 */
data class PersonalRecordPoint(
    val date: LocalDate,
    val weight: Double,
    val reps: Int,
    val oneRepMax: Double, // calculated 1RM
    val isNewRecord: Boolean
)

/**
 * Data class representing comparative analysis results
 */
data class ComparativeAnalysisData(
    val beforeAfterComparisons: List<BeforeAfterComparison>,
    val muscleGroupDistribution: List<MuscleGroupDistribution>,
    val personalRecordsTimelines: List<PersonalRecordsTimeline>
)