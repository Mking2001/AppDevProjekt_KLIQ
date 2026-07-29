# Pull Request: Kapitel 5.9 - Nutzer-Reporting & Blockier-Funktion

## Zusammenfassung
Dieser Pull Request implementiert Kapitel 5.9 („Nutzer-Reporting & Blockier-Funktion“) für die nativen Mobile-App Kliq unter strikter Einhaltung der MVVM-Architektur. Die Implementierung umfasst den vollständigen Reporting-Prozess (Modal Bottom Sheet mit vordefinierten Gründen & Textfeld), die Blockier-Funktion mit Bestätigungsdialog, das High-Contrast Lila/Dark-Designsystem, die lokale Room-Datenbankerweiterung (`BlockedUserEntity`, `BlockedUserDao`, DB Migration 13->14), die synchrone Remote-Backend-Synchronisation (`KliqApiService`) sowie die automatische Reaktiv-Ausfilterung blockierter Nutzer auf der Karte, im Chat und im Discovery Feed.

## Wichtigste Änderungen

### 1. Datenhaltung & Logik (Room DB & Remote Backend)
- **Domain & Room Modell**: `BlockedUser` Domain-Modell und `BlockedUserEntity` (`@Entity(tableName = "blocked_users")`) mit zusammengesetztem Primärschlüssel `(userId, blockedUserId)`, `reason` und `blockedAtTimestampMs`.
- **Database Version 14 Migration**: In `KliqDatabase.kt` auf DB Version 14 angehoben und `MIGRATION_13_14` in `DatabaseMigrations.kt` hinterlegt.
- **DAO & Repository Pattern**: `BlockedUserDao` für reaktive Abfragen (`Flow<Boolean>`, `Flow<List<String>>`) und `UserRepositoryImpl` zur Koordination von lokalen Room DB Schreib-/Löschvorgängen und Remote-Backend Sync (`reportUser`, `blockUser`, `unblockUser` über `KliqApiService`).
- **Dependency Injection**: Bereitstellung von `BlockedUserDao` in `AppModule.kt` und Injection in `UserRepositoryImpl`.

### 2. UI & Design System Kompositionen
- **Reusable Reporting Bottom Sheet (`UserReportBottomSheet.kt`)**: Modal Bottom Sheet im Kliq High-Contrast Purple/Dark-Theme mit auswählbaren vordefinierten Meldegründen (Spam, Beleidigung, unangebrachte Inhalte, Fake-Profil), Freitext-Detailfeld und Bestätigungs-Button.
- **Reusable Block Confirmation Dialog (`BlockConfirmationDialog.kt`)**: Warnungs-AlertDialog vor dem Blockieren eines Nutzers mit Angabe der Konsequenzen und Abbrechen/Blockieren-Aktionen.
- **Profil-Integration (`OtherUserProfileScreen.kt`)**: Kontextmenü mit Optionen „Nutzer melden“ und „Nutzer blockieren“, Dynamischer Button-Zustand (Blockieren / Entblocken) und Einbindung beider Modals.
- **Chat-Detail Bar Integration (`ChatDetailScreen.kt`)**: Kontextmenü in der Chat-TopBar, Modal-Dialoge und schreibgeschützte Eingabeleiste mit Hinweis-Banner bei blockierten Nutzern.

### 3. Automatische Reaktiv-Ausfilterung in ViewModels
- **Karte (`MapViewModel.kt`)**: Automatische Filterung von User-Map-Markern blockierter Nutzer.
- **Chat-Liste (`ChatListViewModel.kt`)**: Ausfilterung von Privatchats mit blockierten Nutzern aus dem Posteingang.
- **Discovery Feed (`ExploreViewModel.kt`)**: Ausfilterung von Feed-Inhalten und Vorschlägen, die von blockierten Nutzern erstellt wurden.

### 4. Git-Strategie & Commit-Historie
Atomare Commits auf dem Feature-Branch `feature/user-reporting-blocking`:
1. `feat(data): add BlockedUser entity, dao, migration 13-14 and repository reporting methods` (`47e474f`)
2. `feat(ui): create UserReportBottomSheet and BlockConfirmationDialog components` (`040560f`)
3. `feat(ui): integrate context menu, report sheet and block dialog in profile and chat screens` (`6f207a6`)
4. `feat(filtering): filter blocked users dynamically from map, chat list and explore feed` (`93dd067`)
5. `test(reporting): add unit tests for reporting, blocking repository and viewmodel logic`
6. `docs: add PR, Code Review, QA Test Plan and QA Checklist documentation`

## Tests & Verifikation
- **Unit Tests**:
  - `UserRepositoryReportingTest`: Validierung von `isUserBlocked`, `getBlockedUserIds`, `blockUser`, `unblockUser` und `reportUser` inklusive lokaler DAO-Schreibvorgänge und Remote-Backend-Requests.
  - `OtherUserProfileViewModelTest`: Validierung von UI-Zuständen, Kontextmenü-Triggering, Bestätigungsdialogen, Blockieren/Entblocken und Reporting.
- **Verifikations-Befehl**: `$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"; ./gradlew testDebugUnitTest`
