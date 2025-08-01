package com.example.gym_tracker.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gym_tracker.core.export.ExportFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Export Data",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Export format selection
        Text(
            text = "Export Format",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        ExportFormatCard(
            title = "JSON Export",
            description = "Complete data export in JSON format. Includes all workouts, exercises, and settings.",
            icon = Icons.Default.Settings,
            isSelected = uiState.selectedFormat == ExportFormat.JSON,
            onClick = { viewModel.selectFormat(ExportFormat.JSON) }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        ExportFormatCard(
            title = "CSV - Workouts",
            description = "Workout data in spreadsheet format. Perfect for analysis in Excel or Google Sheets.",
            icon = Icons.Default.List,
            isSelected = uiState.selectedFormat == ExportFormat.CSV_WORKOUTS,
            onClick = { viewModel.selectFormat(ExportFormat.CSV_WORKOUTS) }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        ExportFormatCard(
            title = "CSV - Exercises",
            description = "Exercise library in spreadsheet format.",
            icon = Icons.Default.List,
            isSelected = uiState.selectedFormat == ExportFormat.CSV_EXERCISES,
            onClick = { viewModel.selectFormat(ExportFormat.CSV_EXERCISES) }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        ExportFormatCard(
            title = "PDF Report",
            description = "Comprehensive report with charts and summaries. Great for sharing or printing.",
            icon = Icons.Default.Info,
            isSelected = uiState.selectedFormat == ExportFormat.PDF_REPORT,
            onClick = { viewModel.selectFormat(ExportFormat.PDF_REPORT) }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Date range selection
        Text(
            text = "Date Range (Optional)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Switch(
                checked = uiState.useDateRange,
                onCheckedChange = viewModel::toggleDateRange
            )
            Text(
                text = "Filter by date range",
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        
        if (uiState.useDateRange) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.startDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "",
                    onValueChange = { },
                    label = { Text("Start Date") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { viewModel.showStartDatePicker() }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select start date")
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                
                OutlinedTextField(
                    value = uiState.endDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "",
                    onValueChange = { },
                    label = { Text("End Date") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { viewModel.showEndDatePicker() }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select end date")
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Export button
        Button(
            onClick = { viewModel.startExport(context) },
            enabled = !uiState.isExporting,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isExporting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Exporting...")
            } else {
                Icon(Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export Data")
            }
        }
        
        // Export status
        uiState.exportStatus?.let { status ->
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (status.isSuccess) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (status.isSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (status.isSuccess) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (status.isSuccess) "Export Successful" else "Export Failed",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = status.message,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    if (status.isSuccess && status.filePath != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "File saved to: ${status.filePath}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExportFormatCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}