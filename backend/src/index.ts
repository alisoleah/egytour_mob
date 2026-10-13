import Fastify from 'fastify';
import cors from '@fastify/cors';
import jwt from '@fastify/jwt';
import rateLimit from '@fastify/rate-limit';
import 'dotenv/config';

import authRoutes from './routes/auth';
import sitesRoutes from './routes/sites';
import poisRoutes from './routes/pois';
import aiRoutes from './routes/ai';
import itineraryRoutes from './routes/itineraries';

const app = Fastify({
    logger: {
        level: process.env.LOG_LEVEL || 'info',
    },
});

// Register plugins
app.register(cors, {
    origin: true,
});

app.register(jwt, {
    secret: process.env.JWT_SECRET!,
});

app.register(rateLimit, {
    max: 100,
    timeWindow: '1 minute',
});

// Register routes
app.register(authRoutes, { prefix: '/api/v1/auth' });
app.register(sitesRoutes, { prefix: '/api/v1/sites' });
app.register(poisRoutes, { prefix: '/api/v1/pois' });
app.register(aiRoutes, { prefix: '/api/v1/ai' });
app.register(itineraryRoutes, { prefix: '/api/v1/itineraries' });



// Health check
app.get('/health', async () => {
    return { status: 'ok', timestamp: new Date().toISOString() };
});

// Start server
const start = async () => {
    try {
        const port = parseInt(process.env.PORT || '3000');
        await app.listen({ port, host: '0.0.0.0' });
        console.log(`🚀 Server running on http://localhost:${port}`);
    } catch (err) {
        app.log.error(err);
        process.exit(1);
    }
};

start();
