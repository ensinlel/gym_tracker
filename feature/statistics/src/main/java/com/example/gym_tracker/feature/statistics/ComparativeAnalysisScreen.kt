package com.example.gym_tracker.feature.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gym_tracker.core.data.model.ComparativeAnalysisData

/**
 * Comparative Analysis Screen showing before/after comparisons, muscle group distribution,
 * and personal records timeline
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparativeAnalysisScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ComparativeAnalysisViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadComparativeAnalysis()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Comparative Analysis",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        when (val state = uiState) {
            is ComparativeAnalysisUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Analyzing your progress...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            is ComparativeAnalysisUiState.Success -> {
                ComparativeAnalysisContent(
                    data = state.data,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            
            is ComparativeAnalysisUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error loading analysis",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { viewModel.loadComparativeAnalysis() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Content for comparative analysis screen
 */
@Composable
private fun ComparativeAnalysisContent(
    data: ComparativeAnalysisData,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Before/After Comparisons Section
        if (data.beforeAfterComparisons.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Progress Comparisons",
                    subtitle = "Statistical analysis of your improvement over time"
                )
            }
            
            items(data.beforeAfterComparisons) { comparison ->
                BeforeAfterComparisonChart(comparison = comparison)
            }
        }
        
        // Muscle Group Distribution Section
        if (data.muscleGroupDistribution.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Training Balance",
                    subtitle = "How your training volume is distributed across muscle groups"
                )
            }
            
            item {
                MuscleGroupDistributionChart(distribution = data.muscleGroupDistribution)
            }
        }
        
        // Personal Records Timeline Section
        if (data.personalRecordsTimelines.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Strength Progression",
                    subtitle = "Timeline of your personal records and strength gains"
                )
            }
            
            items(data.personalRecordsTimelines) { timeline ->
                PersonalRecordsTimelineChart(timeline = timeline)
            }
        }
        
        // Empty state if no data
        if (data.beforeAfterComparisons.isEmpty() && 
            data.muscleGroupDistribution.isEmpty() && 
            data.personalRecordsTimelines.isEmpty()) {
            item {
                EmptyAnalysisState()
            }
        }
    }
}

/**
 * Section header component
 */
@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Empty state when no analysis data is available
 */
@Composable
private fun EmptyAnalysisState(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📊",
                style = MaterialTheme.typography.displayMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Not Enough Data Yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Keep logging your workouts to see comparative analysis and progress insights. You'll need at least a few weeks of data for meaningful comparisons.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

/**
 * UI State for Comparative Analysis
 */
sealed class ComparativeAnalysisUiState {
    object Loading : ComparativeAnalysisUiState()
    data class Success(val data: ComparativeAnalysisData) : ComparativeAnalysisUiState()
    data class Error(val message: String) : ComparativeAnalysisUiState()
}