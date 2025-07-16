package com.example.gym_tracker.core.data.mapper

import com.example.gym_tracker.core.data.model.Equipment
import com.example.gym_tracker.core.data.model.Exercise
import com.example.gym_tracker.core.data.model.ExerciseCategory
import com.example.gym_tracker.core.data.model.MuscleGroup
import com.example.gym_tracker.core.database.entity.ExerciseEntity
import com.example.gym_tracker.core.database.entity.Equipment as DbEquipment
import com.example.gym_tracker.core.database.entity.ExerciseCategory as DbExerciseCategory
import com.example.gym_tracker.core.database.entity.MuscleGroup as DbMuscleGroup
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant

class ExerciseMapperTest {

    @Test
    fun `database entity to domain model mapping works correctly`() {
        // Given
        val dbEntity = ExerciseEntity(
            id = "test-id",
            name = "Bench Press",
            category = DbExerciseCategory.CHEST,
            muscleGroups = listOf(DbMuscleGroup.CHEST, DbMuscleGroup.TRICEPS),
            equipment = DbEquipment.BARBELL,
            instructions = listOf("Lie on bench", "Press weight up"),
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            isCustom = false
        )

        // When
        val domainModel = dbEntity.toDomainModel()

        // Then
        assertThat(domainModel.id).isEqualTo("test-id")
        assertThat(domainModel.name).isEqualTo("Bench Press")
        assertThat(domainModel.category).isEqualTo(ExerciseCategory.CHEST)
        assertThat(domainModel.muscleGroups).containsExactly(MuscleGroup.CHEST, MuscleGroup.TRICEPS)
        assertThat(domainModel.equipment).isEqualTo(Equipment.BARBELL)
        assertThat(domainModel.instructions).containsExactly("Lie on bench", "Press weight up")
        assertThat(domainModel.isCustom).isFalse()
    }

    @Test
    fun `domain model to database entity mapping works correctly`() {
        // Given
        val domainModel = Exercise(
            id = "test-id",
            name = "Squat",
            category = ExerciseCategory.LEGS,
            muscleGroups = listOf(MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES),
            equipment = Equipment.BARBELL,
            instructions = listOf("Stand with feet shoulder-width apart", "Squat down"),
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            isCustom = true
        )

        // When
        val dbEntity = domainModel.toEntity()

        // Then
        assertThat(dbEntity.id).isEqualTo("test-id")
        assertThat(dbEntity.name).isEqualTo("Squat")
        assertThat(dbEntity.category).isEqualTo(DbExerciseCategory.LEGS)
        assertThat(dbEntity.muscleGroups).containsExactly(DbMuscleGroup.QUADRICEPS, DbMuscleGroup.GLUTES)
        assertThat(dbEntity.equipment).isEqualTo(DbEquipment.BARBELL)
        assertThat(dbEntity.instructions).containsExactly("Stand with feet shoulder-width apart", "Squat down")
        assertThat(dbEntity.isCustom).isTrue()
    }

    @Test
    fun `enum mapping works correctly`() {
        // Test ExerciseCategory mapping
        assertThat(DbExerciseCategory.CHEST.toDomainModel()).isEqualTo(ExerciseCategory.CHEST)
        assertThat(ExerciseCategory.BACK.toEntity()).isEqualTo(DbExerciseCategory.BACK)

        // Test MuscleGroup mapping
        assertThat(DbMuscleGroup.BICEPS.toDomainModel()).isEqualTo(MuscleGroup.BICEPS)
        assertThat(MuscleGroup.TRICEPS.toEntity()).isEqualTo(DbMuscleGroup.TRICEPS)

        // Test Equipment mapping
        assertThat(DbEquipment.DUMBBELL.toDomainModel()).isEqualTo(Equipment.DUMBBELL)
        assertThat(Equipment.KETTLEBELL.toEntity()).isEqualTo(DbEquipment.KETTLEBELL)
    }

    @Test
    fun `round trip mapping preserves data integrity`() {
        // Given
        val originalEntity = ExerciseEntity(
            id = "round-trip-test",
            name = "Deadlift",
            category = DbExerciseCategory.BACK,
            muscleGroups = listOf(DbMuscleGroup.UPPER_BACK, DbMuscleGroup.LOWER_BACK, DbMuscleGroup.HAMSTRINGS),
            equipment = DbEquipment.BARBELL,
            instructions = listOf("Stand with feet hip-width apart", "Lift the bar"),
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            isCustom = false
        )

        // When - Convert to domain and back to entity
        val domainModel = originalEntity.toDomainModel()
        val convertedEntity = domainModel.toEntity()

        // Then - Should be identical
        assertThat(convertedEntity.id).isEqualTo(originalEntity.id)
        assertThat(convertedEntity.name).isEqualTo(originalEntity.name)
        assertThat(convertedEntity.category).isEqualTo(originalEntity.category)
        assertThat(convertedEntity.muscleGroups).isEqualTo(originalEntity.muscleGroups)
        assertThat(convertedEntity.equipment).isEqualTo(originalEntity.equipment)
        assertThat(convertedEntity.instructions).isEqualTo(originalEntity.instructions)
        assertThat(convertedEntity.isCustom).isEqualTo(originalEntity.isCustom)
    }
}