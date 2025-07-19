package com.example.gym_tracker.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
 * Streak card component for displaying workout consistency metrics
 * 
 * This component displays:
 * - Current streak in days or weeks (Requirement 1.1)
 * - Longest streak ever achieved (Requirement 1.2)
 * - Days since last workout (Requirement 1.3)
 * - Encouraging message for inactive periods (Requirement 1.4)
 */
@Composable
fun StreakCard(
    currentStreak: Int,
    longestStreak: Int,
    daysSinceLastWorkout: Int,
    streakType: String = "DAYS",
    encouragingMessage: String? = null,
    isLoading: Boolean = false,
    onViewHistoryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    DashboardCard(
        title = "Workout Streak",
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
                    // Streak metrics row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Current streak
                        StreakMetric(
                            value = currentStreak,
                            label = "Current Streak",
                            unit = streakType.lowercase(),
                            color = AccentPurple,
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Longest streak
                        StreakMetric(
                            value = longestStreak,
                            label = "Longest Streak",
                            unit = streakType.lowercase(),
                            color = AccentBlue,
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Days since last workout
                        StreakMetric(
                            value = daysSinceLastWorkout,
                            label = "Days Since",
                            unit = "last workout",
                            color = if (daysSinceLastWorkout > 3) WarningYellow else AccentGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    // Encouraging message
                    encouragingMessage?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        )
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
 * Streak metric component for displaying individual streak statistics
 */
@Composable
private fun StreakMetric(
    value: Int,
    label: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        // Circular background for value
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        
        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        // Unit
        Text(
            text = unit,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StreakCardPreview() {
    GymTrackerTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active streak example
            StreakCard(
                currentStreak = 5,
                longestStreak = 14,
                daysSinceLastWorkout = 0,
                streakType = "DAYS",
                encouragingMessage = "Great job working out today! 🎯"
            )
            
            // Inactive streak example
            StreakCard(
                currentStreak = 0,
                longestStreak = 14,
                daysSinceLastWorkout = 5,
                streakType = "DAYS",
                encouragingMessage = "You've got this! Your longest streak was 14 days. Time to start a new one! 🚀"
            )
            
            // Loading state example
            StreakCard(
                currentStreak = 0,
                longestStreak = 0,
                daysSinceLastWorkout = 0,
                isLoading = true
            )
        }
    }
}