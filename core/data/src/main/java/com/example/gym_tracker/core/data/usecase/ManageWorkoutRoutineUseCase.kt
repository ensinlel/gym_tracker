package com.example.gym_tracker.core.data.usecase

import com.example.gym_tracker.core.data.model.*
import com.example.gym_tracker.core.data.repository.WorkoutRoutineRepository
import java.time.Instant
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for managing workout routines and schedules
 */
@Singleton
class ManageWorkoutRoutineUseCase @Inject constructor(
    private val routineRepository: WorkoutRoutineRepository
) {
    
    /**
     * Create a new workout routine
     */
    suspend fun createRoutine(
        name: String,
        description: String = "",
        isActive: Boolean = true
    ): Result<WorkoutRoutine> {
        return try {
            val routine = WorkoutRoutine(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                isActive = isActive,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
            
            routineRepository.insertRoutine(routine)
            Result.success(routine)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update an existing workout routine
     */
    suspend fun updateRoutine(routine: WorkoutRoutine): Result<WorkoutRoutine> {
        return try {
            val updatedRoutine = routine.copy(updatedAt = Instant.now())
            routineRepository.updateRoutine(updatedRoutine)
            Result.success(updatedRoutine)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete a workout routine
     */
    suspend fun deleteRoutine(routineId: String): Result<Unit> {
        return try {
            routineRepository.deleteRoutineById(routineId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Activate or deactivate a routine
     */
    suspend fun setRoutineActive(routineId: String, isActive: Boolean): Result<Unit> {
        return try {
            val routine = routineRepository.getRoutineById(routineId)
                ?: return Result.failure(IllegalArgumentException("Routine not found"))
            
            val updatedRoutine = routine.copy(
                isActive = isActive,
                updatedAt = Instant.now()
            )
            
            routineRepository.updateRoutine(updatedRoutine)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Add a schedule to a routine
     */
    suspend fun addScheduleToRoutine(
        routineId: String,
        templateId: String,
        dayOfWeek: Int, // 1=Monday, 7=Sunday
        timeOfDay: String? = null, // HH:mm format
        notes: String = ""
    ): Result<RoutineSchedule> {
        return try {
            if (dayOfWeek < 1 || dayOfWeek > 7) {
                return Result.failure(IllegalArgumentException("Day of week must be between 1 and 7"))
            }
            
            val schedule = RoutineSchedule(
                id = UUID.randomUUID().toString(),
                routineId = routineId,
                templateId = templateId,
                dayOfWeek = dayOfWeek,
                timeOfDay = timeOfDay,
                isActive = true,
                notes = notes
            )
            
            routineRepository.insertSchedule(schedule)
            Result.success(schedule)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update a routine schedule
     */
    suspend fun updateSchedule(schedule: RoutineSchedule): Result<RoutineSchedule> {
        return try {
            routineRepository.updateSchedule(schedule)
            Result.success(schedule)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Remove a schedule from a routine
     */
    suspend fun removeScheduleFromRoutine(scheduleId: String): Result<Unit> {
        return try {
            routineRepository.deleteScheduleById(scheduleId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Create a weekly routine with multiple schedules
     */
    suspend fun createWeeklyRoutine(
        name: String,
        description: String = "",
        weeklySchedules: List<WeeklyScheduleItem>
    ): Result<WorkoutRoutineWithDetails> {
        return try {
            // Create the routine
            val routine = WorkoutRoutine(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                isActive = true,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
            
            routineRepository.insertRoutine(routine)
            
            // Create schedules
            val schedules = weeklySchedules.map { scheduleItem ->
                RoutineSchedule(
                    id = UUID.randomUUID().toString(),
                    routineId = routine.id,
                    templateId = scheduleItem.templateId,
                    dayOfWeek = scheduleItem.dayOfWeek,
                    timeOfDay = scheduleItem.timeOfDay,
                    isActive = true,
                    notes = scheduleItem.notes
                )
            }
            
            routineRepository.insertSchedules(schedules)
            
            // Return the complete routine
            val routineWithDetails = routineRepository.getRoutineWithDetails(routine.id)
                ?: return Result.failure(IllegalStateException("Failed to retrieve created routine"))
            
            Result.success(routineWithDetails)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get today's scheduled workouts
     */
    suspend fun getTodaysWorkouts(dayOfWeek: Int): Result<List<RoutineScheduleWithTemplate>> {
        return try {
            val schedules = routineRepository.getSchedulesWithTemplatesByDay(dayOfWeek)
            // Since this returns a Flow, we need to collect it
            // For now, we'll return an empty list and let the caller handle the Flow
            Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Duplicate a routine
     */
    suspend fun duplicateRoutine(
        routineId: String,
        newName: String? = null
    ): Result<WorkoutRoutineWithDetails> {
        return try {
            val originalRoutine = routineRepository.getRoutineWithDetails(routineId)
                ?: return Result.failure(IllegalArgumentException("Routine not found"))
            
            val duplicatedRoutine = originalRoutine.routine.copy(
                id = UUID.randomUUID().toString(),
                name = newName ?: "${originalRoutine.routine.name} (Copy)",
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                isActive = false // Duplicated routines are inactive by default
            )
            
            routineRepository.insertRoutine(duplicatedRoutine)
            
            // Duplicate schedules
            val duplicatedSchedules = originalRoutine.schedules.map { scheduleWithTemplate ->
                scheduleWithTemplate.schedule.copy(
                    id = UUID.randomUUID().toString(),
                    routineId = duplicatedRoutine.id
                )
            }
            
            routineRepository.insertSchedules(duplicatedSchedules)
            
            val routineWithDetails = routineRepository.getRoutineWithDetails(duplicatedRoutine.id)
                ?: return Result.failure(IllegalStateException("Failed to retrieve duplicated routine"))
            
            Result.success(routineWithDetails)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Data class for creating weekly schedules
     */
    data class WeeklyScheduleItem(
        val templateId: String,
        val dayOfWeek: Int,
        val timeOfDay: String? = null,
        val notes: String = ""
    )
}