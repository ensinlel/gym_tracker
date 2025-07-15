package com.example.gym_tracker

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_tracker.data.AppDatabase
import com.example.gym_tracker.data.EntryDao
import com.example.gym_tracker.data.EntryRepository
import com.example.gym_tracker.data.ExerciseEntry
import com.example.gym_tracker.data.ExerciseSet
import com.example.gym_tracker.data.EntryViewModel
import com.example.gym_tracker.data.EntryViewModelFactory
import com.example.gym_tracker.data.SetDao
import com.example.gym_tracker.data.SetViewModel
import com.example.gym_tracker.data.SetViewModelFactory
import com.example.gym_tracker.data.SetRepository
import com.example.gym_tracker.data.StatisticsAdapter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


class StatisticsActivity : MainActivity() {
    private lateinit var entryDao: EntryDao
    private lateinit var setDao: SetDao
    // entryViewModel initialisieren (vorher sicherstellen, dass es existiert)

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
    private lateinit var lineChart: LineChart
    lateinit var statisticsAdapter: StatisticsAdapter
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        // Initialisiere die DAOs
        val database = AppDatabase.getDatabase(application)
        entryDao = database.entryDao()
        setDao = database.setDao()

        // Initialisiere das LineChart und RecyclerView
        lineChart = findViewById(R.id.weight_chart)



        val exerciseId = intent.getLongExtra("exerciseId", -1) // Standardwert -1, falls nicht gesetzt
            val exerciseName = intent.getStringExtra("exerciseName")

        setupWeightProgressChart(exerciseId)

        val headerTextView = findViewById<TextView>(R.id.headerStatisticsTextView)
        headerTextView.text = exerciseName
        recyclerView = findViewById(R.id.recyclerViewStatistics)
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = false  // Neueste Daten erscheinen oben
            reverseLayout = false // Reihenfolge bleibt unverändert
        }
        statisticsAdapter = StatisticsAdapter(emptyList(), emptyList())
        recyclerView.adapter = statisticsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Daten aus den ViewModels beobachten und Adapter aktualisieren
        entryViewModel.getEntriesByExerciseID(listOf(exerciseId)).observe(this) { entries ->
            setViewModel.getSetByEntryId(entries.map { it.entryId }).observe(this) { sets ->
                statisticsAdapter.updateEntriesAndSets(
                    entries.sortedByDescending { it.date },  // Sortiere Einträge nach Datum
                    sets.sortedByDescending { it.entryId }  // Sortiere Sets nach ID
                )
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

        // Setup für den FloatingActionButton
        val fabAddEntry = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_add_entry)
        fabAddEntry.setOnClickListener {
            showAddEntryDialog(exerciseId.toLong())
        }
    }

    private fun setupWeightProgressChart(exerciseId: Long) {
        // Beobachte die Gewicht-Datum-Daten für die spezifische Übung
        setDao.getWeightsByDate(exerciseId).observe(this) { dateWeightList ->
            val chartEntries = mutableListOf<Entry>()

            // Erstelle `Entry`-Objekte für jeden Datensatz (Datum und Gewicht)
            dateWeightList.forEachIndexed { index, dateWeight ->
                // Konvertiere Datum zu einem numerischen Wert für die X-Achse
                val dateInMillis = SimpleDateFormat("dd. MMMM yyyy", Locale.GERMANY).parse(dateWeight.date)?.time?.toFloat() ?: 0f
                chartEntries.add(Entry(index.toFloat(), dateWeight.weight))
            }

            // Erstelle den LineDataSet für die LineChart
            val lineDataSet = LineDataSet(chartEntries, "Gewicht über Zeit")
            lineDataSet.apply {
                color = Color.BLUE
                lineWidth = 2f
                setDrawCircles(true)
                circleRadius = 5f
                setDrawValues(false)
            }

            // Erstelle LineData und setze sie in die Chart
            lineChart.data = LineData(lineDataSet)
            lineChart.invalidate() // Aktualisiere die Chart
        }
    }

    private fun showAddEntryDialog(exerciseId: Long) {
        // Dialog-Layout inflaten
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_entry, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Neue Übung hinzufügen")

        val alertDialog = builder.show()

        // Felder und Button aus dem Dialog abrufen und verarbeiten
        val nameEditAmountSets = dialogView.findViewById<EditText>(R.id.edit_amount_sets)
        val nameEditWeight = dialogView.findViewById<EditText>(R.id.edit_weight)
        val nameEditReps = dialogView.findViewById<EditText>(R.id.edit_reps)
        val addButton = dialogView.findViewById<Button>(R.id.btn_add)

        addButton.setOnClickListener {
            val amountSets = nameEditAmountSets.text.toString().toIntOrNull()
            val weight = nameEditWeight.text.toString().toFloatOrNull()
            val reps = nameEditReps.text.toString().toIntOrNull()

            if (amountSets != null && weight != null && reps != null) {
                lifecycleScope.launch {
                    val newEntryId = withContext(Dispatchers.IO) {
                        // Direkter DAO-Aufruf zum Einfügen und Abrufen der generierten ID
                        val newEntry = ExerciseEntry(
                            exerciseId = exerciseId,
                            date = getFormattedCurrentDate()
                        )
                        entryDao.insert(newEntry)  // Gibt die generierte ID zurück
                    }

                    // Neues ExerciseSet-Objekt erstellen, die richtige Entry-ID verwenden
                    val newSet = ExerciseSet(
                        entryId = newEntryId,
                        weight = weight,
                        reps = reps
                    )

                    // ExerciseSet speichern
                    withContext(Dispatchers.IO) {
                        setDao.insert(newSet)
                    }

                    // Dialog schließen
                    alertDialog.dismiss()
                }
            } else {
                nameEditAmountSets.error = "Bitte füllen Sie alle Felder korrekt aus"
            }
        }

    }

    private fun getFormattedCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern(
            "dd. MMMM yyyy",
            Locale.GERMAN
        )
        return currentDate.format(formatter)
    }

}