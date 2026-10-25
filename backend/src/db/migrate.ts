import 'dotenv/config';
import fs from 'fs';
import path from 'path';
import { migrationClient } from './index';

/**
 * Runs every migrations/*.sql file in order, exactly once, tracking applied
 * files in a schema_migrations table. All migration files must be idempotent
 * (IF NOT EXISTS) so re-running against a pre-tracking database is safe.
 */
async function runMigrations() {
    console.log('⏳ Running database migrations...');

    const migrationsDir = path.resolve(__dirname, '../../migrations');
    const files = fs
        .readdirSync(migrationsDir)
        .filter((f) => f.endsWith('.sql'))
        .sort();

    await migrationClient.unsafe(`
        CREATE TABLE IF NOT EXISTS schema_migrations (
            filename TEXT PRIMARY KEY,
            applied_at TIMESTAMP DEFAULT NOW()
        )
    `);

    const appliedRows = await migrationClient.unsafe<{ filename: string }[]>(
        'SELECT filename FROM schema_migrations',
    );
    const applied = new Set(appliedRows.map((row) => row.filename));

    for (const file of files) {
        if (applied.has(file)) {
            console.log(`⏭️  ${file} (already applied)`);
            continue;
        }
        const sqlContent = fs.readFileSync(path.join(migrationsDir, file), 'utf-8');
        await migrationClient.unsafe(sqlContent);
        await migrationClient.unsafe(
            'INSERT INTO schema_migrations (filename) VALUES ($1)',
            [file],
        );
        console.log(`✅ ${file}`);
    }

    console.log('✅ Migrations applied successfully!');
    await migrationClient.end();
}

runMigrations().catch(async (err) => {
    console.error('❌ Migration failed:', err);
    await migrationClient.end();
    process.exit(1);
});
