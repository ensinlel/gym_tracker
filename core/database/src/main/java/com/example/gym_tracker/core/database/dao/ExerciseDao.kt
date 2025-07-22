package com.example.gym_tracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gym_tracker.core.common.enums.ExerciseCategory
import com.example.gym_tracker.core.database.entity.ExerciseEntity
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
    
    // Advanced Query Capabilities - Task 3.2
    
    /**
     * Full-text search across exercise name, instructions, and muscle groups
     * Supports searching multiple terms and partial matches
     */
    @Query("""
        SELECT * FROM exercises 
        WHERE name LIKE '%' || :searchQuery || '%' 
           OR instructions LIKE '%' || :searchQuery || '%'
           OR muscleGroups LIKE '%' || :searchQuery || '%'
           OR category LIKE '%' || :searchQuery || '%'
        ORDER BY 
            CASE 
                WHEN name LIKE :searchQuery || '%' THEN 1
                WHEN name LIKE '%' || :searchQuery || '%' THEN 2
                ELSE 3
            END,
            name ASC
    """)
    fun fullTextSearchExercises(searchQuery: String): Flow<List<ExerciseEntity>>
    
    /**
     * Paginated exercise search with offset and limit
     */
    @Query("""
        SELECT * FROM exercises 
        WHERE name LIKE '%' || :searchQuery || '%' 
           OR instructions LIKE '%' || :searchQuery || '%'
           OR muscleGroups LIKE '%' || :searchQuery || '%'
        ORDER BY name ASC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchExercisesPaginated(
        searchQuery: String, 
        limit: Int, 
        offset: Int
    ): List<ExerciseEntity>
    
    /**
     * Get exercises with usage statistics for analytics
     */
    @Query("""
        SELECT e.*, 
               COUNT(ei.id) as usage_count,
               MAX(w.startTime) as last_used,
               AVG(es.weight) as avg_weight,
               MAX(es.weight) as max_weight
        FROM exercises e
        LEFT JOIN exercise_instances ei ON e.id = ei.exerciseId
        LEFT JOIN workouts w ON ei.workoutId = w.id
        LEFT JOIN exercise_sets es ON ei.id = es.exerciseInstanceId AND es.isWarmup = 0
        WHERE w.startTime >= :startTime OR w.startTime IS NULL
        GROUP BY e.id
        ORDER BY usage_count DESC, e.name ASC
    """)
    fun getExercisesWithUsageStats(startTime: Long): Flow<List<ExerciseEntity>>
    
    /**
     * Search exercises by multiple muscle groups (AND operation)
     */
    @Query("""
        SELECT * FROM exercises 
        WHERE (:muscleGroup1 = '' OR muscleGroups LIKE '%' || :muscleGroup1 || '%')
          AND (:muscleGroup2 = '' OR muscleGroups LIKE '%' || :muscleGroup2 || '%')
          AND (:muscleGroup3 = '' OR muscleGroups LIKE '%' || :muscleGroup3 || '%')
        ORDER BY name ASC
    """)
    fun getExercisesByMultipleMuscleGroups(
        muscleGroup1: String = "",
        muscleGroup2: String = "",
        muscleGroup3: String = ""
    ): Flow<List<ExerciseEntity>>
    
    /**
     * Get exercise recommendations based on workout history and muscle group balance
     */
    @Query("""
        SELECT e.* FROM exercises e
        WHERE e.id NOT IN (
            SELECT DISTINCT ei.exerciseId 
            FROM exercise_instances ei
            INNER JOIN workouts w ON ei.workoutId = w.id
            WHERE w.startTime >= :recentWorkoutThreshold
        )
        AND (:targetMuscleGroup = '' OR e.muscleGroups LIKE '%' || :targetMuscleGroup || '%')
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    fun getExerciseRecommendations(
        recentWorkoutThreshold: Long,
        targetMuscleGroup: String = "",
        limit: Int = 10
    ): Flow<List<ExerciseEntity>>
}