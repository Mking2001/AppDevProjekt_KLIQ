# QA Test Plan: Kapitel 6.6 - Medien-Versand (Fotos in Chats)

## 1. Scope & Test Objectives
Verification of photo attachment selection, camera capture, compression, database caching, preview modal interaction, and chat bubble rendering for chapter 6.6 of the Kliq Android application.

## 2. Test Environments
- **Target Devices**: Android Emulator (API 34, API 33) & Physical Device.
- **Form Factors**: Portrait & Landscape orientations.
- **Network Conditions**: High-speed WiFi & simulated 3G/LTE latency.

## 3. Test Cases & Validation Steps
1. **Gallery Picker Flow**:
   - Tap paperclip icon -> Select "Galerie".
   - Pick a JPEG/PNG image from gallery.
   - Verify image preview modal opens with thumbnail and caption text field.
   - Enter caption text "Biergarten Berlin" -> Tap "Senden".
   - Verify image bubble appears in chat list with correct aspect ratio and caption.
2. **Camera Capture Flow**:
   - Tap paperclip icon -> Select "Kamera".
   - Take picture -> Accept.
   - Verify preview modal shows captured photo.
   - Tap "Senden" -> Verify compressed photo message is cached in Room database.
3. **Cancel & Dismiss Flow**:
   - Select image -> Tap "Abbrechen" or "X".
   - Verify modal closes and input bar returns to text-only mode without sending message.
4. **Fullscreen Image Viewer Flow**:
   - Tap any sent photo in chat list.
   - Verify full-screen dialog displays image centered on black backdrop.
   - Tap close button -> Return to chat details screen.
