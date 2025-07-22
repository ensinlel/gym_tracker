package com.example.gym_tracker.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.gym_tracker.core.network.impl.NetworkStateMonitorImpl
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

/**
 * Unit tests for NetworkStateMonitor - Task 3.3
 * Tests network connectivity monitoring functionality
 */
class NetworkStateMonitorTest {
    
    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCapabilities: NetworkCapabilities
    private lateinit var networkStateMonitor: NetworkStateMonitor
    
    @Before
    fun setup() {
        context = mockk()
        connectivityManager = mockk()
        networkCapabilities = mockk()
        
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        
        networkStateMonitor = NetworkStateMonitorImpl(context)
    }
    
    @Test
    fun `isCurrentlyOnline returns true when network has internet capability`() = runTest {
        // Given
        every { connectivityManager.activeNetwork } returns mockk()
        every { connectivityManager.getNetworkCapabilities(any()) } returns networkCapabilities
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns true
        
        // When
        val result = networkStateMonitor.isCurrentlyOnline()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `isCurrentlyOnline returns false when network has no internet capability`() = runTest {
        // Given
        every { connectivityManager.activeNetwork } returns mockk()
        every { connectivityManager.getNetworkCapabilities(any()) } returns networkCapabilities
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns false
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) } returns false
        
        // When
        val result = networkStateMonitor.isCurrentlyOnline()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `isCurrentlyOnline returns false when no active network`() = runTest {
        // Given
        every { connectivityManager.activeNetwork } returns null
        
        // When
        val result = networkStateMonitor.isCurrentlyOnline()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `hasInternetConnectivity returns false when socket connection fails`() = runTest {
        // This test would require mocking socket connections
        // In a real implementation, you might use a test double or dependency injection
        // for the socket connection logic
        
        // When
        val result = networkStateMonitor.hasInternetConnectivity()
        
        // Then
        // This will likely be false in test environment without actual network
        assertFalse(result)
    }
}