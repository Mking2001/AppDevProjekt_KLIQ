# Pull Request: Kapitel 7.4 - Suchfunktion für Clubs und Regionen

## Overview
Dieser PR implementiert **Kapitel 7.4 (Suchfunktion für Clubs und Regionen)** für die native Kliq Android-App in Kotlin und Jetpack Compose. Die Funktion ermöglicht das reaktive Suchen und Filtern von Clubs, Locations, Städten und Regionen mit Live-Suche (300ms Debounce), Filter-Badges und visuellen Platzhaltern für Empty/Loading-States unter strikter Einhaltung des Kliq Lila/Dark-Mode High-Contrast Designs.

## Key Changes

- **Domain & Data Layer**:
  - **Datenmodelle**: Definition von `SearchFilterType`, `RegionSearchResult` und `ClubSearchResult` in [ClubSearchModels.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/data/model/ClubSearchModels.kt).
  - **Room-DAO Abfragen**: Erweitert `ClubDao` in [ClubDao.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/data/local/dao/ClubDao.kt) um gefilterte Suchabfragen (`searchClubsFiltered`, `searchDistinctRegionsAndCities`).
  - **Repository**: Erweiterung von [ClubRepository.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/data/repository/ClubRepository.kt) & [ClubRepositoryImpl.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/data/repository/ClubRepositoryImpl.kt) um gefilterte lokale und Backend-Suchabfragen.

- **ViewModel Layer & StateFlow**:
  - Definition von [ClubSearchUiState.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/viewmodel/ClubSearchUiState.kt).
  - Implementierung des `@HiltViewModel` [ClubSearchViewModel.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/viewmodel/ClubSearchViewModel.kt) mit 300ms Debouncing (`debounce(300L)`), reaktivem Flow-Combining (`combine`, `flatMapLatest`) und Umkreissuche / Entfernungsberechnung per GPS.

- **High-Contrast Jetpack Compose UI**:
  - **Suchleiste**: [ClubSearchBar.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/components/search/ClubSearchBar.kt) mit Lupe-Icon, Clear-Button (x) und violettem Fokus-Border.
  - **Filter Badges**: [ClubSearchFilterBadges.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/components/search/ClubSearchFilterBadges.kt) mit Filter-Chips (*"Alle"*, *"Nach Name"*, *"Nach Region/Stadt"*, *"Nach Genre/Vibe"*).
  - **Ergebnisliste**: [ClubSearchResultList.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/components/search/ClubSearchResultList.kt) mit visueller Trennung in *"Städte & Regionen"* und *"Clubs & Locations"*.
  - **Empty & Loading States**: [ClubSearchEmptyState.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/components/search/ClubSearchEmptyState.kt) und [ClubSearchLoadingState.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/components/search/ClubSearchLoadingState.kt).
  - **Screen & Routing**: [ClubSearchScreen.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/screens/search/ClubSearchScreen.kt) und Routen-Registrierung in [NavigationRoute.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/navigation/NavigationRoute.kt).

- **Unit Tests & Automation**:
  - [ClubSearchViewModelTest.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/test/java/com/kliq/app/viewmodel/ClubSearchViewModelTest.kt) und [ClubRepositorySearchTest.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/test/java/com/kliq/app/data/repository/ClubRepositorySearchTest.kt).
  - Automated PowerShell Test Script [test_club_region_search.ps1](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/test_club_region_search.ps1).

## Atomic Commits
1. `feat(data): add search data models, Room DAO queries, and ClubRepository search extensions`
2. `feat(viewmodel): add ClubSearchViewModel and reactive state flow ClubSearchUiState`
3. `feat(ui): add high-contrast search bar, filter badges, result list, empty state, and ClubSearchScreen`
4. `test(search): add unit tests for ClubSearchViewModel and ClubRepository, add test_club_region_search.ps1`
5. `docs(search): add code review audit, QA test scenario, and PR description for chapter 7.4`

## Verification & Quality Assurance
- [x] MVVM-Architektur strikt eingehalten (Data -> Repository -> ViewModel -> Compose View).
- [x] 100% Kliq Lila High-Contrast Dark Mode konform.
- [x] Debounced Live-Suche (300ms) erfolgreich verifiziert.
- [x] Umkreissuche & GPS-Distanzberechnung integriert.
- [x] Unit-Tests vollständig bestanden (`.\gradlew testDebugUnitTest`).
