package com.example.gym_tracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gym_tracker.core.database.entity.ExerciseCategory
import com.example.gym_tracker.core.database.entity.ExerciseEntity
import com.example.gym_tracker.core.database.entity.MuscleGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>
    
    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: String): ExerciseEntity?
    
    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getExerciseByIdFlow(id: String): Flow<ExerciseEntity?>
    
    @Query("SELECT * FROM exercises WHERE category = :category ORDER BY name ASC")
    fun getExercisesByCategory(category: ExerciseCategory): Flow<List<ExerciseEntity>>
    
    @Query("SELECT * FROM exercises WHERE isCustom = :isCustom ORDER BY name ASC")
    fun getExercisesByCustomStatus(isCustom: Boolean): Flow<List<ExerciseEntity>>
    
    @Query("SELECT * FROM exercises WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    fun searchExercisesByName(searchQuery: String): Flow<List<ExerciseEntity>>
    
    @Query("""
        SELECT * FROM exercises 
        WHERE muscleGroups LIKE '%' || :muscleGroup || '%' 
        ORDER BY name ASC
    """)
    fun getExercisesByMuscleGroup(muscleGroup: String): Flow<List<ExerciseEntity>>
    
    @Query("SELECT COUNT(*) FROM exercises")
    fun getExerciseCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM exercises WHERE isCustom = 1")
    fun getCustomExerciseCount(): Flow<Int>
    
    @Query("SELECT DISTINCT category FROM exercises ORDER BY category ASC")
    fun getAllCategories(): Flow<List<ExerciseCategory>>
    
    @Query("""
        SELECT e.* FROM exercises e
        INNER JOIN exercise_instances ei ON e.id = ei.exerciseId
        INNER JOIN workouts w ON ei.workoutId = w.id
        WHERE w.startTime >= :startTime
        GROUP BY e.id
        ORDER BY COUNT(ei.id) DESC, e.name ASC
    """)
    fun getMostUsedExercisesSince(startTime: Long): Flow<List<ExerciseEntity>>
    
    @Query("""
        SELECT e.* FROM exercises e
        INNER JOIN exercise_instances ei ON e.id = ei.exerciseId
        INNER JOIN workouts w ON ei.workoutId = w.id
        GROUP BY e.id
        ORDER BY MAX(w.startTime) DESC
        LIMIT :limit
    """)
    fun getRecentlyUsedExercises(limit: Int = 10): Flow<List<ExerciseEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)
    
    @Update
    suspend fun updateExercise(exercise: ExerciseEntity)
    
    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)
    
    @Query("DELETE FROM exercises WHERE id = :id")
    suspend fun deleteExerciseById(id: String)
}