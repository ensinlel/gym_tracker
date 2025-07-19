package com.example.gym_tracker.core.data.repository.impl

import com.example.gym_tracker.core.data.mapper.toDomainModel
import com.example.gym_tracker.core.data.mapper.toEntity
import com.example.gym_tracker.core.data.model.Exercise
import com.example.gym_tracker.core.data.model.ExerciseCategory
import com.example.gym_tracker.core.data.repository.ExerciseRepository
import com.example.gym_tracker.core.database.dao.ExerciseDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    override suspend fun seedSampleExercises() {
        // Check if exercises already exist to avoid duplicates
        val existingCount = exerciseDao.getExerciseCount().first()
        if (existingCount > 0) {
            return // Database already has exercises
        }

        val sampleExercises = createSampleExercises()
        exerciseDao.insertExercises(sampleExercises.map { it.toEntity() })
    }

    private fun createSampleExercises(): List<Exercise> {
        val now = java.time.Instant.now()
        
        return listOf(
            // Chest exercises
            Exercise(
                id = "bench-press",
                name = "Bench Press",
                category = ExerciseCategory.CHEST,
                muscleGroups = listOf(
                    com.example.gym_tracker.core.data.model.MuscleGroup.CHEST,
                    com.example.gym_tracker.core.data.model.MuscleGroup.TRICEPS,
                    com.example.gym_tracker.core.data.model.MuscleGroup.FRONT_DELTS
                ),
                equipment = com.example.gym_tracker.core.data.model.Equipment.BARBELL,
                instructions = listOf(
                    "Lie flat on bench with feet firmly on ground",
                    "Grip bar with hands slightly wider than shoulder width",
                    "Lower bar to chest with control",
                    "Press bar up explosively until arms are fully extended"
                ),
                createdAt = now,
                updatedAt = now,
                isCustom = false,
                isStarMarked = false
            ),
            Exercise(
                id = "dumbbell-press",
                name = "Dumbbell Bench Press",
                category = ExerciseCategory.CHEST,
                muscleGroups = listOf(
                    com.example.gym_tracker.core.data.model.MuscleGroup.CHEST,
                    com.example.gym_tracker.core.data.model.MuscleGroup.TRICEPS,
                    com.example.gym_tracker.core.data.model.MuscleGroup.FRONT_DELTS
                ),
                equipment = com.example.gym_tracker.core.data.model.Equipment.DUMBBELL,
                instructions = listOf(
                    "Lie on bench holding dumbbells at chest level",
                    "Press dumbbells up and slightly inward",
                    "Lower with control to starting position"
                ),
                createdAt = now,
                updatedAt = now,
                isCustom = false,
                isStarMarked = false
            ),
            
            // Back exercises
            Exercise(
                id = "deadlift",
                name = "Deadlift",
                category = ExerciseCategory.BACK,
                muscleGroups = listOf(
                    com.example.gym_tracker.core.data.model.MuscleGroup.LOWER_BACK,
                    com.example.gym_tracker.core.data.model.MuscleGroup.UPPER_BACK,
                    com.example.gym_tracker.core.data.model.MuscleGroup.GLUTES,
                    com.example.gym_tracker.core.data.model.MuscleGroup.HAMSTRINGS
                ),
                equipment = com.example.gym_tracker.core.data.model.Equipment.BARBELL,
                instructions = listOf(
                    "Stand with feet hip-width apart, bar over mid-foot",
                    "Bend at hips and knees to grip bar",
                    "Keep chest up and back straight",
                    "Drive through heels to stand up straight"
                ),
                createdAt = now,
                updatedAt = now,
                isCustom = false,
                isStarMarked = false
            ),
            Exercise(
                id = "pull-ups",
                name = "Pull-ups",
                category = ExerciseCategory.BACK,
                muscleGroups = listOf(
                    com.example.gym_tracker.core.data.model.MuscleGroup.UPPER_BACK,
                    com.example.gym_tracker.core.data.model.MuscleGroup.BICEPS
                ),
                equipment = com.example.gym_tracker.core.data.model.Equipment.BODYWEIGHT,
                instructions = listOf(
                    "Hang from pull-up bar with overhand grip",
                    "Pull body up until chin clears bar",
                    "Lower with control to starting position"
                ),
                createdAt = now,
                updatedAt = now,
                isCustom = false,
                isStarMarked = false
            ),
            
            // Leg exercises
            Exercise(
                id = "squat",
                name = "Squat",
                category = ExerciseCategory.LEGS,
                muscleGroups = listOf(
                    com.example.gym_tracker.core.data.model.MuscleGroup.QUADRICEPS,
                    com.example.gym_tracker.core.data.model.MuscleGroup.GLUTES,
                    com.example.gym_tracker.core.data.model.MuscleGroup.HAMSTRINGS
                ),
                equipment = com.example.gym_tracker.core.data.model.Equipment.BARBELL,
                instructions = listOf(
                    "Position bar on upper back/traps",
                    "Stand with feet shoulder-width apart",
                    "Lower by bending at hips and knees",
                    "Drive through heels to return to standing"
                ),
                createdAt = now,
                updatedAt = now,
                isCustom = false,
                isStarMarked = false
            ),
            Exercise(
                id = "lunges",
                name = "Lunges",
                category = ExerciseCategory.LEGS,
                muscleGroups = listOf(
                    com.example.gym_tracker.core.data.model.MuscleGroup.QUADRICEPS,
                    com.example.gym_tracker.core.data.model.MuscleGroup.GLUTES,
                    com.example.gym_tracker.core.data.model.MuscleGroup.HAMSTRINGS
                ),
                equipment = com.example.gym_tracker.core.data.model.Equipment.BODYWEIGHT,
                instructions = listOf(
                    "Step forward into lunge position",
                    "Lower back knee toward ground",
                    "Push through front heel to return to standing"
                ),
                createdAt = now,
                updatedAt = now,
                isCustom = false,
                isStarMarked = false
            ),
            
            // Shoulder exercises
            Exercise(
                id = "overhead-press",
                name = "Overhead Press",
                category = ExerciseCategory.SHOULDERS,
                muscleGroups = listOf(
                    com.example.gym_tracker.core.data.model.MuscleGroup.FRONT_DELTS,
                    com.example.gym_tracker.core.data.model.MuscleGroup.SIDE_DELTS,
                    com.example.gym_tracker.core.data.model.MuscleGroup.TRICEPS
                ),
                equipment = com.example.gym_tracker.core.data.model.Equipment.BARBELL,
                instructions = listOf(
                    "Start with bar at shoulder level",
                    "Press bar straight up overhead",
                    "Lower with control to starting position"
                ),
                createdAt = now,
                updatedAt = now,
                isCustom = false,
                isStarMarked = false
            ),
            
            // Arm exercises
            Exercise(
                id = "bicep-curls",
                name = "Bicep Curls",
                category = ExerciseCategory.ARMS,
                muscleGroups = listOf(
                    com.example.gym_tracker.core.data.model.MuscleGroup.BICEPS
                ),
                equipment = com.example.gym_tracker.core.data.model.Equipment.DUMBBELL,
                instructions = listOf(
                    "Hold dumbbells at sides with palms forward",
                    "Curl weights up toward shoulders",
                    "Lower with control to starting position"
                ),
                createdAt = now,
                updatedAt = now,
                isCustom = false,
                isStarMarked = false
            ),
            
            // Core exercises
            Exercise(
                id = "plank",
                name = "Plank",
                category = ExerciseCategory.CORE,
                muscleGroups = listOf(
                    com.example.gym_tracker.core.data.model.MuscleGroup.ABS,
                    com.example.gym_tracker.core.data.model.MuscleGroup.OBLIQUES
                ),
                equipment = com.example.gym_tracker.core.data.model.Equipment.BODYWEIGHT,
                instructions = listOf(
                    "Start in push-up position",
                    "Hold body in straight line from head to heels",
                    "Engage core and breathe normally"
                ),
                createdAt = now,
                updatedAt = now,
                isCustom = false,
                isStarMarked = false
            )
        )
    }
}