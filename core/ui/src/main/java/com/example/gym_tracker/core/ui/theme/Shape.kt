package com.example.gym_tracker.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Shapes inspired by your card-based design preference
val Shapes = Shapes(
    // Small components like chips, buttons
    small = RoundedCornerShape(8.dp),
    
    // Medium components like cards (inspired by your workout cards)
    medium = RoundedCornerShape(16.dp),
    
    // Large components like bottom sheets, dialogs
    large = RoundedCornerShape(24.dp)
)

// Custom shapes for specific components
object GymTrackerShapes {
    val WorkoutCard = RoundedCornerShape(16.dp)
    val ExerciseCard = RoundedCornerShape(12.dp)
    val DashboardCard = RoundedCornerShape(20.dp)
    val Button = RoundedCornerShape(12.dp)
    val TextField = RoundedCornerShape(8.dp)
    val BottomSheet = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val Dialog = RoundedCornerShape(24.dp)
}