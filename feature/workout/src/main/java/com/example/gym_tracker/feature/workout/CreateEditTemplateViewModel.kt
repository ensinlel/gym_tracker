package com.example.gym_tracker.feature.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.data.model.*
import com.example.gym_tracker.core.data.usecase.GetWorkoutTemplatesUseCase
import com.example.gym_tracker.core.data.usecase.ManageWorkoutTemplateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class CreateEditTemplateViewModel @Inject constructor(
    private val getTemplatesUseCase: GetWorkoutTemplatesUseCase,
    private val manageTemplateUseCase: ManageWorkoutTemplateUseCase
) : ViewModel() {
    
    private val _templateId = MutableStateFlow<String?>(null)
    private val _name = MutableStateFlow("")
    private val _description = MutableStateFlow("")
    private val _category = MutableStateFlow(WorkoutCategory.GENERAL)
    private val _difficulty = MutableStateFlow(DifficultyLevel.INTERMEDIATE)
    private val _estimatedDurationMinutes = MutableStateFlow("")
    private val _isPublic = MutableStateFlow(false)
    private val _exercises = MutableStateFlow<List<TemplateExerciseWithDetails>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _isSaved = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    
    val uiState: StateFlow<CreateEditTemplateUiState> = combine(
        _templateId,
        _name,
        _description,
        _category,
        _difficulty,
        _estimatedDurationMinutes,
        _isPublic,
        _exercises,
        _isLoading,
        _isSaved,
        _errorMessage
    ) { flows ->
        val templateId = flows[0] as String?
        val name = flows[1] as String
        val description = flows[2] as String
        val category = flows[3] as WorkoutCategory
        val difficulty = flows[4] as DifficultyLevel
        val estimatedDurationMinutes = flows[5] as String
        val isPublic = flows[6] as Boolean
        val exercises = flows[7] as List<TemplateExerciseWithDetails>
        val isLoading = flows[8] as Boolean
        val isSaved = flows[9] as Boolean
        val errorMessage = flows[10] as String?
        
        CreateEditTemplateUiState(
            templateId = templateId,
            name = name,
            description = description,
            category = category,
            difficulty = difficulty,
            estimatedDurationMinutes = estimatedDurationMinutes,
            isPublic = isPublic,
            exercises = exercises,
            isLoading = isLoading,
            isSaved = isSaved,
            errorMessage = errorMessage,
            canSave = name.isNotBlank() && !isLoading,
            nameError = if (name.isBlank() && name.isNotEmpty()) "Name is required" else null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CreateEditTemplateUiState()
    )
    
    fun loadTemplate(templateId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val templateWithExercises = getTemplatesUseCase.getTemplateWithExercises(templateId)
                
                if (templateWithExercises != null) {
                    _templateId.value = templateId
                    _name.value = templateWithExercises.template.name
                    _description.value = templateWithExercises.template.description
                    _category.value = templateWithExercises.template.category
                    _difficulty.value = templateWithExercises.template.difficulty
                    _estimatedDurationMinutes.value = templateWithExercises.template.estimatedDuration.toMinutes().toString()
                    _isPublic.value = templateWithExercises.template.isPublic
                    _exercises.value = templateWithExercises.exercises
                } else {
                    _errorMessage.value = "Template not found"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load template"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateName(name: String) {
        _name.value = name
    }
    
    fun updateDescription(description: String) {
        _description.value = description
    }
    
    fun updateCategory(category: WorkoutCategory) {
        _category.value = category
    }
    
    fun updateDifficulty(difficulty: DifficultyLevel) {
        _difficulty.value = difficulty
    }
    
    fun updateEstimatedDuration(minutes: String) {
        _estimatedDurationMinutes.value = minutes
    }
    
    fun updateIsPublic(isPublic: Boolean) {
        _isPublic.value = isPublic
    }
    
    fun removeExercise(exerciseId: String) {
        viewModelScope.launch {
            try {
                val result = manageTemplateUseCase.removeExerciseFromTemplate(exerciseId)
                if (result.isSuccess) {
                    // Reload exercises
                    _templateId.value?.let { templateId ->
                        val templateWithExercises = getTemplatesUseCase.getTemplateWithExercises(templateId)
                        templateWithExercises?.let {
                            _exercises.value = it.exercises
                        }
                    }
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to remove exercise"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to remove exercise"
            }
        }
    }
    
    fun moveExerciseUp(exerciseId: String) {
        val currentExercises = _exercises.value.toMutableList()
        val index = currentExercises.indexOfFirst { it.templateExercise.id == exerciseId }
        
        if (index > 0) {
            val exercise = currentExercises.removeAt(index)
            currentExercises.add(index - 1, exercise)
            
            // Update order in database
            updateExerciseOrder(currentExercises)
        }
    }
    
    fun moveExerciseDown(exerciseId: String) {
        val currentExercises = _exercises.value.toMutableList()
        val index = currentExercises.indexOfFirst { it.templateExercise.id == exerciseId }
        
        if (index < currentExercises.size - 1) {
            val exercise = currentExercises.removeAt(index)
            currentExercises.add(index + 1, exercise)
            
            // Update order in database
            updateExerciseOrder(currentExercises)
        }
    }
    
    private fun updateExerciseOrder(exercises: List<TemplateExerciseWithDetails>) {
        viewModelScope.launch {
            try {
                val exerciseIds = exercises.map { it.templateExercise.id }
                _templateId.value?.let { templateId ->
                    val result = manageTemplateUseCase.reorderExercises(templateId, exerciseIds)
                    if (result.isSuccess) {
                        _exercises.value = exercises
                    } else {
                        _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to reorder exercises"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to reorder exercises"
            }
        }
    }
    
    fun saveTemplate() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val estimatedDuration = _estimatedDurationMinutes.value.toLongOrNull()?.let {
                    Duration.ofMinutes(it)
                } ?: Duration.ZERO
                
                val result = if (_templateId.value != null) {
                    // Update existing template
                    val template = getTemplatesUseCase.getTemplateById(_templateId.value!!)
                    if (template != null) {
                        val updatedTemplate = template.copy(
                            name = _name.value,
                            description = _description.value,
                            category = _category.value,
                            difficulty = _difficulty.value,
                            estimatedDuration = estimatedDuration,
                            isPublic = _isPublic.value
                        )
                        manageTemplateUseCase.updateTemplate(updatedTemplate)
                    } else {
                        Result.failure(IllegalStateException("Template not found"))
                    }
                } else {
                    // Create new template
                    manageTemplateUseCase.createTemplate(
                        name = _name.value,
                        description = _description.value,
                        category = _category.value,
                        difficulty = _difficulty.value,
                        estimatedDuration = estimatedDuration,
                        isPublic = _isPublic.value,
                        createdBy = "current_user" // In a real app, get from user session
                    ).also { result ->
                        if (result.isSuccess) {
                            _templateId.value = result.getOrNull()?.id
                        }
                    }
                }
                
                if (result.isSuccess) {
                    _isSaved.value = true
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to save template"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to save template"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}

data class CreateEditTemplateUiState(
    val templateId: String? = null,
    val name: String = "",
    val description: String = "",
    val category: WorkoutCategory = WorkoutCategory.GENERAL,
    val difficulty: DifficultyLevel = DifficultyLevel.INTERMEDIATE,
    val estimatedDurationMinutes: String = "",
    val isPublic: Boolean = false,
    val exercises: List<TemplateExerciseWithDetails> = emptyList(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    val canSave: Boolean = false,
    val nameError: String? = null
)