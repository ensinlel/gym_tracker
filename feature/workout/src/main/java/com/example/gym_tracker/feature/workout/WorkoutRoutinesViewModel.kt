package com.example.gym_tracker.feature.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.data.model.WorkoutRoutineWithDetails
import com.example.gym_tracker.core.data.repository.WorkoutRoutineRepository
import com.example.gym_tracker.core.data.usecase.ManageWorkoutRoutineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutRoutinesViewModel @Inject constructor(
    private val routineRepository: WorkoutRoutineRepository,
    private val manageRoutineUseCase: ManageWorkoutRoutineUseCase
) : ViewModel() {
    
    private val _filter = MutableStateFlow(WorkoutRoutinesFilter.ALL)
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    
    val uiState: StateFlow<WorkoutRoutinesUiState> = combine(
        _filter,
        _isLoading,
        _errorMessage,
        getFilteredRoutines()
    ) { filter, isLoading, errorMessage, routines ->
        WorkoutRoutinesUiState(
            filter = filter,
            routines = routines,
            isLoading = isLoading,
            errorMessage = errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WorkoutRoutinesUiState()
    )
    
    private fun getFilteredRoutines(): Flow<List<WorkoutRoutineWithDetails>> {
        return _filter.flatMapLatest { filter ->
            when (filter) {
                WorkoutRoutinesFilter.ACTIVE -> routineRepository.getActiveRoutinesWithDetails()
                WorkoutRoutinesFilter.INACTIVE -> routineRepository.getAllRoutinesWithDetails()
                    .map { routines -> routines.filter { !it.routine.isActive } }
                WorkoutRoutinesFilter.ALL -> routineRepository.getAllRoutinesWithDetails()
            }
        }
    }
    
    fun updateFilter(filter: WorkoutRoutinesFilter) {
        _filter.value = filter
    }
    
    fun toggleRoutineActive(routineId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val routine = routineRepository.getRoutineById(routineId)
                if (routine != null) {
                    val result = manageRoutineUseCase.setRoutineActive(routineId, !routine.isActive)
                    
                    if (result.isFailure) {
                        _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to update routine"
                    }
                } else {
                    _errorMessage.value = "Routine not found"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to update routine"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun duplicateRoutine(routineId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = manageRoutineUseCase.duplicateRoutine(routineId)
                
                if (result.isFailure) {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to duplicate routine"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to duplicate routine"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteRoutine(routineId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = manageRoutineUseCase.deleteRoutine(routineId)
                
                if (result.isFailure) {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to delete routine"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to delete routine"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}

data class WorkoutRoutinesUiState(
    val filter: WorkoutRoutinesFilter = WorkoutRoutinesFilter.ALL,
    val routines: List<WorkoutRoutineWithDetails> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

enum class WorkoutRoutinesFilter {
    ALL, ACTIVE, INACTIVE
}