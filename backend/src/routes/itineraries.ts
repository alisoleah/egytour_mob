import { FastifyPluginAsync } from 'fastify';
import { z } from 'zod';
import { ItineraryGenerator } from '../services/itinerary.service';

const generateSchema = z.object({
    durationDays: z.number().int().min(1).max(14).default(3),
    regions: z.array(z.string()).default(['cairo', 'luxor']),
    interests: z.array(z.string()).optional(),
    expertiseLevel: z.string().optional().default('casual'),
    startDate: z.string().optional(),
});

const itineraryRoutes: FastifyPluginAsync = async (fastify) => {
    const generator = new ItineraryGenerator();

    // Generate AI Itinerary
    fastify.post('/generate', async (request, reply) => {
        try {
            const body = generateSchema.parse(request.body || {});
            const result = await generator.generateItinerary(body);

            return reply.status(200).send({
                success: true,
                itinerary: result,
            });
        } catch (error) {
            if (error instanceof z.ZodError) {
                return reply.status(400).send({
                    success: false,
                    error: 'Validation Error',
                    details: error.errors,
                });
            }

            request.log.error(error);
            return reply.status(500).send({
                success: false,
                error: 'Failed to generate itinerary',
            });
        }
    });

    // List Sample / User Itineraries
    fastify.get('/', async () => {
        const sample = await generator.generateItinerary({
            durationDays: 3,
            regions: ['cairo', 'luxor'],
            interests: ['ancient_history', 'architecture'],
            expertiseLevel: 'casual',
        });

        return {
            success: true,
            itineraries: [sample],
        };
    });
};

export default itineraryRoutes;
