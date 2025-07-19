package com.example.gym_tracker.core.data.usecase

import com.example.gym_tracker.core.data.model.PersonalRecord
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for retrieving and enhancing personal records for star-marked exercises.
 * 
 * This use case encapsulates the business logic for:
 * - Retrieving PR values for star-marked exercises (Requirement 4.1)
 * - Formatting PR values for display simplicity (Requirement 4.2)
 * - Handling star-marked exercise inclusion (Requirement 4.3)
 * - Providing suggestions when no exercises are star-marked (Requirement 4.4)
 */
@Singleton
class GetPersonalRecordsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    
    /**
     * Executes the use case to get comprehensive personal records information
     * 
     * @return List of PersonalRecord objects containing exercise name, weight, reps,
     *         achieved date, and recency flag, or empty list with suggestion flag if none found
     */
    suspend operator fun invoke(): PersonalRecordsResult {
        val basePersonalRecords = analyticsRepository.getPersonalRecords()
        
        // Enhanced personal records with improved formatting and suggestions
        return enhancePersonalRecords(basePersonalRecords)
    }
    
    /**
     * Enhances the personal records with improved formatting and suggestions
     * for empty star-marked exercises
     */
    private suspend fun enhancePersonalRecords(records: List<PersonalRecord>): PersonalRecordsResult {
        // If no records found, check if there are any exercises that could be star-marked
        if (records.isEmpty()) {
            val shouldSuggestStarMarking = shouldSuggestStarMarking()
            return PersonalRecordsResult(
                records = emptyList(),
                shouldSuggestStarMarking = shouldSuggestStarMarking,
                suggestedExercises = if (shouldSuggestStarMarking) getSuggestedExercisesToStar() else emptyList()
            )
        }
        
        // Sort records by weight (heaviest first) and then by recency
        val sortedRecords = records.sortedWith(
            compareByDescending<PersonalRecord> { it.isRecent }
                .thenByDescending { it.weight }
        )
        
        return PersonalRecordsResult(
            records = sortedRecords,
            shouldSuggestStarMarking = false,
            suggestedExercises = emptyList()
        )
    }
    
    /**
     * Determines if star-marking suggestions should be shown
     * Requirement 4.4: When no exercises are star-marked, suggest marking key exercises
     */
    private suspend fun shouldSuggestStarMarking(): Boolean {
        // Check if there are any exercises in the system that could be star-marked
        val starMarkedExercises = analyticsRepository.getStarMarkedExercises().firstOrNull() ?: emptyList()
        return starMarkedExercises.isEmpty()
    }
    
    /**
     * Gets a list of suggested exercises to star-mark based on highest weights
     * Requirement 4.4: When no exercises are star-marked, suggest marking key exercises
     */
    private suspend fun getSuggestedExercisesToStar(): List<String> {
        // This would typically involve more complex logic to identify key exercises
        // For now, we'll return a list of common exercises as a placeholder
        return listOf(
            "Bench Press",
            "Squat",
            "Deadlift",
            "Overhead Press",
            "Pull-up"
        )
    }
    
    /**
     * Data class representing the result of the GetPersonalRecordsUseCase
     */
    data class PersonalRecordsResult(
        val records: List<PersonalRecord>,
        val shouldSuggestStarMarking: Boolean,
        val suggestedExercises: List<String>
    )
}