package com.example.gym_tracker.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gym_tracker.feature.workout.WorkoutScreen
import com.example.gym_tracker.ui.dashboard.DashboardScreen

/**
 * Main navigation graph for the Gym Tracker app
 */
@Composable
fun GymTrackerNavigation(
    navController: NavHostController,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToWorkouts = {
                    navController.navigate(Screen.Workouts.route)
                },
                modifier = modifier
            )
        }
        
        composable(Screen.Workouts.route) {
            WorkoutScreen()
        }
        
        composable(Screen.Statistics.route) {
            StatisticsScreen()
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }
}

/**
 * Screen destinations for navigation
 */
sealed class Screen(val route: String, val title: String) {
    object Dashboard : Screen("dashboard", "Dashboard")
    object Workouts : Screen("workouts", "Workouts")
    object Statistics : Screen("statistics", "Statistics")
    object Profile : Screen("profile", "Profile")
}

// Placeholder screens - these will be implemented in future tasks
@Composable
private fun StatisticsScreen() {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text("Statistics - Coming Soon")
    }
}

@Composable
private fun ProfileScreen() {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text("Profile - Coming Soon")
    }
}