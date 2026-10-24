import { FastifyPluginAsync } from 'fastify';
import fs from 'fs';
import path from 'path';

const audioRoutes: FastifyPluginAsync = async (fastify) => {
    // GET /api/v1/audio/:poiId/:language/:expertise - Stream audio tour with HTTP Range support
    fastify.get('/:poiId/:language/:expertise', async (request, reply) => {
        const { poiId, language, expertise } = request.params as {
            poiId: string;
            language: string;
            expertise: string;
        };

        const cleanExpertise = expertise.replace(/\.mp3$/, '');
        const cleanLanguage = language.toLowerCase();

        // Check language-specific file first
        const publicAudioDir = path.resolve(__dirname, '../../public/audio');
        let audioFilePath = path.join(publicAudioDir, `khufu_${cleanLanguage}_${cleanExpertise}.mp3`);

        if (!fs.existsSync(audioFilePath)) {
            // Try generic language casual
            audioFilePath = path.join(publicAudioDir, `khufu_${cleanLanguage}_casual.mp3`);
        }

        if (!fs.existsSync(audioFilePath)) {
            // Fallback to default
            audioFilePath = path.join(publicAudioDir, 'default.mp3');
        }

        if (!fs.existsSync(audioFilePath)) {
            return reply.code(404).send({ error: 'Audio track not found' });
        }

        const stat = fs.statSync(audioFilePath);
        const fileSize = stat.size;
        const range = request.headers.range;

        if (range) {
            // Parse Range header (e.g., "bytes=0-1024")
            const parts = range.replace(/bytes=/, '').split('-');
            const start = parseInt(parts[0], 10);
            const end = parts[1] ? parseInt(parts[1], 10) : fileSize - 1;

            if (start >= fileSize || end >= fileSize) {
                reply.header('Content-Range', `bytes */${fileSize}`);
                return reply.code(416).send('Requested Range Not Satisfiable');
            }

            const chunksize = end - start + 1;
            const file = fs.createReadStream(audioFilePath, { start, end });

            reply.raw.writeHead(206, {
                'Content-Range': `bytes ${start}-${end}/${fileSize}`,
                'Accept-Ranges': 'bytes',
                'Content-Length': chunksize,
                'Content-Type': 'audio/mpeg',
            });

            return reply.send(file);
        } else {
            reply.raw.writeHead(200, {
                'Content-Length': fileSize,
                'Content-Type': 'audio/mpeg',
                'Accept-Ranges': 'bytes',
            });

            const file = fs.createReadStream(audioFilePath);
            return reply.send(file);
        }
    });

    // Helper to get audio track info
    fastify.get('/info/:poiId', async (request, reply) => {
        const { poiId } = request.params as { poiId: string };
        return {
            poiId,
            availableLanguages: ['en', 'ar', 'fr'],
            expertiseTiers: ['casual', 'enthusiast', 'scholar'],
            streamUrlTemplate: '/api/v1/audio/{poiId}/{language}/{expertise}.mp3',
        };
    });
};

export default audioRoutes;
