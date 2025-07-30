package com.example.gym_tracker.core.data.validation

import com.example.gym_tracker.core.data.model.ExerciseInstance
import com.example.gym_tracker.core.data.model.ExerciseSet
import com.example.gym_tracker.core.data.repository.ExerciseInstanceRepository
import com.example.gym_tracker.core.data.repository.ExerciseRepository
import com.example.gym_tracker.core.data.repository.ExerciseSetRepository
import com.example.gym_tracker.core.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.first
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Comprehensive data integrity validator for workout exercise persistence
 */
@Singleton
class DataIntegrityValidator @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val exerciseInstanceRepository: ExerciseInstanceRepository,
    private val exerciseSetRepository: ExerciseSetRepository
) {
    
    /**
     * Validate exercise instance creation data
     */
    suspend fun validateExerciseInstanceCreation(exerciseInstance: ExerciseInstance): ValidationResult {
        val errors = mutableListOf<String>()
        
        // Validate required fields
        if (exerciseInstance.id.isBlank()) {
            errors.add("Exercise instance ID cannot be blank")
        }
        
        if (exerciseInstance.workoutId.isBlank()) {
            errors.add("Workout ID cannot be blank")
        }
        
        if (exerciseInstance.exerciseId.isBlank()) {
            errors.add("Exercise ID cannot be blank")
        }
        
        if (exerciseInstance.orderInWorkout < 1) {
            errors.add("Order in workout must be positive (got: ${exerciseInstance.orderInWorkout})")
        }
        
        // Validate foreign key relationships
        try {
            val workout = workoutRepository.getWorkoutById(exerciseInstance.workoutId)
            if (workout == null) {
                errors.add("Referenced workout does not exist: ${exerciseInstance.workoutId}")
            }
        } catch (e: Exception) {
            errors.add("Failed to validate workout reference: ${e.message}")
        }
        
        try {
            val exercise = exerciseRepository.getExerciseById(exerciseInstance.exerciseId)
            if (exercise == null) {
                errors.add("Referenced exercise does not exist: ${exerciseInstance.exerciseId}")
            }
        } catch (e: Exception) {
            errors.add("Failed to validate exercise reference: ${e.message}")
        }
        
        // Validate uniqueness constraints
        try {
            val existingInstance = exerciseInstanceRepository.getExerciseInstanceById(exerciseInstance.id)
            if (existingInstance != null) {
                errors.add("Exercise instance with ID already exists: ${exerciseInstance.id}")
            }
        } catch (e: Exception) {
            errors.add("Failed to validate exercise instance uniqueness: ${e.message}")
        }
        
        // Validate order uniqueness within workout
        try {
            val workoutInstances = exerciseInstanceRepository.getExerciseInstancesByWorkoutId(exerciseInstance.workoutId).first()
            val duplicateOrder = workoutInstances.find { 
                it.orderInWorkout == exerciseInstance.orderInWorkout && it.id != exerciseInstance.id 
            }
            if (duplicateOrder != null) {
                errors.add("Order ${exerciseInstance.orderInWorkout} already exists in workout ${exerciseInstance.workoutId}")
            }
        } catch (e: Exception) {
            errors.add("Failed to validate order uniqueness: ${e.message}")
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            validatedObject = exerciseInstance
        )
    }
    
    /**
     * Validate exercise set creation data
     */
    suspend fun validateExerciseSetCreation(exerciseSet: ExerciseSet): ValidationResult {
        val errors = mutableListOf<String>()
        
        // Validate required fields
        if (exerciseSet.id.isBlank()) {
            errors.add("Exercise set ID cannot be blank")
        }
        
        if (exerciseSet.exerciseInstanceId.isBlank()) {
            errors.add("Exercise instance ID cannot be blank")
        }
        
        if (exerciseSet.setNumber < 1) {
            errors.add("Set number must be positive (got: ${exerciseSet.setNumber})")
        }
        
        if (exerciseSet.weight < 0) {
            errors.add("Weight cannot be negative (got: ${exerciseSet.weight})")
        }
        
        if (exerciseSet.reps < 0) {
            errors.add("Reps cannot be negative (got: ${exerciseSet.reps})")
        }
        
        if (exerciseSet.restTime < Duration.ZERO) {
            errors.add("Rest time cannot be negative (got: ${exerciseSet.restTime})")
        }
        
        // Validate foreign key relationship
        try {
            val exerciseInstance = exerciseInstanceRepository.getExerciseInstanceById(exerciseSet.exerciseInstanceId)
            if (exerciseInstance == null) {
                errors.add("Referenced exercise instance does not exist: ${exerciseSet.exerciseInstanceId}")
            }
        } catch (e: Exception) {
            errors.add("Failed to validate exercise instance reference: ${e.message}")
        }
        
        // Validate uniqueness constraints
        try {
            val existingSet = exerciseSetRepository.getSetById(exerciseSet.id)
            if (existingSet != null) {
                errors.add("Exercise set with ID already exists: ${exerciseSet.id}")
            }
        } catch (e: Exception) {
            errors.add("Failed to validate exercise set uniqueness: ${e.message}")
        }
        
        // Validate set number uniqueness within exercise instance
        try {
            val instanceSets = exerciseSetRepository.getSetsByExerciseInstance(exerciseSet.exerciseInstanceId).first()
            val duplicateSetNumber = instanceSets.find { 
                it.setNumber == exerciseSet.setNumber && it.id != exerciseSet.id 
            }
            if (duplicateSetNumber != null) {
                errors.add("Set number ${exerciseSet.setNumber} already exists for exercise instance ${exerciseSet.exerciseInstanceId}")
            }
        } catch (e: Exception) {
            errors.add("Failed to validate set number uniqueness: ${e.message}")
        }
        
        // Validate logical constraints
        if (exerciseSet.weight > 0 && exerciseSet.reps == 0) {
            errors.add("Warning: Set has weight but no reps")
        }
        
        if (exerciseSet.weight == 0.0 && exerciseSet.reps > 0) {
            errors.add("Warning: Set has reps but no weight (bodyweight exercise?)")
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            validatedObject = exerciseSet
        )
    }
    
    /**
     * Check for orphaned workout sets (sets without valid exercise instances)
     */
    suspend fun checkForOrphanedSets(): OrphanedDataResult {
        val orphanedSets = mutableListOf<ExerciseSet>()
        val errors = mutableListOf<String>()
        
        try {
            // This would require a method to get all sets - implementing a basic check
            // In a real implementation, you'd have a getAllSets() method
            val workouts = workoutRepository.getAllWorkouts().first()
            
            for (workout in workouts) {
                val exerciseInstances = exerciseInstanceRepository.getExerciseInstancesByWorkoutId(workout.id).first()
                
                for (instance in exerciseInstances) {
                    try {
                        val sets = exerciseSetRepository.getSetsByExerciseInstance(instance.id).first()
                        
                        for (set in sets) {
                            // Verify the exercise instance still exists
                            val verifyInstance = exerciseInstanceRepository.getExerciseInstanceById(set.exerciseInstanceId)
                            if (verifyInstance == null) {
                                orphanedSets.add(set)
                            }
                        }
                    } catch (e: Exception) {
                        errors.add("Failed to check sets for instance ${instance.id}: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            errors.add("Failed to check for orphaned sets: ${e.message}")
        }
        
        return OrphanedDataResult(
            orphanedSets = orphanedSets,
            orphanedInstances = emptyList(), // Would implement similar check for instances
            errors = errors
        )
    }
    
    /**
     * Validate foreign key relationships across the database
     */
    suspend fun validateForeignKeyRelationships(): ForeignKeyValidationResult {
        val violations = mutableListOf<ForeignKeyViolation>()
        
        try {
            val workouts = workoutRepository.getAllWorkouts().first()
            
            for (workout in workouts) {
                // Check exercise instances reference valid workouts and exercises
                val instances = exerciseInstanceRepository.getExerciseInstancesByWorkoutId(workout.id).first()
                
                for (instance in instances) {
                    // Validate workout reference
                    val referencedWorkout = workoutRepository.getWorkoutById(instance.workoutId)
                    if (referencedWorkout == null) {
                        violations.add(
                            ForeignKeyViolation(
                                table = "exercise_instances",
                                recordId = instance.id,
                                foreignKey = "workoutId",
                                referencedTable = "workouts",
                                referencedId = instance.workoutId,
                                violation = "Referenced workout does not exist"
                            )
                        )
                    }
                    
                    // Validate exercise reference
                    val referencedExercise = exerciseRepository.getExerciseById(instance.exerciseId)
                    if (referencedExercise == null) {
                        violations.add(
                            ForeignKeyViolation(
                                table = "exercise_instances",
                                recordId = instance.id,
                                foreignKey = "exerciseId",
                                referencedTable = "exercises",
                                referencedId = instance.exerciseId,
                                violation = "Referenced exercise does not exist"
                            )
                        )
                    }
                    
                    // Check sets reference valid exercise instances
                    val sets = exerciseSetRepository.getSetsByExerciseInstance(instance.id).first()
                    for (set in sets) {
                        val referencedInstance = exerciseInstanceRepository.getExerciseInstanceById(set.exerciseInstanceId)
                        if (referencedInstance == null) {
                            violations.add(
                                ForeignKeyViolation(
                                    table = "exercise_sets",
                                    recordId = set.id,
                                    foreignKey = "exerciseInstanceId",
                                    referencedTable = "exercise_instances",
                                    referencedId = set.exerciseInstanceId,
                                    violation = "Referenced exercise instance does not exist"
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            violations.add(
                ForeignKeyViolation(
                    table = "unknown",
                    recordId = "unknown",
                    foreignKey = "unknown",
                    referencedTable = "unknown",
                    referencedId = "unknown",
                    violation = "Validation failed: ${e.message}"
                )
            )
        }
        
        return ForeignKeyValidationResult(
            isValid = violations.isEmpty(),
            violations = violations
        )
    }
    
    /**
     * Perform comprehensive data consistency checks
     */
    suspend fun performDataConsistencyCheck(): DataConsistencyResult {
        val issues = mutableListOf<DataConsistencyIssue>()
        
        try {
            val workouts = workoutRepository.getAllWorkouts().first()
            
            for (workout in workouts) {
                val instances = exerciseInstanceRepository.getExerciseInstancesByWorkoutId(workout.id).first()
                
                // Check for gaps in order sequence
                val orders = instances.map { it.orderInWorkout }.sorted()
                for (i in 1 until orders.size) {
                    if (orders[i] != orders[i-1] + 1) {
                        issues.add(
                            DataConsistencyIssue(
                                type = ConsistencyIssueType.ORDER_GAP,
                                description = "Gap in exercise order sequence in workout ${workout.id}: ${orders[i-1]} -> ${orders[i]}",
                                severity = IssueSeverity.WARNING,
                                affectedRecords = listOf(workout.id)
                            )
                        )
                    }
                }
                
                // Check for duplicate orders
                val duplicateOrders = orders.groupBy { it }.filter { it.value.size > 1 }
                if (duplicateOrders.isNotEmpty()) {
                    issues.add(
                        DataConsistencyIssue(
                            type = ConsistencyIssueType.DUPLICATE_ORDER,
                            description = "Duplicate exercise orders in workout ${workout.id}: ${duplicateOrders.keys}",
                            severity = IssueSeverity.ERROR,
                            affectedRecords = listOf(workout.id)
                        )
                    )
                }
                
                // Check set numbering consistency
                for (instance in instances) {
                    val sets = exerciseSetRepository.getSetsByExerciseInstance(instance.id).first()
                    val setNumbers = sets.map { it.setNumber }.sorted()
                    
                    // Check for gaps in set numbers
                    for (i in 1 until setNumbers.size) {
                        if (setNumbers[i] != setNumbers[i-1] + 1) {
                            issues.add(
                                DataConsistencyIssue(
                                    type = ConsistencyIssueType.SET_NUMBER_GAP,
                                    description = "Gap in set number sequence for exercise instance ${instance.id}: ${setNumbers[i-1]} -> ${setNumbers[i]}",
                                    severity = IssueSeverity.WARNING,
                                    affectedRecords = listOf(instance.id)
                                )
                            )
                        }
                    }
                    
                    // Check for duplicate set numbers
                    val duplicateSetNumbers = setNumbers.groupBy { it }.filter { it.value.size > 1 }
                    if (duplicateSetNumbers.isNotEmpty()) {
                        issues.add(
                            DataConsistencyIssue(
                                type = ConsistencyIssueType.DUPLICATE_SET_NUMBER,
                                description = "Duplicate set numbers for exercise instance ${instance.id}: ${duplicateSetNumbers.keys}",
                                severity = IssueSeverity.ERROR,
                                affectedRecords = listOf(instance.id)
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            issues.add(
                DataConsistencyIssue(
                    type = ConsistencyIssueType.VALIDATION_ERROR,
                    description = "Data consistency check failed: ${e.message}",
                    severity = IssueSeverity.ERROR,
                    affectedRecords = emptyList()
                )
            )
        }
        
        return DataConsistencyResult(
            isConsistent = issues.none { it.severity == IssueSeverity.ERROR },
            issues = issues,
            totalIssues = issues.size,
            errorCount = issues.count { it.severity == IssueSeverity.ERROR },
            warningCount = issues.count { it.severity == IssueSeverity.WARNING }
        )
    }
    
    /**
     * Repair data consistency issues where possible
     */
    suspend fun repairDataConsistencyIssues(issues: List<DataConsistencyIssue>): DataRepairResult {
        val repairedIssues = mutableListOf<DataConsistencyIssue>()
        val failedRepairs = mutableListOf<DataRepairFailure>()
        
        for (issue in issues) {
            try {
                when (issue.type) {
                    ConsistencyIssueType.ORDER_GAP -> {
                        // Attempt to reorder exercises to eliminate gaps
                        if (issue.affectedRecords.isNotEmpty()) {
                            val workoutId = issue.affectedRecords.first()
                            val success = repairOrderGaps(workoutId)
                            if (success) {
                                repairedIssues.add(issue)
                            } else {
                                failedRepairs.add(DataRepairFailure(issue, "Failed to repair order gaps"))
                            }
                        }
                    }
                    ConsistencyIssueType.SET_NUMBER_GAP -> {
                        // Attempt to renumber sets to eliminate gaps
                        if (issue.affectedRecords.isNotEmpty()) {
                            val instanceId = issue.affectedRecords.first()
                            val success = repairSetNumberGaps(instanceId)
                            if (success) {
                                repairedIssues.add(issue)
                            } else {
                                failedRepairs.add(DataRepairFailure(issue, "Failed to repair set number gaps"))
                            }
                        }
                    }
                    else -> {
                        failedRepairs.add(DataRepairFailure(issue, "Repair not implemented for issue type: ${issue.type}"))
                    }
                }
            } catch (e: Exception) {
                failedRepairs.add(DataRepairFailure(issue, "Repair failed with exception: ${e.message}"))
            }
        }
        
        return DataRepairResult(
            totalIssues = issues.size,
            repairedCount = repairedIssues.size,
            failedCount = failedRepairs.size,
            repairedIssues = repairedIssues,
            failedRepairs = failedRepairs
        )
    }
    
    private suspend fun repairOrderGaps(workoutId: String): Boolean {
        return try {
            val instances = exerciseInstanceRepository.getExerciseInstancesByWorkoutId(workoutId).first()
            val sortedInstances = instances.sortedBy { it.orderInWorkout }
            
            sortedInstances.forEachIndexed { index, instance ->
                val newOrder = index + 1
                if (instance.orderInWorkout != newOrder) {
                    val updatedInstance = instance.copy(orderInWorkout = newOrder)
                    exerciseInstanceRepository.updateExerciseInstance(updatedInstance)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    private suspend fun repairSetNumberGaps(instanceId: String): Boolean {
        return try {
            val sets = exerciseSetRepository.getSetsByExerciseInstance(instanceId).first()
            val sortedSets = sets.sortedBy { it.setNumber }
            
            sortedSets.forEachIndexed { index, set ->
                val newSetNumber = index + 1
                if (set.setNumber != newSetNumber) {
                    val updatedSet = set.copy(setNumber = newSetNumber)
                    exerciseSetRepository.updateSet(updatedSet)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // Data classes for validation results
    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String>,
        val validatedObject: Any
    )
    
    data class OrphanedDataResult(
        val orphanedSets: List<ExerciseSet>,
        val orphanedInstances: List<ExerciseInstance>,
        val errors: List<String>
    )
    
    data class ForeignKeyValidationResult(
        val isValid: Boolean,
        val violations: List<ForeignKeyViolation>
    )
    
    data class ForeignKeyViolation(
        val table: String,
        val recordId: String,
        val foreignKey: String,
        val referencedTable: String,
        val referencedId: String,
        val violation: String
    )
    
    data class DataConsistencyResult(
        val isConsistent: Boolean,
        val issues: List<DataConsistencyIssue>,
        val totalIssues: Int,
        val errorCount: Int,
        val warningCount: Int
    )
    
    data class DataConsistencyIssue(
        val type: ConsistencyIssueType,
        val description: String,
        val severity: IssueSeverity,
        val affectedRecords: List<String>
    )
    
    data class DataRepairResult(
        val totalIssues: Int,
        val repairedCount: Int,
        val failedCount: Int,
        val repairedIssues: List<DataConsistencyIssue>,
        val failedRepairs: List<DataRepairFailure>
    )
    
    data class DataRepairFailure(
        val issue: DataConsistencyIssue,
        val reason: String
    )
    
    enum class ConsistencyIssueType {
        ORDER_GAP,
        DUPLICATE_ORDER,
        SET_NUMBER_GAP,
        DUPLICATE_SET_NUMBER,
        VALIDATION_ERROR
    }
    
    enum class IssueSeverity {
        WARNING,
        ERROR
    }
}