# QA Test-Szenario & Dokumentation: Kapitel 5.4 - Anzeige des durchschnittlichen Ratings pro Nutzer

**App:** Kliq Mobile App  
**Modul:** User Reputation & Rating Display (`ProfileViewModel`, `UserRepository`, `UserRatingStarBar`)  
**Architektur:** MVVM Pattern (StateFlow, Jetpack Compose, Room SQL Aggregation, Hilt DI)

---

## 🎯 Test-Szenarien & Simulationsabläufe

### Szenario 1: Profil ohne bisherige Bewertungen (0 Reviews)
* **Ausgangssituation**: Ein Nutzer-Profil besitzt in der lokalen Room-Datenbank noch 0 abgegebene oder verifizierte Bewertungen (`reviewsCount = 0`).
* **Simulierter Ablauf**:
  1. Aufrufen des Profils eines neuen oder unbewerteten Nutzers (`loadProfileData("user_no_reviews")`).
  2. Abfrage der Reputationsdaten über `UserRepository.getUserReputationSummary("user_no_reviews")`.
* **Erwartetes Ergebnis**:
  - `ProfileViewModel` liefert den mathematischen Standardwert `averageRating = 0.0`, `formattedAverageRating = "0.0"` und `hasRatings = false`.
  - Die UI render den Platzhalter-Text `"Keine Bewertungen"` und `"Noch keine verifizierten Ratings"`.
  - Die Compose-Komponente `UserRatingStarBar` rendert fehlerfrei 5 ungefüllte/ausgegraute Sterne im Lila Dark-Mode (`#7C3AED` Accent-Border, `#1E1B2E` Card-Container).

---

### Szenario 2: Korrekte Durchschnitts-Berechnung (3 verifizierte Test-Bewertungen)
* **Ausgangssituation**: Drei verifizierte Test-Bewertungen werden für einen Nutzer in die Datenbank geschrieben:
  - Bewertung 1: **5 Sterne** (Verifizierung via `GPS_GEOFENCE_MATCH`)
  - Bewertung 2: **4 Sterne** (Verifizierung via `QR_CODE_SCAN`)
  - Bewertung 3: **4 Sterne** (Verifizierung via `GPS_GEOFENCE_MATCH`)
* **Simulierter Ablauf**:
  1. Einfügen der 3 ReviewEntities in die Datenbank (`ReviewDao.insertReviews(...)`).
  2. Das Repository führt die SQL-Aggregationsabfrage `SELECT AVG(rating)` aus (`(5 + 4 + 4) / 3.0 = 4.333333333333333`).
  3. `ProfileViewModel` sammelt den neuen `UserReputationSummary`-Stream reaktiv off-main-thread (`Dispatchers.IO`) ein.
* **Erwartetes Ergebnis**:
  - Exakt gerundeter Wert `formattedAverageRating = "4.3"`.
  - Reaktive Aktualisierung des `ProfileUiState` mit `averageRating = 4.333333333333333`, `totalReviewsCount = 3`, `verifiedReviewsCount = 3` und `hasRatings = true`.
  - Visuelles Rendering in Jetpack Compose: 4 volle Sterne + 1 halber Stern, numerisches Label `"4.3 / 5.0"` und Subtext `"3 Bewertungen (3 verifiziert)"`.

---

### Szenario 3: Formatierung & Layout bei Extremwerten
* **Ausgangssituation**:
  - **Fall A (Perfekte 5.0 Sterne)**: 10 verifizierte 5-Sterne-Bewertungen.
  - **Fall B (Sehr hohe Anzahl an Reviews)**: 9.999 verifizierte Bewertungen.
* **Simulierter Ablauf**:
  1. Rendern der `UserRatingStarBar` und Laden des Profils bei extrem hohem Datenaufkommen.
* **Erwartetes Ergebnis**:
  - Fall A rendert exakt `"5.0"` und 5 goldene Sterne (`#FFFFC107`).
  - Fall B rendert das Label `"9999 Bewertungen (9999 verifiziert)"` ohne Zeilenumbrüche oder Clipping-Fehler.
  - Kein Sprengen der UI-Layoutgrenzen, flüssige Bildrate (60/120 fps) ohne Main-Thread-Blocking.

---

## 💻 Ausführbare Test-Skripte

### 1. ViewModel & Repository Unit-Test-Skript
Das Test-Skript befindet sich in [`ProfileAverageRatingUnitTest.kt`](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/app/src/test/java/com/kliq/app/ui/screens/profile/ProfileAverageRatingUnitTest.kt).

**Ausführung via Terminal**:
```bash
cmd /c "set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr&& set PATH=%JAVA_HOME%\bin;%PATH%&& gradlew.bat testDebugUnitTest --tests com.kliq.app.ui.screens.profile.ProfileAverageRatingUnitTest"
```

### 2. UI-Instrumentierungstest-Skript für den Emulator
Das UI-Test-Skript befindet sich in [`UserAverageRatingEmulatorTest.kt`](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/app/src/androidTest/java/com/kliq/app/ui/UserAverageRatingEmulatorTest.kt).

**Ausführung auf dem Emulator**:
```bash
cmd /c "set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr&& set PATH=%JAVA_HOME%\bin;%PATH%&& gradlew.bat connectedDebugAndroidTest --tests com.kliq.app.ui.UserAverageRatingEmulatorTest"
```
