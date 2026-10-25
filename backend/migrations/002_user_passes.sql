-- Hotel pass codes (Tour_os PMS integration)
CREATE TABLE IF NOT EXISTS user_passes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    access_code TEXT NOT NULL UNIQUE,
    experience_id TEXT NOT NULL,
    experience_name TEXT NOT NULL,
    description TEXT,
    source TEXT NOT NULL DEFAULT 'touros',
    activated_at TIMESTAMP DEFAULT NOW(),
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS user_passes_user_id_idx ON user_passes(user_id);
