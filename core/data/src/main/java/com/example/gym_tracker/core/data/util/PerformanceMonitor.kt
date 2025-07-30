package com.example.gym_tracker.core.data.util

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility for monitoring database operation performance
 */
@Singleton
class PerformanceMonitor @Inject constructor() {
    
    private val performanceMetrics = mutableMapOf<String, MutableList<Long>>()
    
    /**
     * Measure the execution time of a database operation
     */
    suspend fun <T> measureOperation(
        operationName: String,
        operation: suspend () -> T
    ): T {
        val startTime = System.currentTimeMillis()
        
        return try {
            val result = operation()
            val executionTime = System.currentTimeMillis() - startTime
            
            // Record performance metric
            recordMetric(operationName, executionTime)
            
            // Log slow operations (> 100ms)
            if (executionTime > 100) {
                Log.w("PerformanceMonitor", "Slow operation: $operationName took ${executionTime}ms")
            }
            
            result
        } catch (e: Exception) {
            val executionTime = System.currentTimeMillis() - startTime
            Log.e("PerformanceMonitor", "Failed operation: $operationName took ${executionTime}ms", e)
            throw e
        }
    }
    
    /**
     * Record a performance metric
     */
    private fun recordMetric(operationName: String, executionTime: Long) {
        val metrics = performanceMetrics.getOrPut(operationName) { mutableListOf() }
        metrics.add(executionTime)
        
        // Keep only the last 100 measurements to prevent memory leaks
        if (metrics.size > 100) {
            metrics.removeAt(0)
        }
    }
    
    /**
     * Get average execution time for an operation
     */
    fun getAverageExecutionTime(operationName: String): Double? {
        val metrics = performanceMetrics[operationName]
        return if (metrics != null && metrics.isNotEmpty()) {
            metrics.average()
        } else null
    }
    
    /**
     * Get performance summary
     */
    fun getPerformanceSummary(): Map<String, PerformanceStats> {
        return performanceMetrics.mapValues { (_, times) ->
            if (times.isNotEmpty()) {
                PerformanceStats(
                    operationCount = times.size,
                    averageTime = times.average(),
                    minTime = times.minOrNull() ?: 0,
                    maxTime = times.maxOrNull() ?: 0
                )
            } else {
                PerformanceStats(0, 0.0, 0, 0)
            }
        }
    }
    
    /**
     * Clear all performance metrics
     */
    fun clearMetrics() {
        performanceMetrics.clear()
    }
    
    data class PerformanceStats(
        val operationCount: Int,
        val averageTime: Double,
        val minTime: Long,
        val maxTime: Long
    )
}