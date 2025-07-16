package com.example.gym_tracker.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gym_tracker.core.ui.theme.GymTrackerTheme
import com.example.gym_tracker.core.ui.theme.AccentPurple
import com.example.gym_tracker.core.ui.theme.AccentBlue
import com.example.gym_tracker.core.ui.theme.GymTrackerShapes

/**
 * Workout card component inspired by your design preferences
 * Features rounded edges, dark theme, and purple accents
 */
@Composable
fun WorkoutCard(
    title: String,
    subtitle: String? = null,
    exerciseCount: Int? = null,
    duration: String? = null,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = GymTrackerShapes.WorkoutCard,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AccentPurple.copy(alpha = 0.2f) 
                           else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            
            // Subtitle if provided
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Exercise count and duration row
            if (exerciseCount != null || duration != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    exerciseCount?.let {
                        ExerciseCountChip(count = it)
                    }
                    
                    duration?.let {
                        DurationChip(duration = it)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseCountChip(count: Int) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = AccentBlue.copy(alpha = 0.2f),
        modifier = Modifier.padding(0.dp)
    ) {
        Text(
            text = "$count exercises",
            style = MaterialTheme.typography.labelMedium,
            color = AccentBlue,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun DurationChip(duration: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = AccentPurple.copy(alpha = 0.2f),
        modifier = Modifier.padding(0.dp)
    ) {
        Text(
            text = duration,
            style = MaterialTheme.typography.labelMedium,
            color = AccentPurple,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutCardPreview() {
    GymTrackerTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WorkoutCard(
                title = "Full body workout",
                subtitle = "Biceps / Triceps / Hands / Abs",
                exerciseCount = 10,
                duration = "45 min",
                isSelected = false,
                onClick = {}
            )
            
            WorkoutCard(
                title = "Push Day",
                subtitle = "Chest / Shoulders / Triceps",
                exerciseCount = 8,
                duration = "60 min",
                isSelected = true,
                onClick = {}
            )
            
            WorkoutCard(
                title = "Leg Day",
                exerciseCount = 6,
                onClick = {}
            )
        }
    }
}