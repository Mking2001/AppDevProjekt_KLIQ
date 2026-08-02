# Pull Request: Implement Club Favorites System (Kapitel 7.7)

## Description
This pull request implements the complete favorite management system for clubs in the Kliq Android application according to Chapter 7.7 specifications.

### Key Changes Introduced:
- **Data & Persistence Layer**:
  - Validated and updated `ClubEntity` Room schema with `isFavorite: Boolean` persistence.
  - Implemented async `toggleFavorite` logic and `getFavoriteClubs` reactive flow streaming in `ClubRepositoryImpl` using `Dispatchers.IO`.

- **ViewModel & State Management**:
  - Injected `ClubRepository` into `ClubViewModel` to stream club detail states and toggle favorites reactively via `StateFlow`.
  - Extended `MapViewModel` with `toggleFavorite` handling to reactively refresh map marker quick views and bottom sheet lists upon state changes.

- **UI & Animations**:
  - Created `AnimatedFavoriteButton` component featuring spring scaling animations and Kliq high-contrast Purple Accent (`#8A2BE2`) styling on active state.
  - Integrated `AnimatedFavoriteButton` across `ClubDetailScreen` top app bar, `MapQuickViewCard`, and `ClubSearchResultList`.

- **Test Suite**:
  - Added unit test suites `ClubFavoriteRepositoryTest` and `ClubFavoriteViewModelTest` verifying state transformations, repository calls, and coroutine flow emissions.

## Verification
- Unit test suite passed for repository and viewmodel favorite operations.
- Dark theme high-contrast design compliance verified against Kliq brand standards.
