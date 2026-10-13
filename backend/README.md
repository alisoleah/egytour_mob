# EgyTour Backend

Backend API for EgyTour mobile application.

## Setup

1. Install dependencies:
```bash
npm install
```

2. Create `.env` file:
```bash
cp .env.example .env
```

3. Update `.env` with your database credentials:
```
DATABASE_URL=postgresql://user:password@localhost:5432/egytour
JWT_SECRET=your-secret-key
GEMINI_API_KEY=your-gemini-key
```

4. Run database migrations:
```bash
npm run db:migrate
```

5. Seed the database:
```bash
npm run db:seed
```

## Development

Start the development server:
```bash
npm run dev
```

Server will run on `http://localhost:3000`

## API Endpoints

### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Refresh access token

### Sites
- `GET /api/v1/sites` - List all sites
- `GET /api/v1/sites/:id` - Get site with POIs
- `GET /api/v1/sites/:id/pois` - Get POIs for a site

### POIs
- `GET /api/v1/pois/:id` - Get single POI with audio content
- `GET /api/v1/pois/nearby?lat=29.9792&lon=31.1342&radius=1000` - Find nearby POIs

## Database

The database uses PostgreSQL with PostGIS extension for location-based queries.

### Tables
- `sites` - Archaeological sites
- `points_of_interest` - POIs within sites
- `audio_content` - Audio guides (3 languages × 3 expertise levels)
- `users` - User accounts
- `itineraries` - Trip plans
- `itinerary_stops` - POIs in trip plans

## Testing

```bash
npm test
```
