# QA Checklist - Club-Bewertungen Filter & Sortierung

## Feature Overview
- **Feature Name**: Filter für Club-Bewertungen
- **Architecture**: MVVM with Jetpack Compose & StateFlow
- **Theme**: High-Contrast Purple Dark-Mode (`#1E1B2E` container, `#7C4DFF` violet, `#00E676` verified green)

## Test Scenario & Emulator Steps

### 1. Daten-Simulation & Test-Bench
- [x] Launch `ReviewFilterTestScreen` or navigation review section.
- [x] Click **"10 Testdaten laden"** to generate reviews spanning 1 to 5 stars and diverse verification methods (`GPS_GEOFENCE_MATCH`, `QR_CODE_SCAN`, `UNVERIFIED`).

### 2. Sterne-Filterung
- [x] Tap **"5 Sterne"**: Verify only 5-star reviews are rendered.
- [x] Tap **"4+ Sterne"**: Verify reviews with rating >= 4 are rendered.
- [x] Tap **"1 Stern"**: Verify low-rating reviews are rendered.
- [x] Tap **"Alle Sterne"**: Verify full list is displayed.

### 3. Verifizierte Besuche Toggle
- [x] Tap **"Nur Verifizierte"**: Check active state highlight with `#00E676` green badge.
- [x] Verify unverified reviews are filtered out immediately.

### 4. Sortierungs-Logik
- [x] Select **"Neueste zuerst"**: Verify timestamps descending order.
- [x] Select **"Älteste zuerst"**: Verify timestamps ascending order.
- [x] Select **"Höchste Bewertung"**: Verify 5-star items appear at top.
- [x] Select **"Niedrigste Bewertung"**: Verify 1-star items appear at top.

### 5. Edge Cases & Empty State
- [x] Click **"Empty State testen"** or select filter combination with 0 matches.
- [x] Verify high-contrast dark purple empty state box ("Keine passenden Bewertungen gefunden") is rendered cleanly without errors.
- [x] Click **"Zurücksetzen"**: Verify all filters reset to default and full list is restored instantly.
