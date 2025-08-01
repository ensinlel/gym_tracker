package com.example.gym_tracker.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.gym_tracker.core.data.model.Goal
import com.example.gym_tracker.core.data.model.GoalType
import com.example.gym_tracker.core.data.model.Exercise
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

/**
 * Dialog for creating a new fitness goal
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onGoalCreated: () -> Unit,
    onCreateGoal: (Goal) -> Unit,
    availableExercises: List<Exercise> = emptyList(),
    modifier: Modifier = Modifier
) {
    var goalTitle by remember { mutableStateOf("") }
    var goalDescription by remember { mutableStateOf("") }
    var selectedGoalType by remember { mutableStateOf<GoalType?>(null) }
    var targetValue by remember { mutableStateOf("") }
    var targetDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }
    var showExerciseDropdown by remember { mutableStateOf(false) }
    
    val isFormValid = goalTitle.isNotBlank() && 
                     selectedGoalType != null && 
                     targetValue.isNotBlank() && 
                     targetValue.toDoubleOrNull() != null &&
                     targetValue.toDoubleOrNull()!! > 0 &&
                     (selectedGoalType != GoalType.PERSONAL_RECORD || selectedExercise != null)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
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
                        text = "Add New Goal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Goal Title
                    item {
                        OutlinedTextField(
                            value = goalTitle,
                            onValueChange = { goalTitle = it },
                            label = { Text("Goal Title") },
                            placeholder = { Text("e.g., Lose 10kg") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = goalTitle.isBlank()
                        )
                    }
                    
                    // Goal Description
                    item {
                        OutlinedTextField(
                            value = goalDescription,
                            onValueChange = { goalDescription = it },
                            label = { Text("Description (Optional)") },
                            placeholder = { Text("Add more details about your goal...") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                    
                    // Goal Type Selection
                    item {
                        Column {
                            Text(
                                text = "Goal Type",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            GoalType.values().forEach { goalType ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = selectedGoalType == goalType,
                                            onClick = { selectedGoalType = goalType }
                                        )
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedGoalType == goalType,
                                        onClick = { selectedGoalType = goalType }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = goalType.getDisplayName(),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                    
                    // Target Value
                    item {
                        OutlinedTextField(
                            value = targetValue,
                            onValueChange = { targetValue = it },
                            label = { 
                                Text("Target Value (${selectedGoalType?.getDefaultUnit() ?: "units"})") 
                            },
                            placeholder = { Text("e.g., 10") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = targetValue.isNotBlank() && targetValue.toDoubleOrNull() == null
                        )
                    }
                    
                    // Exercise Selection (only for PR goals)
                    if (selectedGoalType == GoalType.PERSONAL_RECORD) {
                        item {
                            ExposedDropdownMenuBox(
                                expanded = showExerciseDropdown,
                                onExpandedChange = { showExerciseDropdown = it }
                            ) {
                                OutlinedTextField(
                                    value = selectedExercise?.name ?: "",
                                    onValueChange = { },
                                    readOnly = true,
                                    label = { Text("Select Exercise") },
                                    placeholder = { Text("Choose exercise for PR goal") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showExerciseDropdown) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    isError = selectedExercise == null
                                )
                                
                                ExposedDropdownMenu(
                                    expanded = showExerciseDropdown,
                                    onDismissRequest = { showExerciseDropdown = false }
                                ) {
                                    availableExercises.forEach { exercise ->
                                        DropdownMenuItem(
                                            text = { Text(exercise.name) },
                                            onClick = {
                                                selectedExercise = exercise
                                                showExerciseDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Target Date (Optional)
                    item {
                        Column {
                            Text(
                                text = "Target Date (Optional)",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showDatePicker = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = targetDate?.toString() ?: "Select Date"
                                    )
                                }
                                
                                if (targetDate != null) {
                                    OutlinedButton(
                                        onClick = { targetDate = null }
                                    ) {
                                        Text("Clear")
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            val goal = Goal(
                                id = UUID.randomUUID().toString(),
                                title = goalTitle.trim(),
                                description = goalDescription.trim(),
                                type = selectedGoalType!!,
                                targetValue = targetValue.toDouble(),
                                currentValue = 0.0,
                                unit = selectedGoalType!!.getDefaultUnit(),
                                targetDate = targetDate,
                                isCompleted = false,
                                completedAt = null,
                                createdAt = Instant.now(),
                                updatedAt = Instant.now(),
                                isActive = true,
                                linkedExerciseId = if (selectedGoalType == GoalType.PERSONAL_RECORD) selectedExercise?.id else null
                            )
                            
                            onCreateGoal(goal)
                            onGoalCreated()
                        },
                        enabled = isFormValid,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Create Goal")
                    }
                }
            }
        }
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        
        DatePickerDialog(
            onDateSelected = { dateMillis ->
                dateMillis?.let {
                    targetDate = LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

/**
 * Simple date picker dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onDateSelected(datePickerState.selectedDateMillis) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}