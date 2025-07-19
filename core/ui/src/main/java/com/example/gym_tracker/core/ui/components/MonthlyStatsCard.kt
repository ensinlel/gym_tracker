package com.example.gym_tracker.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gym_tracker.core.ui.theme.*
import java.time.Month
import java.time.format.TextStyle
import java.util.*

/**
 * Monthly stats card component for displaying workout progress compared to previous months
 * 
 * This component displays:
 * - Total workouts completed this month (Requirement 2.1)
 * - Total workouts from previous month for comparison (Requirement 2.2)
 * - Difference as positive/negative indicator (Requirement 2.3)
 * - Positive trend indicator when current month exceeds previous (Requirement 2.4)
 * - Average workouts per week for current month (Requirement 2.5)
 * - Projected average for incomplete months (Requirement 2.6)
 */
@Composable
fun MonthlyStatsCard(
    currentMonthWorkouts: Int,
    previousMonthWorkouts: Int,
    weeklyAverageCurrentMonth: Double,
    weeklyAveragePreviousMonth: Double,
    percentageChange: Double,
    currentMonth: Month = Month.of(java.time.LocalDate.now().monthValue),
    previousMonth: Month = Month.of(if (currentMonth.value == 1) 12 else currentMonth.value - 1),
    isLoading: Boolean = false,
    onViewDetailsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val trendDirection = when {
        percentageChange > 0 -> TrendDirection.UP
        percentageChange < 0 -> TrendDirection.DOWN
        else -> TrendDirection.STABLE
    }
    
    val trendColor = when (trendDirection) {
        TrendDirection.UP -> AccentGreen
        TrendDirection.DOWN -> ErrorRed
        TrendDirection.STABLE -> AccentBlue
    }
    
    val trendIcon = when (trendDirection) {
        TrendDirection.UP -> "↑"
        TrendDirection.DOWN -> "↓"
        TrendDirection.STABLE -> "→"
    }
    
    val formattedPercentage = String.format("%.1f%%", percentageChange.coerceIn(-999.0, 999.0))
    val changeText = when {
        percentageChange > 0 -> "+$formattedPercentage"
        percentageChange < 0 -> formattedPercentage
        else -> "0%"
    }
    
    val currentMonthName = currentMonth.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val previousMonthName = previousMonth.getDisplayName(TextStyle.FULL, Locale.getDefault())
    
    DashboardCard(
        title = "Monthly Progress",
        content = {
            if (isLoading) {
                // Loading state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentPurple)
                }
            } else {
                // Content state
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Monthly comparison row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Current month stats
                        MonthlyStatItem(
                            title = currentMonthName,
                            workoutCount = currentMonthWorkouts,
                            weeklyAverage = weeklyAverageCurrentMonth,
                            color = AccentPurple,
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Trend indicator
                        TrendIndicator(
                            trendIcon = trendIcon,
                            changeText = changeText,
                            color = trendColor,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        
                        // Previous month stats
                        MonthlyStatItem(
                            title = previousMonthName,
                            workoutCount = previousMonthWorkouts,
                            weeklyAverage = weeklyAveragePreviousMonth,
                            color = AccentBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    // View details button
                    TextButton(
                        onClick = onViewDetailsClick,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "View Details",
                            style = MaterialTheme.typography.labelMedium,
                            color = AccentPurple
                        )
                    }
                }
            }
        },
        onClick = null,
        modifier = modifier
    )
}

/**
 * Monthly stat item component for displaying stats for a specific month
 */
@Composable
private fun MonthlyStatItem(
    title: String,
    workoutCount: Int,
    weeklyAverage: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        // Month name
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        
        // Workout count
        Text(
            text = workoutCount.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = color,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        // Weekly average
        Text(
            text = String.format("%.1f/week", weeklyAverage),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Trend indicator component for displaying the trend between months
 */
@Composable
private fun TrendIndicator(
    trendIcon: String,
    changeText: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        // Trend icon
        Text(
            text = trendIcon,
            style = MaterialTheme.typography.headlineMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
        
        // Change percentage
        Text(
            text = changeText,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Enum representing the direction of a trend
 */
private enum class TrendDirection {
    UP, DOWN, STABLE
}

@Preview(showBackground = true)
@Composable
private fun MonthlyStatsCardPreview() {
    GymTrackerTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Improvement example
            MonthlyStatsCard(
                currentMonthWorkouts = 12,
                previousMonthWorkouts = 8,
                weeklyAverageCurrentMonth = 3.0,
                weeklyAveragePreviousMonth = 2.0,
                percentageChange = 50.0
            )
            
            // Decline example
            MonthlyStatsCard(
                currentMonthWorkouts = 6,
                previousMonthWorkouts = 10,
                weeklyAverageCurrentMonth = 1.5,
                weeklyAveragePreviousMonth = 2.5,
                percentageChange = -40.0
            )
            
            // Stable example
            MonthlyStatsCard(
                currentMonthWorkouts = 8,
                previousMonthWorkouts = 8,
                weeklyAverageCurrentMonth = 2.0,
                weeklyAveragePreviousMonth = 2.0,
                percentageChange = 0.0
            )
            
            // Loading state example
            MonthlyStatsCard(
                currentMonthWorkouts = 0,
                previousMonthWorkouts = 0,
                weeklyAverageCurrentMonth = 0.0,
                weeklyAveragePreviousMonth = 0.0,
                percentageChange = 0.0,
                isLoading = true
            )
        }
    }
}