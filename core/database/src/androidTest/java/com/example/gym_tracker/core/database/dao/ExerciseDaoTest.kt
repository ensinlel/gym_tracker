package com.example.gym_tracker.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gym_tracker.core.common.enums.Equipment
import com.example.gym_tracker.core.common.enums.ExerciseCategory
import com.example.gym_tracker.core.common.enums.MuscleGroup
import com.example.gym_tracker.core.database.GymTrackerDatabase
import com.example.gym_tracker.core.database.entity.ExerciseEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class ExerciseDaoTest {

    private lateinit var database: GymTrackerDatabase
    private lateinit var exerciseDao: ExerciseDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GymTrackerDatabase::class.java
        ).allowMainThreadQueries().build()
        
        exerciseDao = database.exerciseDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertExercise_retrievesCorrectly() = runTest {
        // Given
        val exercise = createTestExercise(
            name = "Bench Press",
            category = ExerciseCategory.CHEST,
            muscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS)
        )

        // When
        exerciseDao.insertExercise(exercise)
        val result = exerciseDao.getExerciseById(exercise.id)

        // Then
        assertThat(result).isEqualTo(exercise)
    }

    @Test
    fun getAllExercises_returnsAllInsertedExercises() = runTest {
        // Given
        val exercises = listOf(
            createTestExercise(name = "Bench Press", category = ExerciseCategory.CHEST),
            createTestExercise(name = "Squat", category = ExerciseCategory.LEGS),
            createTestExercise(name = "Deadlift", category = ExerciseCategory.BACK)
        )

        // When
        exercises.forEach { exerciseDao.insertExercise(it) }
        val result = exerciseDao.getAllExercises().first()

        // Then
        assertThat(result).hasSize(3)
        assertThat(result.map { it.name }).containsExactly("Bench Press", "Deadlift", "Squat") // Ordered by name ASC
    }

    @Test
    fun getExercisesByCategory_filtersCorrectly() = runTest {
        // Given
        val chestExercise = createTestExercise(name = "Bench Press", category = ExerciseCategory.CHEST)
        val legExercise = createTestExercise(name = "Squat", category = ExerciseCategory.LEGS)
        
        exerciseDao.insertExercise(chestExercise)
        exerciseDao.insertExercise(legExercise)

        // When
        val chestExercises = exerciseDao.getExercisesByCategory(ExerciseCategory.CHEST).first()

        // Then
        assertThat(chestExercises).hasSize(1)
        assertThat(chestExercises.first().name).isEqualTo("Bench Press")
    }

    @Test
    fun searchExercisesByName_findsPartialMatches() = runTest {
        // Given
        val exercises = listOf(
            createTestExercise(name = "Bench Press"),
            createTestExercise(name = "Incline Bench Press"),
            createTestExercise(name = "Squat")
        )
        exercises.forEach { exerciseDao.insertExercise(it) }

        // When
        val result = exerciseDao.searchExercisesByName("Bench").first()

        // Then
        assertThat(result).hasSize(2)
        assertThat(result.map { it.name }).containsExactly("Bench Press", "Incline Bench Press")
    }

    @Test
    fun updateExercise_updatesCorrectly() = runTest {
        // Given
        val exercise = createTestExercise(name = "Bench Press")
        exerciseDao.insertExercise(exercise)

        // When
        val updatedExercise = exercise.copy(name = "Updated Bench Press")
        exerciseDao.updateExercise(updatedExercise)
        val result = exerciseDao.getExerciseById(exercise.id)

        // Then
        assertThat(result?.name).isEqualTo("Updated Bench Press")
    }

    @Test
    fun deleteExercise_removesFromDatabase() = runTest {
        // Given
        val exercise = createTestExercise(name = "Bench Press")
        exerciseDao.insertExercise(exercise)

        // When
        exerciseDao.deleteExercise(exercise)
        val result = exerciseDao.getExerciseById(exercise.id)

        // Then
        assertThat(result).isNull()
    }

    private fun createTestExercise(
        id: String = "test-id-${System.currentTimeMillis()}",
        name: String = "Test Exercise",
        category: ExerciseCategory = ExerciseCategory.CHEST,
        muscleGroups: List<MuscleGroup> = listOf(MuscleGroup.CHEST),
        equipment: Equipment = Equipment.BARBELL,
        instructions: List<String> = listOf("Test instruction"),
        isCustom: Boolean = false
    ) = ExerciseEntity(
        id = id,
        name = name,
        category = category,
        muscleGroups = muscleGroups,
        equipment = equipment,
        instructions = instructions,
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
        isCustom = isCustom
    )
}