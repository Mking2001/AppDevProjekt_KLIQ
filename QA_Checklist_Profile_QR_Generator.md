# Technische Dokumentation: QR-Code-Generator (Kapitel 5.6)

## Übersicht

Der persönliche QR-Code-Generator ermöglicht das sofortige Verifizieren von Nutzern durch Einscannen des individuellen Kliq-Profil-QR-Codes. Die Funktion wurde nach dem MVVM-Pattern implementiert und beinhaltet UX-Optimierungen für dunkle Club-Umgebungen.

---

## Architektur-Schichtenmodell

```
┌─────────────────────────────────────────────────────────┐
│                  View (Compose UI)                       │
│  ProfileQrCodeBottomSheet.kt                            │
│  - Rendert QR-Bitmap als Image-Composable               │
│  - Helligkeitssteuerung via DisposableEffect             │
│  - Keine Berechnungslogik                               │
├─────────────────────────────────────────────────────────┤
│                  ViewModel                               │
│  ProfileViewModel.kt                                    │
│  - StateFlow: isQrModalVisible, qrCodeBitmap,           │
│    isGeneratingQrCode, qrPayloadText                    │
│  - Orchestriert Service-Aufruf via viewModelScope       │
├─────────────────────────────────────────────────────────┤
│                  Service Layer                           │
│  QrCodeService (Interface) / QrCodeServiceImpl          │
│  - Payload-Erstellung (Kliq-Protokoll-URI)              │
│  - ZXing QR-Matrix-Encoding auf Dispatchers.IO          │
│  - BitMatrix → Bitmap Konvertierung                     │
└─────────────────────────────────────────────────────────┘
```

## Generierungs-Algorithmus

### 1. Payload-Konstruktion
Das System generiert eine App-spezifische Protokoll-URI nach folgendem Schema:
```
kliq://user/verify/{userId}?tag=kliq_profile_v1&ts={epochMillis}
```

| Bestandteil | Beschreibung |
|---|---|
| `kliq://user/verify/` | Protokoll-Präfix für Kliq-interne Verifizierung |
| `{userId}` | Eindeutige Nutzer-ID aus der lokalen Datenbank |
| `tag=kliq_profile_v1` | Versionierter Typ-Tag für Abwärtskompatibilität |
| `ts={epochMillis}` | Unix-Zeitstempel zur Sicherstellung der Einmaligkeit |

### 2. ZXing-basierte Matrix-Generierung
Die Konvertierung erfolgt über die ZXing-Bibliothek (`QRCodeWriter`) auf einem IO-Hintergrund-Thread:

1. **Encoding**: `QRCodeWriter.encode(payload, BarcodeFormat.QR_CODE, 512, 512)` erzeugt eine `BitMatrix`.
2. **Pixel-Konvertierung**: Iteration über die Matrix-Koordinaten mit O(width × height) Komplexität. Jedes Bit wird auf `Color.BLACK` (Datenpunkt) oder `Color.WHITE` (Hintergrund) gemappt.
3. **Bitmap-Erstellung**: `Bitmap.createBitmap()` mit `ARGB_8888`-Config für volle Farbtiefe und Kompatibilität.

### 3. Thread-Management
```
Main-Thread                              IO-Dispatcher
───────────                              ─────────────
showQrCodeModal()
  ├─ isGenerating = true
  └─ viewModelScope.launch ──────────→  withContext(Dispatchers.IO) {
                                           QRCodeWriter.encode()
                                           Pixel-Konvertierung
                                           Bitmap.createBitmap()
                                        }
     qrCodeBitmap = result  ←──────────
     isGenerating = false
```

## UX-Optimierungen

### Automatische Helligkeitsanhebung (Club-Modus)
Beim Öffnen des QR-Modals wird die Bildschirmhelligkeit automatisch auf das Maximum angehoben, um die Scannbarkeit in dunklen Club-Umgebungen zu gewährleisten:

- **Aktivierung**: `WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL` (1.0f)
- **Deaktivierung**: `onDispose` stellt den gespeicherten Originalwert wieder her
- **Fallback**: Bei fehlendem Originalwert → `BRIGHTNESS_OVERRIDE_NONE` (Systemstandard)
- **Nutzerfeedback**: Visuelles Statusbanner mit Sonnen-Icon und Text „Display-Helligkeit für Club-Scan maximiert"

### Dark-Mode-Farbschema

| Element | Farbwert | Verwendung |
|---|---|---|
| Card-Hintergrund | `#1E1B2E` | Modal-Container |
| Akzent-Farbe | `#7C3AED` | Rahmen, Icons, Buttons, Banner |
| QR-Container | `#FFFFFF` | High-Contrast-Hintergrund für optimale Scannbarkeit |
| Text primär | `#FFFFFF` | Nutzername, Titel |
| Text sekundär | `#9E9E9E` | Username, Beschreibung |

## Fehlerbehandlung

| Fehlerfall | Behandlung |
|---|---|
| ZXing-Encoding-Fehler | `try/catch` → `Result.failure(e)` → `errorMessage` im UI-State |
| QR-Service nicht verfügbar | Null-Check mit Early-Return, UI zeigt kein Modal |
| Bitmap noch in Generierung | `CircularProgressIndicator` mit Text „Generiere Kliq QR-Code..." |
| Activity-Context nicht erreichbar | `findActivity()` gibt `null` zurück → Helligkeit bleibt unverändert |

## Checkliste für die Code-Prüfung

### Architektur & MVVM
- [x] String-Konvertierung und Bitmap-Generierung vollständig im Service-Layer (`QrCodeServiceImpl`)
- [x] View enthält null Berechnungslogik — ausschließlich State-Rendering
- [x] ViewModel orchestriert Zustandsübergänge via `StateFlow.update()`
- [x] Interface-Abstraktion (`QrCodeService`) für Entkopplung und Testbarkeit
- [x] Hilt `@Singleton` + `@Inject` Dependency-Injection-Konfiguration

### Performance & Thread-Safety
- [x] Gesamte ZXing-Pipeline in `withContext(Dispatchers.IO)` — kein Main-Thread-Blocking
- [x] `viewModelScope.launch` bindet Coroutine-Lifecycle an ViewModel-Zerstörung
- [x] Dispatcher-Injektion via Konstruktor für deterministische Unit-Tests
- [x] Kein redundantes Re-Encoding bei wiederholtem Modal-Öffnen (State wird beibehalten)

### Fehlerbehandlung & Robustheit
- [x] Kotlin `Result<Bitmap>` für typsichere Erfolgs-/Fehlerbehandlung
- [x] Null-sicherer `QrCodeService?`-Parameter mit Early-Return
- [x] Lade-Indikator (`CircularProgressIndicator`) im Kliq-Dark-Mode-Farbschema
- [x] Helligkeits-Wiederherstellung via `DisposableEffect.onDispose` mit Null-Fallback

### UX & Club-Optimierung
- [x] Automatische Helligkeitsanhebung auf `BRIGHTNESS_OVERRIDE_FULL`
- [x] Status-Banner mit visuellem Feedback zur aktiven Helligkeitsänderung
- [x] High-Contrast QR-Container (weißer Hintergrund, lila Akzent-Rahmen)
- [x] Konsistentes Dark-Mode-Farbschema (`#1E1B2E` / `#7C3AED`)

### Test-Abdeckung
- [x] Unit-Tests: Payload-Format, Bitmap-Dimensionen, StateFlow-Übergänge
- [x] Integration: ZXing Roundtrip-Decodierung (Encode → Pixel → Decode → String-Assertion)
- [x] Instrumentiert: Compose-UI Modal-Rendering und Dismiss-Verhalten
