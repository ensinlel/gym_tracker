package com.example.gym_tracker.feature.workout.util

import com.example.gym_tracker.feature.workout.WorkoutExercisesViewModel
import kotlinx.coroutines.test.runTest
import java.text.SimpleDateFormat
import java.util.*

/**
 * Generates comprehensive UI compatibility reports for database persistence
 */
class UICompatibilityReportGenerator {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    /**
     * Generate a comprehensive UI compatibility report
     */
    suspend fun generateCompatibilityReport(
        viewModel: WorkoutExercisesViewModel,
        cache: com.example.gym_tracker.core.data.cache.ExerciseInstanceCache,
        testScenarios: List<UITestScenario>
    ): UICompatibilityReport {
        val validator = UIBehaviorValidator()
        val results = mutableListOf<UITestResult>()
        
        testScenarios.forEach { scenario ->
            val startTime = System.currentTimeMillis()
            
            try {
                val result = when (scenario.type) {
                    UITestType.STATE_TRANSITIONS -> {
                        validator.validateUIStateTransitions(viewModel, scenario.workoutId)
                    }
                    UITestType.EXERCISE_ADDITION -> {
                        validator.validateExerciseAddition(viewModel, scenario.workoutId, scenario.exerciseId!!)
                    }
                    UITestType.SET_MANAGEMENT -> {
                        validator.validateSetManagement(viewModel, scenario.workoutId, scenario.exerciseId!!)
                    }
                    UITestType.PERSISTENCE_RESTART -> {
                        validator.validatePersistenceAcrossRestart(viewModel, scenario.workoutId, scenario.exerciseId!!, cache)
                    }
                    UITestType.PERFORMANCE -> {
                        validator.validateUIPerformance(viewModel, scenario.workoutId, scenario.exerciseIds ?: emptyList())
                    }
                }
                
                val executionTime = System.currentTimeMillis() - startTime
                
                results.add(
                    UITestResult(
                        scenario = scenario,
                        success = result.success,
                        details = result.details,
                        executionTimeMs = executionTime,
                        timestamp = dateFormat.format(Date())
                    )
                )
                
            } catch (e: Exception) {
                val executionTime = System.currentTimeMillis() - startTime
                
                results.add(
                    UITestResult(
                        scenario = scenario,
                        success = false,
                        details = listOf("✗ Test execution failed: ${e.message}"),
                        executionTimeMs = executionTime,
                        timestamp = dateFormat.format(Date()),
                        exception = e
                    )
                )
            }
        }
        
        return UICompatibilityReport(
            timestamp = dateFormat.format(Date()),
            totalTests = results.size,
            passedTests = results.count { it.success },
            failedTests = results.count { !it.success },
            totalExecutionTimeMs = results.sumOf { it.executionTimeMs },
            results = results
        )
    }
    
    /**
     * Generate a formatted report string
     */
    fun formatReport(report: UICompatibilityReport): String {
        val sb = StringBuilder()
        
        sb.appendLine("=" .repeat(80))
        sb.appendLine("UI COMPATIBILITY REPORT")
        sb.appendLine("=" .repeat(80))
        sb.appendLine("Generated: ${report.timestamp}")
        sb.appendLine("Total Tests: ${report.totalTests}")
        sb.appendLine("Passed: ${report.passedTests}")
        sb.appendLine("Failed: ${report.failedTests}")
        sb.appendLine("Success Rate: ${String.format("%.1f", (report.passedTests.toDouble() / report.totalTests) * 100)}%")
        sb.appendLine("Total Execution Time: ${report.totalExecutionTimeMs}ms")
        sb.appendLine()
        
        // Summary by test type
        val resultsByType = report.results.groupBy { it.scenario.type }
        sb.appendLine("RESULTS BY TEST TYPE:")
        sb.appendLine("-" .repeat(40))
        
        UITestType.values().forEach { type ->
            val typeResults = resultsByType[type] ?: emptyList()
            if (typeResults.isNotEmpty()) {
                val passed = typeResults.count { it.success }
                val total = typeResults.size
                val avgTime = typeResults.map { it.executionTimeMs }.average()
                
                sb.appendLine("${type.displayName}:")
                sb.appendLine("  Tests: $passed/$total passed")
                sb.appendLine("  Avg Time: ${String.format("%.0f", avgTime)}ms")
                sb.appendLine()
            }
        }
        
        // Detailed results
        sb.appendLine("DETAILED RESULTS:")
        sb.appendLine("-" .repeat(40))
        
        report.results.forEach { result ->
            val status = if (result.success) "✓ PASS" else "✗ FAIL"
            sb.appendLine("$status [${result.executionTimeMs}ms] ${result.scenario.name}")
            
            if (result.scenario.description.isNotEmpty()) {
                sb.appendLine("  Description: ${result.scenario.description}")
            }
            
            result.details.forEach { detail ->
                sb.appendLine("    $detail")
            }
            
            if (result.exception != null) {
                sb.appendLine("    Exception: ${result.exception.message}")
            }
            
            sb.appendLine()
        }
        
        // Performance analysis
        sb.appendLine("PERFORMANCE ANALYSIS:")
        sb.appendLine("-" .repeat(40))
        
        val performanceResults = report.results.filter { it.scenario.type == UITestType.PERFORMANCE }
        if (performanceResults.isNotEmpty()) {
            performanceResults.forEach { result ->
                sb.appendLine("${result.scenario.name}: ${result.executionTimeMs}ms")
                result.details.filter { it.contains("time:") || it.contains("performance") }.forEach { detail ->
                    sb.appendLine("  $detail")
                }
            }
        } else {
            sb.appendLine("No performance tests executed")
        }
        
        sb.appendLine()
        
        // Recommendations
        sb.appendLine("RECOMMENDATIONS:")
        sb.appendLine("-" .repeat(40))
        
        val failedResults = report.results.filter { !it.success }
        if (failedResults.isEmpty()) {
            sb.appendLine("✓ All tests passed! UI compatibility is excellent.")
        } else {
            sb.appendLine("Issues found that need attention:")
            failedResults.forEach { result ->
                sb.appendLine("• ${result.scenario.name}: ${result.details.firstOrNull { it.startsWith("✗") } ?: "Test failed"}")
            }
        }
        
        val slowTests = report.results.filter { it.executionTimeMs > 2000 }
        if (slowTests.isNotEmpty()) {
            sb.appendLine()
            sb.appendLine("Performance concerns (>2s execution time):")
            slowTests.forEach { result ->
                sb.appendLine("• ${result.scenario.name}: ${result.executionTimeMs}ms")
            }
        }
        
        sb.appendLine()
        sb.appendLine("=" .repeat(80))
        
        return sb.toString()
    }
    
    data class UITestScenario(
        val name: String,
        val type: UITestType,
        val workoutId: String,
        val exerciseId: String? = null,
        val exerciseIds: List<String>? = null,
        val description: String = ""
    )
    
    data class UITestResult(
        val scenario: UITestScenario,
        val success: Boolean,
        val details: List<String>,
        val executionTimeMs: Long,
        val timestamp: String,
        val exception: Exception? = null
    )
    
    data class UICompatibilityReport(
        val timestamp: String,
        val totalTests: Int,
        val passedTests: Int,
        val failedTests: Int,
        val totalExecutionTimeMs: Long,
        val results: List<UITestResult>
    )
    
    enum class UITestType(val displayName: String) {
        STATE_TRANSITIONS("UI State Transitions"),
        EXERCISE_ADDITION("Exercise Addition"),
        SET_MANAGEMENT("Set Management"),
        PERSISTENCE_RESTART("Persistence Across Restart"),
        PERFORMANCE("Performance Testing")
    }
}