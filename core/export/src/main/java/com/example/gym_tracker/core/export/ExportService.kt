package com.example.gym_tracker.core.export

import com.example.gym_tracker.core.export.model.ExportData
import java.io.File
import java.time.Instant

/**
 * Service interface for data export operations
 */
interface ExportService {
    
    /**
     * Export all data to JSON format
     */
    suspend fun exportToJson(
        outputFile: File,
        startDate: Instant? = null,
        endDate: Instant? = null
    ): ExportResult
    
    /**
     * Export workout data to CSV format
     */
    suspend fun exportWorkoutsToCSV(
        outputFile: File,
        startDate: Instant? = null,
        endDate: Instant? = null
    ): ExportResult
    
    /**
     * Export exercise data to CSV format
     */
    suspend fun exportExercisesToCSV(
        outputFile: File
    ): ExportResult
    
    /**
     * Generate comprehensive PDF report
     */
    suspend fun generatePDFReport(
        outputFile: File,
        startDate: Instant? = null,
        endDate: Instant? = null,
        includeCharts: Boolean = true
    ): ExportResult
    
    /**
     * Get export data for processing
     */
    suspend fun getExportData(
        startDate: Instant? = null,
        endDate: Instant? = null
    ): ExportData
}

/**
 * Result of an export operation
 */
sealed class ExportResult {
    data class Success(
        val file: File,
        val recordCount: Int,
        val fileSizeBytes: Long
    ) : ExportResult()
    
    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : ExportResult()
}

/**
 * Export format options
 */
enum class ExportFormat {
    JSON,
    CSV_WORKOUTS,
    CSV_EXERCISES,
    PDF_REPORT
}

/**
 * Export configuration
 */
data class ExportConfig(
    val format: ExportFormat,
    val startDate: Instant? = null,
    val endDate: Instant? = null,
    val includeCharts: Boolean = true,
    val includePersonalRecords: Boolean = true,
    val includeGoals: Boolean = true,
    val includeTemplates: Boolean = true
)