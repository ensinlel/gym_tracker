package com.example.gym_tracker.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gym_tracker.core.ui.theme.*

/**
 * Volume progress card component for displaying volume trends and exercise improvements
 * 
 * This component displays:
 * - Volume trend showing if total weight lifted is trending up or down (Requirement 3.1)
 * - Most improved exercise from the current month (Requirement 3.2)
 * - Comparison of current month's total volume to previous month (Requirement 3.3)
 * - Percentage improvement in weight or reps (Requirement 3.4)
 */
@Composable
fun VolumeProgressCard(
    totalVolumeThisMonth: Double,
    totalVolumePreviousMonth: Double,
    percentageChange: Double,
    trendDirection: String = "STABLE",
    mostImprovedExercise: MostImprovedExercise? = null,
    isLoading: Boolean = false,
    onViewDetailsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val trendColor = when (trendDirection) {
        "UP" -> AccentGreen
        "DOWN" -> ErrorRed
        else -> AccentBlue
    }
    
    val trendIcon = when (trendDirection) {
        "UP" -> "↑"
        "DOWN" -> "↓"
        else -> "→"
    }
    
    val formattedPercentage = String.format("%.1f%%", percentageChange.coerceIn(-999.0, 999.0))
    val changeText = when {
        percentageChange > 0 -> "+$formattedPercentage"
        percentageChange < 0 -> formattedPercentage
        else -> "0%"
    }
    
    val formattedVolumeThisMonth = formatWeight(totalVolumeThisMonth)
    val formattedVolumePreviousMonth = formatWeight(totalVolumePreviousMonth)
    
    DashboardCard(
        title = "Volume Progress",
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
                    // Volume comparison section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // This month volume
                        VolumeStatItem(
                            title = "This Month",
                            volume = formattedVolumeThisMonth,
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
                        
                        // Previous month volume
                        VolumeStatItem(
                            title = "Previous Month",
                            volume = formattedVolumePreviousMonth,
                            color = AccentBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    // Most improved exercise section
                    mostImprovedExercise?.let {
                        MostImprovedExerciseItem(
                            exerciseName = it.exerciseName,
                            previousWeight = it.previousWeight,
                            currentWeight = it.currentWeight,
                            improvementPercentage = it.improvementPercentage
                        )
                    } ?: run {
                        // No improvement data
                        Text(
                            text = "No exercise improvement data available yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
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
 * Volume stat item component for displaying volume for a specific month
 */
@Composable
private fun VolumeStatItem(
    title: String,
    volume: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        
        // Volume
        Text(
            text = volume,
            style = MaterialTheme.typography.headlineSmall,
            color = color,
            fontWeight = FontWeight.Bold,
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
 * Most improved exercise item component
 */
@Composable
private fun MostImprovedExerciseItem(
    exerciseName: String,
    previousWeight: Double,
    currentWeight: Double,
    improvementPercentage: Double
) {
    val formattedPreviousWeight = formatWeight(previousWeight, false)
    val formattedCurrentWeight = formatWeight(currentWeight, false)
    val formattedImprovement = String.format("+%.1f%%", improvementPercentage)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = AccentGreen.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Title
            Text(
                text = "Most Improved Exercise",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Exercise name and improvement
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exerciseName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = formattedImprovement,
                    style = MaterialTheme.typography.titleMedium,
                    color = AccentGreen,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Weight progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$formattedPreviousWeight → $formattedCurrentWeight",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Formats weight values for display
 */
private fun formatWeight(weight: Double, includeUnit: Boolean = true): String {
    val formattedWeight = when {
        weight >= 1000 -> String.format("%.1f", weight / 1000)
        else -> String.format("%.0f", weight)
    }
    
    return when {
        weight >= 1000 && includeUnit -> "$formattedWeight tons"
        includeUnit -> "$formattedWeight lbs"
        else -> formattedWeight
    }
}

/**
 * Data class representing most improved exercise information
 */
data class MostImprovedExercise(
    val exerciseName: String,
    val previousWeight: Double,
    val currentWeight: Double,
    val improvementPercentage: Double
)

@Preview(showBackground = true)
@Composable
private fun VolumeProgressCardPreview() {
    GymTrackerTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // With improvement example
            VolumeProgressCard(
                totalVolumeThisMonth = 12500.0,
                totalVolumePreviousMonth = 10000.0,
                percentageChange = 25.0,
                trendDirection = "UP",
                mostImprovedExercise = MostImprovedExercise(
                    exerciseName = "Bench Press",
                    previousWeight = 185.0,
                    currentWeight = 225.0,
                    improvementPercentage = 21.6
                )
            )
            
            // Decline example
            VolumeProgressCard(
                totalVolumeThisMonth = 8000.0,
                totalVolumePreviousMonth = 10000.0,
                percentageChange = -20.0,
                trendDirection = "DOWN",
                mostImprovedExercise = null
            )
            
            // Loading state example
            VolumeProgressCard(
                totalVolumeThisMonth = 0.0,
                totalVolumePreviousMonth = 0.0,
                percentageChange = 0.0,
                isLoading = true
            )
        }
    }
}