-- Enable PostGIS extension
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;
DO $$ BEGIN CREATE EXTENSION IF NOT EXISTS vector; EXCEPTION WHEN OTHERS THEN NULL; END $$;

-- Sites table
CREATE TABLE IF NOT EXISTS sites (
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
CREATE TABLE IF NOT EXISTS points_of_interest (
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
CREATE TABLE IF NOT EXISTS audio_content (
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
CREATE TABLE IF NOT EXISTS users (
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
CREATE TABLE IF NOT EXISTS itineraries (
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
CREATE TABLE IF NOT EXISTS itinerary_stops (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    itinerary_id UUID REFERENCES itineraries(id) ON DELETE CASCADE,
    poi_id UUID REFERENCES points_of_interest(id),
    day_number INTEGER NOT NULL,
    order_index INTEGER NOT NULL,
    recommended_duration_minutes INTEGER,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_sites_coordinates ON sites USING GIST (coordinates);
CREATE INDEX IF NOT EXISTS idx_sites_region ON sites(region);
CREATE INDEX IF NOT EXISTS idx_pois_coordinates ON points_of_interest USING GIST (coordinates);
CREATE INDEX IF NOT EXISTS idx_pois_site_id ON points_of_interest(site_id);
CREATE INDEX IF NOT EXISTS idx_pois_type ON points_of_interest(poi_type);
CREATE INDEX IF NOT EXISTS idx_audio_poi_id ON audio_content(poi_id);
CREATE INDEX IF NOT EXISTS idx_audio_language ON audio_content(language_code, expertise_level);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_itinerary_user ON itineraries(user_id);
CREATE INDEX IF NOT EXISTS idx_stops_itinerary ON itinerary_stops(itinerary_id);

-- Knowledge Chunks table (RAG knowledge bank)
CREATE TABLE IF NOT EXISTS knowledge_chunks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    poi_id UUID REFERENCES points_of_interest(id) ON DELETE CASCADE,
    site_id UUID REFERENCES sites(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    historical_period TEXT,
    category TEXT DEFAULT 'history',
    language_code TEXT NOT NULL DEFAULT 'en',
    metadata JSONB,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_knowledge_poi_id ON knowledge_chunks(poi_id);
CREATE INDEX IF NOT EXISTS idx_knowledge_site_id ON knowledge_chunks(site_id);
CREATE INDEX IF NOT EXISTS idx_knowledge_category ON knowledge_chunks(category);
