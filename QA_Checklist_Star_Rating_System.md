# QA Checklist: Feature 5.2 - Sterne-Rating-System

## Übersicht
Dieses Dokument enthält die Qualitätssicherungs-Checkliste zur Verifikation des Features **Sterne-Rating-System (Kapitel 5.2)** der Kliq Mobile App.

---

## 📋 Testkriterien & Prüfpunkte

### 1. Interaktive Sterne-Komponente (`InteractiveStarRating`)
- [x] **5 Sterne Sichtbarkeit**: Alle 5 Sterne werden im High-Contrast-Design korrekt gerendert.
- [x] **Tipp-Gesten**: Beim Tippen auf den *N*-ten Stern werden exakt *N* Sterne visualisiert.
- [x] **Swipe-/Drag-Gesten**: Beim Wischen über die Sternenleiste ändert sich die Auswahl in Echtzeit entsprechend der Fingerposition.
- [x] **Scale-Animation**: Sterne skalieren sanft bei Interaktion (`animateFloatAsState`).
- [x] **Read-Only Modus**: Bei `isReadOnly = true` sind keine Gesteninteraktionen möglich.

### 2. Bewertungs-Bottom-Sheet & Validierung (`RatingBottomSheet`)
- [x] **Mindestbewertung (1 Stern)**:
  - Bei **0 Sternen**: Senden-Button ist deaktiviert und zeigt Hinweistext *"Stern auswählen zum Absenden"*.
  - Bei **1–5 Sternen**: Senden-Button wird aktiviert und zeigt *"Bewertung absenden"*.
- [x] **Kommentarfeld & Zeichenbegrenzung**:
  - Maximale Zeichenanzahl ist auf **300 Zeichen** beschränkt.
  - Verbleibende/Eingegebene Zeichen werden in Echtzeit aktualisiert (*"X / 300 Zeichen"*).
- [x] **Lade- & Erfolgs-Status**:
  - Während des Sendevorgangs wird ein `CircularProgressIndicator` im Button angezeigt.
  - Nach erfolgreichem Senden wird eine Erfolgsanzeige mit Bestätigungs-Button gerendert.
  - Bei Netzwerk-/Speicherfehlern wird eine prägnante Fehlermeldung eingeblendet.

### 3. MVVM & State Management (`RatingViewModel`)
- [x] **StateFlow**: Der UI-Zustand wird reaktiv über `RatingUiState` gesteuert.
- [x] **Repository Integration**: Abgegebene Bewertungen werden sauber im `ReviewRepository` persistiert (`Review`-Modell mit Rating, AuthorId, TargetUserId, Timestamp, Text).
- [x] **State Reset**: Bei Abbruch oder nach erfolgreichem Absenden wird der Zustand zurückgesetzt.

---

## 🧪 Test-Ausführung & Ergebnisse

| Test-Suite | Befehl | Ergebnis |
| :--- | :--- | :--- |
| **ViewModel Unit Tests** | `./gradlew testDebugUnitTest --tests "com.kliq.app.viewmodel.RatingViewModelTest"` | **PASSED** (100%) |
| **Compose UI Tests** | `./gradlew connectedAndroidTest --tests "com.kliq.app.ui.components.InteractiveStarRatingTest"` | **PASSED** |
| **Build-Verifikation** | `./gradlew assembleDebug` | **SUCCESS** |
