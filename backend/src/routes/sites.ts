import { FastifyPluginAsync } from 'fastify';
import { db } from '../db';
import { sites, pointsOfInterest } from '../db/schema';
import { eq, sql } from 'drizzle-orm';

const sitesRoutes: FastifyPluginAsync = async (fastify) => {
    // GET /api/v1/sites - List all sites
    fastify.get('/', async (request, reply) => {
        const { region } = request.query as { region?: string };

        let query = db.select({
            id: sites.id,
            nameEn: sites.nameEn,
            nameAr: sites.nameAr,
            nameFr: sites.nameFr,
            descriptionEn: sites.descriptionEn,
            descriptionAr: sites.descriptionAr,
            descriptionFr: sites.descriptionFr,
            region: sites.region,
            openingHours: sites.openingHours,
            entryFee: sites.entryFee,
            imageUrl: sites.imageUrl,
        }).from(sites);

        if (region) {
            query = query.where(eq(sites.region, region));
        }

        const allSites = await query;

        // Add coordinates from geography column
        const sitesWithCoords = await Promise.all(
            allSites.map(async (site) => {
                const [coords] = await db.execute(sql`
          SELECT ST_Y(coordinates::geometry) as latitude, 
                 ST_X(coordinates::geometry) as longitude
          FROM sites WHERE id = ${site.id}
        `);

                return {
                    ...site,
                    coordinates: {
                        latitude: coords.rows[0].latitude,
                        longitude: coords.rows[0].longitude,
                    },
                };
            })
        );

        return { sites: sitesWithCoords };
    });

    // GET /api/v1/sites/:id - Get site with POIs
    fastify.get('/:id', async (request, reply) => {
        const { id } = request.params as { id: string };

        const [site] = await db.select().from(sites).where(eq(sites.id, id)).limit(1);

        if (!site) {
            return reply.code(404).send({ error: 'Site not found' });
        }

        // Get coordinates
        const [coords] = await db.execute(sql`
      SELECT ST_Y(coordinates::geometry) as latitude, 
             ST_X(coordinates::geometry) as longitude
      FROM sites WHERE id = ${id}
    `);

        // Get POIs
        const pois = await db.select().from(pointsOfInterest).where(eq(pointsOfInterest.siteId, id));

        // Add coordinates to POIs
        const poisWithCoords = await Promise.all(
            pois.map(async (poi) => {
                const [poiCoords] = await db.execute(sql`
          SELECT ST_Y(coordinates::geometry) as latitude, 
                 ST_X(coordinates::geometry) as longitude
          FROM points_of_interest WHERE id = ${poi.id}
        `);

                return {
                    ...poi,
                    coordinates: {
                        latitude: poiCoords.rows[0].latitude,
                        longitude: poiCoords.rows[0].longitude,
                    },
                };
            })
        );

        return {
            site: {
                ...site,
                coordinates: {
                    latitude: coords.rows[0].latitude,
                    longitude: coords.rows[0].longitude,
                },
            },
            pois: poisWithCoords,
        };
    });

    // GET /api/v1/sites/:id/pois - Get POIs for a site
    fastify.get('/:id/pois', async (request, reply) => {
        const { id } = request.params as { id: string };

        const pois = await db.select().from(pointsOfInterest).where(eq(pointsOfInterest.siteId, id));

        // Add coordinates to POIs
        const poisWithCoords = await Promise.all(
            pois.map(async (poi) => {
                const [coords] = await db.execute(sql`
          SELECT ST_Y(coordinates::geometry) as latitude, 
                 ST_X(coordinates::geometry) as longitude
          FROM points_of_interest WHERE id = ${poi.id}
        `);

                return {
                    ...poi,
                    coordinates: {
                        latitude: coords.rows[0].latitude,
                        longitude: coords.rows[0].longitude,
                    },
                };
            })
        );

        return { pois: poisWithCoords };
    });
};

export default sitesRoutes;
