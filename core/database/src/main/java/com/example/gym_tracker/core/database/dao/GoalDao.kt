package com.example.gym_tracker.core.database.dao

import androidx.room.*
import com.example.gym_tracker.core.database.entity.GoalEntity
import com.example.gym_tracker.core.database.entity.GoalType
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Goal operations
 */
@Dao
interface GoalDao {
    
    @Query("SELECT * FROM goals WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getAllActiveGoals(): Flow<List<GoalEntity>>
    
    @Query("SELECT * FROM goals WHERE id = :goalId")
    suspend fun getGoalById(goalId: String): GoalEntity?
    
    @Query("SELECT * FROM goals WHERE id = :goalId")
    fun getGoalByIdFlow(goalId: String): Flow<GoalEntity?>
    
    @Query("SELECT * FROM goals WHERE type = :type AND isActive = 1 ORDER BY createdAt DESC")
    fun getGoalsByType(type: GoalType): Flow<List<GoalEntity>>
    
    @Query("SELECT * FROM goals WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedGoals(): Flow<List<GoalEntity>>
    
    @Query("SELECT * FROM goals WHERE isCompleted = 0 AND isActive = 1 ORDER BY targetDate ASC")
    fun getActiveGoals(): Flow<List<GoalEntity>>
    
    @Query("SELECT * FROM goals WHERE targetDate <= :date AND isCompleted = 0 AND isActive = 1")
    fun getOverdueGoals(date: String): Flow<List<GoalEntity>>
    
    @Query("SELECT * FROM goals WHERE linkedExerciseId = :exerciseId AND isActive = 1")
    fun getGoalsByExercise(exerciseId: String): Flow<List<GoalEntity>>
    
    @Query("SELECT * FROM goals WHERE type = 'PERSONAL_RECORD' AND linkedExerciseId IS NOT NULL AND isActive = 1 ORDER BY createdAt DESC")
    fun getPRGoals(): Flow<List<GoalEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)
    
    @Update
    suspend fun updateGoal(goal: GoalEntity)
    
    @Query("UPDATE goals SET currentValue = :currentValue, updatedAt = :updatedAt WHERE id = :goalId")
    suspend fun updateGoalProgress(goalId: String, currentValue: Double, updatedAt: String)
    
    @Query("UPDATE goals SET isCompleted = 1, completedAt = :completedAt, updatedAt = :updatedAt WHERE id = :goalId")
    suspend fun markGoalCompleted(goalId: String, completedAt: String, updatedAt: String)
    
    @Query("UPDATE goals SET isActive = 0, updatedAt = :updatedAt WHERE id = :goalId")
    suspend fun deactivateGoal(goalId: String, updatedAt: String)
    
    @Delete
    suspend fun deleteGoal(goal: GoalEntity)
    
    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: String)
}