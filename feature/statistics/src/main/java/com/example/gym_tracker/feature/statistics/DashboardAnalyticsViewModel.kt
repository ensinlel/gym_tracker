package com.example.gym_tracker.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.data.model.*
import com.example.gym_tracker.core.data.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * ViewModel for the dashboard analytics components
 * Integrates all analytics use cases into a single ViewModel for the dashboard
 */
@HiltViewModel
class DashboardAnalyticsViewModel @Inject constructor(
    private val getWorkoutStreakUseCase: GetWorkoutStreakUseCase,
    private val getMonthlyStatsUseCase: GetMonthlyStatsUseCase,
    private val getVolumeProgressUseCase: GetVolumeProgressUseCase,
    private val getPersonalRecordsUseCase: GetPersonalRecordsUseCase,
    private val getWeightProgressUseCase: GetWeightProgressUseCase
) : ViewModel() {

    // UI state for the dashboard analytics
    private val _uiState = MutableStateFlow<DashboardAnalyticsUiState>(DashboardAnalyticsUiState.Loading)
    val uiState: StateFlow<DashboardAnalyticsUiState> = _uiState.asStateFlow()
    
    // User profile ID - in a real app, this would come from a user repository or preferences
    private val userProfileId = "default-user"

    init {
        loadAllAnalytics()
    }

    /**
     * Loads all analytics data for the dashboard
     */
    fun loadAllAnalytics() {
        viewModelScope.launch {
            _uiState.value = DashboardAnalyticsUiState.Loading
            try {
                // Load all analytics data in parallel
                val workoutStreak = getWorkoutStreakUseCase()
                val monthlyStats = getMonthlyStatsUseCase()
                val volumeProgress = getVolumeProgressUseCase()
                val personalRecordsResult = getPersonalRecordsUseCase()
                val weightProgressResult = getWeightProgressUseCase(userProfileId)
                
                _uiState.value = DashboardAnalyticsUiState.Success(
                    workoutStreak = workoutStreak,
                    monthlyStats = monthlyStats,
                    volumeProgress = volumeProgress,
                    personalRecordsResult = personalRecordsResult,
                    weightProgressResult = weightProgressResult
                )
            } catch (e: Exception) {
                _uiState.value = DashboardAnalyticsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Refreshes all analytics data
     */
    fun refresh() {
        loadAllAnalytics()
    }
}

/**
 * UI state for the dashboard analytics
 */
sealed class DashboardAnalyticsUiState {
    object Loading : DashboardAnalyticsUiState()
    
    data class Success(
        val workoutStreak: WorkoutStreak,
        val monthlyStats: MonthlyWorkoutStats,
        val volumeProgress: VolumeProgress,
        val personalRecordsResult: GetPersonalRecordsUseCase.PersonalRecordsResult,
        val weightProgressResult: GetWeightProgressUseCase.WeightProgressResult
    ) : DashboardAnalyticsUiState()
    
    data class Error(val message: String) : DashboardAnalyticsUiState()
}