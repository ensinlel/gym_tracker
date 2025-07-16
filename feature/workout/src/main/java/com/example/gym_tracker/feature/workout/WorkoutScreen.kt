package com.example.gym_tracker.feature.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gym_tracker.core.ui.components.GymTrackerFAB
import com.example.gym_tracker.core.ui.components.WorkoutCard
import com.example.gym_tracker.core.ui.theme.GymTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Workouts",
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
            GymTrackerFAB(
                onClick = { /* Create new workout */ }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Workout"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(getSampleWorkouts()) { workout ->
                WorkoutCard(
                    title = workout.name,
                    subtitle = workout.description,
                    exerciseCount = workout.exerciseCount,
                    duration = workout.duration,
                    onClick = { /* Navigate to workout details */ }
                )
            }
            
            // Add some bottom padding for the FAB
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// Sample workout data - this will be replaced with real data from repositories
private data class SampleWorkout(
    val name: String,
    val description: String,
    val exerciseCount: Int,
    val duration: String
)

private fun getSampleWorkouts(): List<SampleWorkout> {
    return listOf(
        SampleWorkout(
            name = "Push Day",
            description = "Chest / Shoulders / Triceps",
            exerciseCount = 8,
            duration = "60 min"
        ),
        SampleWorkout(
            name = "Pull Day", 
            description = "Back / Biceps / Rear Delts",
            exerciseCount = 7,
            duration = "55 min"
        ),
        SampleWorkout(
            name = "Leg Day",
            description = "Quads / Hamstrings / Glutes / Calves",
            exerciseCount = 6,
            duration = "70 min"
        ),
        SampleWorkout(
            name = "Upper Body",
            description = "Chest / Back / Arms / Shoulders",
            exerciseCount = 10,
            duration = "75 min"
        ),
        SampleWorkout(
            name = "Full Body",
            description = "All major muscle groups",
            exerciseCount = 12,
            duration = "90 min"
        ),
        SampleWorkout(
            name = "Arms Focus",
            description = "Biceps / Triceps / Forearms",
            exerciseCount = 6,
            duration = "45 min"
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun WorkoutScreenPreview() {
    GymTrackerTheme(darkTheme = true) {
        WorkoutScreen()
    }
}