package com.example.gym_tracker.core.data.repository.impl

import com.example.gym_tracker.core.data.mapper.toDomainModel
import com.example.gym_tracker.core.data.mapper.toEntity
import com.example.gym_tracker.core.data.model.Goal
import com.example.gym_tracker.core.data.model.GoalType
import com.example.gym_tracker.core.data.repository.GoalRepository
import com.example.gym_tracker.core.database.dao.GoalDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of GoalRepository
 */
@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {
    
    override fun getAllActiveGoals(): Flow<List<Goal>> {
        return goalDao.getAllActiveGoals().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun getGoalById(goalId: String): Goal? {
        return goalDao.getGoalById(goalId)?.toDomainModel()
    }
    
    override fun getGoalByIdFlow(goalId: String): Flow<Goal?> {
        return goalDao.getGoalByIdFlow(goalId).map { entity ->
            entity?.toDomainModel()
        }
    }
    
    override fun getGoalsByType(type: GoalType): Flow<List<Goal>> {
        return goalDao.getGoalsByType(type.toEntity()).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getCompletedGoals(): Flow<List<Goal>> {
        return goalDao.getCompletedGoals().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getActiveGoals(): Flow<List<Goal>> {
        return goalDao.getActiveGoals().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getOverdueGoals(date: LocalDate): Flow<List<Goal>> {
        return goalDao.getOverdueGoals(date.toString()).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun createGoal(goal: Goal) {
        goalDao.insertGoal(goal.toEntity())
    }
    
    override suspend fun updateGoal(goal: Goal) {
        goalDao.updateGoal(goal.toEntity())
    }
    
    override suspend fun updateGoalProgress(goalId: String, currentValue: Double) {
        goalDao.updateGoalProgress(goalId, currentValue, Instant.now().toString())
    }
    
    override suspend fun markGoalCompleted(goalId: String) {
        val now = Instant.now().toString()
        goalDao.markGoalCompleted(goalId, now, now)
    }
    
    override suspend fun deactivateGoal(goalId: String) {
        goalDao.deactivateGoal(goalId, Instant.now().toString())
    }
    
    override suspend fun deleteGoal(goalId: String) {
        goalDao.deleteGoalById(goalId)
    }
    
    override fun getGoalsByExercise(exerciseId: String): Flow<List<Goal>> {
        return goalDao.getGoalsByExercise(exerciseId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getPRGoals(): Flow<List<Goal>> {
        return goalDao.getPRGoals().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}