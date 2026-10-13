import { FastifyPluginAsync } from 'fastify';
import { z } from 'zod';
import bcrypt from 'bcrypt';
import { db } from '../db';
import { users } from '../db/schema';
import { eq } from 'drizzle-orm';

const registerSchema = z.object({
    email: z.string().email(),
    password: z.string().min(8),
    displayName: z.string().min(2),
});

const loginSchema = z.object({
    email: z.string().email(),
    password: z.string(),
});

const authRoutes: FastifyPluginAsync = async (fastify) => {
    // Register
    fastify.post('/register', async (request, reply) => {
        try {
            const { email, password, displayName } = registerSchema.parse(request.body);

            // Check if user exists
            const existingUser = await db.select().from(users).where(eq(users.email, email)).limit(1);
            if (existingUser.length > 0) {
                return reply.code(400).send({ error: 'Email already registered' });
            }

            // Hash password
            const passwordHash = await bcrypt.hash(password, 10);

            // Create user
            const [newUser] = await db.insert(users).values({
                email,
                passwordHash,
                displayName,
            }).returning({
                id: users.id,
                email: users.email,
                displayName: users.displayName,
            });

            // Generate tokens
            const accessToken = fastify.jwt.sign(
                { userId: newUser.id },
                { expiresIn: '15m' }
            );

            const refreshToken = fastify.jwt.sign(
                { userId: newUser.id },
                { expiresIn: '7d' }
            );

            return {
                user: newUser,
                accessToken,
                refreshToken,
            };
        } catch (error) {
            if (error instanceof z.ZodError) {
                return reply.code(400).send({ error: 'Invalid input', details: error.errors });
            }
            throw error;
        }
    });

    // Login
    fastify.post('/login', async (request, reply) => {
        try {
            const { email, password } = loginSchema.parse(request.body);

            // Find user
            const [user] = await db.select().from(users).where(eq(users.email, email)).limit(1);

            if (!user) {
                return reply.code(401).send({ error: 'Invalid credentials' });
            }

            // Verify password
            const isValid = await bcrypt.compare(password, user.passwordHash);
            if (!isValid) {
                return reply.code(401).send({ error: 'Invalid credentials' });
            }

            // Generate tokens
            const accessToken = fastify.jwt.sign(
                { userId: user.id },
                { expiresIn: '15m' }
            );

            const refreshToken = fastify.jwt.sign(
                { userId: user.id },
                { expiresIn: '7d' }
            );

            return {
                user: {
                    id: user.id,
                    email: user.email,
                    displayName: user.displayName,
                    preferredLanguage: user.preferredLanguage,
                    expertiseLevel: user.expertiseLevel,
                },
                accessToken,
                refreshToken,
            };
        } catch (error) {
            if (error instanceof z.ZodError) {
                return reply.code(400).send({ error: 'Invalid input', details: error.errors });
            }
            throw error;
        }
    });

    // Refresh token
    fastify.post('/refresh', async (request, reply) => {
        try {
            const { refreshToken } = request.body as { refreshToken: string };

            const decoded = fastify.jwt.verify(refreshToken) as { userId: string };

            const newAccessToken = fastify.jwt.sign(
                { userId: decoded.userId },
                { expiresIn: '15m' }
            );

            return { accessToken: newAccessToken };
        } catch (error) {
            return reply.code(401).send({ error: 'Invalid refresh token' });
        }
    });
};

export default authRoutes;
