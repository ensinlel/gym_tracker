package com.example.gym_tracker.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gym_tracker.core.data.model.Goal

/**
 * Screen for managing fitness goals
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddGoalDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "My Goals",
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
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddGoalDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Goal") }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        when (val state = uiState) {
            is GoalsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is GoalsUiState.Success -> {
                if (state.activeGoals.isEmpty() && state.completedGoals.isEmpty()) {
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
                                text = "No goals yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Set your first fitness goal to start tracking your progress",
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
                        // Active Goals Section
                        if (state.activeGoals.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Active Goals",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            
                            items(state.activeGoals) { goal ->
                                GoalCard(
                                    goal = goal,
                                    onGoalClick = { /* Navigate to goal details */ },
                                    onMarkCompleted = { viewModel.markGoalCompleted(goal.id) },
                                    onUpdateProgress = { progress -> 
                                        viewModel.updateGoalProgress(goal.id, progress) 
                                    }
                                )
                            }
                        }
                        
                        // Completed Goals Section
                        if (state.completedGoals.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Completed Goals",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            
                            items(state.completedGoals) { goal ->
                                GoalCard(
                                    goal = goal,
                                    onGoalClick = { /* Navigate to goal details */ },
                                    onMarkCompleted = { },
                                    onUpdateProgress = { }
                                )
                            }
                        }
                    }
                }
            }
            
            is GoalsUiState.Error -> {
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
                            text = "Error loading goals",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.loadGoals() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
    
    // Add Goal Dialog
    if (showAddGoalDialog) {
        val availableExercises by viewModel.availableExercises.collectAsState()
        
        AddGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onGoalCreated = { 
                showAddGoalDialog = false
                viewModel.loadGoals()
            },
            onCreateGoal = { goal ->
                viewModel.createGoal(goal)
            },
            availableExercises = availableExercises
        )
    }
}

/**
 * Card displaying a single goal with progress
 */
@Composable
private fun GoalCard(
    goal: Goal,
    onGoalClick: () -> Unit,
    onMarkCompleted: () -> Unit,
    onUpdateProgress: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onGoalClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Goal header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = goal.type.getDisplayName(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                if (!goal.isCompleted) {
                    TextButton(onClick = onMarkCompleted) {
                        Text("Complete")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Goal description
            if (goal.description.isNotBlank()) {
                Text(
                    text = goal.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Progress section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${goal.currentValue}/${goal.targetValue} ${goal.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "${goal.progressPercentage.toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress bar
            LinearProgressIndicator(
                progress = (goal.progressPercentage / 100).toFloat(),
                modifier = Modifier.fillMaxWidth(),
                color = if (goal.isCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
            )
            
            // Target date
            goal.targetDate?.let { targetDate ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Target: $targetDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}