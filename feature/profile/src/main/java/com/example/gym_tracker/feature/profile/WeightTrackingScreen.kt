package com.example.gym_tracker.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gym_tracker.core.ui.theme.GymTrackerTheme
import java.time.format.DateTimeFormatter

/**
 * Screen for tracking and viewing weight history
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightTrackingScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: WeightTrackingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddWeightDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Weight Tracking",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddWeightDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add weight entry"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            when (val state = uiState) {
                is WeightTrackingUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                is WeightTrackingUiState.Success -> {
                    if (state.weightHistory.isEmpty()) {
                        // Empty state
                        EmptyWeightHistoryState(
                            onAddWeight = { showAddWeightDialog = true },
                            onSeedSampleData = { viewModel.seedSampleWeightData() }
                        )
                    } else {
                        // Weight history list
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            // Add sample data button at the top if we have few entries
                            if (state.weightHistory.size < 5) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "Want to test weight trends?",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Add sample weight data (70-80kg over 40 days) to see trend arrows and analytics in the dashboard.",
                                                style = MaterialTheme.typography.bodySmall,
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            OutlinedButton(
                                                onClick = { viewModel.seedSampleWeightData() }
                                            ) {
                                                Text("Add Sample Data")
                                            }
                                        }
                                    }
                                }
                            }
                            
                            items(state.weightHistory) { weightEntry ->
                                WeightHistoryItem(
                                    weight = weightEntry.weight,
                                    date = weightEntry.recordedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                                    notes = weightEntry.notes
                                )
                            }
                        }
                    }
                }
                
                is WeightTrackingUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Error loading weight history",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            
                            Button(
                                onClick = { viewModel.loadWeightHistory() }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Add weight dialog
    if (showAddWeightDialog) {
        AddWeightDialog(
            onDismiss = { showAddWeightDialog = false },
            onAddWeight = { weight, notes ->
                viewModel.addWeightEntry(weight, notes)
                showAddWeightDialog = false
            }
        )
    }
}

@Composable
private fun EmptyWeightHistoryState(
    onAddWeight: () -> Unit,
    onSeedSampleData: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📊",
            style = MaterialTheme.typography.displayLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Start Tracking Your Weight",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Track your weight progress over time to see trends and stay motivated on your fitness journey.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onAddWeight,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add First Entry")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = onSeedSampleData,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Add Sample Data (70-80kg)")
        }
    }
}

@Composable
private fun WeightHistoryItem(
    weight: Double,
    date: String,
    notes: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${weight} kg",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (notes.isNotBlank()) {
                    Text(
                        text = notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddWeightDialog(
    onDismiss: () -> Unit,
    onAddWeight: (Double, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var weightText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Weight Entry")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { 
                        weightText = it
                        isError = false
                    },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = isError,
                    supportingText = if (isError) {
                        { Text("Please enter a valid weight") }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val weight = weightText.toDoubleOrNull()
                    if (weight != null && weight > 0) {
                        onAddWeight(weight, notes.trim())
                    } else {
                        isError = true
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun WeightTrackingScreenPreview() {
    GymTrackerTheme(darkTheme = true) {
        WeightTrackingScreen()
    }
}