# Pull Request: Kapitel 8.1 - Long-Press Geste für Map-Marker-Quick-View

**Branch:** `feature/map-longpress-quickview` ➔ `main`  
**PR-Link:** [Pull Request auf GitHub erstellen](https://github.com/Mking2001/AppDevProjekt_KLIQ/pull/new/feature/map-longpress-quickview)

---

## 📌 Übersicht & Zielstellung

Dieser Pull Request implementiert die Funktion **"Long-Press Geste für Map-Marker-Quick-View"** gemäß Kapitel 8.1 des Kliq-Entwicklungsplans. Durch Ausführen einer Long-Press-Geste (Press-and-Hold) auf Karten-Markern (Clubs und Events) oder Nearby-Listen-Elementen wird ein leichtes haptisches Feedback ausgelöst und ein interaktives Quick-View-Panel eingeblendet, ohne dass die Haupt-Kartenansicht verlassen werden muss.

Das Quick-View-Panel zeigt essenzielle Live-Daten:
- Name der Location
- Berechnung & Anzeige der aktuellen GPS-Entfernung
- Live-Besucherstatistik & Geschlechterverhältnis (Gäste-Anzahl & prozentuale M/W-Verteilung)
- Durchschnittliches Sterne-Rating

---

## 🛠 Umgesetzte Änderungen

### 1. Daten- & ViewModel-Ebene (`MapViewModel.kt`)
- **Modell-Erweiterung (`VenueItemUi`)**: Erweiterung der UI-Repräsentation um `totalLiveVisitors: Int`, `malePercentage: Int` und `femalePercentage: Int`.
- **Repository-Anbindung**: Auslesen und Mappen der Analytics-Daten aus `Club.analytics` (`totalLiveVisitors`, `malePercentage`, `femalePercentage`) in `observeClubRepository()`.
- **Fallback-Daten**: Hinterlegung realistischer Besucher- und Geschlechterverteilungs-Testdaten in `getFallbackVenues()`.
- **Interaktions-Logik (`onMarkerLongPressed`)**: Setzen des aktiven Venue-Zustands für die Quick-View-Karten-Überlagerung und Zentrieren der Kartenkamera.

### 2. UI-Komponenten & Gestenerkennung (`MapQuickViewCard.kt` & `MapScreen.kt`)
- **Haptisches Feedback**: Auslösen von `HapticFeedbackType.LongPress` via `LocalHapticFeedback.current` bei Ausführen der Long-Press-Geste.
- **Marker-Gestenerkennung**: Einbinden von `onInfoWindowLongClick` auf den Google Maps `Marker`-Komponenten in `MapScreen.kt`.
- **Listen-Gestenerkennung**: Anbindung von `combinedClickable` mit `onLongClick` in der `VenueCard`-Komponente des `VenueBottomSheet`.
- **High-Contrast Design**: Überarbeitung von `MapQuickViewCard.kt` im Kliq Dark-Purple-Design (#8A2BE2) mit visueller Anzeige der Live-Besucherzahl und farbcodierten Geschlechterverteilungs-Balken (Blau ♂ / Pink ♀).
- **Animationen**: Flüssiges Ein- und Ausblenden über `AnimatedVisibility` mit `slideInUp` / `slideOutDown`.

### 3. Unit-Tests (`MapViewModelTest.kt`)
- `testMarkerLongPressed_triggersQuickViewAndUpdatesVenueState`: Prüft die korrekte Zustandsaktualisierung bei Auslösen der Long-Press-Geste.
- `testVenueItemUi_mapsAnalyticsGenderRatioAndVisitorCount`: Verifiziert das fehlerfreie Mapping von Live-Besucherzahlen und Geschlechterverteilungen aus den Analytics-Modellen.

---

## 📋 Commit-Historie

1. `feat(map): extend VenueItemUi and MapViewModel with live visitor count and gender ratio data`
2. `feat(ui): update MapQuickViewCard with live visitor stats and high-contrast gender ratio bar`
3. `feat(map): add marker long-press gesture recognition and haptic feedback to MapScreen`
4. `test(map): add unit tests for marker long-press gesture and analytics mapping in MapViewModelTest`
5. `docs(map): add pull request documentation for chapter 8.1 map long-press quick-view feature`

---

## 🧪 Verifizierung

- Unit-Tests in `MapViewModelTest.kt` hinzugefügt und auf Zustandskorrektheit geprüft.
- Einhaltung des Kliq High-Contrast Lila/Dark-Themes.
- Striktes Befolgen der Null-Transparenz-Regel (keine KI-Referenzen im Quellcode oder in Commits).
