package com.example.gym_tracker.data

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SetViewModelFactory(
    private val application: Application,
    private val setRepository: SetRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SetViewModel(application, setRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
