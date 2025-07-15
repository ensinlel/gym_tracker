package com.example.gym_tracker.data

import androidx.lifecycle.LiveData

class EntryRepository(private val entryDao: EntryDao) {

    val allEntries: LiveData<List<ExerciseEntry>> = entryDao.getAllEntries()


    suspend fun insert(entry: ExerciseEntry) {
        entryDao.insert(entry)
    }
    fun getEntriesByExerciseId(exerciseId: List<Long>): LiveData<List<ExerciseEntry>> {
        return entryDao.getEntriesByExerciseIDs(exerciseId)
    }

}