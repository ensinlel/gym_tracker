package com.example.gym_tracker.core.data.sync

import java.time.Instant

/**
 * Interface for resolving data conflicts during synchronization
 */
interface ConflictResolver<T> {
    
    /**
     * Resolve conflict between local and remote data
     */
    suspend fun resolveConflict(
        local: T,
        remote: T,
        conflictType: ConflictType
    ): ConflictResolution<T>
}

/**
 * Types of data conflicts
 */
enum class ConflictType {
    BOTH_MODIFIED,      // Both local and remote data were modified
    LOCAL_DELETED,      // Local data was deleted, remote was modified
    REMOTE_DELETED,     // Remote data was deleted, local was modified
    DUPLICATE_CREATION  // Same item created on both sides
}

/**
 * Conflict resolution strategies
 */
enum class ConflictResolutionStrategy {
    LOCAL_WINS,         // Always prefer local data
    REMOTE_WINS,        // Always prefer remote data
    LATEST_WINS,        // Prefer data with latest timestamp
    MERGE,              // Attempt to merge both versions
    MANUAL              // Require manual user intervention
}

/**
 * Result of conflict resolution
 */
data class ConflictResolution<T>(
    val resolvedData: T,
    val strategy: ConflictResolutionStrategy,
    val requiresUserAction: Boolean = false,
    val mergeDetails: String? = null
)

/**
 * Syncable data interface
 */
interface SyncableData {
    val id: String
    val lastModified: Instant
    val isDeleted: Boolean
    val syncVersion: Long
}

/**
 * Default conflict resolver implementation
 */
class DefaultConflictResolver<T : SyncableData> : ConflictResolver<T> {
    
    override suspend fun resolveConflict(
        local: T,
        remote: T,
        conflictType: ConflictType
    ): ConflictResolution<T> {
        return when (conflictType) {
            ConflictType.BOTH_MODIFIED -> {
                // Use latest timestamp strategy
                if (local.lastModified.isAfter(remote.lastModified)) {
                    ConflictResolution(local, ConflictResolutionStrategy.LOCAL_WINS)
                } else {
                    ConflictResolution(remote, ConflictResolutionStrategy.REMOTE_WINS)
                }
            }
            
            ConflictType.LOCAL_DELETED -> {
                // If local was deleted, keep it deleted unless remote has newer changes
                if (remote.lastModified.isAfter(local.lastModified)) {
                    ConflictResolution(remote, ConflictResolutionStrategy.REMOTE_WINS)
                } else {
                    ConflictResolution(local, ConflictResolutionStrategy.LOCAL_WINS)
                }
            }
            
            ConflictType.REMOTE_DELETED -> {
                // If remote was deleted, keep it deleted unless local has newer changes
                if (local.lastModified.isAfter(remote.lastModified)) {
                    ConflictResolution(local, ConflictResolutionStrategy.LOCAL_WINS)
                } else {
                    ConflictResolution(remote, ConflictResolutionStrategy.REMOTE_WINS)
                }
            }
            
            ConflictType.DUPLICATE_CREATION -> {
                // For duplicates, prefer the one with higher sync version
                if (local.syncVersion > remote.syncVersion) {
                    ConflictResolution(local, ConflictResolutionStrategy.LOCAL_WINS)
                } else {
                    ConflictResolution(remote, ConflictResolutionStrategy.REMOTE_WINS)
                }
            }
        }
    }
}