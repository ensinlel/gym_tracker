package com.example.gym_tracker.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EntryViewModel(application: Application, private val entryRepository: EntryRepository) : AndroidViewModel(application) {
    val allEntries: LiveData<List<ExerciseEntry>> = entryRepository.allEntries

    fun insert(entry: ExerciseEntry) = viewModelScope.launch {
        try {
            entryRepository.insert(entry)
        } catch (e: Exception) {
            // Handle the exception, e.g., log it or show an error message
            // Log.e("WorkoutViewModel", "Error inserting exercise", e)
        }
    }
    fun getEntriesByExerciseID(exerciseId: List<Long>): LiveData<List<ExerciseEntry>> {
        return entryRepository.getEntriesByExerciseId(exerciseId)
    }


}