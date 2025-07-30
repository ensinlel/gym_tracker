package com.example.gym_tracker.core.data.repository.impl

import com.example.gym_tracker.core.data.mapper.toDomainModel
import com.example.gym_tracker.core.data.mapper.toEntity
import com.example.gym_tracker.core.data.model.ExerciseSet
import com.example.gym_tracker.core.data.repository.ExerciseSetRepository
import com.example.gym_tracker.core.data.validation.DataIntegrityValidator
import com.example.gym_tracker.core.database.dao.ExerciseSetDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Enhanced ExerciseSetRepository with built-in data validation
 */
@Singleton
class ValidatedExerciseSetRepositoryImpl @Inject constructor(
    private val exerciseSetDao: ExerciseSetDao,
    private val dataIntegrityValidator: DataIntegrityValidator
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
        // Validate data integrity before insertion
        val validationResult = dataIntegrityValidator.validateExerciseSetCreation(set)
        if (!validationResult.isValid) {
            throw IllegalArgumentException("Exercise set validation failed: ${validationResult.errors.joinToString(", ")}")
        }
        
        // Auto-assign set number if not provided or invalid
        val validatedSet = if (set.setNumber <= 0) {
            val maxSetNumber = getMaxSetNumber(set.exerciseInstanceId) ?: 0
            set.copy(setNumber = maxSetNumber + 1)
        } else {
            set
        }
        
        exerciseSetDao.insertSet(validatedSet.toEntity())
    }

    override suspend fun insertSets(sets: List<ExerciseSet>) {
        // Validate all sets before insertion
        sets.forEach { set ->
            val validationResult = dataIntegrityValidator.validateExerciseSetCreation(set)
            if (!validationResult.isValid) {
                throw IllegalArgumentException("Exercise set validation failed for ${set.id}: ${validationResult.errors.joinToString(", ")}")
            }
        }
        
        // Auto-assign set numbers for sets with invalid numbers
        val validatedSets = sets.map { set ->
            if (set.setNumber <= 0) {
                val maxSetNumber = getMaxSetNumber(set.exerciseInstanceId) ?: 0
                set.copy(setNumber = maxSetNumber + 1)
            } else {
                set
            }
        }
        
        exerciseSetDao.insertSets(validatedSets.map { it.toEntity() })
    }

    override suspend fun updateSet(set: ExerciseSet) {
        // Validate data integrity before update
        val validationResult = dataIntegrityValidator.validateExerciseSetCreation(set)
        if (!validationResult.isValid) {
            throw IllegalArgumentException("Exercise set validation failed: ${validationResult.errors.joinToString(", ")}")
        }
        
        exerciseSetDao.updateSet(set.toEntity())
    }

    override suspend fun deleteSet(set: ExerciseSet) {
        exerciseSetDao.deleteSet(set.toEntity())
        
        // After deletion, reorder remaining sets to maintain sequence
        reorderSetsAfterDeletion(set.exerciseInstanceId, set.setNumber)
    }

    override suspend fun deleteSetById(id: String) {
        val set = getSetById(id)
        if (set != null) {
            exerciseSetDao.deleteSetById(id)
            
            // After deletion, reorder remaining sets to maintain sequence
            reorderSetsAfterDeletion(set.exerciseInstanceId, set.setNumber)
        }
    }

    override suspend fun deleteSetsByExerciseInstance(exerciseInstanceId: String) {
        exerciseSetDao.deleteSetsByExerciseInstance(exerciseInstanceId)
    }

    override suspend fun deleteSetByNumber(exerciseInstanceId: String, setNumber: Int) {
        exerciseSetDao.deleteSetByNumber(exerciseInstanceId, setNumber)
        
        // After deletion, reorder remaining sets to maintain sequence
        reorderSetsAfterDeletion(exerciseInstanceId, setNumber)
    }
    
    /**
     * Reorder sets after deletion to maintain continuous numbering
     */
    private suspend fun reorderSetsAfterDeletion(exerciseInstanceId: String, deletedSetNumber: Int) {
        try {
            val remainingSets = exerciseSetDao.getSetsForExerciseInstanceSync(exerciseInstanceId)
            
            // Reorder sets that come after the deleted set
            val setsToReorder = remainingSets.filter { it.setNumber > deletedSetNumber }
            
            setsToReorder.forEach { setEntity ->
                val updatedSet = setEntity.copy(setNumber = setEntity.setNumber - 1)
                exerciseSetDao.updateSet(updatedSet)
            }
            
            println("Reordered ${setsToReorder.size} sets after deletion of set $deletedSetNumber")
        } catch (e: Exception) {
            println("Failed to reorder sets after deletion: ${e.message}")
        }
    }
    
    /**
     * Validate and repair set numbering for an exercise instance
     */
    suspend fun validateAndRepairSetNumbering(exerciseInstanceId: String): Boolean {
        return try {
            val sets = exerciseSetDao.getSetsForExerciseInstanceSync(exerciseInstanceId)
            val sortedSets = sets.sortedBy { it.setNumber }
            
            var needsRepair = false
            
            // Check for gaps or duplicates
            sortedSets.forEachIndexed { index, set ->
                val expectedSetNumber = index + 1
                if (set.setNumber != expectedSetNumber) {
                    needsRepair = true
                }
            }
            
            if (needsRepair) {
                // Repair set numbering
                sortedSets.forEachIndexed { index, set ->
                    val correctedSetNumber = index + 1
                    if (set.setNumber != correctedSetNumber) {
                        val correctedSet = set.copy(setNumber = correctedSetNumber)
                        exerciseSetDao.updateSet(correctedSet)
                    }
                }
                println("Repaired set numbering for exercise instance $exerciseInstanceId")
            }
            
            true
        } catch (e: Exception) {
            println("Failed to repair set numbering: ${e.message}")
            false
        }
    }
    
    /**
     * Get comprehensive set statistics for validation
     */
    suspend fun getSetStatistics(exerciseInstanceId: String): SetStatistics {
        return try {
            val sets = exerciseSetDao.getSetsForExerciseInstanceSync(exerciseInstanceId)
            
            SetStatistics(
                totalSets = sets.size,
                setNumbers = sets.map { it.setNumber }.sorted(),
                hasGaps = hasSetNumberGaps(sets.map { it.setNumber }),
                hasDuplicates = hasSetNumberDuplicates(sets.map { it.setNumber }),
                averageWeight = sets.map { it.weight }.average().takeIf { sets.isNotEmpty() } ?: 0.0,
                averageReps = sets.map { it.reps }.average().takeIf { sets.isNotEmpty() } ?: 0.0,
                totalVolume = sets.sumOf { it.weight * it.reps }
            )
        } catch (e: Exception) {
            SetStatistics(
                totalSets = 0,
                setNumbers = emptyList(),
                hasGaps = false,
                hasDuplicates = false,
                averageWeight = 0.0,
                averageReps = 0.0,
                totalVolume = 0.0
            )
        }
    }
    
    private fun hasSetNumberGaps(setNumbers: List<Int>): Boolean {
        val sorted = setNumbers.sorted()
        for (i in 1 until sorted.size) {
            if (sorted[i] != sorted[i-1] + 1) {
                return true
            }
        }
        return false
    }
    
    private fun hasSetNumberDuplicates(setNumbers: List<Int>): Boolean {
        return setNumbers.size != setNumbers.distinct().size
    }
    
    data class SetStatistics(
        val totalSets: Int,
        val setNumbers: List<Int>,
        val hasGaps: Boolean,
        val hasDuplicates: Boolean,
        val averageWeight: Double,
        val averageReps: Double,
        val totalVolume: Double
    )
}