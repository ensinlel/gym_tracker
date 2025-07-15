package com.example.gym_tracker.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow


class ExerciseRepository(private val exerciseDao: ExerciseDao) {

    val allExercises: LiveData<List<Exercise>> = exerciseDao.getAllExercises()


    suspend fun insert(exercise: Exercise) {
        exerciseDao.insertExercise(exercise)
    }
    fun getExercisesByWorkoutName(workoutName: String): LiveData<List<Exercise>> {
        return exerciseDao.getExercisesByWorkoutName(workoutName)
    }
    fun getExerciseCountByWorkoutName(workoutName: String): LiveData<Int> {
        return exerciseDao.getExerciseCountByWorkoutName(workoutName)
    }
}