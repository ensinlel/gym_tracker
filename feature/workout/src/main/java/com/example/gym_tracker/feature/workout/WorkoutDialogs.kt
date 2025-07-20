package com.example.gym_tracker.feature.workout

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

/**
 * Placeholder for WorkoutDialogs - temporarily commented out for build testing
 */
@Composable
fun CreateWorkoutDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: Implement create workout dialog
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Workout - Under Development") },
        text = { Text("This dialog is under development") },
        confirmButton = {
            TextButton(onClick = { onConfirm("", "") }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}