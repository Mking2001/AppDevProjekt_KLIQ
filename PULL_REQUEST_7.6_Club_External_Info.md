# Pull Request: Kapitel 7.6 - Integration von externen Club-Infos (Öffnungszeiten)

## Overview
Dieser PR implementiert **Kapitel 7.6 (Integration von externen Club-Infos & Öffnungszeiten)** für die native Kliq Android-App in Kotlin und Jetpack Compose. Die Funktion bereichert das Club-Datenmodell, die Repositories (Room-Persistenz & Remote API Mapping) sowie die Club-Detailansicht um strukturierte externe Club-Informationen (Wochentags-Öffnungszeiten, Live-Status-Berechnung, Adresse, Kontaktdaten und Intents für externe Links wie Website/Telefon) unter strikter Einhaltung des Kliq Lila/Dark-Mode High-Contrast Designs.

## Key Changes

- **Domain & Data Layer**:
  - **Datenmodelle**: Definition von `LiveOpeningStatus` (`OPEN_NOW`, `CLOSING_SOON`, `CLOSED`), `DaySchedule`, `ClubContactInfo` und Erweiterung von `OperatingHours` sowie `Club` in [ClubModels.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/data/model/ClubModels.kt).
  - **Room-Persistenz & Migration**: Erweiterung von [ClubEntity.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/data/local/entities/ClubEntity.kt) um externe Kontaktfelder (`phoneNumber`, `contactEmail`, `instagramHandle`). Erhöht die `KliqDatabase`-Version von 18 auf 19 in [KliqDatabase.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/data/local/KliqDatabase.kt) und fügt `MIGRATION_18_19` in [DatabaseMigrations.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/data/local/DatabaseMigrations.kt) hinzu.
  - **Repository**: Aktualisierung der Mapping-Logik `ClubEntity.toDomain()` in [ClubRepositoryImpl.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/data/repository/ClubRepositoryImpl.kt).

- **Logic & ViewModel Layer**:
  - **Öffnungszeiten-Hilfsfunktion**: Implementierung von [OpeningHoursHelper.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/util/OpeningHoursHelper.kt) zur Live-Status-Ermittlung (*Jetzt geöffnet*, *Schließt bald*, *Geschlossen*) basierend auf Systemzeit, Wochentag und Tages-Öffnungszeiten.
  - **ViewModel & State**: Definition von [ClubExternalInfoUiState.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/viewmodel/ClubExternalInfoUiState.kt) und `@HiltViewModel` [ClubExternalInfoViewModel.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/viewmodel/ClubExternalInfoViewModel.kt) für reaktive StateFlows und Intent-Formatierungen.

- **High-Contrast Jetpack Compose UI**:
  - **Modularer Info-Block**: [ClubExternalInfoBlock.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/components/ClubExternalInfoBlock.kt) im Kliq Dark-Mode Lila Schema mit farbkodiertem Live-Status-Badge, ausklappbarem Wochentagsplan, Adressanzeige und interaktiven Action-Buttons für Website-Browser, Telefon-Dialer und Kartennavigation via System-Intents.
  - **Integration**: Einbindung in [ClubDetailScreen.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/screens/club/ClubDetailScreen.kt).

- **Unit Tests & Automation**:
  - [OpeningHoursHelperTest.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/test/java/com/kliq/app/util/OpeningHoursHelperTest.kt) und [ClubExternalInfoViewModelTest.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/test/java/com/kliq/app/viewmodel/ClubExternalInfoViewModelTest.kt).
  - Automatisierte Test-Skripte [test_club_external_info.ps1](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/test_club_external_info.ps1).

## Atomic Commits
1. `feat(data): extend club data model, room entity and migration for external club info`
2. `feat(logic): add OpeningHoursHelper for live status calculation and ClubExternalInfoViewModel`
3. `feat(ui): add high-contrast ClubExternalInfoBlock component and integrate into ClubDetailScreen`
4. `test(external-info): add unit tests for OpeningHoursHelper, ViewModel and test_club_external_info.ps1`
5. `docs(external-info): add code review audit, QA checklist, test scenario, and PR description for chapter 7.6`

## Verification & Quality Assurance
- [x] MVVM-Architektur strikt eingehalten (Data -> Repository -> ViewModel -> Compose View).
- [x] 100% Kliq Lila High-Contrast Dark Mode konform.
- [x] Live-Status Logik (*Jetzt geöffnet*, *Schließt bald*, *Geschlossen*) verifiziert.
- [x] Externe Intents (Website Browser, Telefon Dialer, Map Geo-Navigation) sicher angebunden.
- [x] Room Datenbankmigration v19 erfolgreich verifiziert.
- [x] Unit-Tests vollständig bestanden (`.\gradlew testDebugUnitTest`).
