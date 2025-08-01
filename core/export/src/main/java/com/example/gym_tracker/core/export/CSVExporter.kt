package com.example.gym_tracker.core.export

import com.example.gym_tracker.core.export.model.ExportExercise
import com.example.gym_tracker.core.export.model.ExportWorkout
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles CSV export operations
 */
@Singleton
class CSVExporter @Inject constructor() {
    
    /**
     * Export workouts to CSV format
     */
    fun exportWorkoutsToCSV(workouts: List<ExportWorkout>, outputFile: File): Int {
        var recordCount = 0
        
        FileWriter(outputFile).use { fileWriter ->
            CSVWriter(fileWriter).use { csvWriter ->
                // Write header
                csvWriter.writeNext(arrayOf(
                    "Workout ID",
                    "Workout Name",
                    "Template ID",
                    "Start Time",
                    "End Time",
                    "Duration (minutes)",
                    "Notes",
                    "Rating",
                    "Total Volume",
                    "Average Rest Time (seconds)",
                    "Exercise Name",
                    "Exercise Order",
                    "Set Number",
                    "Weight",
                    "Reps",
                    "Rest Time (seconds)",
                    "RPE",
                    "Tempo",
                    "Is Warmup",
                    "Is Failure",
                    "Set Notes"
                ))
                
                // Write workout data
                workouts.forEach { workout ->
                    if (workout.exerciseInstances.isEmpty()) {
                        // Workout with no exercises
                        csvWriter.writeNext(arrayOf(
                            workout.id,
                            workout.name,
                            workout.templateId ?: "",
                            workout.startTime,
                            workout.endTime ?: "",
                            calculateDurationMinutes(workout.startTime, workout.endTime).toString(),
                            workout.notes,
                            workout.rating?.toString() ?: "",
                            workout.totalVolume.toString(),
                            workout.averageRestTimeSeconds.toString(),
                            "", "", "", "", "", "", "", "", "", "", ""
                        ))
                        recordCount++
                    } else {
                        // Workout with exercises
                        workout.exerciseInstances.forEach { exerciseInstance ->
                            if (exerciseInstance.sets.isEmpty()) {
                                // Exercise with no sets
                                csvWriter.writeNext(arrayOf(
                                    workout.id,
                                    workout.name,
                                    workout.templateId ?: "",
                                    workout.startTime,
                                    workout.endTime ?: "",
                                    calculateDurationMinutes(workout.startTime, workout.endTime).toString(),
                                    workout.notes,
                                    workout.rating?.toString() ?: "",
                                    workout.totalVolume.toString(),
                                    workout.averageRestTimeSeconds.toString(),
                                    exerciseInstance.exerciseName,
                                    exerciseInstance.orderInWorkout.toString(),
                                    "", "", "", "", "", "", "", "", ""
                                ))
                                recordCount++
                            } else {
                                // Exercise with sets
                                exerciseInstance.sets.forEach { set ->
                                    csvWriter.writeNext(arrayOf(
                                        workout.id,
                                        workout.name,
                                        workout.templateId ?: "",
                                        workout.startTime,
                                        workout.endTime ?: "",
                                        calculateDurationMinutes(workout.startTime, workout.endTime).toString(),
                                        workout.notes,
                                        workout.rating?.toString() ?: "",
                                        workout.totalVolume.toString(),
                                        workout.averageRestTimeSeconds.toString(),
                                        exerciseInstance.exerciseName,
                                        exerciseInstance.orderInWorkout.toString(),
                                        set.setNumber.toString(),
                                        set.weight.toString(),
                                        set.reps.toString(),
                                        set.restTimeSeconds.toString(),
                                        set.rpe?.toString() ?: "",
                                        set.tempo ?: "",
                                        set.isWarmup.toString(),
                                        set.isFailure.toString(),
                                        set.notes
                                    ))
                                    recordCount++
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return recordCount
    }
    
    /**
     * Export exercises to CSV format
     */
    fun exportExercisesToCSV(exercises: List<ExportExercise>, outputFile: File): Int {
        FileWriter(outputFile).use { fileWriter ->
            CSVWriter(fileWriter).use { csvWriter ->
                // Write header
                csvWriter.writeNext(arrayOf(
                    "Exercise ID",
                    "Name",
                    "Category",
                    "Muscle Groups",
                    "Equipment",
                    "Instructions",
                    "Created At",
                    "Updated At",
                    "Is Custom",
                    "Is Star Marked"
                ))
                
                // Write exercise data
                exercises.forEach { exercise ->
                    csvWriter.writeNext(arrayOf(
                        exercise.id,
                        exercise.name,
                        exercise.category,
                        exercise.muscleGroups.joinToString("; "),
                        exercise.equipment,
                        exercise.instructions.joinToString("; "),
                        exercise.createdAt,
                        exercise.updatedAt,
                        exercise.isCustom.toString(),
                        exercise.isStarMarked.toString()
                    ))
                }
            }
        }
        
        return exercises.size
    }
    
    private fun calculateDurationMinutes(startTime: String, endTime: String?): Long {
        return if (endTime != null) {
            try {
                val start = java.time.Instant.parse(startTime)
                val end = java.time.Instant.parse(endTime)
                java.time.Duration.between(start, end).toMinutes()
            } catch (e: Exception) {
                0L
            }
        } else {
            0L
        }
    }
}