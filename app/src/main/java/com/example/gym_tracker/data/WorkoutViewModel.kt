package com.example.gym_tracker.data
import android.app.Application
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gym_tracker.data.AppDatabase
import com.example.gym_tracker.data.Exercise
import com.example.gym_tracker.data.ExerciseRepository
import com.example.gym_tracker.data.WorkoutViewModelFactory
import kotlinx.coroutines.launch


class WorkoutViewModel(application: Application, private val exerciseRepository: ExerciseRepository) : AndroidViewModel(application) {

    val allExercises: LiveData<List<Exercise>> = exerciseRepository.allExercises

    fun insert(exercise: Exercise) = viewModelScope.launch {
        try {
            exerciseRepository.insert(exercise)
        } catch (e: Exception) {
            // Handle the exception, e.g., log it or show an error message
            // Log.e("WorkoutViewModel", "Error inserting exercise", e)
        }
    }
    fun getExercisesByWorkoutName(workoutName: String): LiveData<List<Exercise>> {
        return exerciseRepository.getExercisesByWorkoutName(workoutName)
    }
    fun getExerciseCountByWorkoutName(workoutName: String): LiveData<Int> {
        return exerciseRepository.getExerciseCountByWorkoutName(workoutName)
    }

}