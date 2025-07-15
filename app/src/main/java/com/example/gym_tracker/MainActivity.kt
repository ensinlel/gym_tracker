package com.example.gym_tracker


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_tracker.data.AppDatabase
import com.example.gym_tracker.data.ExerciseRepository
import com.example.gym_tracker.data.WorkoutViewModel
import com.example.gym_tracker.data.WorkoutViewModelFactory
//import androidx.compose.ui.text.intl.Locale
import com.google.android.material.button.MaterialButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.text.format
import java.util.Locale


open class MainActivity : AppCompatActivity() {
    private val workoutViewModel: WorkoutViewModel by viewModels {
        WorkoutViewModelFactory(
            application,
            ExerciseRepository(AppDatabase.getDatabase(application).exerciseDao())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dateTextView = findViewById<TextView>(R.id.tv_date)
        val currentDate = getFormattedCurrentDate()
        dateTextView.text = currentDate

        val pushExerciseCountTextView = findViewById<TextView>(R.id.PushExerciseCount)
        val pullExerciseCountTextView = findViewById<TextView>(R.id.PullExerciseCount)
        val legsExerciseCountTextView = findViewById<TextView>(R.id.LegsExerciseCount)

        // LiveData beobachten
        workoutViewModel.getExerciseCountByWorkoutName("Push Workout").observe(this) { count ->
            pushExerciseCountTextView.text = count.toString() + " Exercises"
        }
        workoutViewModel.getExerciseCountByWorkoutName("Pull Workout").observe(this) { count ->
            pullExerciseCountTextView.text = count.toString() + " Exercises"
        }
        workoutViewModel.getExerciseCountByWorkoutName("Leg Workout").observe(this) { count ->
            legsExerciseCountTextView.text = count.toString() + " Exercises"
        }


        val button_Workout_1 = findViewById<MaterialButton>(R.id.button_Workout_1)
        val button_Workout_2 = findViewById<MaterialButton>(R.id.button_Workout_2)
        val button_Workout_3 = findViewById<MaterialButton>(R.id.button_Workout_3)

        button_Workout_1.setOnClickListener {
            // Handle button 1 click
            var workoutName = "Push Workout"
            val intent = Intent(this, WorkoutActivity::class.java)
            intent.putExtra("workoutName", workoutName)
            startActivity(intent)
        }

        button_Workout_2.setOnClickListener {
            // Handle button 2 click
            var workoutName = "Pull Workout"
            val intent = Intent(this, WorkoutActivity::class.java)
            intent.putExtra("workoutName", workoutName)
            startActivity(intent)
        }

        button_Workout_3.setOnClickListener {
            // Handle button 3 click
            var workoutName = "Leg Workout"
            val intent = Intent(this, WorkoutActivity::class.java)
            intent.putExtra("workoutName", workoutName)
            startActivity(intent)
        }

    }
}

fun getFormattedCurrentDate(): String {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern(
        "dd. MMMM yyyy",
        Locale.GERMAN
    )
    return currentDate.format(formatter)
}



