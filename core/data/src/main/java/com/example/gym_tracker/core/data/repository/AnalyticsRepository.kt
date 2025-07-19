package com.example.gym_tracker.core.data.repository

import com.example.gym_tracker.core.data.model.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for Analytics data operations
 */
interface AnalyticsRepository {
    
    // Workout Streak Analytics
    /**
     * Get current workout streak information
     */
    suspend fun getWorkoutStreak(): WorkoutStreak
    
    /**
     * Get workout dates for calendar display
     */
    fun getWorkoutDates(startDate: LocalDate, endDate: LocalDate): Flow<List<LocalDate>>
    
    // Monthly Statistics
    /**
     * Get monthly workout statistics comparing current and previous month
     */
    suspend fun getMonthlyWorkoutStats(): MonthlyWorkoutStats
    
    /**
     * Get workout count for a specific month
     */
    suspend fun getWorkoutCountForMonth(year: Int, month: Int): Int
    
    /**
     * Get workout count for a date range
     */
    suspend fun getWorkoutCountForDateRange(startDate: LocalDate, endDate: LocalDate): Int
    
    // Volume Progress Analytics
    /**
     * Get volume progress information comparing current and previous month
     */
    suspend fun getVolumeProgress(): VolumeProgress
    
    /**
     * Get total volume for a specific month
     */
    suspend fun getTotalVolumeForMonth(year: Int, month: Int): Double
    
    /**
     * Get most improved exercise based on weight progression
     */
    suspend fun getMostImprovedExercise(): ExerciseImprovement?
    
    // Personal Records
    /**
     * Get personal records for star-marked exercises
     */
    suspend fun getPersonalRecords(): List<PersonalRecord>
    
    /**
     * Get star-marked exercises
     */
    fun getStarMarkedExercises(): Flow<List<Exercise>>
    
    /**
     * Get best set for a specific exercise
     */
    suspend fun getBestSetForExercise(exerciseId: String): ExerciseSet?
    
    /**
     * Update exercise star status
     */
    suspend fun updateExerciseStarStatus(exerciseId: String, isStarMarked: Boolean)
    
    // Weight Progress
    /**
     * Get weight progress information
     */
    suspend fun getWeightProgress(userProfileId: String): WeightProgress
    
    /**
     * Get latest weight entry for user
     */
    suspend fun getLatestWeightEntry(userProfileId: String): WeightHistory?
    
    /**
     * Get weight entry from specific days ago
     */
    suspend fun getWeightEntryFromDaysAgo(userProfileId: String, daysAgo: Int): WeightHistory?
    
    /**
     * Get weight history for user
     */
    fun getWeightHistory(userProfileId: String): Flow<List<WeightHistory>>
    
    /**
     * Add weight entry
     */
    suspend fun addWeightEntry(weightHistory: WeightHistory)
    
    // Calendar Integration
    /**
     * Check if a specific date has workouts
     */
    suspend fun hasWorkoutOnDate(date: LocalDate): Boolean
    
    /**
     * Get workout summary for a specific date
     */
    suspend fun getWorkoutSummaryForDate(date: LocalDate): List<String>
    
    /**
     * Get all workout dates for a specific month
     */
    suspend fun getWorkoutDatesForMonth(month: java.time.YearMonth): Set<LocalDate>
}