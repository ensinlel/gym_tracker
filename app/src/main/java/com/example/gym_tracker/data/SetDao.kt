package com.example.gym_tracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(set: ExerciseSet)

    @Query("SELECT * FROM sets")
    fun getAllSets(): LiveData<List<ExerciseSet>>

    @Query("SELECT MAX(weight) FROM sets WHERE entryId = :entryId")
    fun getMaxWeightForEntry(entryId: Long): LiveData<Float>

    @Query("SELECT * FROM sets WHERE entryId IN (:entryId)")
    fun getSetByEntryId(entryId: List<Long>): LiveData<List<ExerciseSet>>

    @Query("SELECT MAX(weight) FROM sets WHERE entryId IN (:entryIds)")
    fun getMaxWeightForEntries(entryIds: List<Long>): LiveData<Float>

    @Query("""SELECT e.date, s.weight FROM entries AS e JOIN sets AS s ON e.entryId = s.entryId WHERE e.exerciseId = :exerciseId ORDER BY e.date ASC""")
    fun getWeightsByDate(exerciseId: Long): LiveData<List<DateWeight>>


}