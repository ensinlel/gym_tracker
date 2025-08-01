package com.example.gym_tracker.core.data.service

import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import com.example.gym_tracker.core.data.repository.GoalRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for automatically updating goal progress based on workout data
 */
@Singleton
class GoalProgressService @Inject constructor(
    private val goalRepository: GoalRepository,
    private val analyticsRepository: AnalyticsRepository
) {
    
    /**
     * Update PR goals for a specific exercise when a new PR is achieved
     */
    suspend fun updatePRGoalsForExercise(exerciseId: String) {
        try {
            // Get all PR goals for this exercise
            val prGoals = goalRepository.getGoalsByExercise(exerciseId).first()
                .filter { it.type.name == "PERSONAL_RECORD" && !it.isCompleted }
            
            if (prGoals.isEmpty()) return
            
            // Get the current PR for this exercise
            val allPRs = analyticsRepository.getPersonalRecords()
            val currentPR = allPRs.find { it.exerciseId == exerciseId }?.weight ?: 0.0
            
            // Update each PR goal with the current PR value
            prGoals.forEach { goal ->
                if (currentPR > goal.currentValue) {
                    goalRepository.updateGoalProgress(goal.id, currentPR)
                    
                    // Check if goal is completed
                    if (currentPR >= goal.targetValue) {
                        goalRepository.markGoalCompleted(goal.id)
                    }
                }
            }
        } catch (e: Exception) {
            // Log error but don't throw to avoid breaking workout flow
            println("Error updating PR goals for exercise $exerciseId: ${e.message}")
        }
    }
    
    /**
     * Update all PR goals by checking current PRs
     */
    suspend fun updateAllPRGoals() {
        try {
            val prGoals = goalRepository.getPRGoals().first()
                .filter { !it.isCompleted && it.linkedExerciseId != null }
            
            val allPRs = analyticsRepository.getPersonalRecords()
            
            prGoals.forEach { goal ->
                val exerciseId = goal.linkedExerciseId!!
                val currentPR = allPRs.find { it.exerciseId == exerciseId }?.weight ?: 0.0
                
                if (currentPR > goal.currentValue) {
                    goalRepository.updateGoalProgress(goal.id, currentPR)
                    
                    if (currentPR >= goal.targetValue) {
                        goalRepository.markGoalCompleted(goal.id)
                    }
                }
            }
        } catch (e: Exception) {
            println("Error updating all PR goals: ${e.message}")
        }
    }
}