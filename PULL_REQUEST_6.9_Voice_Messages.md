# Pull Request: Kapitel 6.9 - Sprachnachrichten-Funktion im Chat-System

## Overview
This PR implements **Chapter 6.9 (Voice Messages in Chat System)** for the native Kliq Android application in Kotlin and Jetpack Compose. It adds full audio recording and playback capabilities, database persistence, and a high-contrast dark-mode recording UI.

## Key Changes
- **Data Layer & Room Database**:
  - Extended `MessageType` enum to include `VOICE`.
  - Added `audioDurationMs: Long = 0L` to `ChatMessage`, `DirectMessage`, `MessageEntity`, and `DirectMessageEntity`.
  - Bumped `KliqDatabase` version to `16` and added Room database migration `MIGRATION_15_16` in `DatabaseMigrations.kt`.
  - Updated mapping functions in `ChatRepositoryImpl` to map audio metadata between SQLite entities and Compose UI models.
- **Audio Recording & Playback Managers**:
  - Implemented `VoiceRecorderManager` (`com.kliq.app.util`) wrapping `MediaRecorder` for MPEG_4 / AAC encoding (128kbps, 44.1kHz), realtime amplitude measuring (`getMaxAmplitudeNormalized()`), and lifecycle cleanup.
  - Implemented `VoicePlayerManager` (`com.kliq.app.util`) wrapping `MediaPlayer` for playback control (play, pause, seek, completion handling, and realtime progress updates via Kotlin Coroutines).
  - Added `android.permission.RECORD_AUDIO` to `AndroidManifest.xml`.
- **UI & High-Contrast Dark-Mode Design**:
  - Updated `ChatInputBar` in `ChatComponents.kt` with a microphone action button when text input is empty.
  - Added live recording overlay with pulsing red recording indicator, duration timer (`mm:ss`), live waveform amplitude visualization, cancel action button, and send action button.
  - Implemented `VoiceMessageBubble` in `ChatComponents.kt` with play/pause button, audio duration display, interactive progress slider, and message delivery status ticks.
  - Enhanced `ChatBubble` to dynamically render `VoiceMessageBubble` for voice message types.
- **MVVM Architecture & State Management**:
  - Extended `ChatDetailViewModel` and `ChatDetailUiState` with recording states (`isRecordingVoice`, `recordingDurationMs`, `recordingAmplitudes`) and playback states (`playingMessageId`, `isPlayingVoice`, `voicePlaybackPositionMs`, `voicePlaybackDurationMs`).
  - Added lifecycle-aware resource cleanup (`release()`) in `ViewModel.onCleared()` and `ChatDetailScreen`.
- **Unit Tests**:
  - Added `ChatVoiceMessageTest` covering `ChatMessage` and `DirectMessage` data model instantiation with `MessageType.VOICE`.
  - Updated `ChatDetailViewModelTest` with test cases for voice recording, stop and send, and playback controls.
  - Updated `DatabaseMigrationTest` with `migrate15To16_addsAudioDurationMsAndStatusTimestamps`.

## Verification & Quality Assurance
- [x] Room Database Migration 15 -> 16 passes unit test and preserves existing user data.
- [x] Audio recording releases `MediaRecorder` resources cleanly without memory leaks.
- [x] Audio playback releases `MediaPlayer` resources cleanly upon completion and ViewModel clearance.
- [x] High-contrast Kliq Dark-Mode (Purple theme) styled consistently across recording overlays and voice message bubbles.
- [x] All unit tests pass (`ChatVoiceMessageTest`, `ChatDetailViewModelTest`, `DatabaseMigrationTest`).
