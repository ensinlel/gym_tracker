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
import com.example.gym_tracker.core.database.entity.VolumeProgressionPoint
import com.example.gym_tracker.core.database.entity.StrengthProgressionPoint
import com.example.gym_tracker.core.database.entity.WorkoutFrequencyPoint
import com.example.gym_tracker.core.database.entity.MuscleGroupDistribution
import com.example.gym_tracker.core.database.entity.WorkoutConsistencyPoint
import com.example.gym_tracker.core.database.entity.WorkoutPerformanceTrend
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
    
    // Advanced Analytics Queries - Task 3.2
    
    /**
     * Get volume progression data for analytics charts
     * Returns daily volume totals over time
     */
    @Query("""
        SELECT 
            DATE(startTime/1000, 'unixepoch') as workout_date,
            SUM(totalVolume) as daily_volume,
            COUNT(*) as workout_count,
            AVG(totalVolume) as avg_workout_volume
        FROM workouts 
        WHERE startTime >= :startTime 
          AND startTime <= :endTime
          AND totalVolume > 0
        GROUP BY DATE(startTime/1000, 'unixepoch')
        ORDER BY workout_date ASC
    """)
    fun getVolumeProgressionData(startTime: Long, endTime: Long): Flow<List<VolumeProgressionPoint>>
    
    /**
     * Get strength progression for specific exercise
     * Returns max weight progression over time
     */
    @Query("""
        SELECT 
            DATE(w.startTime/1000, 'unixepoch') as workout_date,
            MAX(es.weight) as max_weight,
            MAX(es.weight * (1 + es.reps / 30.0)) as estimated_1rm,
            AVG(es.weight) as avg_weight,
            SUM(es.weight * es.reps) as total_volume
        FROM workouts w
        INNER JOIN exercise_instances ei ON w.id = ei.workoutId
        INNER JOIN exercise_sets es ON ei.id = es.exerciseInstanceId
        WHERE ei.exerciseId = :exerciseId
          AND w.startTime >= :startTime
          AND w.startTime <= :endTime
          AND es.isWarmup = 0
        GROUP BY DATE(w.startTime/1000, 'unixepoch')
        ORDER BY workout_date ASC
    """)
    fun getStrengthProgressionForExercise(
        exerciseId: String, 
        startTime: Long, 
        endTime: Long
    ): Flow<List<StrengthProgressionPoint>>
    
    /**
     * Get workout frequency analysis
     * Returns workout count by day of week and time periods
     */
    @Query("""
        SELECT 
            strftime('%w', startTime/1000, 'unixepoch') as day_of_week,
            strftime('%Y-%W', startTime/1000, 'unixepoch') as week_year,
            COUNT(*) as workout_count,
            AVG(totalVolume) as avg_volume,
            AVG((endTime - startTime) / 60000.0) as avg_duration_minutes
        FROM workouts 
        WHERE startTime >= :startTime 
          AND endTime IS NOT NULL
        GROUP BY strftime('%w', startTime/1000, 'unixepoch'), strftime('%Y-%W', startTime/1000, 'unixepoch')
        ORDER BY week_year ASC, day_of_week ASC
    """)
    fun getWorkoutFrequencyAnalysis(startTime: Long): Flow<List<WorkoutFrequencyPoint>>
    
    /**
     * Get muscle group distribution from recent workouts
     */
    @Query("""
        SELECT 
            e.muscleGroups,
            COUNT(ei.id) as exercise_count,
            SUM(es.weight * es.reps) as total_volume,
            AVG(es.weight) as avg_weight
        FROM workouts w
        INNER JOIN exercise_instances ei ON w.id = ei.workoutId
        INNER JOIN exercises e ON ei.exerciseId = e.id
        INNER JOIN exercise_sets es ON ei.id = es.exerciseInstanceId
        WHERE w.startTime >= :startTime
          AND es.isWarmup = 0
        GROUP BY e.muscleGroups
        ORDER BY total_volume DESC
    """)
    fun getMuscleGroupDistribution(startTime: Long): Flow<List<MuscleGroupDistribution>>
    
    /**
     * Get workout consistency metrics
     * Returns streak information and consistency patterns
     */
    @Query("""
        SELECT 
            DATE(startTime/1000, 'unixepoch') as workout_date,
            COUNT(*) as workouts_per_day,
            NULL as prev_date
        FROM workouts 
        WHERE startTime >= :startTime
        GROUP BY DATE(startTime/1000, 'unixepoch')
        ORDER BY workout_date ASC
    """)
    fun getWorkoutConsistencyData(startTime: Long): Flow<List<WorkoutConsistencyPoint>>
    
    /**
     * Get paginated workouts with advanced filtering
     */
    @Query("""
        SELECT * FROM workouts 
        WHERE (:startTime = 0 OR startTime >= :startTime)
          AND (:endTime = 0 OR startTime <= :endTime)
          AND (:minRating = 0 OR rating >= :minRating)
          AND (:hasNotes = 0 OR (notes IS NOT NULL AND notes != ''))
        ORDER BY startTime DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getWorkoutsPaginated(
        startTime: Long = 0,
        endTime: Long = 0,
        minRating: Int = 0,
        hasNotes: Int = 0,
        limit: Int,
        offset: Int
    ): List<WorkoutEntity>
    
    /**
     * Get workout performance trends
     * Compares current period with previous period
     */
    @Query("""
        SELECT 
            'current' as period,
            COUNT(*) as workout_count,
            AVG(totalVolume) as avg_volume,
            MAX(totalVolume) as max_volume,
            AVG((endTime - startTime) / 60000.0) as avg_duration_minutes,
            AVG(rating) as avg_rating
        FROM workouts 
        WHERE startTime >= :currentPeriodStart 
          AND startTime <= :currentPeriodEnd
        UNION ALL
        SELECT 
            'previous' as period,
            COUNT(*) as workout_count,
            AVG(totalVolume) as avg_volume,
            MAX(totalVolume) as max_volume,
            AVG((endTime - startTime) / 60000.0) as avg_duration_minutes,
            AVG(rating) as avg_rating
        FROM workouts 
        WHERE startTime >= :previousPeriodStart 
          AND startTime <= :previousPeriodEnd
    """)
    fun getWorkoutPerformanceTrends(
        currentPeriodStart: Long,
        currentPeriodEnd: Long,
        previousPeriodStart: Long,
        previousPeriodEnd: Long
    ): Flow<List<WorkoutPerformanceTrend>>
}