package com.example.gym_tracker.feature.profile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gym_tracker.core.data.model.WeightHistory
import com.example.gym_tracker.core.ui.theme.GymTrackerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class WeightTrackingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun weightTrackingScreen_displaysEmptyStateCorrectly() {
        // Given - Empty weight history
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightTrackingScreen()
            }
        }

        // Then - Should display empty state elements
        composeTestRule
            .onNodeWithText("Start Tracking Your Weight")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Track your weight progress over time to see trends and stay motivated on your fitness journey.")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Add First Entry")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Add Sample Data (70-80kg)")
            .assertIsDisplayed()
    }

    @Test
    fun weightTrackingScreen_showsAddWeightDialogWhenFabClicked() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightTrackingScreen()
            }
        }

        // When - Click the FAB
        composeTestRule
            .onNodeWithContentDescription("Add weight entry")
            .performClick()

        // Then - Dialog should appear
        composeTestRule
            .onNodeWithText("Add Weight Entry")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Weight (kg)")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Notes (optional)")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Add")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Cancel")
            .assertIsDisplayed()
    }

    @Test
    fun weightTrackingScreen_addWeightDialog_validatesInput() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightTrackingScreen()
            }
        }

        // When - Open dialog and try to add without weight
        composeTestRule
            .onNodeWithContentDescription("Add weight entry")
            .performClick()
        
        composeTestRule
            .onNodeWithText("Add")
            .performClick()

        // Then - Should show error
        composeTestRule
            .onNodeWithText("Please enter a valid weight")
            .assertIsDisplayed()
    }

    @Test
    fun weightTrackingScreen_addWeightDialog_acceptsValidInput() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightTrackingScreen()
            }
        }

        // When - Open dialog and enter valid weight
        composeTestRule
            .onNodeWithContentDescription("Add weight entry")
            .performClick()
        
        composeTestRule
            .onNodeWithText("Weight (kg)")
            .performTextInput("75.5")
        
        composeTestRule
            .onNodeWithText("Notes (optional)")
            .performTextInput("Morning weigh-in")
        
        composeTestRule
            .onNodeWithText("Add")
            .performClick()

        // Then - Dialog should close (no longer visible)
        composeTestRule
            .onNodeWithText("Add Weight Entry")
            .assertDoesNotExist()
    }

    @Test
    fun weightTrackingScreen_addWeightDialog_canBeCancelled() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightTrackingScreen()
            }
        }

        // When - Open dialog and cancel
        composeTestRule
            .onNodeWithContentDescription("Add weight entry")
            .performClick()
        
        composeTestRule
            .onNodeWithText("Cancel")
            .performClick()

        // Then - Dialog should close
        composeTestRule
            .onNodeWithText("Add Weight Entry")
            .assertDoesNotExist()
    }

    @Test
    fun weightTrackingScreen_displaysWeightHistoryList() {
        // Given - Mock weight history data
        val mockWeightHistory = listOf(
            WeightHistory(
                id = "1",
                userProfileId = "test-user",
                weight = 74.5,
                recordedDate = LocalDate.now(),
                notes = "Current weight"
            ),
            WeightHistory(
                id = "2",
                userProfileId = "test-user",
                weight = 75.0,
                recordedDate = LocalDate.now().minusDays(7),
                notes = "Last week"
            )
        )

        composeTestRule.setContent {
            GymTrackerTheme {
                // Note: In a real test, we'd inject mock data through the ViewModel
                // For now, this tests the UI structure
                WeightTrackingScreen()
            }
        }

        // Then - Should display the screen structure
        composeTestRule
            .onNodeWithText("Weight Tracking")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithContentDescription("Add weight entry")
            .assertIsDisplayed()
    }

    @Test
    fun weightTrackingScreen_hasCorrectNavigationElements() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightTrackingScreen()
            }
        }

        // Then - Should have navigation elements
        composeTestRule
            .onNodeWithText("Weight Tracking")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithContentDescription("Back")
            .assertIsDisplayed()
    }

    @Test
    fun weightTrackingScreen_sampleDataButton_isDisplayedInEmptyState() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightTrackingScreen()
            }
        }

        // Then - Sample data button should be visible
        composeTestRule
            .onNodeWithText("Add Sample Data (70-80kg)")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun weightTrackingScreen_addWeightDialog_handlesLongNotes() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightTrackingScreen()
            }
        }

        // When - Open dialog and enter long notes
        composeTestRule
            .onNodeWithContentDescription("Add weight entry")
            .performClick()
        
        composeTestRule
            .onNodeWithText("Weight (kg)")
            .performTextInput("74.0")
        
        val longNotes = "This is a very long note that tests the text field's ability to handle multiple lines of text input for weight tracking entries."
        composeTestRule
            .onNodeWithText("Notes (optional)")
            .performTextInput(longNotes)

        // Then - Should accept the long text
        composeTestRule
            .onNodeWithText("Add")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun weightTrackingScreen_addWeightDialog_handlesDecimalWeights() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightTrackingScreen()
            }
        }

        // When - Open dialog and enter decimal weight
        composeTestRule
            .onNodeWithContentDescription("Add weight entry")
            .performClick()
        
        composeTestRule
            .onNodeWithText("Weight (kg)")
            .performTextInput("74.25")
        
        composeTestRule
            .onNodeWithText("Add")
            .performClick()

        // Then - Should accept decimal input and close dialog
        composeTestRule
            .onNodeWithText("Add Weight Entry")
            .assertDoesNotExist()
    }

    @Test
    fun weightTrackingScreen_addWeightDialog_rejectsInvalidWeights() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightTrackingScreen()
            }
        }

        // When - Try various invalid inputs
        composeTestRule
            .onNodeWithContentDescription("Add weight entry")
            .performClick()

        // Test negative weight
        composeTestRule
            .onNodeWithText("Weight (kg)")
            .performTextInput("-5")
        
        composeTestRule
            .onNodeWithText("Add")
            .performClick()

        // Then - Should show error
        composeTestRule
            .onNodeWithText("Please enter a valid weight")
            .assertIsDisplayed()

        // Test zero weight
        composeTestRule
            .onNodeWithText("Weight (kg)")
            .performTextClearance()
        
        composeTestRule
            .onNodeWithText("Weight (kg)")
            .performTextInput("0")
        
        composeTestRule
            .onNodeWithText("Add")
            .performClick()

        // Then - Should show error
        composeTestRule
            .onNodeWithText("Please enter a valid weight")
            .assertIsDisplayed()

        // Test non-numeric input
        composeTestRule
            .onNodeWithText("Weight (kg)")
            .performTextClearance()
        
        composeTestRule
            .onNodeWithText("Weight (kg)")
            .performTextInput("abc")
        
        composeTestRule
            .onNodeWithText("Add")
            .performClick()

        // Then - Should show error
        composeTestRule
            .onNodeWithText("Please enter a valid weight")
            .assertIsDisplayed()
    }
}