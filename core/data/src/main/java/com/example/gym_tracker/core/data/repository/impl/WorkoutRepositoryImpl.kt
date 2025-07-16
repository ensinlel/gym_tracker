package com.example.gym_tracker.core.data.repository.impl

import com.example.gym_tracker.core.data.mapper.toDomainModel
import com.example.gym_tracker.core.data.mapper.toEntity
import com.example.gym_tracker.core.data.model.Workout
import com.example.gym_tracker.core.data.model.WorkoutWithDetails
import com.example.gym_tracker.core.data.repository.WorkoutRepository
import com.example.gym_tracker.core.database.dao.WorkoutDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of WorkoutRepository using local database
 */
@Singleton
class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao
) : WorkoutRepository {

    override fun getAllWorkouts(): Flow<List<Workout>> {
        return workoutDao.getAllWorkouts().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getWorkoutById(id: String): Workout? {
        return workoutDao.getWorkoutById(id)?.toDomainModel()
    }

    override fun getWorkoutByIdFlow(id: String): Flow<Workout?> {
        return workoutDao.getWorkoutByIdFlow(id).map { entity ->
            entity?.toDomainModel()
        }
    }

    override suspend fun getWorkoutWithDetailsById(id: String): WorkoutWithDetails? {
        return workoutDao.getWorkoutWithDetailsById(id)?.toDomainModel()
    }

    override fun getWorkoutWithDetailsByIdFlow(id: String): Flow<WorkoutWithDetails?> {
        return workoutDao.getWorkoutWithDetailsByIdFlow(id).map { entity ->
            entity?.toDomainModel()
        }
    }

    override fun getAllWorkoutsWithDetails(): Flow<List<WorkoutWithDetails>> {
        return workoutDao.getAllWorkoutsWithDetails().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getWorkoutsByTemplate(templateId: String): Flow<List<Workout>> {
        return workoutDao.getWorkoutsByTemplate(templateId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getWorkoutsByDateRange(startTime: Long, endTime: Long): Flow<List<Workout>> {
        return workoutDao.getWorkoutsByDateRange(startTime, endTime).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getWorkoutCount(): Flow<Int> {
        return workoutDao.getWorkoutCount()
    }

    override fun getWorkoutCountSince(startTime: Long): Flow<Int> {
        return workoutDao.getWorkoutCountSince(startTime)
    }

    override fun getAverageWorkoutVolume(): Flow<Double?> {
        return workoutDao.getAverageWorkoutVolume()
    }

    override fun getMaxWorkoutVolume(): Flow<Double?> {
        return workoutDao.getMaxWorkoutVolume()
    }

    override fun getRatedWorkouts(): Flow<List<Workout>> {
        return workoutDao.getRatedWorkouts().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun insertWorkout(workout: Workout) {
        workoutDao.insertWorkout(workout.toEntity())
    }

    override suspend fun insertWorkouts(workouts: List<Workout>) {
        workoutDao.insertWorkouts(workouts.map { it.toEntity() })
    }

    override suspend fun updateWorkout(workout: Workout) {
        workoutDao.updateWorkout(workout.toEntity())
    }

    override suspend fun deleteWorkout(workout: Workout) {
        workoutDao.deleteWorkout(workout.toEntity())
    }

    override suspend fun deleteWorkoutById(id: String) {
        workoutDao.deleteWorkoutById(id)
    }
}