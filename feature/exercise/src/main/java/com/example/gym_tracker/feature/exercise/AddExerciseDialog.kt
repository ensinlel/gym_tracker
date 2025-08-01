package com.example.gym_tracker.feature.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.gym_tracker.core.common.enums.ExerciseCategory
import com.example.gym_tracker.core.common.enums.MuscleGroup
import com.example.gym_tracker.core.common.enums.Equipment

/**
 * Dialog for creating a new exercise
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseDialog(
    onDismiss: () -> Unit,
    onExerciseCreated: () -> Unit,
    onCreateExercise: (String, ExerciseCategory, List<MuscleGroup>, Equipment) -> Unit,
    modifier: Modifier = Modifier
) {
    var exerciseName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExerciseCategory?>(null) }
    var selectedMuscleGroups by remember { mutableStateOf(setOf<MuscleGroup>()) }
    var selectedEquipmentType by remember { mutableStateOf<String?>(null) } // "Machine" or "Freeweight"
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showMuscleGroupSelection by remember { mutableStateOf(false) }
    
    val isFormValid = exerciseName.isNotBlank() && 
                     selectedCategory != null && 
                     selectedMuscleGroups.isNotEmpty() && 
                     selectedEquipmentType != null

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
                        text = "Add New Exercise",
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
                    // Exercise Name
                    item {
                        OutlinedTextField(
                            value = exerciseName,
                            onValueChange = { exerciseName = it },
                            label = { Text("Exercise Name") },
                            placeholder = { Text("e.g., Bench Press") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = exerciseName.isBlank()
                        )
                    }
                    
                    // Category Selection
                    item {
                        ExposedDropdownMenuBox(
                            expanded = showCategoryDropdown,
                            onExpandedChange = { showCategoryDropdown = it }
                        ) {
                            OutlinedTextField(
                                value = selectedCategory?.name?.replace("_", " ")?.lowercase()
                                    ?.replaceFirstChar { it.uppercase() } ?: "",
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Category") },
                                placeholder = { Text("Select category") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                isError = selectedCategory == null
                            )
                            
                            ExposedDropdownMenu(
                                expanded = showCategoryDropdown,
                                onDismissRequest = { showCategoryDropdown = false }
                            ) {
                                ExerciseCategory.values().forEach { category ->
                                    DropdownMenuItem(
                                        text = { 
                                            Text(category.name.replace("_", " ").lowercase()
                                                .replaceFirstChar { it.uppercase() }) 
                                        },
                                        onClick = {
                                            selectedCategory = category
                                            showCategoryDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    // Muscle Groups Selection
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Muscle Groups",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                
                                TextButton(
                                    onClick = { showMuscleGroupSelection = !showMuscleGroupSelection }
                                ) {
                                    Text(if (showMuscleGroupSelection) "Hide" else "Select")
                                }
                            }
                            
                            if (selectedMuscleGroups.isNotEmpty()) {
                                Text(
                                    text = "Selected: ${selectedMuscleGroups.joinToString(", ") { 
                                        it.name.replace("_", " ").lowercase().replaceFirstChar { char -> char.uppercase() }
                                    }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Text(
                                    text = "No muscle groups selected",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                    
                    // Muscle Group Selection Grid
                    if (showMuscleGroupSelection) {
                        val muscleGroupChunks = MuscleGroup.values().toList().chunked(2)
                        items(muscleGroupChunks) { rowMuscleGroups ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowMuscleGroups.forEach { muscleGroup ->
                                    FilterChip(
                                        onClick = {
                                            selectedMuscleGroups = if (selectedMuscleGroups.contains(muscleGroup)) {
                                                selectedMuscleGroups - muscleGroup
                                            } else {
                                                selectedMuscleGroups + muscleGroup
                                            }
                                        },
                                        label = { 
                                            Text(
                                                text = muscleGroup.name.replace("_", " ").lowercase()
                                                    .replaceFirstChar { it.uppercase() },
                                                style = MaterialTheme.typography.bodySmall
                                            ) 
                                        },
                                        selected = selectedMuscleGroups.contains(muscleGroup),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                
                                // Add empty space if odd number of items
                                if (rowMuscleGroups.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                    
                    // Equipment Type Selection
                    item {
                        Column {
                            Text(
                                text = "Equipment Type",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Machine option
                                Row(
                                    modifier = Modifier
                                        .selectable(
                                            selected = selectedEquipmentType == "Machine",
                                            onClick = { selectedEquipmentType = "Machine" }
                                        )
                                        .weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedEquipmentType == "Machine",
                                        onClick = { selectedEquipmentType = "Machine" }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Machine")
                                }
                                
                                // Freeweight option
                                Row(
                                    modifier = Modifier
                                        .selectable(
                                            selected = selectedEquipmentType == "Freeweight",
                                            onClick = { selectedEquipmentType = "Freeweight" }
                                        )
                                        .weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedEquipmentType == "Freeweight",
                                        onClick = { selectedEquipmentType = "Freeweight" }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Freeweight")
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
                            val equipment = when (selectedEquipmentType) {
                                "Machine" -> Equipment.MACHINE
                                "Freeweight" -> Equipment.BARBELL // Default to barbell for freeweight
                                else -> Equipment.OTHER
                            }
                            
                            onCreateExercise(
                                exerciseName.trim(),
                                selectedCategory!!,
                                selectedMuscleGroups.toList(),
                                equipment
                            )
                            onExerciseCreated()
                        },
                        enabled = isFormValid,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Create Exercise")
                    }
                }
            }
        }
    }
}