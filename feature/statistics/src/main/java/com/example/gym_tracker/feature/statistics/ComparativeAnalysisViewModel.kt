package com.example.gym_tracker.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.data.model.ComparativeAnalysisData
import com.example.gym_tracker.core.data.usecase.GetComparativeAnalysisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Comparative Analysis Screen
 * 
 * Manages the state and business logic for displaying comparative analysis features
 * including before/after comparisons, muscle group distribution, and personal records timeline.
 */
@HiltViewModel
class ComparativeAnalysisViewModel @Inject constructor(
    private val getComparativeAnalysisUseCase: GetComparativeAnalysisUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ComparativeAnalysisUiState>(ComparativeAnalysisUiState.Loading)
    val uiState: StateFlow<ComparativeAnalysisUiState> = _uiState.asStateFlow()
    
    /**
     * Loads comparative analysis data
     */
    fun loadComparativeAnalysis() {
        viewModelScope.launch {
            _uiState.value = ComparativeAnalysisUiState.Loading
            
            try {
                val analysisData = getComparativeAnalysisUseCase()
                _uiState.value = ComparativeAnalysisUiState.Success(analysisData)
            } catch (exception: Exception) {
                _uiState.value = ComparativeAnalysisUiState.Error(
                    message = exception.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    /**
     * Refreshes the comparative analysis data
     */
    fun refresh() {
        loadComparativeAnalysis()
    }
}