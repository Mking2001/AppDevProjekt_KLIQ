# Emulator-Test-Skript: Kapitel 6.6 - Medien-Versand (Fotos in Chats)

Dieses Test-Skript beschreibt die Schritt-für-Schritt-Ausführung zur Überprüfung des Medien-Versands im Android-Emulator.

## Vorbereitung & Voraussetzungen
1. Android-Emulator starten (API 33 oder 34).
2. ADB-Verbindung herstellen:
   ```bash
   adb devices
   ```
3. Test-Bild auf den Emulator pushen:
   ```bash
   adb push ./app/src/main/res/drawable/ic_launcher_background.xml /sdcard/Pictures/sample_test_image.png
   ```

## Test-Szenarien

### Szenario 1: Galerie-Bild auswählen und mit Bildunterschrift senden
1. Kliq-App öffnen und in einen beliebigen Chat navigieren (z. B. `priv_1` oder `pub_1`).
2. Auf das **Anhang-Icon (Büroklammer)** in der Eingabeleiste tippen.
3. Im erscheinenden Bottom-Sheet **"Galerie"** auswählen.
4. Test-Bild im nativen System Photo Picker auswählen.
5. Verifizieren, dass das **Foto-Vorschau-Modal** erscheint.
6. Bildunterschrift eingeben: `"Test-Foto aus Galerie"`.
7. Auf **"Senden"** tippen.
8. **Ergebnis-Prüfung**:
   - Lade-Spinner erscheint kurz über dem Bild während der Komprimierung.
   - Bild-Sprechblase wird im Kliq Dark/Purple-Design mit abgerundeten Ecken gerendert.
   - Bildunterschrift steht unter dem Foto.

### Szenario 2: Vollbild-Ansicht durch Antippen öffnen
1. In der Chat-Nachrichtenliste auf das soeben gesendete Foto tippen.
2. **Ergebnis-Prüfung**:
   - `FullscreenImageViewerDialog` öffnet sich auf schwarzem Hintergrund.
   - Oben rechts befindet sich der Schließen-Button (X).
3. Auf den Schließen-Button tippen -> App kehrt flüssig zum Chat zurück.

### Szenario 3: Kamera-Aufnahme & Abbrechen-Test
1. Auf das Büroklammer-Icon tippen -> **"Kamera"** wählen.
2. Foto aufnehmen und bestätigen.
3. Im Vorschau-Modal auf **"Abbrechen"** oder das **"X"** oben rechts tippen.
4. **Ergebnis-Prüfung**: Das Modal schließt sich und es wird keine Nachricht gesendet.
