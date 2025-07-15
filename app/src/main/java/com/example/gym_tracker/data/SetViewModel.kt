package com.example.gym_tracker.data


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SetViewModel(application: Application, private val setRepository: SetRepository) : AndroidViewModel(application) {
    val allSets: LiveData<List<ExerciseSet>> = setRepository.allSets

    fun insert(set: ExerciseSet) = viewModelScope.launch {
        try {
            setRepository.insert(set)
        } catch (e: Exception) {
            // Handle the exception, e.g., log it or show an error message
            // Log.e("WorkoutViewModel", "Error inserting exercise", e)
        }
    }
    fun getSetByEntryId(entryId: List<Long>): LiveData<List<ExerciseSet>> {
        return setRepository.getSetByEntryId(entryId)
    }
    fun getWeightsByDate(exerciseId: Long): LiveData<List<DateWeight>> {
        return setRepository.getWeightsByDate(exerciseId)
    }


}