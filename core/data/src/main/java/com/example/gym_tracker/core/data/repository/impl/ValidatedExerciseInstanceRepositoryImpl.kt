package com.example.gym_tracker.core.data.repository.impl

import com.example.gym_tracker.core.data.cache.ExerciseInstanceCache
import com.example.gym_tracker.core.data.mapper.toDomainModel
import com.example.gym_tracker.core.data.mapper.toEntity
import com.example.gym_tracker.core.data.model.ExerciseInstance
import com.example.gym_tracker.core.data.model.ExerciseInstanceWithDetails
import com.example.gym_tracker.core.data.repository.ExerciseInstanceRepository
import com.example.gym_tracker.core.data.util.PerformanceMonitor
import com.example.gym_tracker.core.data.validation.DataIntegrityValidator
import com.example.gym_tracker.core.database.dao.ExerciseInstanceDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Enhanced ExerciseInstanceRepository with built-in data validation
 */
@Singleton
class ValidatedExerciseInstanceRepositoryImpl @Inject constructor(
    private val exerciseInstanceDao: ExerciseInstanceDao,
    private val cache: ExerciseInstanceCache,
    private val performanceMonitor: PerformanceMonitor,
    private val dataIntegrityValidator: DataIntegrityValidator
) : ExerciseInstanceRepository {

    override fun getExerciseInstancesByWorkoutId(workoutId: String): Flow<List<ExerciseInstance>> {
        return exerciseInstanceDao.getExerciseInstancesByWorkout(workoutId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getExerciseInstancesWithDetailsByWorkoutId(workoutId: String): Flow<List<ExerciseInstanceWithDetails>> {
        return exerciseInstanceDao.getExerciseInstancesWithDetailsByWorkout(workoutId)
            .map { entities ->
                entities.map { it.toDomainModel() }
            }
            .onEach { exerciseInstances ->
                // Cache the results for better performance
                cache.cacheExerciseInstancesForWorkout(workoutId, exerciseInstances)
            }
    }

    override suspend fun getExerciseInstanceById(id: String): ExerciseInstance? {
        return performanceMonitor.measureOperation("getExerciseInstanceById") {
            // Check cache first
            cache.getCachedExerciseInstance(id)?.let { return@measureOperation it }
            
            // If not in cache, fetch from database
            val exerciseInstance = exerciseInstanceDao.getExerciseInstanceById(id)?.toDomainModel()
            
            // Cache the result if found
            exerciseInstance?.let { cache.cacheExerciseInstance(it) }
            
            exerciseInstance
        }
    }

    override suspend fun getExerciseInstanceWithDetailsById(id: String): ExerciseInstanceWithDetails? {
        // Check cache first
        cache.getCachedExerciseInstanceWithDetails(id)?.let { return it }
        
        // If not in cache, fetch from database
        val exerciseInstanceWithDetails = exerciseInstanceDao.getExerciseInstanceWithDetailsById(id)?.toDomainModel()
        
        // Cache the result if found
        exerciseInstanceWithDetails?.let { cache.cacheExerciseInstanceWithDetails(it) }
        
        return exerciseInstanceWithDetails
    }

    override fun getExerciseInstancesByExerciseId(exerciseId: String): Flow<List<ExerciseInstance>> {
        return exerciseInstanceDao.getExerciseInstancesByExerciseId(exerciseId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun insertExerciseInstance(exerciseInstance: ExerciseInstance): String {
        return performanceMonitor.measureOperation("insertExerciseInstance") {
            // Validate data integrity before insertion
            val validationResult = dataIntegrityValidator.validateExerciseInstanceCreation(exerciseInstance)
            if (!validationResult.isValid) {
                throw IllegalArgumentException("Exercise instance validation failed: ${validationResult.errors.joinToString(", ")}")
            }
            
            exerciseInstanceDao.insertExerciseInstance(exerciseInstance.toEntity())
            
            // Cache the inserted instance
            cache.cacheExerciseInstance(exerciseInstance)
            
            // Invalidate workout cache to ensure fresh data on next load
            cache.invalidateWorkoutCache(exerciseInstance.workoutId)
            
            exerciseInstance.id
        }
    }

    override suspend fun insertExerciseInstances(exerciseInstances: List<ExerciseInstance>) {
        // Validate all instances before insertion
        exerciseInstances.forEach { exerciseInstance ->
            val validationResult = dataIntegrityValidator.validateExerciseInstanceCreation(exerciseInstance)
            if (!validationResult.isValid) {
                throw IllegalArgumentException("Exercise instance validation failed for ${exerciseInstance.id}: ${validationResult.errors.joinToString(", ")}")
            }
        }
        
        exerciseInstanceDao.insertExerciseInstances(exerciseInstances.map { it.toEntity() })
        
        // Cache all inserted instances
        exerciseInstances.forEach { cache.cacheExerciseInstance(it) }
        
        // Invalidate workout caches for all affected workouts
        exerciseInstances.map { it.workoutId }.distinct().forEach { workoutId ->
            cache.invalidateWorkoutCache(workoutId)
        }
    }

    override suspend fun updateExerciseInstance(exerciseInstance: ExerciseInstance) {
        // Validate data integrity before update
        val validationResult = dataIntegrityValidator.validateExerciseInstanceCreation(exerciseInstance)
        if (!validationResult.isValid) {
            throw IllegalArgumentException("Exercise instance validation failed: ${validationResult.errors.joinToString(", ")}")
        }
        
        exerciseInstanceDao.updateExerciseInstance(exerciseInstance.toEntity())
        
        // Update cache
        cache.cacheExerciseInstance(exerciseInstance)
        
        // Invalidate workout cache to ensure fresh data
        cache.invalidateWorkoutCache(exerciseInstance.workoutId)
    }

    override suspend fun deleteExerciseInstance(exerciseInstanceId: String) {
        val exerciseInstance = exerciseInstanceDao.getExerciseInstanceById(exerciseInstanceId)
        if (exerciseInstance != null) {
            exerciseInstanceDao.deleteExerciseInstance(exerciseInstance)
            
            // Invalidate caches
            cache.invalidateExerciseInstanceCache(exerciseInstanceId)
            cache.invalidateWorkoutCache(exerciseInstance.workoutId)
        }
    }

    override suspend fun deleteExerciseInstancesByWorkoutId(workoutId: String) {
        exerciseInstanceDao.deleteExerciseInstancesByWorkout(workoutId)
        
        // Invalidate workout cache
        cache.invalidateWorkoutCache(workoutId)
    }

    override suspend fun getNextOrderInWorkout(workoutId: String): Int {
        val existingInstances = exerciseInstanceDao.getExerciseInstancesForWorkoutSync(workoutId)
        return (existingInstances.maxOfOrNull { it.orderInWorkout } ?: 0) + 1
    }
    
    /**
     * Validate and repair data integrity for a specific workout
     */
    suspend fun validateAndRepairWorkoutIntegrity(workoutId: String): DataIntegrityValidator.DataConsistencyResult {
        val consistencyResult = dataIntegrityValidator.performDataConsistencyCheck()
        
        // Filter issues related to this workout
        val workoutIssues = consistencyResult.issues.filter { issue ->
            issue.affectedRecords.contains(workoutId)
        }
        
        if (workoutIssues.isNotEmpty()) {
            // Attempt to repair issues
            val repairResult = dataIntegrityValidator.repairDataConsistencyIssues(workoutIssues)
            
            // Invalidate cache after repairs
            cache.invalidateWorkoutCache(workoutId)
            
            println("Data integrity repair for workout $workoutId: ${repairResult.repairedCount}/${repairResult.totalIssues} issues repaired")
        }
        
        return consistencyResult
    }
}