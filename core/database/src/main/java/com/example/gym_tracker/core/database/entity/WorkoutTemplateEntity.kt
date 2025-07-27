package com.example.gym_tracker.core.database.entity

import androidx.room.*
import com.example.gym_tracker.core.common.enums.Equipment
import com.example.gym_tracker.core.common.enums.MuscleGroup
import java.time.Duration
import java.time.Instant

/**
 * Room entity for workout templates
 */
@Entity(
    tableName = "workout_templates",
    indices = [
        Index(value = ["name"]),
        Index(value = ["category"]),
        Index(value = ["createdBy"]),
        Index(value = ["isPublic"]),
        Index(value = ["createdAt"])
    ]
)
data class WorkoutTemplateEntity(
    @PrimaryKey
    val id: String,
    
    val name: String,
    val description: String,
    val category: String, // WorkoutCategory enum as string
    val difficulty: String, // DifficultyLevel enum as string
    val estimatedDuration: Long, // Duration in milliseconds
    val targetMuscleGroups: List<MuscleGroup>,
    val requiredEquipment: List<Equipment>,
    val isPublic: Boolean,
    val createdBy: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val usageCount: Int,
    val rating: Double,
    val tags: List<String>
)

/**
 * Room entity for template exercises
 */
@Entity(
    tableName = "template_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutTemplateEntity::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["templateId"]),
        Index(value = ["exerciseId"]),
        Index(value = ["orderInTemplate"])
    ]
)
data class TemplateExerciseEntity(
    @PrimaryKey
    val id: String,
    
    val templateId: String,
    val exerciseId: String,
    val orderInTemplate: Int,
    val targetSets: Int,
    val targetRepsMin: Int?, // Minimum reps in range
    val targetRepsMax: Int?, // Maximum reps in range
    val targetWeight: Double?,
    val restTime: Long, // Duration in milliseconds
    val notes: String,
    val isSuperset: Boolean,
    val supersetGroup: Int?
)

/**
 * Room entity for workout routines
 */
@Entity(
    tableName = "workout_routines",
    indices = [
        Index(value = ["name"]),
        Index(value = ["isActive"]),
        Index(value = ["createdAt"])
    ]
)
data class WorkoutRoutineEntity(
    @PrimaryKey
    val id: String,
    
    val name: String,
    val description: String,
    val isActive: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * Room entity for routine schedules
 */
@Entity(
    tableName = "routine_schedules",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutRoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WorkoutTemplateEntity::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["routineId"]),
        Index(value = ["templateId"]),
        Index(value = ["dayOfWeek"]),
        Index(value = ["isActive"])
    ]
)
data class RoutineScheduleEntity(
    @PrimaryKey
    val id: String,
    
    val routineId: String,
    val templateId: String,
    val dayOfWeek: Int, // 1=Monday, 7=Sunday
    val timeOfDay: String?, // HH:mm format
    val isActive: Boolean,
    val notes: String
)

/**
 * Relation class for template with exercises
 */
data class WorkoutTemplateWithExercisesEntity(
    @Embedded val template: WorkoutTemplateEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "templateId",
        entity = TemplateExerciseEntity::class
    )
    val exercises: List<TemplateExerciseWithDetailsEntity>
)

/**
 * Relation class for template exercise with exercise details
 */
data class TemplateExerciseWithDetailsEntity(
    @Embedded val templateExercise: TemplateExerciseEntity,
    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "id"
    )
    val exercise: ExerciseEntity
)

/**
 * Relation class for routine with schedules
 */
data class WorkoutRoutineWithDetailsEntity(
    @Embedded val routine: WorkoutRoutineEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "routineId",
        entity = RoutineScheduleEntity::class
    )
    val schedules: List<RoutineScheduleWithTemplateEntity>
)

/**
 * Relation class for routine schedule with template
 */
data class RoutineScheduleWithTemplateEntity(
    @Embedded val schedule: RoutineScheduleEntity,
    @Relation(
        parentColumn = "templateId",
        entityColumn = "id"
    )
    val template: WorkoutTemplateEntity
)