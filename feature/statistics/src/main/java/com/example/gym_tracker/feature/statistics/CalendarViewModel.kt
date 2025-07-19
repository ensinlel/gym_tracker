package com.example.gym_tracker.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * ViewModel for the CalendarCard component
 */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    // UI state for the calendar card
    private val _uiState = MutableStateFlow<CalendarUiState>(CalendarUiState.Loading)
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadCalendarData(YearMonth.now())
    }

    /**
     * Loads calendar data for the specified month
     */
    fun loadCalendarData(month: YearMonth) {
        viewModelScope.launch {
            _uiState.value = CalendarUiState.Loading
            try {
                val workoutDates = analyticsRepository.getWorkoutDatesForMonth(month)
                val workoutSummaries = mutableMapOf<LocalDate, List<String>>()
                
                // Get workout summaries for each date
                workoutDates.forEach { date ->
                    val summary = analyticsRepository.getWorkoutSummaryForDate(date)
                    workoutSummaries[date] = summary
                }
                
                _uiState.value = CalendarUiState.Success(
                    workoutDates = workoutDates,
                    workoutSummaries = workoutSummaries,
                    currentMonth = month
                )
            } catch (e: Exception) {
                _uiState.value = CalendarUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Changes the displayed month and loads data for that month
     */
    fun changeMonth(month: YearMonth) {
        loadCalendarData(month)
    }

    /**
     * Refreshes the calendar data for the current month
     */
    fun refresh() {
        val currentState = _uiState.value
        if (currentState is CalendarUiState.Success) {
            loadCalendarData(currentState.currentMonth)
        } else {
            loadCalendarData(YearMonth.now())
        }
    }
}

/**
 * UI state for the calendar card
 */
sealed class CalendarUiState {
    object Loading : CalendarUiState()
    data class Success(
        val workoutDates: Set<LocalDate>,
        val workoutSummaries: Map<LocalDate, List<String>>,
        val currentMonth: YearMonth
    ) : CalendarUiState()
    data class Error(val message: String) : CalendarUiState()
}