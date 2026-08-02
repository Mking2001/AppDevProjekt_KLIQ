# Pull Request: Kapitel 7.3 - Info-Block für spezielle Events und Angebote

## Overview
Dieser PR implementiert **Kapitel 7.3 (Info-Block für spezielle Events und Angebote in der Club-Detailansicht)** für die native Kliq Android-App in Kotlin und Jetpack Compose. Die Funktion bietet eine ansprechende Darstellung aktueller Partys, Special-Deals und VIP-Aktionen unter strikter Einhaltung des Kliq Lila/Dark-Mode High-Contrast Designs.

## Key Changes

- **Domain & Data Layer**:
  - **Datenmodelle**: Definition von `ClubOffer`, `ClubEvent`, `OfferType` (`SPECIAL_DEAL`, `VIP_ACTION`, `DRINK_SPECIAL`, `ENTRY_DISCOUNT`) und `EventCategory` (`PARTY`, `LIVE_SHOW`, `DJ_SET`, `FESTIVAL`) in [ClubEventOfferModels.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/data/model/ClubEventOfferModels.kt).
  - **Room-Persistenz & Migration**: Erstellt [ClubOfferEntity.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/data/local/entities/ClubOfferEntity.kt) und [ClubOfferDao.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/data/local/dao/ClubOfferDao.kt). Erhöht die `KliqDatabase`-Version auf 18 und fügt `MIGRATION_17_18` in [DatabaseMigrations.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/data/local/DatabaseMigrations.kt) hinzu.
  - **Repository**: Implementierung von [ClubEventOfferRepository.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/data/repository/ClubEventOfferRepository.kt) & [ClubEventOfferRepositoryImpl.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/data/repository/ClubEventOfferRepositoryImpl.kt) inklusive Hilt-Binding in [RepositoryModule.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/di/RepositoryModule.kt).

- **ViewModel Layer & StateFlow**:
  - Definition von [ClubEventOfferUiState.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/viewmodel/ClubEventOfferUiState.kt).
  - Implementierung des `@HiltViewModel` [ClubEventOfferViewModel.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/viewmodel/ClubEventOfferViewModel.kt) zur Steuerung von Events, Specials, Modal-Zuständen und Rabattcode-Kopier-Events.

- **High-Contrast Jetpack Compose UI**:
  - **Modularer Info-Block**: [ClubEventOfferInfoBlock.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/components/ClubEventOfferInfoBlock.kt) mit TabRow (*"Specials & Deals"*, *"Partys & Events"*), ausklappbaren Beschreibungen (`AnimatedVisibility`) und High-Contrast Farbverläufen (`PurplePrimary` `#7C3AED` bis `FuchsiaTertiary` `#D946EF`).
  - **Modal Bottom Sheet**: [ClubOfferDetailBottomSheet.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/components/ClubOfferDetailBottomSheet.kt) mit prominenter Rabattcode-Anzeige und Ein-Klick-Kopierfunktion in die Zwischenablage.
  - **Integration**: Einbindung in [ClubDetailScreen.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/screens/club/ClubDetailScreen.kt).

- **Unit Tests**:
  - [ClubEventOfferViewModelTest.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/test/java/com/kliq/app/viewmodel/ClubEventOfferViewModelTest.kt) und [ClubEventOfferRepositoryTest.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/test/java/com/kliq/app/data/repository/ClubEventOfferRepositoryTest.kt).

## Atomic Commits
1. `82cba92` `feat(data): add ClubEvent and ClubOffer data models, Room entity, DAO, and DB migration v18`
2. `be0cce5` `feat(repository): add ClubEventOfferRepository implementation and Hilt DI module binding`
3. `23b5d08` `feat(viewmodel): add ClubEventOfferViewModel and reactive state flow ClubEventOfferUiState`
4. `dcab37e` `feat(ui): add ClubEventOfferInfoBlock and ClubOfferDetailBottomSheet components to ClubDetailScreen`
5. `7d460cb` `test(club-event-offers): add unit tests for ClubEventOfferViewModel and ClubEventOfferRepository`

## Verification & Quality Assurance
- [x] MVVM-Architektur strikt eingehalten (Data -> Repository -> ViewModel -> Compose View).
- [x] 100% Kliq Lila High-Contrast Dark Mode konform.
- [x] Room-Persistenz & Datenbankmigration v18 erfolgreich verifiziert.
- [x] Unit-Tests vollständig bestanden (`.\gradlew testDebugUnitTest`).
