# Requirements Document

## Introduction

This feature enhances the gym tracker dashboard with meaningful analytics and data visualization to provide users with at-a-glance insights into their workout consistency, progress, and achievements. The dashboard will display key metrics that motivate users and help them track their fitness journey without overwhelming them with details.

## Requirements

### Requirement 1

**User Story:** As a gym user, I want to see my workout consistency metrics on the dashboard, so that I can stay motivated and track my gym attendance patterns.

#### Acceptance Criteria

1. WHEN the dashboard loads THEN the system SHALL display the current workout streak in consecutive days or weeks
2. WHEN the dashboard loads THEN the system SHALL display the longest streak ever achieved
3. WHEN the dashboard loads THEN the system SHALL display days since last workout as a motivation indicator
4. IF the user has not worked out in over 3 days THEN the system SHALL display an encouraging message

### Requirement 2

**User Story:** As a gym user, I want to see my monthly workout progress compared to previous months, so that I can understand if I'm improving my consistency.

#### Acceptance Criteria

1. WHEN the dashboard loads THEN the system SHALL display total workouts completed this month
2. WHEN the dashboard loads THEN the system SHALL display total workouts from the previous month for comparison
3. WHEN displaying monthly comparison THEN the system SHALL show the difference as a positive or negative indicator
4. IF current month workouts exceed previous month THEN the system SHALL display a positive trend indicator
5. WHEN the dashboard loads THEN the system SHALL display average workouts per week for the current month
6. WHEN calculating weekly average THEN the system SHALL use completed weeks in the current month or show projected average if month is incomplete

### Requirement 3

**User Story:** As a gym user, I want to see my volume and exercise progress trends, so that I can understand if I'm getting stronger over time.

#### Acceptance Criteria

1. WHEN the dashboard loads THEN the system SHALL display volume trend showing if total weight lifted is trending up or down
2. WHEN the dashboard loads THEN the system SHALL identify and display the most improved exercise from the current month
3. WHEN calculating volume trend THEN the system SHALL compare current month's total volume to previous month
4. WHEN identifying most improved exercise THEN the system SHALL calculate percentage improvement in weight or reps

### Requirement 4

**User Story:** As a gym user, I want to see my personal records for key exercises on the dashboard, so that I can quickly view my best achievements.

#### Acceptance Criteria

1. WHEN the dashboard loads THEN the system SHALL display PR values for star-marked exercises
2. WHEN displaying PR values THEN the system SHALL show only the weight value for simplicity
3. WHEN a user marks an exercise with a star THEN the system SHALL include it in dashboard PR display
4. WHEN no exercises are star-marked THEN the system SHALL suggest marking key exercises

### Requirement 5

**User Story:** As a gym user, I want to see my body weight trend on the dashboard, so that I can monitor my physical progress alongside my workout data.

#### Acceptance Criteria

1. WHEN the dashboard loads AND weight data exists THEN the system SHALL display weight trend with simple up/down arrow
2. WHEN calculating weight trend THEN the system SHALL compare current weight to weight from 30 days ago
3. IF no weight data exists THEN the system SHALL display a prompt to start tracking weight
4. WHEN weight trend is stable (within 2 lbs) THEN the system SHALL display a stable indicator

### Requirement 6

**User Story:** As a gym user, I want the calendar to show my workout history visually, so that I can see patterns in my gym attendance at a glance.

#### Acceptance Criteria

1. WHEN the dashboard loads THEN the system SHALL display a calendar showing days with workout entries
2. WHEN a day has workout data THEN the system SHALL mark that day with a visual indicator
3. WHEN displaying the calendar THEN the system SHALL show the current month by default
4. WHEN a user taps a calendar day with workouts THEN the system SHALL show a summary of that day's activities