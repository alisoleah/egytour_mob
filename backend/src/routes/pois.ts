import { FastifyPluginAsync } from 'fastify';
import { db } from '../db';
import { pointsOfInterest, audioContent } from '../db/schema';
import { eq, sql } from 'drizzle-orm';

const poisRoutes: FastifyPluginAsync = async (fastify) => {
    // GET /api/v1/pois/:id - Get single POI with audio
    fastify.get('/:id', async (request, reply) => {
        const { id } = request.params as { id: string };

        const [poi] = await db.select().from(pointsOfInterest).where(eq(pointsOfInterest.id, id)).limit(1);

        if (!poi) {
            return reply.code(404).send({ error: 'POI not found' });
        }

        // Get coordinates
        const [coords] = await db.execute(sql`
      SELECT ST_Y(coordinates::geometry) as latitude, 
             ST_X(coordinates::geometry) as longitude
      FROM points_of_interest WHERE id = ${id}
    `);

        // Get audio content
        const audio = await db.select().from(audioContent).where(eq(audioContent.poiId, id));

        return {
            poi: {
                ...poi,
                coordinates: {
                    latitude: coords.rows[0].latitude,
                    longitude: coords.rows[0].longitude,
                },
            },
            audioContent: audio,
        };
    });

    // GET /api/v1/pois/nearby - Find nearby POIs
    fastify.get('/nearby', async (request, reply) => {
        const { lat, lon, radius = 1000 } = request.query as {
            lat: string;
            lon: string;
            radius?: number;
        };

        if (!lat || !lon) {
            return reply.code(400).send({ error: 'lat and lon are required' });
        }

        const userPoint = `POINT(${lon} ${lat})`;

        const nearbyPOIs = await db.execute(sql`
      SELECT 
        poi.*,
        ST_Y(poi.coordinates::geometry) as latitude,
        ST_X(poi.coordinates::geometry) as longitude,
        ST_Distance(poi.coordinates::geography, ST_GeogFromText(${userPoint})) as distance
      FROM points_of_interest poi
      WHERE ST_DWithin(poi.coordinates::geography, ST_GeogFromText(${userPoint}), ${radius})
      ORDER BY distance ASC
      LIMIT 20
    `);

        const formattedPOIs = nearbyPOIs.rows.map((poi: any) => ({
            id: poi.id,
            siteId: poi.site_id,
            nameEn: poi.name_en,
            nameAr: poi.name_ar,
            nameFr: poi.name_fr,
            poiType: poi.poi_type,
            coordinates: {
                latitude: poi.latitude,
                longitude: poi.longitude,
            },
            triggerRadiusMeters: poi.trigger_radius_meters,
            historicalPeriod: poi.historical_period,
            importanceRank: poi.importance_rank,
            imageUrl: poi.image_url,
            distance: Math.round(poi.distance),
        }));

        return { pois: formattedPOIs };
    });
};

export default poisRoutes;
