package com.example.gym_tracker.feature.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
// No special imports needed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gym_tracker.core.data.model.Workout

/**
 * Dialog components for workout management
 */

@Composable
fun CreateWorkoutDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var workoutName by remember { mutableStateOf("") }
    var workoutNotes by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create New Workout")
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = workoutName,
                    onValueChange = { workoutName = it },
                    label = { Text("Workout Name") },
                    placeholder = { Text("e.g., Push Day, Leg Day, Full Body") },
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = workoutNotes,
                    onValueChange = { workoutNotes = it },
                    label = { Text("Notes (Optional)") },
                    placeholder = { Text("Any notes about this workout...") },
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (workoutName.isNotBlank()) {
                        onConfirm(workoutName.trim(), workoutNotes.trim())
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
        },
        modifier = modifier
    )
}

@Composable
fun EditWorkoutDialog(
    workout: Workout,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var workoutName by remember { mutableStateOf(workout.name) }
    var workoutNotes by remember { mutableStateOf(workout.notes) }
    var workoutRating by remember { mutableStateOf(workout.rating?.toString() ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Workout")
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = workoutName,
                    onValueChange = { workoutName = it },
                    label = { Text("Workout Name") },
                    leadingIcon = {
                        Icon(Icons.Default.Favorite, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = workoutNotes,
                    onValueChange = { workoutNotes = it },
                    label = { Text("Notes") },
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = workoutRating,
                    onValueChange = { 
                        // Only allow numbers 1-5
                        if (it.isEmpty() || (it.toIntOrNull()?.let { rating -> rating in 1..5 } == true)) {
                            workoutRating = it
                        }
                    },
                    label = { Text("Rating (1-5)") },
                    placeholder = { Text("Rate your workout") },
                    leadingIcon = {
                        Icon(Icons.Default.Star, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (workoutName.isNotBlank()) {
                        val rating = workoutRating.toIntOrNull()
                        onConfirm(workoutName.trim(), workoutNotes.trim(), rating)
                    }
                },
                enabled = workoutName.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = modifier
    )
}

@Composable
fun DeleteWorkoutDialog(
    workoutName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text("Delete Workout") },
        text = {
            Text("Are you sure you want to delete \"$workoutName\"? This action cannot be undone.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = modifier
    )
}

@Composable
fun FinishWorkoutDialog(
    workoutName: String,
    exerciseCount: Int,
    totalVolume: Double,
    onDismiss: () -> Unit,
    onFinish: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var rating by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = { Text("Finish Workout") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Great job completing \"$workoutName\"!")
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Exercises:")
                            Text(exerciseCount.toString())
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Volume:")
                            Text("${totalVolume.toInt()} lbs")
                        }
                    }
                }
                
                OutlinedTextField(
                    value = rating,
                    onValueChange = { 
                        if (it.isEmpty() || (it.toIntOrNull()?.let { r -> r in 1..5 } == true)) {
                            rating = it
                        }
                    },
                    label = { Text("Rate this workout (1-5)") },
                    placeholder = { Text("How did it go?") },
                    leadingIcon = {
                        Icon(Icons.Default.Star, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val workoutRating = rating.toIntOrNull()
                    onFinish(workoutRating)
                }
            ) {
                Text("Finish")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Continue")
            }
        },
        modifier = modifier
    )
}