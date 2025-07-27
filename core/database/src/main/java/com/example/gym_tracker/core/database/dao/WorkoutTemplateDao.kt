package com.example.gym_tracker.core.database.dao

import androidx.room.*
import com.example.gym_tracker.core.database.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO for workout template operations
 */
@Dao
interface WorkoutTemplateDao {
    
    // Basic CRUD operations
    @Query("SELECT * FROM workout_templates ORDER BY name ASC")
    fun getAllTemplates(): Flow<List<WorkoutTemplateEntity>>
    
    @Query("SELECT * FROM workout_templates WHERE id = :templateId")
    suspend fun getTemplateById(templateId: String): WorkoutTemplateEntity?
    
    @Query("SELECT * FROM workout_templates WHERE id = :templateId")
    fun getTemplateByIdFlow(templateId: String): Flow<WorkoutTemplateEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: WorkoutTemplateEntity)
    
    @Update
    suspend fun updateTemplate(template: WorkoutTemplateEntity)
    
    @Delete
    suspend fun deleteTemplate(template: WorkoutTemplateEntity)
    
    @Query("DELETE FROM workout_templates WHERE id = :templateId")
    suspend fun deleteTemplateById(templateId: String)
    
    // Template with exercises
    @Transaction
    @Query("SELECT * FROM workout_templates WHERE id = :templateId")
    suspend fun getTemplateWithExercises(templateId: String): WorkoutTemplateWithExercisesEntity?
    
    @Transaction
    @Query("SELECT * FROM workout_templates WHERE id = :templateId")
    fun getTemplateWithExercisesFlow(templateId: String): Flow<WorkoutTemplateWithExercisesEntity?>
    
    @Transaction
    @Query("SELECT * FROM workout_templates ORDER BY name ASC")
    fun getAllTemplatesWithExercises(): Flow<List<WorkoutTemplateWithExercisesEntity>>
    
    // Filtering and searching
    @Query("SELECT * FROM workout_templates WHERE category = :category ORDER BY name ASC")
    fun getTemplatesByCategory(category: String): Flow<List<WorkoutTemplateEntity>>
    
    @Query("SELECT * FROM workout_templates WHERE difficulty = :difficulty ORDER BY name ASC")
    fun getTemplatesByDifficulty(difficulty: String): Flow<List<WorkoutTemplateEntity>>
    
    @Query("SELECT * FROM workout_templates WHERE isPublic = :isPublic ORDER BY name ASC")
    fun getTemplatesByVisibility(isPublic: Boolean): Flow<List<WorkoutTemplateEntity>>
    
    @Query("SELECT * FROM workout_templates WHERE createdBy = :userId ORDER BY createdAt DESC")
    fun getTemplatesByUser(userId: String): Flow<List<WorkoutTemplateEntity>>
    
    @Query("""
        SELECT * FROM workout_templates 
        WHERE name LIKE '%' || :searchQuery || '%' 
        OR description LIKE '%' || :searchQuery || '%'
        ORDER BY name ASC
    """)
    fun searchTemplates(searchQuery: String): Flow<List<WorkoutTemplateEntity>>
    
    // Popular and recommended templates
    @Query("SELECT * FROM workout_templates WHERE isPublic = 1 ORDER BY rating DESC, usageCount DESC LIMIT :limit")
    fun getPopularTemplates(limit: Int = 10): Flow<List<WorkoutTemplateEntity>>
    
    @Query("SELECT * FROM workout_templates WHERE isPublic = 1 ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentTemplates(limit: Int = 10): Flow<List<WorkoutTemplateEntity>>
    
    // Usage tracking
    @Query("UPDATE workout_templates SET usageCount = usageCount + 1 WHERE id = :templateId")
    suspend fun incrementUsageCount(templateId: String)
    
    @Query("UPDATE workout_templates SET rating = :rating WHERE id = :templateId")
    suspend fun updateTemplateRating(templateId: String, rating: Double)
    
    // Bulk operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<WorkoutTemplateEntity>)
    
    @Query("DELETE FROM workout_templates WHERE createdBy = :userId")
    suspend fun deleteTemplatesByUser(userId: String)
}

/**
 * DAO for template exercise operations
 */
@Dao
interface TemplateExerciseDao {
    
    // Basic CRUD operations
    @Query("SELECT * FROM template_exercises WHERE templateId = :templateId ORDER BY orderInTemplate ASC")
    fun getExercisesByTemplate(templateId: String): Flow<List<TemplateExerciseEntity>>
    
    @Query("SELECT * FROM template_exercises WHERE id = :exerciseId")
    suspend fun getTemplateExerciseById(exerciseId: String): TemplateExerciseEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateExercise(exercise: TemplateExerciseEntity)
    
    @Update
    suspend fun updateTemplateExercise(exercise: TemplateExerciseEntity)
    
    @Delete
    suspend fun deleteTemplateExercise(exercise: TemplateExerciseEntity)
    
    @Query("DELETE FROM template_exercises WHERE id = :exerciseId")
    suspend fun deleteTemplateExerciseById(exerciseId: String)
    
    @Query("DELETE FROM template_exercises WHERE templateId = :templateId")
    suspend fun deleteExercisesByTemplate(templateId: String)
    
    // Template exercises with details
    @Transaction
    @Query("SELECT * FROM template_exercises WHERE templateId = :templateId ORDER BY orderInTemplate ASC")
    fun getTemplateExercisesWithDetails(templateId: String): Flow<List<TemplateExerciseWithDetailsEntity>>
    
    // Bulk operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateExercises(exercises: List<TemplateExerciseEntity>)
    
    @Query("UPDATE template_exercises SET orderInTemplate = :newOrder WHERE id = :exerciseId")
    suspend fun updateExerciseOrder(exerciseId: String, newOrder: Int)
    
    // Superset operations
    @Query("SELECT * FROM template_exercises WHERE templateId = :templateId AND isSuperset = 1 ORDER BY supersetGroup ASC, orderInTemplate ASC")
    fun getSupersetExercises(templateId: String): Flow<List<TemplateExerciseEntity>>
    
    @Query("SELECT * FROM template_exercises WHERE templateId = :templateId AND supersetGroup = :groupId ORDER BY orderInTemplate ASC")
    fun getExercisesBySupersetGroup(templateId: String, groupId: Int): Flow<List<TemplateExerciseEntity>>
}

/**
 * DAO for workout routine operations
 */
@Dao
interface WorkoutRoutineDao {
    
    // Basic CRUD operations
    @Query("SELECT * FROM workout_routines ORDER BY name ASC")
    fun getAllRoutines(): Flow<List<WorkoutRoutineEntity>>
    
    @Query("SELECT * FROM workout_routines WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveRoutines(): Flow<List<WorkoutRoutineEntity>>
    
    @Query("SELECT * FROM workout_routines WHERE id = :routineId")
    suspend fun getRoutineById(routineId: String): WorkoutRoutineEntity?
    
    @Query("SELECT * FROM workout_routines WHERE id = :routineId")
    fun getRoutineByIdFlow(routineId: String): Flow<WorkoutRoutineEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: WorkoutRoutineEntity)
    
    @Update
    suspend fun updateRoutine(routine: WorkoutRoutineEntity)
    
    @Delete
    suspend fun deleteRoutine(routine: WorkoutRoutineEntity)
    
    @Query("DELETE FROM workout_routines WHERE id = :routineId")
    suspend fun deleteRoutineById(routineId: String)
    
    // Routine with schedules
    @Transaction
    @Query("SELECT * FROM workout_routines WHERE id = :routineId")
    suspend fun getRoutineWithDetails(routineId: String): WorkoutRoutineWithDetailsEntity?
    
    @Transaction
    @Query("SELECT * FROM workout_routines WHERE id = :routineId")
    fun getRoutineWithDetailsFlow(routineId: String): Flow<WorkoutRoutineWithDetailsEntity?>
    
    @Transaction
    @Query("SELECT * FROM workout_routines ORDER BY name ASC")
    fun getAllRoutinesWithDetails(): Flow<List<WorkoutRoutineWithDetailsEntity>>
    
    @Transaction
    @Query("SELECT * FROM workout_routines WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveRoutinesWithDetails(): Flow<List<WorkoutRoutineWithDetailsEntity>>
}

/**
 * DAO for routine schedule operations
 */
@Dao
interface RoutineScheduleDao {
    
    // Basic CRUD operations
    @Query("SELECT * FROM routine_schedules WHERE routineId = :routineId ORDER BY dayOfWeek ASC")
    fun getSchedulesByRoutine(routineId: String): Flow<List<RoutineScheduleEntity>>
    
    @Query("SELECT * FROM routine_schedules WHERE dayOfWeek = :dayOfWeek AND isActive = 1")
    fun getSchedulesByDay(dayOfWeek: Int): Flow<List<RoutineScheduleEntity>>
    
    @Query("SELECT * FROM routine_schedules WHERE id = :scheduleId")
    suspend fun getScheduleById(scheduleId: String): RoutineScheduleEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: RoutineScheduleEntity)
    
    @Update
    suspend fun updateSchedule(schedule: RoutineScheduleEntity)
    
    @Delete
    suspend fun deleteSchedule(schedule: RoutineScheduleEntity)
    
    @Query("DELETE FROM routine_schedules WHERE id = :scheduleId")
    suspend fun deleteScheduleById(scheduleId: String)
    
    @Query("DELETE FROM routine_schedules WHERE routineId = :routineId")
    suspend fun deleteSchedulesByRoutine(routineId: String)
    
    // Schedule with template details
    @Transaction
    @Query("SELECT * FROM routine_schedules WHERE routineId = :routineId ORDER BY dayOfWeek ASC")
    fun getSchedulesWithTemplates(routineId: String): Flow<List<RoutineScheduleWithTemplateEntity>>
    
    @Transaction
    @Query("SELECT * FROM routine_schedules WHERE dayOfWeek = :dayOfWeek AND isActive = 1")
    fun getSchedulesWithTemplatesByDay(dayOfWeek: Int): Flow<List<RoutineScheduleWithTemplateEntity>>
    
    // Bulk operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<RoutineScheduleEntity>)
}