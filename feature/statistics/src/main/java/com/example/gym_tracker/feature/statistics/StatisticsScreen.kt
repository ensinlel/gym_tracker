package com.example.gym_tracker.feature.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.collectAsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Main statistics screen that displays various analytics cards and charts
 * using modern Jetpack Compose and Vico charts
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Statistics",
                        style = MaterialTheme.typography.headlineMedium
                    ) 
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Volume Progress Chart Card
            item {
                VolumeProgressChartCard(
                    onViewDetailsClick = { /* TODO: Navigate to detailed view */ }
                )
            }
            
            // Monthly Stats Card
            item {
                MonthlyStatsCardContainer()
            }
            
            // Personal Records Card
            item {
                PersonalRecordsCardContainer()
            }
            
            // Workout Streak Card
            item {
                StreakCardContainer()
            }
            
            // Calendar Card
            item {
                CalendarCardContainer()
            }
            
            // Dashboard Analytics Card
            item {
                DashboardAnalyticsContainer()
            }
        }
    }
}

/**
 * ViewModel for the main statistics screen
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()
    
}

/**
 * UI state for the statistics screen
 */
data class StatisticsUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)