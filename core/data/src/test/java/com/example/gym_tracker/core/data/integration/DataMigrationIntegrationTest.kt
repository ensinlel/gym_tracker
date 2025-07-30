package com.example.gym_tracker.core.data.integration

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gym_tracker.core.data.cache.ExerciseInstanceCache
import com.example.gym_tracker.core.data.model.ExerciseInstance
import com.example.gym_tracker.core.data.model.ExerciseSet
import com.example.gym_tracker.core.data.repository.impl.ExerciseInstanceRepositoryImpl
import com.example.gym_tracker.core.data.repository.impl.ExerciseSetRepositoryImpl
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
 * Integration tests for data migration scenarios
 */
@RunWith(AndroidJUnit4::class)
class DataMigrationIntegrationTest {

    private lateinit var database: GymTrackerDatabase
    private lateinit var exerciseInstanceRepository: ExerciseInstanceRepositoryImpl
    private lateinit var exerciseSetRepository: ExerciseSetRepositoryImpl
    private lateinit var cache: ExerciseInstanceCache
    private lateinit var performanceMonitor: PerformanceMonitor

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GymTrackerDatabase::class.java
        ).build()
        
        cache = ExerciseInstanceCache()
        performanceMonitor = PerformanceMonitor()
        
        exerciseInstanceRepository = ExerciseInstanceRepositoryImpl(
            database.exerciseInstanceDao(),
            cache,
            performanceMonitor
        )
        
        exerciseSetRepository = ExerciseSetRepositoryImpl(
            database.exerciseSetDao()
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun testEmptyDatabaseMigration() = runTest {
        // Simulate migration with empty database
        val workoutExercises = exerciseInstanceRepository.getExerciseInstancesByWorkoutId("empty_workout").first()
        assertTrue("Empty database should return empty list", workoutExercises.isEmpty())
        
        // Verify no errors occur with empty state
        val nonExistentInstance = exerciseInstanceRepository.getExerciseInstanceById("non_existent")
        assertNull("Non-existent instance should return null", nonExistentInstance)
    }

    @Test
    fun testPartialDataMigration() = runTest {
        val workoutId = "partial_migration_workout"
        
        // Simulate scenario where some exercises have sets, others don't
        val exerciseWithSets = ExerciseInstance(
            id = "exercise_with_sets",
            workoutId = workoutId,
            exerciseId = "bench_press",
            orderInWorkout = 1,
            notes = "Has sets"
        )
        
        val exerciseWithoutSets = ExerciseInstance(
            id = "exercise_without_sets",
            workoutId = workoutId,
            exerciseId = "shoulder_press",
            orderInWorkout = 2,
            notes = "No sets yet"
        )

        // Insert exercises
        exerciseInstanceRepository.insertExerciseInstance(exerciseWithSets)
        exerciseInstanceRepository.insertExerciseInstance(exerciseWithoutSets)

        // Add sets only to first exercise
        val sets = listOf(
            ExerciseSet("set1", exerciseWithSets.id, 1, 100.0, 10, 0, "Set 1"),
            ExerciseSet("set2", exerciseWithSets.id, 2, 105.0, 8, 0, "Set 2")
        )
        sets.forEach { exerciseSetRepository.insertSet(it) }

        // Verify partial migration state
        val allExercises = exerciseInstanceRepository.getExerciseInstancesByWorkoutId(workoutId).first()
        assertEquals(2, allExercises.size)

        val setsForFirstExercise = exerciseSetRepository.getSetsByExerciseInstance(exerciseWithSets.id).first()
        val setsForSecondExercise = exerciseSetRepository.getSetsByExerciseInstance(exerciseWithoutSets.id).first()

        assertEquals(2, setsForFirstExercise.size)
        assertEquals(0, setsForSecondExercise.size)

        // Simulate app restart
        cache.clearAll()

        // Verify partial state persists correctly
        val persistedExercises = exerciseInstanceRepository.getExerciseInstancesByWorkoutId(workoutId).first()
        assertEquals(2, persistedExercises.size)

        val persistedSetsFirst = exerciseSetRepository.getSetsByExerciseInstance(exerciseWithSets.id).first()
        val persistedSetsSecond = exerciseSetRepository.getSetsByExerciseInstance(exerciseWithoutSets.id).first()

        assertEquals(2, persistedSetsFirst.size)
        assertEquals(0, persistedSetsSecond.size)
    }

    @Test
    fun testLargeDatasetMigration() = runTest {
        val workoutId = "large_dataset_workout"
        val exerciseCount = 50
        val setsPerExercise = 5

        // Create large dataset
        val exercises = (1..exerciseCount).map { exerciseIndex ->
            ExerciseInstance(
                id = "exercise_$exerciseIndex",
                workoutId = workoutId,
                exerciseId = "exercise_type_$exerciseIndex",
                orderInWorkout = exerciseIndex,
                notes = "Exercise $exerciseIndex"
            )
        }

        // Insert all exercises
        exercises.forEach { exercise ->
            exerciseInstanceRepository.insertExerciseInstance(exercise)
        }

        // Add sets to each exercise
        exercises.forEach { exercise ->
            repeat(setsPerExercise) { setIndex ->
                val set = ExerciseSet(
                    id = "${exercise.id}_set_${setIndex + 1}",
                    exerciseInstanceId = exercise.id,
                    setNumber = setIndex + 1,
                    weight = 50.0 + (setIndex * 10),
                    reps = 12 - setIndex,
                    restTime = 0,
                    notes = "Set ${setIndex + 1}"
                )
                exerciseSetRepository.insertSet(set)
            }
        }

        // Verify large dataset
        val allExercises = exerciseInstanceRepository.getExerciseInstancesByWorkoutId(workoutId).first()
        assertEquals(exerciseCount, allExercises.size)

        // Verify total sets
        var totalSets = 0
        allExercises.forEach { exercise ->
            val sets = exerciseSetRepository.getSetsByExerciseInstance(exercise.id).first()
            totalSets += sets.size
        }
        assertEquals(exerciseCount * setsPerExercise, totalSets)

        // Simulate app restart with large dataset
        cache.clearAll()

        // Verify large dataset persists
        val persistedExercises = exerciseInstanceRepository.getExerciseInstancesByWorkoutId(workoutId).first()
        assertEquals(exerciseCount, persistedExercises.size)

        // Verify data integrity for large dataset
        var persistedTotalSets = 0
        persistedExercises.forEach { exercise ->
            val sets = exerciseSetRepository.getSetsByExerciseInstance(exercise.id).first()
            persistedTotalSets += sets.size
            
            // Verify set data integrity
            sets.forEach { set ->
                assertTrue("Weight should be positive", set.weight > 0)
                assertTrue("Reps should be positive", set.reps > 0)
                assertTrue("Set number should be valid", set.setNumber in 1..setsPerExercise)
            }
        }
        assertEquals(exerciseCount * setsPerExercise, persistedTotalSets)
    }

    @Test
    fun testMigrationWithDuplicateData() = runTest {
        val workoutId = "duplicate_test_workout"
        val exerciseInstance = ExerciseInstance(
            id = "duplicate_exercise",
            workoutId = workoutId,
            exerciseId = "test_exercise",
            orderInWorkout = 1,
            notes = "Original"
        )

        // Insert original data
        exerciseInstanceRepository.insertExerciseInstance(exerciseInstance)

        // Attempt to insert duplicate (should replace due to REPLACE conflict strategy)
        val duplicateInstance = exerciseInstance.copy(notes = "Duplicate")
        exerciseInstanceRepository.insertExerciseInstance(duplicateInstance)

        // Verify only one instance exists with updated data
        val retrieved = exerciseInstanceRepository.getExerciseInstanceById(exerciseInstance.id)
        assertNotNull(retrieved)
        assertEquals("Duplicate", retrieved?.notes)

        val allExercises = exerciseInstanceRepository.getExerciseInstancesByWorkoutId(workoutId).first()
        assertEquals(1, allExercises.size)

        // Simulate app restart
        cache.clearAll()

        // Verify duplicate handling persists
        val persistedExercises = exerciseInstanceRepository.getExerciseInstancesByWorkoutId(workoutId).first()
        assertEquals(1, persistedExercises.size)
        assertEquals("Duplicate", persistedExercises.first().notes)
    }

    @Test
    fun testMigrationErrorRecovery() = runTest {
        val workoutId = "error_recovery_workout"
        
        // Create valid exercise instance
        val validExercise = ExerciseInstance(
            id = "valid_exercise",
            workoutId = workoutId,
            exerciseId = "valid_exercise_type",
            orderInWorkout = 1,
            notes = "Valid exercise"
        )

        // Insert valid data
        exerciseInstanceRepository.insertExerciseInstance(validExercise)

        // Add valid set
        val validSet = ExerciseSet(
            id = "valid_set",
            exerciseInstanceId = validExercise.id,
            setNumber = 1,
            weight = 100.0,
            reps = 10,
            restTime = 0,
            notes = "Valid set"
        )
        exerciseSetRepository.insertSet(validSet)

        // Verify valid data exists
        val exercises = exerciseInstanceRepository.getExerciseInstancesByWorkoutId(workoutId).first()
        assertEquals(1, exercises.size)

        val sets = exerciseSetRepository.getSetsByExerciseInstance(validExercise.id).first()
        assertEquals(1, sets.size)

        // Simulate app restart after partial failure
        cache.clearAll()

        // Verify valid data still exists after "recovery"
        val recoveredExercises = exerciseInstanceRepository.getExerciseInstancesByWorkoutId(workoutId).first()
        assertEquals(1, recoveredExercises.size)
        assertEquals("Valid exercise", recoveredExercises.first().notes)

        val recoveredSets = exerciseSetRepository.getSetsByExerciseInstance(validExercise.id).first()
        assertEquals(1, recoveredSets.size)
        assertEquals(100.0, recoveredSets.first().weight, 0.01)
    }

    @Test
    fun testMigrationPerformanceWithVariousDataStates() = runTest {
        val scenarios = listOf(
            "small_workout" to 5,
            "medium_workout" to 20,
            "large_workout" to 50
        )

        scenarios.forEach { (workoutId, exerciseCount) ->
            val startTime = System.currentTimeMillis()

            // Create exercises
            repeat(exerciseCount) { index ->
                val exercise = ExerciseInstance(
                    id = "${workoutId}_exercise_$index",
                    workoutId = workoutId,
                    exerciseId = "exercise_type_$index",
                    orderInWorkout = index + 1,
                    notes = "Exercise $index"
                )
                exerciseInstanceRepository.insertExerciseInstance(exercise)

                // Add 3 sets per exercise
                repeat(3) { setIndex ->
                    val set = ExerciseSet(
                        id = "${exercise.id}_set_$setIndex",
                        exerciseInstanceId = exercise.id,
                        setNumber = setIndex + 1,
                        weight = 80.0 + (setIndex * 5),
                        reps = 10 - setIndex,
                        restTime = 0,
                        notes = "Set ${setIndex + 1}"
                    )
                    exerciseSetRepository.insertSet(set)
                }
            }

            val migrationTime = System.currentTimeMillis() - startTime

            // Verify migration completed successfully
            val exercises = exerciseInstanceRepository.getExerciseInstancesByWorkoutId(workoutId).first()
            assertEquals(exerciseCount, exercises.size)

            // Performance should be reasonable (less than 5 seconds for largest dataset)
            assertTrue(
                "Migration for $exerciseCount exercises should complete in reasonable time",
                migrationTime < 5000
            )

            // Clear cache and verify persistence
            cache.clearAll()
            val persistedExercises = exerciseInstanceRepository.getExerciseInstancesByWorkoutId(workoutId).first()
            assertEquals(exerciseCount, persistedExercises.size)
        }
    }
}