package com.example.gym_tracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.*

@Dao
interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ExerciseEntry): Long

    @Query("SELECT * FROM entries")
    fun getAllEntries(): LiveData<List<ExerciseEntry>>

    @Query("SELECT * FROM entries WHERE exerciseId IN (:exerciseIds) ORDER BY date DESC")
    fun getEntriesByExerciseIDs(exerciseIds: List<Long>): LiveData<List<ExerciseEntry>>
}
