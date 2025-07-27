package com.example.gym_tracker.core.data.usecase

import com.example.gym_tracker.core.data.model.*
import com.example.gym_tracker.core.data.repository.WorkoutTemplateRepository
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for managing workout templates (create, update, delete)
 */
@Singleton
class ManageWorkoutTemplateUseCase @Inject constructor(
    private val templateRepository: WorkoutTemplateRepository
) {
    
    /**
     * Create a new workout template
     */
    suspend fun createTemplate(
        name: String,
        description: String = "",
        category: WorkoutCategory = WorkoutCategory.GENERAL,
        difficulty: DifficultyLevel = DifficultyLevel.INTERMEDIATE,
        estimatedDuration: java.time.Duration = java.time.Duration.ZERO,
        targetMuscleGroups: List<com.example.gym_tracker.core.common.enums.MuscleGroup> = emptyList(),
        requiredEquipment: List<com.example.gym_tracker.core.common.enums.Equipment> = emptyList(),
        isPublic: Boolean = false,
        createdBy: String? = null,
        tags: List<String> = emptyList()
    ): Result<WorkoutTemplate> {
        return try {
            val template = WorkoutTemplate(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                category = category,
                difficulty = difficulty,
                estimatedDuration = estimatedDuration,
                targetMuscleGroups = targetMuscleGroups,
                requiredEquipment = requiredEquipment,
                isPublic = isPublic,
                createdBy = createdBy,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                usageCount = 0,
                rating = 0.0,
                tags = tags
            )
            
            templateRepository.insertTemplate(template)
            Result.success(template)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update an existing workout template
     */
    suspend fun updateTemplate(template: WorkoutTemplate): Result<WorkoutTemplate> {
        return try {
            val updatedTemplate = template.copy(updatedAt = Instant.now())
            templateRepository.updateTemplate(updatedTemplate)
            Result.success(updatedTemplate)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete a workout template
     */
    suspend fun deleteTemplate(templateId: String): Result<Unit> {
        return try {
            templateRepository.deleteTemplateById(templateId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Duplicate a workout template
     */
    suspend fun duplicateTemplate(
        templateId: String,
        newName: String? = null,
        userId: String? = null
    ): Result<WorkoutTemplate> {
        return try {
            val originalTemplate = templateRepository.getTemplateWithExercises(templateId)
                ?: return Result.failure(IllegalArgumentException("Template not found"))
            
            val duplicatedTemplate = originalTemplate.template.copy(
                id = UUID.randomUUID().toString(),
                name = newName ?: "${originalTemplate.template.name} (Copy)",
                createdBy = userId,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                usageCount = 0,
                rating = 0.0,
                isPublic = false // Duplicated templates are private by default
            )
            
            templateRepository.insertTemplate(duplicatedTemplate)
            
            // Duplicate exercises
            val duplicatedExercises = originalTemplate.exercises.map { exerciseWithDetails ->
                exerciseWithDetails.templateExercise.copy(
                    id = UUID.randomUUID().toString(),
                    templateId = duplicatedTemplate.id
                )
            }
            
            templateRepository.insertTemplateExercises(duplicatedExercises)
            
            Result.success(duplicatedTemplate)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Add exercise to template
     */
    suspend fun addExerciseToTemplate(
        templateId: String,
        exerciseId: String,
        targetSets: Int = 3,
        targetReps: IntRange? = null,
        targetWeight: Double? = null,
        restTime: java.time.Duration = java.time.Duration.ofMinutes(2),
        notes: String = "",
        isSuperset: Boolean = false,
        supersetGroup: Int? = null
    ): Result<TemplateExercise> {
        return try {
            // Get current exercises to determine order
            val currentExercises = templateRepository.getExercisesByTemplate(templateId).first()
            val orderInTemplate = currentExercises.size + 1
            
            val templateExercise = TemplateExercise(
                id = UUID.randomUUID().toString(),
                templateId = templateId,
                exerciseId = exerciseId,
                orderInTemplate = orderInTemplate,
                targetSets = targetSets,
                targetReps = targetReps,
                targetWeight = targetWeight,
                restTime = restTime,
                notes = notes,
                isSuperset = isSuperset,
                supersetGroup = supersetGroup
            )
            
            templateRepository.insertTemplateExercise(templateExercise)
            Result.success(templateExercise)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Remove exercise from template
     */
    suspend fun removeExerciseFromTemplate(templateExerciseId: String): Result<Unit> {
        return try {
            templateRepository.deleteTemplateExerciseById(templateExerciseId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update exercise in template
     */
    suspend fun updateTemplateExercise(templateExercise: TemplateExercise): Result<TemplateExercise> {
        return try {
            templateRepository.updateTemplateExercise(templateExercise)
            Result.success(templateExercise)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Reorder exercises in template
     */
    suspend fun reorderExercises(
        templateId: String,
        exerciseIds: List<String>
    ): Result<Unit> {
        return try {
            exerciseIds.forEachIndexed { index, exerciseId ->
                templateRepository.updateExerciseOrder(exerciseId, index + 1)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Rate a template
     */
    suspend fun rateTemplate(templateId: String, rating: Double): Result<Unit> {
        return try {
            if (rating < 0.0 || rating > 5.0) {
                return Result.failure(IllegalArgumentException("Rating must be between 0.0 and 5.0"))
            }
            
            templateRepository.updateTemplateRating(templateId, rating)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Increment template usage count
     */
    suspend fun incrementTemplateUsage(templateId: String): Result<Unit> {
        return try {
            templateRepository.incrementUsageCount(templateId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}