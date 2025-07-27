package com.example.gym_tracker.feature.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gym_tracker.core.data.model.*
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import kotlin.math.*

/**
 * Before/After Comparison Chart Component
 * Shows statistical comparison between two time periods
 */
@Composable
fun BeforeAfterComparisonChart(
    comparison: BeforeAfterComparison,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with comparison type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${comparison.comparisonType.name.lowercase().replaceFirstChar { it.uppercase() }} Comparison",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                StatisticalSignificanceBadge(comparison.statisticalSignificance)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Before/After comparison bars
            BeforeAfterBars(
                beforeValue = comparison.beforePeriod.totalVolume,
                afterValue = comparison.afterPeriod.totalVolume,
                improvementPercentage = comparison.improvementPercentage
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Period details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PeriodSummaryCard(
                    title = "Before",
                    period = comparison.beforePeriod,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                PeriodSummaryCard(
                    title = "After",
                    period = comparison.afterPeriod,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Statistical significance badge
 */
@Composable
private fun StatisticalSignificanceBadge(
    significance: StatisticalSignificance,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (significance) {
        StatisticalSignificance.HIGHLY_SIGNIFICANT -> MaterialTheme.colorScheme.primary to "Highly Significant"
        StatisticalSignificance.SIGNIFICANT -> MaterialTheme.colorScheme.secondary to "Significant"
        StatisticalSignificance.MARGINALLY_SIGNIFICANT -> MaterialTheme.colorScheme.tertiary to "Marginally Significant"
        StatisticalSignificance.NOT_SIGNIFICANT -> MaterialTheme.colorScheme.outline to "Not Significant"
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Before/After comparison bars
 */
@Composable
private fun BeforeAfterBars(
    beforeValue: Double,
    afterValue: Double,
    improvementPercentage: Double,
    modifier: Modifier = Modifier
) {
    val maxValue = maxOf(beforeValue, afterValue)
    val beforePercentage = if (maxValue > 0) (beforeValue / maxValue).toFloat() else 0f
    val afterPercentage = if (maxValue > 0) (afterValue / maxValue).toFloat() else 0f
    
    Column(modifier = modifier) {
        // Before bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Before",
                modifier = Modifier.width(60.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(4.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(beforePercentage)
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            RoundedCornerShape(4.dp)
                        )
                )
            }
            
            Text(
                text = "${beforeValue.toInt()}kg",
                modifier = Modifier.width(80.dp),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // After bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "After",
                modifier = Modifier.width(60.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(4.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(afterPercentage)
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.secondary,
                            RoundedCornerShape(4.dp)
                        )
                )
            }
            
            Text(
                text = "${afterValue.toInt()}kg",
                modifier = Modifier.width(80.dp),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Improvement indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isImprovement = improvementPercentage > 0
            val icon = if (isImprovement) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown
            val color = if (isImprovement) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "${if (isImprovement) "+" else ""}${improvementPercentage.toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

/**
 * Period summary card
 */
@Composable
private fun PeriodSummaryCard(
    title: String,
    period: ComparisonPeriod,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${period.totalVolume.toInt()}kg",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Total Volume",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${period.workoutCount} workouts",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Muscle Group Distribution Pie Chart
 */
@Composable
fun MuscleGroupDistributionChart(
    distribution: List<MuscleGroupDistribution>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Muscle Group Distribution",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Training volume by muscle group",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Pie chart
                PieChart(
                    data = distribution,
                    modifier = Modifier.size(200.dp)
                )
                
                Spacer(modifier = Modifier.width(20.dp))
                
                // Legend
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(distribution) { item ->
                        MuscleGroupLegendItem(item)
                    }
                }
            }
        }
    }
}

/**
 * Pie chart component
 */
@Composable
private fun PieChart(
    data: List<MuscleGroupDistribution>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = minOf(size.width, size.height) / 2 * 0.8f
        
        var startAngle = -90f
        
        data.forEach { item ->
            val sweepAngle = (item.percentage.toFloat() / 100f) * 360f
            
            drawArc(
                color = Color(android.graphics.Color.parseColor(item.color)),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2f, radius * 2f)
            )
            
            startAngle += sweepAngle
        }
    }
}

/**
 * Legend item for muscle group
 */
@Composable
private fun MuscleGroupLegendItem(
    item: MuscleGroupDistribution,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(
                    Color(android.graphics.Color.parseColor(item.color)),
                    CircleShape
                )
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.muscleGroup.displayName(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "${item.percentage.toInt()}% • ${item.totalVolume.toInt()}kg",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Personal Records Timeline Chart
 */
@Composable
fun PersonalRecordsTimelineChart(
    timeline: PersonalRecordsTimeline,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = timeline.exerciseName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Personal Records Timeline",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                TrendIndicator(
                    trendDirection = timeline.trendDirection,
                    improvement = timeline.totalImprovement
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Timeline chart
            val chartData = remember(timeline.records) {
                ChartEntryModelProducer(
                    timeline.records.mapIndexed { index, record ->
                        entryOf(index.toFloat(), record.weight.toFloat())
                    }
                )
            }
            
            Chart(
                chart = lineChart(
                    lines = listOf(
                        LineChart.LineSpec(
                            lineColor = MaterialTheme.colorScheme.primary.toArgb()
                        )
                    )
                ),
                chartModelProducer = chartData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Recent records
            Text(
                text = "Recent Records",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                modifier = Modifier.heightIn(max = 200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(timeline.records.takeLast(5).reversed()) { record ->
                    PersonalRecordTimelineItem(record)
                }
            }
        }
    }
}

/**
 * Trend indicator for timeline
 */
@Composable
private fun TrendIndicator(
    trendDirection: TrendDirection,
    improvement: Double,
    modifier: Modifier = Modifier
) {
    val (icon, color, text) = when (trendDirection) {
        TrendDirection.UP -> Triple(
            Icons.Default.KeyboardArrowUp,
            MaterialTheme.colorScheme.primary,
            "+${improvement.toInt()}%"
        )
        TrendDirection.DOWN -> Triple(
            Icons.Default.KeyboardArrowDown,
            MaterialTheme.colorScheme.error,
            "${improvement.toInt()}%"
        )
        TrendDirection.STABLE -> Triple(
            Icons.Default.KeyboardArrowRight,
            MaterialTheme.colorScheme.outline,
            "Stable"
        )
    }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Individual timeline item
 */
@Composable
private fun PersonalRecordTimelineItem(
    record: PersonalRecordPoint,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (record.isNewRecord) 
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else 
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = record.date.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "${record.weight}kg × ${record.reps}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (record.isNewRecord) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "PR",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}