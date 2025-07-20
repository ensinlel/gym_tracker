package com.example.gym_tracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gym_tracker.core.database.entity.ExerciseSetEntity
import com.example.gym_tracker.core.database.entity.VolumeProgressionData
import com.example.gym_tracker.core.database.entity.ExerciseStrengthTrend
import com.example.gym_tracker.core.database.entity.PersonalRecordData
import com.example.gym_tracker.core.database.entity.IntensityAnalysisData
import com.example.gym_tracker.core.database.entity.RestTimeAnalysisData
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseSetDao {
    
    @Query("SELECT * FROM exercise_sets WHERE exerciseInstanceId = :exerciseInstanceId ORDER BY setNumber ASC")
    fun getSetsByExerciseInstance(exerciseInstanceId: String): Flow<List<ExerciseSetEntity>>
    
    @Query("SELECT * FROM exercise_sets WHERE exerciseInstanceId = :exerciseInstanceId ORDER BY setNumber ASC")
    suspend fun getSetsForExerciseInstanceSync(exerciseInstanceId: String): List<ExerciseSetEntity>
    
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
    
    // Advanced Analytics Queries - Task 3.2
    
    /**
     * Get volume progression data for all exercises over time
     * Useful for overall training volume analytics
     */
    @Query("""
        SELECT 
            DATE(w.startTime/1000, 'unixepoch') as workout_date,
            SUM(es.weight * es.reps) as total_volume,
            COUNT(DISTINCT ei.exerciseId) as unique_exercises,
            COUNT(es.id) as total_sets,
            AVG(es.weight) as avg_weight,
            AVG(es.reps) as avg_reps
        FROM exercise_sets es
        INNER JOIN exercise_instances ei ON es.exerciseInstanceId = ei.id
        INNER JOIN workouts w ON ei.workoutId = w.id
        WHERE w.startTime >= :startTime 
          AND w.startTime <= :endTime
          AND es.isWarmup = 0
        GROUP BY DATE(w.startTime/1000, 'unixepoch')
        ORDER BY workout_date ASC
    """)
    fun getVolumeProgressionByDate(startTime: Long, endTime: Long): Flow<List<VolumeProgressionData>>
    
    /**
     * Get strength trends for multiple exercises
     * Returns progression data for comparison charts
     */
    @Query("""
        SELECT 
            e.name as exercise_name,
            ei.exerciseId,
            DATE(w.startTime/1000, 'unixepoch') as workout_date,
            MAX(es.weight) as max_weight,
            MAX(es.weight * (1 + es.reps / 30.0)) as estimated_1rm,
            AVG(es.weight) as avg_weight,
            SUM(es.weight * es.reps) as exercise_volume
        FROM exercise_sets es
        INNER JOIN exercise_instances ei ON es.exerciseInstanceId = ei.id
        INNER JOIN workouts w ON ei.workoutId = w.id
        INNER JOIN exercises e ON ei.exerciseId = e.id
        WHERE ei.exerciseId IN (:exerciseIds)
          AND w.startTime >= :startTime
          AND w.startTime <= :endTime
          AND es.isWarmup = 0
        GROUP BY ei.exerciseId, DATE(w.startTime/1000, 'unixepoch')
        ORDER BY exercise_name ASC, workout_date ASC
    """)
    fun getStrengthTrendsForExercises(
        exerciseIds: List<String>,
        startTime: Long,
        endTime: Long
    ): Flow<List<ExerciseStrengthTrend>>
    
    /**
     * Get personal records with progression tracking
     * Shows when PRs were achieved and progression over time
     */
    @Query("""
        SELECT 
            e.name as exercise_name,
            ei.exerciseId,
            es.weight,
            es.reps,
            es.weight * (1 + es.reps / 30.0) as estimated_1rm,
            w.startTime as achieved_date,
            1 as pr_rank
        FROM exercise_sets es
        INNER JOIN exercise_instances ei ON es.exerciseInstanceId = ei.id
        INNER JOIN workouts w ON ei.workoutId = w.id
        INNER JOIN exercises e ON ei.exerciseId = e.id
        WHERE es.isWarmup = 0
          AND w.startTime >= :startTime
        ORDER BY ei.exerciseId ASC, estimated_1rm DESC
    """)
    fun getPersonalRecordsProgression(startTime: Long): Flow<List<PersonalRecordData>>
    
    /**
     * Get training intensity analysis
     * Analyzes RPE distribution and intensity patterns
     */
    @Query("""
        SELECT 
            es.rpe,
            COUNT(*) as set_count,
            AVG(es.weight) as avg_weight,
            AVG(es.reps) as avg_reps,
            SUM(es.weight * es.reps) as total_volume,
            strftime('%Y-%m', w.startTime/1000, 'unixepoch') as month_year
        FROM exercise_sets es
        INNER JOIN exercise_instances ei ON es.exerciseInstanceId = ei.id
        INNER JOIN workouts w ON ei.workoutId = w.id
        WHERE es.rpe IS NOT NULL
          AND es.isWarmup = 0
          AND w.startTime >= :startTime
        GROUP BY es.rpe, strftime('%Y-%m', w.startTime/1000, 'unixepoch')
        ORDER BY month_year ASC, es.rpe ASC
    """)
    fun getTrainingIntensityAnalysis(startTime: Long): Flow<List<IntensityAnalysisData>>
    
    /**
     * Get rest time analysis for optimization insights
     */
    @Query("""
        SELECT 
            e.name as exercise_name,
            ei.exerciseId,
            AVG(es.restTime) as avg_rest_time,
            MIN(es.restTime) as min_rest_time,
            MAX(es.restTime) as max_rest_time,
            COUNT(*) as set_count,
            AVG(es.weight) as avg_weight_with_rest
        FROM exercise_sets es
        INNER JOIN exercise_instances ei ON es.exerciseInstanceId = ei.id
        INNER JOIN exercises e ON ei.exerciseId = e.id
        INNER JOIN workouts w ON ei.workoutId = w.id
        WHERE es.restTime > 0
          AND es.isWarmup = 0
          AND w.startTime >= :startTime
        GROUP BY ei.exerciseId
        ORDER BY avg_rest_time DESC
    """)
    fun getRestTimeAnalysis(startTime: Long): Flow<List<RestTimeAnalysisData>>
    
    /**
     * Get paginated exercise sets with advanced filtering
     * Supports filtering by weight range, rep range, RPE, etc.
     */
    @Query("""
        SELECT es.* FROM exercise_sets es
        INNER JOIN exercise_instances ei ON es.exerciseInstanceId = ei.id
        INNER JOIN workouts w ON ei.workoutId = w.id
        WHERE (:exerciseId = '' OR ei.exerciseId = :exerciseId)
          AND (:minWeight = 0.0 OR es.weight >= :minWeight)
          AND (:maxWeight = 0.0 OR es.weight <= :maxWeight)
          AND (:minReps = 0 OR es.reps >= :minReps)
          AND (:maxReps = 0 OR es.reps <= :maxReps)
          AND (:minRpe = 0 OR es.rpe >= :minRpe)
          AND (:maxRpe = 0 OR es.rpe <= :maxRpe)
          AND (:isWarmup = -1 OR es.isWarmup = :isWarmup)
          AND w.startTime >= :startTime
        ORDER BY w.startTime DESC, es.setNumber ASC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getExerciseSetsPaginated(
        exerciseId: String = "",
        minWeight: Double = 0.0,
        maxWeight: Double = 0.0,
        minReps: Int = 0,
        maxReps: Int = 0,
        minRpe: Int = 0,
        maxRpe: Int = 0,
        isWarmup: Int = -1, // -1 = all, 0 = working sets, 1 = warmup sets
        startTime: Long,
        limit: Int,
        offset: Int
    ): List<ExerciseSetEntity>
}