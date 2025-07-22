package com.example.gym_tracker.feature.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState

/**
 * Screen showing exercises within a specific workout
 * Allows adding exercises and tracking sets/reps
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutExercisesScreen(
    workoutId: String,
    onNavigateBack: () -> Unit,
    onNavigateToExerciseStats: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WorkoutExercisesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(workoutId) {
        viewModel.loadWorkoutExercises(workoutId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = when (val state = uiState) {
                            is WorkoutExercisesUiState.Success -> state.workoutName ?: "Workout Exercises"
                            else -> "Workout Exercises"
                        },
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
                },
                actions = {
                    IconButton(onClick = { showAddExerciseDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Exercise"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddExerciseDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Exercise") }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        when (val state = uiState) {
            is WorkoutExercisesUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is WorkoutExercisesUiState.Success -> {
                if (state.exercises.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "No exercises in this workout",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tap the + button to add exercises",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.exercises) { exerciseInstance ->
                            WorkoutExerciseCard(
                                exerciseInstance = exerciseInstance,
                                onExerciseClick = { onNavigateToExerciseStats(exerciseInstance.exercise.id) },
                                onAddSet = { viewModel.addSetToExercise(exerciseInstance.exerciseInstance.id) },
                                onUpdateSet = { setId, weight, reps -> 
                                    viewModel.updateSet(setId, weight, reps) 
                                }
                            )
                        }
                    }
                }
            }
            
            is WorkoutExercisesUiState.Error -> {
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
                            text = "Error loading workout exercises",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.loadWorkoutExercises(workoutId) }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
    
    // Add Exercise Dialog
    if (showAddExerciseDialog) {
        AddExerciseToWorkoutDialog(
            onDismiss = { showAddExerciseDialog = false },
            onExerciseSelected = { exerciseId ->
                viewModel.addExerciseToWorkout(exerciseId)
                showAddExerciseDialog = false
            }
        )
    }
}

/**
 * Card showing an exercise within a workout with set tracking
 */
@Composable
private fun WorkoutExerciseCard(
    exerciseInstance: WorkoutExerciseInstanceData,
    onExerciseClick: () -> Unit,
    onAddSet: () -> Unit,
    onUpdateSet: (String, Double, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Exercise header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = exerciseInstance.exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Last performed data
                    exerciseInstance.lastPerformed?.let { lastPerformed ->
                        Text(
                            text = "Last: ${lastPerformed.weight}kg × ${lastPerformed.reps} reps",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onExerciseClick) {
                        Text("View Stats")
                    }
                    
                    FilledTonalButton(onClick = onAddSet) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Set")
                    }
                }
            }
            
            // Sets list
            if (exerciseInstance.sets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Sets",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                exerciseInstance.sets.forEachIndexed { index, set ->
                    WorkoutSetRow(
                        setNumber = index + 1,
                        set = set,
                        onUpdateSet = { weight, reps ->
                            onUpdateSet(set.id, weight, reps)
                        }
                    )
                    
                    if (index < exerciseInstance.sets.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

/**
 * Row for editing a single set
 */
@Composable
private fun WorkoutSetRow(
    setNumber: Int,
    set: WorkoutSetData,
    onUpdateSet: (Double, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var weight by remember(set.id) { mutableStateOf(set.weight.toString()) }
    var reps by remember(set.id) { mutableStateOf(set.reps.toString()) }
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$setNumber.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(24.dp)
        )
        
        OutlinedTextField(
            value = weight,
            onValueChange = { 
                weight = it
                it.toDoubleOrNull()?.let { w ->
                    reps.toIntOrNull()?.let { r ->
                        onUpdateSet(w, r)
                    }
                }
            },
            label = { Text("Weight (kg)") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        
        OutlinedTextField(
            value = reps,
            onValueChange = { 
                reps = it
                it.toIntOrNull()?.let { r ->
                    weight.toDoubleOrNull()?.let { w ->
                        onUpdateSet(w, r)
                    }
                }
            },
            label = { Text("Reps") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
    }
}

/**
 * Data classes for workout exercises
 */
data class WorkoutExerciseInstanceData(
    val exerciseInstance: ExerciseInstanceData,
    val exercise: ExerciseData,
    val sets: List<WorkoutSetData>,
    val lastPerformed: LastPerformedData?
)

data class ExerciseInstanceData(
    val id: String,
    val exerciseId: String,
    val workoutId: String
)

data class ExerciseData(
    val id: String,
    val name: String
)

data class WorkoutSetData(
    val id: String,
    val weight: Double,
    val reps: Int
)

data class LastPerformedData(
    val weight: Double,
    val reps: Int,
    val date: String
)