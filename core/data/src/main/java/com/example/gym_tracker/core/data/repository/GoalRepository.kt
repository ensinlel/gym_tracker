package com.example.gym_tracker.core.data.repository

import com.example.gym_tracker.core.data.model.Goal
import com.example.gym_tracker.core.data.model.GoalType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for Goal data operations
 */
interface GoalRepository {
    
    /**
     * Get all active goals as a reactive stream
     */
    fun getAllActiveGoals(): Flow<List<Goal>>
    
    /**
     * Get goal by ID
     */
    suspend fun getGoalById(goalId: String): Goal?
    
    /**
     * Get goal by ID as a reactive stream
     */
    fun getGoalByIdFlow(goalId: String): Flow<Goal?>
    
    /**
     * Get goals by type
     */
    fun getGoalsByType(type: GoalType): Flow<List<Goal>>
    
    /**
     * Get completed goals
     */
    fun getCompletedGoals(): Flow<List<Goal>>
    
    /**
     * Get active (incomplete) goals
     */
    fun getActiveGoals(): Flow<List<Goal>>
    
    /**
     * Get overdue goals
     */
    fun getOverdueGoals(date: LocalDate = LocalDate.now()): Flow<List<Goal>>
    
    /**
     * Create a new goal
     */
    suspend fun createGoal(goal: Goal)
    
    /**
     * Update an existing goal
     */
    suspend fun updateGoal(goal: Goal)
    
    /**
     * Update goal progress
     */
    suspend fun updateGoalProgress(goalId: String, currentValue: Double)
    
    /**
     * Mark goal as completed
     */
    suspend fun markGoalCompleted(goalId: String)
    
    /**
     * Deactivate a goal (soft delete)
     */
    suspend fun deactivateGoal(goalId: String)
    
    /**
     * Delete a goal permanently
     */
    suspend fun deleteGoal(goalId: String)
    
    /**
     * Get goals linked to a specific exercise
     */
    fun getGoalsByExercise(exerciseId: String): Flow<List<Goal>>
    
    /**
     * Get all PR goals
     */
    fun getPRGoals(): Flow<List<Goal>>
}