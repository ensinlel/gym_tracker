package com.example.gym_tracker.core.data.sync.impl

import com.example.gym_tracker.core.data.repository.WorkoutRepository
import com.example.gym_tracker.core.data.repository.ExerciseRepository
import com.example.gym_tracker.core.data.repository.UserProfileRepository
import com.example.gym_tracker.core.data.sync.*
import com.example.gym_tracker.core.network.NetworkStateMonitor
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of SyncManager that coordinates data synchronization
 */
@Singleton
class SyncManagerImpl @Inject constructor(
    private val networkStateMonitor: NetworkStateMonitor,
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val userProfileRepository: UserProfileRepository,
    private val syncPreferences: SyncPreferences
) : SyncManager {

    private val _syncStatus = MutableStateFlow(SyncStatus.IDLE)
    override val syncStatus: Flow<SyncStatus> = _syncStatus.asStateFlow()

    private val syncMutex = Mutex()
    private var currentSyncJob: kotlinx.coroutines.Job? = null

    override suspend fun syncAll(): SyncResult = syncMutex.withLock {
        if (!networkStateMonitor.isCurrentlyOnline()) {
            return SyncResult(SyncStatus.OFFLINE)
        }

        _syncStatus.value = SyncStatus.SYNCING
        
        try {
            val results = mutableListOf<SyncResult>()
            
            // Sync in order of dependency
            results.add(syncUserProfile())
            results.add(syncExercises())
            results.add(syncWorkouts())
            
            val totalSynced = results.sumOf { it.syncedItems }
            val totalConflicts = results.sumOf { it.conflictsResolved }
            val allErrors = results.flatMap { it.errors }
            
            val finalStatus = if (allErrors.isEmpty()) SyncStatus.SUCCESS else SyncStatus.ERROR
            _syncStatus.value = finalStatus
            
            // Update last sync time
            syncPreferences.setLastSyncTime(Instant.now())
            
            SyncResult(
                status = finalStatus,
                syncedItems = totalSynced,
                conflictsResolved = totalConflicts,
                errors = allErrors
            )
            
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.ERROR
            SyncResult(
                status = SyncStatus.ERROR,
                errors = listOf(
                    SyncError(
                        type = SyncErrorType.UNKNOWN_ERROR,
                        message = e.message ?: "Unknown sync error",
                        exception = e
                    )
                )
            )
        }
    }

    override suspend fun syncWorkouts(): SyncResult {
        return try {
            // This is a simplified implementation
            // In a real app, you would:
            // 1. Get local changes since last sync
            // 2. Get remote changes since last sync
            // 3. Resolve conflicts
            // 4. Apply changes to both local and remote
            
            val localWorkouts = workoutRepository.getAllWorkouts().first()
            
            // Simulate sync process
            kotlinx.coroutines.delay(1000) // Simulate network delay
            
            SyncResult(
                status = SyncStatus.SUCCESS,
                syncedItems = localWorkouts.size,
                conflictsResolved = 0
            )
            
        } catch (e: Exception) {
            SyncResult(
                status = SyncStatus.ERROR,
                errors = listOf(
                    SyncError(
                        type = SyncErrorType.NETWORK_ERROR,
                        message = "Failed to sync workouts: ${e.message}",
                        exception = e
                    )
                )
            )
        }
    }

    override suspend fun syncExercises(): SyncResult {
        return try {
            val localExercises = exerciseRepository.getAllExercises().first()
            
            // Simulate sync process
            kotlinx.coroutines.delay(500)
            
            SyncResult(
                status = SyncStatus.SUCCESS,
                syncedItems = localExercises.size,
                conflictsResolved = 0
            )
            
        } catch (e: Exception) {
            SyncResult(
                status = SyncStatus.ERROR,
                errors = listOf(
                    SyncError(
                        type = SyncErrorType.NETWORK_ERROR,
                        message = "Failed to sync exercises: ${e.message}",
                        exception = e
                    )
                )
            )
        }
    }

    override suspend fun syncUserProfile(): SyncResult {
        return try {
            // Simulate sync process
            kotlinx.coroutines.delay(300)
            
            SyncResult(
                status = SyncStatus.SUCCESS,
                syncedItems = 1,
                conflictsResolved = 0
            )
            
        } catch (e: Exception) {
            SyncResult(
                status = SyncStatus.ERROR,
                errors = listOf(
                    SyncError(
                        type = SyncErrorType.NETWORK_ERROR,
                        message = "Failed to sync user profile: ${e.message}",
                        exception = e
                    )
                )
            )
        }
    }

    override suspend fun setAutoSyncEnabled(enabled: Boolean) {
        syncPreferences.setAutoSyncEnabled(enabled)
    }

    override suspend fun getLastSyncTime(): Instant? {
        return syncPreferences.getLastSyncTime()
    }

    override suspend fun isSyncNeeded(): Boolean {
        val lastSync = getLastSyncTime()
        if (lastSync == null) return true
        
        // Check if it's been more than 1 hour since last sync
        val oneHourAgo = Instant.now().minusSeconds(3600)
        return lastSync.isBefore(oneHourAgo)
    }

    override suspend fun cancelSync() {
        currentSyncJob?.cancel()
        _syncStatus.value = SyncStatus.CANCELLED
    }
}

/**
 * Interface for sync preferences storage
 */
interface SyncPreferences {
    suspend fun setAutoSyncEnabled(enabled: Boolean)
    suspend fun isAutoSyncEnabled(): Boolean
    suspend fun setLastSyncTime(time: Instant)
    suspend fun getLastSyncTime(): Instant?
}