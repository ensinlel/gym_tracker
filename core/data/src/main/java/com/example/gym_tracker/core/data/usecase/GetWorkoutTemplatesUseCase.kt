package com.example.gym_tracker.core.data.usecase

import com.example.gym_tracker.core.data.model.*
import com.example.gym_tracker.core.data.repository.WorkoutTemplateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for retrieving workout templates with various filtering options
 */
@Singleton
class GetWorkoutTemplatesUseCase @Inject constructor(
    private val templateRepository: WorkoutTemplateRepository
) {
    
    /**
     * Get all templates
     */
    fun getAllTemplates(): Flow<List<WorkoutTemplate>> {
        return templateRepository.getAllTemplates()
    }
    
    /**
     * Get all templates with exercises
     */
    fun getAllTemplatesWithExercises(): Flow<List<WorkoutTemplateWithExercises>> {
        return templateRepository.getAllTemplatesWithExercises()
    }
    
    /**
     * Get template by ID
     */
    suspend fun getTemplateById(templateId: String): WorkoutTemplate? {
        return templateRepository.getTemplateById(templateId)
    }
    
    /**
     * Get template with exercises by ID
     */
    suspend fun getTemplateWithExercises(templateId: String): WorkoutTemplateWithExercises? {
        return templateRepository.getTemplateWithExercises(templateId)
    }
    
    /**
     * Get templates by category
     */
    fun getTemplatesByCategory(category: WorkoutCategory): Flow<List<WorkoutTemplate>> {
        return templateRepository.getTemplatesByCategory(category)
    }
    
    /**
     * Get templates by difficulty level
     */
    fun getTemplatesByDifficulty(difficulty: DifficultyLevel): Flow<List<WorkoutTemplate>> {
        return templateRepository.getTemplatesByDifficulty(difficulty)
    }
    
    /**
     * Get public templates
     */
    fun getPublicTemplates(): Flow<List<WorkoutTemplate>> {
        return templateRepository.getTemplatesByVisibility(true)
    }
    
    /**
     * Get user's private templates
     */
    fun getUserTemplates(userId: String): Flow<List<WorkoutTemplate>> {
        return templateRepository.getTemplatesByUser(userId)
    }
    
    /**
     * Search templates by name or description
     */
    fun searchTemplates(searchQuery: String): Flow<List<WorkoutTemplate>> {
        return templateRepository.searchTemplates(searchQuery)
    }
    
    /**
     * Get popular templates
     */
    fun getPopularTemplates(limit: Int = 10): Flow<List<WorkoutTemplate>> {
        return templateRepository.getPopularTemplates(limit)
    }
    
    /**
     * Get recently created templates
     */
    fun getRecentTemplates(limit: Int = 10): Flow<List<WorkoutTemplate>> {
        return templateRepository.getRecentTemplates(limit)
    }
    
    /**
     * Get filtered templates based on multiple criteria
     */
    data class TemplateFilter(
        val category: WorkoutCategory? = null,
        val difficulty: DifficultyLevel? = null,
        val isPublic: Boolean? = null,
        val userId: String? = null,
        val searchQuery: String? = null,
        val maxDuration: Long? = null, // in minutes
        val requiredEquipment: List<String>? = null,
        val targetMuscleGroups: List<String>? = null
    )
    
    /**
     * Get templates with advanced filtering
     * Note: This is a simplified implementation. In a real app, you might want to implement
     * this filtering at the database level for better performance.
     */
    fun getFilteredTemplates(filter: TemplateFilter): Flow<List<WorkoutTemplate>> {
        return when {
            filter.category != null -> templateRepository.getTemplatesByCategory(filter.category)
            filter.difficulty != null -> templateRepository.getTemplatesByDifficulty(filter.difficulty)
            filter.isPublic != null -> templateRepository.getTemplatesByVisibility(filter.isPublic)
            filter.userId != null -> templateRepository.getTemplatesByUser(filter.userId)
            filter.searchQuery != null -> templateRepository.searchTemplates(filter.searchQuery)
            else -> templateRepository.getAllTemplates()
        }
    }
}