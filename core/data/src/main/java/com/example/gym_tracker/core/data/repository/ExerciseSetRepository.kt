package com.example.gym_tracker.core.data.repository

import com.example.gym_tracker.core.data.model.ExerciseSet
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Exercise Set data operations
 */
interface ExerciseSetRepository {
    
    /**
     * Get sets by exercise instance ID
     */
    fun getSetsByExerciseInstance(exerciseInstanceId: String): Flow<List<ExerciseSet>>
    
    /**
     * Get set by ID
     */
    suspend fun getSetById(id: String): ExerciseSet?
    
    /**
     * Get set by ID as a reactive stream
     */
    fun getSetByIdFlow(id: String): Flow<ExerciseSet?>
    
    /**
     * Get set by exercise instance and set number
     */
    suspend fun getSetByNumber(exerciseInstanceId: String, setNumber: Int): ExerciseSet?
    
    /**
     * Get maximum set number for an exercise instance
     */
    suspend fun getMaxSetNumber(exerciseInstanceId: String): Int?
    
    /**
     * Get set count by exercise instance
     */
    fun getSetCountByExerciseInstance(exerciseInstanceId: String): Flow<Int>
    
    /**
     * Get working set count (excluding warmup sets)
     */
    fun getWorkingSetCountByExerciseInstance(exerciseInstanceId: String): Flow<Int>
    
    /**
     * Get average weight for an exercise
     */
    fun getAverageWeightForExercise(exerciseId: String): Flow<Double?>
    
    /**
     * Get maximum weight for an exercise
     */
    fun getMaxWeightForExercise(exerciseId: String): Flow<Double?>
    
    /**
     * Get estimated one rep max for an exercise
     */
    fun getEstimatedOneRepMaxForExercise(exerciseId: String): Flow<Double?>
    
    /**
     * Get total volume for an exercise since a timestamp
     */
    fun getTotalVolumeForExerciseSince(exerciseId: String, startTime: Long): Flow<Double?>
    
    /**
     * Get average RPE for an exercise
     */
    fun getAverageRPEForExercise(exerciseId: String): Flow<Double?>
    
    /**
     * Get average rest time for an exercise instance
     */
    fun getAverageRestTimeForExerciseInstance(exerciseInstanceId: String): Flow<Long?>
    
    /**
     * Get set history for an exercise
     */
    fun getSetHistoryForExercise(exerciseId: String): Flow<List<ExerciseSet>>
    
    /**
     * Get set history for an exercise since a timestamp
     */
    fun getSetHistoryForExerciseSince(exerciseId: String, startTime: Long): Flow<List<ExerciseSet>>
    
    /**
     * Get failure sets
     */
    fun getFailureSets(): Flow<List<ExerciseSet>>
    
    /**
     * Get high intensity sets (RPE >= minRpe)
     */
    fun getHighIntensitySets(minRpe: Int = 8): Flow<List<ExerciseSet>>
    
    /**
     * Insert or update set
     */
    suspend fun insertSet(set: ExerciseSet)
    
    /**
     * Insert or update multiple sets
     */
    suspend fun insertSets(sets: List<ExerciseSet>)
    
    /**
     * Update set
     */
    suspend fun updateSet(set: ExerciseSet)
    
    /**
     * Delete set
     */
    suspend fun deleteSet(set: ExerciseSet)
    
    /**
     * Delete set by ID
     */
    suspend fun deleteSetById(id: String)
    
    /**
     * Delete sets by exercise instance
     */
    suspend fun deleteSetsByExerciseInstance(exerciseInstanceId: String)
    
    /**
     * Delete set by exercise instance and set number
     */
    suspend fun deleteSetByNumber(exerciseInstanceId: String, setNumber: Int)
}