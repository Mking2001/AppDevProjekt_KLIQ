# Pull Request: Kapitel 5.5 - Kommentarsektion für Bewertungen

**Branch:** `feature/review-comments-section-mvvm` ➔ `main`  
**PR-Link:** [Pull Request auf GitHub erstellen](https://github.com/Mking2001/AppDevProjekt_KLIQ/pull/new/feature/review-comments-section-mvvm)

---

## 📌 Übersicht & Zielstellung

Dieser Pull Request implementiert die **verifizierte Kommentarsektion für Nutzerbewertungen** für die Kliq Mobile-App gemäß **Kapitel 5.5** der technischen Spezifikation nach dem MVVM-Muster.

Schriftliche Kommentare sind fest an verifizierte Sterne-Bewertungen gekoppelt und unterliegen derselben strikten Anti-Spam Logik-Sperre (Freischaltung ausschließlich bei physischer Nähe via GPS-Match oder persönlichem QR-Code-Scan). Das System unterstützt eine Zeichenbegrenzung von maximal 280 Zeichen mit Echtzeit-Eingabe-Validierung.

---

## 🛠 Umgesetzte Änderungen

### 1. Datenhaltung & Model Erweiterung
- **`ReviewEntity.kt` / `ReviewDao.kt`**: Raum-SQL Abfragen für verifizierte Nutzerkommentare (`getReviewsForTargetUser`).
- **`ReviewRepository.kt` & `ReviewRepositoryImpl.kt`**: Implementierung der Methode `submitVerifiedUserComment(reviewerUserId, targetUserId, rating, text, verificationMethod, qrToken)` mit harter Sicherheitsprüfungs-Logik im Data-Layer.

### 2. MVVM ViewModel & Anti-Spam Validierung
- **`ReviewViewModel.kt` & `ReviewUiState`**:
  - Echtzeit-Zeichenzähler (`remainingCharacters` bei max 280 Zeichen limit).
  - Anti-Spam Sperr-Zustand (`isVerificationLocked`, `activeVerificationMethod`).
  - Ladezustände (`isSectionEmpty`, `isSubmitting`, `submitSuccessMessage`, `errorMessage`).

### 3. UI/UX Design (Jetpack Compose Dark Mode)
- **`ReviewCommentCard.kt`**: Einzelansicht verifizierter Kommentare mit Avatar, Name des Verfassers, Zeitstempel, Sternenanzeige und Verifizierungs-Badge ("GPS-Match" / "QR-Scan").
- **`ReviewCommentInputCard.kt`**: Eingabekarte mit Zeichenzähler, Sterneauswahl (1-5 Sterne), Sperr-Banner bei unverifizierten Versuchen und Absenden-Button.
- **`ReviewCommentSection.kt`**: Komplette Sektion für das Nutzerprofil.

### 4. Tests & QA-Dokumentation
- **Unit-Tests**: `ReviewCommentsSectionUnitTest.kt`.
- **QA-Checkliste**: [QA_Checklist_Review_Comments_Section.md](file:///c:/Users/Alexa/Desktop/AppDevProjekt_KLIQ/QA_Checklist_Review_Comments_Section.md).

---

## 📋 Commit-Historie

1. `feat(database): extend review comment model and DAO queries`
2. `feat(viewmodel): implement review comment loading, character limit, and anti-spam validation`
3. `feat(ui): implement ReviewCommentList and ReviewInputCard in Kliq Dark Mode`
4. `test(review): add unit tests, QA checklist and PR documentation for Kapitel 5.5`

---

## 🧪 Verifizierung

- `./gradlew testDebugUnitTest --tests "com.kliq.app.viewmodel.ReviewCommentsSectionUnitTest"` erfolgreich bestanden.
- Strikte Einhaltung aller MVVM, Anti-Spam und Entwicklungs-Regeln.
