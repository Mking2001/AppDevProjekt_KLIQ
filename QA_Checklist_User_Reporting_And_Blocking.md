# QA Checklist: Kapitel 5.9 - Nutzer-Reporting & Blockier-Funktion

## QA-Checkliste

- [x] **UI & Design (Kliq High-Contrast Purple/Dark Theme)**
  - [x] Kontextmenü („Nutzer melden“, „Nutzer blockieren“) in TopBar der Profil-Detailansicht anderer Nutzer integriert.
  - [x] Kontextmenü in Chat-Aktionsleiste (`ChatDetailScreen.kt`) integriert.
  - [x] Modal Bottom Sheet (`UserReportBottomSheet.kt`) mit vordefinierten Meldegründen (Spam, Beleidigung, unangebrachte Inhalte, Fake-Profil) erstellt.
  - [x] Textfeld für zusätzliche Details im Reporting Bottom Sheet verfügbar.
  - [x] Bestätigungs-AlertDialog (`BlockConfirmationDialog.kt`) vor Durchführung der Blockierung vorhanden.
  - [x] Schreibgeschützte Chat-Eingabeleiste und Hinweis-Banner bei blockierten Nutzern im Chat.

- [x] **Datenhaltung & Backend-Synchronisation**
  - [x] Room Entity `BlockedUserEntity` (`blocked_users`) mit zusammengesetztem Primärschlüssel `(userId, blockedUserId)` vorhanden.
  - [x] Room Database Version 14 Migration (`MIGRATION_13_14`) registriert.
  - [x] `BlockedUserDao` mit Flow & Suspend-Methoden in Hilt `AppModule` bereitgestellt.
  - [x] `UserRepositoryImpl` erweitert um `isUserBlocked`, `getBlockedUserIds`, `blockUser`, `unblockUser` und `reportUser`.
  - [x] `KliqApiService` DTOs (`ReportUserRequestDto`, `BlockUserRequestDto`) und Retrofit-Endpunkte integriert.

- [x] **Reaktive Systemweite Filterung**
  - [x] Blockierte Nutzer auf der Karte (`MapViewModel.kt`) automatisch ausgeblendet.
  - [x] Blockierte Privatchats im Chat-Posteingang (`ChatListViewModel.kt`) automatisch gefiltert.
  - [x] Feed-Inhalte von blockierten Nutzern im Explore Feed (`ExploreViewModel.kt`) automatisch ausgeblendet.

- [x] **Qualitätssicherung & Git-Strategie**
  - [x] Entwickelt auf dezidiertem Feature-Branch `feature/user-reporting-blocking`.
  - [x] Atomare Commits gemäß Conventional Commits Standard durchgeführt.
  - [x] Unit-Tests `UserRepositoryReportingTest` und `OtherUserProfileViewModelTest` erfolgreich ausgeführt (`BUILD SUCCESSFUL`).
  - [x] PR-Dokumentation `PULL_REQUEST_5.9_User_Reporting_And_Blocking.md` und Code-Review `CODE_REVIEW_5.9_User_Reporting_And_Blocking.md` angelegt.
