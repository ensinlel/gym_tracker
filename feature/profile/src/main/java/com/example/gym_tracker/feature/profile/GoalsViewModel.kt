package com.example.gym_tracker.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.data.model.Goal
import com.example.gym_tracker.core.data.model.Exercise
import com.example.gym_tracker.core.data.repository.GoalRepository
import com.example.gym_tracker.core.data.repository.ExerciseRepository
import com.example.gym_tracker.core.data.service.GoalProgressService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Goals screen
 */
@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val exerciseRepository: ExerciseRepository,
    private val goalProgressService: GoalProgressService
) : ViewModel() {

    private val _uiState = MutableStateFlow<GoalsUiState>(GoalsUiState.Loading)
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()
    
    private val _availableExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val availableExercises: StateFlow<List<Exercise>> = _availableExercises.asStateFlow()

    init {
        loadGoals()
        loadExercises()
        updateAllPRGoals()
    }

    /**
     * Load all goals from the repository
     */
    fun loadGoals() {
        viewModelScope.launch {
            _uiState.value = GoalsUiState.Loading
            try {
                combine(
                    goalRepository.getActiveGoals(),
                    goalRepository.getCompletedGoals()
                ) { activeGoals, completedGoals ->
                    GoalsUiState.Success(
                        activeGoals = activeGoals,
                        completedGoals = completedGoals
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.value = GoalsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Create a new goal
     */
    fun createGoal(goal: Goal) {
        viewModelScope.launch {
            try {
                goalRepository.createGoal(goal)
                // Goals will be automatically updated through the Flow
            } catch (e: Exception) {
                _uiState.value = GoalsUiState.Error("Failed to create goal: ${e.message}")
            }
        }
    }

    /**
     * Update goal progress
     */
    fun updateGoalProgress(goalId: String, currentValue: Double) {
        viewModelScope.launch {
            try {
                goalRepository.updateGoalProgress(goalId, currentValue)
                // Check if goal should be marked as completed
                val goal = goalRepository.getGoalById(goalId)
                if (goal != null && currentValue >= goal.targetValue && !goal.isCompleted) {
                    goalRepository.markGoalCompleted(goalId)
                }
            } catch (e: Exception) {
                _uiState.value = GoalsUiState.Error("Failed to update progress: ${e.message}")
            }
        }
    }

    /**
     * Mark a goal as completed
     */
    fun markGoalCompleted(goalId: String) {
        viewModelScope.launch {
            try {
                goalRepository.markGoalCompleted(goalId)
            } catch (e: Exception) {
                _uiState.value = GoalsUiState.Error("Failed to complete goal: ${e.message}")
            }
        }
    }

    /**
     * Deactivate a goal (soft delete)
     */
    fun deactivateGoal(goalId: String) {
        viewModelScope.launch {
            try {
                goalRepository.deactivateGoal(goalId)
            } catch (e: Exception) {
                _uiState.value = GoalsUiState.Error("Failed to deactivate goal: ${e.message}")
            }
        }
    }
    
    /**
     * Load available exercises for PR goals
     */
    private fun loadExercises() {
        viewModelScope.launch {
            try {
                exerciseRepository.getAllExercises().collect { exercises ->
                    _availableExercises.value = exercises
                }
            } catch (e: Exception) {
                // Don't update UI state for exercise loading errors
                println("Error loading exercises: ${e.message}")
            }
        }
    }
    
    /**
     * Update all PR goals with current PRs
     */
    private fun updateAllPRGoals() {
        viewModelScope.launch {
            goalProgressService.updateAllPRGoals()
        }
    }
}

/**
 * UI state for the Goals screen
 */
sealed class GoalsUiState {
    object Loading : GoalsUiState()
    data class Success(
        val activeGoals: List<Goal>,
        val completedGoals: List<Goal>
    ) : GoalsUiState()
    data class Error(val message: String) : GoalsUiState()
}