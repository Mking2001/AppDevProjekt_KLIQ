# Pull Request: Kapitel 7.2 - UI-Anzeige der Live-Besucherstatistik pro Club

## Overview
This PR implements **Chapter 7.2 (UI Display of Live Visitor Statistics per Club)** for the native Kliq Android application in Kotlin and Jetpack Compose. It introduces real-time occupancy level categorization ("Schwach", "Mittel", "Voll"), pulsing live badge animations, high-contrast Lila/Dark-Mode capacity progress gauges, reactive StateFlow UI state binding in `ClubAnalyticsViewModel`, and integration into `ClubDetailScreen`.

## Key Changes

- **Domain & Data Layer Extensions**:
  - Added `OccupancyCategory` enum (`com.kliq.app.data.model.OccupancyCategory`) with threshold-based mapping (`fromPercentage`), display labels ("Schwach", "Mittel", "Voll"), status descriptions, and Kliq high-contrast color tokens.
  - Added `OccupancyTrend` enum (`com.kliq.app.data.model.OccupancyTrend`) supporting `RISING`, `STABLE`, and `FALLING`.
  - Extended `ClubAnalytics` model with `maxCapacity: Int = 1500`, `occupancyTrend`, and `lastUpdatedTimestamp`.

- **ViewModel Layer & UI State Binding**:
  - Updated `ClubAnalyticsUiState` with capacity fields (`occupancyRate`, `currentCapacityPercent`, `maxCapacity`, `occupancyCategory`, `occupancyTrend`, `formattedCapacityPercent`, `formattedVisitorCount`).
  - Extended `@HiltViewModel` `ClubAnalyticsViewModel` with `updateVisitorStats` and automated occupancy category calculations when observing or refreshing club analytics data.

- **High-Contrast Jetpack Compose UI**:
  - Created `LiveVisitorStatsCard.kt` (`com.kliq.app.ui.components.LiveVisitorStatsCard`):
    - Glowing pulsing `• LIVE` indicator badge with infinite alpha transition animation (`rememberInfiniteTransition`).
    - High-contrast card container with gradient borders (`PurplePrimary` `#7C3AED` to `FuchsiaTertiary` `#D946EF`).
    - Animated occupancy progress gauge (`animateFloatAsState`).
    - Occupancy status pill ("Schwach" / "Mittel" / "Voll") with distinct color accents (`TealSecondary`, `PurplePrimary`, `FuchsiaTertiary`).
    - Visitor stats summary ("1.420 / 1.500 Gäste vor Ort").
    - Embedded gender distribution breakdown bar.

- **Screen Integration**:
  - Updated `ClubDetailScreen.kt` to bind `ClubAnalyticsViewModel` reactively via `collectAsStateWithLifecycle()` and display `LiveVisitorStatsCard`.

- **Unit & Integration Tests**:
  - Created `ClubLiveVisitorStatsTest.kt` (`com.kliq.app.viewmodel.ClubLiveVisitorStatsTest`):
    - Verified `OccupancyCategory.fromPercentage` boundary calculations (0%, 39%, 40%, 75%, 76%, 100%).
    - Verified `ClubAnalyticsUiState` computed properties.
    - Verified `ClubAnalyticsViewModel` state updates and flow emissions.

## Atomic Commits
1. `feat(data): add OccupancyCategory and OccupancyTrend models for live stats`
2. `feat(viewmodel): update ClubAnalyticsViewModel and UiState for live visitor capacity`
3. `feat(ui): implement LiveVisitorStatsCard and integrate into ClubDetailScreen`
4. `test(analytics): add unit tests for OccupancyCategory logic and live stats state updates`
5. `docs(analytics): add code review audit, QA checklist and PR description for chapter 7.2`

## Verification & Quality Assurance
- [x] All unit tests for `ClubLiveVisitorStatsTest` and `ClubAnalyticsViewModelTest` pass cleanly.
- [x] Reactive state updates rendered smoothly without UI flicker using `StateFlow` and Compose state collection.
- [x] High-contrast Dark/Lila design conforms to Kliq UI design guidelines.
- [x] Strict compliance with Null-Transparenz-Regel.
