package com.example.gym_tracker.feature.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.data.model.WorkoutTemplate
import com.example.gym_tracker.core.data.model.WorkoutCategory
import com.example.gym_tracker.core.data.usecase.GetWorkoutTemplatesUseCase
import com.example.gym_tracker.core.data.usecase.ManageWorkoutTemplateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutTemplatesViewModel @Inject constructor(
    private val getTemplatesUseCase: GetWorkoutTemplatesUseCase,
    private val manageTemplateUseCase: ManageWorkoutTemplateUseCase
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<WorkoutCategory?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    
    val uiState: StateFlow<WorkoutTemplatesUiState> = combine(
        _searchQuery,
        _selectedCategory,
        _isLoading,
        _errorMessage,
        getFilteredTemplates()
    ) { searchQuery, selectedCategory, isLoading, errorMessage, templates ->
        WorkoutTemplatesUiState(
            searchQuery = searchQuery,
            selectedCategory = selectedCategory,
            templates = templates,
            isLoading = isLoading,
            errorMessage = errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WorkoutTemplatesUiState()
    )
    
    private fun getFilteredTemplates(): Flow<List<WorkoutTemplate>> {
        return combine(
            _searchQuery,
            _selectedCategory
        ) { searchQuery, selectedCategory ->
            when {
                searchQuery.isNotEmpty() -> getTemplatesUseCase.searchTemplates(searchQuery)
                selectedCategory != null -> getTemplatesUseCase.getTemplatesByCategory(selectedCategory)
                else -> getTemplatesUseCase.getAllTemplates()
            }
        }.flatMapLatest { it }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun updateCategoryFilter(category: WorkoutCategory?) {
        _selectedCategory.value = category
    }
    
    fun duplicateTemplate(templateId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = manageTemplateUseCase.duplicateTemplate(
                    templateId = templateId,
                    userId = "current_user" // In a real app, get from user session
                )
                
                if (result.isFailure) {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to duplicate template"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to duplicate template"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteTemplate(templateId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = manageTemplateUseCase.deleteTemplate(templateId)
                
                if (result.isFailure) {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to delete template"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to delete template"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}

data class WorkoutTemplatesUiState(
    val searchQuery: String = "",
    val selectedCategory: WorkoutCategory? = null,
    val templates: List<WorkoutTemplate> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)