# Pull Request: Kapitel 5.4 - Anzeige des durchschnittlichen Ratings pro Nutzer

**Branch:** `feature/user-average-rating-display-mvvm` ➔ `main`  
**PR-Link:** [Pull Request auf GitHub erstellen](https://github.com/Mking2001/AppDevProjekt_KLIQ/pull/new/feature/user-average-rating-display-mvvm)

---

## 📌 Übersicht & Zielstellung

Dieser Pull Request implementiert die reaktive Daten-Aggregation und visuelle Anzeige des **durchschnittlichen Nutzer-Ratings** für die Kliq Mobile-App gemäß **Kapitel 5.4** der technischen Spezifikation nach dem MVVM-Muster.

Das System aggregiert mathematisch präzise alle verifizierten Nutzerbewertungen (1 bis 5 Sterne) aus der lokalen Room-Datenbank off-main-thread (`Dispatchers.IO`) und stellt den errechneten Durchschnittswert sowie die Gesamtanzahl der verifizierten Reviews reaktiv im Nutzerprofil bereit.

---

## 🛠 Umgesetzte Änderungen

### 1. Daten-Aggregation & Domain-Model
- **`UserReputationSummary.kt`**: Domain-Modell für aggregierte Reputationswerte (`targetUserId`, `averageRating`, `totalReviewsCount`, `verifiedReviewsCount`, `formattedAverageRating`).
- **`ReviewDao.kt`**: Raum-SQL-Aggregationsabfragen:
  - `SELECT AVG(rating) FROM reviews WHERE targetUserId = :targetUserId`
  - `SELECT COUNT(*) FROM reviews WHERE targetUserId = :targetUserId AND isVerified = 1`
  - `SELECT COUNT(*) FROM reviews WHERE targetUserId = :targetUserId`

### 2. Repository Layer
- **`UserRepository.kt` & `UserRepositoryImpl.kt`**: Implementierung der reaktiven Reputationsabfrage `getUserReputationSummary(userId: String): Flow<UserReputationSummary>` unter Nutzung von `combine` und `ioDispatcher`.

### 3. MVVM ViewModel State Wiring
- **`ProfileViewModel.kt` & `ProfileUiState`**: Einbinden der Reputations-Attribute (`averageRating`, `formattedAverageRating`, `totalReviewsCount`, `verifiedReviewsCount`, `hasRatings`) in `ProfileUiState`.
- Asynchrones Laden off-main-thread über `viewModelScope.launch`.

### 4. UI Component & Screen Integration
- **`UserRatingStarBar.kt`**: Wiederverwendbare Jetpack Compose UI-Komponente für grafische Sternenanzeigen (Voll-, Halb- und Outlined-Sterne) mit prominenter numerischer Anzeige im Kliq Dark-Mode (Lila Accent `#7C3AED` & Gold Sternen-Glow `#FFFFC107`).
- **`ProfileScreen.kt`**: Integration des `UserRatingStarBar`-Badge im `ProfileHeader`.

### 5. Tests & QA-Dokumentation
- **Unit-Tests**: `UserRatingAggregationTest.kt`.
- **QA-Checkliste**: [QA_Checklist_User_Average_Rating.md](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/QA_Checklist_User_Average_Rating.md).

---

## 📋 Commit-Historie

1. `feat(database): add SQL aggregation query and repository methods for verified user average rating`
2. `feat(viewmodel): observe user average rating and review count off main thread in ProfileViewModel`
3. `feat(ui): implement UserRatingBadge and embed average rating star display into ProfileScreen`
4. `test(rating): add UserRatingAggregationTest, QA checklist and PR documentation for Kapitel 5.4`

---

## 🧪 Verifizierung

- `./gradlew testDebugUnitTest --tests "com.kliq.app.data.repository.UserRatingAggregationTest"` erfolgreich bestanden.
- Vollständige Einhaltung aller MVVM-, Clean-Architecture- und Entwicklungs-Regeln.
