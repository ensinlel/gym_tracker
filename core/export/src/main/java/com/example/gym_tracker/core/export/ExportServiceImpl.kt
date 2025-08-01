package com.example.gym_tracker.core.export

import com.example.gym_tracker.core.data.repository.ExerciseRepository
import com.example.gym_tracker.core.data.repository.WorkoutRepository
import com.example.gym_tracker.core.data.model.*
import com.example.gym_tracker.core.export.model.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ExportService
 */
@Singleton
class ExportServiceImpl @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val csvExporter: CSVExporter,
    private val pdfExporter: PDFExporter,
    private val dataMapper: ExportDataMapper
) : ExportService {
    
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    
    override suspend fun exportToJson(
        outputFile: File,
        startDate: Instant?,
        endDate: Instant?
    ): ExportResult {
        return try {
            val exportData = getExportData(startDate, endDate)
            val jsonString = json.encodeToString(exportData)
            
            outputFile.writeText(jsonString)
            
            ExportResult.Success(
                file = outputFile,
                recordCount = exportData.workouts.size + exportData.exercises.size,
                fileSizeBytes = outputFile.length()
            )
        } catch (e: Exception) {
            ExportResult.Error("Failed to export to JSON", e)
        }
    }
    
    override suspend fun exportWorkoutsToCSV(
        outputFile: File,
        startDate: Instant?,
        endDate: Instant?
    ): ExportResult {
        return try {
            val exportData = getExportData(startDate, endDate)
            val recordCount = csvExporter.exportWorkoutsToCSV(exportData.workouts, outputFile)
            
            ExportResult.Success(
                file = outputFile,
                recordCount = recordCount,
                fileSizeBytes = outputFile.length()
            )
        } catch (e: Exception) {
            ExportResult.Error("Failed to export workouts to CSV", e)
        }
    }
    
    override suspend fun exportExercisesToCSV(
        outputFile: File
    ): ExportResult {
        return try {
            val exportData = getExportData()
            val recordCount = csvExporter.exportExercisesToCSV(exportData.exercises, outputFile)
            
            ExportResult.Success(
                file = outputFile,
                recordCount = recordCount,
                fileSizeBytes = outputFile.length()
            )
        } catch (e: Exception) {
            ExportResult.Error("Failed to export exercises to CSV", e)
        }
    }
    
    override suspend fun generatePDFReport(
        outputFile: File,
        startDate: Instant?,
        endDate: Instant?,
        includeCharts: Boolean
    ): ExportResult {
        return try {
            val exportData = getExportData(startDate, endDate)
            val recordCount = pdfExporter.generatePDFReport(
                exportData = exportData,
                outputFile = outputFile,
                includeCharts = includeCharts
            )
            
            ExportResult.Success(
                file = outputFile,
                recordCount = recordCount,
                fileSizeBytes = outputFile.length()
            )
        } catch (e: Exception) {
            ExportResult.Error("Failed to generate PDF report", e)
        }
    }
    
    override suspend fun getExportData(
        startDate: Instant?,
        endDate: Instant?
    ): ExportData {
        // Get all exercises
        val exercises = exerciseRepository.getAllExercises().first()
        
        // Get workouts (filtered by date if specified)
        val workouts = if (startDate != null && endDate != null) {
            workoutRepository.getWorkoutsByDateRange(
                startDate.toEpochMilli(),
                endDate.toEpochMilli()
            ).first()
        } else {
            workoutRepository.getAllWorkouts().first()
        }
        
        // Get complete workout details for each workout
        val workoutsWithCompleteDetails = workouts.mapNotNull { workout ->
            // For now, we'll use basic details since complete details might not be implemented yet
            workoutRepository.getWorkoutWithDetailsById(workout.id)?.let { basicDetails ->
                // Convert to complete details structure (simplified for now)
                WorkoutWithCompleteDetails(
                    workout = basicDetails.workout,
                    exerciseInstances = basicDetails.exerciseInstances.map { instance ->
                        ExerciseInstanceWithDetails(
                            exerciseInstance = instance,
                            exercise = exercises.find { it.id == instance.exerciseId } 
                                ?: Exercise(
                                    id = instance.exerciseId,
                                    name = "Unknown Exercise",
                                    category = com.example.gym_tracker.core.common.enums.ExerciseCategory.CHEST,
                                    muscleGroups = emptyList(),
                                    equipment = com.example.gym_tracker.core.common.enums.Equipment.BODYWEIGHT,
                                    instructions = emptyList(),
                                    createdAt = java.time.Instant.now(),
                                    updatedAt = java.time.Instant.now(),
                                    isCustom = false,
                                    isStarMarked = false
                                ),
                            sets = emptyList() // TODO: Get actual sets when available
                        )
                    }
                )
            }
        }
        
        // Create metadata
        val metadata = ExportMetadata(
            version = "1.0",
            exportDate = Instant.now().toString(),
            appVersion = "2.0.0", // TODO: Get from BuildConfig
            totalWorkouts = workouts.size,
            totalExercises = exercises.size,
            dateRange = if (startDate != null && endDate != null) {
                ExportDateRange(
                    startDate = startDate.toString(),
                    endDate = endDate.toString()
                )
            } else null
        )
        
        return ExportData(
            metadata = metadata,
            exercises = dataMapper.mapExercises(exercises),
            workouts = dataMapper.mapWorkoutsWithCompleteDetails(workoutsWithCompleteDetails),
            templates = emptyList(), // TODO: Implement when templates are available
            goals = emptyList(),     // TODO: Implement when goals are available
            personalRecords = emptyList() // TODO: Implement when PRs are available
        )
    }
}