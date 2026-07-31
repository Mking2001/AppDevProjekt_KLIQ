# Pull Request: Kapitel 7.1 - Nutzerdaten-Aggregation Geschlechterverhältnis

## Overview
This PR implements **Chapter 7.1 (User Data Aggregation for Gender Ratio in Clubs/Venues)** for the native Kliq Android application in Kotlin and Jetpack Compose. It introduces privacy-compliant data aggregation of verified user check-ins at nightlife locations, asynchronous StateFlow state management in `ClubAnalyticsViewModel`, and high-contrast Kliq Purple UI visualization models.

## Key Changes

- **Data Layer & Room Database**:
  - Added `Gender` enum (`com.kliq.app.data.model.Gender`) supporting `MALE`, `FEMALE`, `DIVERSE`, `OTHER`, `PREFER_NOT_TO_SAY`, and `UNSPECIFIED`.
  - Created `GenderRatio` domain model (`com.kliq.app.data.model.GenderRatio`) with percentage calculation logic, division-by-zero protection, and minimum privacy threshold (`MIN_PRIVACY_THRESHOLD = 5`).
  - Extended `UserEntity` with `gender: String = "UNSPECIFIED"` column.
  - Added Room database migration `MIGRATION_16_17` in `DatabaseMigrations.kt` and bumped `KliqDatabase` version to `17`.
  - Added SQL aggregation queries (`getGenderCountsForClub`, `getVerifiedLogsForClub`) in `VisitedLogDao.kt`.

- **Repository Layer**:
  - Extended `ClubRepository` interface and `ClubRepositoryImpl` with `getClubGenderRatio(clubId: String)` and `calculateClubGenderRatio(clubId: String)`.
  - Ensured asynchronous processing off the main thread using Kotlin Coroutines (`Dispatchers.IO` / `ioDispatcher`).
  - Enforced strict GDPR/privacy compliance by only returning aggregated percentage proportions and masking individual user metrics when check-in counts are below the privacy threshold.

- **ViewModel Layer & UI State Preparation**:
  - Implemented `@HiltViewModel` `ClubAnalyticsViewModel` managing state flow (`StateFlow<ClubAnalyticsUiState>`).
  - Created `ClubAnalyticsUiState` and `GenderBarSegment` models for high-contrast Kliq Lila-Style bar chart rendering (`FuchsiaTertiary` `#D946EF`, `PurplePrimary` `#7C3AED`, `TealSecondary` `#14B8A6`).

- **Unit & Integration Test Suite**:
  - Added `GenderAggregationUnitTest.kt` verifying `GenderRatio` calculations, privacy threshold enforcement, and enum mappings.
  - Added `ClubAnalyticsViewModelTest.kt` verifying asynchronous state flow updates and UI segment formatting.
  - Added `GenderAggregationIntegrationTest.kt` simulating 20 verified check-ins (10x female, 8x male, 2x diverse -> 50% W / 40% M / 10% D) and edge cases (0 check-ins, low check-in count privacy masking).

## Atomic Commits
1. `feat(data): add Gender enum, GenderRatio domain model and DB migration 16->17`
2. `feat(repository): implement privacy-compliant gender ratio aggregation in ClubRepository`
3. `feat(viewmodel): implement ClubAnalyticsViewModel and UI state preparation in Kliq Purple theme`
4. `test(analytics): add unit tests for gender aggregation and ClubAnalyticsViewModel`

## Verification & Quality Assurance
- [x] Room Database Migration 16 -> 17 passes and preserves existing user records.
- [x] Privacy threshold prevents exposing individual user data when check-in count is under 5.
- [x] Asynchronous execution confirmed off the main thread using Kotlin Coroutines.
- [x] All 10 unit and integration tests pass successfully (`.\gradlew testDebugUnitTest`).
