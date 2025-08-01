package com.example.gym_tracker.core.export

import com.example.gym_tracker.core.data.model.*
import com.example.gym_tracker.core.export.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Maps domain models to export models
 */
@Singleton
class ExportDataMapper @Inject constructor() {
    
    fun mapExercises(exercises: List<Exercise>): List<ExportExercise> {
        return exercises.map { exercise ->
            ExportExercise(
                id = exercise.id,
                name = exercise.name,
                category = exercise.category.name,
                muscleGroups = exercise.muscleGroups.map { it.name },
                equipment = exercise.equipment.name,
                instructions = exercise.instructions,
                createdAt = exercise.createdAt.toString(),
                updatedAt = exercise.updatedAt.toString(),
                isCustom = exercise.isCustom,
                isStarMarked = exercise.isStarMarked
            )
        }
    }
    
    fun mapWorkouts(workoutsWithDetails: List<WorkoutWithDetails>): List<ExportWorkout> {
        return workoutsWithDetails.map { workoutWithDetails ->
            val workout = workoutWithDetails.workout
            
            ExportWorkout(
                id = workout.id,
                name = workout.name,
                templateId = workout.templateId,
                startTime = workout.startTime.toString(),
                endTime = workout.endTime?.toString(),
                notes = workout.notes,
                rating = workout.rating,
                totalVolume = workout.totalVolume,
                averageRestTimeSeconds = workout.averageRestTime.seconds,
                exerciseInstances = mapExerciseInstances(workoutWithDetails.exerciseInstances)
            )
        }
    }
    
    fun mapWorkoutsWithCompleteDetails(workoutsWithDetails: List<WorkoutWithCompleteDetails>): List<ExportWorkout> {
        return workoutsWithDetails.map { workoutWithDetails ->
            val workout = workoutWithDetails.workout
            
            ExportWorkout(
                id = workout.id,
                name = workout.name,
                templateId = workout.templateId,
                startTime = workout.startTime.toString(),
                endTime = workout.endTime?.toString(),
                notes = workout.notes,
                rating = workout.rating,
                totalVolume = workout.totalVolume,
                averageRestTimeSeconds = workout.averageRestTime.seconds,
                exerciseInstances = mapExerciseInstancesWithDetails(workoutWithDetails.exerciseInstances)
            )
        }
    }
    
    private fun mapExerciseInstances(instances: List<ExerciseInstance>): List<ExportExerciseInstance> {
        return instances.map { instance ->
            ExportExerciseInstance(
                id = instance.id,
                exerciseId = instance.exerciseId,
                exerciseName = "", // Basic instance without exercise details
                orderInWorkout = instance.orderInWorkout,
                notes = instance.notes,
                sets = emptyList() // Basic instance without sets
            )
        }
    }
    
    private fun mapExerciseInstancesWithDetails(instances: List<ExerciseInstanceWithDetails>): List<ExportExerciseInstance> {
        return instances.map { instanceWithDetails ->
            val instance = instanceWithDetails.exerciseInstance
            
            ExportExerciseInstance(
                id = instance.id,
                exerciseId = instance.exerciseId,
                exerciseName = instanceWithDetails.exercise.name,
                orderInWorkout = instance.orderInWorkout,
                notes = instance.notes,
                sets = mapExerciseSets(instanceWithDetails.sets)
            )
        }
    }
    
    private fun mapExerciseSets(sets: List<ExerciseSet>): List<ExportExerciseSet> {
        return sets.map { set ->
            ExportExerciseSet(
                id = set.id,
                setNumber = set.setNumber,
                weight = set.weight,
                reps = set.reps,
                restTimeSeconds = set.restTime.seconds,
                rpe = set.rpe,
                tempo = set.tempo,
                isWarmup = set.isWarmup,
                isFailure = set.isFailure,
                notes = set.notes
            )
        }
    }
}