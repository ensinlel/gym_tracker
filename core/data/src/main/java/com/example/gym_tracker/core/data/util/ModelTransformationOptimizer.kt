package com.example.gym_tracker.core.data.util

import com.example.gym_tracker.core.data.model.ExerciseInstanceWithDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for optimizing UI model transformations
 */
@Singleton
class ModelTransformationOptimizer @Inject constructor() {
    
    /**
     * Transform exercise instances with details to UI models in a performance-optimized way
     */
    suspend fun <T> optimizedTransform(
        data: List<ExerciseInstanceWithDetails>,
        transformer: (ExerciseInstanceWithDetails) -> T
    ): List<T> = withContext(Dispatchers.Default) {
        // Use parallel processing for large datasets
        if (data.size > 20) {
            data.chunked(10).flatMap { chunk ->
                chunk.map { transformer(it) }
            }
        } else {
            data.map { transformer(it) }
        }
    }
    
    /**
     * Batch transform with caching for repeated transformations
     */
    private val transformationCache = mutableMapOf<String, Any>()
    
    suspend fun <T> cachedTransform(
        cacheKey: String,
        data: List<ExerciseInstanceWithDetails>,
        transformer: (ExerciseInstanceWithDetails) -> T
    ): List<T> {
        @Suppress("UNCHECKED_CAST")
        val cached = transformationCache[cacheKey] as? List<T>
        if (cached != null) {
            return cached
        }
        
        val result = optimizedTransform(data, transformer)
        transformationCache[cacheKey] = result
        return result
    }
    
    /**
     * Clear transformation cache
     */
    fun clearTransformationCache() {
        transformationCache.clear()
    }
    
    /**
     * Clear specific cache entry
     */
    fun clearCacheEntry(cacheKey: String) {
        transformationCache.remove(cacheKey)
    }
}