package com.example.gym_tracker.core.data.backup.impl

import com.example.gym_tracker.core.data.backup.*
import com.example.gym_tracker.core.data.repository.WorkoutRepository
import com.example.gym_tracker.core.data.repository.ExerciseRepository
import com.example.gym_tracker.core.data.repository.UserProfileRepository
import com.example.gym_tracker.core.network.NetworkStateMonitor
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CloudBackupService
 */
@Singleton
class CloudBackupServiceImpl @Inject constructor(
    private val networkStateMonitor: NetworkStateMonitor,
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val userProfileRepository: UserProfileRepository,
    private val backupPreferences: BackupPreferences
) : CloudBackupService {

    private val _backupStatus = MutableStateFlow(BackupStatus.IDLE)
    override val backupStatus: Flow<BackupStatus> = _backupStatus.asStateFlow()

    private val backupMutex = Mutex()

    override suspend fun createFullBackup(): BackupResult = backupMutex.withLock {
        if (!networkStateMonitor.isCurrentlyOnline()) {
            return BackupResult(
                success = false,
                error = "No internet connection available"
            )
        }

        _backupStatus.value = BackupStatus.BACKING_UP
        
        try {
            // Collect all data
            val workouts = workoutRepository.getAllWorkouts().first()
            val exercises = exerciseRepository.getAllExercises().first()
            
            // Create backup data structure
            val backupData = BackupData(
                workouts = workouts,
                exercises = exercises,
                timestamp = Instant.now(),
                version = BACKUP_VERSION
            )
            
            // Simulate backup upload
            kotlinx.coroutines.delay(2000)
            
            val backupId = UUID.randomUUID().toString()
            val itemCount = workouts.size + exercises.size
            val backupSize = estimateBackupSize(backupData)
            
            // Store backup info
            val backupInfo = BackupInfo(
                id = backupId,
                timestamp = Instant.now(),
                size = backupSize,
                type = BackupType.FULL,
                itemCount = itemCount
            )
            
            backupPreferences.addBackupInfo(backupInfo)
            backupPreferences.setLastBackupTime(Instant.now())
            
            _backupStatus.value = BackupStatus.SUCCESS
            
            BackupResult(
                success = true,
                backupId = backupId,
                itemsBackedUp = itemCount,
                backupSize = backupSize
            )
            
        } catch (e: Exception) {
            _backupStatus.value = BackupStatus.ERROR
            BackupResult(
                success = false,
                error = "Backup failed: ${e.message}"
            )
        }
    }

    override suspend fun createIncrementalBackup(): BackupResult {
        val lastBackupTime = backupPreferences.getLastBackupTime()
        if (lastBackupTime == null) {
            // No previous backup, create full backup instead
            return createFullBackup()
        }
        
        return backupMutex.withLock {
            if (!networkStateMonitor.isCurrentlyOnline()) {
                return BackupResult(
                    success = false,
                    error = "No internet connection available"
                )
            }

            _backupStatus.value = BackupStatus.BACKING_UP
            
            try {
                // Get only changed data since last backup
                // This is simplified - in real implementation you'd track changes
                val allWorkouts = workoutRepository.getAllWorkouts().first()
                val changedWorkouts = allWorkouts.filter { 
                    it.startTime.isAfter(lastBackupTime)
                }
                
                // Simulate incremental backup
                kotlinx.coroutines.delay(1000)
                
                val backupId = UUID.randomUUID().toString()
                val itemCount = changedWorkouts.size
                val backupSize = estimateIncrementalBackupSize(changedWorkouts)
                
                val backupInfo = BackupInfo(
                    id = backupId,
                    timestamp = Instant.now(),
                    size = backupSize,
                    type = BackupType.INCREMENTAL,
                    itemCount = itemCount
                )
                
                backupPreferences.addBackupInfo(backupInfo)
                backupPreferences.setLastBackupTime(Instant.now())
                
                _backupStatus.value = BackupStatus.SUCCESS
                
                BackupResult(
                    success = true,
                    backupId = backupId,
                    itemsBackedUp = itemCount,
                    backupSize = backupSize
                )
                
            } catch (e: Exception) {
                _backupStatus.value = BackupStatus.ERROR
                BackupResult(
                    success = false,
                    error = "Incremental backup failed: ${e.message}"
                )
            }
        }
    }

    override suspend fun restoreFromBackup(backupId: String): RestoreResult {
        return backupMutex.withLock {
            if (!networkStateMonitor.isCurrentlyOnline()) {
                return RestoreResult(
                    success = false,
                    error = "No internet connection available"
                )
            }

            _backupStatus.value = BackupStatus.RESTORING
            
            try {
                // Simulate restore process
                kotlinx.coroutines.delay(3000)
                
                _backupStatus.value = BackupStatus.SUCCESS
                
                RestoreResult(
                    success = true,
                    itemsRestored = 100, // Simulated
                    conflictsResolved = 5 // Simulated
                )
                
            } catch (e: Exception) {
                _backupStatus.value = BackupStatus.ERROR
                RestoreResult(
                    success = false,
                    error = "Restore failed: ${e.message}"
                )
            }
        }
    }

    override suspend fun listBackups(): List<BackupInfo> {
        return backupPreferences.getBackupInfos()
    }

    override suspend fun deleteBackup(backupId: String): Boolean {
        return try {
            backupPreferences.removeBackupInfo(backupId)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun setAutoBackupEnabled(enabled: Boolean) {
        backupPreferences.setAutoBackupEnabled(enabled)
    }

    override suspend fun setBackupFrequency(frequency: BackupFrequency) {
        backupPreferences.setBackupFrequency(frequency)
    }

    override suspend fun getBackupSettings(): BackupSettings {
        return BackupSettings(
            isAutoBackupEnabled = backupPreferences.isAutoBackupEnabled(),
            frequency = backupPreferences.getBackupFrequency(),
            includeImages = backupPreferences.includeImages(),
            wifiOnly = backupPreferences.isWifiOnly(),
            maxBackupsToKeep = backupPreferences.getMaxBackupsToKeep(),
            lastBackupTime = backupPreferences.getLastBackupTime()
        )
    }

    private fun estimateBackupSize(backupData: BackupData): Long {
        // Simplified size estimation
        return (backupData.workouts.size * 1024L) + (backupData.exercises.size * 512L)
    }

    private fun estimateIncrementalBackupSize(changedWorkouts: List<Any>): Long {
        return changedWorkouts.size * 1024L
    }

    companion object {
        private const val BACKUP_VERSION = 1
    }
}

/**
 * Backup data structure
 */
data class BackupData(
    val workouts: List<Any>,
    val exercises: List<Any>,
    val timestamp: Instant,
    val version: Int
)

/**
 * Interface for backup preferences storage
 */
interface BackupPreferences {
    suspend fun setAutoBackupEnabled(enabled: Boolean)
    suspend fun isAutoBackupEnabled(): Boolean
    suspend fun setBackupFrequency(frequency: BackupFrequency)
    suspend fun getBackupFrequency(): BackupFrequency
    suspend fun includeImages(): Boolean
    suspend fun isWifiOnly(): Boolean
    suspend fun getMaxBackupsToKeep(): Int
    suspend fun setLastBackupTime(time: Instant)
    suspend fun getLastBackupTime(): Instant?
    suspend fun addBackupInfo(backupInfo: BackupInfo)
    suspend fun getBackupInfos(): List<BackupInfo>
    suspend fun removeBackupInfo(backupId: String)
}