package com.example.gym_tracker.core.data.cache

import com.example.gym_tracker.core.data.model.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory cache for analytics data to improve performance
 * Implements TTL (Time To Live) caching with automatic expiration
 */
@Singleton
class AnalyticsCache @Inject constructor() {
    
    // Make these internal so inline functions can access them
    internal val mutex = Mutex()
    internal val cache = mutableMapOf<String, CacheEntry<*>>()
    
    // Cache TTL configurations (in minutes)
    internal val defaultTtl = 5L // 5 minutes for most analytics
    private val workoutStreakTtl = 10L // 10 minutes for workout streak
    private val weightProgressTtl = 15L // 15 minutes for weight progress
    private val personalRecordsTtl = 30L // 30 minutes for personal records
    
    /**
     * Get cached value if it exists and hasn't expired
     */
    internal suspend inline fun <reified T> get(key: String): T? = mutex.withLock {
        val entry = cache[key] as? CacheEntry<T>
        if (entry != null && !entry.isExpired()) {
            entry.value
        } else {
            cache.remove(key)
            null
        }
    }
    
    /**
     * Put value in cache with default TTL
     */
    suspend fun <T> put(key: String, value: T, ttlMinutes: Long = defaultTtl): Unit = mutex.withLock {
        cache[key] = CacheEntry(value, LocalDateTime.now().plusMinutes(ttlMinutes))
    }
    
    /**
     * Get or compute value with caching
     */
    internal suspend inline fun <reified T> getOrCompute(
        key: String,
        ttlMinutes: Long = defaultTtl,
        crossinline compute: suspend () -> T
    ): T {
        return get<T>(key) ?: run {
            val value = compute()
            put(key, value, ttlMinutes)
            value
        }
    }
    
    /**
     * Invalidate specific cache entry
     */
    suspend fun invalidate(key: String): Unit = mutex.withLock {
        cache.remove(key)
        Unit
    }
    
    /**
     * Invalidate all cache entries matching a pattern
     */
    suspend fun invalidatePattern(pattern: String): Unit = mutex.withLock {
        val keysToRemove = cache.keys.filter { it.contains(pattern) }
        keysToRemove.forEach { cache.remove(it) }
        Unit
    }
    
    /**
     * Clear all cache entries
     */
    suspend fun clear(): Unit = mutex.withLock {
        cache.clear()
    }
    
    /**
     * Get cache statistics for monitoring
     */
    internal suspend fun getStats(): CacheStats = mutex.withLock {
        val totalEntries = cache.size
        val expiredEntries = cache.values.count { it.isExpired() }
        val activeEntries = totalEntries - expiredEntries
        
        CacheStats(
            totalEntries = totalEntries,
            activeEntries = activeEntries,
            expiredEntries = expiredEntries,
            hitRatio = 0.0 // Would need hit/miss tracking for accurate ratio
        )
    }
    
    // Cache key generators
    object Keys {
        fun workoutStreak() = "workout_streak"
        fun monthlyStats() = "monthly_stats"
        fun volumeProgress() = "volume_progress"
        fun personalRecords() = "personal_records"
        fun weightProgress(userId: String) = "weight_progress_$userId"
        fun workoutCount(year: Int, month: Int) = "workout_count_${year}_$month"
        fun totalVolume(year: Int, month: Int) = "total_volume_${year}_$month"
        fun mostImprovedExercise() = "most_improved_exercise"
        fun workoutDates(startDate: LocalDate, endDate: LocalDate) = "workout_dates_${startDate}_$endDate"
    }
    
    // TTL configurations for different data types
    object TTL {
        const val WORKOUT_STREAK = 10L
        const val MONTHLY_STATS = 15L
        const val VOLUME_PROGRESS = 15L
        const val PERSONAL_RECORDS = 30L
        const val WEIGHT_PROGRESS = 15L
        const val WORKOUT_COUNT = 60L // Longer TTL for historical data
        const val MOST_IMPROVED = 30L
    }
    
    internal data class CacheEntry<T>(
        val value: T,
        val expiresAt: LocalDateTime
    ) {
        fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiresAt)
    }
    
    data class CacheStats(
        val totalEntries: Int,
        val activeEntries: Int,
        val expiredEntries: Int,
        val hitRatio: Double
    )
}