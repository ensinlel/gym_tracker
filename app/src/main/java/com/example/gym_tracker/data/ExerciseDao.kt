package com.example.gym_tracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.*

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(vararg exercises: Exercise): Unit

    @Query("SELECT * FROM exercises")
    fun getAllExercises(): LiveData<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE workoutName = :workoutName")
    fun getExercisesByWorkoutName(workoutName: String): LiveData<List<Exercise>>

    @Query("SELECT COUNT(*) FROM exercises WHERE workoutName = :workoutName")
    fun getExerciseCountByWorkoutName(workoutName: String): LiveData<Int>

    @Query("UPDATE exercises SET isVisible = :isVisible WHERE exerciseId = :exerciseId")
    suspend fun updateExerciseVisibility(exerciseId: Int, isVisible: Boolean)
}