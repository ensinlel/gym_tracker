package com.example.gym_tracker.feature.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.data.model.Goal
import com.example.gym_tracker.core.data.repository.GoalRepository
import com.example.gym_tracker.core.data.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Container for displaying active goals on the dashboard
 */
@Composable
fun GoalsCardContainer(
    onNavigateToGoals: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: GoalsCardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is GoalsCardUiState.Loading -> {
            Card(
                modifier = modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        
        is GoalsCardUiState.Success -> {
            if (state.goals.isNotEmpty()) {
                Card(
                    modifier = modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Active Goals",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            TextButton(onClick = onNavigateToGoals) {
                                Text("View All")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Goals list
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(state.goals.take(3)) { goalWithExercise ->
                                GoalProgressCard(
                                    goal = goalWithExercise.goal,
                                    exerciseName = goalWithExercise.exerciseName,
                                    onClick = onNavigateToGoals,
                                    modifier = Modifier.width(280.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        is GoalsCardUiState.Error -> {
            // Don't show error card on dashboard, just skip
        }
    }
}

/**
 * ViewModel for the goals card on dashboard
 */
@HiltViewModel
class GoalsCardViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<GoalsCardUiState>(GoalsCardUiState.Loading)
    val uiState: StateFlow<GoalsCardUiState> = _uiState.asStateFlow()
    
    init {
        loadActiveGoals()
    }
    
    private fun loadActiveGoals() {
        viewModelScope.launch {
            try {
                combine(
                    goalRepository.getActiveGoals(),
                    exerciseRepository.getAllExercises()
                ) { goals, exercises ->
                    val exerciseMap = exercises.associateBy { it.id }
                    
                    goals.map { goal ->
                        GoalWithExercise(
                            goal = goal,
                            exerciseName = goal.linkedExerciseId?.let { exerciseMap[it]?.name }
                        )
                    }
                }.collect { goalsWithExercises ->
                    _uiState.value = GoalsCardUiState.Success(goalsWithExercises)
                }
            } catch (e: Exception) {
                _uiState.value = GoalsCardUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

/**
 * UI state for goals card
 */
sealed class GoalsCardUiState {
    object Loading : GoalsCardUiState()
    data class Success(val goals: List<GoalWithExercise>) : GoalsCardUiState()
    data class Error(val message: String) : GoalsCardUiState()
}

/**
 * Data class combining goal with exercise name
 */
data class GoalWithExercise(
    val goal: Goal,
    val exerciseName: String?
)

/**
 * Card component for displaying goal progress on the dashboard
 */
@Composable
private fun GoalProgressCard(
    goal: Goal,
    exerciseName: String? = null,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
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
                    
                    if (exerciseName != null) {
                        Text(
                            text = exerciseName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Progress percentage
                Text(
                    text = "${goal.progressPercentage.toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (goal.isCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress details
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
                
                if (goal.isCompleted) {
                    Text(
                        text = "✓ Completed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress bar
            LinearProgressIndicator(
                progress = { (goal.progressPercentage / 100).toFloat() },
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