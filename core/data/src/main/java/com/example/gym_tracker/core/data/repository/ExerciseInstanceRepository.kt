package com.example.gym_tracker.core.data.repository

import com.example.gym_tracker.core.data.model.ExerciseInstance
import com.example.gym_tracker.core.data.model.ExerciseInstanceWithDetails
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Exercise Instance data operations
 */
interface ExerciseInstanceRepository {
    
    /**
     * Get exercise instances by workout ID
     */
    fun getExerciseInstancesByWorkoutId(workoutId: String): Flow<List<ExerciseInstance>>
    
    /**
     * Get exercise instances with details by workout ID
     */
    fun getExerciseInstancesWithDetailsByWorkoutId(workoutId: String): Flow<List<ExerciseInstanceWithDetails>>
    
    /**
     * Get exercise instance by ID
     */
    suspend fun getExerciseInstanceById(id: String): ExerciseInstance?
    
    /**
     * Get exercise instance with details by ID
     */
    suspend fun getExerciseInstanceWithDetailsById(id: String): ExerciseInstanceWithDetails?
    
    /**
     * Get exercise instances by exercise ID
     */
    fun getExerciseInstancesByExerciseId(exerciseId: String): Flow<List<ExerciseInstance>>
    
    /**
     * Insert exercise instance
     */
    suspend fun insertExerciseInstance(exerciseInstance: ExerciseInstance): String
    
    /**
     * Insert multiple exercise instances
     */
    suspend fun insertExerciseInstances(exerciseInstances: List<ExerciseInstance>)
    
    /**
     * Update exercise instance
     */
    suspend fun updateExerciseInstance(exerciseInstance: ExerciseInstance)
    
    /**
     * Delete exercise instance
     */
    suspend fun deleteExerciseInstance(exerciseInstanceId: String)
    
    /**
     * Delete exercise instances by workout ID
     */
    suspend fun deleteExerciseInstancesByWorkoutId(workoutId: String)
    
    /**
     * Get next order number for workout
     */
    suspend fun getNextOrderInWorkout(workoutId: String): Int
}