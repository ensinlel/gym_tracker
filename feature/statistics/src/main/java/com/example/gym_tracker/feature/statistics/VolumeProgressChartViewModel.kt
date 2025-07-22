package com.example.gym_tracker.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.data.usecase.GetVolumeProgressUseCase
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the VolumeProgressChartCard component
 * Handles data loading and chart data preparation for Vico charts
 */
@HiltViewModel
class VolumeProgressChartViewModel @Inject constructor(
    private val getVolumeProgressUseCase: GetVolumeProgressUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<VolumeProgressChartUiState>(VolumeProgressChartUiState.Loading)
    val uiState: StateFlow<VolumeProgressChartUiState> = _uiState.asStateFlow()

    init {
        loadVolumeProgressChart()
    }

    /**
     * Loads volume progress data and prepares chart data
     */
    fun loadVolumeProgressChart() {
        viewModelScope.launch {
            _uiState.value = VolumeProgressChartUiState.Loading
            try {
                val volumeProgress = getVolumeProgressUseCase()
                
                // Create chart data for Vico
                val chartData = ChartEntryModelProducer()
                
                // Generate sample data points for the chart
                // In a real implementation, this would come from historical data
                val chartEntries = generateVolumeChartData(
                    volumeProgress.totalVolumePreviousMonth,
                    volumeProgress.totalVolumeThisMonth
                )
                
                chartData.setEntries(chartEntries)
                
                _uiState.value = VolumeProgressChartUiState.Success(
                    chartData = chartData,
                    currentVolume = volumeProgress.totalVolumeThisMonth,
                    previousVolume = volumeProgress.totalVolumePreviousMonth,
                    percentageChange = volumeProgress.percentageChange,
                    trendDirection = volumeProgress.trendDirection.name
                )
            } catch (e: Exception) {
                _uiState.value = VolumeProgressChartUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Retry loading volume progress data after an error
     */
    fun retry() {
        loadVolumeProgressChart()
    }

    /**
     * Generates chart data points for volume progression
     * This creates a simple progression from previous month to current month
     * In a real implementation, this would use actual daily/weekly data points
     */
    private fun generateVolumeChartData(
        previousVolume: Double,
        currentVolume: Double
    ): List<com.patrykandpatrick.vico.core.entry.ChartEntry> {
        // Generate 8 data points showing progression over time
        val dataPoints = mutableListOf<com.patrykandpatrick.vico.core.entry.ChartEntry>()
        
        // If we have no data, show a flat line at 0
        if (previousVolume == 0.0 && currentVolume == 0.0) {
            repeat(8) { index ->
                dataPoints.add(entryOf(index.toFloat(), 0f))
            }
            return dataPoints
        }
        
        // Create a progression from previous to current volume
        val volumeDifference = currentVolume - previousVolume
        val stepSize = volumeDifference / 7 // 7 steps between 8 points
        
        repeat(8) { index ->
            val volume = previousVolume + (stepSize * index)
            dataPoints.add(entryOf(index.toFloat(), volume.toFloat()))
        }
        
        return dataPoints
    }
}

/**
 * UI state for the volume progress chart
 */
sealed class VolumeProgressChartUiState {
    object Loading : VolumeProgressChartUiState()
    
    data class Success(
        val chartData: ChartEntryModelProducer,
        val currentVolume: Double,
        val previousVolume: Double,
        val percentageChange: Double,
        val trendDirection: String
    ) : VolumeProgressChartUiState()
    
    data class Error(val message: String) : VolumeProgressChartUiState()
}