package com.example.gym_tracker.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Personal records card component for displaying PR values for star-marked exercises
 * 
 * This component displays:
 * - PR values for star-marked exercises (Requirement 4.1)
 * - Only the weight value for simplicity (Requirement 4.2)
 * - Star-marked exercises in the dashboard (Requirement 4.3)
 * - Suggestions when no exercises are star-marked (Requirement 4.4)
 */
@Composable
fun PersonalRecordsCard(
    personalRecords: List<PersonalRecord> = emptyList(),
    shouldSuggestStarMarking: Boolean = false,
    suggestedExercises: List<String> = emptyList(),
    isLoading: Boolean = false,
    onStarExercise: () -> Unit = {},
    onViewAllClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    DashboardCard(
        title = "Personal Records",
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
            } else if (personalRecords.isEmpty()) {
                // Empty state
                EmptyPersonalRecordsContent(
                    shouldSuggestStarMarking = shouldSuggestStarMarking,
                    suggestedExercises = suggestedExercises,
                    onStarExercise = onStarExercise
                )
            } else {
                // Content state
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Personal records list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(personalRecords) { record ->
                            PersonalRecordItem(
                                exerciseName = record.exerciseName,
                                weight = record.weight,
                                achievedDate = record.achievedDate,
                                isRecent = record.isRecent
                            )
                        }
                    }
                    
                    // View all button
                    TextButton(
                        onClick = onViewAllClick,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "View All",
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
 * Empty personal records content component
 */
@Composable
private fun EmptyPersonalRecordsContent(
    shouldSuggestStarMarking: Boolean,
    suggestedExercises: List<String>,
    onStarExercise: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (shouldSuggestStarMarking) {
            // Suggestion to star exercises
            Text(
                text = "Star your key exercises to track personal records",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            // Suggested exercises
            if (suggestedExercises.isNotEmpty()) {
                Text(
                    text = "Suggested exercises:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = suggestedExercises.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = AccentPurple,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
            
            // Star exercise button
            Button(
                onClick = onStarExercise,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPurple
                ),
                shape = GymTrackerShapes.Button
            ) {
                Text("⭐ Select Exercises to Track")
            }
        } else {
            // No personal records yet
            Text(
                text = "No personal records yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Complete workouts to see your progress",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Personal record item component
 */
@Composable
private fun PersonalRecordItem(
    exerciseName: String,
    weight: Double,
    achievedDate: LocalDate,
    isRecent: Boolean
) {
    val formattedDate = achievedDate.format(DateTimeFormatter.ofPattern("MMM d"))
    val formattedWeight = String.format("%.0f lbs", weight)
    val backgroundColor = if (isRecent) AccentPurple.copy(alpha = 0.1f) else Color.Transparent
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Exercise name
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = exerciseName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Weight value
            Text(
                text = formattedWeight,
                style = MaterialTheme.typography.titleMedium,
                color = if (isRecent) AccentPurple else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Data class representing a personal record
 */
data class PersonalRecord(
    val exerciseName: String,
    val weight: Double,
    val achievedDate: LocalDate,
    val isRecent: Boolean = false
)

@Preview(showBackground = true)
@Composable
private fun PersonalRecordsCardPreview() {
    GymTrackerTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // With records example
            PersonalRecordsCard(
                personalRecords = listOf(
                    PersonalRecord(
                        exerciseName = "Bench Press",
                        weight = 225.0,
                        achievedDate = LocalDate.now().minusDays(5),
                        isRecent = true
                    ),
                    PersonalRecord(
                        exerciseName = "Squat",
                        weight = 315.0,
                        achievedDate = LocalDate.now().minusDays(10),
                        isRecent = true
                    ),
                    PersonalRecord(
                        exerciseName = "Deadlift",
                        weight = 405.0,
                        achievedDate = LocalDate.now().minusDays(45),
                        isRecent = false
                    )
                )
            )
            
            // Empty with suggestions example
            PersonalRecordsCard(
                personalRecords = emptyList(),
                shouldSuggestStarMarking = true,
                suggestedExercises = listOf("Bench Press", "Squat", "Deadlift", "Overhead Press")
            )
            
            // Empty without suggestions example
            PersonalRecordsCard(
                personalRecords = emptyList(),
                shouldSuggestStarMarking = false
            )
            
            // Loading state example
            PersonalRecordsCard(
                isLoading = true
            )
        }
    }
}