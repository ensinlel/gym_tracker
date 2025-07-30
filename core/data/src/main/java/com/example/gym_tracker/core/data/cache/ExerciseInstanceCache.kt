package com.example.gym_tracker.core.data.cache

import com.example.gym_tracker.core.data.model.ExerciseInstance
import com.example.gym_tracker.core.data.model.ExerciseInstanceWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cache for exercise instance data to improve performance
 */
@Singleton
class ExerciseInstanceCache @Inject constructor() {
    
    // Cache for exercise instances by workout ID
    private val workoutExercisesCache = mutableMapOf<String, CacheEntry<List<ExerciseInstanceWithDetails>>>()
    
    // Cache for individual exercise instances
    private val exerciseInstanceCache = mutableMapOf<String, CacheEntry<ExerciseInstance>>()
    
    // Cache for exercise instances with details
    private val exerciseInstanceWithDetailsCache = mutableMapOf<String, CacheEntry<ExerciseInstanceWithDetails>>()
    
    // Cache TTL in milliseconds (5 minutes)
    private val cacheTimeToLive = 5 * 60 * 1000L
    
    data class CacheEntry<T>(
        val data: T,
        val timestamp: Long = System.currentTimeMillis()
    ) {
        fun isExpired(): Boolean = System.currentTimeMillis() - timestamp > 5 * 60 * 1000L
    }
    
    /**
     * Get cached exercise instances for a workout
     */
    fun getCachedExerciseInstancesForWorkout(workoutId: String): List<ExerciseInstanceWithDetails>? {
        val entry = workoutExercisesCache[workoutId]
        return if (entry != null && !entry.isExpired()) {
            entry.data
        } else {
            workoutExercisesCache.remove(workoutId)
            null
        }
    }
    
    /**
     * Cache exercise instances for a workout
     */
    fun cacheExerciseInstancesForWorkout(workoutId: String, exercises: List<ExerciseInstanceWithDetails>) {
        workoutExercisesCache[workoutId] = CacheEntry(exercises)
    }
    
    /**
     * Get cached exercise instance by ID
     */
    fun getCachedExerciseInstance(id: String): ExerciseInstance? {
        val entry = exerciseInstanceCache[id]
        return if (entry != null && !entry.isExpired()) {
            entry.data
        } else {
            exerciseInstanceCache.remove(id)
            null
        }
    }
    
    /**
     * Cache exercise instance
     */
    fun cacheExerciseInstance(exerciseInstance: ExerciseInstance) {
        exerciseInstanceCache[exerciseInstance.id] = CacheEntry(exerciseInstance)
    }
    
    /**
     * Get cached exercise instance with details by ID
     */
    fun getCachedExerciseInstanceWithDetails(id: String): ExerciseInstanceWithDetails? {
        val entry = exerciseInstanceWithDetailsCache[id]
        return if (entry != null && !entry.isExpired()) {
            entry.data
        } else {
            exerciseInstanceWithDetailsCache.remove(id)
            null
        }
    }
    
    /**
     * Cache exercise instance with details
     */
    fun cacheExerciseInstanceWithDetails(exerciseInstance: ExerciseInstanceWithDetails) {
        exerciseInstanceWithDetailsCache[exerciseInstance.exerciseInstance.id] = CacheEntry(exerciseInstance)
    }
    
    /**
     * Invalidate cache for a specific workout
     */
    fun invalidateWorkoutCache(workoutId: String) {
        workoutExercisesCache.remove(workoutId)
    }
    
    /**
     * Invalidate cache for a specific exercise instance
     */
    fun invalidateExerciseInstanceCache(exerciseInstanceId: String) {
        exerciseInstanceCache.remove(exerciseInstanceId)
        exerciseInstanceWithDetailsCache.remove(exerciseInstanceId)
    }
    
    /**
     * Clear all caches
     */
    fun clearAll() {
        workoutExercisesCache.clear()
        exerciseInstanceCache.clear()
        exerciseInstanceWithDetailsCache.clear()
    }
    
    /**
     * Clean up expired entries
     */
    fun cleanupExpiredEntries() {
        workoutExercisesCache.entries.removeAll { it.value.isExpired() }
        exerciseInstanceCache.entries.removeAll { it.value.isExpired() }
        exerciseInstanceWithDetailsCache.entries.removeAll { it.value.isExpired() }
    }
}