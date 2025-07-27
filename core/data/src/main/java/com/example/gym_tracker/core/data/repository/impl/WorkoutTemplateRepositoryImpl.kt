package com.example.gym_tracker.core.data.repository.impl

import com.example.gym_tracker.core.data.mapper.toDomainModel
import com.example.gym_tracker.core.data.mapper.toEntity
import com.example.gym_tracker.core.data.model.*
import com.example.gym_tracker.core.data.repository.WorkoutTemplateRepository
import com.example.gym_tracker.core.database.dao.TemplateExerciseDao
import com.example.gym_tracker.core.database.dao.WorkoutTemplateDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of WorkoutTemplateRepository using local database
 */
@Singleton
class WorkoutTemplateRepositoryImpl @Inject constructor(
    private val templateDao: WorkoutTemplateDao,
    private val templateExerciseDao: TemplateExerciseDao
) : WorkoutTemplateRepository {

    override fun getAllTemplates(): Flow<List<WorkoutTemplate>> {
        return templateDao.getAllTemplates().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getTemplateById(templateId: String): WorkoutTemplate? {
        return templateDao.getTemplateById(templateId)?.toDomainModel()
    }

    override fun getTemplateByIdFlow(templateId: String): Flow<WorkoutTemplate?> {
        return templateDao.getTemplateByIdFlow(templateId).map { entity ->
            entity?.toDomainModel()
        }
    }

    override suspend fun insertTemplate(template: WorkoutTemplate) {
        templateDao.insertTemplate(template.toEntity())
    }

    override suspend fun updateTemplate(template: WorkoutTemplate) {
        templateDao.updateTemplate(template.toEntity())
    }

    override suspend fun deleteTemplate(template: WorkoutTemplate) {
        templateDao.deleteTemplate(template.toEntity())
    }

    override suspend fun deleteTemplateById(templateId: String) {
        templateDao.deleteTemplateById(templateId)
    }

    override suspend fun getTemplateWithExercises(templateId: String): WorkoutTemplateWithExercises? {
        return templateDao.getTemplateWithExercises(templateId)?.toDomainModel()
    }

    override fun getTemplateWithExercisesFlow(templateId: String): Flow<WorkoutTemplateWithExercises?> {
        return templateDao.getTemplateWithExercisesFlow(templateId).map { entity ->
            entity?.toDomainModel()
        }
    }

    override fun getAllTemplatesWithExercises(): Flow<List<WorkoutTemplateWithExercises>> {
        return templateDao.getAllTemplatesWithExercises().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getTemplatesByCategory(category: WorkoutCategory): Flow<List<WorkoutTemplate>> {
        return templateDao.getTemplatesByCategory(category.name).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getTemplatesByDifficulty(difficulty: DifficultyLevel): Flow<List<WorkoutTemplate>> {
        return templateDao.getTemplatesByDifficulty(difficulty.name).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getTemplatesByVisibility(isPublic: Boolean): Flow<List<WorkoutTemplate>> {
        return templateDao.getTemplatesByVisibility(isPublic).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getTemplatesByUser(userId: String): Flow<List<WorkoutTemplate>> {
        return templateDao.getTemplatesByUser(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun searchTemplates(searchQuery: String): Flow<List<WorkoutTemplate>> {
        return templateDao.searchTemplates(searchQuery).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getPopularTemplates(limit: Int): Flow<List<WorkoutTemplate>> {
        return templateDao.getPopularTemplates(limit).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getRecentTemplates(limit: Int): Flow<List<WorkoutTemplate>> {
        return templateDao.getRecentTemplates(limit).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun incrementUsageCount(templateId: String) {
        templateDao.incrementUsageCount(templateId)
    }

    override suspend fun updateTemplateRating(templateId: String, rating: Double) {
        templateDao.updateTemplateRating(templateId, rating)
    }

    override fun getExercisesByTemplate(templateId: String): Flow<List<TemplateExercise>> {
        return templateExerciseDao.getExercisesByTemplate(templateId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getTemplateExerciseById(exerciseId: String): TemplateExercise? {
        return templateExerciseDao.getTemplateExerciseById(exerciseId)?.toDomainModel()
    }

    override suspend fun insertTemplateExercise(exercise: TemplateExercise) {
        templateExerciseDao.insertTemplateExercise(exercise.toEntity())
    }

    override suspend fun updateTemplateExercise(exercise: TemplateExercise) {
        templateExerciseDao.updateTemplateExercise(exercise.toEntity())
    }

    override suspend fun deleteTemplateExercise(exercise: TemplateExercise) {
        templateExerciseDao.deleteTemplateExercise(exercise.toEntity())
    }

    override suspend fun deleteTemplateExerciseById(exerciseId: String) {
        templateExerciseDao.deleteTemplateExerciseById(exerciseId)
    }

    override suspend fun deleteExercisesByTemplate(templateId: String) {
        templateExerciseDao.deleteExercisesByTemplate(templateId)
    }

    override fun getTemplateExercisesWithDetails(templateId: String): Flow<List<TemplateExerciseWithDetails>> {
        return templateExerciseDao.getTemplateExercisesWithDetails(templateId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun insertTemplateExercises(exercises: List<TemplateExercise>) {
        templateExerciseDao.insertTemplateExercises(exercises.map { it.toEntity() })
    }

    override suspend fun updateExerciseOrder(exerciseId: String, newOrder: Int) {
        templateExerciseDao.updateExerciseOrder(exerciseId, newOrder)
    }

    override fun getSupersetExercises(templateId: String): Flow<List<TemplateExercise>> {
        return templateExerciseDao.getSupersetExercises(templateId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getExercisesBySupersetGroup(templateId: String, groupId: Int): Flow<List<TemplateExercise>> {
        return templateExerciseDao.getExercisesBySupersetGroup(templateId, groupId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun insertTemplates(templates: List<WorkoutTemplate>) {
        templateDao.insertTemplates(templates.map { it.toEntity() })
    }

    override suspend fun deleteTemplatesByUser(userId: String) {
        templateDao.deleteTemplatesByUser(userId)
    }
}