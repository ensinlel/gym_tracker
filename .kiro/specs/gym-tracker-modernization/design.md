# Design Document

## Overview

This design document outlines the modernization of the Gym Tracker Android application, transforming it from a monolithic XML-based app into a modular, Jetpack Compose-powered fitness tracking platform with advanced analytics, AI coaching, and modern architecture patterns.

## Architecture

### High-Level Architecture

The modernized app follows Clean Architecture principles with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐│
│  │   Workout UI    │ │  Statistics UI  │ │   Profile UI    ││
│  │   (Compose)     │ │   (Compose)     │ │   (Compose)     ││
│  └─────────────────┘ └─────────────────┘ └─────────────────┘│
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                     Domain Layer                            │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐│
│  │  Workout Domain │ │Analytics Domain │ │   AI Domain     ││
│  │   Use Cases     │ │   Use Cases     │ │   Use Cases     ││
│  └─────────────────┘ └─────────────────┘ └─────────────────┘│
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                      Data Layer                             │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐│
│  │  Local Database │ │  Remote API     │ │  File Storage   ││
│  │     (Room)      │ │   (Retrofit)    │ │   (DataStore)   ││
│  └─────────────────┘ └─────────────────┘ └─────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

### Module Structure

```
app/
├── core/                          # Core utilities and shared components
│   ├── common/                    # Common utilities, extensions
│   ├── database/                  # Room database setup
│   ├── network/                   # Network configuration
│   ├── ui/                        # Shared UI components
│   └── testing/                   # Testing utilities
├── feature/
│   ├── workout/                   # Workout management feature
│   ├── exercise/                  # Exercise tracking feature
│   ├── statistics/                # Analytics and charts feature
│   ├── profile/                   # User profile and settings
│   └── ai-coaching/               # AI-powered coaching feature
└── app/                           # Main app module
```

## Components and Interfaces

### Database Architecture Modernization

**Current Issues:**
- Mixed use of LiveData and Flow
- Direct DAO access in ViewModels
- No proper migration strategy
- Limited caching

**Modern Solution:**

```kotlin
// Enhanced Entity with relationships
@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: ExerciseCategory,
    val muscleGroups: List<MuscleGroup>,
    val equipment: Equipment,
    val instructions: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isCustom: Boolean = false
)

// Repository with Flow and caching
interface ExerciseRepository {
    fun getExercisesFlow(): Flow<List<Exercise>>
    fun getExercisesByCategory(category: ExerciseCategory): Flow<List<Exercise>>
    suspend fun insertExercise(exercise: Exercise)
    suspend fun updateExercise(exercise: Exercise)
    suspend fun deleteExercise(id: String)
    suspend fun syncWithRemote()
}

@Singleton
class ExerciseRepositoryImpl @Inject constructor(
    private val localDataSource: ExerciseLocalDataSource,
    private val remoteDataSource: ExerciseRemoteDataSource,
    private val cacheManager: CacheManager
) : ExerciseRepository {
    
    override fun getExercisesFlow(): Flow<List<Exercise>> = 
        localDataSource.getExercisesFlow()
            .map { exercises ->
                cacheManager.cache("exercises", exercises)
                exercises
            }
}
```

### Advanced Data Visualization

**Current Issues:**
- Basic MPAndroidChart implementation
- Limited chart types
- No interactivity
- Poor performance with large datasets

**Modern Solution - Vico Charts:**

```kotlin
// Chart configuration with Vico
@Composable
fun ProgressChart(
    data: List<WorkoutData>,
    chartType: ChartType,
    modifier: Modifier = Modifier
) {
    val chartEntryModel = remember(data) {
        data.toChartEntryModel()
    }
    
    when (chartType) {
        ChartType.LINE -> {
            Chart(
                chart = lineChart(
                    lines = listOf(
                        LineChart.LineSpec(
                            lineColor = MaterialTheme.colorScheme.primary,
                            lineBackgroundShader = DynamicShaders.fromBrush(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                        Color.Transparent
                                    )
                                )
                            )
                        )
                    )
                ),
                chartModelProducer = chartEntryModel,
                startAxis = AxisRenderer.start(),
                bottomAxis = AxisRenderer.bottom(),
                modifier = modifier
            )
        }
        ChartType.BAR -> {
            Chart(
                chart = columnChart(
                    columns = listOf(
                        ColumnChart.ColumnSpec(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                ),
                chartModelProducer = chartEntryModel,
                modifier = modifier
            )
        }
        ChartType.HEATMAP -> {
            HeatmapChart(
                data = data.toHeatmapData(),
                modifier = modifier
            )
        }
    }
}

// Advanced analytics calculations
class AnalyticsEngine @Inject constructor() {
    
    fun calculateVolumeProgression(workouts: List<Workout>): Flow<List<VolumePoint>> =
        flow {
            val volumePoints = workouts
                .groupBy { it.date.toLocalDate() }
                .map { (date, dayWorkouts) ->
                    val totalVolume = dayWorkouts.sumOf { workout ->
                        workout.exercises.sumOf { exercise ->
                            exercise.sets.sumOf { set ->
                                set.weight * set.reps
                            }
                        }
                    }
                    VolumePoint(date, totalVolume)
                }
                .sortedBy { it.date }
            
            emit(volumePoints)
        }
    
    fun calculateOneRepMax(sets: List<ExerciseSet>): Flow<Double> =
        flow {
            val maxSet = sets.maxByOrNull { it.weight * (1 + it.reps / 30.0) }
            val oneRM = maxSet?.let { 
                it.weight * (1 + it.reps / 30.0) // Epley formula
            } ?: 0.0
            emit(oneRM)
        }
}
```

### Modern UI/UX with Jetpack Compose

**Current Issues:**
- XML layouts with complex view hierarchies
- No consistent theming
- Poor accessibility
- Limited animations

**Modern Solution:**

```kotlin
// Material Design 3 theming
@Composable
fun GymTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Workout screen with modern Compose
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel = hiltViewModel(),
    onNavigateToExercise: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout") },
                actions = {
                    IconButton(onClick = viewModel::toggleView) {
                        Icon(
                            imageVector = if (uiState.isGridView) Icons.Default.List else Icons.Default.GridView,
                            contentDescription = "Toggle view"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = viewModel::addExercise,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Exercise") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(
                items = uiState.exercises,
                key = { it.id }
            ) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    onClick = { onNavigateToExercise(exercise.id) },
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }
}
```

### AI-Powered Coaching Integration

```kotlin
// AI coaching service
interface AICoachingService {
    suspend fun getExerciseTips(
        exercise: Exercise,
        userProfile: UserProfile,
        recentPerformance: List<ExerciseSet>
    ): Result<CoachingTips>
    
    suspend fun analyzeForm(
        exercise: Exercise,
        performanceData: PerformanceData
    ): Result<FormAnalysis>
    
    suspend fun generateWorkoutPlan(
        userGoals: UserGoals,
        currentLevel: FitnessLevel,
        equipment: List<Equipment>
    ): Result<WorkoutPlan>
}

@Singleton
class AICoachingServiceImpl @Inject constructor(
    private val apiService: LLMApiService,
    private val cacheRepository: CacheRepository
) : AICoachingService {
    
    override suspend fun getExerciseTips(
        exercise: Exercise,
        userProfile: UserProfile,
        recentPerformance: List<ExerciseSet>
    ): Result<CoachingTips> = withContext(Dispatchers.IO) {
        try {
            // Check cache first
            val cacheKey = "tips_${exercise.id}_${userProfile.id}"
            val cachedTips = cacheRepository.get<CoachingTips>(cacheKey)
            
            if (cachedTips != null && !cachedTips.isExpired()) {
                return@withContext Result.success(cachedTips)
            }
            
            // Prepare context for LLM
            val context = buildString {
                append("Exercise: ${exercise.name}\n")
                append("Muscle Groups: ${exercise.muscleGroups.joinToString()}\n")
                append("User Level: ${userProfile.fitnessLevel}\n")
                append("Recent Performance: ${recentPerformance.summarize()}\n")
                append("User Goals: ${userProfile.goals.joinToString()}\n")
            }
            
            val response = apiService.generateCoachingTips(context)
            val tips = response.toCoachingTips()
            
            // Cache the result
            cacheRepository.put(cacheKey, tips, duration = 24.hours)
            
            Result.success(tips)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

## Data Models

### Enhanced Database Schema

```kotlin
// Workout entity with relationships
@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val templateId: String?,
    val startTime: Instant,
    val endTime: Instant?,
    val notes: String = "",
    val rating: Int? = null, // 1-5 stars
    val totalVolume: Double = 0.0,
    val averageRestTime: Duration = Duration.ZERO
)

// Exercise set with advanced metrics
@Entity(tableName = "exercise_sets")
data class ExerciseSet(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val exerciseInstanceId: String,
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val restTime: Duration = Duration.ZERO,
    val rpe: Int? = null, // Rate of Perceived Exertion (1-10)
    val tempo: String? = null, // e.g., "3-1-2-1"
    val isWarmup: Boolean = false,
    val isFailure: Boolean = false,
    val notes: String = ""
)

// User profile for AI coaching
@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val age: Int,
    val weight: Double,
    val height: Double,
    val fitnessLevel: FitnessLevel,
    val goals: List<FitnessGoal>,
    val limitations: List<String> = emptyList(),
    val preferredEquipment: List<Equipment> = emptyList(),
    val trainingFrequency: Int = 3, // days per week
    val createdAt: Instant,
    val updatedAt: Instant
)
```

## Error Handling

### Comprehensive Error Management

```kotlin
// Sealed class for different error types
sealed class AppError : Exception() {
    data class NetworkError(override val message: String) : AppError()
    data class DatabaseError(override val message: String) : AppError()
    data class ValidationError(override val message: String) : AppError()
    data class AIServiceError(override val message: String) : AppError()
    data class UnknownError(override val message: String) : AppError()
}

// Error handling in ViewModels
@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val errorHandler: ErrorHandler
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState = _uiState.asStateFlow()
    
    fun loadWorkouts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            workoutRepository.getWorkouts()
                .catch { exception ->
                    val error = errorHandler.handleError(exception)
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = error
                        ) 
                    }
                }
                .collect { workouts ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            workouts = workouts,
                            error = null
                        ) 
                    }
                }
        }
    }
}
```

## Testing Strategy

### Comprehensive Testing Approach

```kotlin
// Unit tests for domain layer
class AnalyticsEngineTest {
    
    private val analyticsEngine = AnalyticsEngine()
    
    @Test
    fun `calculateVolumeProgression returns correct progression`() = runTest {
        // Given
        val workouts = listOf(
            createWorkout(date = LocalDate.of(2024, 1, 1), volume = 1000.0),
            createWorkout(date = LocalDate.of(2024, 1, 2), volume = 1100.0),
            createWorkout(date = LocalDate.of(2024, 1, 3), volume = 1200.0)
        )
        
        // When
        val result = analyticsEngine.calculateVolumeProgression(workouts).first()
        
        // Then
        assertThat(result).hasSize(3)
        assertThat(result[0].volume).isEqualTo(1000.0)
        assertThat(result[1].volume).isEqualTo(1100.0)
        assertThat(result[2].volume).isEqualTo(1200.0)
    }
}

// Integration tests for repository
@HiltAndroidTest
class ExerciseRepositoryTest {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var repository: ExerciseRepository
    
    @Test
    fun `repository caches exercises correctly`() = runTest {
        // Given
        val exercise = createExercise(name = "Bench Press")
        
        // When
        repository.insertExercise(exercise)
        val result = repository.getExercisesFlow().first()
        
        // Then
        assertThat(result).contains(exercise)
    }
}

// UI tests with Compose
@HiltAndroidTest
class WorkoutScreenTest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun `workout screen displays exercises correctly`() {
        composeTestRule.setContent {
            GymTrackerTheme {
                WorkoutScreen(
                    onNavigateToExercise = {}
                )
            }
        }
        
        composeTestRule
            .onNodeWithText("Bench Press")
            .assertIsDisplayed()
    }
}
```

## Technology Stack Comparison

### Current vs Modern Stack

| Component | Current | Modern Alternative | Benefits |
|-----------|---------|-------------------|----------|
| **UI Framework** | XML Layouts + Fragments | Jetpack Compose | Declarative UI, better performance, less boilerplate |
| **Charts** | MPAndroidChart | Vico Charts | Better Compose integration, modern API, customizable |
| **Database** | Room + LiveData | Room + Flow + Hilt | Better coroutine support, dependency injection |
| **Architecture** | MVVM (basic) | Clean Architecture + MVVM | Better separation of concerns, testability |
| **Dependency Injection** | Manual | Hilt | Compile-time safety, better performance |
| **Navigation** | Manual Intents | Navigation Compose | Type-safe navigation, better back stack management |
| **Async Programming** | Mixed (Callbacks/LiveData) | Kotlin Coroutines + Flow | Better structured concurrency |
| **State Management** | Manual | StateFlow + Compose State | Reactive state management |
| **Testing** | Limited | Comprehensive (Unit/Integration/UI) | Better code quality, regression prevention |

### Recommended Libraries

```kotlin
// build.gradle.kts (Module level)
dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    
    // Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // Charts
    implementation("com.patrykandpatrick.vico:compose:1.13.1")
    implementation("com.patrykandpatrick.vico:compose-m3:1.13.1")
    implementation("com.patrykandpatrick.vico:core:1.13.1")
    
    // Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Data Store
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    testImplementation("com.google.truth:truth:1.1.4")
    
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
    
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

This design provides a comprehensive modernization plan that addresses all the requirements while maintaining backward compatibility and ensuring a smooth migration path.