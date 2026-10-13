# EgyTour - Sprint Execution Plan (Native iOS/Android)

## 🎯 Overview

This document provides a **complete 6-sprint execution plan** for building EgyTour using **native Swift (iOS) and Kotlin (Android)**, adapted from the original implementation roadmap.

**Timeline**: 12 weeks (6 sprints × 2 weeks each)  
**Team**: 2 iOS devs, 2 Android devs, 1 backend dev, 1 designer, 1 Egyptologist

---

## 📅 Sprint 1: Foundation (Weeks 1-2)

### Sprint Goal
Establish project infrastructure, database schema, and basic API endpoints for both platforms.

### Tasks

#### 1.1 Database Schema (Backend - Day 1-2)

**Implementation:**

```sql
-- Enable PostGIS extension
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;
CREATE EXTENSION IF NOT EXISTS vector;

-- Sites table
CREATE TABLE sites (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name_en TEXT NOT NULL,
    name_ar TEXT NOT NULL,
    name_fr TEXT NOT NULL,
    description_en TEXT NOT NULL,
    description_ar TEXT NOT NULL,
    description_fr TEXT NOT NULL,
    region TEXT NOT NULL,
    coordinates GEOGRAPHY(POINT, 4326) NOT NULL,
    bounding_box GEOGRAPHY(POLYGON, 4326),
    opening_hours JSONB,
    entry_fee JSONB,
    image_url TEXT,
    content_version TEXT NOT NULL DEFAULT '1.0.0',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Points of Interest table
CREATE TABLE points_of_interest (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    site_id UUID REFERENCES sites(id) ON DELETE CASCADE,
    name_en TEXT NOT NULL,
    name_ar TEXT NOT NULL,
    name_fr TEXT NOT NULL,
    poi_type TEXT NOT NULL,
    coordinates GEOGRAPHY(POINT, 4326) NOT NULL,
    trigger_radius_meters INTEGER DEFAULT 20,
    historical_period TEXT,
    importance_rank INTEGER DEFAULT 5,
    accessibility_info JSONB,
    image_url TEXT,
    content_version TEXT NOT NULL DEFAULT '1.0.0',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Audio Content table
CREATE TABLE audio_content (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    poi_id UUID REFERENCES points_of_interest(id) ON DELETE CASCADE,
    language_code TEXT NOT NULL,
    expertise_level TEXT NOT NULL,
    audio_url TEXT NOT NULL,
    audio_duration_seconds INTEGER NOT NULL,
    transcript TEXT NOT NULL,
    narrator_name TEXT,
    file_size_bytes INTEGER NOT NULL,
    content_version TEXT NOT NULL DEFAULT '1.0.0',
    created_at TIMESTAMP DEFAULT NOW()
);

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    display_name TEXT NOT NULL,
    preferred_language TEXT DEFAULT 'en',
    expertise_level TEXT DEFAULT 'casual',
    subscription_tier TEXT DEFAULT 'free',
    subscription_expires_at TIMESTAMP,
    preferences JSONB DEFAULT '{"autoPlayAudio": true, "downloadOnWifiOnly": true, "theme": "system"}',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Itineraries table
CREATE TABLE itineraries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    regions TEXT[],
    status TEXT DEFAULT 'draft',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Itinerary Stops table
CREATE TABLE itinerary_stops (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    itinerary_id UUID REFERENCES itineraries(id) ON DELETE CASCADE,
    poi_id UUID REFERENCES points_of_interest(id),
    day_number INTEGER NOT NULL,
    order_index INTEGER NOT NULL,
    recommended_duration_minutes INTEGER,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_sites_coordinates ON sites USING GIST (coordinates);
CREATE INDEX idx_pois_coordinates ON points_of_interest USING GIST (coordinates);
CREATE INDEX idx_pois_site_id ON points_of_interest(site_id);
CREATE INDEX idx_audio_poi_id ON audio_content(poi_id);
CREATE INDEX idx_audio_language ON audio_content(language_code, expertise_level);
CREATE INDEX idx_itinerary_user ON itineraries(user_id);
CREATE INDEX idx_stops_itinerary ON itinerary_stops(itinerary_id);
```

#### 1.2 Seed Data Generator (Backend - Day 3)

```typescript
// src/seed/giza.ts
import { db } from '../db';
import { sites, pointsOfInterest, audioContent } from '../db/schema';

export async function seedGiza() {
  // Insert Giza Pyramids site
  const [gizaSite] = await db.insert(sites).values({
    nameEn: 'Giza Pyramid Complex',
    nameAr: 'مجمع أهرامات الجيزة',
    nameFr: 'Complexe pyramidal de Gizeh',
    descriptionEn: 'The Giza pyramid complex is an archaeological site on the Giza Plateau, on the outskirts of Cairo, Egypt.',
    descriptionAr: 'مجمع أهرامات الجيزة هو موقع أثري على هضبة الجيزة، على مشارف القاهرة، مصر.',
    descriptionFr: 'Le complexe pyramidal de Gizeh est un site archéologique sur le plateau de Gizeh, à la périphérie du Caire, en Égypte.',
    region: 'cairo',
    coordinates: { type: 'Point', coordinates: [31.1342, 29.9792] },
    openingHours: {
      monday: { open: '08:00', close: '17:00' },
      tuesday: { open: '08:00', close: '17:00' },
      wednesday: { open: '08:00', close: '17:00' },
      thursday: { open: '08:00', close: '17:00' },
      friday: { open: '08:00', close: '17:00' },
      saturday: { open: '08:00', close: '17:00' },
      sunday: { open: '08:00', close: '17:00' },
    },
    entryFee: {
      egyptian: 60,
      foreign: 200,
      student: 100,
      currency: 'EGP',
    },
    imageUrl: 'https://cdn.egytour.app/sites/giza-pyramids.jpg',
  }).returning();

  // Insert POIs
  const pois = [
    {
      siteId: gizaSite.id,
      nameEn: 'Great Pyramid of Khufu',
      nameAr: 'هرم خوفو الأكبر',
      nameFr: 'Grande pyramide de Khéops',
      poiType: 'pyramid',
      coordinates: { type: 'Point', coordinates: [31.1342, 29.9792] },
      triggerRadiusMeters: 30,
      historicalPeriod: 'old_kingdom',
      importanceRank: 10,
      imageUrl: 'https://cdn.egytour.app/pois/great-pyramid.jpg',
    },
    {
      siteId: gizaSite.id,
      nameEn: 'The Great Sphinx',
      nameAr: 'أبو الهول',
      nameFr: 'Le Grand Sphinx',
      poiType: 'statue',
      coordinates: { type: 'Point', coordinates: [31.1376, 29.9753] },
      triggerRadiusMeters: 25,
      historicalPeriod: 'old_kingdom',
      importanceRank: 10,
      imageUrl: 'https://cdn.egytour.app/pois/sphinx.jpg',
    },
    {
      siteId: gizaSite.id,
      nameEn: 'Pyramid of Khafre',
      nameAr: 'هرم خفرع',
      nameFr: 'Pyramide de Khéphren',
      poiType: 'pyramid',
      coordinates: { type: 'Point', coordinates: [31.1308, 29.9759] },
      triggerRadiusMeters: 30,
      historicalPeriod: 'old_kingdom',
      importanceRank: 9,
      imageUrl: 'https://cdn.egytour.app/pois/khafre-pyramid.jpg',
    },
    {
      siteId: gizaSite.id,
      nameEn: 'Pyramid of Menkaure',
      nameAr: 'هرم منقرع',
      nameFr: 'Pyramide de Mykérinos',
      poiType: 'pyramid',
      coordinates: { type: 'Point', coordinates: [31.1281, 29.9722] },
      triggerRadiusMeters: 25,
      historicalPeriod: 'old_kingdom',
      importanceRank: 8,
      imageUrl: 'https://cdn.egytour.app/pois/menkaure-pyramid.jpg',
    },
    {
      siteId: gizaSite.id,
      nameEn: 'Solar Boat Museum',
      nameAr: 'متحف مركب الشمس',
      nameFr: 'Musée de la barque solaire',
      poiType: 'museum',
      coordinates: { type: 'Point', coordinates: [31.1351, 29.9784] },
      triggerRadiusMeters: 20,
      historicalPeriod: 'old_kingdom',
      importanceRank: 7,
      imageUrl: 'https://cdn.egytour.app/pois/solar-boat.jpg',
    },
  ];

  const insertedPOIs = await db.insert(pointsOfInterest).values(pois).returning();

  // Insert audio content for each POI (3 languages × 3 expertise levels)
  const audioEntries = [];
  for (const poi of insertedPOIs) {
    const languages = ['en', 'ar', 'fr'];
    const levels = ['casual', 'enthusiast', 'scholar'];
    
    for (const lang of languages) {
      for (const level of levels) {
        audioEntries.push({
          poiId: poi.id,
          languageCode: lang,
          expertiseLevel: level,
          audioUrl: `https://cdn.egytour.app/audio/${poi.id}/${lang}/${level}.mp3`,
          audioDurationSeconds: level === 'casual' ? 120 : level === 'enthusiast' ? 240 : 360,
          transcript: `[${level}] Audio transcript for ${poi.nameEn} in ${lang}`,
          narratorName: lang === 'en' ? 'James Wilson' : lang === 'ar' ? 'أحمد محمد' : 'Marie Dubois',
          fileSizeBytes: level === 'casual' ? 2_000_000 : level === 'enthusiast' ? 4_000_000 : 6_000_000,
        });
      }
    }
  }

  await db.insert(audioContent).values(audioEntries);

  console.log('✅ Giza Pyramids seeded successfully!');
}
```

#### 1.3 Basic API Endpoints (Backend - Day 4-5)

```typescript
// src/routes/sites.ts
import { FastifyPluginAsync } from 'fastify';
import { z } from 'zod';
import { db } from '../db';
import { sites, pointsOfInterest } from '../db/schema';
import { eq, sql } from 'drizzle-orm';

const sitesRoutes: FastifyPluginAsync = async (fastify) => {
  // GET /api/v1/sites - List all sites
  fastify.get('/', async (request, reply) => {
    const { region } = request.query as { region?: string };
    
    let query = db.select().from(sites);
    
    if (region) {
      query = query.where(eq(sites.region, region));
    }
    
    const allSites = await query;
    
    return { sites: allSites };
  });

  // GET /api/v1/sites/:id - Get site with POIs
  fastify.get('/:id', async (request, reply) => {
    const { id } = request.params as { id: string };
    
    const site = await db.select().from(sites).where(eq(sites.id, id)).limit(1);
    
    if (!site.length) {
      return reply.code(404).send({ error: 'Site not found' });
    }
    
    const pois = await db.select().from(pointsOfInterest).where(eq(pointsOfInterest.siteId, id));
    
    return {
      site: site[0],
      pois,
    };
  });

  // GET /api/v1/sites/:id/pois - Get POIs for a site
  fastify.get('/:id/pois', async (request, reply) => {
    const { id } = request.params as { id: string };
    
    const pois = await db.select().from(pointsOfInterest).where(eq(pointsOfInterest.siteId, id));
    
    return { pois };
  });
};

// src/routes/pois.ts
const poisRoutes: FastifyPluginAsync = async (fastify) => {
  // GET /api/v1/pois/:id - Get single POI with audio
  fastify.get('/:id', async (request, reply) => {
    const { id } = request.params as { id: string };
    
    const poi = await db.select().from(pointsOfInterest).where(eq(pointsOfInterest.id, id)).limit(1);
    
    if (!poi.length) {
      return reply.code(404).send({ error: 'POI not found' });
    }
    
    const audio = await db.select().from(audioContent).where(eq(audioContent.poiId, id));
    
    return {
      poi: poi[0],
      audioContent: audio,
    };
  });

  // GET /api/v1/pois/nearby - Find nearby POIs
  fastify.get('/nearby', async (request, reply) => {
    const { lat, lon, radius = 1000 } = request.query as { lat: string; lon: string; radius?: number };
    
    const userPoint = `POINT(${lon} ${lat})`;
    
    const nearbyPOIs = await db.execute(sql`
      SELECT *, 
             ST_Distance(coordinates::geography, ST_GeogFromText(${userPoint})) as distance
      FROM points_of_interest
      WHERE ST_DWithin(coordinates::geography, ST_GeogFromText(${userPoint}), ${radius})
      ORDER BY distance ASC
      LIMIT 20
    `);
    
    return { pois: nearbyPOIs.rows };
  });
};

export { sitesRoutes, poisRoutes };
```

#### 1.4 iOS Project Setup (iOS Team - Day 1-3)

```swift
// Run setup script from Week 1 Kickstart Guide
// Then implement base models

// Models/Site.swift
import Foundation

struct Site: Codable, Identifiable {
    let id: String
    let nameEn: String
    let nameAr: String
    let nameFr: String
    let descriptionEn: String
    let descriptionAr: String
    let descriptionFr: String
    let region: String
    let coordinates: Coordinates
    let openingHours: OpeningHours?
    let entryFee: EntryFee?
    let imageUrl: String?
    let contentVersion: String
    
    var name: String {
        // Return based on user's preferred language
        return nameEn
    }
}

struct Coordinates: Codable {
    let latitude: Double
    let longitude: Double
}

struct OpeningHours: Codable {
    let monday: DayHours?
    let tuesday: DayHours?
    let wednesday: DayHours?
    let thursday: DayHours?
    let friday: DayHours?
    let saturday: DayHours?
    let sunday: DayHours?
}

struct DayHours: Codable {
    let open: String
    let close: String
}

struct EntryFee: Codable {
    let egyptian: Int
    let foreign: Int
    let student: Int
    let currency: String
}

// Models/POI.swift
struct POI: Codable, Identifiable {
    let id: String
    let siteId: String
    let nameEn: String
    let nameAr: String
    let nameFr: String
    let poiType: String
    let coordinates: Coordinates
    let triggerRadiusMeters: Int
    let historicalPeriod: String?
    let importanceRank: Int
    let imageUrl: String?
    
    var name: String {
        return nameEn
    }
}

// Services/APIService.swift
import Foundation

class APIService {
    static let shared = APIService()
    private let baseURL = "https://api.egytour.app"
    
    func fetchSites() async throws -> [Site] {
        let url = URL(string: "\(baseURL)/api/v1/sites")!
        let (data, _) = try await URLSession.shared.data(from: url)
        let response = try JSONDecoder().decode(SitesResponse.self, from: data)
        return response.sites
    }
    
    func fetchPOIs(for siteId: String) async throws -> [POI] {
        let url = URL(string: "\(baseURL)/api/v1/sites/\(siteId)/pois")!
        let (data, _) = try await URLSession.shared.data(from: url)
        let response = try JSONDecoder().decode(POIsResponse.self, from: data)
        return response.pois
    }
}

struct SitesResponse: Codable {
    let sites: [Site]
}

struct POIsResponse: Codable {
    let pois: [POI]
}
```

#### 1.5 Android Project Setup (Android Team - Day 1-3)

```kotlin
// Run setup script from Week 1 Kickstart Guide
// Then implement base models

// data/model/Site.kt
package com.egytour.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Site(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val nameFr: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val descriptionFr: String,
    val region: String,
    val coordinates: Coordinates,
    val openingHours: OpeningHours? = null,
    val entryFee: EntryFee? = null,
    val imageUrl: String? = null,
    val contentVersion: String
) {
    val name: String get() = nameEn // Based on user preference
}

@Serializable
data class Coordinates(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class OpeningHours(
    val monday: DayHours? = null,
    val tuesday: DayHours? = null,
    val wednesday: DayHours? = null,
    val thursday: DayHours? = null,
    val friday: DayHours? = null,
    val saturday: DayHours? = null,
    val sunday: DayHours? = null
)

@Serializable
data class DayHours(
    val open: String,
    val close: String
)

@Serializable
data class EntryFee(
    val egyptian: Int,
    val foreign: Int,
    val student: Int,
    val currency: String
)

// data/model/POI.kt
@Serializable
data class POI(
    val id: String,
    val siteId: String,
    val nameEn: String,
    val nameAr: String,
    val nameFr: String,
    val poiType: String,
    val coordinates: Coordinates,
    val triggerRadiusMeters: Int,
    val historicalPeriod: String? = null,
    val importanceRank: Int,
    val imageUrl: String? = null
) {
    val name: String get() = nameEn
}

// data/remote/ApiService.kt
package com.egytour.data.remote

import com.egytour.data.model.Site
import com.egytour.data.model.POI
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/v1/sites")
    suspend fun getSites(): SitesResponse
    
    @GET("api/v1/sites/{id}/pois")
    suspend fun getPOIs(@Path("id") siteId: String): POIsResponse
}

data class SitesResponse(val sites: List<Site>)
data class POIsResponse(val pois: List<POI>)
```

### Sprint 1 Success Criteria
- ✅ Database schema created with all tables
- ✅ Giza Pyramids site seeded with 5 POIs
- ✅ API endpoints functional and tested
- ✅ iOS project fetches and displays sites
- ✅ Android project fetches and displays sites
- ✅ CI/CD pipelines configured

---

## 📅 Sprint 2: Audio Engine (Weeks 3-4)

### Sprint Goal
Implement location-based audio triggers and audio playback on both platforms.

### Tasks

#### 2.1 Location Service (iOS - Day 1-2)

```swift
// Services/LocationService.swift
import CoreLocation
import Combine

class LocationService: NSObject, ObservableObject {
    @Published var currentLocation: CLLocation?
    @Published var authorizationStatus: CLAuthorizationStatus = .notDetermined
    
    private let locationManager = CLLocationManager()
    private var cancellables = Set<AnyCancellable>()
    
    override init() {
        super.init()
        setupLocationManager()
    }
    
    private func setupLocationManager() {
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.distanceFilter = 10 // Update every 10 meters
        locationManager.allowsBackgroundLocationUpdates = true
        locationManager.pausesLocationUpdatesAutomatically = false
    }
    
    func requestPermissions() {
        locationManager.requestWhenInUseAuthorization()
        locationManager.requestAlwaysAuthorization()
    }
    
    func startTracking() {
        locationManager.startUpdatingLocation()
    }
    
    func stopTracking() {
        locationManager.stopUpdatingLocation()
    }
}

extension LocationService: CLLocationManagerDelegate {
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        currentLocation = location
        checkProximityToPOIs(location: location)
    }
    
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        authorizationStatus = manager.authorizationStatus
    }
    
    private func checkProximityToPOIs(location: CLLocation) {
        Task {
            do {
                let nearbyPOIs = try await APIService.shared.fetchNearbyPOIs(
                    lat: location.coordinate.latitude,
                    lon: location.coordinate.longitude,
                    radius: 100
                )
                
                for poi in nearbyPOIs {
                    let poiLocation = CLLocation(
                        latitude: poi.coordinates.latitude,
                        longitude: poi.coordinates.longitude
                    )
                    
                    let distance = location.distance(from: poiLocation)
                    
                    if distance <= Double(poi.triggerRadiusMeters) {
                        if shouldTriggerAudio(for: poi) {
                            AudioService.shared.triggerAudio(for: poi)
                        }
                    }
                }
            } catch {
                print("Error fetching nearby POIs: \(error)")
            }
        }
    }
    
    private func shouldTriggerAudio(for poi: POI) -> Bool {
        let lastTriggerKey = "lastTrigger_\(poi.id)"
        
        if let lastTrigger = UserDefaults.standard.object(forKey: lastTriggerKey) as? Date {
            let tenMinutesAgo = Date().addingTimeInterval(-10 * 60)
            return lastTrigger < tenMinutesAgo
        }
        
        return true
    }
}
```

#### 2.2 Audio Service (iOS - Day 3-4)

```swift
// Services/AudioService.swift
import AVFoundation
import MediaPlayer

class AudioService: ObservableObject {
    static let shared = AudioService()
    
    @Published var isPlaying = false
    @Published var currentPOI: POI?
    @Published var progress: Double = 0
    @Published var duration: Double = 0
    
    private var player: AVPlayer?
    private var timeObserver: Any?
    
    init() {
        setupAudioSession()
        setupRemoteCommands()
    }
    
    private func setupAudioSession() {
        do {
            try AVAudioSession.sharedInstance().setCategory(.playback, mode: .default)
            try AVAudioSession.sharedInstance().setActive(true)
        } catch {
            print("Failed to setup audio session: \(error)")
        }
    }
    
    private func setupRemoteCommands() {
        let commandCenter = MPRemoteCommandCenter.shared()
        
        commandCenter.playCommand.addTarget { [weak self] _ in
            self?.play()
            return .success
        }
        
        commandCenter.pauseCommand.addTarget { [weak self] _ in
            self?.pause()
            return .success
        }
    }
    
    func triggerAudio(for poi: POI) {
        // Save last trigger time
        UserDefaults.standard.set(Date(), forKey: "lastTrigger_\(poi.id)")
        
        // Show notification
        NotificationService.shared.showPOINotification(poi: poi)
        
        // Auto-play if enabled
        if UserDefaults.standard.bool(forKey: "autoPlayAudio") {
            loadAudio(for: poi)
        }
    }
    
    func loadAudio(for poi: POI) {
        currentPOI = poi
        
        // Get user's preferred language and expertise level
        let language = UserDefaults.standard.string(forKey: "preferredLanguage") ?? "en"
        let expertise = UserDefaults.standard.string(forKey: "expertiseLevel") ?? "casual"
        
        // Construct audio URL
        let audioURL = URL(string: "https://cdn.egytour.app/audio/\(poi.id)/\(language)/\(expertise).mp3")!
        
        let playerItem = AVPlayerItem(url: audioURL)
        player = AVPlayer(playerItem: playerItem)
        
        // Observe playback progress
        timeObserver = player?.addPeriodicTimeObserver(
            forInterval: CMTime(seconds: 0.5, preferredTimescale: 600),
            queue: .main
        ) { [weak self] time in
            guard let self = self,
                  let duration = self.player?.currentItem?.duration.seconds,
                  duration.isFinite else { return }
            
            self.progress = time.seconds / duration
            self.duration = duration
        }
        
        play()
    }
    
    func play() {
        player?.play()
        isPlaying = true
        updateNowPlaying()
    }
    
    func pause() {
        player?.pause()
        isPlaying = false
    }
    
    func seek(to progress: Double) {
        guard let duration = player?.currentItem?.duration.seconds else { return }
        let time = CMTime(seconds: duration * progress, preferredTimescale: 600)
        player?.seek(to: time)
    }
    
    private func updateNowPlaying() {
        guard let poi = currentPOI else { return }
        
        var nowPlayingInfo = [String: Any]()
        nowPlayingInfo[MPMediaItemPropertyTitle] = poi.name
        nowPlayingInfo[MPMediaItemPropertyArtist] = "EgyTour"
        nowPlayingInfo[MPNowPlayingInfoPropertyElapsedPlaybackTime] = player?.currentTime().seconds
        nowPlayingInfo[MPMediaItemPropertyPlaybackDuration] = duration
        
        MPNowPlayingInfoCenter.default().nowPlayingInfo = nowPlayingInfo
    }
}
```

#### 2.3 Audio Player UI (iOS - Day 5)

```swift
// Features/POI/Views/AudioPlayerView.swift
import SwiftUI

struct AudioPlayerView: View {
    @ObservedObject var audioService = AudioService.shared
    let poi: POI
    
    var body: some View {
        VStack(spacing: 16) {
            // POI Image
            AsyncImage(url: URL(string: poi.imageUrl ?? "")) { image in
                image
                    .resizable()
                    .aspectRatio(contentMode: .fill)
            } placeholder: {
                Color.bgSecondary
            }
            .frame(height: 200)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            
            // POI Info
            VStack(alignment: .leading, spacing: 8) {
                Text(poi.historicalPeriod ?? "")
                    .font(.caption)
                    .foregroundColor(.primaryGold)
                    .textCase(.uppercase)
                
                Text(poi.name)
                    .font(.siteName)
                    .foregroundColor(.textPrimary)
                
                Text(formatDuration(audioService.duration))
                    .font(.mono)
                    .foregroundColor(.textTertiary)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            
            // Progress Bar
            VStack(spacing: 8) {
                GeometryReader { geometry in
                    ZStack(alignment: .leading) {
                        Rectangle()
                            .fill(Color.border)
                            .frame(height: 4)
                        
                        Rectangle()
                            .fill(Color.papyrusOrange)
                            .frame(width: geometry.size.width * audioService.progress, height: 4)
                    }
                    .clipShape(Capsule())
                }
                .frame(height: 4)
                
                HStack {
                    Text(formatTime(audioService.progress * audioService.duration))
                        .font(.caption)
                        .foregroundColor(.textSecondary)
                    
                    Spacer()
                    
                    Text(formatTime(audioService.duration))
                        .font(.caption)
                        .foregroundColor(.textSecondary)
                }
            }
            
            // Controls
            HStack(spacing: 32) {
                Button(action: { audioService.seek(to: max(0, audioService.progress - 0.1)) }) {
                    Image(systemName: "gobackward.10")
                        .font(.title2)
                        .foregroundColor(.textPrimary)
                }
                
                Button(action: {
                    if audioService.isPlaying {
                        audioService.pause()
                    } else {
                        audioService.play()
                    }
                }) {
                    ZStack {
                        Circle()
                            .fill(Color.primaryGold)
                            .frame(width: 64, height: 64)
                        
                        Image(systemName: audioService.isPlaying ? "pause.fill" : "play.fill")
                            .font(.title)
                            .foregroundColor(.bgPrimary)
                    }
                }
                .scaleEffect(audioService.isPlaying ? 1.0 : 1.0)
                .animation(.spring(), value: audioService.isPlaying)
                
                Button(action: { audioService.seek(to: min(1, audioService.progress + 0.1)) }) {
                    Image(systemName: "goforward.10")
                        .font(.title2)
                        .foregroundColor(.textPrimary)
                }
            }
            
            // Ask AI Button
            Button(action: {
                // Navigate to AI chat
            }) {
                HStack {
                    Image(systemName: "bubble.left.and.bubble.right")
                    Text("Ask a Question")
                        .fontWeight(.semibold)
                }
                .frame(maxWidth: .infinity)
                .frame(height: 48)
                .background(Color.bgSecondary)
                .foregroundColor(.textPrimary)
                .cornerRadius(12)
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .stroke(Color.border, lineWidth: 1)
                )
            }
        }
        .padding()
        .background(Color.bgPrimary)
        .onAppear {
            if audioService.currentPOI?.id != poi.id {
                audioService.loadAudio(for: poi)
            }
        }
    }
    
    private func formatDuration(_ seconds: Double) -> String {
        let minutes = Int(seconds) / 60
        return "\(minutes) min"
    }
    
    private func formatTime(_ seconds: Double) -> String {
        let mins = Int(seconds) / 60
        let secs = Int(seconds) % 60
        return String(format: "%d:%02d", mins, secs)
    }
}
```

#### 2.4 Location & Audio Service (Android - Day 1-4)

```kotlin
// services/LocationService.kt
package com.egytour.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationService @Inject constructor(
    private val context: Context,
    private val audioService: AudioService,
    private val apiService: ApiService
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val lastTriggered = mutableMapOf<String, Long>()
    
    @SuppressLint("MissingPermission")
    fun getLocationUpdates(): Flow<Location> = callbackFlow {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // 10 seconds
        ).apply {
            setMinUpdateDistanceMeters(10f)
            setWaitForAccurateLocation(true)
        }.build()
        
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(location)
                    checkProximityToPOIs(location)
                }
            }
        }
        
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            context.mainLooper
        )
        
        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
    
    private suspend fun checkProximityToPOIs(location: Location) {
        try {
            val nearbyPOIs = apiService.getNearbyPOIs(
                location.latitude,
                location.longitude,
                100
            )
            
            for (poi in nearbyPOIs.pois) {
                val poiLocation = Location("").apply {
                    latitude = poi.coordinates.latitude
                    longitude = poi.coordinates.longitude
                }
                
                val distance = location.distanceTo(poiLocation)
                
                if (distance <= poi.triggerRadiusMeters) {
                    if (shouldTriggerAudio(poi.id)) {
                        audioService.triggerAudio(poi)
                        lastTriggered[poi.id] = System.currentTimeMillis()
                    }
                }
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    private fun shouldTriggerAudio(poiId: String): Boolean {
        val lastTrigger = lastTriggered[poiId] ?: return true
        val tenMinutesAgo = System.currentTimeMillis() - (10 * 60 * 1000)
        return lastTrigger < tenMinutesAgo
    }
}

// services/AudioService.kt
package com.egytour.services

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioService @Inject constructor(
    private val context: Context
) {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    
    private val _currentPOI = MutableStateFlow<POI?>(null)
    val currentPOI: StateFlow<POI?> = _currentPOI
    
    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress
    
    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                _isPlaying.value = playing
            }
        })
    }
    
    fun triggerAudio(poi: POI) {
        // Show notification
        // Auto-play if enabled
        loadAudio(poi)
    }
    
    fun loadAudio(poi: POI) {
        _currentPOI.value = poi
        
        // Get user preferences
        val language = "en" // From SharedPreferences
        val expertise = "casual" // From SharedPreferences
        
        val audioUrl = "https://cdn.egytour.app/audio/${poi.id}/$language/$expertise.mp3"
        
        val mediaItem = MediaItem.fromUri(audioUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }
    
    fun play() {
        player.play()
    }
    
    fun pause() {
        player.pause()
    }
    
    fun seekTo(progress: Float) {
        val position = (player.duration * progress).toLong()
        player.seekTo(position)
    }
}
```

### Sprint 2 Success Criteria
- ✅ Location tracking works on both platforms
- ✅ Audio triggers when entering POI radius
- ✅ Audio player UI functional with controls
- ✅ Background audio playback works
- ✅ Lock screen controls implemented
- ✅ Debounce logic prevents re-triggers

---

*Due to length constraints, I'll create the remaining sprints (3-6) in a separate continuation. Would you like me to continue with Sprint 3 (Maps & AR), Sprint 4 (AI Features), Sprint 5 (Trip Planning), and Sprint 6 (Polish & Launch)?*
