package com.example.gym_tracker.feature.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
// import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.outlined.*
import com.example.gym_tracker.core.data.model.ExerciseInstanceWithDetails
import com.example.gym_tracker.core.data.model.ExerciseSet

/**
 * Exercise Tracking Screen - Task 4.3 Implementation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseTrackingScreen(
    exerciseInstanceWithDetails: ExerciseInstanceWithDetails,
    onNavigateBack: () -> Unit,
    viewModel: ExerciseTrackingViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(exerciseInstanceWithDetails) {
        viewModel.loadExerciseInstance(exerciseInstanceWithDetails)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = uiState.exerciseInstanceWithDetails?.exercise?.name ?: "Exercise",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.addSet() }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Set")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.addSet() },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Set") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Exercise Info Card
            ExerciseInfoCard(
                exerciseInstanceWithDetails = uiState.exerciseInstanceWithDetails,
                modifier = Modifier.padding(16.dp)
            )

            // Quick Add Buttons
            QuickAddButtons(
                onQuickAdd = { weight, reps -> viewModel.applyQuickAdd(weight, reps) },
                modifier = Modifier.padding(16.dp)
            )

            // Sets List
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SetHeaderRow()
                }
                
                items(uiState.sets) { set ->
                    SetRow(
                        set = set,
                        onUpdateSet = { viewModel.updateSet(it) },
                        onDeleteSet = { viewModel.deleteSet(it) }
                    )
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
private fun ExerciseInfoCard(
    exerciseInstanceWithDetails: ExerciseInstanceWithDetails?,
    modifier: Modifier = Modifier
) {
    exerciseInstanceWithDetails?.let { details ->
        Card(
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = details.exercise.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Muscle Groups: ${details.exercise.muscleGroups.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "Equipment: ${details.exercise.equipment}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (details.exercise.instructions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Instructions:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    details.exercise.instructions.forEach { instruction ->
                        Text(
                            text = "• $instruction",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}



@Composable
private fun QuickAddButtons(
    onQuickAdd: (Double, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Quick Add",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Common weight/rep combinations (converted to kg)
                QuickAddButton(
                    text = "60×5",
                    onClick = { onQuickAdd(60.0, 5) },
                    modifier = Modifier.weight(1f)
                )
                QuickAddButton(
                    text = "80×3",
                    onClick = { onQuickAdd(80.0, 3) },
                    modifier = Modifier.weight(1f)
                )
                QuickAddButton(
                    text = "100×1",
                    onClick = { onQuickAdd(100.0, 1) },
                    modifier = Modifier.weight(1f)
                )
                QuickAddButton(
                    text = "BW×10",
                    onClick = { onQuickAdd(0.0, 10) }, // Bodyweight
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickAddButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SetHeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Set",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(40.dp)
        )
        Text(
            text = "Weight",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Reps",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "RPE",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(60.dp)
        )
        Spacer(modifier = Modifier.width(48.dp)) // Space for actions
    }
}

@Composable
private fun SetRow(
    set: ExerciseSet,
    onUpdateSet: (ExerciseSet) -> Unit,
    onDeleteSet: (ExerciseSet) -> Unit,
    modifier: Modifier = Modifier
) {
    var weight by remember(set.id) { mutableStateOf(set.weight.toString()) }
    var reps by remember(set.id) { mutableStateOf(set.reps.toString()) }
    var rpe by remember(set.id) { mutableStateOf(set.rpe?.toString() ?: "") }

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Set number
            Text(
                text = set.setNumber.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(40.dp)
            )

            // Weight input
            OutlinedTextField(
                value = weight,
                onValueChange = { 
                    weight = it
                    it.toDoubleOrNull()?.let { weightValue ->
                        onUpdateSet(set.copy(weight = weightValue))
                    }
                },
                label = { Text("kg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )

            // Reps input
            OutlinedTextField(
                value = reps,
                onValueChange = { 
                    reps = it
                    it.toIntOrNull()?.let { repsValue ->
                        onUpdateSet(set.copy(reps = repsValue))
                    }
                },
                label = { Text("reps") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            // RPE input
            OutlinedTextField(
                value = rpe,
                onValueChange = { 
                    rpe = it
                    val rpeValue = it.toIntOrNull()
                    onUpdateSet(set.copy(rpe = rpeValue))
                },
                label = { Text("RPE") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(60.dp)
            )

            // Actions
            IconButton(
                onClick = { onDeleteSet(set) }
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Set")
            }
        }
    }
}