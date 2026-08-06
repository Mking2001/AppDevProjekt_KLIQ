# Pull Request: Kapitel 8.6 - Custom Fonts & Icon Styling System

**Branch:** `feature/custom-fonts-and-icon-styling` ➔ `main`  
**PR-Link:** [Pull Request auf GitHub erstellen](https://github.com/Mking2001/AppDevProjekt_KLIQ/pull/new/feature/custom-fonts-and-icon-styling)

---

## 📌 Übersicht & Zielstellung

Dieser Pull Request implementiert die **systemweite Einbindung von Custom Fonts** (Poppins Font Family) und ein **einheitliches Icon-Design-System** für die native Android-App *Kliq* gemäß Kapitel 8.6 des Entwicklungsplans.

Das System stellt klare Typografie-Hierarchien, einheitliche Icon-Größen (16dp, 24dp, 32dp, 48dp) und dynamische Tinting-Logik bereit, abgestimmt auf das Kliq Lila-High-Contrast Dark-Theme.

---

## 🛠 Umgesetzte Architektur & Technische Details

### 1. Custom Typography System (`Type.kt`)
- Einbindung der Font Family `PoppinsFontFamily` (`Light`, `Regular`, `Medium`, `SemiBold`, `Bold`) in Compose `Typography`.
- Korrekte Zuordnung aller Material 3 TextStyles (Display, Headline, Title, Body, Label).
- Bereitstellung von Kliq-Typografie-Extensions (`heading1`, `heading2`, `heading3`, `bodyRegular`, `button`, `caption`).

### 2. Zentrale Icon-Architektur & Tinting (`KliqIconSystem.kt`)
- `KliqIconSize`: Standard-Icon-Abmessungen (`SMALL`: 16dp, `MEDIUM`: 24dp, `LARGE`: 32dp, `DISPLAY`: 48dp).
- `KliqIconCategory`: Kategorisierung für dynamisches Color Tinting (`ACTION`, `NAVIGATION`, `EVENT_MARKER`, `STANDARD`).
- Wiederverwendbare Compose-Wrapper `KliqIcon` & `KliqIconButton` mit integriertem High-Contrast Lila-Schema.

### 3. Integration in Kern-Screens
- **Bottom Navigation ([KliqBottomBar.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/navigation/KliqBottomBar.kt))**: Migration auf `KliqIcon` mit `KliqIconCategory.NAVIGATION`.
- **Top Bar ([KliqTopBar.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/navigation/KliqTopBar.kt))**: Menü & Overflow-Actions auf `KliqIconCategory.ACTION` umgestellt.
- **Chat Bubbles ([ChatComponents.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/components/ChatComponents.kt))**: VoiceMessage-Buttons und Media-Actions vereinheitlicht.
- **Map Overlay ([MapQuickViewCard.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/components/MapQuickViewCard.kt))**: Event-Marker und QuickView-Icons auf `KliqIconSystem` umgestellt.
- **Profile Details ([ProfileScreen.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/screens/profile/ProfileScreen.kt))**: Typografie und Action-Buttons angepasst.

### 4. Catalog Screen & Test Suite
- **[TypographyAndIconCatalogScreen.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/main/java/com/kliq/app/ui/screens/catalog/TypographyAndIconCatalogScreen.kt)**: Interaktiver Catalog-Screen mit Live Theme-Switching (Dark, Light, High-Contrast Lila).
- **Compose Previews**: `@Preview`-Suite für Light & Dark Mode.
- **[TypographyAndIconCatalogEmulatorTest.kt](file:///c:/Users/Felix/Documents/GitHub/AppDevProjekt_Vibe/app/src/androidTest/java/com/kliq/app/ui/TypographyAndIconCatalogEmulatorTest.kt)**: Compose TestRule UI-Tests für Accessibility und Component-Rendering.

---

## 📋 Commit-Historie

1. `feat: add custom typography system and font assets`
2. `feat: implement branded icon styling and tint wrappers`
3. `refactor: apply typography and icon system to main screens`
4. `test: add typography and icon catalog preview screen and emulator ui tests`

---

## 📋 PR Abnahme-Checkliste

- [x] **Typografie**: Alle Text-Styles nutzen `PoppinsFontFamily` mit korrekt zugewiesenen Weights.
- [x] **Icon-System**: Standardisierte Icon-Größen (16dp, 24dp, 32dp, 48dp) und dynamisches Tinting etabliert.
- [x] **High-Contrast Dark Theme**: Lila Primary (`#9C27B0` / `#6A1B9A` / `#7C3AED`) und Kontraste im Nachtleben-Ambiente verifiziert.
- [x] **Accessibility**: Alle Icons besitzen `contentDescription`s für Screenreader (TalkBack).
- [x] **Testabdeckung**: Interaktiver Catalog-Screen und Compose UI Test Suite bereitgestellt.
