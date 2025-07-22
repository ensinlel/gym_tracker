package com.example.gym_tracker.core.data.sync

import kotlinx.coroutines.flow.Flow
import java.time.Instant

/**
 * Interface for managing data synchronization between local and remote storage
 */
interface SyncManager {
    
    /**
     * Sync status flow
     */
    val syncStatus: Flow<SyncStatus>
    
    /**
     * Trigger a full sync of all data
     */
    suspend fun syncAll(): SyncResult
    
    /**
     * Sync specific data types
     */
    suspend fun syncWorkouts(): SyncResult
    suspend fun syncExercises(): SyncResult
    suspend fun syncUserProfile(): SyncResult
    
    /**
     * Enable/disable automatic sync
     */
    suspend fun setAutoSyncEnabled(enabled: Boolean)
    
    /**
     * Get last sync timestamp
     */
    suspend fun getLastSyncTime(): Instant?
    
    /**
     * Check if sync is needed
     */
    suspend fun isSyncNeeded(): Boolean
    
    /**
     * Cancel ongoing sync operation
     */
    suspend fun cancelSync()
}

/**
 * Sync status states
 */
enum class SyncStatus {
    IDLE,
    SYNCING,
    SUCCESS,
    ERROR,
    CANCELLED,
    OFFLINE
}

/**
 * Sync result with details
 */
data class SyncResult(
    val status: SyncStatus,
    val syncedItems: Int = 0,
    val conflictsResolved: Int = 0,
    val errors: List<SyncError> = emptyList(),
    val timestamp: Instant = Instant.now()
)

/**
 * Sync error information
 */
data class SyncError(
    val type: SyncErrorType,
    val message: String,
    val itemId: String? = null,
    val exception: Throwable? = null
)

/**
 * Types of sync errors
 */
enum class SyncErrorType {
    NETWORK_ERROR,
    AUTHENTICATION_ERROR,
    CONFLICT_RESOLUTION_FAILED,
    DATA_VALIDATION_ERROR,
    STORAGE_ERROR,
    UNKNOWN_ERROR
}