package com.example.gym_tracker.core.data.repository

import com.example.gym_tracker.core.data.model.Workout
import com.example.gym_tracker.core.data.model.WorkoutWithDetails
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Workout data operations
 */
interface WorkoutRepository {
    
    /**
     * Get all workouts as a reactive stream
     */
    fun getAllWorkouts(): Flow<List<Workout>>
    
    /**
     * Get workout by ID
     */
    suspend fun getWorkoutById(id: String): Workout?
    
    /**
     * Get workout by ID as a reactive stream
     */
    fun getWorkoutByIdFlow(id: String): Flow<Workout?>
    
    /**
     * Get workout with basic details by ID
     */
    suspend fun getWorkoutWithDetailsById(id: String): WorkoutWithDetails?
    
    /**
     * Get workout with basic details by ID as a reactive stream
     */
    fun getWorkoutWithDetailsByIdFlow(id: String): Flow<WorkoutWithDetails?>
    
    /**
     * Get all workouts with basic details
     */
    fun getAllWorkoutsWithDetails(): Flow<List<WorkoutWithDetails>>
    

    
    /**
     * Get workouts by template ID
     */
    fun getWorkoutsByTemplate(templateId: String): Flow<List<Workout>>
    
    /**
     * Get workouts within a date range
     */
    fun getWorkoutsByDateRange(startTime: Long, endTime: Long): Flow<List<Workout>>
    
    /**
     * Get workout count
     */
    fun getWorkoutCount(): Flow<Int>
    
    /**
     * Get workout count since a timestamp
     */
    fun getWorkoutCountSince(startTime: Long): Flow<Int>
    
    /**
     * Get average workout volume
     */
    fun getAverageWorkoutVolume(): Flow<Double?>
    
    /**
     * Get maximum workout volume
     */
    fun getMaxWorkoutVolume(): Flow<Double?>
    
    /**
     * Get rated workouts
     */
    fun getRatedWorkouts(): Flow<List<Workout>>
    
    /**
     * Insert or update workout
     */
    suspend fun insertWorkout(workout: Workout)
    
    /**
     * Insert or update multiple workouts
     */
    suspend fun insertWorkouts(workouts: List<Workout>)
    
    /**
     * Update workout
     */
    suspend fun updateWorkout(workout: Workout)
    
    /**
     * Delete workout
     */
    suspend fun deleteWorkout(workout: Workout)
    
    /**
     * Delete workout by ID
     */
    suspend fun deleteWorkoutById(id: String)
}