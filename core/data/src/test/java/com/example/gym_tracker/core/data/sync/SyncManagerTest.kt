package com.example.gym_tracker.core.data.sync

import com.example.gym_tracker.core.data.repository.WorkoutRepository
import com.example.gym_tracker.core.data.repository.ExerciseRepository
import com.example.gym_tracker.core.data.repository.UserProfileRepository
import com.example.gym_tracker.core.data.sync.impl.SyncManagerImpl
import com.example.gym_tracker.core.data.sync.impl.SyncPreferences
import com.example.gym_tracker.core.network.NetworkStateMonitor
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for SyncManager - Task 3.3
 * Tests offline-first architecture and sync functionality
 */
class SyncManagerTest {
    
    private lateinit var networkStateMonitor: NetworkStateMonitor
    private lateinit var workoutRepository: WorkoutRepository
    private lateinit var exerciseRepository: ExerciseRepository
    private lateinit var userProfileRepository: UserProfileRepository
    private lateinit var syncPreferences: SyncPreferences
    private lateinit var syncManager: SyncManager
    
    @Before
    fun setup() {
        networkStateMonitor = mockk()
        workoutRepository = mockk()
        exerciseRepository = mockk()
        userProfileRepository = mockk()
        syncPreferences = mockk()
        
        syncManager = SyncManagerImpl(
            networkStateMonitor = networkStateMonitor,
            workoutRepository = workoutRepository,
            exerciseRepository = exerciseRepository,
            userProfileRepository = userProfileRepository,
            syncPreferences = syncPreferences
        )
    }
    
    @Test
    fun `syncAll returns offline status when network is unavailable`() = runTest {
        // Given
        coEvery { networkStateMonitor.isCurrentlyOnline() } returns false
        
        // When
        val result = syncManager.syncAll()
        
        // Then
        assertEquals(SyncStatus.OFFLINE, result.status)
        assertEquals(0, result.syncedItems)
        assertTrue(result.errors.isEmpty())
    }
    
    @Test
    fun `syncAll succeeds when network is available and data syncs successfully`() = runTest {
        // Given
        coEvery { networkStateMonitor.isCurrentlyOnline() } returns true
        every { workoutRepository.getAllWorkouts() } returns flowOf(emptyList())
        every { exerciseRepository.getAllExercises() } returns flowOf(emptyList())
        coEvery { syncPreferences.setLastSyncTime(any()) } just Runs
        
        // When
        val result = syncManager.syncAll()
        
        // Then
        assertEquals(SyncStatus.SUCCESS, result.status)
        assertTrue(result.errors.isEmpty())
        coVerify { syncPreferences.setLastSyncTime(any()) }
    }
    
    @Test
    fun `syncWorkouts handles network errors gracefully`() = runTest {
        // Given
        every { workoutRepository.getAllWorkouts() } throws RuntimeException("Network error")
        
        // When
        val result = syncManager.syncWorkouts()
        
        // Then
        assertEquals(SyncStatus.ERROR, result.status)
        assertEquals(1, result.errors.size)
        assertEquals(SyncErrorType.NETWORK_ERROR, result.errors[0].type)
        assertTrue(result.errors[0].message.contains("Network error"))
    }
    
    @Test
    fun `isSyncNeeded returns true when no previous sync exists`() = runTest {
        // Given
        coEvery { syncPreferences.getLastSyncTime() } returns null
        
        // When
        val result = syncManager.isSyncNeeded()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `isSyncNeeded returns true when last sync was more than 1 hour ago`() = runTest {
        // Given
        val twoHoursAgo = Instant.now().minusSeconds(7200)
        coEvery { syncPreferences.getLastSyncTime() } returns twoHoursAgo
        
        // When
        val result = syncManager.isSyncNeeded()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `isSyncNeeded returns false when last sync was recent`() = runTest {
        // Given
        val thirtyMinutesAgo = Instant.now().minusSeconds(1800)
        coEvery { syncPreferences.getLastSyncTime() } returns thirtyMinutesAgo
        
        // When
        val result = syncManager.isSyncNeeded()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `sync status flow emits correct states during sync process`() = runTest {
        // Given
        coEvery { networkStateMonitor.isCurrentlyOnline() } returns true
        every { workoutRepository.getAllWorkouts() } returns flowOf(emptyList())
        every { exerciseRepository.getAllExercises() } returns flowOf(emptyList())
        coEvery { syncPreferences.setLastSyncTime(any()) } just Runs
        
        // When
        val initialStatus = syncManager.syncStatus.first()
        syncManager.syncAll()
        val finalStatus = syncManager.syncStatus.first()
        
        // Then
        assertEquals(SyncStatus.IDLE, initialStatus)
        assertEquals(SyncStatus.SUCCESS, finalStatus)
    }
    
    @Test
    fun `setAutoSyncEnabled updates preferences`() = runTest {
        // Given
        coEvery { syncPreferences.setAutoSyncEnabled(true) } just Runs
        
        // When
        syncManager.setAutoSyncEnabled(true)
        
        // Then
        coVerify { syncPreferences.setAutoSyncEnabled(true) }
    }
    
    @Test
    fun `cancelSync sets status to cancelled`() = runTest {
        // When
        syncManager.cancelSync()
        
        // Then
        val status = syncManager.syncStatus.first()
        assertEquals(SyncStatus.CANCELLED, status)
    }
}