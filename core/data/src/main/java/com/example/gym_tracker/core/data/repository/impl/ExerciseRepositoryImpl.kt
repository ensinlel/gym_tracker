package com.example.gym_tracker.core.data.repository.impl

import com.example.gym_tracker.core.data.mapper.toDomainModel
import com.example.gym_tracker.core.data.mapper.toEntity
import com.example.gym_tracker.core.data.model.Exercise
import com.example.gym_tracker.core.data.model.ExerciseCategory
import com.example.gym_tracker.core.data.repository.ExerciseRepository
import com.example.gym_tracker.core.database.dao.ExerciseDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ExerciseRepository using local database
 */
@Singleton
class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao
) : ExerciseRepository {

    override fun getAllExercises(): Flow<List<Exercise>> {
        return exerciseDao.getAllExercises().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getExerciseById(id: String): Exercise? {
        return exerciseDao.getExerciseById(id)?.toDomainModel()
    }

    override fun getExerciseByIdFlow(id: String): Flow<Exercise?> {
        return exerciseDao.getExerciseByIdFlow(id).map { entity ->
            entity?.toDomainModel()
        }
    }

    override fun getExercisesByCategory(category: ExerciseCategory): Flow<List<Exercise>> {
        return exerciseDao.getExercisesByCategory(category.toEntity()).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getExercisesByCustomStatus(isCustom: Boolean): Flow<List<Exercise>> {
        return exerciseDao.getExercisesByCustomStatus(isCustom).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun searchExercisesByName(query: String): Flow<List<Exercise>> {
        return exerciseDao.searchExercisesByName(query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getExercisesByMuscleGroup(muscleGroup: String): Flow<List<Exercise>> {
        return exerciseDao.getExercisesByMuscleGroup(muscleGroup).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getExerciseCount(): Flow<Int> {
        return exerciseDao.getExerciseCount()
    }

    override fun getCustomExerciseCount(): Flow<Int> {
        return exerciseDao.getCustomExerciseCount()
    }

    override fun getAllCategories(): Flow<List<ExerciseCategory>> {
        return exerciseDao.getAllCategories().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getMostUsedExercisesSince(startTime: Long): Flow<List<Exercise>> {
        return exerciseDao.getMostUsedExercisesSince(startTime).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getRecentlyUsedExercises(limit: Int): Flow<List<Exercise>> {
        return exerciseDao.getRecentlyUsedExercises(limit).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun insertExercise(exercise: Exercise) {
        exerciseDao.insertExercise(exercise.toEntity())
    }

    override suspend fun insertExercises(exercises: List<Exercise>) {
        exerciseDao.insertExercises(exercises.map { it.toEntity() })
    }

    override suspend fun updateExercise(exercise: Exercise) {
        exerciseDao.updateExercise(exercise.toEntity())
    }

    override suspend fun deleteExercise(exercise: Exercise) {
        exerciseDao.deleteExercise(exercise.toEntity())
    }

    override suspend fun deleteExerciseById(id: String) {
        exerciseDao.deleteExerciseById(id)
    }
}