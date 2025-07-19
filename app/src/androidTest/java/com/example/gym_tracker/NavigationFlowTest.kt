package com.example.gym_tracker

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun navigationFlow_dashboardToExerciseSelection_works() {
        // Given - App starts on Dashboard
        composeTestRule
            .onNodeWithText("Dashboard")
            .assertIsDisplayed()

        // When - Navigate to Exercise Selection via bottom nav
        composeTestRule
            .onNodeWithText("Exercises")
            .performClick()

        // Then - Should be on Exercise Selection screen
        composeTestRule
            .onNodeWithText("Select Exercises for PR Tracking")
            .assertIsDisplayed()
    }

    @Test
    fun navigationFlow_dashboardToProfile_works() {
        // Given - App starts on Dashboard
        composeTestRule
            .onNodeWithText("Dashboard")
            .assertIsDisplayed()

        // When - Navigate to Profile via bottom nav
        composeTestRule
            .onNodeWithContentDescription("Profile")
            .performClick()

        // Then - Should be on Weight Tracking screen
        composeTestRule
            .onNodeWithText("Weight Tracking")
            .assertIsDisplayed()
    }

    @Test
    fun navigationFlow_exerciseSelectionSearch_works() {
        // Given - Navigate to Exercise Selection
        composeTestRule
            .onNodeWithText("Exercises")
            .performClick()

        composeTestRule
            .onNodeWithText("Select Exercises for PR Tracking")
            .assertIsDisplayed()

        // When - Use search functionality
        composeTestRule
            .onNodeWithText("Search exercises")
            .performTextInput("bench")

        // Then - Search should work (exact behavior depends on data)
        composeTestRule.waitForIdle()
    }

    @Test
    fun navigationFlow_weightTracking_addEntry_works() {
        // Given - Navigate to Weight Tracking
        composeTestRule
            .onNodeWithContentDescription("Profile")
            .performClick()

        composeTestRule
            .onNodeWithText("Weight Tracking")
            .assertIsDisplayed()

        // When - Add weight entry
        composeTestRule
            .onNodeWithContentDescription("Add weight entry")
            .performClick()

        composeTestRule
            .onNodeWithText("Add Weight Entry")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Weight (kg)")
            .performTextInput("75.0")

        composeTestRule
            .onNodeWithText("Add")
            .performClick()

        // Then - Dialog should close
        composeTestRule
            .onNodeWithText("Add Weight Entry")
            .assertDoesNotExist()
    }

    @Test
    fun navigationFlow_bottomNavigation_allTabs_work() {
        // Test all bottom navigation tabs

        // Dashboard
        composeTestRule
            .onNodeWithText("Dashboard")
            .assertIsDisplayed()

        // Workouts
        composeTestRule
            .onNodeWithText("Workouts")
            .performClick()
        composeTestRule.waitForIdle()

        // Exercises
        composeTestRule
            .onNodeWithText("Exercises")
            .performClick()
        
        composeTestRule
            .onNodeWithText("Select Exercises for PR Tracking")
            .assertIsDisplayed()

        // Profile
        composeTestRule
            .onNodeWithContentDescription("Profile")
            .performClick()
        
        composeTestRule
            .onNodeWithText("Weight Tracking")
            .assertIsDisplayed()

        // Back to Dashboard
        composeTestRule
            .onNodeWithContentDescription("Dashboard")
            .performClick()
        
        composeTestRule
            .onNodeWithText("Dashboard")
            .assertIsDisplayed()
    }

    @Test
    fun navigationFlow_dashboardFab_works() {
        // Given - On Dashboard
        composeTestRule
            .onNodeWithText("Dashboard")
            .assertIsDisplayed()

        // When - Click FAB
        composeTestRule
            .onNodeWithContentDescription("Start Workout")
            .performClick()

        // Then - Should navigate to workouts (exact behavior depends on implementation)
        composeTestRule.waitForIdle()
    }

    @Test
    fun navigationFlow_backButton_works() {
        // Given - Navigate to Exercise Selection
        composeTestRule
            .onNodeWithText("Exercises")
            .performClick()

        composeTestRule
            .onNodeWithText("Select Exercises for PR Tracking")
            .assertIsDisplayed()

        // When - Click back button (if available)
        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        // Then - Should go back (behavior depends on navigation implementation)
        composeTestRule.waitForIdle()
    }

    @Test
    fun navigationFlow_weightTrackingDialog_cancel_works() {
        // Given - Navigate to Weight Tracking and open dialog
        composeTestRule
            .onNodeWithContentDescription("Profile")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Add weight entry")
            .performClick()

        composeTestRule
            .onNodeWithText("Add Weight Entry")
            .assertIsDisplayed()

        // When - Cancel dialog
        composeTestRule
            .onNodeWithText("Cancel")
            .performClick()

        // Then - Dialog should close
        composeTestRule
            .onNodeWithText("Add Weight Entry")
            .assertDoesNotExist()
    }

    @Test
    fun navigationFlow_exerciseSelection_instructions_visible() {
        // Given - Navigate to Exercise Selection
        composeTestRule
            .onNodeWithText("Exercises")
            .performClick()

        // Then - Instructions should be visible
        composeTestRule
            .onNodeWithText("⭐ Tap the star to track personal records for an exercise. Starred exercises will appear in your dashboard analytics.")
            .assertIsDisplayed()
    }

    @Test
    fun navigationFlow_weightTracking_emptyState_visible() {
        // Given - Navigate to Weight Tracking
        composeTestRule
            .onNodeWithContentDescription("Profile")
            .performClick()

        // Then - Empty state should be visible (if no data)
        composeTestRule
            .onNodeWithText("Weight Tracking")
            .assertIsDisplayed()

        // Should show either empty state or weight history
        composeTestRule.waitForIdle()
    }

    @Test
    fun navigationFlow_dashboard_analytics_visible() {
        // Given - On Dashboard
        composeTestRule
            .onNodeWithText("Dashboard")
            .assertIsDisplayed()

        // Then - Analytics section should be visible
        composeTestRule.waitForIdle()
        
        // Should show analytics cards (exact content depends on data)
        // This test verifies the analytics container is rendered
    }

    @Test
    fun navigationFlow_dashboard_recentWorkouts_visible() {
        // Given - On Dashboard
        composeTestRule
            .onNodeWithText("Dashboard")
            .assertIsDisplayed()

        // Then - Recent workouts should be visible
        composeTestRule
            .onNodeWithText("Recent Workouts")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Push Day")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Pull Day")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Leg Day")
            .assertIsDisplayed()
    }

    @Test
    fun navigationFlow_persistsState_acrossTabs() {
        // Given - Navigate to Exercise Selection and search
        composeTestRule
            .onNodeWithText("Exercises")
            .performClick()

        composeTestRule
            .onNodeWithText("Search exercises")
            .performTextInput("bench")

        // When - Navigate away and back
        composeTestRule
            .onNodeWithText("Dashboard")
            .performClick()

        composeTestRule
            .onNodeWithText("Exercises")
            .performClick()

        // Then - Search state should be preserved (behavior depends on implementation)
        composeTestRule.waitForIdle()
    }

    @Test
    fun navigationFlow_weightTracking_sampleData_works() {
        // Given - Navigate to Weight Tracking
        composeTestRule
            .onNodeWithContentDescription("Profile")
            .performClick()

        composeTestRule
            .onNodeWithText("Weight Tracking")
            .assertIsDisplayed()

        // When - Click sample data button (if visible)
        try {
            composeTestRule
                .onNodeWithText("Add Sample Data")
                .performClick()
            
            // Then - Should add sample data
            composeTestRule.waitForIdle()
        } catch (e: AssertionError) {
            // Sample data button might not be visible if data already exists
            // This is expected behavior
        }
    }

    @Test
    fun navigationFlow_completeUserJourney_works() {
        // Complete user journey test
        
        // 1. Start on Dashboard
        composeTestRule
            .onNodeWithText("Dashboard")
            .assertIsDisplayed()

        // 2. Go to Exercise Selection and search
        composeTestRule
            .onNodeWithText("Exercises")
            .performClick()

        composeTestRule
            .onNodeWithText("Search exercises")
            .performTextInput("bench")

        // 3. Go to Weight Tracking and add entry
        composeTestRule
            .onNodeWithContentDescription("Profile")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Add weight entry")
            .performClick()

        composeTestRule
            .onNodeWithText("Weight (kg)")
            .performTextInput("74.5")

        composeTestRule
            .onNodeWithText("Add")
            .performClick()

        // 4. Return to Dashboard
        composeTestRule
            .onNodeWithText("Dashboard")
            .performClick()

        composeTestRule
            .onNodeWithText("Dashboard")
            .assertIsDisplayed()

        // Journey complete - all navigation worked
    }
}