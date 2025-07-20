package com.example.gym_tracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.gym_tracker.core.database.entity.ExerciseInstanceEntity
import com.example.gym_tracker.core.database.entity.ExerciseInstanceWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseInstanceDao {
    
    @Query("SELECT * FROM exercise_instances WHERE workoutId = :workoutId ORDER BY orderInWorkout ASC")
    fun getExerciseInstancesByWorkout(workoutId: String): Flow<List<ExerciseInstanceEntity>>
    
    @Query("SELECT * FROM exercise_instances WHERE workoutId = :workoutId ORDER BY orderInWorkout ASC")
    suspend fun getExerciseInstancesForWorkoutSync(workoutId: String): List<ExerciseInstanceEntity>
    
    @Query("SELECT * FROM exercise_instances WHERE exerciseId = :exerciseId ORDER BY orderInWorkout ASC")
    fun getExerciseInstancesByExerciseId(exerciseId: String): Flow<List<ExerciseInstanceEntity>>
    
    @Query("SELECT * FROM exercise_instances WHERE id = :id")
    suspend fun getExerciseInstanceById(id: String): ExerciseInstanceEntity?
    
    @Transaction
    @Query("SELECT * FROM exercise_instances WHERE workoutId = :workoutId ORDER BY orderInWorkout ASC")
    fun getExerciseInstancesWithDetailsByWorkout(workoutId: String): Flow<List<ExerciseInstanceWithDetails>>
    
    @Transaction
    @Query("SELECT * FROM exercise_instances WHERE id = :id")
    suspend fun getExerciseInstanceWithDetailsById(id: String): ExerciseInstanceWithDetails?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseInstance(exerciseInstance: ExerciseInstanceEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseInstances(exerciseInstances: List<ExerciseInstanceEntity>)
    
    @Update
    suspend fun updateExerciseInstance(exerciseInstance: ExerciseInstanceEntity)
    
    @Delete
    suspend fun deleteExerciseInstance(exerciseInstance: ExerciseInstanceEntity)
    
    @Query("DELETE FROM exercise_instances WHERE workoutId = :workoutId")
    suspend fun deleteExerciseInstancesByWorkout(workoutId: String)
    
    @Query("SELECT COUNT(*) FROM exercise_instances WHERE workoutId = :workoutId")
    fun getExerciseInstanceCountByWorkout(workoutId: String): Flow<Int>
    
    @Query("SELECT MAX(orderInWorkout) FROM exercise_instances WHERE workoutId = :workoutId")
    suspend fun getMaxOrderInWorkout(workoutId: String): Int?
    
    @Query("""
        UPDATE exercise_instances 
        SET orderInWorkout = orderInWorkout - 1 
        WHERE workoutId = :workoutId AND orderInWorkout > :deletedOrder
    """)
    suspend fun reorderAfterDeletion(workoutId: String, deletedOrder: Int)
}