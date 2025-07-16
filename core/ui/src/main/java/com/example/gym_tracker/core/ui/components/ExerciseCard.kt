package com.example.gym_tracker.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gym_tracker.core.ui.theme.*

/**
 * Exercise card component inspired by your design
 * Shows exercise details with weight, reps, and progress indicators
 */
@Composable
fun ExerciseCard(
    exerciseName: String,
    sets: Int,
    reps: String,
    weight: String? = null,
    isCompleted: Boolean = false,
    progress: Float? = null,
    onClick: () -> Unit,
    onAddSet: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = GymTrackerShapes.ExerciseCard,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side - Exercise info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = exerciseName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SetsBadge(sets = sets, isCompleted = isCompleted)
                    
                    weight?.let {
                        WeightDisplay(weight = it, reps = reps)
                    }
                }
                
                // Progress indicator if provided
                progress?.let {
                    LinearProgressIndicator(
                        progress = it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = AccentGreen,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }
            }
            
            // Right side - Add button
            onAddSet?.let {
                IconButton(
                    onClick = it,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = AccentPurple.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add set",
                        tint = AccentPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SetsBadge(sets: Int, isCompleted: Boolean) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isCompleted) AccentGreen.copy(alpha = 0.2f) 
               else AccentBlue.copy(alpha = 0.2f)
    ) {
        Text(
            text = "$sets sets",
            style = MaterialTheme.typography.labelSmall,
            color = if (isCompleted) AccentGreen else AccentBlue,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun WeightDisplay(weight: String, reps: String) {
    Column {
        Text(
            text = "Weight",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = weight,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
    
    Spacer(modifier = Modifier.width(12.dp))
    
    Column {
        Text(
            text = "Reps",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = reps,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExerciseCardPreview() {
    GymTrackerTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExerciseCard(
                exerciseName = "Hammer curl",
                sets = 2,
                reps = "12",
                weight = "12 kg",
                isCompleted = false,
                progress = 0.6f,
                onClick = {},
                onAddSet = {}
            )
            
            ExerciseCard(
                exerciseName = "Concentration curl",
                sets = 1,
                reps = "8-12",
                weight = "140 kg",
                isCompleted = true,
                onClick = {},
                onAddSet = {}
            )
            
            ExerciseCard(
                exerciseName = "Dumbbell bicep curl",
                sets = 2,
                reps = "10",
                weight = "146 kg",
                onClick = {}
            )
        }
    }
}