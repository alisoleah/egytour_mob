# 🧪 EgyTour Testing Guide

This guide will help you run and verify the features implemented in Sprint 1 (Foundation) and Sprint 2 (Audio Engine).

## 🛠️ 1. Backend Verification

The backend provides the API and database that the apps will eventually connect to.

### Setup & Run
1.  **Open Terminal** and navigate to `backend/`:
    ```bash
    cd backend
    npm install
    ```
2.  **Start Database**: Ensure PostgreSQL is running.
3.  **Run Migrations & Seed**:
    ```bash
    npm run db:migrate
    npm run db:seed
    ```
4.  **Start Server**:
    ```bash
    npm run dev
    ```

### Verify Endpoints
Open a new terminal window to test:

- **Check Health**:
  ```bash
  curl http://localhost:3000/health
  ```
- **Login (Mock)**:
  ```bash
  curl -X POST http://localhost:3000/api/v1/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"test@egytour.app", "password":"password"}'
  ```

---

## 📱 2. iOS Verification

### Run the App
1.  Open `ios/Egytour.xcodeproj` in **Xcode**.
2.  Select a simulator (e.g., **iPhone 15 Pro**).
3.  Press **Cmd + R** to build and run.

### Test Scenarios
1.  **Login Screen**:
    - You should see the "EgyTour" splash/login screen with Pharaoh's Gold branding.
    - **Action**: Click "Skip for Demo".
2.  **Audio Player**:
    - After login, you will land on the Audio Player view.
    - **Verify**: Cover art placeholder, title "Great Pyramid of Khufu".
    - **Action**: Drag the slider, click Play/Pause (icons should toggle).

---

## 🤖 3. Android Verification

### Run the App
1.  Open `android/` directory in **Android Studio**.
2.  Wait for Gradle sync to finish.
3.  Select an emulator or connected device.
4.  Click the Green **Run** button (Shift + F10).

### Test Scenarios
1.  **Login Screen**:
    - Verify the dark mode UI with Gold accents.
    - **Action**: Click "Skip for Demo".
2.  **Audio Player**:
    - The app navigates to `AudioPlayerScreen`.
    - **Verify**: Material Design 3 UI, slider, and playback controls.
    - **Action**: Tap Play/Pause button.

---

## 🗺️ 4. Sprint 3 Verification: Navigation, Routes & AR Compass

### iOS Maps, Route & AR
1.  **Navigation**: Tap the **"Map"** tab at the bottom.
2.  **Map View & POIs**:
    - The map displays the Giza plateau with custom POI markers.
    - Tap on a marker (e.g. Great Pyramid). The POI card slides up.
3.  **Directions & Route Polyline**:
    - Tap **"Directions"** on the card.
    - A golden walking route polyline will render between the visitor entrance and the monument.
    - The card will update with total walking distance (e.g. 420m) and ETA (~5 min walk).
4.  **Turn-by-Turn Walking Navigation**:
    - Tap **"Start Walk"**.
    - The top Turn-by-Turn guidance banner will slide down, displaying step maneuver icons, step distance ("In 120m"), and instructions.
    - Tap **"Next >"** to advance through steps, or **"X"** to exit navigation.
5.  **AR Wayfinding & Directional Compass**:
    - Tap the **"AR View"** button on the POI card.
    - The full-screen AR View will open.
    - **Verify**:
      - Live HUD guidance pill at top ("Directly Ahead" with green dot when aligned, or "Turn X° Right/Left").
      - Center HUD rotating golden compass pointer tracking relative bearing and target azimuth.
      - Bottom destination badge showing remaining meters and walking ETA.
      - RealityKit 3D golden directional pointer in augmented space.
      - Tap the "X" button to dismiss.

### Android Maps, Route & AR
1.  **Navigation**: Tap the **"Map"** tab in the bottom navigation bar.
2.  **Map View & POI Selection**:
    - Map centers on Giza with interactive markers for Great Pyramid, Sphinx, and Luxor.
    - Tap a marker to highlight and view details.
3.  **Directions & Route Polyline**:
    - Tap **"Directions"** on the POI card.
    - Golden route polyline is drawn on the map.
    - Distance and walking ETA are calculated and shown.
4.  **Turn-by-Turn Navigation Banner**:
    - Tap **"Start Walk"**.
    - The top dark-gold HUD banner appears with step instructions, step counter (Step 1/4), and remaining meters.
5.  **AR Directional Compass**:
    - Tap **"AR View"**.
    - The AR viewfinder displays the rotating golden compass arrow, azimuth heading, and relative angle badge.

---

## 🤖 5. Sprint 4 Verification: AI Egyptologist Q&A (RAG & Chat)

### Backend API Testing
1. **Start the backend server**:
   ```bash
   cd backend
   npm run dev
   ```
2. **Test Suggested Discovery Prompts**:
   ```bash
   curl http://localhost:3000/api/v1/ai/suggested-prompts
   ```
3. **Ask the Egyptologist AI**:
   ```bash
   curl -X POST http://localhost:3000/api/v1/ai/ask \
     -H "Content-Type: application/json" \
     -d '{"question":"Who built the Great Pyramid of Giza and were they slaves?"}'
   ```
   **Verify response**:
   - Scholarly answer debunking slave myth with archaeological evidence.
   - Verified sources citations (e.g., Giza Workers' Village excavations).
   - High confidence score (0.95+).
   - Dynamic suggested follow-up questions.

### iOS AI Chat Interface
1. Run the app in Xcode Simulator.
2. Log in / click "Skip for Demo".
3. Tap the **"Egyptologist AI"** tab in the bottom TabView.
4. **Verify**:
   - Header with Dr. Zahi verified scholar badge.
   - Welcome greeting with Egyptology grounding.
   - Horizontal prompt pills (*"Who actually built the Great Pyramid?", etc.*).
   - Tap any suggestion chip or type a question:
     - Message sends immediately.
     - Animated typing indicator pulses.
     - Egyptologist response arrives with source citations and clickable follow-up question chips.

### Android AI Chat Interface
1. Run the app in Android Studio.
2. Log in / click "Skip for Demo".
3. Tap the **"AI Guide"** navigation item in the bottom bar.
4. **Verify**:
   - Dark mode Material 3 Egyptologist chat screen.
   - Dr. Zahi verified guide avatar.
   - Quick prompt pills row.
   - Type or tap a prompt: message sends with auto-scroll, verified sources, and follow-up chips.

---

## 🧭 Testing Sprint 5: AI Trip Planning & Itineraries

### iOS Trip Planner
1. Open the iOS app and tap the **"Trips"** tab in the bottom bar.
2. **Verify List View**:
   - Sample *"3-Day Classical Egyptian Odyssey"* itinerary card is visible with day count, total stop count, and region tags (CAIRO, LUXOR).
   - Tap the card to open **Itinerary Detail View**.
3. **Verify Detail View**:
   - Trip header card with total duration and stops.
   - Horizontal day selection tabs (*DAY 1 Cairo, DAY 2 Cairo, DAY 3 Luxor*).
   - Day theme title (*"Giza Plateau: Pyramids & The Sphinx"*).
   - Vertical timeline with numbered gold circular nodes, connecting lines, visit duration pills (*"150m"*), and Egyptologist notes.
4. **Test AI Generator**:
   - Tap Back, then tap the `+` button in the navigation bar.
   - Configure duration (e.g., 4 Days), toggle regions and interests.
   - Tap **"Generate with Egyptologist AI"**.
   - Watch generator loading state and verify newly generated custom itinerary is prepended to the list.

### Android Trip Planner
1. Run the Android app and tap the **"Trips"** navigation icon in the bottom navigation bar.
2. **Verify List & Timeline**:
   - Card summaries with Pharaoh's Gold badges.
   - Tap into a trip to view day-by-day tabs and vertical timeline stop nodes.
3. **Test Generator**:
   - Tap the `+` icon in the top app bar.
   - Adjust duration stepper, select target regions and interests.
   - Tap **"Generate AI"** to generate new multi-day plans.

---

## 🚀 Testing Sprint 6: Onboarding, Settings & Launch Readiness

### 1. Onboarding Flow (iOS & Android)
- Launch the app for the first time (or fresh install).
- Verify the **3-page hero carousel**:
  1. *Geo-Triggered Narration* (GPS audio narratives trigger on approach).
  2. *AR Wayfinding* (3D camera directional pointer and compass).
  3. *Gemini RAG AI* (Dr. Zahi chat and customized trip planning).
- Test swiping or tapping **"Next"** to advance through pages with animated capsule indicators.
- Test **"Skip"** button to jump straight to authentication.
- On the final page, tap **"Begin Your Expedition"** to persist completion and proceed to Login.

### 2. Settings & Profile (iOS & Android)
- Navigate to the **"Settings"** tab (Gear icon in bottom navigation).
- **Profile Card**: Displays *"Archaeology Explorer"*, user email, and *"VIP PASS • ALL SITES UNLOCKED"* gold badge.
- **Content Preferences**:
  - Switch narration language between English, Arabic, and French.
  - Adjust Scholar Depth (*Casual*, *Enthusiast*, *Scholar*).
- **Audio & AR**:
  - Toggle Auto-Play Geo-Audio, Wi-Fi Only downloads, Haptic Compass Guidance, and Proximity Monument Alerts.
- **Offline Storage**:
  - View offline cached asset size (*142.8 MB*).
  - Tap **"Clear Offline Cache"**: confirm dialog and verify storage drops to *0 MB*.
- **Sign Out**:
  - Tap **"Sign Out"** to return safely to the Login screen.

---

## 🏆 Full End-to-End Feature Verification Checklist (Sprints 1–6)

| Sprint | Feature | Verification Status |
| :--- | :--- | :--- |
| **Sprint 1** | PostgreSQL + PostGIS Schema & Fastify REST API | ✅ Verified |
| **Sprint 2** | Geo-Triggered Audio Narration Engine & Player | ✅ Verified |
| **Sprint 3** | Turn-by-Turn MapKit/GoogleMap Navigation & AR Wayfinding Compass | ✅ Verified |
| **Sprint 4** | Gemini RAG Egyptology Knowledge Bank & Dr. Zahi AI Chat | ✅ Verified |
| **Sprint 5** | AI Multi-Day Itinerary Generation & Vertical Timeline UI | ✅ Verified |
| **Sprint 6** | Onboarding Carousel Pager, Preferences, & Offline Cache Management | ✅ Verified |

---

## 📂 Project Files Reference

All the source code you need has been generated in:
- **iOS**: `ios/EgyTour/`
- **Android**: `android/app/src/main/java/com/egytour/app/`
- **Backend**: `backend/src/`

Documentation backups are in:
- `files/`


