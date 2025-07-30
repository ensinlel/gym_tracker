package com.example.gym_tracker.core.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gym_tracker.core.database.dao.ExerciseDao
import com.example.gym_tracker.core.database.dao.ExerciseInstanceDao
import com.example.gym_tracker.core.database.dao.ExerciseSetDao
import com.example.gym_tracker.core.database.dao.RoutineScheduleDao
import com.example.gym_tracker.core.database.dao.TemplateExerciseDao
import com.example.gym_tracker.core.database.dao.UserProfileDao
import com.example.gym_tracker.core.database.dao.WeightHistoryDao
import com.example.gym_tracker.core.database.dao.WorkoutDao
import com.example.gym_tracker.core.database.dao.WorkoutRoutineDao
import com.example.gym_tracker.core.database.dao.WorkoutTemplateDao
import com.example.gym_tracker.core.database.entity.ExerciseEntity
import com.example.gym_tracker.core.database.entity.ExerciseInstanceEntity
import com.example.gym_tracker.core.database.entity.ExerciseSetEntity
import com.example.gym_tracker.core.database.entity.RoutineScheduleEntity
import com.example.gym_tracker.core.database.entity.TemplateExerciseEntity
import com.example.gym_tracker.core.database.entity.UserProfileEntity
import com.example.gym_tracker.core.database.entity.WeightHistoryEntity
import com.example.gym_tracker.core.database.entity.WorkoutEntity
import com.example.gym_tracker.core.database.entity.WorkoutRoutineEntity
import com.example.gym_tracker.core.database.entity.WorkoutTemplateEntity

@Database(
    entities = [
        WorkoutEntity::class,
        ExerciseEntity::class,
        ExerciseInstanceEntity::class,
        ExerciseSetEntity::class,
        UserProfileEntity::class,
        WeightHistoryEntity::class,
        WorkoutTemplateEntity::class,
        TemplateExerciseEntity::class,
        WorkoutRoutineEntity::class,
        RoutineScheduleEntity::class
    ],
    version = 6,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class GymTrackerDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseInstanceDao(): ExerciseInstanceDao
    abstract fun exerciseSetDao(): ExerciseSetDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun weightHistoryDao(): WeightHistoryDao
    abstract fun workoutTemplateDao(): WorkoutTemplateDao
    abstract fun templateExerciseDao(): TemplateExerciseDao
    abstract fun workoutRoutineDao(): WorkoutRoutineDao
    abstract fun routineScheduleDao(): RoutineScheduleDao
    
    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Migration from version 1 to 2 (if needed for existing data)
                database.execSQL("""
                    ALTER TABLE exercises ADD COLUMN equipment TEXT NOT NULL DEFAULT 'BODYWEIGHT'
                """.trimIndent())
                database.execSQL("""
                    ALTER TABLE exercises ADD COLUMN instructions TEXT NOT NULL DEFAULT '[]'
                """.trimIndent())
                database.execSQL("""
                    ALTER TABLE exercises ADD COLUMN createdAt INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}
                """.trimIndent())
                database.execSQL("""
                    ALTER TABLE exercises ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}
                """.trimIndent())
            }
        }
        
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create new tables for enhanced schema
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS exercise_instances (
                        id TEXT NOT NULL PRIMARY KEY,
                        workoutId TEXT NOT NULL,
                        exerciseId TEXT NOT NULL,
                        orderInWorkout INTEGER NOT NULL,
                        notes TEXT NOT NULL DEFAULT '',
                        FOREIGN KEY(workoutId) REFERENCES workouts(id) ON DELETE CASCADE,
                        FOREIGN KEY(exerciseId) REFERENCES exercises(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS exercise_sets (
                        id TEXT NOT NULL PRIMARY KEY,
                        exerciseInstanceId TEXT NOT NULL,
                        setNumber INTEGER NOT NULL,
                        weight REAL NOT NULL,
                        reps INTEGER NOT NULL,
                        restTime INTEGER NOT NULL DEFAULT 0,
                        rpe INTEGER,
                        tempo TEXT,
                        isWarmup INTEGER NOT NULL DEFAULT 0,
                        isFailure INTEGER NOT NULL DEFAULT 0,
                        notes TEXT NOT NULL DEFAULT '',
                        FOREIGN KEY(exerciseInstanceId) REFERENCES exercise_instances(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_profile (
                        id TEXT NOT NULL PRIMARY KEY,
                        age INTEGER NOT NULL,
                        weight REAL NOT NULL,
                        height REAL NOT NULL,
                        fitnessLevel TEXT NOT NULL,
                        goals TEXT NOT NULL,
                        limitations TEXT NOT NULL DEFAULT '[]',
                        preferredEquipment TEXT NOT NULL DEFAULT '[]',
                        trainingFrequency INTEGER NOT NULL DEFAULT 3,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """.trimIndent())
                
                // Update workouts table with new columns
                database.execSQL("""
                    ALTER TABLE workouts ADD COLUMN templateId TEXT
                """.trimIndent())
                database.execSQL("""
                    ALTER TABLE workouts ADD COLUMN rating INTEGER
                """.trimIndent())
                database.execSQL("""
                    ALTER TABLE workouts ADD COLUMN totalVolume REAL NOT NULL DEFAULT 0.0
                """.trimIndent())
                database.execSQL("""
                    ALTER TABLE workouts ADD COLUMN averageRestTime INTEGER NOT NULL DEFAULT 0
                """.trimIndent())
                
                // Update exercises table structure (recreate with new schema)
                database.execSQL("""
                    CREATE TABLE exercises_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        category TEXT NOT NULL,
                        muscleGroups TEXT NOT NULL,
                        equipment TEXT NOT NULL,
                        instructions TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        isCustom INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
                
                // Copy existing data with transformations
                database.execSQL("""
                    INSERT INTO exercises_new (id, name, category, muscleGroups, equipment, instructions, createdAt, updatedAt, isCustom)
                    SELECT 
                        id, 
                        name, 
                        CASE 
                            WHEN category = 'chest' THEN 'CHEST'
                            WHEN category = 'back' THEN 'BACK'
                            WHEN category = 'shoulders' THEN 'SHOULDERS'
                            WHEN category = 'arms' THEN 'ARMS'
                            WHEN category = 'legs' THEN 'LEGS'
                            WHEN category = 'core' THEN 'CORE'
                            ELSE 'FULL_BODY'
                        END,
                        COALESCE(muscleGroups, '["CHEST"]'),
                        COALESCE(equipment, 'BODYWEIGHT'),
                        COALESCE(instructions, '[]'),
                        COALESCE(createdAt, ${System.currentTimeMillis()}),
                        COALESCE(updatedAt, ${System.currentTimeMillis()}),
                        isCustom
                    FROM exercises
                """.trimIndent())
                
                // Drop old table and rename new one
                database.execSQL("DROP TABLE exercises")
                database.execSQL("ALTER TABLE exercises_new RENAME TO exercises")
            }
        }
        
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add isStarMarked column to exercises table
                database.execSQL("""
                    ALTER TABLE exercises ADD COLUMN isStarMarked INTEGER NOT NULL DEFAULT 0
                """.trimIndent())
                
                // Create weight_history table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS weight_history (
                        id TEXT NOT NULL PRIMARY KEY,
                        userProfileId TEXT NOT NULL,
                        weight REAL NOT NULL,
                        recordedDate INTEGER NOT NULL,
                        notes TEXT NOT NULL DEFAULT '',
                        FOREIGN KEY(userProfileId) REFERENCES user_profile(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                
                // Create index for better query performance
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_weight_history_userProfileId_recordedDate 
                    ON weight_history(userProfileId, recordedDate)
                """.trimIndent())
            }
        }
        
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create workout_templates table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS workout_templates (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        category TEXT NOT NULL,
                        difficulty TEXT NOT NULL,
                        estimatedDuration INTEGER NOT NULL,
                        targetMuscleGroups TEXT NOT NULL,
                        requiredEquipment TEXT NOT NULL,
                        isPublic INTEGER NOT NULL,
                        createdBy TEXT,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL,
                        usageCount INTEGER NOT NULL,
                        rating REAL NOT NULL,
                        tags TEXT NOT NULL
                    )
                """.trimIndent())
                
                // Create template_exercises table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS template_exercises (
                        id TEXT NOT NULL PRIMARY KEY,
                        templateId TEXT NOT NULL,
                        exerciseId TEXT NOT NULL,
                        orderInTemplate INTEGER NOT NULL,
                        targetSets INTEGER NOT NULL,
                        targetRepsMin INTEGER,
                        targetRepsMax INTEGER,
                        targetWeight REAL,
                        restTime INTEGER NOT NULL,
                        notes TEXT NOT NULL,
                        isSuperset INTEGER NOT NULL,
                        supersetGroup INTEGER,
                        FOREIGN KEY(templateId) REFERENCES workout_templates(id) ON DELETE CASCADE,
                        FOREIGN KEY(exerciseId) REFERENCES exercises(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                
                // Create workout_routines table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS workout_routines (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        isActive INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                """.trimIndent())
                
                // Create routine_schedules table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS routine_schedules (
                        id TEXT NOT NULL PRIMARY KEY,
                        routineId TEXT NOT NULL,
                        templateId TEXT NOT NULL,
                        dayOfWeek INTEGER NOT NULL,
                        timeOfDay TEXT,
                        isActive INTEGER NOT NULL,
                        notes TEXT NOT NULL,
                        FOREIGN KEY(routineId) REFERENCES workout_routines(id) ON DELETE CASCADE,
                        FOREIGN KEY(templateId) REFERENCES workout_templates(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                
                // Create indices for better performance
                database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_templates_name ON workout_templates(name)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_templates_category ON workout_templates(category)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_templates_createdBy ON workout_templates(createdBy)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_templates_isPublic ON workout_templates(isPublic)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_templates_createdAt ON workout_templates(createdAt)")
                
                database.execSQL("CREATE INDEX IF NOT EXISTS index_template_exercises_templateId ON template_exercises(templateId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_template_exercises_exerciseId ON template_exercises(exerciseId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_template_exercises_orderInTemplate ON template_exercises(orderInTemplate)")
                
                database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_routines_isActive ON workout_routines(isActive)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_workout_routines_createdAt ON workout_routines(createdAt)")
                
                database.execSQL("CREATE INDEX IF NOT EXISTS index_routine_schedules_routineId ON routine_schedules(routineId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_routine_schedules_templateId ON routine_schedules(templateId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_routine_schedules_dayOfWeek ON routine_schedules(dayOfWeek)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_routine_schedules_isActive ON routine_schedules(isActive)")
            }
        }
        
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Migration for workout exercise persistence enhancements
                // Add any missing indices for better performance
                database.execSQL("CREATE INDEX IF NOT EXISTS index_exercise_instances_workoutId ON exercise_instances(workoutId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_exercise_instances_exerciseId ON exercise_instances(exerciseId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_exercise_instances_orderInWorkout ON exercise_instances(orderInWorkout)")
                
                database.execSQL("CREATE INDEX IF NOT EXISTS index_exercise_sets_exerciseInstanceId ON exercise_sets(exerciseInstanceId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_exercise_sets_setNumber ON exercise_sets(exerciseInstanceId, setNumber)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_exercise_sets_isWarmup ON exercise_sets(exerciseInstanceId, isWarmup)")
                
                // Ensure all tables have proper constraints and indices for the persistence system
                // This migration ensures compatibility with the new persistence implementation
            }
        }
    }
}