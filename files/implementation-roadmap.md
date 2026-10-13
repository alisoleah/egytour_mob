# EgyTour - Claude Code Implementation Roadmap

## How to Use This with Claude Code

### Step 1: Setup Repository
Give Claude Code this prompt first:
```
Initialize a monorepo for EgyTour with:
- pnpm workspaces
- apps/mobile (Expo React Native + ViroReact)
- apps/web (Next.js 14)
- apps/api (Fastify)
- packages/db (Drizzle ORM)
- packages/ui (shared components)
- packages/audio-engine (audio playback + location triggers)
- services/ai-guide (RAG Q&A service)

Use the CLAUDE.md file for project context.
```

### Step 2: Database First
```
Create the Drizzle schema based on the ERD in the blueprint.

Include:
- sites table with PostGIS geography columns
- points_of_interest with trigger_radius_meters
- audio_content with language_code and expertise_level
- ar_assets with quality tiers
- users with language/expertise preferences
- itineraries and itinerary_stops
- offline_packages for content bundling
```

### Step 3: Feature-by-Feature
Reference specific user stories when building features.

---

## Sprint 1: Foundation (Week 1-2)

### Task 1.1: Database Schema
**Claude Code Prompt:**
```
Create Drizzle ORM schema for PostgreSQL + PostGIS with these tables:

1. sites - id, name_en, name_ar, name_fr, description_en, description_ar, region, 
   coordinates (geography POINT), bounding_box (geography POLYGON), 
   opening_hours (jsonb), entry_fee (jsonb), content_version

2. points_of_interest - id, site_id (FK), name_en, name_ar, poi_type (enum), 
   coordinates (geography POINT), trigger_radius_meters (default 20), 
   historical_period, importance_rank, accessibility_info (jsonb)

3. audio_content - id, poi_id (FK), language_code, expertise_level (enum), 
   audio_url, audio_duration_seconds, transcript, narrator_name, file_size_bytes

4. users - id, email, display_name, preferred_language, expertise_level, 
   subscription_tier, preferences (jsonb)

5. itineraries - id, user_id (FK), title, start_date, end_date, 
   regions (text[]), status (enum)

6. itinerary_stops - id, itinerary_id (FK), poi_id (FK), day_number, 
   order_index, recommended_duration_minutes

Add proper indexes including GIST for geography columns.
Enable PostGIS extension.
```

### Task 1.2: Seed Data Generator
**Claude Code Prompt:**
```
Create a seed script that generates:
- 5 sites (Giza Pyramids, Egyptian Museum, Karnak Temple, Valley of Kings, Abu Simbel)
- 50 POIs across these sites with realistic Egyptian historical data
- Audio content entries for EN, AR, FR for each POI (3 expertise levels)
- Proper GPS coordinates for all POIs
- Entry fees in EGP
- Opening hours in Egypt timezone

Use actual historical information for POI names and descriptions.
Include important landmarks like Great Pyramid, Sphinx, Tutankhamun's tomb, etc.
```

### Task 1.3: Basic API Endpoints
**Claude Code Prompt:**
```
Create Fastify API endpoints:

1. GET /api/v1/sites - List sites with pagination, filter by region
2. GET /api/v1/sites/:id - Get site with all POIs
3. GET /api/v1/sites/:id/pois - Get POIs for a site with audio metadata
4. GET /api/v1/pois/:id - Get single POI with all audio versions
5. GET /api/v1/pois/nearby - Find POIs within radius of coordinates

Include:
- Zod schemas for request/response validation
- PostGIS spatial queries using ST_DWithin
- Response caching headers for CDN
- Error handling with proper HTTP codes
```

---

## Sprint 2: Audio Engine (Week 3-4)

### Task 2.1: Location-Based Audio Trigger
**Claude Code Prompt:**
```
Create the audio trigger system for React Native:

packages/audio-engine/src/trigger.ts:
- Monitor user location using expo-location
- Check proximity to POIs using Haversine formula
- Trigger audio when user enters trigger_radius_meters
- Debounce triggers (don't re-trigger same POI within 10 minutes)
- Handle multiple POIs nearby (prioritize by importance_rank)
- Support background location tracking

Include:
- TypeScript types for POI proximity events
- Event emitter for trigger notifications
- Battery-efficient location tracking modes
```

### Task 2.2: Audio Player Component
**Claude Code Prompt:**
```
Create audio player React Native component using expo-av:

Features:
- Play/pause/seek controls
- Background audio playback
- Lock screen controls (expo-av AudioSession)
- Progress bar with time display
- Playback speed control (0.75x, 1x, 1.25x, 1.5x)
- Download indicator for cached content
- "Ask a Question" button that opens AI chat

Use design-system.md for styling:
- Pharaoh's Gold for play button
- Dark theme card background
- Playfair Display for POI name
- JetBrains Mono for duration

Animations with Moti:
- Pulse effect when audio is playing
- Fade in entrance animation
- Progress bar smooth transition
```

### Task 2.3: Offline Audio Download System
**Claude Code Prompt:**
```
Create offline content download system:

1. API endpoint: GET /api/v1/offline/package/:siteId
   - Returns manifest with all audio URLs, sizes, versions
   
2. React Native downloader service:
   - Download audio files to expo-file-system
   - Track download progress per file
   - Resume interrupted downloads
   - Verify file integrity with checksums
   - Store download state in AsyncStorage
   
3. UI component:
   - Site download card with progress bar
   - Total size / downloaded size display
   - Cancel download button
   - "Downloaded" badge with checkmark

Handle:
- Background downloads
- WiFi-only download option
- Storage space warnings
- Content version updates
```

---

## Sprint 3: Navigation & Maps (Week 5-6)

### Task 3.1: Custom Egypt Map
**Claude Code Prompt:**
```
Create Mapbox map component with custom Egypt styling:

1. Custom map style (use Mapbox Studio or inline):
   - Desert/sand colored land
   - Nile River highlighted in Nile Blue
   - POI markers in Pharaoh's Gold
   - Site boundary overlays

2. Features:
   - User location blue dot
   - POI markers with category icons
   - Cluster markers for zoomed out view
   - Site boundary polygons
   - Route line to selected POI
   
3. Interactions:
   - Tap marker to show POI card
   - Long press to add to itinerary
   - Pinch zoom with marker clustering
   - 3D building mode for museums

Use react-native-mapbox-gl with offline maps support.
```

### Task 3.2: AR Wayfinding
**Claude Code Prompt:**
```
Implement AR wayfinding using ViroReact:

1. AR Scene component:
   - Compass-based direction arrow
   - Distance to destination overlay
   - POI name floating above target
   - Gradient line path on ground
   
2. Camera permissions handling:
   - Request camera access
   - Fallback to 2D map if denied
   - Handle tracking loss gracefully
   
3. Location anchoring:
   - Convert GPS coordinates to AR position
   - Update arrow direction as user moves
   - Smooth transitions between targets

Performance:
   - Auto-reduce quality on low-end devices
   - Disable when battery < 20%
   - Frame rate monitoring
```

### Task 3.3: Turn-by-Turn Navigation
**Claude Code Prompt:**
```
Create navigation system:

1. Route calculation:
   - Mapbox Directions API for walking routes
   - Avoid roads, prefer pedestrian paths
   - Include site entry points
   
2. Navigation UI:
   - Next instruction banner at top
   - Distance and ETA display
   - Step list expandable drawer
   - Re-route if user goes off path
   
3. Audio directions (optional):
   - TTS for turn instructions
   - POI approach announcements
   - "You have arrived" notification

Store routes offline for downloaded sites.
```

---

## Sprint 4: AI Features (Week 7-8)

### Task 4.1: RAG Knowledge Base Setup
**Claude Code Prompt:**
```
Create RAG system for AI Q&A:

1. Knowledge base ingestion:
   - Parse Markdown content from /content/pois/
   - Chunk into 500-token segments
   - Generate embeddings using sentence-transformers
   - Store in pgvector table
   
2. Database table:
   CREATE TABLE knowledge_chunks (
     id UUID PRIMARY KEY,
     poi_id UUID REFERENCES points_of_interest(id),
     content TEXT,
     embedding vector(768),
     source_type TEXT, -- 'description', 'history', 'architecture'
     metadata JSONB
   );
   
3. Retrieval function:
   - Input: user question + current POI context
   - Output: top 5 relevant chunks
   - Filter by poi_id for location-aware answers
```

### Task 4.2: AI Q&A API Endpoint
**Claude Code Prompt:**
```
Create AI question-answering endpoint:

POST /api/v1/ai/ask
{
  "question": "Why did they build the pyramids with this alignment?",
  "poi_id": "uuid",
  "conversation_id": "uuid" // for follow-ups
}

Implementation:
1. Retrieve relevant chunks from pgvector
2. Build prompt with context + question
3. Call Claude API with system prompt for Egyptologist persona
4. Parse response and extract sources
5. Generate TTS for audio response (optional)

Response:
{
  "answer_text": "...",
  "audio_url": "...", // optional
  "sources": [{"title": "...", "chunk_id": "..."}],
  "confidence": 0.92,
  "follow_up_suggestions": [...]
}

Rate limit: 10 requests/minute per user
Include confidence scoring - flag low confidence answers
```

### Task 4.3: AI Chat Interface
**Claude Code Prompt:**
```
Create chat interface component:

Features:
- Message bubbles (user/assistant)
- Voice input button (Whisper STT)
- Audio playback for AI responses
- Source citations expandable
- Follow-up suggestion chips
- Loading state with typing indicator

Design system:
- Assistant messages in card background
- User messages in Nile Blue
- Timestamps in JetBrains Mono
- Smooth scroll to latest message

State management:
- Conversation history in context
- Save conversations to AsyncStorage
- "New conversation" option
```

---

## Sprint 5: Trip Planning (Week 9-10)

### Task 5.1: Itinerary Data Model
**Claude Code Prompt:**
```
Create itinerary management system:

API endpoints:
1. POST /api/v1/itineraries - Create new itinerary
2. GET /api/v1/itineraries - List user's itineraries
3. GET /api/v1/itineraries/:id - Get itinerary with stops
4. POST /api/v1/itineraries/:id/stops - Add stop
5. PATCH /api/v1/itineraries/:id/stops/:stopId - Update stop order/time
6. DELETE /api/v1/itineraries/:id/stops/:stopId - Remove stop

Itinerary structure:
{
  "id": "uuid",
  "title": "Cairo in 3 Days",
  "start_date": "2025-03-15",
  "days": [
    {
      "day_number": 1,
      "date": "2025-03-15",
      "stops": [
        {
          "poi_id": "uuid",
          "poi_name": "Great Pyramid",
          "start_time": "08:00",
          "duration_minutes": 120,
          "travel_time_from_previous": 0
        }
      ]
    }
  ],
  "total_entry_fees": 500,
  "offline_package_size_mb": 350
}
```

### Task 5.2: AI Itinerary Generator
**Claude Code Prompt:**
```
Create AI-powered itinerary generation:

POST /api/v1/itineraries/generate
{
  "duration_days": 5,
  "start_date": "2025-03-15",
  "regions": ["cairo", "luxor"],
  "interests": ["ancient_history", "temples", "photography"],
  "pace": "relaxed", // relaxed | moderate | intensive
  "budget_tier": "mid_range",
  "accessibility_needs": []
}

Algorithm:
1. Score POIs based on interest matching
2. Cluster POIs by proximity using DBSCAN
3. Apply traveling salesman heuristic per day
4. Respect opening hours constraints
5. Add travel time estimates between stops
6. Balance across days

Return generated itinerary with:
- Optimized route per day
- Estimated costs (entry fees)
- Suggested meal breaks
- Photography golden hour suggestions
- Offline download size
```

### Task 5.3: Itinerary UI
**Claude Code Prompt:**
```
Create itinerary screens:

1. Itinerary List Screen:
   - Card per itinerary with image collage
   - Trip dates, # of days, # of sites
   - Download status indicator
   - Swipe to delete

2. Itinerary Detail Screen:
   - Tab per day
   - Timeline view of stops
   - Drag to reorder stops
   - Tap to view POI details
   - "Start Tour" button for day 1
   
3. Itinerary Generator Screen:
   - Multi-step form wizard
   - Date picker
   - Interest chips (multi-select)
   - Pace slider
   - Generate button with loading state
   - Preview generated itinerary
   - "Accept" or "Regenerate" actions

Animations:
- Reorder with spring animation
- Timeline dot pulse for current stop
- Confetti on "Tour Complete"
```

---

## Sprint 6: Polish & Launch (Week 11-12)

### Task 6.1: Onboarding Flow
**Claude Code Prompt:**
```
Create onboarding screens:

1. Welcome splash with EgyTour logo animation
2. Value proposition slides:
   - "Your Personal Egyptologist"
   - "Works Offline Everywhere"
   - "AR Time Travel"
3. Language selection (EN/AR/FR with flag icons)
4. Expertise level selection (Casual/Enthusiast/Scholar)
5. Push notification permission
6. "Download your first site" suggestion

Design:
- Full-bleed Egyptian imagery backgrounds
- Parallax scroll effect
- Page indicator dots in Pharaoh's Gold
- Skip button top right
- Smooth page transitions

Store preferences to user profile on completion.
```

### Task 6.2: Settings & Profile
**Claude Code Prompt:**
```
Create settings screen:

Sections:
1. Account
   - Email (read-only)
   - Display name (editable)
   - Sign out button
   
2. Preferences
   - Language selector
   - Expertise level
   - Audio auto-play toggle
   - Auto-download on WiFi toggle
   
3. Downloads
   - Storage used / available
   - Downloaded sites list
   - "Delete all downloads" button
   
4. Appearance
   - Dark/Light/System theme toggle
   
5. About
   - App version
   - Terms of Service
   - Privacy Policy
   - Credits (Egyptologist consultants)
   - Contact support

Match design system:
- Grouped sections with card backgrounds
- Toggle switches with smooth animation
- Destructive actions in error color
```

### Task 6.3: Push Notifications
**Claude Code Prompt:**
```
Implement push notifications using expo-notifications:

Notification types:
1. "Approaching [Site Name]" - Location-triggered when near a site
2. "Your itinerary starts tomorrow" - Day before trip
3. "New content available" - Content updates for downloaded sites
4. "You're at [POI]" - Audio ready to play notification

Backend:
- Store FCM/APNs tokens in users table
- POST /api/v1/notifications/register
- Scheduled job for itinerary reminders
- Webhook for content update broadcasts

Client:
- Request permission during onboarding
- Handle notification tap (deep link to POI/itinerary)
- Background notification handling
- Notification preferences screen
```

---

## Testing Prompts

### Unit Tests
```
Write unit tests for the audio trigger system:
1. Test user enters trigger radius - audio should trigger
2. Test user exits trigger radius - no re-trigger immediately
3. Test debounce prevents re-trigger within 10 minutes
4. Test multiple POIs - highest importance wins
5. Test offline mode uses cached POI data
```

### Integration Tests
```
Write integration tests for the itinerary generator:
1. Test generates valid itinerary for 3-day Cairo trip
2. Test respects opening hours (no 3am pyramid visits)
3. Test travel times between stops are reasonable
4. Test accessibility needs are respected
5. Test response time < 5 seconds
```

### E2E Tests
```
Write Detox E2E tests for mobile app:
1. User completes onboarding flow
2. User searches for "Pyramids" and views results
3. User downloads Giza offline package
4. User plays audio at POI
5. User asks AI a question and gets response
6. User creates itinerary and adds stops
```

---

## Common Issues & Solutions

### Issue: PostGIS extension not enabled
```sql
Solution: Run in PostgreSQL:
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;
```

### Issue: pgvector dimension mismatch
```
Solution: Ensure embedding dimensions match:
- all-MiniLM-L6-v2 produces 384 dimensions
- Update schema: embedding vector(384)
```

### Issue: ViroReact build fails
```
Solution: ViroReact requires specific setup:
1. Use Expo development build, not Expo Go
2. Add viro-community/react-viro to app.json plugins
3. Rebuild with eas build
```

### Issue: Mapbox offline maps not downloading
```
Solution: Ensure Mapbox token has Downloads scope:
1. Check token permissions in Mapbox dashboard
2. Add SKU to billing
3. Use OfflineManager.createPack() with proper region
```

### Issue: Audio continues after backgrounding
```
Solution: Configure audio session properly:
await Audio.setAudioModeAsync({
  staysActiveInBackground: true,
  interruptionModeIOS: Audio.INTERRUPTION_MODE_IOS_DO_NOT_MIX,
  playsInSilentModeIOS: true,
});
```

### Issue: Arabic RTL layout broken
```
Solution: Enable RTL in app.json:
{
  "expo": {
    "extra": {
      "supportsRTL": true
    }
  }
}
And use I18nManager.forceRTL(true) for Arabic locale.
```

---

## Deployment Checklist

### Pre-Launch
- [ ] All content reviewed by Egyptologist
- [ ] Audio recorded for all POIs in all languages
- [ ] 3D models optimized for mobile
- [ ] API load tested for 10,000 concurrent users
- [ ] Offline packages generated for all sites
- [ ] App store assets prepared (screenshots, descriptions)
- [ ] Privacy policy updated
- [ ] Analytics events tracked

### App Store Submission
- [ ] iOS: Background location usage description
- [ ] iOS: Camera usage description (AR)
- [ ] iOS: Microphone usage description (voice Q&A)
- [ ] Android: Fine location permission
- [ ] Android: Camera permission
- [ ] Android: Large asset download disclosure

### Post-Launch Monitoring
- [ ] Crashlytics/Sentry configured
- [ ] API response time monitoring
- [ ] Download completion rate tracking
- [ ] User journey funnel analytics
- [ ] Content staleness alerts
