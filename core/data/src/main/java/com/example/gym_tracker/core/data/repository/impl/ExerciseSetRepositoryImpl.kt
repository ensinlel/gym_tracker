package com.example.gym_tracker.core.data.repository.impl

import com.example.gym_tracker.core.data.mapper.toDomainModel
import com.example.gym_tracker.core.data.mapper.toEntity
import com.example.gym_tracker.core.data.model.ExerciseSet
import com.example.gym_tracker.core.data.repository.ExerciseSetRepository
import com.example.gym_tracker.core.data.repository.ExerciseInstanceRepository
import com.example.gym_tracker.core.data.service.GoalProgressService
import com.example.gym_tracker.core.database.dao.ExerciseSetDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ExerciseSetRepository using local database
 */
@Singleton
class ExerciseSetRepositoryImpl @Inject constructor(
    private val exerciseSetDao: ExerciseSetDao,
    private val exerciseInstanceRepository: ExerciseInstanceRepository,
    private val goalProgressService: GoalProgressService
) : ExerciseSetRepository {

    override fun getSetsByExerciseInstance(exerciseInstanceId: String): Flow<List<ExerciseSet>> {
        return exerciseSetDao.getSetsByExerciseInstance(exerciseInstanceId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getSetById(id: String): ExerciseSet? {
        return exerciseSetDao.getSetById(id)?.toDomainModel()
    }

    override fun getSetByIdFlow(id: String): Flow<ExerciseSet?> {
        return exerciseSetDao.getSetByIdFlow(id).map { entity ->
            entity?.toDomainModel()
        }
    }

    override suspend fun getSetByNumber(exerciseInstanceId: String, setNumber: Int): ExerciseSet? {
        return exerciseSetDao.getSetByNumber(exerciseInstanceId, setNumber)?.toDomainModel()
    }

    override suspend fun getMaxSetNumber(exerciseInstanceId: String): Int? {
        return exerciseSetDao.getMaxSetNumber(exerciseInstanceId)
    }

    override fun getSetCountByExerciseInstance(exerciseInstanceId: String): Flow<Int> {
        return exerciseSetDao.getSetCountByExerciseInstance(exerciseInstanceId)
    }

    override fun getWorkingSetCountByExerciseInstance(exerciseInstanceId: String): Flow<Int> {
        return exerciseSetDao.getWorkingSetCountByExerciseInstance(exerciseInstanceId)
    }

    override fun getAverageWeightForExercise(exerciseId: String): Flow<Double?> {
        return exerciseSetDao.getAverageWeightForExercise(exerciseId)
    }

    override fun getMaxWeightForExercise(exerciseId: String): Flow<Double?> {
        return exerciseSetDao.getMaxWeightForExercise(exerciseId)
    }

    override fun getEstimatedOneRepMaxForExercise(exerciseId: String): Flow<Double?> {
        return exerciseSetDao.getEstimatedOneRepMaxForExercise(exerciseId)
    }

    override fun getTotalVolumeForExerciseSince(exerciseId: String, startTime: Long): Flow<Double?> {
        return exerciseSetDao.getTotalVolumeForExerciseSince(exerciseId, startTime)
    }

    override fun getAverageRPEForExercise(exerciseId: String): Flow<Double?> {
        return exerciseSetDao.getAverageRPEForExercise(exerciseId)
    }

    override fun getAverageRestTimeForExerciseInstance(exerciseInstanceId: String): Flow<Long?> {
        return exerciseSetDao.getAverageRestTimeForExerciseInstance(exerciseInstanceId)
    }

    override fun getSetHistoryForExercise(exerciseId: String): Flow<List<ExerciseSet>> {
        return exerciseSetDao.getSetHistoryForExercise(exerciseId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getSetHistoryForExerciseSince(exerciseId: String, startTime: Long): Flow<List<ExerciseSet>> {
        return exerciseSetDao.getSetHistoryForExerciseSince(exerciseId, startTime).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getFailureSets(): Flow<List<ExerciseSet>> {
        return exerciseSetDao.getFailureSets().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getHighIntensitySets(minRpe: Int): Flow<List<ExerciseSet>> {
        return exerciseSetDao.getHighIntensitySets(minRpe).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun insertSet(set: ExerciseSet) {
        exerciseSetDao.insertSet(set.toEntity())
        
        // Update PR goals for this exercise if it's a new PR
        try {
            val exerciseInstance = exerciseInstanceRepository.getExerciseInstanceById(set.exerciseInstanceId)
            exerciseInstance?.let { instance ->
                goalProgressService.updatePRGoalsForExercise(instance.exerciseId)
            }
        } catch (e: Exception) {
            // Don't fail the set insertion if goal update fails
            println("Failed to update PR goals: ${e.message}")
        }
    }

    override suspend fun insertSets(sets: List<ExerciseSet>) {
        exerciseSetDao.insertSets(sets.map { it.toEntity() })
        
        // Update PR goals for all exercises involved
        try {
            val exerciseIds = sets.mapNotNull { set ->
                exerciseInstanceRepository.getExerciseInstanceById(set.exerciseInstanceId)?.exerciseId
            }.distinct()
            
            exerciseIds.forEach { exerciseId ->
                goalProgressService.updatePRGoalsForExercise(exerciseId)
            }
        } catch (e: Exception) {
            println("Failed to update PR goals: ${e.message}")
        }
    }

    override suspend fun updateSet(set: ExerciseSet) {
        exerciseSetDao.updateSet(set.toEntity())
    }

    override suspend fun deleteSet(set: ExerciseSet) {
        exerciseSetDao.deleteSet(set.toEntity())
    }

    override suspend fun deleteSetById(id: String) {
        exerciseSetDao.deleteSetById(id)
    }

    override suspend fun deleteSetsByExerciseInstance(exerciseInstanceId: String) {
        exerciseSetDao.deleteSetsByExerciseInstance(exerciseInstanceId)
    }

    override suspend fun deleteSetByNumber(exerciseInstanceId: String, setNumber: Int) {
        exerciseSetDao.deleteSetByNumber(exerciseInstanceId, setNumber)
    }
}