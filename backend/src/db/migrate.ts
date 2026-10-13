import 'dotenv/config';
import fs from 'fs';
import path from 'path';
import { migrationClient } from './index';

async function runMigrations() {
    console.log('⏳ Running database migrations...');
    const migrationFile = path.resolve(__dirname, '../../migrations/001_initial_schema.sql');
    const sqlContent = fs.readFileSync(migrationFile, 'utf-8');

    await migrationClient.unsafe(sqlContent);
    console.log('✅ Migrations applied successfully!');
    await migrationClient.end();
}

runMigrations().catch(async (err) => {
    console.error('❌ Migration failed:', err);
    await migrationClient.end();
    process.exit(1);
});
