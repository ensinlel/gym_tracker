package com.example.gym_tracker.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

/**
 * Weight progress card component for displaying body weight trends
 * 
 * This component displays:
 * - Weight trend with simple up/down arrow (Requirement 5.1)
 * - Comparison to weight from 30 days ago (Requirement 5.2)
 * - Prompt to start tracking weight if no data exists (Requirement 5.3)
 * - Stable indicator when weight is within 1 kg threshold (Requirement 5.4)
 */
@Composable
fun WeightProgressCard(
    currentWeight: Double? = null,
    weightThirtyDaysAgo: Double? = null,
    weightChange: Double? = null,
    weightChangeFormatted: String? = null,
    weightChangeDescription: String? = null,
    trendDirection: String = "STABLE",
    shouldPromptWeightTracking: Boolean = false,
    shouldPromptForRecentData: Boolean = false,
    isLoading: Boolean = false,
    onAddWeightClick: () -> Unit = {},
    onViewHistoryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val trendColor = when (trendDirection) {
        "UP" -> WarningYellow // Weight gain is shown in yellow (not necessarily positive)
        "DOWN" -> AccentGreen // Weight loss is shown in green
        else -> AccentBlue // Stable weight is shown in blue
    }
    
    val trendIcon = when (trendDirection) {
        "UP" -> "↑"
        "DOWN" -> "↓"
        else -> "→"
    }
    
    DashboardCard(
        title = "Weight Progress",
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
            } else if (shouldPromptWeightTracking) {
                // Prompt to start tracking weight
                EmptyWeightContent(
                    onAddWeightClick = onAddWeightClick
                )
            } else {
                // Content state
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Weight comparison section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Current weight
                        WeightStatItem(
                            title = "Current",
                            weight = currentWeight?.let { String.format("%.1f kg", it) } ?: "--",
                            color = AccentPurple,
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Trend indicator
                        if (weightChange != null) {
                            TrendIndicator(
                                trendIcon = trendIcon,
                                changeText = weightChangeFormatted ?: "",
                                description = weightChangeDescription,
                                color = trendColor,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                        
                        // 30 days ago weight
                        WeightStatItem(
                            title = "30 Days Ago",
                            weight = weightThirtyDaysAgo?.let { String.format("%.1f kg", it) } ?: "--",
                            color = AccentBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    // Prompt for recent data if needed
                    if (shouldPromptForRecentData) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = WarningYellow.copy(alpha = 0.1f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Your weight data is not recent",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                TextButton(
                                    onClick = onAddWeightClick,
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = WarningYellow
                                    )
                                ) {
                                    Text("Update")
                                }
                            }
                        }
                    }
                    
                    // View history button
                    TextButton(
                        onClick = onViewHistoryClick,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "View History",
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
 * Empty weight content component
 */
@Composable
private fun EmptyWeightContent(
    onAddWeightClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Message
        Text(
            text = "Start tracking your weight to see progress",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        // Add weight button
        Button(
            onClick = onAddWeightClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentPurple
            ),
            shape = GymTrackerShapes.Button
        ) {
            Text("Add Weight")
        }
    }
}

/**
 * Weight stat item component
 */
@Composable
private fun WeightStatItem(
    title: String,
    weight: String,
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
        
        // Weight
        Text(
            text = weight,
            style = MaterialTheme.typography.headlineSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Trend indicator component for displaying the trend between weights
 */
@Composable
private fun TrendIndicator(
    trendIcon: String,
    changeText: String,
    description: String? = null,
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
        
        // Change text
        Text(
            text = changeText,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
        
        // Description
        description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeightProgressCardPreview() {
    GymTrackerTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Weight loss example
            WeightProgressCard(
                currentWeight = 180.5,
                weightThirtyDaysAgo = 185.0,
                weightChange = -4.5,
                weightChangeFormatted = "-4.5 lbs",
                weightChangeDescription = "Weight decreased",
                trendDirection = "DOWN"
            )
            
            // Weight gain example
            WeightProgressCard(
                currentWeight = 182.5,
                weightThirtyDaysAgo = 180.0,
                weightChange = 2.5,
                weightChangeFormatted = "+2.5 lbs",
                weightChangeDescription = "Weight increased",
                trendDirection = "UP"
            )
            
            // Stable weight example
            WeightProgressCard(
                currentWeight = 180.5,
                weightThirtyDaysAgo = 180.0,
                weightChange = 0.5,
                weightChangeFormatted = "+0.5 lbs",
                weightChangeDescription = "Weight stable (±2 lbs)",
                trendDirection = "STABLE"
            )
            
            // Prompt for recent data example
            WeightProgressCard(
                currentWeight = 180.5,
                weightThirtyDaysAgo = 182.0,
                weightChange = -1.5,
                weightChangeFormatted = "-1.5 lbs",
                weightChangeDescription = "Weight stable (±2 lbs)",
                trendDirection = "STABLE",
                shouldPromptForRecentData = true
            )
            
            // Empty state example
            WeightProgressCard(
                shouldPromptWeightTracking = true
            )
            
            // Loading state example
            WeightProgressCard(
                isLoading = true
            )
        }
    }
}