# Implementation Plan

- [ ] 1. Repository Layer Enhancement
  - Enhance ExerciseInstanceRepository with workout-specific queries
  - Add methods for retrieving exercise instances by workout ID
  - Implement proper Flow-based data loading for reactive updates
  - _Requirements: 1.1, 3.1, 3.3_

- [ ] 2. WorkoutSet Repository Integration
  - Create or enhance WorkoutSetRepository for set data persistence
  - Implement CRUD operations for workout sets linked to exercise instances
  - Add methods for retrieving sets by exercise instance ID
  - _Requirements: 2.1, 2.2, 3.2_

- [ ] 3. ViewModel Dependency Injection Setup
  - Inject ExerciseInstanceRepository and WorkoutSetRepository into WorkoutExercisesViewModel
  - Inject ExerciseRepository for exercise metadata
  - Inject WorkoutRepository for workout name resolution
  - Remove static storage companion object
  - _Requirements: 3.1, 3.3, 6.1_

- [ ] 4. Database-Based Exercise Loading
  - Replace generateSampleWorkoutData() with repository-based data loading
  - Implement loadWorkoutExercises() using exerciseInstanceRepository.getExerciseInstancesByWorkoutId()
  - Transform database entities to current UI models for compatibility
  - Add proper error handling for database operations
  - _Requirements: 1.2, 1.3, 5.3, 6.2_

- [ ] 5. Exercise Addition Persistence
  - Replace in-memory exercise addition with database insertion
  - Implement addExerciseToWorkout() using exerciseInstanceRepository.insertExerciseInstance()
  - Generate proper ExerciseInstance entities with correct workout relationships
  - Ensure immediate UI updates after successful database operations
  - _Requirements: 1.1, 2.1, 5.1, 7.1_

- [ ] 6. Set Data Persistence Implementation
  - Replace in-memory set storage with WorkoutSet entity persistence
  - Implement addSetToExercise() using workoutSetRepository.insertWorkoutSet()
  - Implement updateSet() using workoutSetRepository.updateWorkoutSet()
  - Ensure sets are properly linked to exercise instances
  - _Requirements: 2.1, 2.2, 2.3, 7.2_

- [ ] 7. Data Model Transformation Layer
  - Create transformation functions from database entities to UI models
  - Implement WorkoutExerciseInstanceData creation from ExerciseInstance + Exercise + WorkoutSets
  - Add LastPerformedData calculation from historical workout set data
  - Maintain backward compatibility with existing UI models
  - _Requirements: 6.1, 6.2, 6.3, 7.3_

- [ ] 8. Reactive Data Loading Implementation
  - Replace static data with Flow-based reactive data streams
  - Implement proper lifecycle-aware data observation
  - Ensure UI updates automatically when database data changes
  - Add loading states during database operations
  - _Requirements: 5.1, 5.2, 6.2_

- [ ] 9. Error Handling and Recovery
  - Implement comprehensive error handling for all database operations
  - Add retry mechanisms for failed database operations
  - Create user-friendly error messages for database failures
  - Implement graceful degradation when database is unavailable
  - _Requirements: 5.3, 5.4_

- [ ] 10. Data Migration Implementation
  - Create migration utility to transfer existing in-memory data to database
  - Implement one-time migration on app startup if in-memory data exists
  - Ensure no data loss during migration process
  - Add migration success/failure logging and user feedback
  - _Requirements: 4.1, 4.2, 4.3, 4.4_

- [ ] 11. Workout Name Resolution Enhancement
  - Replace static workout name storage with database-based resolution
  - Use WorkoutRepository to get workout names by ID
  - Remove workoutNamesStorage companion object
  - Ensure workout titles display correctly from database data
  - _Requirements: 6.3, 6.4, 7.3_

- [ ] 12. Performance Optimization
  - Implement proper database query optimization with indexes
  - Add caching for frequently accessed exercise and workout data
  - Optimize UI model transformation for large datasets
  - Ensure responsive UI during database operations
  - _Requirements: 5.1, 5.2_

- [ ] 13. Integration Testing
  - Write integration tests for database persistence across app restarts
  - Test exercise and set data persistence after process termination
  - Verify data integrity across all CRUD operations
  - Test migration scenarios with various data states
  - _Requirements: 1.3, 1.4, 2.3, 4.2_

- [ ] 14. UI Compatibility Validation
  - Verify all existing UI functionality works with database persistence
  - Test workout exercise screen behavior matches current implementation
  - Ensure exercise addition, set management, and navigation work identically
  - Validate workout titles and exercise names display correctly
  - _Requirements: 6.1, 6.2, 6.3, 6.4_

- [ ] 15. Data Integrity and Validation
  - Implement data validation for exercise instance creation
  - Add constraints to prevent orphaned workout sets
  - Ensure proper foreign key relationships in database operations
  - Add data consistency checks and repair mechanisms
  - _Requirements: 7.1, 7.2, 7.3, 7.4_

- [ ] 16. Final Testing and Cleanup
  - Remove all static storage and in-memory data structures
  - Clean up debug logging and temporary migration code
  - Perform comprehensive end-to-end testing
  - Validate app behavior matches requirements across all scenarios
  - _Requirements: 1.4, 2.4, 5.4, 6.4_