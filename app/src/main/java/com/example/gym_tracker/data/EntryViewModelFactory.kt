package com.example.gym_tracker.data

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EntryViewModelFactory(
    private val application: Application,
    private val entryRepository: EntryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EntryViewModel(application, entryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}