# Design Document

## Overview

This design implements permanent database persistence for workout exercises and sets data, replacing the current in-memory storage with proper database integration using the existing Room database infrastructure. The solution maintains the current user experience while ensuring all workout data persists across app sessions.

## Architecture

### Current State Analysis

The current implementation uses static in-memory storage in the `WorkoutExercisesViewModel`:

```kotlin
companion object {
    private val workoutExercisesStorage = mutableMapOf<String, MutableList<WorkoutExerciseInstanceData>>()
    private val workoutNamesStorage = mutableMapOf<String, String>()
}
```

This approach works for session persistence but fails when the app process is terminated.

### Target Architecture

The new architecture will integrate with the existing database layer:

```
┌─────────────────────┐    ┌──────────────────────┐    ┌─────────────────────┐
│   UI Layer          │    │   Data Layer         │    │   Database Layer    │
│                     │    │                      │    │                     │
│ WorkoutExercises    │◄──►│ ExerciseInstance     │◄──►│ ExerciseInstance    │
│ ViewModel           │    │ Repository           │    │ DAO                 │
│                     │    │                      │    │                     │
│ WorkoutExercises    │    │ WorkoutSet           │    │ WorkoutSet          │
│ Screen              │    │ Repository           │    │ DAO                 │
└─────────────────────┘    └──────────────────────┘    └─────────────────────┘
```

## Components and Interfaces

### 1. Repository Layer Integration

#### ExerciseInstanceRepository Enhancement
```kotlin
interface ExerciseInstanceRepository {
    // Existing methods...
    suspend fun getExerciseInstancesByWorkoutId(workoutId: String): Flow<List<ExerciseInstance>>
    suspend fun insertExerciseInstance(exerciseInstance: ExerciseInstance): String
    suspend fun updateExerciseInstance(exerciseInstance: ExerciseInstance)
    suspend fun deleteExerciseInstance(exerciseInstanceId: String)
}
```

#### WorkoutSetRepository Enhancement
```kotlin
interface WorkoutSetRepository {
    suspend fun getSetsByExerciseInstanceId(exerciseInstanceId: String): Flow<List<WorkoutSet>>
    suspend fun insertWorkoutSet(workoutSet: WorkoutSet): String
    suspend fun updateWorkoutSet(workoutSet: WorkoutSet)
    suspend fun deleteWorkoutSet(setId: String)
}
```

### 2. ViewModel Transformation

#### Current Data Models
```kotlin
// Current in-memory data structures
data class WorkoutExerciseInstanceData(...)
data class ExerciseInstanceData(...)
data class WorkoutSetData(...)
```

#### Target Integration
```kotlin
@HiltViewModel
class WorkoutExercisesViewModel @Inject constructor(
    private val exerciseInstanceRepository: ExerciseInstanceRepository,
    private val workoutSetRepository: WorkoutSetRepository,
    private val exerciseRepository: ExerciseRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    // Remove static storage
    // Add repository-based operations
}
```

### 3. Data Flow Design

#### Loading Workout Exercises
```
User navigates to workout
        ↓
ViewModel.loadWorkoutExercises(workoutId)
        ↓
Repository.getExerciseInstancesByWorkoutId(workoutId)
        ↓
Database query via DAO
        ↓
Return Flow<List<ExerciseInstance>>
        ↓
Transform to UI models
        ↓
Update UI state
```

#### Adding Exercise to Workout
```
User adds exercise
        ↓
ViewModel.addExerciseToWorkout(exerciseId)
        ↓
Create ExerciseInstance entity
        ↓
Repository.insertExerciseInstance(instance)
        ↓
Database insert via DAO
        ↓
Reload workout exercises
        ↓
Update UI state
```

#### Adding/Updating Sets
```
User adds/updates set
        ↓
ViewModel.addSetToExercise() / updateSet()
        ↓
Create/Update WorkoutSet entity
        ↓
Repository.insertWorkoutSet() / updateWorkoutSet()
        ↓
Database operation via DAO
        ↓
Reload exercise sets
        ↓
Update UI state
```

## Data Models

### Database Entities (Existing)

#### ExerciseInstance Entity
```kotlin
@Entity(tableName = "exercise_instances")
data class ExerciseInstanceEntity(
    @PrimaryKey val id: String,
    val workoutId: String,
    val exerciseId: String,
    val orderInWorkout: Int,
    val notes: String,
    val completedAt: Instant?
)
```

#### WorkoutSet Entity
```kotlin
@Entity(tableName = "workout_sets")
data class WorkoutSetEntity(
    @PrimaryKey val id: String,
    val exerciseInstanceId: String,
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val restTime: Int?,
    val completedAt: Instant?
)
```

### Domain Models (Existing)
```kotlin
data class ExerciseInstance(
    val id: String,
    val workoutId: String,
    val exerciseId: String,
    val orderInWorkout: Int,
    val notes: String,
    val completedAt: Instant?
)

data class WorkoutSet(
    val id: String,
    val exerciseInstanceId: String,
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val restTime: Int?,
    val completedAt: Instant?
)
```

### UI Models (Current)
```kotlin
// Transform domain models to current UI models for compatibility
data class WorkoutExerciseInstanceData(
    val exerciseInstance: ExerciseInstanceData,
    val exercise: ExerciseData,
    val sets: List<WorkoutSetData>,
    val lastPerformed: LastPerformedData?
)
```

## Error Handling

### Database Operation Failures
```kotlin
sealed class WorkoutExercisesUiState {
    object Loading : WorkoutExercisesUiState()
    data class Success(
        val workoutName: String?,
        val exercises: List<WorkoutExerciseInstanceData>
    ) : WorkoutExercisesUiState()
    data class Error(
        val message: String,
        val isRetryable: Boolean = true
    ) : WorkoutExercisesUiState()
}
```

### Error Recovery Strategies
1. **Network/Database Unavailable**: Show cached data with offline indicator
2. **Insert/Update Failures**: Retry with exponential backoff
3. **Data Corruption**: Fallback to empty state with user notification
4. **Migration Failures**: Preserve in-memory data and retry migration

## Testing Strategy

### Unit Tests
```kotlin
class WorkoutExercisesViewModelTest {
    @Test
    fun `loadWorkoutExercises loads from repository`()
    
    @Test
    fun `addExerciseToWorkout saves to database`()
    
    @Test
    fun `addSetToExercise persists set data`()
    
    @Test
    fun `updateSet modifies existing set`()
    
    @Test
    fun `error handling shows appropriate UI state`()
}
```

### Integration Tests
```kotlin
class WorkoutExercisePersistenceTest {
    @Test
    fun `exercise persists across app restart`()
    
    @Test
    fun `sets persist across navigation`()
    
    @Test
    fun `workout data survives process death`()
}
```

### Migration Tests
```kotlin
class DataMigrationTest {
    @Test
    fun `in-memory data migrates to database`()
    
    @Test
    fun `migration preserves all exercise data`()
    
    @Test
    fun `migration handles edge cases`()
}
```

## Implementation Phases

### Phase 1: Repository Integration
- Inject repositories into ViewModel
- Replace static storage with repository calls
- Maintain current UI models for compatibility

### Phase 2: Database Operations
- Implement exercise instance CRUD operations
- Implement workout set CRUD operations
- Add proper error handling

### Phase 3: Data Migration
- Create migration utility for existing in-memory data
- Implement fallback mechanisms
- Add migration tests

### Phase 4: Performance Optimization
- Implement proper caching strategies
- Optimize database queries
- Add loading states for better UX

### Phase 5: Testing and Validation
- Comprehensive testing across all scenarios
- Performance testing with large datasets
- User acceptance testing

## Migration Strategy

### In-Memory to Database Migration
```kotlin
class WorkoutDataMigration {
    suspend fun migrateInMemoryData() {
        // Extract data from static storage
        val inMemoryData = WorkoutExercisesViewModel.getStoredData()
        
        // Transform to database entities
        val exerciseInstances = transformToExerciseInstances(inMemoryData)
        val workoutSets = transformToWorkoutSets(inMemoryData)
        
        // Save to database
        exerciseInstanceRepository.insertAll(exerciseInstances)
        workoutSetRepository.insertAll(workoutSets)
        
        // Clear in-memory storage
        WorkoutExercisesViewModel.clearStoredData()
    }
}
```

### Backward Compatibility
- Keep current UI models unchanged
- Transform database models to UI models
- Maintain existing screen behavior
- Preserve current navigation flow

## Performance Considerations

### Database Query Optimization
- Use Flow for reactive data loading
- Implement proper indexing on foreign keys
- Use Room's built-in caching mechanisms

### Memory Management
- Remove static storage to prevent memory leaks
- Use proper lifecycle-aware data loading
- Implement pagination for large workout histories

### User Experience
- Show loading states during database operations
- Implement optimistic updates for immediate feedback
- Cache frequently accessed data

## Security and Data Integrity

### Data Validation
- Validate exercise instance relationships
- Ensure workout set consistency
- Prevent orphaned data records

### Transaction Management
- Use database transactions for multi-table operations
- Implement proper rollback mechanisms
- Ensure data consistency across operations

### Backup and Recovery
- Leverage existing database backup mechanisms
- Implement data export/import functionality
- Provide data recovery options for users