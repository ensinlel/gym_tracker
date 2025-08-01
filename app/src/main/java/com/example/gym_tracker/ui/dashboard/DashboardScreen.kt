package com.example.gym_tracker.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gym_tracker.core.ui.components.*
import com.example.gym_tracker.core.ui.theme.GymTrackerTheme
import com.example.gym_tracker.feature.statistics.DashboardAnalyticsContainer
import java.time.LocalDate

/**
 * Dashboard screen - the main homepage of the app
 * Features calendar, stats, and recent workouts matching your design vision
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToWorkouts: () -> Unit = {},
    onNavigateToExerciseSelection: () -> Unit = {},
    onNavigateToGoals: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToWorkouts,
                icon = { 
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    ) 
                },
                text = { Text("Log Workout") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Analytics Cards - replacing old placeholder stats
            item {
                DashboardAnalyticsContainer(
                    onAddWeightClick = { 
                        // Navigate to weight tracking screen
                        // For now, this will be handled by the WeightProgressCard itself
                    },
                    onViewWeightHistoryClick = { 
                        // Navigate to weight tracking screen
                        // For now, this will be handled by the WeightProgressCard itself
                    },
                    onExerciseStarClick = { exerciseId, isStarred -> 
                        // Handle exercise star marking
                        // This is handled by the analytics system
                    },
                    onNavigateToExerciseSelection = onNavigateToExerciseSelection,
                    onNavigateToGoals = onNavigateToGoals
                )
            }
            
            // Recent Workouts Section
            item {
                Text(
                    text = "Recent Workouts",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            // Recent Workout Cards
            items(getSampleWorkouts()) { workout ->
                WorkoutCard(
                    title = workout.name,
                    subtitle = workout.exercises,
                    exerciseCount = workout.exerciseCount,
                    duration = workout.duration,
                    onClick = onNavigateToWorkouts
                )
            }
            
            // Add some bottom padding for the FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// Sample data - this will be replaced with real data from repositories
private fun getSampleWorkoutDates(): Set<LocalDate> {
    val today = LocalDate.now()
    return setOf(
        today.minusDays(1),
        today.minusDays(3),
        today.minusDays(5),
        today.minusDays(7)
    )
}

private data class SampleWorkout(
    val name: String,
    val exercises: String,
    val exerciseCount: Int,
    val duration: String
)

private fun getSampleWorkouts(): List<SampleWorkout> {
    return listOf(
        SampleWorkout(
            name = "Push Day",
            exercises = "Chest / Shoulders / Triceps",
            exerciseCount = 8,
            duration = "65 min"
        ),
        SampleWorkout(
            name = "Pull Day",
            exercises = "Back / Biceps / Rear Delts",
            exerciseCount = 7,
            duration = "55 min"
        ),
        SampleWorkout(
            name = "Leg Day",
            exercises = "Quads / Hamstrings / Glutes / Calves",
            exerciseCount = 6,
            duration = "70 min"
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    GymTrackerTheme(darkTheme = true) {
        DashboardScreen()
    }
}