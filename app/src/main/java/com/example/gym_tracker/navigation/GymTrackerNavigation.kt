package com.example.gym_tracker.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gym_tracker.feature.workout.WorkoutScreen
import com.example.gym_tracker.feature.exercise.ExerciseSelectionScreen
import com.example.gym_tracker.feature.profile.WeightTrackingScreen
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
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(
            route = Screen.Dashboard.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            DashboardScreen(
                onNavigateToWorkouts = {
                    navController.navigate(Screen.Workouts.route)
                },
                onNavigateToExerciseSelection = {
                    navController.navigate(Screen.ExerciseSelection.route)
                }
            )
        }
        
        composable(
            route = Screen.Workouts.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            WorkoutScreen()
        }
        
        composable(
            route = Screen.ExerciseSelection.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            ExerciseSelectionScreen()
        }
        
        composable(
            route = Screen.Profile.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            WeightTrackingScreen()
        }
    }
}

/**
 * Screen destinations for navigation
 */
sealed class Screen(val route: String, val title: String) {
    object Dashboard : Screen("dashboard", "Dashboard")
    object Workouts : Screen("workouts", "Workouts")
    object ExerciseSelection : Screen("exercise_selection", "Exercises")
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