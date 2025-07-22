package com.example.gym_tracker.core.data.repository

import com.example.gym_tracker.core.common.enums.ExerciseCategory
import com.example.gym_tracker.core.data.model.Exercise
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Exercise data operations
 */
interface ExerciseRepository {
    
    /**
     * Get all exercises as a reactive stream
     */
    fun getAllExercises(): Flow<List<Exercise>>
    
    /**
     * Get exercise by ID
     */
    suspend fun getExerciseById(id: String): Exercise?
    
    /**
     * Get exercise by ID as a reactive stream
     */
    fun getExerciseByIdFlow(id: String): Flow<Exercise?>
    
    /**
     * Get exercises by category
     */
    fun getExercisesByCategory(category: ExerciseCategory): Flow<List<Exercise>>
    
    /**
     * Get exercises by custom status
     */
    fun getExercisesByCustomStatus(isCustom: Boolean): Flow<List<Exercise>>
    
    /**
     * Search exercises by name
     */
    fun searchExercisesByName(query: String): Flow<List<Exercise>>
    
    /**
     * Get exercises by muscle group
     */
    fun getExercisesByMuscleGroup(muscleGroup: String): Flow<List<Exercise>>
    
    /**
     * Get exercise count
     */
    fun getExerciseCount(): Flow<Int>
    
    /**
     * Get custom exercise count
     */
    fun getCustomExerciseCount(): Flow<Int>
    
    /**
     * Get all exercise categories
     */
    fun getAllCategories(): Flow<List<ExerciseCategory>>
    
    /**
     * Get most used exercises since a timestamp
     */
    fun getMostUsedExercisesSince(startTime: Long): Flow<List<Exercise>>
    
    /**
     * Get recently used exercises
     */
    fun getRecentlyUsedExercises(limit: Int = 10): Flow<List<Exercise>>
    
    /**
     * Insert or update exercise
     */
    suspend fun insertExercise(exercise: Exercise)
    
    /**
     * Insert or update multiple exercises
     */
    suspend fun insertExercises(exercises: List<Exercise>)
    
    /**
     * Update exercise
     */
    suspend fun updateExercise(exercise: Exercise)
    
    /**
     * Delete exercise
     */
    suspend fun deleteExercise(exercise: Exercise)
    
    /**
     * Delete exercise by ID
     */
    suspend fun deleteExerciseById(id: String)
    
    /**
     * Seed database with sample exercises
     */
    suspend fun seedSampleExercises()
}