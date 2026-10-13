# EgyTour - Claude Code Project Context

## Project Overview
EgyTour is an AI-powered self-guided tour application for Egypt, providing tourists and locals with immersive audio guides, AR navigation, 3D historical reconstructions, and intelligent trip planning—all without requiring a human tour guide.

## Tech Stack
- **Mobile**:native 
- **Backend**: Node.js with Fastify for API, Python for AI services (if you have better solution use it)
- **Database**: PostgreSQL 15 + PostGIS + pgvector + Drizzle ORM
- **Cache/Queue**: Redis 7 + BullMQ
- **AI/ML**: gemini (Q&A), Whisper (STT), ElevenLabs (TTS), sentence-transformers (embeddings)
- **Maps**: Mapbox GL Native with custom Egypt styling
- **Storage**: AWS S3 for audio/3D assets, CloudFront CDN

## Repository Structure
```
egytour/
├── apps/
│   ├── mobile/          # React Native (Expo) + ViroReact
│   ├── web/             # Next.js 14 (marketing + web app)
│   └── api/             # Fastify backend
├── packages/
│   ├── db/              # Drizzle schema + migrations
│   ├── ui/              # Shared React components
│   ├── audio-engine/    # Audio playback + location triggers
│   └── shared/          # TypeScript types, utils
├── services/
│   ├── ai-guide/        # RAG-powered Q&A service
│   ├── tts-generator/   # Audio narration generation
│   ├── trip-planner/    # Itinerary optimization
│   └── ar-assets/       # 3D model processing
├── content/
│   ├── sites/           # Site JSON definitions
│   ├── pois/            # Point of interest data
│   └── translations/    # i18n content
└── docker/
```

## Key Design Decisions
1. **Offline-First Architecture**: All core features work without internet; content synced via delta updates
2. **Content Versioning**: Each entity has `content_version` for efficient sync
3. **Audio Trigger System**: POIs have `trigger_radius_meters` (default 20m) for auto-play
4. **Multi-Language Audio**: Separate audio files per language, downloaded on-demand
5. **RAG for Q&A**: AI answers grounded in Egyptologist-verified knowledge base
6. **AR Quality Tiers**: Auto-detect device capability and serve appropriate 3D detail level

## Database Schema Overview
- **sites**: Major archaeological locations (Giza, Luxor, Karnak)
- **points_of_interest**: Individual POIs within sites
- **audio_content**: Multi-language narrations per POI
- **ar_assets**: 3D models and AR overlays
- **users**: User profiles with language/expertise preferences
- **itineraries**: AI-generated or manual trip plans
- **offline_packages**: Bundled content for download

## Current Sprint Focus
Building MVP with core features:
1. Location-triggered audio playback
2. Offline content download system
3. Basic AR wayfinding
4. AI-powered trip planner

## Commands
```bash
pnpm dev              # Start all services
pnpm db:migrate       # Run migrations
pnpm db:seed          # Seed test data (Giza complex)
pnpm test             # Run tests
pnpm content:sync     # Sync content from CMS
pnpm audio:generate   # Generate TTS for new content
```

## Important Files
- `/packages/db/schema.ts` - Database schema
- `/packages/audio-engine/trigger.ts` - Location-based audio triggers
- `/apps/api/routers/` - API endpoints
- `/apps/mobile/app/` - Mobile screens
- `/services/ai-guide/rag.ts` - RAG implementation
- `/content/sites/` - Site definitions

## Environment Variables
```env
DATABASE_URL=postgresql://...
REDIS_URL=redis://...
MAPBOX_ACCESS_TOKEN=pk...
CLAUDE_API_KEY=sk-ant-...
ELEVENLABS_API_KEY=...
WHISPER_API_KEY=...
AWS_S3_BUCKET=egytour-assets
AWS_CLOUDFRONT_DOMAIN=cdn.egytour.app
```

## Content Pipeline
1. **Egyptologist writes content** → Markdown in `/content/`
2. **Review & approve** → Content moves to `published` status
3. **TTS generation** → Audio files generated for each language
4. **3D asset processing** → Models optimized for mobile
5. **CDN deployment** → Assets pushed to CloudFront
6. **App sync** → Mobile app pulls delta updates

## Design System
See `design-system.md` for colors, typography, and animation specs.

## Implementation Roadmap
See `implementation-roadmap.md` for sprint-by-sprint development plan.

## API Rate Limits
- AI Q&A: 10 requests/minute per user
- Search: 30 requests/minute per user
- Content download: 100MB/hour per user

## Testing Strategy
- Unit tests: Vitest for business logic
- Integration tests: Supertest for API
- E2E tests: Detox for mobile flows
- Content tests: Validate all POIs have audio for all languages
