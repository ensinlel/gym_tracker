# Implementation Plan

## Analytics Infrastructure

- [x] 1. Extend database entities for analytics support





  - Add `isStarMarked: Boolean` field to ExerciseEntity for PR tracking
  - Add weight history tracking to UserProfileEntity or create separate WeightHistoryEntity
  - Create database migration scripts for new fields
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 5.1, 5.2, 5.3, 5.4_

- [x] 2. Create analytics data models



  - Implement WorkoutStreak, MonthlyWorkoutStats, VolumeProgress data classes
  - Implement PersonalRecord, WeightProgress, ExerciseImprovement data classes
  - Create TrendDirection enum and supporting types
  - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2, 2.3, 3.1, 3.2, 4.1, 5.1_

- [x] 3. Implement analytics repository interface and implementation















  - Create AnalyticsRepository interface with methods for all metrics
  - Implement AnalyticsRepositoryImpl with database queries for streak calculations
  - Add methods for monthly stats, volume progress, and PR calculations
  - Implement weight progress and calendar data queries
  - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 3.1, 3.2, 3.3, 3.4, 4.1, 4.2, 4.3, 4.4, 5.1, 5.2, 5.3, 5.4, 6.1, 6.2, 6.3, 6.4_

## Analytics Use Cases

- [X] 4. Implement workout streak use case




  - Create GetWorkoutStreakUseCase for calculating current and longest streaks
  - Implement logic for days since last workout calculation
  - Add streak type detection (days vs weeks)
  - Include encouraging message logic for inactive periods
  - _Requirements: 1.1, 1.2, 1.3, 1.4_

- [X] 5. Implement monthly statistics use case

















  - Create GetMonthlyStatsUseCase for workout count comparisons
  - Implement weekly average calculations for current month
  - Add trend direction calculation logic
  - Handle incomplete months with projected averages
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6_

- [x] 6. Implement volume progress use case












  - Create GetVolumeProgressUseCase for volume trend analysis
  - Implement most improved exercise identification logic
  - Add percentage improvement calculations
  - Handle cases with insufficient data
  - _Requirements: 3.1, 3.2, 3.3, 3.4_

- [x] 7. Implement personal records use case



  - Create GetPersonalRecordsUseCase for star-marked exercise PRs
  - Implement PR identification and weight extraction logic
  - Add star-marking suggestion for new users
  - Handle empty star-marked exercise scenarios
  - _Requirements: 4.1, 4.2, 4.3, 4.4_

- [x] 8. Implement weight progress use case



  - Create GetWeightProgressUseCase for body weight trend analysis
  - Implement 30-day comparison logic with trend indicators
  - Add stable weight detection (within 2 lbs threshold)
  - Handle missing weight data with prompts
  - _Requirements: 5.1, 5.2, 5.3, 5.4_

## Analytics UI Components

- [x] 9. Create StreakCard component



  - Implement UI component displaying current streak, longest streak, days since last workout
  - Add encouraging message display for inactive periods
  - Include proper styling and responsive layout
  - Add loading and error states
  - _Requirements: 1.1, 1.2, 1.3, 1.4_

- [x] 10. Create MonthlyStatsCard component




  - Implement UI component for monthly workout counts and comparisons
  - Add trend indicators (positive/negative) with visual styling
  - Display weekly averages with proper formatting
  - Include loading states and empty data handling
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6_




- [x] 11. Create VolumeProgressCard component
  - Implement UI component for volume trends and exercise improvements
  - Add visual trend indicators and percentage displays
  - Include most improved exercise highlighting



  - Handle cases with no improvement data
  - _Requirements: 3.1, 3.2, 3.3, 3.4_

- [x] 12. Create PersonalRecordsCard component
  - Implement UI component for displaying star-marked exercise PRs
  - Add weight values with proper formatting and units
  - Include star-marking suggestions for new users
  - Handle empty PR lists gracefully
  - _Requirements: 4.1, 4.2, 4.3, 4.4_

- [x] 13. Create WeightProgressCard component




  - Implement UI component for body weight trends
  - Add arrow indicators for weight direction (up/down/stable)
  - Include weight tracking prompts for missing data
  - Display weight changes with proper formatting
  - _Requirements: 5.1, 5.2, 5.3, 5.4_

## Enhanced Calendar Integration

- [x] 14. Enhance CalendarCard with workout indicators






  - Modify existing CalendarCard to show visual workout indicators
  - Add different visual styles for workout days vs non-workout days
  - Implement tap interaction for daily workout summaries
  - Integrate with analytics repository for workout date data
  - _Requirements: 6.1, 6.2, 6.3, 6.4_

## Dashboard Integration

- [x] 15. Create analytics ViewModel for dashboard







  - Implement DashboardAnalyticsViewModel with all analytics use cases
  - Add state management for loading, success, and error states
  - Implement data refresh and caching logic
  - Handle user interactions and navigation
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 3.1, 3.2, 3.3, 3.4, 4.1, 4.2, 4.3, 4.4, 5.1, 5.2, 5.3, 5.4_

- [x] 16. Integrate analytics cards into DashboardScreen





  - Replace existing placeholder stats with new analytics cards
  - Add StreakCard and MonthlyStatsCard to dashboard layout
  - Integrate VolumeProgressCard and PersonalRecordsCard
  - Add WeightProgressCard and enhanced CalendarCard
  - Ensure proper spacing and responsive design
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 3.1, 3.2, 3.3, 3.4, 4.1, 4.2, 4.3, 4.4, 5.1, 5.2, 5.3, 5.4, 6.1, 6.2, 6.3, 6.4_

## Exercise Star-Marking Feature

- [x] 17. Implement exercise star-marking functionality












  - Add star-marking UI to exercise selection/management screens
  - Implement repository methods for updating exercise star status
  - Add star-marking suggestions in PersonalRecordsCard
  - Create user flow for marking/unmarking exercises
  - _Requirements: 4.1, 4.2, 4.3, 4.4_

## Weight Tracking Integration

- [x] 18. Implement weight history tracking



  - Add weight entry UI components to profile or dedicated screen
  - Implement weight history storage and retrieval
  - Add weight tracking prompts in WeightProgressCard
  - Create user flow for regular weight updates
  - _Requirements: 5.1, 5.2, 5.3, 5.4_

## Testing and Polish

- [x] 19. Implement comprehensive unit tests



  - Write unit tests for all analytics use cases
  - Test repository implementations with various data scenarios
  - Add ViewModel tests for state management and user interactions
  - Test edge cases and error handling
  - _Requirements: All requirements_

- [x] 20. Implement UI tests for analytics components



  - Write UI tests for all new analytics cards
  - Test user interactions and navigation flows
  - Verify responsive design and accessibility
  - Test loading states and error scenarios
  - _Requirements: All requirements_

- [x] 21. Performance optimization and final integration







  - Optimize database queries for analytics calculations
  - Implement result caching for expensive operations
  - Add background calculation for complex metrics
  - Final integration testing and bug fixes
  - _Requirements: All requirements_