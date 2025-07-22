package com.example.gym_tracker.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.common.enums.FitnessGoal
import com.example.gym_tracker.core.common.enums.FitnessLevel
import com.example.gym_tracker.core.data.model.WeightHistory
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import com.example.gym_tracker.core.database.dao.UserProfileDao
import com.example.gym_tracker.core.database.entity.UserProfileEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the Weight Tracking Screen
 */
@HiltViewModel
class WeightTrackingViewModel @Inject constructor(
    private val analyticsRepository: AnalyticsRepository,
    private val userProfileDao: UserProfileDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeightTrackingUiState>(WeightTrackingUiState.Loading)
    val uiState: StateFlow<WeightTrackingUiState> = _uiState.asStateFlow()
    
    // For now, we'll use a default user profile ID
    // In a real app, this would come from user authentication
    private val defaultUserProfileId = "default-user"

    init {
        initializeUserProfileAndLoadData()
    }
    
    /**
     * Initialize user profile if needed and load weight history
     */
    private fun initializeUserProfileAndLoadData() {
        viewModelScope.launch {
            try {
                // Check if user profile exists, create default if not
                ensureUserProfileExists()
                // Then load weight history
                loadWeightHistory()
            } catch (e: Exception) {
                _uiState.value = WeightTrackingUiState.Error(e.message ?: "Failed to initialize")
            }
        }
    }
    
    /**
     * Ensure a default user profile exists
     */
    private suspend fun ensureUserProfileExists() {
        val existingProfile = userProfileDao.getUserProfileById(defaultUserProfileId)
        if (existingProfile == null) {
            // Create a default user profile
            val defaultProfile = UserProfileEntity(
                id = defaultUserProfileId,
                age = 25, // Default age
                weight = 70.0, // Default weight in kg
                height = 175.0, // Default height in cm (175cm)
                fitnessLevel = FitnessLevel.BEGINNER,
                goals = listOf(FitnessGoal.GENERAL_FITNESS),
                limitations = emptyList(),
                preferredEquipment = emptyList(),
                trainingFrequency = 3,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
            userProfileDao.insertUserProfile(defaultProfile)
        }
    }

    /**
     * Load weight history for the current user
     */
    fun loadWeightHistory() {
        viewModelScope.launch {
            _uiState.value = WeightTrackingUiState.Loading
            try {
                analyticsRepository.getWeightHistory(defaultUserProfileId).first().let { weightHistory ->
                    _uiState.value = WeightTrackingUiState.Success(weightHistory)
                }
            } catch (e: Exception) {
                _uiState.value = WeightTrackingUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Add a new weight entry
     */
    fun addWeightEntry(weight: Double, notes: String) {
        viewModelScope.launch {
            try {
                val weightEntry = WeightHistory(
                    id = UUID.randomUUID().toString(),
                    userProfileId = defaultUserProfileId,
                    weight = weight,
                    recordedDate = LocalDate.now(),
                    notes = notes
                )
                
                analyticsRepository.addWeightEntry(weightEntry)
                
                // Reload the weight history to show the new entry
                loadWeightHistory()
            } catch (e: Exception) {
                // Handle error - could show a snackbar or toast
                // For now, just reload to revert any optimistic updates
                loadWeightHistory()
            }
        }
    }
    
    /**
     * Seed sample weight data for testing (70-80kg range over 40 days)
     */
    fun seedSampleWeightData() {
        viewModelScope.launch {
            try {
                // Check if sample data already exists to avoid duplicates
                val existingData = analyticsRepository.getWeightHistory(defaultUserProfileId).first()
                val hasSampleData = existingData.any { it.notes.contains("30 days ago baseline") }
                if (hasSampleData) {
                    return@launch // Sample data already exists
                }
                
                val sampleWeightEntries = createSampleWeightData()
                sampleWeightEntries.forEach { weightEntry ->
                    analyticsRepository.addWeightEntry(weightEntry)
                }
                
                // Reload the weight history to show the new entries
                loadWeightHistory()
            } catch (e: Exception) {
                // Handle error silently for sample data
                loadWeightHistory()
            }
        }
    }
    
    /**
     * Create sample weight data showing a gradual weight loss trend from 78kg to 72kg
     */
    private fun createSampleWeightData(): List<WeightHistory> {
        val today = LocalDate.now()
        val sampleData = mutableListOf<WeightHistory>()
        
        // Create weight entries over the past 40 days showing a gradual trend
        val weightEntries = listOf(
            // 40 days ago - starting higher
            Pair(40, 78.2),
            Pair(37, 78.0),
            Pair(35, 77.8),
            Pair(32, 77.5),
            Pair(30, 77.2), // 30 days ago for trend comparison
            Pair(28, 77.0),
            Pair(25, 76.8),
            Pair(23, 76.5),
            Pair(20, 76.2),
            Pair(18, 76.0),
            Pair(15, 75.8),
            Pair(13, 75.5),
            Pair(10, 75.2),
            Pair(8, 75.0),
            Pair(5, 74.8),
            Pair(3, 74.5),
            Pair(1, 74.2), // Yesterday
            Pair(0, 74.0)  // Today - showing downward trend
        )
        
        weightEntries.forEach { (daysAgo, weight) ->
            sampleData.add(
                WeightHistory(
                    id = UUID.randomUUID().toString(),
                    userProfileId = defaultUserProfileId,
                    weight = weight,
                    recordedDate = today.minusDays(daysAgo.toLong()),
                    notes = when {
                        daysAgo == 0 -> "Current weight"
                        daysAgo == 30 -> "30 days ago baseline"
                        daysAgo % 7 == 0 -> "Weekly check-in"
                        weight < 75.0 -> "Making good progress!"
                        else -> ""
                    }
                )
            )
        }
        
        return sampleData
    }
}

/**
 * UI state for the Weight Tracking screen
 */
sealed class WeightTrackingUiState {
    object Loading : WeightTrackingUiState()
    data class Success(val weightHistory: List<WeightHistory>) : WeightTrackingUiState()
    data class Error(val message: String) : WeightTrackingUiState()
}