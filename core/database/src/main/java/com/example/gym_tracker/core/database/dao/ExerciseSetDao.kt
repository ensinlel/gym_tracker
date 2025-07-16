package com.example.gym_tracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gym_tracker.core.database.entity.ExerciseSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseSetDao {
    
    @Query("SELECT * FROM exercise_sets WHERE exerciseInstanceId = :exerciseInstanceId ORDER BY setNumber ASC")
    fun getSetsByExerciseInstance(exerciseInstanceId: String): Flow<List<ExerciseSetEntity>>
    
    @Query("SELECT * FROM exercise_sets WHERE id = :id")
    suspend fun getSetById(id: String): ExerciseSetEntity?
    
    @Query("SELECT * FROM exercise_sets WHERE id = :id")
    fun getSetByIdFlow(id: String): Flow<ExerciseSetEntity?>
    
    @Query("SELECT * FROM exercise_sets WHERE exerciseInstanceId = :exerciseInstanceId AND setNumber = :setNumber")
    suspend fun getSetByNumber(exerciseInstanceId: String, setNumber: Int): ExerciseSetEntity?
    
    @Query("SELECT MAX(setNumber) FROM exercise_sets WHERE exerciseInstanceId = :exerciseInstanceId")
    suspend fun getMaxSetNumber(exerciseInstanceId: String): Int?
    
    @Query("SELECT COUNT(*) FROM exercise_sets WHERE exerciseInstanceId = :exerciseInstanceId")
    fun getSetCountByExerciseInstance(exerciseInstanceId: String): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM exercise_sets WHERE exerciseInstanceId = :exerciseInstanceId AND isWarmup = 0")
    fun getWorkingSetCountByExerciseInstance(exerciseInstanceId: String): Flow<Int>
    
    @Query("""
        SELECT AVG(weight) FROM exercise_sets 
        WHERE exerciseInstanceId IN (
            SELECT id FROM exercise_instances WHERE exerciseId = :exerciseId
        ) AND isWarmup = 0
    """)
    fun getAverageWeightForExercise(exerciseId: String): Flow<Double?>
    
    @Query("""
        SELECT MAX(weight) FROM exercise_sets 
        WHERE exerciseInstanceId IN (
            SELECT id FROM exercise_instances WHERE exerciseId = :exerciseId
        ) AND isWarmup = 0
    """)
    fun getMaxWeightForExercise(exerciseId: String): Flow<Double?>
    
    @Query("""
        SELECT MAX(weight * (1 + reps / 30.0)) FROM exercise_sets 
        WHERE exerciseInstanceId IN (
            SELECT id FROM exercise_instances WHERE exerciseId = :exerciseId
        ) AND isWarmup = 0
    """)
    fun getEstimatedOneRepMaxForExercise(exerciseId: String): Flow<Double?>
    
    @Query("""
        SELECT SUM(weight * reps) FROM exercise_sets 
        WHERE exerciseInstanceId IN (
            SELECT ei.id FROM exercise_instances ei
            INNER JOIN workouts w ON ei.workoutId = w.id
            WHERE ei.exerciseId = :exerciseId AND w.startTime >= :startTime
        ) AND isWarmup = 0
    """)
    fun getTotalVolumeForExerciseSince(exerciseId: String, startTime: Long): Flow<Double?>
    
    @Query("""
        SELECT AVG(rpe) FROM exercise_sets 
        WHERE exerciseInstanceId IN (
            SELECT id FROM exercise_instances WHERE exerciseId = :exerciseId
        ) AND rpe IS NOT NULL AND isWarmup = 0
    """)
    fun getAverageRPEForExercise(exerciseId: String): Flow<Double?>
    
    @Query("""
        SELECT AVG(restTime) FROM exercise_sets 
        WHERE exerciseInstanceId = :exerciseInstanceId AND restTime > 0
    """)
    fun getAverageRestTimeForExerciseInstance(exerciseInstanceId: String): Flow<Long?>
    
    @Query("""
        SELECT * FROM exercise_sets 
        WHERE exerciseInstanceId IN (
            SELECT ei.id FROM exercise_instances ei
            INNER JOIN workouts w ON ei.workoutId = w.id
            WHERE ei.exerciseId = :exerciseId
        )
        ORDER BY (
            SELECT w.startTime FROM workouts w 
            INNER JOIN exercise_instances ei ON w.id = ei.workoutId 
            WHERE ei.id = exerciseInstanceId
        ) DESC, setNumber ASC
    """)
    fun getSetHistoryForExercise(exerciseId: String): Flow<List<ExerciseSetEntity>>
    
    @Query("""
        SELECT * FROM exercise_sets 
        WHERE exerciseInstanceId IN (
            SELECT ei.id FROM exercise_instances ei
            INNER JOIN workouts w ON ei.workoutId = w.id
            WHERE ei.exerciseId = :exerciseId AND w.startTime >= :startTime
        )
        ORDER BY (
            SELECT w.startTime FROM workouts w 
            INNER JOIN exercise_instances ei ON w.id = ei.workoutId 
            WHERE ei.id = exerciseInstanceId
        ) DESC, setNumber ASC
    """)
    fun getSetHistoryForExerciseSince(exerciseId: String, startTime: Long): Flow<List<ExerciseSetEntity>>
    
    @Query("SELECT * FROM exercise_sets WHERE isFailure = 1 ORDER BY weight DESC, reps DESC")
    fun getFailureSets(): Flow<List<ExerciseSetEntity>>
    
    @Query("SELECT * FROM exercise_sets WHERE rpe >= :minRpe ORDER BY rpe DESC, weight DESC")
    fun getHighIntensitySets(minRpe: Int = 8): Flow<List<ExerciseSetEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: ExerciseSetEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<ExerciseSetEntity>)
    
    @Update
    suspend fun updateSet(set: ExerciseSetEntity)
    
    @Delete
    suspend fun deleteSet(set: ExerciseSetEntity)
    
    @Query("DELETE FROM exercise_sets WHERE id = :id")
    suspend fun deleteSetById(id: String)
    
    @Query("DELETE FROM exercise_sets WHERE exerciseInstanceId = :exerciseInstanceId")
    suspend fun deleteSetsByExerciseInstance(exerciseInstanceId: String)
    
    @Query("DELETE FROM exercise_sets WHERE exerciseInstanceId = :exerciseInstanceId AND setNumber = :setNumber")
    suspend fun deleteSetByNumber(exerciseInstanceId: String, setNumber: Int)
}