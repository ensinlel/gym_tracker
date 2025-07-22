package com.example.gym_tracker.core.data.backup

import kotlinx.coroutines.flow.Flow
import java.time.Instant

/**
 * Interface for cloud backup operations
 */
interface CloudBackupService {
    
    /**
     * Backup status flow
     */
    val backupStatus: Flow<BackupStatus>
    
    /**
     * Create a full backup of all user data
     */
    suspend fun createFullBackup(): BackupResult
    
    /**
     * Create incremental backup (only changed data since last backup)
     */
    suspend fun createIncrementalBackup(): BackupResult
    
    /**
     * Restore data from backup
     */
    suspend fun restoreFromBackup(backupId: String): RestoreResult
    
    /**
     * List available backups
     */
    suspend fun listBackups(): List<BackupInfo>
    
    /**
     * Delete a backup
     */
    suspend fun deleteBackup(backupId: String): Boolean
    
    /**
     * Enable/disable automatic backups
     */
    suspend fun setAutoBackupEnabled(enabled: Boolean)
    
    /**
     * Set backup frequency
     */
    suspend fun setBackupFrequency(frequency: BackupFrequency)
    
    /**
     * Get backup settings
     */
    suspend fun getBackupSettings(): BackupSettings
}

/**
 * Backup status states
 */
enum class BackupStatus {
    IDLE,
    BACKING_UP,
    RESTORING,
    SUCCESS,
    ERROR,
    CANCELLED
}

/**
 * Backup frequency options
 */
enum class BackupFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    MANUAL_ONLY
}

/**
 * Backup result
 */
data class BackupResult(
    val success: Boolean,
    val backupId: String? = null,
    val itemsBackedUp: Int = 0,
    val backupSize: Long = 0, // in bytes
    val error: String? = null,
    val timestamp: Instant = Instant.now()
)

/**
 * Restore result
 */
data class RestoreResult(
    val success: Boolean,
    val itemsRestored: Int = 0,
    val conflictsResolved: Int = 0,
    val error: String? = null,
    val timestamp: Instant = Instant.now()
)

/**
 * Backup information
 */
data class BackupInfo(
    val id: String,
    val timestamp: Instant,
    val size: Long,
    val type: BackupType,
    val itemCount: Int,
    val isCorrupted: Boolean = false
)

/**
 * Backup type
 */
enum class BackupType {
    FULL,
    INCREMENTAL
}

/**
 * Backup settings
 */
data class BackupSettings(
    val isAutoBackupEnabled: Boolean = false,
    val frequency: BackupFrequency = BackupFrequency.WEEKLY,
    val includeImages: Boolean = true,
    val wifiOnly: Boolean = true,
    val maxBackupsToKeep: Int = 10,
    val lastBackupTime: Instant? = null
)