package com.example.gym_tracker.core.data.repository.impl

import com.example.gym_tracker.core.data.repository.AdvancedQueryRepository
import com.example.gym_tracker.core.database.dao.*
import com.example.gym_tracker.core.database.entity.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AdvancedQueryRepository - Task 3.2
 * Provides complex analytics queries, full-text search, and efficient pagination
 */
@Singleton
class AdvancedQueryRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao,
    private val exerciseSetDao: ExerciseSetDao,
    private val exerciseInstanceDao: ExerciseInstanceDao
) : AdvancedQueryRepository {
    
    // Volume and Strength Analytics
    override fun getVolumeProgressionData(startTime: Long, endTime: Long): Flow<List<VolumeProgressionPoint>> {
        return workoutDao.getVolumeProgressionData(startTime, endTime)
    }
    
    override fun getStrengthProgressionForExercise(
        exerciseId: String, 
        startTime: Long, 
        endTime: Long
    ): Flow<List<StrengthProgressionPoint>> {
        return workoutDao.getStrengthProgressionForExercise(exerciseId, startTime, endTime)
    }
    
    override fun getStrengthTrendsForExercises(
        exerciseIds: List<String>,
        startTime: Long,
        endTime: Long
    ): Flow<List<ExerciseStrengthTrend>> {
        return exerciseSetDao.getStrengthTrendsForExercises(exerciseIds, startTime, endTime)
    }
    
    override fun getVolumeProgressionByDate(startTime: Long, endTime: Long): Flow<List<VolumeProgressionData>> {
        return exerciseSetDao.getVolumeProgressionByDate(startTime, endTime)
    }
    
    // Workout Analytics
    override fun getWorkoutFrequencyAnalysis(startTime: Long): Flow<List<WorkoutFrequencyPoint>> {
        return workoutDao.getWorkoutFrequencyAnalysis(startTime)
    }
    
    override fun getMuscleGroupDistribution(startTime: Long): Flow<List<MuscleGroupDistribution>> {
        return workoutDao.getMuscleGroupDistribution(startTime)
    }
    
    override fun getWorkoutConsistencyData(startTime: Long): Flow<List<WorkoutConsistencyPoint>> {
        return workoutDao.getWorkoutConsistencyData(startTime)
    }
    
    override fun getWorkoutPerformanceTrends(
        currentPeriodStart: Long,
        currentPeriodEnd: Long,
        previousPeriodStart: Long,
        previousPeriodEnd: Long
    ): Flow<List<WorkoutPerformanceTrend>> {
        return workoutDao.getWorkoutPerformanceTrends(
            currentPeriodStart,
            currentPeriodEnd,
            previousPeriodStart,
            previousPeriodEnd
        )
    }
    
    // Personal Records and Progression
    override fun getPersonalRecordsProgression(startTime: Long): Flow<List<PersonalRecordData>> {
        return exerciseSetDao.getPersonalRecordsProgression(startTime)
    }
    
    // Training Analysis
    override fun getTrainingIntensityAnalysis(startTime: Long): Flow<List<IntensityAnalysisData>> {
        return exerciseSetDao.getTrainingIntensityAnalysis(startTime)
    }
    
    override fun getRestTimeAnalysis(startTime: Long): Flow<List<RestTimeAnalysisData>> {
        return exerciseSetDao.getRestTimeAnalysis(startTime)
    }
    
    // Full-Text Search Capabilities
    override fun fullTextSearchExercises(searchQuery: String): Flow<List<ExerciseEntity>> {
        return exerciseDao.fullTextSearchExercises(searchQuery)
    }
    
    override fun getExercisesWithUsageStats(startTime: Long): Flow<List<ExerciseEntity>> {
        return exerciseDao.getExercisesWithUsageStats(startTime)
    }
    
    override fun getExercisesByMultipleMuscleGroups(
        muscleGroup1: String,
        muscleGroup2: String,
        muscleGroup3: String
    ): Flow<List<ExerciseEntity>> {
        return exerciseDao.getExercisesByMultipleMuscleGroups(muscleGroup1, muscleGroup2, muscleGroup3)
    }
    
    override fun getExerciseRecommendations(
        recentWorkoutThreshold: Long,
        targetMuscleGroup: String,
        limit: Int
    ): Flow<List<ExerciseEntity>> {
        return exerciseDao.getExerciseRecommendations(recentWorkoutThreshold, targetMuscleGroup, limit)
    }
    
    // Efficient Pagination
    override suspend fun searchExercisesPaginated(
        searchQuery: String, 
        limit: Int, 
        offset: Int
    ): List<ExerciseEntity> {
        return exerciseDao.searchExercisesPaginated(searchQuery, limit, offset)
    }
    
    override suspend fun getWorkoutsPaginated(
        startTime: Long,
        endTime: Long,
        minRating: Int,
        hasNotes: Int,
        limit: Int,
        offset: Int
    ): List<WorkoutEntity> {
        return workoutDao.getWorkoutsPaginated(startTime, endTime, minRating, hasNotes, limit, offset)
    }
    
    override suspend fun getExerciseSetsPaginated(
        exerciseId: String,
        minWeight: Double,
        maxWeight: Double,
        minReps: Int,
        maxReps: Int,
        minRpe: Int,
        maxRpe: Int,
        isWarmup: Int,
        startTime: Long,
        limit: Int,
        offset: Int
    ): List<ExerciseSetEntity> {
        return exerciseSetDao.getExerciseSetsPaginated(
            exerciseId, minWeight, maxWeight, minReps, maxReps, 
            minRpe, maxRpe, isWarmup, startTime, limit, offset
        )
    }
}