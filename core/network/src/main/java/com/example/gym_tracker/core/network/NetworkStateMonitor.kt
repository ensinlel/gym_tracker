package com.example.gym_tracker.core.network

import kotlinx.coroutines.flow.Flow

/**
 * Interface for monitoring network connectivity state
 */
interface NetworkStateMonitor {
    /**
     * Flow that emits true when network is available, false otherwise
     */
    val isOnline: Flow<Boolean>
    
    /**
     * Get current network state synchronously
     */
    suspend fun isCurrentlyOnline(): Boolean
    
    /**
     * Check if device has internet connectivity (not just network connection)
     */
    suspend fun hasInternetConnectivity(): Boolean
}

/**
 * Network connection state
 */
enum class NetworkState {
    CONNECTED,
    DISCONNECTED,
    CONNECTING
}

/**
 * Network connection type
 */
enum class NetworkType {
    WIFI,
    CELLULAR,
    ETHERNET,
    UNKNOWN
}

/**
 * Detailed network information
 */
data class NetworkInfo(
    val state: NetworkState,
    val type: NetworkType,
    val isMetered: Boolean = false,
    val signalStrength: Int? = null
)