package com.example.gym_tracker.feature.profile

import android.content.Context
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.core.export.ExportFormat
import com.example.gym_tracker.core.export.ExportResult
import com.example.gym_tracker.core.export.ExportService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val exportService: ExportService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()
    
    fun selectFormat(format: ExportFormat) {
        _uiState.value = _uiState.value.copy(selectedFormat = format)
    }
    
    fun toggleDateRange(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            useDateRange = enabled,
            startDate = if (enabled) LocalDate.now().minusMonths(1) else null,
            endDate = if (enabled) LocalDate.now() else null
        )
    }
    
    fun showStartDatePicker() {
        // TODO: Implement date picker
    }
    
    fun showEndDatePicker() {
        // TODO: Implement date picker
    }
    
    fun setStartDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(startDate = date)
    }
    
    fun setEndDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(endDate = date)
    }
    
    fun startExport(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isExporting = true,
                exportStatus = null
            )
            
            try {
                val currentState = _uiState.value
                val outputFile = createOutputFile(context, currentState.selectedFormat)
                
                val startInstant = currentState.startDate?.let { date ->
                    date.atStartOfDay(ZoneId.systemDefault()).toInstant()
                }
                val endInstant = currentState.endDate?.let { date ->
                    date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                }
                
                val result = when (currentState.selectedFormat) {
                    ExportFormat.JSON -> exportService.exportToJson(
                        outputFile = outputFile,
                        startDate = startInstant,
                        endDate = endInstant
                    )
                    ExportFormat.CSV_WORKOUTS -> exportService.exportWorkoutsToCSV(
                        outputFile = outputFile,
                        startDate = startInstant,
                        endDate = endInstant
                    )
                    ExportFormat.CSV_EXERCISES -> exportService.exportExercisesToCSV(
                        outputFile = outputFile
                    )
                    ExportFormat.PDF_REPORT -> exportService.generatePDFReport(
                        outputFile = outputFile,
                        startDate = startInstant,
                        endDate = endInstant,
                        includeCharts = true
                    )
                }
                
                val status = when (result) {
                    is ExportResult.Success -> ExportStatus(
                        isSuccess = true,
                        message = "Successfully exported ${result.recordCount} records (${formatFileSize(result.fileSizeBytes)})",
                        filePath = result.file.absolutePath
                    )
                    is ExportResult.Error -> ExportStatus(
                        isSuccess = false,
                        message = result.message,
                        filePath = null
                    )
                }
                
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    exportStatus = status
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    exportStatus = ExportStatus(
                        isSuccess = false,
                        message = "Export failed: ${e.message}",
                        filePath = null
                    )
                )
            }
        }
    }
    
    private fun createOutputFile(context: Context, format: ExportFormat): File {
        val timestamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
            .format(java.time.LocalDateTime.now())
        
        val fileName = when (format) {
            ExportFormat.JSON -> "gym_tracker_export_$timestamp.json"
            ExportFormat.CSV_WORKOUTS -> "gym_tracker_workouts_$timestamp.csv"
            ExportFormat.CSV_EXERCISES -> "gym_tracker_exercises_$timestamp.csv"
            ExportFormat.PDF_REPORT -> "gym_tracker_report_$timestamp.pdf"
        }
        
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        return File(downloadsDir, fileName)
    }
    
    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> "${bytes / (1024 * 1024)} MB"
        }
    }
}

data class ExportUiState(
    val selectedFormat: ExportFormat = ExportFormat.JSON,
    val useDateRange: Boolean = false,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val isExporting: Boolean = false,
    val exportStatus: ExportStatus? = null
)

data class ExportStatus(
    val isSuccess: Boolean,
    val message: String,
    val filePath: String?
)