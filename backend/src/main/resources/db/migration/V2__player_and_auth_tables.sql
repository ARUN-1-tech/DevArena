-- ==============================================================================
-- DevArena Database Migration: V2 Player and Auth Tables
-- Creates profiles, player_stats, player_progression, and refresh_tokens
-- ==============================================================================

-- 1. Profiles Table (Linked 1:1 to users)
CREATE TABLE IF NOT EXISTS profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    avatar VARCHAR(100) DEFAULT 'avatar-1' NOT NULL,
    bio VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_profiles_username ON profiles(username);
CREATE INDEX IF NOT EXISTS idx_profiles_user_id ON profiles(user_id);

-- 2. Player Stats Table (Rating, ELO, Win/Loss Record)
CREATE TABLE IF NOT EXISTS player_stats (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    rating INT DEFAULT 1000 NOT NULL,
    wins INT DEFAULT 0 NOT NULL,
    losses INT DEFAULT 0 NOT NULL,
    draws INT DEFAULT 0 NOT NULL,
    win_streak INT DEFAULT 0 NOT NULL,
    highest_rating INT DEFAULT 1000 NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_player_stats_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_player_stats_rating ON player_stats(rating DESC);
CREATE INDEX IF NOT EXISTS idx_player_stats_user_id ON player_stats(user_id);

-- 3. Player Progression Table (Level, XP)
CREATE TABLE IF NOT EXISTS player_progression (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    level INT DEFAULT 1 NOT NULL,
    current_xp INT DEFAULT 0 NOT NULL,
    xp_to_next_level INT DEFAULT 100 NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_player_progression_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_player_progression_user_id ON player_progression(user_id);

-- 4. Refresh Tokens Table (Rotation and Revocation)
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token VARCHAR(1000) NOT NULL UNIQUE,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- Update active module metadata
UPDATE system_metadata SET value = 'MODULE_03' WHERE key = 'active_module';
