package com.example.gym_tracker.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.data.model.PersonalRecord
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import com.example.gym_tracker.core.data.usecase.GetPersonalRecordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the PersonalRecordsCard component
 */
@HiltViewModel
class PersonalRecordsViewModel @Inject constructor(
    private val getPersonalRecordsUseCase: GetPersonalRecordsUseCase,
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    // UI state for the personal records card
    private val _uiState = MutableStateFlow<PersonalRecordsUiState>(PersonalRecordsUiState.Loading)
    val uiState: StateFlow<PersonalRecordsUiState> = _uiState.asStateFlow()

    init {
        loadPersonalRecords()
    }

    /**
     * Loads personal records data from the use case
     */
    fun loadPersonalRecords() {
        viewModelScope.launch {
            _uiState.value = PersonalRecordsUiState.Loading
            try {
                val personalRecordsResult = getPersonalRecordsUseCase()
                _uiState.value = PersonalRecordsUiState.Success(personalRecordsResult)
            } catch (e: Exception) {
                _uiState.value = PersonalRecordsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Updates the star status of an exercise
     */
    fun updateExerciseStarStatus(exerciseId: String, isStarMarked: Boolean) {
        viewModelScope.launch {
            try {
                analyticsRepository.updateExerciseStarStatus(exerciseId, isStarMarked)
                loadPersonalRecords() // Reload personal records after updating star status
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    /**
     * Retry loading personal records data after an error
     */
    fun retry() {
        loadPersonalRecords()
    }
}

/**
 * UI state for the personal records card
 */
sealed class PersonalRecordsUiState {
    object Loading : PersonalRecordsUiState()
    data class Success(val personalRecordsResult: GetPersonalRecordsUseCase.PersonalRecordsResult) : PersonalRecordsUiState()
    data class Error(val message: String) : PersonalRecordsUiState()
}