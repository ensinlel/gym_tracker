package com.example.gym_tracker.core.data.model

import java.time.LocalDate

/**
 * Data class representing a weight history entry in the test environment
 */
data class WeightHistory(
    val id: String,
    val userProfileId: String,
    val weight: Double,
    val recordedDate: LocalDate,
    val notes: String? = null
)