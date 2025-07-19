package com.example.gym_tracker.feature.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.gym_tracker.core.data.model.WeightHistory
import com.example.gym_tracker.core.data.repository.AnalyticsRepository
import com.example.gym_tracker.core.database.dao.UserProfileDao
import com.example.gym_tracker.core.database.entity.UserProfileEntity
import com.example.gym_tracker.core.database.entity.FitnessLevel
import com.example.gym_tracker.core.database.entity.FitnessGoal
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class WeightTrackingViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var analyticsRepository: AnalyticsRepository
    private lateinit var userProfileDao: UserProfileDao
    private lateinit var viewModel: WeightTrackingViewModel

    private val defaultUserProfileId = "default-user"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        analyticsRepository = mockk()
        userProfileDao = mockk()
        
        // Mock default behavior
        coEvery { userProfileDao.getUserProfileById(defaultUserProfileId) } returns null
        coEvery { userProfileDao.insertUserProfile(any()) } just Runs
        coEvery { analyticsRepository.getWeightHistory(defaultUserProfileId) } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init creates default user profile when none exists`() = runTest {
        // Given - No existing user profile
        coEvery { userProfileDao.getUserProfileById(defaultUserProfileId) } returns null

        // When
        viewModel = WeightTrackingViewModel(analyticsRepository, userProfileDao)

        // Then
        coVerify {
            userProfileDao.insertUserProfile(
                match { profile ->
                    profile.id == defaultUserProfileId &&
                    profile.weight == 70.0 && // Default weight in kg
                    profile.height == 175.0 && // Default height in cm
                    profile.fitnessLevel == FitnessLevel.BEGINNER
                }
            )
        }
    }

    @Test
    fun `init does not create user profile when one already exists`() = runTest {
        // Given - Existing user profile
        val existingProfile = UserProfileEntity(
            id = defaultUserProfileId,
            age = 30,
            weight = 75.0,
            height = 180.0,
            fitnessLevel = FitnessLevel.INTERMEDIATE,
            goals = listOf(FitnessGoal.STRENGTH),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        coEvery { userProfileDao.getUserProfileById(defaultUserProfileId) } returns existingProfile

        // When
        viewModel = WeightTrackingViewModel(analyticsRepository, userProfileDao)

        // Then
        coVerify(exactly = 0) { userProfileDao.insertUserProfile(any()) }
    }

    @Test
    fun `loadWeightHistory updates UI state to Success when data is loaded`() = runTest {
        // Given
        val weightHistory = listOf(
            WeightHistory(
                id = "1",
                userProfileId = defaultUserProfileId,
                weight = 74.0,
                recordedDate = LocalDate.now(),
                notes = "Current weight"
            ),
            WeightHistory(
                id = "2",
                userProfileId = defaultUserProfileId,
                weight = 75.0,
                recordedDate = LocalDate.now().minusDays(7),
                notes = "Last week"
            )
        )
        coEvery { analyticsRepository.getWeightHistory(defaultUserProfileId) } returns flowOf(weightHistory)

        // When
        viewModel = WeightTrackingViewModel(analyticsRepository, userProfileDao)

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState is WeightTrackingUiState.Success)
        assertEquals(2, (uiState as WeightTrackingUiState.Success).weightHistory.size)
        assertEquals(74.0, uiState.weightHistory[0].weight)
        assertEquals("Current weight", uiState.weightHistory[0].notes)
    }

    @Test
    fun `loadWeightHistory updates UI state to Error when exception occurs`() = runTest {
        // Given
        coEvery { analyticsRepository.getWeightHistory(defaultUserProfileId) } throws RuntimeException("Database error")

        // When
        viewModel = WeightTrackingViewModel(analyticsRepository, userProfileDao)

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState is WeightTrackingUiState.Error)
        assertEquals("Database error", (uiState as WeightTrackingUiState.Error).message)
    }

    @Test
    fun `addWeightEntry adds new entry and reloads data`() = runTest {
        // Given
        val initialHistory = listOf(
            WeightHistory(
                id = "1",
                userProfileId = defaultUserProfileId,
                weight = 75.0,
                recordedDate = LocalDate.now().minusDays(1),
                notes = "Yesterday"
            )
        )
        val updatedHistory = initialHistory + WeightHistory(
            id = "2",
            userProfileId = defaultUserProfileId,
            weight = 74.5,
            recordedDate = LocalDate.now(),
            notes = "Today"
        )

        coEvery { analyticsRepository.getWeightHistory(defaultUserProfileId) } returnsMany listOf(
            flowOf(initialHistory),
            flowOf(updatedHistory)
        )
        coEvery { analyticsRepository.addWeightEntry(any()) } just Runs

        viewModel = WeightTrackingViewModel(analyticsRepository, userProfileDao)

        // When
        viewModel.addWeightEntry(74.5, "Today")

        // Then
        coVerify {
            analyticsRepository.addWeightEntry(
                match { entry ->
                    entry.weight == 74.5 &&
                    entry.notes == "Today" &&
                    entry.userProfileId == defaultUserProfileId &&
                    entry.recordedDate == LocalDate.now()
                }
            )
        }

        // Verify UI state is updated
        val uiState = viewModel.uiState.value
        assertTrue(uiState is WeightTrackingUiState.Success)
        assertEquals(2, (uiState as WeightTrackingUiState.Success).weightHistory.size)
    }

    @Test
    fun `addWeightEntry handles error gracefully and reloads data`() = runTest {
        // Given
        val initialHistory = listOf(
            WeightHistory(
                id = "1",
                userProfileId = defaultUserProfileId,
                weight = 75.0,
                recordedDate = LocalDate.now().minusDays(1),
                notes = "Yesterday"
            )
        )

        coEvery { analyticsRepository.getWeightHistory(defaultUserProfileId) } returns flowOf(initialHistory)
        coEvery { analyticsRepository.addWeightEntry(any()) } throws RuntimeException("Add failed")

        viewModel = WeightTrackingViewModel(analyticsRepository, userProfileDao)

        // When
        viewModel.addWeightEntry(74.5, "Today")

        // Then - Should still attempt to reload data after error
        coVerify(atLeast = 2) { analyticsRepository.getWeightHistory(defaultUserProfileId) }
    }

    @Test
    fun `seedSampleWeightData creates sample data when no existing data`() = runTest {
        // Given - No existing weight data
        coEvery { analyticsRepository.getWeightHistory(defaultUserProfileId) } returnsMany listOf(
            flowOf(emptyList()), // Initial empty state
            flowOf(emptyList()), // Check for existing sample data
            flowOf(createSampleWeightHistory()) // After seeding
        )
        coEvery { analyticsRepository.addWeightEntry(any()) } just Runs

        viewModel = WeightTrackingViewModel(analyticsRepository, userProfileDao)

        // When
        viewModel.seedSampleWeightData()

        // Then - Should add multiple weight entries
        coVerify(atLeast = 10) { analyticsRepository.addWeightEntry(any()) }
    }

    @Test
    fun `seedSampleWeightData does not create data when sample data already exists`() = runTest {
        // Given - Existing sample data (identified by specific note)
        val existingSampleData = listOf(
            WeightHistory(
                id = "1",
                userProfileId = defaultUserProfileId,
                weight = 77.2,
                recordedDate = LocalDate.now().minusDays(30),
                notes = "30 days ago baseline" // This identifies sample data
            )
        )
        coEvery { analyticsRepository.getWeightHistory(defaultUserProfileId) } returns flowOf(existingSampleData)

        viewModel = WeightTrackingViewModel(analyticsRepository, userProfileDao)

        // When
        viewModel.seedSampleWeightData()

        // Then - Should not add any new entries
        coVerify(exactly = 0) { analyticsRepository.addWeightEntry(any()) }
    }

    private fun createSampleWeightHistory(): List<WeightHistory> {
        val today = LocalDate.now()
        return listOf(
            WeightHistory(
                id = "sample1",
                userProfileId = defaultUserProfileId,
                weight = 74.0,
                recordedDate = today,
                notes = "Current weight"
            ),
            WeightHistory(
                id = "sample2",
                userProfileId = defaultUserProfileId,
                weight = 77.2,
                recordedDate = today.minusDays(30),
                notes = "30 days ago baseline"
            )
        )
    }
}