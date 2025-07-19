package com.example.gym_tracker.core.data.model

/**
 * Data class representing an exercise in the test environment
 */
data class Exercise(
    val id: String,
    val name: String,
    val isStarMarked: Boolean = false
)