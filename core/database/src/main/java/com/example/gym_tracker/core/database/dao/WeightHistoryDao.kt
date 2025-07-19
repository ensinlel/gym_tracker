package com.example.gym_tracker.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gym_tracker.core.database.entity.WeightHistoryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface WeightHistoryDao {
    
    @Query("SELECT * FROM weight_history WHERE userProfileId = :userProfileId ORDER BY recordedDate DESC")
    fun getWeightHistoryForUser(userProfileId: String): Flow<List<WeightHistoryEntity>>
    
    @Query("SELECT * FROM weight_history WHERE userProfileId = :userProfileId AND recordedDate >= :fromDate ORDER BY recordedDate DESC")
    fun getWeightHistoryFromDate(userProfileId: String, fromDate: LocalDate): Flow<List<WeightHistoryEntity>>
    
    @Query("SELECT * FROM weight_history WHERE userProfileId = :userProfileId ORDER BY recordedDate DESC LIMIT 1")
    suspend fun getLatestWeightEntry(userProfileId: String): WeightHistoryEntity?
    
    @Query("SELECT * FROM weight_history WHERE userProfileId = :userProfileId AND recordedDate <= :beforeDate ORDER BY recordedDate DESC LIMIT 1")
    suspend fun getWeightEntryBeforeDate(userProfileId: String, beforeDate: LocalDate): WeightHistoryEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightEntry(weightEntry: WeightHistoryEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightEntries(weightEntries: List<WeightHistoryEntity>)
    
    @Update
    suspend fun updateWeightEntry(weightEntry: WeightHistoryEntity)
    
    @Delete
    suspend fun deleteWeightEntry(weightEntry: WeightHistoryEntity)
    
    @Query("DELETE FROM weight_history WHERE userProfileId = :userProfileId")
    suspend fun deleteAllWeightEntriesForUser(userProfileId: String)
}