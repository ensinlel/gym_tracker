package com.example.gym_tracker.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.data.model.VolumeProgress
import com.example.gym_tracker.core.data.usecase.GetVolumeProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the VolumeProgressCard component
 */
@HiltViewModel
class VolumeProgressViewModel @Inject constructor(
    private val getVolumeProgressUseCase: GetVolumeProgressUseCase
) : ViewModel() {

    // UI state for the volume progress card
    private val _uiState = MutableStateFlow<VolumeProgressUiState>(VolumeProgressUiState.Loading)
    val uiState: StateFlow<VolumeProgressUiState> = _uiState.asStateFlow()

    init {
        loadVolumeProgress()
    }

    /**
     * Loads volume progress data from the use case
     */
    fun loadVolumeProgress() {
        viewModelScope.launch {
            _uiState.value = VolumeProgressUiState.Loading
            try {
                val volumeProgress = getVolumeProgressUseCase()
                _uiState.value = VolumeProgressUiState.Success(volumeProgress)
            } catch (e: Exception) {
                _uiState.value = VolumeProgressUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Retry loading volume progress data after an error
     */
    fun retry() {
        loadVolumeProgress()
    }
}

/**
 * UI state for the volume progress card
 */
sealed class VolumeProgressUiState {
    object Loading : VolumeProgressUiState()
    data class Success(val volumeProgress: VolumeProgress) : VolumeProgressUiState()
    data class Error(val message: String) : VolumeProgressUiState()
}