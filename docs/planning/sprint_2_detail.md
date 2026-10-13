# Sprint 2: Audio Engine Implementation

## 🎯 Objective
Implement the core "Audio Engine" that triggers content based on user location and plays it back with a rich media experience.

## 📱 iOS Implementation (Swift/SwiftUI)

### 1. Location Service (`Services/LocationService.swift`)
- **CoreLocation** configuration for high accuracy navigation
- Background location updates capability
- Proximity calculation to POIs (using Haversine formula or `CLLocation` distance)
- Combine publisher for location updates

### 2. Audio Service (`Services/AudioService.swift`)
- **AVFoundation** `AVPlayer` management
- **MediaPlayer** integration (lock screen controls, command center)
- State management (`isPlaying`, `duration`, `currentTime`)
- Preloading logic for seamless playback

### 3. Audio Player UI (`Features/Audio/AudioPlayerView.swift`)
- SwiftUI implementation of the player
- Progress bar with seeking
- Play/Pause/Skip controls
- Cover art display (from POI image)

## 🤖 Android Implementation (Kotlin/Jetpack Compose)

### 1. Location Service (`services/LocationService.kt`)
- **FusedLocationProviderClient** setup
- `callbackFlow` for coroutine-based location updates
- Proximity checks against POI list

### 2. Audio Service (`services/AudioService.kt`)
- **ExoPlayer** (Media3) integration
- Foreground Service for background playback
- Notification integration (MediaStyle)
- `StateFlow` for UI state observation

### 3. Audio Player UI (`features/audio/AudioPlayerScreen.kt`)
- Jetpack Compose player UI
- Slider for progress
- Media controls
- Album art integration

## 🔙 Backend Support

- Verify `GET /api/v1/pois/:id` returns audio URLs correctly
- Ensure CDN (simulated or real) is accessible for audio files

## 🧪 Testing Plan
- Simulate GPS movement (Xcode / Android Emulator)
- Verify background audio playback
- Test audio interruption handling (phone calls, etc.)
