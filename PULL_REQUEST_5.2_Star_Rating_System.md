# Pull Request: Feature 5.2 - Sterne-Rating-System (`InteractiveStarRating` & `RatingViewModel`)

## Zusammenfassung
Dieses Pull Request implementiert **Kapitel 5.2: Sterne-Rating-System** gemäß den **MVVM-Architekturvorgaben** in Jetpack Compose für die native Android-App *Kliq*.

Das Feature stellt eine wiederverwendbare, hoch-interaktive Sterne-Bewertungs-Komponente (1 bis 5 Sterne) bereit, unterstützt flüssige Touch- und Swipe-Gesten zur Bewertungsauswahl im Kliq High-Contrast-Design, bietet ein optionales Kommentarfeld mit Echtzeit-Zeichenbegrenzung und steuert den Sendevorgang mit reaktivem UI-State (Idle, Submitting, Success, Error).

---

## 🚀 Wichtigste Neuerungen & Merkmale

### 1. UI-Komponenten (High-Contrast Kliq Design)
- **`InteractiveStarRating.kt`**: Wiederverwendbare 1-5 Sterne Komponente.
  - **Tipp- & Swipe-Gesten**: Dynamische Echtzeit-Berechnung des Sternwerts über `pointerInput` (`detectTapGestures` & `detectDragGestures`).
  - **Visuelle Rückmeldung**: Bouncy Scale-Animationen (`animateFloatAsState`) und leuchtend goldener/violette Akzentfarben im Kliq High-Contrast-Farbschema.
  - **Flexibilität**: Unterstützt Read-Only-Modus und anpassbare Stern-Größen.
- **`RatingBottomSheet.kt`**: Interaktives Modal/Bottom Sheet zur Bewertungsabgabe.
  - Integration der `InteractiveStarRating`-Komponente.
  - `OutlinedTextField` für optionales Text-Review mit Zeichenbegrenzung (max. 300 Zeichen) und verbleibender Zeichenanzeige.
  - Senden-Button mit integriertem Lade-State (`CircularProgressIndicator`) und Erfolgs-State.
  - **Validierung**: Mindestbewertung von 1 Stern. Der Absende-Button ist deaktiviert, solange 0 Sterne ausgewählt sind.

### 2. ViewModel & Architektur (MVVM)
- **`RatingViewModel.kt`**: `@HiltViewModel` zur reaktiven Verwaltung des Bewertungs-UI-States (`StateFlow<RatingUiState>`).
  - Einhaltung der MVVM-Prinzipien.
  - Zustände: `Idle`, `Submitting`, `Success(Review)`, `Error(String)`.
  - Anbindung an `ReviewRepository` zur Speicherung des Datenmodells (`submitUnverifiedReview`).

### 3. Screen-Integration
- **`OtherUserProfileScreen.kt`**: Integration von `InteractiveStarRating` und Validierung in das Profil-Bewertungs-Modal für Nutzer-Reviews.

---

## 🧪 Verifikation & Tests

- **Unit Tests (`RatingViewModelTest.kt`)**:
  - Initial-State & Validierung (Button-Deaktivierung bei 0 Sternen).
  - Sternauswahl-Logik (1–5 Sterne).
  - Text-Eingabe und Zeichenbegrenzung auf 300 Zeichen.
  - Erfolgreiches und fehlerhaftes Absenden via `ReviewRepository`.
  - State-Resetting.
- **UI Tests (`InteractiveStarRatingTest.kt` & `StarRatingSystemE2ETest.kt`)**:
  - Rendering aller 5 Sterne.
  - Klick- und Drag-Interaktion zur Sternauswahl.
  - Verifikation des Deaktivierungs-States und der Error/Success Banner im `RatingBottomSheet`.

---

## 📋 Finale GitHub Pull-Request Qualitäts-Checkliste

- [x] **MVVM-Konformität geprüft**: Trennung von UI und Logik über `RatingViewModel` & `StateFlow` verifiziert.
- [x] **UI-Design im Kliq-Lila-Schema verifiziert**: High-Contrast Dunkelmodus mit Lila-/Gold-Akzenten eingehalten.
- [x] **Unit-/UI-Tests erfolgreich durchgelaufen**: `RatingViewModelTest` (100% Passed) & `StarRatingSystemE2ETest` grün.
- [x] **Repository-Anbindung & Validierungslogik getestet**: Mindestbewertung (1 Stern) & 300-Zeichen-Limit gegengeprüft.
- [x] **Code-Cleanliness & Dokumentation im Code geprüft**: Keine Speicher- oder Performance-Fallen, saubere Struktur.

---

## 📌 Atomare Commit-Historie

1. `feat: add RatingViewModel and reactive state flow`
2. `ui: add interactive star rating component with gesture support`
3. `ui: add rating bottom sheet with text review input and submit validation`
4. `feat: integrate InteractiveStarRating into profile rating sheet`
5. `test: add unit and UI tests for star rating system`
6. `docs: add pull request documentation and QA checklist for feature 5.2`
7. `test: add QA test scenario, E2E test suite and test bench screen`
8. `docs(review): add lead architect code review and final PR checklist`
