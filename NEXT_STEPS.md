# EgyTour - Implementation Status & Next Steps Roadmap

## 📌 Project Overview
**EgyTour** is an offline-first, AI-powered personal Egyptologist and mobile audio tour guide for tourists exploring Egypt's monuments (Giza, Luxor, Aswan, Cairo).

* **Mobile Clients**: Native Android (Kotlin + Jetpack Compose + Media3 ExoPlayer) & Native iOS (Swift + SwiftUI + AVFoundation).
* **Backend API**: Node.js + Fastify + TypeScript + Drizzle ORM.
* **Database & GIS**: PostgreSQL + PostGIS spatial geometry (`ST_GeogFromText`) + pgvector RAG embeddings.
* **Audio Engine**: HTTP 206 Partial Content range streaming with multi-tier narration (Casual, Enthusiast, Scholar) in English, Arabic, and French.

---

## 📊 Current Feature Status Scorecard

### Phase 1: Backend Foundation
- [x] Native Mobile + Backend Project Architecture (`android/`, `ios/`, `backend/`, `scripts/dev.sh`)
- [x] PostgreSQL + PostGIS spatial schema (`sites`, `points_of_interest`, `audio_content`, `knowledge_chunks`, `itineraries`)
- [x] Drizzle ORM migrations (`npm run db:migrate`)
- [x] Fastify API with JWT authentication, CORS, and rate limiting
- [x] Database seeding with real historical sites, POIs, coordinates, and audio metadata (`npm run db:seed`)
- [x] CRUD and spatial endpoints (`/sites`, `/pois/nearby`, `/ai/ask`, `/itineraries`, `/audio/:poiId/:language/:expertise`)

### Phase 2: Audio Engine & Offline Sync
- [x] Native Jetpack Media3 ExoPlayer (Android) & AVFoundation (iOS) integration
- [x] HTTP 206 Partial Content audio streaming with range request support
- [x] 3-tier scholarly depth selector (Casual 2m, Enthusiast 4m, Scholar 6m)
- [x] Multilingual voice narration (English 🇬🇧, Arabic 🇪🇬, French 🇫🇷)
- [x] Dynamic synchronous audio transcripts
- [x] Offline Monument Pack manager (`OfflineSyncManager`) for zero-reception tomb/desert playback
- [x] Variable playback speed (`0.75x` – `1.5x`) and seek rewind/forward controls

### Phase 3: Navigation & AR
- [x] Google Maps SDK integration with monument markers and radius circles
- [x] Proximity detection and auto-trigger audio logic
- [x] Live CameraX AR viewfinder with HUD compass heading
- [x] Spatial waypoint navigation and distance-to-target countdown
- [ ] Real-time 3D ancient temple reconstructions (glTF/GLB models)

### Phase 4: AI Egyptologist (Gemini RAG)
- [x] In-situ contextual chat interface ([ChatScreen.kt](file:///Users/galala/egytour_mob/egytour_mob/android/app/src/main/java/com/egytour/app/features/ai/ui/ChatScreen.kt))
- [x] Gemini RAG pipeline querying `knowledge_chunks` with location context
- [x] Excavation citation cards with clickable historical source references
- [x] Suggested context-sensitive quick question chips

### Phase 5: Trip Planner
- [x] Dynamic multi-day itinerary builder ([ItineraryScreen.kt](file:///Users/galala/egytour_mob/egytour_mob/android/app/src/main/java/com/egytour/app/features/itinerary/ui/ItineraryScreen.kt))
- [x] AI-powered itinerary scheduling endpoint (`POST /api/v1/itineraries/generate`)
- [x] Ticketing prices (EGP), opening/closing hours, and duration estimates
- [ ] Export itinerary schedule to device calendar (`.ics` / Google Calendar intent)

### Phase 6: Polish & Monetization
- [x] 3-slide animated onboarding flow with skip/start triggers
- [x] Settings screen with language selection, scholarly depth, and offline cache clearing
- [x] VIP All-Access Pass checkout UI ([CheckoutScreen.kt](file:///Users/galala/egytour_mob/egytour_mob/android/app/src/main/java/com/egytour/app/features/checkout/ui/CheckoutScreen.kt)) and purchase success receipt
- [ ] Store payment processing SDK (Google Play Billing / RevenueCat)
- [ ] Firebase Cloud Messaging (FCM) push notifications for proximity alerts

---

## 🚀 Actionable Next Steps

### 1. In-App Purchase & Subscription SDK (Sprint 1)
* **Goal**: Enable real credit card / App Store payments for the EGP 750 ($15 USD) VIP Pass.
* **Tasks**:
  1. Integrate RevenueCat SDK or Google Play Billing Client 6.x into `android/app/build.gradle.kts`.
  2. Implement `Purchases.shared.purchasePackage()` in `CheckoutScreen.kt`.
  3. Validate purchase receipts on backend endpoint `POST /api/v1/billing/verify`.

### 2. 3D AR Temple Reconstructions (Sprint 2)
* **Goal**: Allow visitors standing in front of ruins to view digital overlays of how the monuments appeared during the New/Old Kingdoms.
* **Tasks**:
  1. Add Google Sceneview / Filament dependency to Android.
  2. Procure/bundle lightweight `.glb` 3D models for the Great Pyramid (with casing stones) and Karnak Hypostyle Hall.
  3. Render 3D model anchored over CameraX preview in `ARNavigationScreen.kt`.

### 3. Push Notifications & Geofence Background Service (Sprint 3)
* **Goal**: Send notifications when approaching a monument even when the phone is locked in the tourist's pocket.
* **Tasks**:
  1. Add Firebase Cloud Messaging (FCM) & Google Play Services Location Geofencing API.
  2. Register background Geofence transitions for Giza, Karnak, and Luxor.
  3. Post high-priority notification: *"You are near the Sphinx! Tap to listen to Dr. Zahi's tour."*

### 4. Itinerary Device Calendar Export (Sprint 4)
* **Goal**: Export scheduled trips to Google Calendar or Apple Calendar.
* **Tasks**:
  1. Implement Android `Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI)` in `ItineraryScreen.kt`.
  2. Generate `.ics` iCalendar download link from `GET /api/v1/itineraries/:id/export.ics`.

### 5. iOS Build & Simulator Validation (Sprint 5)
* **Goal**: Verify parity on iOS Simulator.
* **Tasks**:
  1. Run `sudo xcodebuild -license accept` and `xcodebuild -runFirstLaunch`.
  2. Execute `./scripts/dev.sh ios` to build and verify SwiftUI screens on `iPhone 15` simulator.

---

## 🛠️ How to Run the Local Dev Environment
One command launches the database, runs migrations, seeds data, starts the Fastify API, boots the Android emulator, and installs the app:

```bash
# Launch entire stack (Backend + Android)
./scripts/dev.sh android

# Backend only (http://localhost:3000)
./scripts/dev.sh backend

# iOS Simulator
./scripts/dev.sh ios
```
