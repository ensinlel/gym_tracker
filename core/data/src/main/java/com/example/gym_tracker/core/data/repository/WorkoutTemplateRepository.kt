package com.example.gym_tracker.core.data.repository

import com.example.gym_tracker.core.data.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Workout Template data operations
 */
interface WorkoutTemplateRepository {
    
    // Template CRUD operations
    fun getAllTemplates(): Flow<List<WorkoutTemplate>>
    suspend fun getTemplateById(templateId: String): WorkoutTemplate?
    fun getTemplateByIdFlow(templateId: String): Flow<WorkoutTemplate?>
    suspend fun insertTemplate(template: WorkoutTemplate)
    suspend fun updateTemplate(template: WorkoutTemplate)
    suspend fun deleteTemplate(template: WorkoutTemplate)
    suspend fun deleteTemplateById(templateId: String)
    
    // Template with exercises
    suspend fun getTemplateWithExercises(templateId: String): WorkoutTemplateWithExercises?
    fun getTemplateWithExercisesFlow(templateId: String): Flow<WorkoutTemplateWithExercises?>
    fun getAllTemplatesWithExercises(): Flow<List<WorkoutTemplateWithExercises>>
    
    // Template filtering and searching
    fun getTemplatesByCategory(category: WorkoutCategory): Flow<List<WorkoutTemplate>>
    fun getTemplatesByDifficulty(difficulty: DifficultyLevel): Flow<List<WorkoutTemplate>>
    fun getTemplatesByVisibility(isPublic: Boolean): Flow<List<WorkoutTemplate>>
    fun getTemplatesByUser(userId: String): Flow<List<WorkoutTemplate>>
    fun searchTemplates(searchQuery: String): Flow<List<WorkoutTemplate>>
    
    // Popular and recommended templates
    fun getPopularTemplates(limit: Int = 10): Flow<List<WorkoutTemplate>>
    fun getRecentTemplates(limit: Int = 10): Flow<List<WorkoutTemplate>>
    
    // Usage tracking
    suspend fun incrementUsageCount(templateId: String)
    suspend fun updateTemplateRating(templateId: String, rating: Double)
    
    // Template exercises
    fun getExercisesByTemplate(templateId: String): Flow<List<TemplateExercise>>
    suspend fun getTemplateExerciseById(exerciseId: String): TemplateExercise?
    suspend fun insertTemplateExercise(exercise: TemplateExercise)
    suspend fun updateTemplateExercise(exercise: TemplateExercise)
    suspend fun deleteTemplateExercise(exercise: TemplateExercise)
    suspend fun deleteTemplateExerciseById(exerciseId: String)
    suspend fun deleteExercisesByTemplate(templateId: String)
    fun getTemplateExercisesWithDetails(templateId: String): Flow<List<TemplateExerciseWithDetails>>
    suspend fun insertTemplateExercises(exercises: List<TemplateExercise>)
    suspend fun updateExerciseOrder(exerciseId: String, newOrder: Int)
    
    // Superset operations
    fun getSupersetExercises(templateId: String): Flow<List<TemplateExercise>>
    fun getExercisesBySupersetGroup(templateId: String, groupId: Int): Flow<List<TemplateExercise>>
    
    // Bulk operations
    suspend fun insertTemplates(templates: List<WorkoutTemplate>)
    suspend fun deleteTemplatesByUser(userId: String)
}

/**
 * Repository interface for Workout Routine data operations
 */
interface WorkoutRoutineRepository {
    
    // Routine CRUD operations
    fun getAllRoutines(): Flow<List<WorkoutRoutine>>
    fun getActiveRoutines(): Flow<List<WorkoutRoutine>>
    suspend fun getRoutineById(routineId: String): WorkoutRoutine?
    fun getRoutineByIdFlow(routineId: String): Flow<WorkoutRoutine?>
    suspend fun insertRoutine(routine: WorkoutRoutine)
    suspend fun updateRoutine(routine: WorkoutRoutine)
    suspend fun deleteRoutine(routine: WorkoutRoutine)
    suspend fun deleteRoutineById(routineId: String)
    
    // Routine with schedules
    suspend fun getRoutineWithDetails(routineId: String): WorkoutRoutineWithDetails?
    fun getRoutineWithDetailsFlow(routineId: String): Flow<WorkoutRoutineWithDetails?>
    fun getAllRoutinesWithDetails(): Flow<List<WorkoutRoutineWithDetails>>
    fun getActiveRoutinesWithDetails(): Flow<List<WorkoutRoutineWithDetails>>
    
    // Schedule operations
    fun getSchedulesByRoutine(routineId: String): Flow<List<RoutineSchedule>>
    fun getSchedulesByDay(dayOfWeek: Int): Flow<List<RoutineSchedule>>
    suspend fun getScheduleById(scheduleId: String): RoutineSchedule?
    suspend fun insertSchedule(schedule: RoutineSchedule)
    suspend fun updateSchedule(schedule: RoutineSchedule)
    suspend fun deleteSchedule(schedule: RoutineSchedule)
    suspend fun deleteScheduleById(scheduleId: String)
    suspend fun deleteSchedulesByRoutine(routineId: String)
    
    // Schedule with template details
    fun getSchedulesWithTemplates(routineId: String): Flow<List<RoutineScheduleWithTemplate>>
    fun getSchedulesWithTemplatesByDay(dayOfWeek: Int): Flow<List<RoutineScheduleWithTemplate>>
    
    // Bulk operations
    suspend fun insertSchedules(schedules: List<RoutineSchedule>)
}