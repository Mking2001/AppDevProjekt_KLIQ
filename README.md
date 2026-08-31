#  KLIQ — Nightlife, Club-Radar & Social Network für Android

 Wir kennen es alle: Es ist Freitag- oder Samstagabend, man sitzt mit Freunden zusammen und die ewige Frage taucht auf: *„Wo gehen wir heute feiern? Wo ist wirklich was los und wo steht man sich nur die Beine in den Bauch?“*

Genau dafür haben wir **KLIQ** gebaut. KLIQ ist eine moderne, native Android-App (100% Kotlin & Jetpack Compose), die Nachtschwärmer, Clubs, Bars und Events in Echtzeit zusammenbringt. Keine veralteten Flyer, keine toten WhatsApp-Gruppen – stattdessen Live-Auslastung, GPS-verifizierte Bewertungen, ein aktiver Nightlife-Feed, private Chats und ein Hype-System, das zeigt, wo die Nacht brennt.

---

##  Was macht KLIQ besonders? (Highlights & Features)

###  1. Live-Club-Radar & Hype-Meter
- **Live-Auslastung & Besucherzahlen**: Sieh auf einen Blick, wie voll ein Club aktuell ist (in Prozent und geschätzten Live-Besuchern).
- **Gender-Distribution**: Statistiken zur Gäste-Zusammensetzung (Männer-/Frauenanteil).
- **Daily Flames (Club-Hype)**: Gib deinem Lieblingsclub täglich eine Flamme. Clubs mit dem größten Hype steigen im Ranking ganz nach oben!
- **Echtzeit-Öffnungszeiten**: Dynamischer Status (*Offen*, *Schließt bald*, *Geschlossen*) basierend auf dem aktuellen Wochentag und der Uhrzeit.

###  2. Interaktive Karte & Geofencing
- **Google Maps mit Custom Markern**: Speziell gerenderte, speicheroptimierte Marker zeigen Clubs, Bars und Lounges mit farblicher Auslastungskennzeichnung.
- **GPS-Geofencing**: Automatische Erkennung, wenn du dich in der Nähe eines Clubs befindest (z.B. 150m–200m Radius).
- **Event-Pinning & Adresssuche**: Du veranstaltest eine eigene Spontan-Party oder Afterhour? Platziere dein Event mit Adresssuche direkt auf der Karte!
- **Akkusparendes Tracking**: Durch unseren `AdaptiveLocationController` wird die GPS-Abtastung je nach Bewegungsstatus dynamisch gedrosselt – damit der Akku die ganze Nacht durchhält.

###  3. Social Feed, 24h-Stories & Vernetzung
- **Nightlife-Feed**: Teile Fotos, Eindrücke und Check-Ins mit der Community. Unterstützt Likes, Kommentare und Club-Geotags.
- **24h-Stories**: Teile Momentaufnahmen – wahlweise öffentlich oder exklusiv für deine Freunde.
- **Live-User-Suche**: Finde Freunde und bekannte Gesichter direkt über die integrierte Schnellsuchleiste.

###  4. Messenger & Public City Chats
- **Public City Chat**: Tausche dich im öffentlichen Stadt-Chat mit anderen Partygängern über Line-Ups, Specials oder Einlasszeiten aus.
- **Gruppen-Chats**: Erstelle eigene Gruppen im vertrauten Messenger-Style.
- **Echtzeit-1:1-Direktnachrichten**: Ende-zu-Ende verschlüsselt (AES-256-GCM) für maximale Privatsphäre.
- **Rich Media & Audio**: Fotos aus Kamera/Galerie sowie Sprachnachrichten (Voice Notes).
- **Status & Interaktion**: Lesebestätigungen (Häkchen), Online-Status und intuitive Swipe-Aktionen (Pin, Stummschalten, Löschen).

###  5. Ehrliche, GPS-verifizierte Bewertungen
- **Schluss mit Fake-Reviews**: Eine Club-Bewertung kann nur abgeben werden, wer sich tatsächlich physisch vor Ort befindet (GPS-Proximity-Check) oder den Club nachweislich besucht hat.
- **Community-Moderation**: Bewerte Reviews als hilfreich oder melde unpassende Einträge.

###  6. Profil & QR-Code-Vernetzung
- **4-Slot Fotogalerie**: Zeige deine besten Party-Momente mit Story-artiger Vollbildvorschau.
- **QR-Code Scanner (CameraX + ML Kit)**: Nie wieder umständlich Telefonnummern abtippen – einfach den persönlichen QR-Code vor Ort scannen und sofort befreundet sein.
- **Lifestyle & Party-Präferenzen**: Halte deine Musikvorlieben, Ausgeh-Intents (Freunde, Dating, Party) und Gewohnheiten im Profil fest.
- **Datenschutz & DSGVO**: Volle Kontrolle über deine Daten inklusive sicherem Passwort-Check und 1-Klick-Accountlöschung mit kaskadierender Bereinigung in der Cloud.

---

##  Tech Stack & Architektur

Bei der Entwicklung von KLIQ haben wir großen Wert auf moderne Android-Standards, saubere Architektur (MVVM + Clean Architecture) und hohe Performance gelegt:

| Bereich | Technologie / Bibliothek | Zweck |
| :--- | :--- | :--- |
| **Sprache & Build** | Kotlin 1.9.22, Gradle (Kotlin DSL), JDK 17 | Moderne Sprachfeatures & Typsicherheit |
| **UI Framework** | Jetpack Compose & Material 3 | Deklarative UI, flüssige Animationen & Dark Mode |
| **Dependency Injection** | Dagger Hilt 2.50 | Saubere Entkopplung & Testbarkeit |
| **Lokale Datenbank** | Android Room 2.6.1 (SQLite) | Offline-First Caching, DAOs, Entity-Migrationen |
| **Cloud-Backend** | Firebase Data Connect (PostgreSQL / Google Cloud SQL) | Typsichere GraphQL-Abfragen und Relationen |
| **Auth & Security** | Firebase Auth & EncryptedSharedPreferences (AES-256) | Sichere Sitzungen & verschlüsselte Chats |
| **Maps & Location** | Google Maps SDK for Android & Maps Compose | Kartendarstellung, Geometrie- & Distanzberechnung |
| **Kamera & QR-Code** | CameraX 1.3.1, Google ML Kit & ZXing | QR-Code Scanning & Generierung |
| **Image Loading** | Coil Compose 2.5.0 | Asynchrones Laden mit 50MB Disk- & Memory-Cache |
| **Push & Monitoring** | Firebase Cloud Messaging (FCM), Crashlytics, Timber, LeakCanary | Push-Benachrichtigungen, Crash-Reports & Memory-Leak-Checks |

---

##  Projektstruktur

```text
AppDevProjekt_Vibe/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/kliq/app/
│   │   │   │   ├── data/                 # Datenquellen, Repositories, Entities & Models
│   │   │   │   │   ├── datasource/       # Lokale & Remote Datenquellen
│   │   │   │   │   ├── generated/        # Firebase Data Connect GraphQL generierter SDK-Code
│   │   │   │   │   ├── local/            # Room Database (KliqDatabase, DAOs, Entities)
│   │   │   │   │   ├── model/            # Domain- & UI-Datenmodelle (Club, User, Post, Chat, etc.)
│   │   │   │   │   ├── repository/       # Repository-Implementierungen (Single Source of Truth)
│   │   │   │   │   └── seed/             # Initialer Klagenfurt-Datensatz (Clubs, Events, Posts)
│   │   │   │   ├── di/                   # Dagger Hilt Module (DatabaseModule, NetworkModule, etc.)
│   │   │   │   ├── domain/               # UseCases & Business Logic (z.B. DistanceCalculation)
│   │   │   │   ├── service/              # Background Services (FCM, Crashlytics, Location)
│   │   │   │   ├── ui/                   # Jetpack Compose UI
│   │   │   │   │   ├── components/       # Wiederverwendbare UI-Elemente (Cards, Bars, Dialoge)
│   │   │   │   │   ├── navigation/       # Compose Navigation & Router
│   │   │   │   │   ├── screens/          # Alle Screens (Auth, Map, Feed, Club, Chat, Profile...)
│   │   │   │   │   └── theme/            # Color Schemes, Typografie & Shapes
│   │   │   │   ├── util/                 # Hilfsklassen (AdaptiveLocation, DateFormatter, Haptics)
│   │   │   │   └── viewmodel/            # Jetpack ViewModels (StateFlow / UI-States)
│   │   │   └── res/                      # Android Ressourcen (Drawables, Strings, Icons)
│   │   └── test/                         # Umfassende Unit- & Mockito-Tests
│   └── build.gradle.kts                  # App-Level Build-Konfiguration & Dependencies
├── dataconnect/                          # Firebase Data Connect Konfiguration
│   ├── connector/                        # GraphQL Queries & Mutationen
│   └── schema/schema.gql                 # PostgreSQL Datenbankschema
├── migrate_kliq.sql                      # SQL-Skript für Cloud SQL Tabellen & Indizes
└── build.gradle.kts                      # Root Build-Konfiguration
```

---

##  Erste Schritte & Lokales Setup

Möchtest du das Projekt lokal bauen oder weiterentwickeln? Folge einfach diesen Schritten:

### 1. Voraussetzungen
- **Android Studio** 
- **JDK 17** 
- **Android SDK**: `minSdk = 26` , `targetSdk = 34` 
- Ein physisches Android-Gerät oder ein Emulator mit Google Play Services.

### 2. Repository klonen
```bash
git clone https://github.com/Mking2001/AppDevProjekt_KLIQ.git
cd AppDevProjekt_KLIQ
```

### 3. API-Keys konfigurieren (`local.properties`)
Erstelle im Projekt-Hauptverzeichnis eine Datei namens `local.properties`  und trage deinen Google Maps API Key ein:

```properties
sdk.dir=/Dein/Pfad/Zum/Android/Sdk
MAPS_API_KEY=AIzaSyDeinEchterGoogleMapsApiKeyHier
```


### 4. Firebase Anbindung
- Stelle sicher, dass die `google-services.json` im Ordner `/app` hinterlegt ist.
- KLIQ nutzt Firebase Data Connect für das Backend. Die Cloud SQL PostgreSQL Tabellen können bei Bedarf über das mitgelieferte `migrate_kliq.sql` Skript synchronisiert werden.

### 5. App starten
- Öffne das Projekt in Android Studio.
- Warte, bis der Gradle-Sync abgeschlossen ist.
- Wähle dein Zielgerät/Emulator aus und klicke auf **Run (`Shift + F10`)**.
- Beim ersten Start befüllt der `KliqDatabaseSeeder` die lokale Datenbank automatisch mit echten Hotspots und Clubs aus Klagenfurt & Kärnten (z.B. Eventstage, Teatro, etc.).

---

## Tests & Qualitätssicherung

Wir haben für die Kernkomponenten automatisierte Unit- und Integrationstests geschrieben:

```bash
# Alle lokalen Unit-Tests ausführen
./gradlew testDebugUnitTest

# UI- & Compose-Tests auf einem verbundenen Gerät/Emulator ausführen
./gradlew connectedAndroidTest
```

**Getestete Bereiche:**
- `AdaptiveLocationSamplingTest`: Überprüfung der adaptiven GPS-Abtastraten zur Batterieschonung.
- `GetClubsWithDistanceUseCaseTest`: Mathematische Korrektheit der Distanz- & Geofence-Berechnung (Haversine).
- `MapMarkerPerformanceTest`: Schnelles Rendern von Custom Bitmaps ohne UI-Jank.
- `NotificationsViewModelTest` & `FakeChatRepository`: Mocking von Nachrichten- und Push-Workflows.

---


