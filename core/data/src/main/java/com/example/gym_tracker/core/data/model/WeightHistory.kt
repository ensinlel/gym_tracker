package com.example.gym_tracker.core.data.model

import java.time.LocalDate

/**
 * Domain model for Weight History
 */
data class WeightHistory(
    val id: String,
    val userProfileId: String,
    val weight: Double,
    val recordedDate: LocalDate,
    val notes: String = ""
)