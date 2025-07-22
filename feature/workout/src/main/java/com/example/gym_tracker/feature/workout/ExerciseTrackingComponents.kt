package com.example.gym_tracker.feature.workout

import androidx.compose.animation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gym_tracker.core.data.model.ExerciseSet

/**
 * Reusable components for exercise tracking
 */

@Composable
fun SetInputRow(
    set: ExerciseSet,
    onUpdateSet: (ExerciseSet) -> Unit,
    onDeleteSet: () -> Unit,
    modifier: Modifier = Modifier
) {
    var weight by remember(set.id) { mutableStateOf(set.weight.toString()) }
    var reps by remember(set.id) { mutableStateOf(set.reps.toString()) }
    var rpe by remember(set.id) { mutableStateOf(set.rpe?.toString() ?: "") }
    var isWarmup by remember(set.id) { mutableStateOf(set.isWarmup) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isWarmup) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Set ${set.setNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Warmup",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Switch(
                        checked = isWarmup,
                        onCheckedChange = { 
                            isWarmup = it
                            onUpdateSet(set.copy(isWarmup = it))
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Weight input
                OutlinedTextField(
                    value = weight,
                    onValueChange = { 
                        weight = it
                        it.toDoubleOrNull()?.let { weightValue ->
                            onUpdateSet(set.copy(weight = weightValue))
                        }
                    },
                    label = { Text("Weight") },
                    suffix = { Text("kg") },
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
                    label = { Text("Reps") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                // RPE input
                OutlinedTextField(
                    value = rpe,
                    onValueChange = { 
                        if (it.isEmpty() || (it.toIntOrNull()?.let { r -> r in 1..10 } == true)) {
                            rpe = it
                            val rpeValue = it.toIntOrNull()
                            onUpdateSet(set.copy(rpe = rpeValue))
                        }
                    },
                    label = { Text("RPE") },
                    placeholder = { Text("1-10") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(80.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onDeleteSet,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete set")
                }
            }
        }
    }
}



@Composable
fun QuickAddSection(
    onQuickAdd: (Double, Int) -> Unit,
    recentSets: List<ExerciseSet> = emptyList(),
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
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Recent sets (if available)
            if (recentSets.isNotEmpty()) {
                Text(
                    text = "Recent Sets",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    recentSets.take(3).forEach { set ->
                        OutlinedButton(
                            onClick = { onQuickAdd(set.weight, set.reps) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("${set.weight.toInt()}×${set.reps}")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            // Common combinations
            Text(
                text = "Common",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ExerciseStatsCard(
    exerciseName: String,
    totalSets: Int,
    totalVolume: Double,
    maxWeight: Double,
    averageRpe: Double?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Session Stats",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Sets",
                    value = totalSets.toString()
                )
                
                StatItem(
                    label = "Volume",
                    value = "${totalVolume.toInt()} kg"
                )
                
                StatItem(
                    label = "Max Weight",
                    value = "${maxWeight.toInt()} kg"
                )
                
                averageRpe?.let { rpe ->
                    StatItem(
                        label = "Avg RPE",
                        value = String.format("%.1f", rpe)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
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
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}