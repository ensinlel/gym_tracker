package com.example.gym_tracker.core.data.util

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.gym_tracker.core.data.model.Exercise
import com.example.gym_tracker.core.data.model.Workout
import com.example.gym_tracker.core.database.GymTrackerDatabase
import kotlinx.coroutines.runBlocking

/**
 * Helper class for setting up test database with common data
 */
object TestDatabaseHelper {
    
    /**
     * Create an in-memory database for testing
     */
    fun createTestDatabase(): GymTrackerDatabase {
        return Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GymTrackerDatabase::class.java
        ).build()
    }
    
    /**
     * Populate database with sample workout data
     */
    fun populateWithSampleData(database: GymTrackerDatabase) = runBlocking {
        val workoutDao = database.workoutDao()
        val exerciseDao = database.exerciseDao()
        
        // Create sample workouts
        val workouts = listOf(
            Workout("1", "Push Day", "Chest, shoulders, triceps", System.currentTimeMillis(), System.currentTimeMillis()),
            Workout("2", "Pull Day", "Back, biceps", System.currentTimeMillis(), System.currentTimeMillis()),
            Workout("3", "Leg Day", "Legs, glutes", System.currentTimeMillis(), System.currentTimeMillis())
        )
        
        workouts.forEach { workoutDao.insertWorkout(it.toEntity()) }
        
        // Create sample exercises
        val exercises = listOf(
            Exercise("1", "Bench Press", "Chest", listOf("Chest", "Triceps"), "Barbell", 
                    "Bench press instructions", System.currentTimeMillis(), System.currentTimeMillis(), false),
            Exercise("2", "Incline Dumbbell Press", "Chest", listOf("Chest", "Triceps"), "Dumbbells", 
                    "Incline press instructions", System.currentTimeMillis(), System.currentTimeMillis(), false),
            Exercise("3", "Shoulder Press", "Shoulders", listOf("Shoulders", "Triceps"), "Dumbbells", 
                    "Shoulder press instructions", System.currentTimeMillis(), System.currentTimeMillis(), false),
            Exercise("4", "Tricep Dips", "Triceps", listOf("Triceps"), "Bodyweight", 
                    "Tricep dips instructions", System.currentTimeMillis(), System.currentTimeMillis(), false),
            Exercise("5", "Push-ups", "Chest", listOf("Chest", "Triceps"), "Bodyweight", 
                    "Push-ups instructions", System.currentTimeMillis(), System.currentTimeMillis(), false)
        )
        
        exercises.forEach { exerciseDao.insertExercise(it.toEntity()) }
    }
    
    /**
     * Create a workout with specified number of exercises and sets
     */
    fun createWorkoutWithData(
        database: GymTrackerDatabase,
        workoutId: String,
        workoutName: String,
        exerciseCount: Int,
        setsPerExercise: Int
    ) = runBlocking {
        val workoutDao = database.workoutDao()
        val exerciseDao = database.exerciseDao()
        val exerciseInstanceDao = database.exerciseInstanceDao()
        val exerciseSetDao = database.exerciseSetDao()
        
        // Create workout
        val workout = Workout(workoutId, workoutName, "Test workout", System.currentTimeMillis(), System.currentTimeMillis())
        workoutDao.insertWorkout(workout.toEntity())
        
        // Create exercises and instances
        repeat(exerciseCount) { exerciseIndex ->
            val exerciseId = "${workoutId}_exercise_$exerciseIndex"
            val exercise = Exercise(
                exerciseId, "Exercise $exerciseIndex", "Test", listOf("Test"), "None",
                "Test instructions", System.currentTimeMillis(), System.currentTimeMillis(), false
            )
            exerciseDao.insertExercise(exercise.toEntity())
            
            // Create exercise instance
            val exerciseInstance = com.example.gym_tracker.core.data.model.ExerciseInstance(
                "${workoutId}_instance_$exerciseIndex", workoutId, exerciseId, exerciseIndex + 1, "Test instance"
            )
            exerciseInstanceDao.insertExerciseInstance(exerciseInstance.toEntity())
            
            // Create sets
            repeat(setsPerExercise) { setIndex ->
                val exerciseSet = com.example.gym_tracker.core.data.model.ExerciseSet(
                    "${exerciseInstance.id}_set_$setIndex",
                    exerciseInstance.id,
                    setIndex + 1,
                    80.0 + (setIndex * 5),
                    10 - setIndex,
                    0,
                    "Set ${setIndex + 1}"
                )
                exerciseSetDao.insertExerciseSet(exerciseSet.toEntity())
            }
        }
    }
    
    /**
     * Verify database integrity
     */
    fun verifyDatabaseIntegrity(database: GymTrackerDatabase): Boolean = runBlocking {
        try {
            val workoutDao = database.workoutDao()
            val exerciseDao = database.exerciseDao()
            val exerciseInstanceDao = database.exerciseInstanceDao()
            val exerciseSetDao = database.exerciseSetDao()
            
            // Basic integrity checks
            val workouts = workoutDao.getAllWorkoutsSync()
            val exercises = exerciseDao.getAllExercisesSync()
            val instances = exerciseInstanceDao.getExerciseInstancesForWorkoutSync("test")
            val sets = exerciseSetDao.getAllSetsSync()
            
            // All queries should execute without error
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get database statistics for testing
     */
    fun getDatabaseStats(database: GymTrackerDatabase): DatabaseStats = runBlocking {
        val workoutDao = database.workoutDao()
        val exerciseDao = database.exerciseDao()
        val exerciseInstanceDao = database.exerciseInstanceDao()
        val exerciseSetDao = database.exerciseSetDao()
        
        DatabaseStats(
            workoutCount = workoutDao.getAllWorkoutsSync().size,
            exerciseCount = exerciseDao.getAllExercisesSync().size,
            exerciseInstanceCount = exerciseInstanceDao.getExerciseInstancesForWorkoutSync("").size,
            exerciseSetCount = exerciseSetDao.getAllSetsSync().size
        )
    }
    
    data class DatabaseStats(
        val workoutCount: Int,
        val exerciseCount: Int,
        val exerciseInstanceCount: Int,
        val exerciseSetCount: Int
    )
}

// Extension functions for easier entity conversion in tests
private fun Workout.toEntity() = com.example.gym_tracker.core.database.entity.WorkoutEntity(
    id, name, description, createdAt, updatedAt
)

private fun Exercise.toEntity() = com.example.gym_tracker.core.database.entity.ExerciseEntity(
    id, name, category, muscleGroups, equipment, instructions, createdAt, updatedAt, isCustom, false
)

private fun com.example.gym_tracker.core.data.model.ExerciseInstance.toEntity() = 
    com.example.gym_tracker.core.database.entity.ExerciseInstanceEntity(
        id, workoutId, exerciseId, orderInWorkout, notes
    )

private fun com.example.gym_tracker.core.data.model.ExerciseSet.toEntity() = 
    com.example.gym_tracker.core.database.entity.ExerciseSetEntity(
        id, exerciseInstanceId, setNumber, weight, reps, 
        java.time.Duration.ofSeconds(restTime.toLong()), null, null, false, false, notes
    )