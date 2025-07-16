package com.example.gym_tracker.core.database.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant

class UserProfileEntityTest {

    @Test
    fun userProfileEntity_createsWithCorrectDefaults() {
        // Given
        val now = Instant.now()
        val userProfile = UserProfileEntity(
            age = 25,
            weight = 70.0,
            height = 175.0,
            fitnessLevel = FitnessLevel.BEGINNER,
            goals = listOf(FitnessGoal.GENERAL_FITNESS),
            createdAt = now,
            updatedAt = now
        )

        // Then
        assertThat(userProfile.age).isEqualTo(25)
        assertThat(userProfile.weight).isEqualTo(70.0)
        assertThat(userProfile.height).isEqualTo(175.0)
        assertThat(userProfile.fitnessLevel).isEqualTo(FitnessLevel.BEGINNER)
        assertThat(userProfile.goals).containsExactly(FitnessGoal.GENERAL_FITNESS)
        assertThat(userProfile.limitations).isEmpty()
        assertThat(userProfile.preferredEquipment).isEmpty()
        assertThat(userProfile.trainingFrequency).isEqualTo(3)
        assertThat(userProfile.id).isNotEmpty()
    }

    @Test
    fun userProfileEntity_withComplexData() {
        // Given
        val now = Instant.now()
        val userProfile = UserProfileEntity(
            age = 30,
            weight = 80.5,
            height = 180.0,
            fitnessLevel = FitnessLevel.ADVANCED,
            goals = listOf(FitnessGoal.STRENGTH, FitnessGoal.MUSCLE_BUILDING),
            limitations = listOf("Lower back injury", "Shoulder impingement"),
            preferredEquipment = listOf(Equipment.BARBELL, Equipment.DUMBBELL),
            trainingFrequency = 5,
            createdAt = now,
            updatedAt = now
        )

        // Then
        assertThat(userProfile.age).isEqualTo(30)
        assertThat(userProfile.weight).isEqualTo(80.5)
        assertThat(userProfile.height).isEqualTo(180.0)
        assertThat(userProfile.fitnessLevel).isEqualTo(FitnessLevel.ADVANCED)
        assertThat(userProfile.goals).containsExactly(FitnessGoal.STRENGTH, FitnessGoal.MUSCLE_BUILDING)
        assertThat(userProfile.limitations).containsExactly("Lower back injury", "Shoulder impingement")
        assertThat(userProfile.preferredEquipment).containsExactly(Equipment.BARBELL, Equipment.DUMBBELL)
        assertThat(userProfile.trainingFrequency).isEqualTo(5)
    }

    @Test
    fun fitnessLevel_hasAllExpectedValues() {
        val levels = FitnessLevel.values()
        
        assertThat(levels).asList().containsExactly(
            FitnessLevel.BEGINNER,
            FitnessLevel.INTERMEDIATE,
            FitnessLevel.ADVANCED,
            FitnessLevel.EXPERT
        )
    }

    @Test
    fun fitnessGoal_hasAllExpectedValues() {
        val goals = FitnessGoal.values()
        
        assertThat(goals).asList().containsExactly(
            FitnessGoal.STRENGTH,
            FitnessGoal.MUSCLE_BUILDING,
            FitnessGoal.WEIGHT_LOSS,
            FitnessGoal.ENDURANCE,
            FitnessGoal.GENERAL_FITNESS,
            FitnessGoal.POWERLIFTING,
            FitnessGoal.BODYBUILDING
        )
    }
}