import { pgTable, uuid, text, timestamp, integer, jsonb, date } from 'drizzle-orm/pg-core';

export const sites = pgTable('sites', {
    id: uuid('id').primaryKey().defaultRandom(),
    nameEn: text('name_en').notNull(),
    nameAr: text('name_ar').notNull(),
    nameFr: text('name_fr').notNull(),
    descriptionEn: text('description_en').notNull(),
    descriptionAr: text('description_ar').notNull(),
    descriptionFr: text('description_fr').notNull(),
    region: text('region').notNull(),
    // Note: coordinates stored as geography, queried with raw SQL
    openingHours: jsonb('opening_hours'),
    entryFee: jsonb('entry_fee'),
    imageUrl: text('image_url'),
    contentVersion: text('content_version').notNull().default('1.0.0'),
    createdAt: timestamp('created_at').defaultNow(),
    updatedAt: timestamp('updated_at').defaultNow(),
});

export const pointsOfInterest = pgTable('points_of_interest', {
    id: uuid('id').primaryKey().defaultRandom(),
    siteId: uuid('site_id').references(() => sites.id, { onDelete: 'cascade' }),
    nameEn: text('name_en').notNull(),
    nameAr: text('name_ar').notNull(),
    nameFr: text('name_fr').notNull(),
    poiType: text('poi_type').notNull(),
    // Note: coordinates stored as geography, queried with raw SQL
    triggerRadiusMeters: integer('trigger_radius_meters').default(20),
    historicalPeriod: text('historical_period'),
    importanceRank: integer('importance_rank').default(5),
    accessibilityInfo: jsonb('accessibility_info'),
    imageUrl: text('image_url'),
    contentVersion: text('content_version').notNull().default('1.0.0'),
    createdAt: timestamp('created_at').defaultNow(),
    updatedAt: timestamp('updated_at').defaultNow(),
});

export const audioContent = pgTable('audio_content', {
    id: uuid('id').primaryKey().defaultRandom(),
    poiId: uuid('poi_id').references(() => pointsOfInterest.id, { onDelete: 'cascade' }),
    languageCode: text('language_code').notNull(),
    expertiseLevel: text('expertise_level').notNull(),
    audioUrl: text('audio_url').notNull(),
    audioDurationSeconds: integer('audio_duration_seconds').notNull(),
    transcript: text('transcript').notNull(),
    narratorName: text('narrator_name'),
    fileSizeBytes: integer('file_size_bytes').notNull(),
    contentVersion: text('content_version').notNull().default('1.0.0'),
    createdAt: timestamp('created_at').defaultNow(),
});

export const users = pgTable('users', {
    id: uuid('id').primaryKey().defaultRandom(),
    email: text('email').notNull().unique(),
    passwordHash: text('password_hash').notNull(),
    displayName: text('display_name').notNull(),
    preferredLanguage: text('preferred_language').default('en'),
    expertiseLevel: text('expertise_level').default('casual'),
    subscriptionTier: text('subscription_tier').default('free'),
    subscriptionExpiresAt: timestamp('subscription_expires_at'),
    preferences: jsonb('preferences').default({ autoPlayAudio: true, downloadOnWifiOnly: true, theme: 'system' }),
    createdAt: timestamp('created_at').defaultNow(),
    updatedAt: timestamp('updated_at').defaultNow(),
});

export const itineraries = pgTable('itineraries', {
    id: uuid('id').primaryKey().defaultRandom(),
    userId: uuid('user_id').references(() => users.id, { onDelete: 'cascade' }),
    title: text('title').notNull(),
    startDate: date('start_date').notNull(),
    endDate: date('end_date').notNull(),
    regions: text('regions').array(),
    status: text('status').default('draft'),
    createdAt: timestamp('created_at').defaultNow(),
    updatedAt: timestamp('updated_at').defaultNow(),
});

export const itineraryStops = pgTable('itinerary_stops', {
    id: uuid('id').primaryKey().defaultRandom(),
    itineraryId: uuid('itinerary_id').references(() => itineraries.id, { onDelete: 'cascade' }),
    poiId: uuid('poi_id').references(() => pointsOfInterest.id),
    dayNumber: integer('day_number').notNull(),
    orderIndex: integer('order_index').notNull(),
    recommendedDurationMinutes: integer('recommended_duration_minutes'),
    createdAt: timestamp('created_at').defaultNow(),
});

export const knowledgeChunks = pgTable('knowledge_chunks', {
    id: uuid('id').primaryKey().defaultRandom(),
    poiId: uuid('poi_id').references(() => pointsOfInterest.id, { onDelete: 'cascade' }),
    siteId: uuid('site_id').references(() => sites.id, { onDelete: 'cascade' }),
    title: text('title').notNull(),
    content: text('content').notNull(),
    historicalPeriod: text('historical_period'),
    category: text('category').default('history'),
    languageCode: text('language_code').notNull().default('en'),
    metadata: jsonb('metadata'),
    createdAt: timestamp('created_at').defaultNow(),
});

/**
 * Hotel-issued pass codes (Tour_os PMS). A guest buys an experience at the
 * front desk, receives an EG-XXXXXXXX access code, and activates it here to
 * unlock the corresponding content tier.
 */
export const userPasses = pgTable('user_passes', {
    id: uuid('id').primaryKey().defaultRandom(),
    userId: uuid('user_id').references(() => users.id, { onDelete: 'cascade' }),
    accessCode: text('access_code').notNull().unique(),
    experienceId: text('experience_id').notNull(),
    experienceName: text('experience_name').notNull(),
    description: text('description'),
    /** 'touros' = validated server-to-server against the PMS; 'dev' = local dev code. */
    source: text('source').notNull().default('touros'),
    activatedAt: timestamp('activated_at').defaultNow(),
    expiresAt: timestamp('expires_at'),
    createdAt: timestamp('created_at').defaultNow(),
});

