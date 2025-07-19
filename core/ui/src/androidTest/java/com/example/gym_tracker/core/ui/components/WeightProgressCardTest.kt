package com.example.gym_tracker.core.ui.components

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gym_tracker.core.ui.theme.GymTrackerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeightProgressCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun weightProgressCard_displaysCurrentWeight() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightProgressCard(
                    currentWeight = 74.5,
                    weightThirtyDaysAgo = 77.0,
                    weightChange = -2.5,
                    weightChangeFormatted = "-2.5 kg",
                    weightChangeDescription = "Weight decreased",
                    trendDirection = "DOWN"
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("74.5 kg")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Current")
            .assertIsDisplayed()
    }

    @Test
    fun weightProgressCard_displaysThirtyDaysAgoWeight() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightProgressCard(
                    currentWeight = 74.5,
                    weightThirtyDaysAgo = 77.0,
                    weightChange = -2.5,
                    weightChangeFormatted = "-2.5 kg",
                    weightChangeDescription = "Weight decreased",
                    trendDirection = "DOWN"
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("77.0 kg")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("30 Days Ago")
            .assertIsDisplayed()
    }

    @Test
    fun weightProgressCard_displaysTrendArrow_down() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightProgressCard(
                    currentWeight = 74.5,
                    weightThirtyDaysAgo = 77.0,
                    weightChange = -2.5,
                    weightChangeFormatted = "-2.5 kg",
                    weightChangeDescription = "Weight decreased",
                    trendDirection = "DOWN"
                )
            }
        }

        // Then - Should show down arrow
        composeTestRule
            .onNodeWithText("↓")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("-2.5 kg")
            .assertIsDisplayed()
    }

    @Test
    fun weightProgressCard_displaysTrendArrow_up() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightProgressCard(
                    currentWeight = 76.5,
                    weightThirtyDaysAgo = 74.0,
                    weightChange = 2.5,
                    weightChangeFormatted = "+2.5 kg",
                    weightChangeDescription = "Weight increased",
                    trendDirection = "UP"
                )
            }
        }

        // Then - Should show up arrow
        composeTestRule
            .onNodeWithText("↑")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("+2.5 kg")
            .assertIsDisplayed()
    }

    @Test
    fun weightProgressCard_displaysTrendArrow_stable() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightProgressCard(
                    currentWeight = 74.5,
                    weightThirtyDaysAgo = 74.0,
                    weightChange = 0.5,
                    weightChangeFormatted = "+0.5 kg",
                    weightChangeDescription = "Weight stable (±1 kg)",
                    trendDirection = "STABLE"
                )
            }
        }

        // Then - Should show stable arrow
        composeTestRule
            .onNodeWithText("→")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("+0.5 kg")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Weight stable (±1 kg)")
            .assertIsDisplayed()
    }

    @Test
    fun weightProgressCard_displaysEmptyState_whenNoData() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightProgressCard(
                    shouldPromptWeightTracking = true
                )
            }
        }

        // Then - Should show prompt to start tracking
        composeTestRule
            .onNodeWithText("Start tracking your weight to see progress")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Add Weight")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun weightProgressCard_displaysLoadingState() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightProgressCard(
                    isLoading = true
                )
            }
        }

        // Then - Should show loading indicator and title
        composeTestRule
            .onNodeWithText("Weight Progress")
            .assertIsDisplayed()
    }

    @Test
    fun weightProgressCard_displaysRecentDataPrompt() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightProgressCard(
                    currentWeight = 74.5,
                    weightThirtyDaysAgo = 77.0,
                    weightChange = -2.5,
                    weightChangeFormatted = "-2.5 kg",
                    weightChangeDescription = "Weight decreased",
                    trendDirection = "DOWN",
                    shouldPromptForRecentData = true
                )
            }
        }

        // Then - Should show prompt for recent data
        composeTestRule
            .onNodeWithText("Your weight data is not recent")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Update")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun weightProgressCard_hasViewHistoryButton() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightProgressCard(
                    currentWeight = 74.5,
                    weightThirtyDaysAgo = 77.0,
                    weightChange = -2.5,
                    weightChangeFormatted = "-2.5 kg",
                    weightChangeDescription = "Weight decreased",
                    trendDirection = "DOWN"
                )
            }
        }

        // Then - Should have view history button
        composeTestRule
            .onNodeWithText("View History")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun weightProgressCard_addWeightButton_triggersCallback() {
        // Given
        var addWeightClicked = false
        
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightProgressCard(
                    shouldPromptWeightTracking = true,
                    onAddWeightClick = { addWeightClicked = true }
                )
            }
        }

        // When - Click add weight button
        composeTestRule
            .onNodeWithText("Add Weight")
            .performClick()

        // Then - Should trigger callback
        assert(addWeightClicked)
    }

    @Test
    fun weightProgressCard_viewHistoryButton_triggersCallback() {
        // Given
        var viewHistoryClicked = false
        
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightProgressCard(
                    currentWeight = 74.5,
                    weightThirtyDaysAgo = 77.0,
                    weightChange = -2.5,
                    weightChangeFormatted = "-2.5 kg",
                    weightChangeDescription = "Weight decreased",
                    trendDirection = "DOWN",
                    onViewHistoryClick = { viewHistoryClicked = true }
                )
            }
        }

        // When - Click view history button
        composeTestRule
            .onNodeWithText("View History")
            .performClick()

        // Then - Should trigger callback
        assert(viewHistoryClicked)
    }

    @Test
    fun weightProgressCard_updateButton_triggersCallback() {
        // Given
        var updateClicked = false
        
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightProgressCard(
                    currentWeight = 74.5,
                    weightThirtyDaysAgo = 77.0,
                    weightChange = -2.5,
                    weightChangeFormatted = "-2.5 kg",
                    weightChangeDescription = "Weight decreased",
                    trendDirection = "DOWN",
                    shouldPromptForRecentData = true,
                    onAddWeightClick = { updateClicked = true }
                )
            }
        }

        // When - Click update button
        composeTestRule
            .onNodeWithText("Update")
            .performClick()

        // Then - Should trigger callback
        assert(updateClicked)
    }

    @Test
    fun weightProgressCard_handlesNullWeights() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightProgressCard(
                    currentWeight = null,
                    weightThirtyDaysAgo = null,
                    weightChange = null,
                    weightChangeFormatted = null,
                    weightChangeDescription = null,
                    trendDirection = "STABLE"
                )
            }
        }

        // Then - Should show placeholder values
        composeTestRule
            .onNodeWithText("--")
            .assertExists()
    }

    @Test
    fun weightProgressCard_displaysWeightProgressTitle() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightProgressCard(
                    currentWeight = 74.5,
                    weightThirtyDaysAgo = 77.0,
                    weightChange = -2.5,
                    weightChangeFormatted = "-2.5 kg",
                    weightChangeDescription = "Weight decreased",
                    trendDirection = "DOWN"
                )
            }
        }

        // Then - Should show title
        composeTestRule
            .onNodeWithText("Weight Progress")
            .assertIsDisplayed()
    }

    @Test
    fun weightProgressCard_hasCorrectLayout() {
        // Given
        composeTestRule.setContent {
            GymTrackerTheme {
                WeightProgressCard(
                    currentWeight = 74.5,
                    weightThirtyDaysAgo = 77.0,
                    weightChange = -2.5,
                    weightChangeFormatted = "-2.5 kg",
                    weightChangeDescription = "Weight decreased",
                    trendDirection = "DOWN"
                )
            }
        }

        // Then - Should have all elements in correct layout
        composeTestRule
            .onNodeWithText("Weight Progress")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Current")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("30 Days Ago")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("↓")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("View History")
            .assertIsDisplayed()
    }
}