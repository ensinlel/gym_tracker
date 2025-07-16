package com.example.gym_tracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.gym_tracker.core.database.entity.WorkoutEntity
import com.example.gym_tracker.core.database.entity.WorkoutWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    
    @Query("SELECT * FROM workouts ORDER BY startTime DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>
    
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: String): WorkoutEntity?
    
    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getWorkoutByIdFlow(id: String): Flow<WorkoutEntity?>
    
    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutWithDetailsById(id: String): WorkoutWithDetails?
    
    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getWorkoutWithDetailsByIdFlow(id: String): Flow<WorkoutWithDetails?>
    
    @Transaction
    @Query("SELECT * FROM workouts ORDER BY startTime DESC")
    fun getAllWorkoutsWithDetails(): Flow<List<WorkoutWithDetails>>
    
    @Query("SELECT * FROM workouts WHERE templateId = :templateId ORDER BY startTime DESC")
    fun getWorkoutsByTemplate(templateId: String): Flow<List<WorkoutEntity>>
    
    @Query("SELECT * FROM workouts WHERE startTime >= :startTime AND startTime <= :endTime ORDER BY startTime DESC")
    fun getWorkoutsByDateRange(startTime: Long, endTime: Long): Flow<List<WorkoutEntity>>
    
    @Query("SELECT COUNT(*) FROM workouts")
    fun getWorkoutCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM workouts WHERE startTime >= :startTime")
    fun getWorkoutCountSince(startTime: Long): Flow<Int>
    
    @Query("SELECT AVG(totalVolume) FROM workouts WHERE totalVolume > 0")
    fun getAverageWorkoutVolume(): Flow<Double?>
    
    @Query("SELECT MAX(totalVolume) FROM workouts")
    fun getMaxWorkoutVolume(): Flow<Double?>
    
    @Query("SELECT * FROM workouts WHERE rating IS NOT NULL ORDER BY rating DESC, startTime DESC")
    fun getRatedWorkouts(): Flow<List<WorkoutEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkouts(workouts: List<WorkoutEntity>)
    
    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)
    
    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)
    
    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteWorkoutById(id: String)
}