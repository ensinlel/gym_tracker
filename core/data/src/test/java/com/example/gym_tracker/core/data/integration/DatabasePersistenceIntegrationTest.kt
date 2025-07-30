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
 * Integration tests for database persistence across app restarts and data integrity
 */
@RunWith(AndroidJUnit4::class)
class DatabasePersistenceIntegrationTest {

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
    fun testExerciseInstancePersistenceAcrossAppRestart() = runTest {
        // Simulate app session 1: Create exercise instance
        val workoutId = "workout_1"
        val exerciseId = "exercise_1"
        val exerciseInstance = ExerciseInstance(
            id = "instance_1",
            workoutId = workoutId,
            exerciseId = exerciseId,
            orderInWorkout = 1,
            notes = "Test exercise instance"
        )

        // Insert exercise instance
        val insertedId = exerciseInstanceRepository.insertExerciseInstance(exerciseInstance)
        assertEquals(exerciseInstance.id, insertedId)

        // Verify immediate retrieval works
        val retrievedInstance = exerciseInstanceRepository.getExerciseInstanceById(exerciseInstance.id)
        assertNotNull(retrievedInstance)
        assertEquals(exerciseInstance.id, retrievedInstance?.id)
        assertEquals(exerciseInstance.workoutId, retrievedInstance?.workoutId)

        // Simulate app restart: Clear cache (simulates process termination)
        cache.clearAll()

        // Verify data persists after "restart"
        val persistedInstance = exerciseInstanceRepository.getExerciseInstanceById(exerciseInstance.id)
        assertNotNull(persistedInstance)
        assertEquals(exerciseInstance.id, persistedInstance?.id)
        assertEquals(exerciseInstance.workoutId, persistedInstance?.workoutId)
        assertEquals(exerciseInstance.exerciseId, persistedInstance?.exerciseId)
        assertEquals(exerciseInstance.orderInWorkout, persistedInstance?.orderInWorkout)
        assertEquals(exerciseInstance.notes, persistedInstance?.notes)
    }

    @Test
    fun testExerciseSetPersistenceAcrossAppRestart() = runTest {
        // Create exercise instance first
        val exerciseInstance = ExerciseInstance(
            id = "instance_2",
            workoutId = "workout_2",
            exerciseId = "exercise_2",
            orderInWorkout = 1,
            notes = ""
        )
        exerciseInstanceRepository.insertExerciseInstance(exerciseInstance)

        // Create exercise sets
        val set1 = ExerciseSet(
            id = "set_1",
            exerciseInstanceId = exerciseInstance.id,
            setNumber = 1,
            weight = 100.0,
            reps = 10,
            restTime = 0,
            notes = "First set"
        )
        
        val set2 = ExerciseSet(
            id = "set_2",
            exerciseInstanceId = exerciseInstance.id,
            setNumber = 2,
            weight = 105.0,
            reps = 8,
            restTime = 0,
            notes = "Second set"
        )

        // Insert sets
        exerciseSetRepository.insertSet(set1)
        exerciseSetRepository.insertSet(set2)

        // Verify immediate retrieval
        val retrievedSets = exerciseSetRepository.getSetsByExerciseInstance(exerciseInstance.id).first()
        assertEquals(2, retrievedSets.size)

        // Simulate app restart: Clear cache
        cache.clearAll()

        // Verify sets persist after "restart"
        val persistedSets = exerciseSetRepository.getSetsByExerciseInstance(exerciseInstance.id).first()
        assertEquals(2, persistedSets.size)
        
        val persistedSet1 = persistedSets.find { it.id == set1.id }
        val persistedSet2 = persistedSets.find { it.id == set2.id }
        
        assertNotNull(persistedSet1)
        assertNotNull(persistedSet2)
        
        assertEquals(set1.weight, persistedSet1?.weight, 0.01)
        assertEquals(set1.reps, persistedSet1?.reps)
        assertEquals(set2.weight, persistedSet2?.weight, 0.01)
        assertEquals(set2.reps, persistedSet2?.reps)
    }

    @Test
    fun testCompleteWorkoutPersistenceScenario() = runTest {
        val workoutId = "complete_workout_test"
        
        // Create multiple exercise instances for a workout
        val exercises = listOf(
            ExerciseInstance("ex1", workoutId, "bench_press", 1, "Chest exercise"),
            ExerciseInstance("ex2", workoutId, "incline_press", 2, "Upper chest"),
            ExerciseInstance("ex3", workoutId, "flyes", 3, "Chest isolation")
        )

        // Insert all exercises
        exercises.forEach { exercise ->
            exerciseInstanceRepository.insertExerciseInstance(exercise)
        }

        // Add sets to each exercise
        val allSets = mutableListOf<ExerciseSet>()
        exercises.forEachIndexed { exerciseIndex, exercise ->
            repeat(3) { setIndex ->
                val set = ExerciseSet(
                    id = "set_${exerciseIndex}_${setIndex}",
                    exerciseInstanceId = exercise.id,
                    setNumber = setIndex + 1,
                    weight = 80.0 + (setIndex * 5),
                    reps = 10 - setIndex,
                    restTime = 0,
                    notes = "Set ${setIndex + 1}"
                )
                allSets.add(set)
                exerciseSetRepository.insertSet(set)
            }
        }

        // Verify complete workout data
        val workoutExercises = exerciseInstanceRepository.getExerciseInstancesByWorkoutId(workoutId).first()
        assertEquals(3, workoutExercises.size)

        // Simulate app restart
        cache.clearAll()

        // Verify all data persists
        val persistedExercises = exerciseInstanceRepository.getExerciseInstancesByWorkoutId(workoutId).first()
        assertEquals(3, persistedExercises.size)

        // Verify sets for each exercise persist
        persistedExercises.forEach { exercise ->
            val sets = exerciseSetRepository.getSetsByExerciseInstance(exercise.id).first()
            assertEquals(3, sets.size)
            
            // Verify set data integrity
            sets.forEach { set ->
                assertTrue(set.weight > 0)
                assertTrue(set.reps > 0)
                assertTrue(set.setNumber in 1..3)
            }
        }
    }

    @Test
    fun testDataIntegrityAcrossCRUDOperations() = runTest {
        val workoutId = "crud_test_workout"
        val exerciseInstance = ExerciseInstance(
            id = "crud_exercise",
            workoutId = workoutId,
            exerciseId = "test_exercise",
            orderInWorkout = 1,
            notes = "Original notes"
        )

        // CREATE
        exerciseInstanceRepository.insertExerciseInstance(exerciseInstance)
        var retrieved = exerciseInstanceRepository.getExerciseInstanceById(exerciseInstance.id)
        assertNotNull(retrieved)
        assertEquals("Original notes", retrieved?.notes)

        // UPDATE
        val updatedInstance = exerciseInstance.copy(notes = "Updated notes")
        exerciseInstanceRepository.updateExerciseInstance(updatedInstance)
        
        // Simulate app restart
        cache.clearAll()
        
        retrieved = exerciseInstanceRepository.getExerciseInstanceById(exerciseInstance.id)
        assertNotNull(retrieved)
        assertEquals("Updated notes", retrieved?.notes)

        // DELETE
        exerciseInstanceRepository.deleteExerciseInstance(exerciseInstance.id)
        
        // Simulate app restart
        cache.clearAll()
        
        retrieved = exerciseInstanceRepository.getExerciseInstanceById(exerciseInstance.id)
        assertNull(retrieved)
    }

    @Test
    fun testCacheConsistencyWithDatabaseOperations() = runTest {
        val exerciseInstance = ExerciseInstance(
            id = "cache_test",
            workoutId = "cache_workout",
            exerciseId = "cache_exercise",
            orderInWorkout = 1,
            notes = "Cache test"
        )

        // Insert and verify cache is populated
        exerciseInstanceRepository.insertExerciseInstance(exerciseInstance)
        
        // First retrieval should populate cache
        val firstRetrieval = exerciseInstanceRepository.getExerciseInstanceById(exerciseInstance.id)
        assertNotNull(firstRetrieval)

        // Second retrieval should come from cache (faster)
        val startTime = System.currentTimeMillis()
        val cachedRetrieval = exerciseInstanceRepository.getExerciseInstanceById(exerciseInstance.id)
        val cacheTime = System.currentTimeMillis() - startTime
        
        assertNotNull(cachedRetrieval)
        assertEquals(firstRetrieval?.id, cachedRetrieval?.id)
        
        // Cache retrieval should be very fast (< 10ms)
        assertTrue("Cache retrieval should be fast", cacheTime < 10)

        // Update should invalidate cache
        val updatedInstance = exerciseInstance.copy(notes = "Updated via cache test")
        exerciseInstanceRepository.updateExerciseInstance(updatedInstance)

        // Next retrieval should get updated data
        val updatedRetrieval = exerciseInstanceRepository.getExerciseInstanceById(exerciseInstance.id)
        assertNotNull(updatedRetrieval)
        assertEquals("Updated via cache test", updatedRetrieval?.notes)
    }

    @Test
    fun testPerformanceMonitoringIntegration() = runTest {
        val exerciseInstance = ExerciseInstance(
            id = "perf_test",
            workoutId = "perf_workout",
            exerciseId = "perf_exercise",
            orderInWorkout = 1,
            notes = "Performance test"
        )

        // Perform operations that should be monitored
        exerciseInstanceRepository.insertExerciseInstance(exerciseInstance)
        exerciseInstanceRepository.getExerciseInstanceById(exerciseInstance.id)
        exerciseInstanceRepository.updateExerciseInstance(exerciseInstance.copy(notes = "Updated"))

        // Verify performance metrics were collected
        val performanceSummary = performanceMonitor.getPerformanceSummary()
        
        assertTrue("Should have insert metrics", performanceSummary.containsKey("insertExerciseInstance"))
        assertTrue("Should have get metrics", performanceSummary.containsKey("getExerciseInstanceById"))
        
        val insertStats = performanceSummary["insertExerciseInstance"]
        assertNotNull(insertStats)
        assertTrue("Should have recorded operations", insertStats!!.operationCount > 0)
        assertTrue("Should have reasonable execution time", insertStats.averageTime < 1000) // Less than 1 second
    }

    @Test
    fun testWorkoutDeletionCascade() = runTest {
        val workoutId = "cascade_test_workout"
        
        // Create exercise instance
        val exerciseInstance = ExerciseInstance(
            id = "cascade_exercise",
            workoutId = workoutId,
            exerciseId = "test_exercise",
            orderInWorkout = 1,
            notes = "Cascade test"
        )
        exerciseInstanceRepository.insertExerciseInstance(exerciseInstance)

        // Create sets for the exercise
        val sets = listOf(
            ExerciseSet("set1", exerciseInstance.id, 1, 100.0, 10, 0, "Set 1"),
            ExerciseSet("set2", exerciseInstance.id, 2, 105.0, 8, 0, "Set 2")
        )
        sets.forEach { exerciseSetRepository.insertSet(it) }

        // Verify data exists
        assertNotNull(exerciseInstanceRepository.getExerciseInstanceById(exerciseInstance.id))
        assertEquals(2, exerciseSetRepository.getSetsByExerciseInstance(exerciseInstance.id).first().size)

        // Delete all exercise instances for the workout
        exerciseInstanceRepository.deleteExerciseInstancesByWorkoutId(workoutId)

        // Simulate app restart
        cache.clearAll()

        // Verify cascade deletion worked
        assertNull(exerciseInstanceRepository.getExerciseInstanceById(exerciseInstance.id))
        assertEquals(0, exerciseSetRepository.getSetsByExerciseInstance(exerciseInstance.id).first().size)
    }
}