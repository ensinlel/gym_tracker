package com.example.gym_tracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gym_tracker.core.database.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>
    
    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: String): ExerciseEntity?
    
    @Query("SELECT * FROM exercises WHERE category = :category")
    fun getExercisesByCategory(category: String): Flow<List<ExerciseEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity)
    
    @Update
    suspend fun updateExercise(exercise: ExerciseEntity)
    
    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)
}