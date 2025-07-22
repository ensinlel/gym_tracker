package com.example.gym_tracker.feature.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Dialog for adding exercises to a workout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseToWorkoutDialog(
    onDismiss: () -> Unit,
    onExerciseSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Sample exercises - TODO: Replace with actual data
    val allExercises = remember {
        listOf(
            ExerciseData("1", "Bench Press"),
            ExerciseData("2", "Incline Dumbbell Press"),
            ExerciseData("3", "Shoulder Press"),
            ExerciseData("4", "Tricep Dips"),
            ExerciseData("5", "Push-ups"),
            ExerciseData("6", "Chest Flyes"),
            ExerciseData("7", "Lateral Raises"),
            ExerciseData("8", "Front Raises"),
            ExerciseData("9", "Tricep Extensions"),
            ExerciseData("10", "Diamond Push-ups")
        )
    }
    
    val filteredExercises = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            allExercises
        } else {
            allExercises.filter { 
                it.name.contains(searchQuery, ignoreCase = true) 
            }
        }
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
                    
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
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
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredExercises) { exercise ->
                        ExerciseSelectionItem(
                            exercise = exercise,
                            onSelect = { onExerciseSelected(exercise.id) }
                        )
                    }
                    
                    if (filteredExercises.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
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
                            }
                        }
                    }
                }
            }
        }
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