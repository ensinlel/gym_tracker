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
import com.example.gym_tracker.core.database.dao.UserProfileDao
import com.example.gym_tracker.core.database.dao.WorkoutDao
import com.example.gym_tracker.core.database.entity.ExerciseEntity
import com.example.gym_tracker.core.database.entity.ExerciseInstanceEntity
import com.example.gym_tracker.core.database.entity.ExerciseSetEntity
import com.example.gym_tracker.core.database.entity.UserProfileEntity
import com.example.gym_tracker.core.database.entity.WorkoutEntity

@Database(
    entities = [
        WorkoutEntity::class,
        ExerciseEntity::class,
        ExerciseInstanceEntity::class,
        ExerciseSetEntity::class,
        UserProfileEntity::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class GymTrackerDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseInstanceDao(): ExerciseInstanceDao
    abstract fun exerciseSetDao(): ExerciseSetDao
    abstract fun userProfileDao(): UserProfileDao
    
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
    }
}