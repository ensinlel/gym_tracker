package com.example.gym_tracker.feature.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
// No special imports needed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
// import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gym_tracker.core.data.model.Workout
import com.example.gym_tracker.core.data.model.WorkoutWithDetails
import java.time.format.DateTimeFormatter

/**
 * Workout Management Screen - Task 4.2 Implementation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    onNavigateToExerciseTracking: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Create workout dialog
    if (uiState.showCreateDialog) {
        CreateWorkoutDialog(
            onDismiss = { viewModel.hideCreateDialog() },
            onConfirm = { name -> viewModel.createNewWorkout(name) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Workouts",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleViewMode() }) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Toggle view mode"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.showCreateDialog() },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Workout") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                uiState.workouts.isEmpty() -> {
                    EmptyWorkoutsState(
                        onCreateWorkout = { viewModel.showCreateDialog() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                else -> {
                    if (uiState.isGridView) {
                        WorkoutGrid(
                            workouts = uiState.workouts,
                            onWorkoutClick = { workout -> 
                                // Navigate to workout exercises screen
                                onNavigateToExerciseTracking(workout.workout.id)
                            },
                            onDeleteWorkout = { viewModel.deleteWorkout(it) }
                        )
                    } else {
                        WorkoutList(
                            workouts = uiState.workouts,
                            onWorkoutClick = { workout -> 
                                onNavigateToExerciseTracking(workout.workout.id)
                            },
                            onDeleteWorkout = { viewModel.deleteWorkout(it) }
                        )
                    }
                }
            }
        }
    }

    // Error handling
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or handle error
            viewModel.clearError()
        }
    }
}

@Composable
private fun WorkoutList(
    workouts: List<WorkoutWithDetails>,
    onWorkoutClick: (WorkoutWithDetails) -> Unit,
    onDeleteWorkout: (Workout) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(workouts) { workoutWithDetails ->
            WorkoutCard(
                workoutWithDetails = workoutWithDetails,
                onClick = { onWorkoutClick(workoutWithDetails) },
                onDelete = { onDeleteWorkout(workoutWithDetails.workout) }
            )
        }
    }
}

@Composable
private fun WorkoutGrid(
    workouts: List<WorkoutWithDetails>,
    onWorkoutClick: (WorkoutWithDetails) -> Unit,
    onDeleteWorkout: (Workout) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(workouts) { workoutWithDetails ->
            WorkoutCard(
                workoutWithDetails = workoutWithDetails,
                onClick = { onWorkoutClick(workoutWithDetails) },
                onDelete = { onDeleteWorkout(workoutWithDetails.workout) },
                isCompact = true
            )
        }
    }
}

@Composable
private fun WorkoutCard(
    workoutWithDetails: WorkoutWithDetails,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    isCompact: Boolean = false,
    modifier: Modifier = Modifier
) {
    val workout = workoutWithDetails.workout
    val exerciseCount = workoutWithDetails.exerciseInstances.size
    
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = workout.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = workout.startTime.atZone(java.time.ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("MMM dd, yyyy • HH:mm")),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete workout",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            if (!isCompact) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WorkoutStat(
                        label = "Exercises",
                        value = exerciseCount.toString()
                    )
                    
                    WorkoutStat(
                        label = "Volume",
                        value = "${(workout.totalVolume * 0.453592).toInt()} kg"
                    )
                    
                    workout.rating?.let { rating ->
                        WorkoutStat(
                            label = "Rating",
                            value = "★".repeat(rating)
                        )
                    }
                }
                
                if (workout.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = workout.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$exerciseCount exercises",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WorkoutStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyWorkoutsState(
    onCreateWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No workouts yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Start your fitness journey by creating your first workout",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onCreateWorkout) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create Workout")
        }
    }
}

@Composable
private fun CreateWorkoutDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var workoutName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Workout") },
        text = {
            Column {
                Text("Enter a name for your workout:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = workoutName,
                    onValueChange = { workoutName = it },
                    label = { Text("Workout Name") },
                    placeholder = { Text("e.g., Push Day, Leg Day") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    if (workoutName.isNotBlank()) {
                        onConfirm(workoutName.trim())
                    }
                },
                enabled = workoutName.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}