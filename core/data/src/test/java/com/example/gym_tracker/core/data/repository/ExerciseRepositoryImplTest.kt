package com.example.gym_tracker.core.data.repository

import com.example.gym_tracker.core.data.model.Equipment
import com.example.gym_tracker.core.data.model.Exercise
import com.example.gym_tracker.core.data.model.ExerciseCategory
import com.example.gym_tracker.core.data.model.MuscleGroup
import com.example.gym_tracker.core.data.repository.impl.ExerciseRepositoryImpl
import com.example.gym_tracker.core.database.dao.ExerciseDao
import com.example.gym_tracker.core.database.entity.ExerciseEntity
import com.example.gym_tracker.core.database.entity.Equipment as DbEquipment
import com.example.gym_tracker.core.database.entity.ExerciseCategory as DbExerciseCategory
import com.example.gym_tracker.core.database.entity.MuscleGroup as DbMuscleGroup
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

class ExerciseRepositoryImplTest {

    private lateinit var exerciseDao: ExerciseDao
    private lateinit var repository: ExerciseRepositoryImpl

    @Before
    fun setup() {
        exerciseDao = mockk()
        repository = ExerciseRepositoryImpl(exerciseDao)
    }

    @Test
    fun `getAllExercises returns mapped domain models`() = runTest {
        // Given
        val dbExercises = listOf(
            createDbExercise(name = "Bench Press"),
            createDbExercise(name = "Squat")
        )
        every { exerciseDao.getAllExercises() } returns flowOf(dbExercises)

        // When
        val result = repository.getAllExercises().first()

        // Then
        assertThat(result).hasSize(2)
        assertThat(result.map { it.name }).containsExactly("Bench Press", "Squat")
        assertThat(result.first().category).isEqualTo(ExerciseCategory.CHEST)
    }

    @Test
    fun `getExerciseById returns mapped domain model`() = runTest {
        // Given
        val dbExercise = createDbExercise(name = "Bench Press")
        coEvery { exerciseDao.getExerciseById("test-id") } returns dbExercise

        // When
        val result = repository.getExerciseById("test-id")

        // Then
        assertThat(result).isNotNull()
        assertThat(result?.name).isEqualTo("Bench Press")
        assertThat(result?.category).isEqualTo(ExerciseCategory.CHEST)
    }

    @Test
    fun `getExerciseById returns null when not found`() = runTest {
        // Given
        coEvery { exerciseDao.getExerciseById("non-existent") } returns null

        // When
        val result = repository.getExerciseById("non-existent")

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `insertExercise calls dao with mapped entity`() = runTest {
        // Given
        val exercise = createDomainExercise(name = "New Exercise")
        coEvery { exerciseDao.insertExercise(any()) } returns Unit

        // When
        repository.insertExercise(exercise)

        // Then
        coVerify { exerciseDao.insertExercise(any()) }
    }

    @Test
    fun `searchExercisesByName returns filtered results`() = runTest {
        // Given
        val dbExercises = listOf(
            createDbExercise(name = "Bench Press"),
            createDbExercise(name = "Incline Bench Press")
        )
        every { exerciseDao.searchExercisesByName("Bench") } returns flowOf(dbExercises)

        // When
        val result = repository.searchExercisesByName("Bench").first()

        // Then
        assertThat(result).hasSize(2)
        assertThat(result.map { it.name }).containsExactly("Bench Press", "Incline Bench Press")
    }

    @Test
    fun `getExercisesByCategory returns filtered results`() = runTest {
        // Given
        val dbExercises = listOf(createDbExercise(name = "Bench Press"))
        every { exerciseDao.getExercisesByCategory(DbExerciseCategory.CHEST) } returns flowOf(dbExercises)

        // When
        val result = repository.getExercisesByCategory(ExerciseCategory.CHEST).first()

        // Then
        assertThat(result).hasSize(1)
        assertThat(result.first().name).isEqualTo("Bench Press")
        assertThat(result.first().category).isEqualTo(ExerciseCategory.CHEST)
    }

    private fun createDbExercise(
        id: String = "test-id",
        name: String = "Test Exercise",
        category: DbExerciseCategory = DbExerciseCategory.CHEST,
        muscleGroups: List<DbMuscleGroup> = listOf(DbMuscleGroup.CHEST),
        equipment: DbEquipment = DbEquipment.BARBELL
    ) = ExerciseEntity(
        id = id,
        name = name,
        category = category,
        muscleGroups = muscleGroups,
        equipment = equipment,
        instructions = listOf("Test instruction"),
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
        isCustom = false
    )

    private fun createDomainExercise(
        id: String = "test-id",
        name: String = "Test Exercise",
        category: ExerciseCategory = ExerciseCategory.CHEST,
        muscleGroups: List<MuscleGroup> = listOf(MuscleGroup.CHEST),
        equipment: Equipment = Equipment.BARBELL
    ) = Exercise(
        id = id,
        name = name,
        category = category,
        muscleGroups = muscleGroups,
        equipment = equipment,
        instructions = listOf("Test instruction"),
        createdAt = Instant.now(),
        updatedAt = Instant.now(),
        isCustom = false
    )
}