package com.example.gym_tracker.data

import androidx.lifecycle.LiveData

class SetRepository(private val setDao: SetDao) {
    val allSets: LiveData<List<ExerciseSet>> = setDao.getAllSets()


    suspend fun insert(set: ExerciseSet) {
        setDao.insert(set)
    }
    fun getSetByMaxWeight(entryId: Long): LiveData<Float> {
        return setDao.getMaxWeightForEntry(entryId)
    }
    fun getSetByEntryId(entryId: List<Long>): LiveData<List<ExerciseSet>> {
        return setDao.getSetByEntryId(entryId)
    }

    fun getWeightsByDate(exerciseId: Long): LiveData<List<DateWeight>> {
        return setDao.getWeightsByDate(exerciseId)
    }

}