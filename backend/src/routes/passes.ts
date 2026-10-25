import { FastifyPluginAsync } from 'fastify';
import { z } from 'zod';
import { and, desc, eq } from 'drizzle-orm';
import { db } from '../db';
import { userPasses, users } from '../db/schema';
import { TourosClient } from '../services/touros.client';

const activateSchema = z.object({
    code: z
        .string()
        .trim()
        .toUpperCase()
        .regex(/^EG-[A-Z2-9]{8}$/, 'Pass codes look like EG-XXXXXXXX (8 letters/digits, no 0/O/1/I)'),
});

// Experience catalog ids that grant full VIP access, with their pass duration.
const VIP_EXPERIENCE_HOURS: Record<string, number> = {
    'egytour-vip-all-access': 72,
};
const DEFAULT_PASS_HOURS = 24;

const passesRoutes: FastifyPluginAsync = async (fastify) => {
    const touros = new TourosClient();

    // All pass endpoints require the app's JWT ({ userId } payload).
    fastify.addHook('preHandler', async (request, reply) => {
        try {
            await request.jwtVerify();
        } catch {
            return reply.code(401).send({ success: false, error: 'Unauthorized' });
        }
    });

    /**
     * Activate a hotel-issued pass code (sold to the guest at the front desk
     * via the Tour_os PMS). Validates the code against the PMS, redeems it
     * (single use), records the pass, and upgrades the user's tier.
     */
    fastify.post('/activate', async (request, reply) => {
        try {
            const { code } = activateSchema.parse(request.body);
            const userId = (request.user as { userId: string }).userId;

            // Idempotent: re-entering an already-activated code returns the pass.
            const existing = await db
                .select()
                .from(userPasses)
                .where(and(eq(userPasses.accessCode, code), eq(userPasses.userId, userId)))
                .limit(1);
            if (existing.length > 0) {
                return {
                    success: true,
                    alreadyActive: true,
                    pass: existing[0],
                };
            }

            // Someone else may already own this code.
            const claimed = await db
                .select({ userId: userPasses.userId })
                .from(userPasses)
                .where(eq(userPasses.accessCode, code))
                .limit(1);
            if (claimed.length > 0) {
                return reply.code(409).send({
                    success: false,
                    error: 'This code has already been activated on another account',
                });
            }

            const validation = await touros.validateAccess(code);
            if (!validation.valid) {
                return reply.code(404).send({
                    success: false,
                    error: 'Invalid or unknown pass code',
                });
            }

            // Single-use redemption in the PMS (no-op in dev mode). A code
            // redeemed on another device is refused.
            const redeemed = await touros.redeem(code);
            if (!redeemed) {
                return reply.code(409).send({
                    success: false,
                    error: 'This code has already been used on another device',
                });
            }

            const experienceId = validation.experienceId ?? 'unknown';
            const durationHours = VIP_EXPERIENCE_HOURS[experienceId] ?? DEFAULT_PASS_HOURS;
            const expiresAt = new Date(Date.now() + durationHours * 3600_000);
            const isVip = experienceId in VIP_EXPERIENCE_HOURS;

            const [pass] = await db
                .insert(userPasses)
                .values({
                    userId,
                    accessCode: code,
                    experienceId,
                    experienceName: validation.experienceName ?? 'EgyTour Experience',
                    description: validation.description ?? null,
                    source: touros.isLive() ? 'touros' : 'dev',
                    expiresAt,
                })
                .returning();

            // VIP passes upgrade the account tier the app already tracks.
            if (isVip) {
                await db
                    .update(users)
                    .set({
                        subscriptionTier: 'vip',
                        subscriptionExpiresAt: expiresAt,
                        updatedAt: new Date(),
                    })
                    .where(eq(users.id, userId));
            }

            return {
                success: true,
                pass,
                subscriptionTier: isVip ? 'vip' : undefined,
                expiresAt,
            };
        } catch (error) {
            if (error instanceof z.ZodError) {
                return reply.code(400).send({
                    success: false,
                    error: 'Validation Error',
                    details: error.errors,
                });
            }
            request.log.error(error);
            return reply.code(502).send({
                success: false,
                error:
                    error instanceof Error && error.name === 'TourosError'
                        ? error.message
                        : 'Pass activation failed',
            });
        }
    });

    /** The authenticated user's activated passes. */
    fastify.get('/', async (request) => {
        const userId = (request.user as { userId: string }).userId;
        const passes = await db
            .select()
            .from(userPasses)
            .where(eq(userPasses.userId, userId))
            .orderBy(desc(userPasses.activatedAt));
        return { success: true, passes };
    });
};

export default passesRoutes;
