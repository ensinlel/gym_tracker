package com.example.gym_tracker.core.data.repository.impl

import com.example.gym_tracker.core.data.mapper.toDomainModel
import com.example.gym_tracker.core.data.mapper.toEntity
import com.example.gym_tracker.core.data.model.*
import com.example.gym_tracker.core.data.repository.WorkoutRoutineRepository
import com.example.gym_tracker.core.database.dao.RoutineScheduleDao
import com.example.gym_tracker.core.database.dao.WorkoutRoutineDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of WorkoutRoutineRepository using local database
 */
@Singleton
class WorkoutRoutineRepositoryImpl @Inject constructor(
    private val routineDao: WorkoutRoutineDao,
    private val scheduleDao: RoutineScheduleDao
) : WorkoutRoutineRepository {

    override fun getAllRoutines(): Flow<List<WorkoutRoutine>> {
        return routineDao.getAllRoutines().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getActiveRoutines(): Flow<List<WorkoutRoutine>> {
        return routineDao.getActiveRoutines().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getRoutineById(routineId: String): WorkoutRoutine? {
        return routineDao.getRoutineById(routineId)?.toDomainModel()
    }

    override fun getRoutineByIdFlow(routineId: String): Flow<WorkoutRoutine?> {
        return routineDao.getRoutineByIdFlow(routineId).map { entity ->
            entity?.toDomainModel()
        }
    }

    override suspend fun insertRoutine(routine: WorkoutRoutine) {
        routineDao.insertRoutine(routine.toEntity())
    }

    override suspend fun updateRoutine(routine: WorkoutRoutine) {
        routineDao.updateRoutine(routine.toEntity())
    }

    override suspend fun deleteRoutine(routine: WorkoutRoutine) {
        routineDao.deleteRoutine(routine.toEntity())
    }

    override suspend fun deleteRoutineById(routineId: String) {
        routineDao.deleteRoutineById(routineId)
    }

    override suspend fun getRoutineWithDetails(routineId: String): WorkoutRoutineWithDetails? {
        return routineDao.getRoutineWithDetails(routineId)?.toDomainModel()
    }

    override fun getRoutineWithDetailsFlow(routineId: String): Flow<WorkoutRoutineWithDetails?> {
        return routineDao.getRoutineWithDetailsFlow(routineId).map { entity ->
            entity?.toDomainModel()
        }
    }

    override fun getAllRoutinesWithDetails(): Flow<List<WorkoutRoutineWithDetails>> {
        return routineDao.getAllRoutinesWithDetails().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getActiveRoutinesWithDetails(): Flow<List<WorkoutRoutineWithDetails>> {
        return routineDao.getActiveRoutinesWithDetails().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getSchedulesByRoutine(routineId: String): Flow<List<RoutineSchedule>> {
        return scheduleDao.getSchedulesByRoutine(routineId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getSchedulesByDay(dayOfWeek: Int): Flow<List<RoutineSchedule>> {
        return scheduleDao.getSchedulesByDay(dayOfWeek).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getScheduleById(scheduleId: String): RoutineSchedule? {
        return scheduleDao.getScheduleById(scheduleId)?.toDomainModel()
    }

    override suspend fun insertSchedule(schedule: RoutineSchedule) {
        scheduleDao.insertSchedule(schedule.toEntity())
    }

    override suspend fun updateSchedule(schedule: RoutineSchedule) {
        scheduleDao.updateSchedule(schedule.toEntity())
    }

    override suspend fun deleteSchedule(schedule: RoutineSchedule) {
        scheduleDao.deleteSchedule(schedule.toEntity())
    }

    override suspend fun deleteScheduleById(scheduleId: String) {
        scheduleDao.deleteScheduleById(scheduleId)
    }

    override suspend fun deleteSchedulesByRoutine(routineId: String) {
        scheduleDao.deleteSchedulesByRoutine(routineId)
    }

    override fun getSchedulesWithTemplates(routineId: String): Flow<List<RoutineScheduleWithTemplate>> {
        return scheduleDao.getSchedulesWithTemplates(routineId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getSchedulesWithTemplatesByDay(dayOfWeek: Int): Flow<List<RoutineScheduleWithTemplate>> {
        return scheduleDao.getSchedulesWithTemplatesByDay(dayOfWeek).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun insertSchedules(schedules: List<RoutineSchedule>) {
        scheduleDao.insertSchedules(schedules.map { it.toEntity() })
    }
}