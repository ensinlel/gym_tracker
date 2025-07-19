package com.example.gym_tracker.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.data.model.StreakType
import com.example.gym_tracker.core.data.model.WorkoutStreak
import com.example.gym_tracker.core.data.usecase.GetWorkoutStreakUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the StreakCard component
 */
@HiltViewModel
class StreakViewModel @Inject constructor(
    private val getWorkoutStreakUseCase: GetWorkoutStreakUseCase
) : ViewModel() {

    // UI state for the streak card
    private val _uiState = MutableStateFlow<StreakUiState>(StreakUiState.Loading)
    val uiState: StateFlow<StreakUiState> = _uiState.asStateFlow()

    init {
        loadWorkoutStreak()
    }

    /**
     * Loads workout streak data from the use case
     */
    fun loadWorkoutStreak() {
        viewModelScope.launch {
            _uiState.value = StreakUiState.Loading
            try {
                val streak = getWorkoutStreakUseCase()
                _uiState.value = StreakUiState.Success(streak)
            } catch (e: Exception) {
                _uiState.value = StreakUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Retry loading workout streak data after an error
     */
    fun retry() {
        loadWorkoutStreak()
    }
}

/**
 * UI state for the streak card
 */
sealed class StreakUiState {
    object Loading : StreakUiState()
    data class Success(val streak: WorkoutStreak) : StreakUiState()
    data class Error(val message: String) : StreakUiState()
}