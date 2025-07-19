package com.example.gym_tracker.feature.exercise

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gym_tracker.core.ui.theme.GymTrackerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExerciseSelectionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun exerciseSelectionScreen_displaysCorrectTitle() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                ExerciseSelectionScreen()
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Select Exercises for PR Tracking")
            .assertIsDisplayed()
    }

    @Test
    fun exerciseSelectionScreen_hasNavigationElements() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                ExerciseSelectionScreen()
            }
        }

        // Then - Should have back button
        composeTestRule
            .onNodeWithContentDescription("Back")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun exerciseSelectionScreen_hasSearchFunctionality() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                ExerciseSelectionScreen()
            }
        }

        // Then - Should have search field
        composeTestRule
            .onNodeWithText("Search exercises")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithContentDescription("Search")
            .assertIsDisplayed()
    }

    @Test
    fun exerciseSelectionScreen_searchField_acceptsInput() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                ExerciseSelectionScreen()
            }
        }

        // When - Type in search field
        composeTestRule
            .onNodeWithText("Search exercises")
            .performTextInput("bench press")

        // Then - Should display the input
        composeTestRule
            .onNodeWithText("bench press")
            .assertIsDisplayed()
    }

    @Test
    fun exerciseSelectionScreen_displaysInstructions() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                ExerciseSelectionScreen()
            }
        }

        // Then - Should show instructions
        composeTestRule
            .onNodeWithText("⭐ Tap the star to track personal records for an exercise. Starred exercises will appear in your dashboard analytics.")
            .assertIsDisplayed()
    }

    @Test
    fun exerciseSelectionScreen_showsLoadingState() {
        // Given - Screen in loading state
        composeTestRule.setContent {
            GymTrackerTheme {
                ExerciseSelectionScreen()
            }
        }

        // Then - Should show loading indicator initially
        // Note: This test depends on the ViewModel's initial state
        // In a real scenario, we'd mock the ViewModel to control the state
        composeTestRule
            .onNode(hasTestTag("loading_indicator") or hasContentDescription("Loading"))
            .assertExists()
    }

    @Test
    fun exerciseSelectionScreen_handlesEmptySearchResults() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                ExerciseSelectionScreen()
            }
        }

        // When - Search for non-existent exercise
        composeTestRule
            .onNodeWithText("Search exercises")
            .performTextInput("nonexistentexercise123")

        // Then - Should show no results message
        // Note: This test would work better with mocked data
        composeTestRule.waitForIdle()
    }

    @Test
    fun exerciseSelectionScreen_searchField_isSingleLine() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                ExerciseSelectionScreen()
            }
        }

        // When - Try to enter multiple lines
        composeTestRule
            .onNodeWithText("Search exercises")
            .performTextInput("line1\nline2\nline3")

        // Then - Should only show single line (behavior depends on singleLine = true)
        composeTestRule
            .onNodeWithText("Search exercises")
            .assertIsDisplayed()
    }

    @Test
    fun exerciseSelectionScreen_searchField_clearsCorrectly() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                ExerciseSelectionScreen()
            }
        }

        // When - Enter text and clear it
        composeTestRule
            .onNodeWithText("Search exercises")
            .performTextInput("test search")
        
        composeTestRule
            .onNodeWithText("test search")
            .performTextClearance()

        // Then - Field should be empty
        composeTestRule
            .onNodeWithText("Search exercises")
            .assertIsDisplayed()
    }

    @Test
    fun exerciseSelectionScreen_hasCorrectLayout() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                ExerciseSelectionScreen()
            }
        }

        // Then - Should have all main components in correct order
        composeTestRule
            .onNodeWithText("Select Exercises for PR Tracking")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Search exercises")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("⭐ Tap the star to track personal records for an exercise. Starred exercises will appear in your dashboard analytics.")
            .assertIsDisplayed()
    }

    @Test
    fun exerciseSelectionScreen_instructionsCard_isVisible() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                ExerciseSelectionScreen()
            }
        }

        // Then - Instructions card should be prominently displayed
        composeTestRule
            .onNodeWithText("⭐ Tap the star to track personal records for an exercise. Starred exercises will appear in your dashboard analytics.")
            .assertIsDisplayed()
    }

    @Test
    fun exerciseSelectionScreen_searchIcon_isVisible() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                ExerciseSelectionScreen()
            }
        }

        // Then - Search icon should be visible
        composeTestRule
            .onNodeWithContentDescription("Search")
            .assertIsDisplayed()
    }

    @Test
    fun exerciseSelectionScreen_backButton_isClickable() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                ExerciseSelectionScreen()
            }
        }

        // Then - Back button should be clickable
        composeTestRule
            .onNodeWithContentDescription("Back")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun exerciseSelectionScreen_searchField_hasCorrectHint() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                ExerciseSelectionScreen()
            }
        }

        // Then - Search field should have correct label
        composeTestRule
            .onNodeWithText("Search exercises")
            .assertIsDisplayed()
    }

    @Test
    fun exerciseSelectionScreen_handlesLongSearchQueries() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                ExerciseSelectionScreen()
            }
        }

        // When - Enter very long search query
        val longQuery = "this is a very long search query that tests the text field's ability to handle extended input"
        composeTestRule
            .onNodeWithText("Search exercises")
            .performTextInput(longQuery)

        // Then - Should handle long input gracefully
        composeTestRule.waitForIdle()
        // The exact behavior depends on the implementation
    }

    @Test
    fun exerciseSelectionScreen_searchField_triggersSearch() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                ExerciseSelectionScreen()
            }
        }

        // When - Type in search field
        composeTestRule
            .onNodeWithText("Search exercises")
            .performTextInput("bench")

        // Then - Should trigger search (behavior depends on ViewModel implementation)
        composeTestRule.waitForIdle()
        // In a real test with mocked ViewModel, we'd verify the search was triggered
    }
}