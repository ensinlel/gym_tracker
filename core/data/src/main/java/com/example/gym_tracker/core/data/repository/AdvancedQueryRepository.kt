package com.example.gym_tracker.core.data.repository

import com.example.gym_tracker.core.database.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for advanced query capabilities - Task 3.2
 * Provides complex analytics queries, full-text search, and efficient pagination
 */
interface AdvancedQueryRepository {
    
    // Volume and Strength Analytics
    /**
     * Get volume progression data over time for analytics charts
     */
    fun getVolumeProgressionData(startTime: Long, endTime: Long): Flow<List<VolumeProgressionPoint>>
    
    /**
     * Get strength progression for specific exercise
     */
    fun getStrengthProgressionForExercise(
        exerciseId: String, 
        startTime: Long, 
        endTime: Long
    ): Flow<List<StrengthProgressionPoint>>
    
    /**
     * Get strength trends for multiple exercises for comparison
     */
    fun getStrengthTrendsForExercises(
        exerciseIds: List<String>,
        startTime: Long,
        endTime: Long
    ): Flow<List<ExerciseStrengthTrend>>
    
    /**
     * Get detailed volume progression by date
     */
    fun getVolumeProgressionByDate(startTime: Long, endTime: Long): Flow<List<VolumeProgressionData>>
    
    // Workout Analytics
    /**
     * Get workout frequency analysis
     */
    fun getWorkoutFrequencyAnalysis(startTime: Long): Flow<List<WorkoutFrequencyPoint>>
    
    /**
     * Get muscle group distribution from workouts
     */
    fun getMuscleGroupDistribution(startTime: Long): Flow<List<MuscleGroupDistribution>>
    
    /**
     * Get workout consistency data for streak analysis
     */
    fun getWorkoutConsistencyData(startTime: Long): Flow<List<WorkoutConsistencyPoint>>
    
    /**
     * Get workout performance trends comparing periods
     */
    fun getWorkoutPerformanceTrends(
        currentPeriodStart: Long,
        currentPeriodEnd: Long,
        previousPeriodStart: Long,
        previousPeriodEnd: Long
    ): Flow<List<WorkoutPerformanceTrend>>
    
    // Personal Records and Progression
    /**
     * Get personal records with progression tracking
     */
    fun getPersonalRecordsProgression(startTime: Long): Flow<List<PersonalRecordData>>
    
    // Training Analysis
    /**
     * Get training intensity analysis based on RPE
     */
    fun getTrainingIntensityAnalysis(startTime: Long): Flow<List<IntensityAnalysisData>>
    
    /**
     * Get rest time analysis for optimization insights
     */
    fun getRestTimeAnalysis(startTime: Long): Flow<List<RestTimeAnalysisData>>
    
    // Full-Text Search Capabilities
    /**
     * Full-text search across exercises with ranking
     */
    fun fullTextSearchExercises(searchQuery: String): Flow<List<ExerciseEntity>>
    
    /**
     * Get exercises with usage statistics for analytics
     */
    fun getExercisesWithUsageStats(startTime: Long): Flow<List<ExerciseEntity>>
    
    /**
     * Search exercises by multiple muscle groups
     */
    fun getExercisesByMultipleMuscleGroups(
        muscleGroup1: String = "",
        muscleGroup2: String = "",
        muscleGroup3: String = ""
    ): Flow<List<ExerciseEntity>>
    
    /**
     * Get exercise recommendations based on workout history
     */
    fun getExerciseRecommendations(
        recentWorkoutThreshold: Long,
        targetMuscleGroup: String = "",
        limit: Int = 10
    ): Flow<List<ExerciseEntity>>
    
    // Efficient Pagination
    /**
     * Get paginated exercises with search
     */
    suspend fun searchExercisesPaginated(
        searchQuery: String, 
        limit: Int, 
        offset: Int
    ): List<ExerciseEntity>
    
    /**
     * Get paginated workouts with advanced filtering
     */
    suspend fun getWorkoutsPaginated(
        startTime: Long = 0,
        endTime: Long = 0,
        minRating: Int = 0,
        hasNotes: Int = 0,
        limit: Int,
        offset: Int
    ): List<WorkoutEntity>
    
    /**
     * Get paginated exercise sets with advanced filtering
     */
    suspend fun getExerciseSetsPaginated(
        exerciseId: String = "",
        minWeight: Double = 0.0,
        maxWeight: Double = 0.0,
        minReps: Int = 0,
        maxReps: Int = 0,
        minRpe: Int = 0,
        maxRpe: Int = 0,
        isWarmup: Int = -1,
        startTime: Long,
        limit: Int,
        offset: Int
    ): List<ExerciseSetEntity>
}