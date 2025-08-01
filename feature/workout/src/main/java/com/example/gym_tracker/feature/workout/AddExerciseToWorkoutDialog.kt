package com.example.gym_tracker.feature.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gym_tracker.feature.exercise.ExerciseSelectionViewModel
import com.example.gym_tracker.feature.exercise.ExerciseSelectionUiState
import com.example.gym_tracker.feature.exercise.AddExerciseDialog

/**
 * Dialog for adding exercises to a workout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseToWorkoutDialog(
    onDismiss: () -> Unit,
    onExerciseSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExerciseSelectionViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadExercises()
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Exercise",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(onClick = { showAddExerciseDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Create")
                        }
                        
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        viewModel.searchExercises(it)
                    },
                    label = { Text("Search exercises") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Exercise list
                when (val state = uiState) {
                    is ExerciseSelectionUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    
                    is ExerciseSelectionUiState.Success -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.exercises) { exercise ->
                                ExerciseSelectionItem(
                                    exercise = ExerciseData(exercise.id, exercise.name),
                                    onSelect = { onExerciseSelected(exercise.id) }
                                )
                            }
                            
                            if (state.exercises.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = if (searchQuery.isNotEmpty()) {
                                                    "No exercises found matching \"$searchQuery\""
                                                } else {
                                                    "No exercises available"
                                                },
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            
                                            TextButton(
                                                onClick = { showAddExerciseDialog = true }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Create New Exercise")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    is ExerciseSelectionUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Error loading exercises",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                                
                                TextButton(onClick = { viewModel.loadExercises() }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Add Exercise Dialog
    if (showAddExerciseDialog) {
        AddExerciseDialog(
            onDismiss = { showAddExerciseDialog = false },
            onExerciseCreated = { showAddExerciseDialog = false },
            onCreateExercise = { name, category, muscleGroups, equipment ->
                viewModel.createExercise(name, category, muscleGroups, equipment)
            }
        )
    }
}

/**
 * Individual exercise item in the selection dialog
 */
@Composable
private fun ExerciseSelectionItem(
    exercise: ExerciseData,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onSelect,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            FilledTonalButton(
                onClick = onSelect,
                modifier = Modifier.size(width = 80.dp, height = 36.dp)
            ) {
                Text("Add")
            }
        }
    }
}