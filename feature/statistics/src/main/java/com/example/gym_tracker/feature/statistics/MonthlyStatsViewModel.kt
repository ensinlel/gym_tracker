package com.example.gym_tracker.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.data.model.MonthlyWorkoutStats
import com.example.gym_tracker.core.data.model.TrendDirection
import com.example.gym_tracker.core.data.usecase.GetMonthlyStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Month
import javax.inject.Inject

/**
 * ViewModel for the MonthlyStatsCard component
 */
@HiltViewModel
class MonthlyStatsViewModel @Inject constructor(
    private val getMonthlyStatsUseCase: GetMonthlyStatsUseCase
) : ViewModel() {

    // UI state for the monthly stats card
    private val _uiState = MutableStateFlow<MonthlyStatsUiState>(MonthlyStatsUiState.Loading)
    val uiState: StateFlow<MonthlyStatsUiState> = _uiState.asStateFlow()

    init {
        loadMonthlyStats()
    }

    /**
     * Loads monthly stats data from the use case
     */
    fun loadMonthlyStats() {
        viewModelScope.launch {
            _uiState.value = MonthlyStatsUiState.Loading
            try {
                val stats = getMonthlyStatsUseCase()
                
                // Get current and previous month names
                val now = java.time.LocalDate.now()
                val currentMonth = Month.of(now.monthValue)
                val previousMonth = Month.of(if (now.monthValue == 1) 12 else now.monthValue - 1)
                
                _uiState.value = MonthlyStatsUiState.Success(
                    stats = stats,
                    currentMonth = currentMonth,
                    previousMonth = previousMonth
                )
            } catch (e: Exception) {
                _uiState.value = MonthlyStatsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Retry loading monthly stats data after an error
     */
    fun retry() {
        loadMonthlyStats()
    }
}

/**
 * UI state for the monthly stats card
 */
sealed class MonthlyStatsUiState {
    object Loading : MonthlyStatsUiState()
    data class Success(
        val stats: MonthlyWorkoutStats,
        val currentMonth: Month,
        val previousMonth: Month
    ) : MonthlyStatsUiState()
    data class Error(val message: String) : MonthlyStatsUiState()
}