package com.example.gym_tracker.core.data.validation

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gym_tracker.core.data.model.Exercise
import com.example.gym_tracker.core.data.model.ExerciseInstance
import com.example.gym_tracker.core.data.model.ExerciseSet
import com.example.gym_tracker.core.data.model.Workout
import com.example.gym_tracker.core.data.repository.impl.ExerciseRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.ExerciseSetRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.ValidatedExerciseInstanceRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.ValidatedExerciseSetRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.WorkoutRepositoryImpl
import com.example.gym_tracker.core.data.cache.ExerciseInstanceCache
import com.example.gym_tracker.core.data.util.PerformanceMonitor
import com.example.gym_tracker.core.database.GymTrackerDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Comprehensive tests for data integrity validation
 */
@RunWith(AndroidJUnit4::class)
class DataIntegrityValidationTest {

    private lateinit var database: GymTrackerDatabase
    private lateinit var dataIntegrityValidator: DataIntegrityValidator
    private lateinit var validatedExerciseInstanceRepository: ValidatedExerciseInstanceRepositoryImpl
    private lateinit var validatedExerciseSetRepository: ValidatedExerciseSetRepositoryImpl
    private lateinit var workoutRepository: WorkoutRepositoryImpl
    private lateinit var exerciseRepository: ExerciseRepositoryImpl
    private lateinit var cache: ExerciseInstanceCache
    private lateinit var performanceMonitor: PerformanceMonitor

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GymTrackerDatabase::class.java
        ).build()
        
        cache = ExerciseInstanceCache()
        performanceMonitor = PerformanceMonitor()
        
        workoutRepository = WorkoutRepositoryImpl(database.workoutDao())
        exerciseRepository = ExerciseRepositoryImpl(database.exerciseDao())
        
        dataIntegrityValidator = DataIntegrityValidator(
            workoutRepository,
            exerciseRepository,
            ExerciseInstanceRepositoryImpl(database.exerciseInstanceDao(), cache, performanceMonitor),
            ExerciseSetRepositoryImpl(database.exerciseSetDao())
        )
        
        validatedExerciseInstanceRepository = ValidatedExerciseInstanceRepositoryImpl(
            database.exerciseInstanceDao(),
            cache,
            performanceMonitor,
            dataIntegrityValidator
        )
        
        validatedExerciseSetRepository = ValidatedExerciseSetRepositoryImpl(
            database.exerciseSetDao(),
            dataIntegrityValidator
        )
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    fun testExerciseInstanceValidation_ValidData() = runTest {
        // Setup test data
        setupTestWorkoutAndExercise()
        
        val validExerciseInstance = ExerciseInstance(
            id = "valid_instance",
            workoutId = "test_workout",
            exerciseId = "test_exercise",
            orderInWorkout = 1,
            notes = "Valid instance"
        )
        
        // Validate
        val result = dataIntegrityValidator.validateExerciseInstanceCreation(validExerciseInstance)
        
        assertTrue("Valid exercise instance should pass validation", result.isValid)
        assertTrue("Should have no errors", result.errors.isEmpty())
    }

    @Test
    fun testExerciseInstanceValidation_InvalidData() = runTest {
        val invalidExerciseInstance = ExerciseInstance(
            id = "", // Invalid: blank ID
            workoutId = "nonexistent_workout", // Invalid: nonexistent workout
            exerciseId = "", // Invalid: blank exercise ID
            orderInWorkout = -1, // Invalid: negative order
            notes = "Invalid instance"
        )
        
        // Validate
        val result = dataIntegrityValidator.validateExerciseInstanceCreation(invalidExerciseInstance)
        
        assertFalse("Invalid exercise instance should fail validation", result.isValid)
        assertTrue("Should have multiple errors", result.errors.size >= 4)
        
        // Check specific error messages
        assertTrue("Should have blank ID error", result.errors.any { it.contains("ID cannot be blank") })
        assertTrue("Should have negative order error", result.errors.any { it.contains("must be positive") })
    }

    @Test
    fun testExerciseSetValidation_ValidData() = runTest {
        // Setup test data
        setupTestWorkoutAndExercise()
        val exerciseInstance = createValidExerciseInstance()
        validatedExerciseInstanceRepository.insertExerciseInstance(exerciseInstance)
        
        val validExerciseSet = ExerciseSet(
            id = "valid_set",
            exerciseInstanceId = exerciseInstance.id,
            setNumber = 1,
            weight = 100.0,
            reps = 10,
            restTime = 60,
            notes = "Valid set"
        )
        
        // Validate
        val result = dataIntegrityValidator.validateExerciseSetCreation(validExerciseSet)
        
        assertTrue("Valid exercise set should pass validation", result.isValid)
        assertTrue("Should have no errors", result.errors.isEmpty())
    }

    @Test
    fun testExerciseSetValidation_InvalidData() = runTest {
        val invalidExerciseSet = ExerciseSet(
            id = "", // Invalid: blank ID
            exerciseInstanceId = "nonexistent_instance", // Invalid: nonexistent instance
            setNumber = -1, // Invalid: negative set number
            weight = -50.0, // Invalid: negative weight
            reps = -5, // Invalid: negative reps
            restTime = -30, // Invalid: negative rest time
            notes = "Invalid set"
        )
        
        // Validate
        val result = dataIntegrityValidator.validateExerciseSetCreation(invalidExerciseSet)
        
        assertFalse("Invalid exercise set should fail validation", result.isValid)
        assertTrue("Should have multiple errors", result.errors.size >= 6)
        
        // Check specific error messages
        assertTrue("Should have blank ID error", result.errors.any { it.contains("ID cannot be blank") })
        assertTrue("Should have negative weight error", result.errors.any { it.contains("Weight cannot be negative") })
        assertTrue("Should have negative reps error", result.errors.any { it.contains("Reps cannot be negative") })
    }

    @Test
    fun testValidatedRepositoryInsertion_ValidData() = runTest {
        // Setup test data
        setupTestWorkoutAndExercise()
        
        val validExerciseInstance = createValidExerciseInstance()
        
        // Should insert successfully
        val insertedId = validatedExerciseInstanceRepository.insertExerciseInstance(validExerciseInstance)
        assertEquals("Should return correct ID", validExerciseInstance.id, insertedId)
        
        // Verify insertion
        val retrieved = validatedExerciseInstanceRepository.getExerciseInstanceById(validExerciseInstance.id)
        assertNotNull("Should retrieve inserted instance", retrieved)
        assertEquals("Should have correct workout ID", validExerciseInstance.workoutId, retrieved?.workoutId)
    }

    @Test
    fun testValidatedRepositoryInsertion_InvalidData() = runTest {
        val invalidExerciseInstance = ExerciseInstance(
            id = "",
            workoutId = "nonexistent_workout",
            exerciseId = "nonexistent_exercise",
            orderInWorkout = -1,
            notes = "Invalid"
        )
        
        // Should throw exception
        try {
            validatedExerciseInstanceRepository.insertExerciseInstance(invalidExerciseInstance)
            fail("Should have thrown exception for invalid data")
        } catch (e: IllegalArgumentException) {
            assertTrue("Should contain validation error message", e.message?.contains("validation failed") == true)
        }
    }

    @Test
    fun testForeignKeyValidation() = runTest {
        // Setup test data with some invalid references
        setupTestWorkoutAndExercise()
        
        // Create valid instance
        val validInstance = createValidExerciseInstance()
        validatedExerciseInstanceRepository.insertExerciseInstance(validInstance)
        
        // Create instance with invalid workout reference (directly in database to bypass validation)
        val invalidInstance = ExerciseInstance(
            id = "invalid_instance",
            workoutId = "nonexistent_workout",
            exerciseId = "test_exercise",
            orderInWorkout = 2,
            notes = "Invalid workout reference"
        )
        
        // Insert directly to database to bypass validation
        database.exerciseInstanceDao().insertExerciseInstance(invalidInstance.toEntity())
        
        // Validate foreign keys
        val result = dataIntegrityValidator.validateForeignKeyRelationships()
        
        assertFalse("Should detect foreign key violations", result.isValid)
        assertTrue("Should have violations", result.violations.isNotEmpty())
        
        val workoutViolation = result.violations.find { it.foreignKey == "workoutId" }
        assertNotNull("Should detect workout reference violation", workoutViolation)
    }

    @Test
    fun testDataConsistencyCheck() = runTest {
        // Setup test data with consistency issues
        setupTestWorkoutAndExercise()
        
        // Create instances with order gaps
        val instance1 = ExerciseInstance("inst1", "test_workout", "test_exercise", 1, "")
        val instance3 = ExerciseInstance("inst3", "test_workout", "test_exercise", 3, "") // Gap: missing order 2
        val instance5 = ExerciseInstance("inst5", "test_workout", "test_exercise", 5, "") // Gap: missing order 4
        
        validatedExerciseInstanceRepository.insertExerciseInstance(instance1)
        validatedExerciseInstanceRepository.insertExerciseInstance(instance3)
        validatedExerciseInstanceRepository.insertExerciseInstance(instance5)
        
        // Add sets with number gaps
        val set1 = ExerciseSet("set1", instance1.id, 1, 100.0, 10, 0, "")
        val set3 = ExerciseSet("set3", instance1.id, 3, 105.0, 8, 0, "") // Gap: missing set 2
        
        validatedExerciseSetRepository.insertSet(set1)
        validatedExerciseSetRepository.insertSet(set3)
        
        // Check consistency
        val result = dataIntegrityValidator.performDataConsistencyCheck()
        
        assertFalse("Should detect consistency issues", result.isConsistent)
        assertTrue("Should have issues", result.issues.isNotEmpty())
        
        // Should detect order gaps
        val orderGapIssue = result.issues.find { it.type == DataIntegrityValidator.ConsistencyIssueType.ORDER_GAP }
        assertNotNull("Should detect exercise order gaps", orderGapIssue)
        
        // Should detect set number gaps
        val setGapIssue = result.issues.find { it.type == DataIntegrityValidator.ConsistencyIssueType.SET_NUMBER_GAP }
        assertNotNull("Should detect set number gaps", setGapIssue)
    }

    @Test
    fun testDataConsistencyRepair() = runTest {
        // Setup test data with repairable issues
        setupTestWorkoutAndExercise()
        
        // Create instances with order gaps
        val instance1 = ExerciseInstance("inst1", "test_workout", "test_exercise", 1, "")
        val instance3 = ExerciseInstance("inst3", "test_workout", "test_exercise", 3, "")
        val instance5 = ExerciseInstance("inst5", "test_workout", "test_exercise", 5, "")
        
        validatedExerciseInstanceRepository.insertExerciseInstance(instance1)
        validatedExerciseInstanceRepository.insertExerciseInstance(instance3)
        validatedExerciseInstanceRepository.insertExerciseInstance(instance5)
        
        // Check and repair
        val consistencyResult = dataIntegrityValidator.performDataConsistencyCheck()
        val repairResult = dataIntegrityValidator.repairDataConsistencyIssues(consistencyResult.issues)
        
        assertTrue("Should repair some issues", repairResult.repairedCount > 0)
        
        // Verify repair worked
        val instances = validatedExerciseInstanceRepository.getExerciseInstancesByWorkoutId("test_workout").first()
        val orders = instances.map { it.orderInWorkout }.sorted()
        
        assertEquals("Should have continuous order sequence", listOf(1, 2, 3), orders)
    }

    @Test
    fun testSetNumberingRepair() = runTest {
        // Setup test data
        setupTestWorkoutAndExercise()
        val instance = createValidExerciseInstance()
        validatedExerciseInstanceRepository.insertExerciseInstance(instance)
        
        // Create sets with gaps (directly in database to bypass validation)
        val set1 = ExerciseSet("set1", instance.id, 1, 100.0, 10, 0, "")
        val set3 = ExerciseSet("set3", instance.id, 3, 105.0, 8, 0, "")
        val set5 = ExerciseSet("set5", instance.id, 5, 110.0, 6, 0, "")
        
        database.exerciseSetDao().insertSet(set1.toEntity())
        database.exerciseSetDao().insertSet(set3.toEntity())
        database.exerciseSetDao().insertSet(set5.toEntity())
        
        // Repair set numbering
        val repairSuccess = validatedExerciseSetRepository.validateAndRepairSetNumbering(instance.id)
        assertTrue("Should successfully repair set numbering", repairSuccess)
        
        // Verify repair
        val sets = validatedExerciseSetRepository.getSetsByExerciseInstance(instance.id).first()
        val setNumbers = sets.map { it.setNumber }.sorted()
        
        assertEquals("Should have continuous set numbers", listOf(1, 2, 3), setNumbers)
    }

    @Test
    fun testSetStatistics() = runTest {
        // Setup test data
        setupTestWorkoutAndExercise()
        val instance = createValidExerciseInstance()
        validatedExerciseInstanceRepository.insertExerciseInstance(instance)
        
        // Add sets
        val sets = listOf(
            ExerciseSet("set1", instance.id, 1, 100.0, 10, 0, ""),
            ExerciseSet("set2", instance.id, 2, 105.0, 8, 0, ""),
            ExerciseSet("set3", instance.id, 3, 110.0, 6, 0, "")
        )
        
        sets.forEach { validatedExerciseSetRepository.insertSet(it) }
        
        // Get statistics
        val stats = validatedExerciseSetRepository.getSetStatistics(instance.id)
        
        assertEquals("Should have correct set count", 3, stats.totalSets)
        assertEquals("Should have correct set numbers", listOf(1, 2, 3), stats.setNumbers)
        assertFalse("Should have no gaps", stats.hasGaps)
        assertFalse("Should have no duplicates", stats.hasDuplicates)
        assertEquals("Should have correct average weight", 105.0, stats.averageWeight, 0.01)
        assertEquals("Should have correct average reps", 8.0, stats.averageReps, 0.01)
        assertEquals("Should have correct total volume", 2540.0, stats.totalVolume, 0.01) // (100*10 + 105*8 + 110*6)
    }

    @Test
    fun testOrphanedDataDetection() = runTest {
        // Setup test data
        setupTestWorkoutAndExercise()
        val instance = createValidExerciseInstance()
        validatedExerciseInstanceRepository.insertExerciseInstance(instance)
        
        // Add a set
        val set = ExerciseSet("set1", instance.id, 1, 100.0, 10, 0, "")
        validatedExerciseSetRepository.insertSet(set)
        
        // Delete the exercise instance directly from database (creating orphaned set)
        database.exerciseInstanceDao().deleteExerciseInstance(instance.toEntity())
        
        // Check for orphaned data
        val orphanedResult = dataIntegrityValidator.checkForOrphanedSets()
        
        assertTrue("Should detect orphaned sets", orphanedResult.orphanedSets.isNotEmpty())
        assertEquals("Should find the orphaned set", set.id, orphanedResult.orphanedSets.first().id)
    }

    private suspend fun setupTestWorkoutAndExercise() {
        val workout = Workout("test_workout", "Test Workout", "Test", System.currentTimeMillis(), System.currentTimeMillis())
        workoutRepository.insertWorkout(workout)
        
        val exercise = Exercise("test_exercise", "Test Exercise", "Test", listOf("Test"), "None", 
                               "Test", System.currentTimeMillis(), System.currentTimeMillis(), false)
        exerciseRepository.insertExercise(exercise)
    }
    
    private fun createValidExerciseInstance(): ExerciseInstance {
        return ExerciseInstance(
            id = "test_instance",
            workoutId = "test_workout",
            exerciseId = "test_exercise",
            orderInWorkout = 1,
            notes = "Test instance"
        )
    }
    
    private fun ExerciseInstance.toEntity() = com.example.gym_tracker.core.database.entity.ExerciseInstanceEntity(
        id, workoutId, exerciseId, orderInWorkout, notes
    )
    
    private fun ExerciseSet.toEntity() = com.example.gym_tracker.core.database.entity.ExerciseSetEntity(
        id, exerciseInstanceId, setNumber, weight, reps, 
        java.time.Duration.ofSeconds(restTime.toLong()), null, null, false, false, notes
    )
}