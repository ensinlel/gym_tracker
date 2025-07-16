package com.example.gym_tracker.core.database.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant

class ExerciseEntityTest {

    @Test
    fun exerciseEntity_createsWithCorrectDefaults() {
        // Given
        val now = Instant.now()
        val exercise = ExerciseEntity(
            name = "Bench Press",
            category = ExerciseCategory.CHEST,
            muscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
            equipment = Equipment.BARBELL,
            instructions = listOf("Lie on bench", "Press weight up"),
            createdAt = now,
            updatedAt = now
        )

        // Then
        assertThat(exercise.name).isEqualTo("Bench Press")
        assertThat(exercise.category).isEqualTo(ExerciseCategory.CHEST)
        assertThat(exercise.muscleGroups).containsExactly(MuscleGroup.CHEST, MuscleGroup.TRICEPS)
        assertThat(exercise.equipment).isEqualTo(Equipment.BARBELL)
        assertThat(exercise.instructions).containsExactly("Lie on bench", "Press weight up")
        assertThat(exercise.isCustom).isFalse()
        assertThat(exercise.id).isNotEmpty()
    }

    @Test
    fun exerciseEntity_customExercise() {
        // Given
        val exercise = ExerciseEntity(
            name = "My Custom Exercise",
            category = ExerciseCategory.FULL_BODY,
            muscleGroups = listOf(MuscleGroup.CHEST),
            equipment = Equipment.BODYWEIGHT,
            instructions = listOf("Custom instruction"),
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            isCustom = true
        )

        // Then
        assertThat(exercise.isCustom).isTrue()
        assertThat(exercise.name).isEqualTo("My Custom Exercise")
    }

    @Test
    fun exerciseCategory_hasAllExpectedValues() {
        val categories = ExerciseCategory.values()
        
        assertThat(categories).asList().containsExactly(
            ExerciseCategory.CHEST,
            ExerciseCategory.BACK,
            ExerciseCategory.SHOULDERS,
            ExerciseCategory.ARMS,
            ExerciseCategory.LEGS,
            ExerciseCategory.CORE,
            ExerciseCategory.CARDIO,
            ExerciseCategory.FULL_BODY
        )
    }

    @Test
    fun muscleGroup_hasAllExpectedValues() {
        val muscleGroups = MuscleGroup.values()
        
        assertThat(muscleGroups).asList().containsAtLeast(
            MuscleGroup.CHEST,
            MuscleGroup.UPPER_BACK,
            MuscleGroup.LOWER_BACK,
            MuscleGroup.FRONT_DELTS,
            MuscleGroup.SIDE_DELTS,
            MuscleGroup.REAR_DELTS,
            MuscleGroup.BICEPS,
            MuscleGroup.TRICEPS,
            MuscleGroup.QUADRICEPS,
            MuscleGroup.HAMSTRINGS,
            MuscleGroup.GLUTES,
            MuscleGroup.CALVES,
            MuscleGroup.ABS
        )
    }

    @Test
    fun equipment_hasAllExpectedValues() {
        val equipment = Equipment.values()
        
        assertThat(equipment).asList().containsExactly(
            Equipment.BARBELL,
            Equipment.DUMBBELL,
            Equipment.CABLE,
            Equipment.MACHINE,
            Equipment.BODYWEIGHT,
            Equipment.RESISTANCE_BAND,
            Equipment.KETTLEBELL,
            Equipment.OTHER
        )
    }
}