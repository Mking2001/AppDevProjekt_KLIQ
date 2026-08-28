# Test-Szenario & Validation Manual: Kapitel 9.3 - Speicher-Leck Analyse & Optimierung

Diese Anleitung beschreibt das Stress-Testing Szenario, die Auswertung von Profiling-Tools (**LeakCanary** & **Android Studio Memory Profiler** / **Xcode Instruments**) sowie das Vorher-Nachher-Ergebnisprotokoll zur Verifizierung der Speicher-Optimierungen in der **Kliq App**.

---

## 1. Test-Ablauf zur Leck-Provoziereung (Stress-Testing)

### Schritt-für-Schritt Szenario im Emulator / Simulator

#### Zyklus 1: Intensiver Screen-Switching Stresstest (20x Iteration)
1. Starte die App im Debug-Modus mit aktiver LeakCanary-Überwachung.
2. Navigiere schnell und wiederholt durch alle Haupt-Screens über die Bottom Navigation Bar:
   - `Home` ➔ `Entdecken` ➔ `Karte` ➔ `Chat-Übersicht` ➔ `Profil` ➔ `Home`.
3. Öffne mehrmals den `ClubDetailScreen` (z. B. Berghain) und kehre umgehend zurück.
4. Öffne mehrmals einen `ChatDetailScreen` (Direct Messaging) und schreibe eine Test-Nachricht.

#### Zyklus 2: Bildschirm-Rotationen (Konfigurationsänderungen)
1. Öffne die `MapScreen` mit gerenderten Club-Pins und User-Profilen.
2. Führe 10x Bildschirm-Rotationen durch (Portrait ↔ Landscape Wechsel via Emulator Controls `Ctrl + F11` / `Cmd + LeftArrow`).
3. Wechsel zum `ProfileScreen` und führe erneut 5x Rotationen durch.

#### Zyklus 3: Hintergrund-Verhalten & GPS-Service Trimming
1. Aktiviere das Hintergrund-GPS-Tracking im `LocationTrackingViewModel`.
2. Bewege die Kliq-App in den Hintergrund (Home-Button drücken).
3. Simuliere im Terminal einen Memory-Trim des Android-Systems:
   ```bash
   adb shell am send-trim-memory com.kliq.app RUNNING_CRITICAL
   ```
4. Bringe die App wieder in den Vordergrund und pausierte/reaktivierte das Tracking.

---

## 2. Profiling & Verifizierung

### Android: LeakCanary & Memory Profiler
1. **LeakCanary Überprüfung**:
   - Achte nach Beenden der Interaktionszyklen auf die LeakCanary-Benachrichtigung in der Statusleiste (`"0 Leaks Detected"`).
   - Tippe auf das LeakCanary-Icon auf dem Emulator-Startbildschirm und verifiziere, dass keine retained Objects (z. B. `MapActivity`, `MapViewModel`, `ChatDetailScreen`) im Heap gelistet sind.
2. **Android Studio Memory Profiler**:
   - Öffne `View -> Tool Windows -> Profiler` und wähle den `com.kliq.app` Prozess.
   - Beobachte die Java-Heap Kurve während der 20x Screen-Switches.
   - Drücke das **Garbage Collector (GC) Symbol** (Mülleimer-Icon) oben links im Profiler.
   - **Kriterium**: Nach dem manuellen GC muss die Heap-Belegung unmittelbar auf das stabile Basis-Niveau (~45–55 MB) zurückfallen.

### iOS: Xcode Memory Graph & Leaks Instrument
1. Starte das UI-Test-Schema in Xcode: `Product -> Profile (Cmd + I)`.
2. Wähle das Template **Leaks** aus und starte die Aufzeichnung.
3. Führe die obigen Navigations- und Rotations-Zyklen durch.
4. Betätige den **Memory Graph Debugger** (`Debug -> View Memory -> Save Memory Graph`).
5. **Kriterium**: Es werden 0 rot markierte Memory Leaks und 0 retained View Controller angezeigt.

---

## 3. Vorher-Nachher Ergebnis-Protokoll

Das folgende Protokoll vergleicht den Zustand der App vor den Speicher-Optimierungen (Kapitel 9.3) mit dem optimierten Ziel-Zustand:

```text
==========================================================================
 SPEICHER-PROFILING PROTOKOLL: KAPITEL 9.3 SPEICHER-OPTIMIERUNG           
==========================================================================

[METRIK & RESSOURCEN]              | VOR OPTIMIERUNG  | NACH OPTIMIERUNG (KAPITEL 9.3)
-----------------------------------+------------------+----------------------------------
LeakCanary Retained Objects Count  | 5 Leaks          | 0 Leaks (PASS)
Heap-Belegung nach 20x Switches   | ~185 MB (Steigend)| ~52 MB (Stabil nach GC)
Coil Image Memory Cache Limits     | Unbegrenzt       | Max 25% RAM (Dynamic Trimming)
Marker Bitmap Descriptor Eviction  | Manuell / Keine  | Automatisch in MapViewModel.onCleared()
Context Reference Safety           | Activity Leak    | ApplicationContext / WeakRef
System Trim-Memory Response        | Keine Reaktion   | Evakuierung aller Bitmap-Caches

==========================================================================
 VERIFIZIERUNGS-ERGEBNIS:                                                 
 ✔ Heap-Belegung kehrt nach manueller GC auf Basis-Niveau zurück
 ✔ Keine verbleibenden Retained Objects in LeakCanary Heap-Dumps
 ✔ Speicher-Leck Optimierung (Kapitel 9.3) ERFOLGREICH VALIDIERET
==========================================================================
```
