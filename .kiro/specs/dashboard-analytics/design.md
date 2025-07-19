# Dashboard Analytics Design Document

## Overview

This design document outlines the implementation of comprehensive analytics features for the gym tracker dashboard. The system will provide users with meaningful insights into their workout consistency, progress trends, and achievements through visually appealing cards and metrics that integrate seamlessly with the existing dashboard architecture.

The analytics system will leverage the existing database entities (WorkoutEntity, ExerciseSetEntity, UserProfileEntity) to calculate real-time metrics and display them using the established UI component system.

## Architecture

### Data Flow Architecture

```
Database Entities → Repository Layer → Use Cases → ViewModels → UI Components
```

**Components:**
- **Analytics Repository**: Aggregates data from multiple DAOs to calculate metrics
- **Analytics Use Cases**: Business logic for specific metric calculations
- **Dashboard ViewModel**: Manages analytics state and provides data to UI
- **Analytics UI Components**: Specialized cards for displaying metrics

### Database Integration

The analytics system will query existing entities:
- `WorkoutEntity`: For workout dates, volume, duration tracking
- `ExerciseSetEntity`: For PR calculations and volume trends
- `UserProfileEntity`: For weight tracking and goal management
- `ExerciseEntity`: For identifying star-marked exercises

## Components and Interfaces

### 1. Analytics Repository

```kotlin
interface AnalyticsRepository {
    suspend fun getWorkoutStreak(): WorkoutStreak
    suspend fun getMonthlyWorkoutStats(month: YearMonth): MonthlyWorkoutStats
    suspend fun getVolumeProgress(): VolumeProgress
    suspend fun getPersonalRecords(): List<PersonalRecord>
    suspend fun getWeightProgress(): WeightProgress
    suspend fun getWorkoutCalendarData(month: YearMonth): Set<LocalDate>
}
```

### 2. Analytics Use Cases

**Individual use cases for each metric:**
- `GetWorkoutStreakUseCase`: Calculates current and longest streaks
- `GetMonthlyStatsUseCase`: Computes monthly comparisons and averages
- `GetVolumeProgressUseCase`: Analyzes volume trends and improvements
- `GetPersonalRecordsUseCase`: Identifies PRs for star-marked exercises
- `GetWeightProgressUseCase`: Tracks body weight changes

### 3. Analytics UI Components

**New specialized components:**
- `StreakCard`: Displays current streak, longest streak, days since last workout
- `MonthlyStatsCard`: Shows monthly workout count, comparison, and weekly average
- `VolumeProgressCard`: Volume trend and most improved exercise
- `PersonalRecordsCard`: Star-marked exercise PRs
- `WeightProgressCard`: Body weight trend with arrow indicator

### 4. Enhanced Calendar Integration

**CalendarCard enhancements:**
- Visual indicators for workout days
- Different colors for workout intensity/volume
- Tap interaction to show daily workout summary

## Data Models

### Analytics Data Classes

```kotlin
data class WorkoutStreak(
    val currentStreak: Int,
    val longestStreak: Int,
    val daysSinceLastWorkout: Int,
    val streakType: StreakType // DAYS or WEEKS
)

data class MonthlyWorkoutStats(
    val currentMonthWorkouts: Int,
    val previousMonthWorkouts: Int,
    val averageWorkoutsPerWeek: Double,
    val trend: TrendDirection
)

data class VolumeProgress(
    val currentMonthVolume: Double,
    val previousMonthVolume: Double,
    val volumeTrend: TrendDirection,
    val mostImprovedExercise: ExerciseImprovement?
)

data class PersonalRecord(
    val exerciseName: String,
    val weight: Double,
    val achievedDate: LocalDate,
    val isStarMarked: Boolean
)

data class WeightProgress(
    val currentWeight: Double?,
    val previousWeight: Double?,
    val trend: TrendDirection,
    val changeAmount: Double
)

enum class TrendDirection { UP, DOWN, STABLE }
```

### Database Schema Extensions

**New fields to add to existing entities:**
- `ExerciseEntity.isStarMarked: Boolean` - Flag for dashboard PR display
- `UserProfileEntity.weightHistory: List<WeightEntry>` - Historical weight tracking

```kotlin
data class WeightEntry(
    val weight: Double,
    val recordedDate: LocalDate
)
```

## Error Handling

### Data Availability Scenarios

**Insufficient Data Handling:**
- New users: Show onboarding prompts and placeholder metrics
- Missing weight data: Display weight tracking encouragement
- No star-marked exercises: Suggest marking key exercises
- Empty workout history: Show motivational getting-started content

**Error States:**
- Database query failures: Graceful fallback to cached data
- Calculation errors: Display "Unable to calculate" with retry option
- Network issues: Offline-first approach with local calculations

### User Experience Considerations

- Progressive disclosure: Show basic metrics first, detailed analytics on tap
- Loading states: Skeleton screens while calculating metrics
- Empty states: Helpful guidance for new users
- Error recovery: Clear actions for users to resolve issues

## Testing Strategy

### Unit Testing

**Repository Layer:**
- Mock DAO responses for various data scenarios
- Test edge cases (empty data, single workout, etc.)
- Verify calculation accuracy for all metrics

**Use Case Layer:**
- Test business logic for streak calculations
- Validate trend analysis algorithms
- Test PR identification logic

**ViewModel Layer:**
- Test state management and UI data transformation
- Verify loading and error state handling
- Test user interaction flows

### Integration Testing

**Database Integration:**
- Test complex queries across multiple entities
- Verify data consistency and relationships
- Test performance with large datasets

**UI Integration:**
- Test component rendering with various data states
- Verify user interactions and navigation
- Test responsive layout behavior

### Performance Testing

**Query Optimization:**
- Benchmark analytics queries with large datasets
- Test caching effectiveness
- Monitor memory usage during calculations

**UI Performance:**
- Test smooth scrolling with multiple analytics cards
- Verify quick loading of dashboard metrics
- Test component recomposition efficiency

## Implementation Phases

### Phase 1: Core Analytics Infrastructure
- Implement analytics repository and use cases
- Create basic data models and calculations
- Set up database query optimization

### Phase 2: Streak and Consistency Metrics
- Implement workout streak calculations
- Create monthly statistics analysis
- Build StreakCard and MonthlyStatsCard components

### Phase 3: Progress and Volume Analytics
- Implement volume trend analysis
- Create exercise improvement tracking
- Build VolumeProgressCard component

### Phase 4: Personal Records and Weight Tracking
- Implement PR identification system
- Add star-marking functionality to exercises
- Create PersonalRecordsCard and WeightProgressCard
- Implement weight history tracking

### Phase 5: Enhanced Calendar and Polish
- Enhance CalendarCard with workout indicators
- Add interactive features and animations
- Implement comprehensive error handling
- Performance optimization and testing

## Technical Considerations

### Performance Optimization
- Use Room's built-in query optimization
- Implement result caching for expensive calculations
- Lazy loading for non-critical metrics

### Data Privacy
- All analytics calculations performed locally
- No external analytics services
- User data remains on device

### Scalability
- Efficient queries that scale with workout history
- Pagination for large datasets
- Background calculation for complex metrics

### Accessibility
- Screen reader support for all metrics
- High contrast mode compatibility
- Large text support for analytics cards