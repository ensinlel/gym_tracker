package com.example.gym_tracker

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import com.example.gym_tracker.R
import com.example.gym_tracker.data.Exercise
import com.example.gym_tracker.data.WorkoutViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.asFlow
import com.example.gym_tracker.MainActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_tracker.data.ExerciseAdapter
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import androidx.activity.viewModels

import androidx.lifecycle.ViewModelProvider
import com.example.gym_tracker.data.AppDatabase
import com.example.gym_tracker.data.ExerciseRepository
import com.example.gym_tracker.data.WorkoutViewModelFactory
import java.time.LocalDate
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_tracker.data.EntryRepository
import com.example.gym_tracker.data.EntryViewModel
import com.example.gym_tracker.data.EntryViewModelFactory
import com.example.gym_tracker.data.ExerciseEntry
import com.example.gym_tracker.data.ExerciseSet
import com.example.gym_tracker.data.SetRepository
import com.example.gym_tracker.data.SetViewModel
import com.example.gym_tracker.data.SetViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.android.car.ui.toolbar.MenuItem as MenuItem1



class WorkoutActivity : MainActivity() {
    // WorkoutViewModel initialisieren (vorher sicherstellen, dass es existiert)
    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var recyclerView: RecyclerView
    private val workoutViewModel: WorkoutViewModel by viewModels {
        WorkoutViewModelFactory(
            application,
            ExerciseRepository(AppDatabase.getDatabase(application).exerciseDao())
        )
    }
    private val entryViewModel: EntryViewModel by viewModels {
        EntryViewModelFactory(
            application,
            EntryRepository(AppDatabase.getDatabase(application).entryDao())
        )
    }
    private val setViewModel: SetViewModel by viewModels {
        SetViewModelFactory(
            application,
            SetRepository(AppDatabase.getDatabase(application).setDao())
        )
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout1)

        // Initialisiere exerciseAdapter und weise es der RecyclerView zu
        exerciseAdapter = ExerciseAdapter(emptyList(), emptyList(), emptyList(), this, AppDatabase.getDatabase(application).setDao())
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = exerciseAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setze den Header-Text
        val headerTextView = findViewById<TextView>(R.id.headerTextView)
        val workoutName = intent.getStringExtra("workoutName") ?: getString(R.string.workout)
        headerTextView.text = workoutName

        // Beobachte die Übungen für das aktuelle Workout
        workoutViewModel.getExercisesByWorkoutName(workoutName).observe(this) { exercises ->
            exercises?.let {
                exerciseAdapter.updateExercises(it)
            } ?: run {
                exerciseAdapter.updateExercises(emptyList())
            }
        }

        workoutViewModel.getExercisesByWorkoutName(workoutName).observe(this) { exercises ->
            exercises?.let { exerciseList ->
                val exerciseIds = exerciseList.map { it.exerciseId }
                if (exerciseIds.isNotEmpty()) {
                    entryViewModel.getEntriesByExerciseID(exerciseIds).observe(this) { entries ->
                        setViewModel.allSets.observe(this) { sets ->
                            exerciseAdapter.updateEntriesAndSets(entries, sets)
                        }
                    }
                } else {
                    // Fallback-Logik oder eine leere Liste an `updateEntriesAndSets` übergeben
                    exerciseAdapter.updateEntriesAndSets(emptyList(), emptyList())
                }
            }
        }
        // Finde die BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)

        // Setze den Listener
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_button -> {
                    // Navigation zur Hauptaktivität
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.settings -> {
                    // Hier kannst du die Navigation zu den Einstellungen hinzufügen
                    // val intent = Intent(this, SettingsActivity::class.java)
                    // startActivity(intent)
                    true
                }
                else -> false
            }
        }
        exerciseAdapter.setOnItemClickListener { exercise ->
            val intent = Intent(this, StatisticsActivity::class.java).apply {
                putExtra("exerciseName", exercise.exerciseName)
                putExtra("exerciseId", exercise.exerciseId)
            }
            startActivity(intent)
        }


        // Setup für den FloatingActionButton
        val fabAddExercise = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_add_exercise)
        fabAddExercise.setOnClickListener {
            showAddExerciseDialog(workoutName)
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_navigation, menu) // Lade das Menü aus bottom_navigation.xml
        return true
    }



    private fun showAddExerciseDialog(workoutName: String) {
        // Dialog-Layout inflaten
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_exercise, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Neue Übung hinzufügen")

        val alertDialog = builder.show()

        // Felder und Button aus dem Dialog abrufen und verarbeiten
        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_name)
        val nameEditMuscleGroup = dialogView.findViewById<EditText>(R.id.edit_muscle_group)
        val addButton = dialogView.findViewById<Button>(R.id.btn_add)

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val muscleGroup = nameEditMuscleGroup.text.toString()

            if (name.isNotEmpty()) {
                // Neue Übung erstellen und über das ViewModel hinzufügen
                val newExercise = Exercise(
                    exerciseName = name,
                    workoutName = workoutName,
                    muscleGroup = muscleGroup,
                    isVisible = true // Setze Sichtbarkeit auf true, da Übung ausgewählt wurde
                )
                workoutViewModel.insert(newExercise)
                alertDialog.dismiss()
            } else {
                nameEditText.error = "Name darf nicht leer sein"
            }
        }
    }
}
