package com.example.gym_tracker.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gym_tracker.feature.workout.WorkoutScreen
import com.example.gym_tracker.feature.workout.ExerciseTrackingScreen
import com.example.gym_tracker.feature.exercise.ExerciseSelectionScreen
import com.example.gym_tracker.feature.profile.WeightTrackingScreen
import com.example.gym_tracker.feature.statistics.ComparativeAnalysisScreen
import com.example.gym_tracker.feature.statistics.StatisticsScreen
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
                    navController.navigate(Screen.Workouts.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToExerciseSelection = {
                    navController.navigate(Screen.ExerciseSelection.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
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
            WorkoutScreen(
                onNavigateToExerciseTracking = { workoutId ->
                    navController.navigate(Screen.WorkoutExercises.createRoute(workoutId))
                }
            )
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
            ExerciseSelectionScreen(
                onNavigateToExerciseStats = { exerciseId ->
                    navController.navigate(Screen.ExerciseStatistics.createRoute(exerciseId))
                }
            )
        }
        
        composable(
            route = Screen.ExerciseTracking.route,
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
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
            ExerciseTrackingScreenWrapper(
                workoutId = workoutId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
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
        
        composable(
            route = Screen.ExerciseStatistics.route,
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
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
            com.example.gym_tracker.feature.statistics.ExerciseStatisticsScreen(
                exerciseId = exerciseId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.WorkoutExercises.route,
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
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
            com.example.gym_tracker.feature.workout.WorkoutExercisesScreen(
                workoutId = workoutId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToExerciseStats = { exerciseId ->
                    navController.navigate(Screen.ExerciseStatistics.createRoute(exerciseId))
                }
            )
        }
        
        composable(
            route = Screen.Statistics.route,
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
            com.example.gym_tracker.feature.statistics.StatisticsScreen(
                onNavigateToComparativeAnalysis = {
                    navController.navigate(Screen.ComparativeAnalysis.route)
                }
            )
        }
        
        composable(
            route = Screen.ComparativeAnalysis.route,
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
            com.example.gym_tracker.feature.statistics.ComparativeAnalysisScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
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
    object ExerciseTracking : Screen("exercise_tracking/{workoutId}", "Exercise Tracking") {
        fun createRoute(workoutId: String) = "exercise_tracking/$workoutId"
    }
    object Profile : Screen("profile", "Profile")
    object ExerciseStatistics : Screen("exercise_statistics/{exerciseId}", "Exercise Statistics") {
        fun createRoute(exerciseId: String) = "exercise_statistics/$exerciseId"
    }
    object WorkoutExercises : Screen("workout_exercises/{workoutId}", "Workout Exercises") {
        fun createRoute(workoutId: String) = "workout_exercises/$workoutId"
    }
    object Statistics : Screen("statistics", "Statistics")
    object ComparativeAnalysis : Screen("comparative_analysis", "Comparative Analysis")
}

@Composable
private fun ExerciseTrackingScreenWrapper(
    workoutId: String,
    onNavigateBack: () -> Unit
) {
    // For now, show a placeholder screen that indicates we're in exercise tracking mode
    // This will be properly implemented when we have the data layer working
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            androidx.compose.material3.Text(
                text = "Exercise Tracking",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
            androidx.compose.material3.Text(
                text = "Workout ID: $workoutId",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
            androidx.compose.material3.Button(
                onClick = onNavigateBack
            ) {
                androidx.compose.material3.Text("Back to Workouts")
            }
        }
    }
}

// Statistics screen is now implemented in the feature module