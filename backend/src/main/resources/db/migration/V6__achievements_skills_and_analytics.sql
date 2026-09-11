-- ==============================================================================
-- DevArena Database Migration: V6 Achievements, Skills, and Analytics
-- ==============================================================================

-- 1. Achievements Catalog Table
CREATE TABLE IF NOT EXISTS achievements (
    id UUID PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(255) NOT NULL,
    icon VARCHAR(64) NOT NULL,
    category VARCHAR(32) NOT NULL,
    requirement_type VARCHAR(64) NOT NULL,
    requirement_value INT NOT NULL DEFAULT 1,
    xp_reward INT NOT NULL DEFAULT 100,
    rarity VARCHAR(32) NOT NULL DEFAULT 'COMMON',
    is_secret BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_achievements_category ON achievements(category);
CREATE INDEX IF NOT EXISTS idx_achievements_rarity ON achievements(rarity);
CREATE INDEX IF NOT EXISTS idx_achievements_code ON achievements(code);

-- 2. Player Achievements Junction Table
CREATE TABLE IF NOT EXISTS player_achievements (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    achievement_id UUID NOT NULL REFERENCES achievements(id) ON DELETE CASCADE,
    unlocked_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    xp_awarded INT NOT NULL DEFAULT 0,
    CONSTRAINT uq_player_achievement UNIQUE (user_id, achievement_id)
);

CREATE INDEX IF NOT EXISTS idx_player_achievements_user ON player_achievements(user_id);
CREATE INDEX IF NOT EXISTS idx_player_achievements_unlocked_at ON player_achievements(unlocked_at DESC);

-- 3. Skills Catalog Table
CREATE TABLE IF NOT EXISTS skills (
    id UUID PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    category VARCHAR(64) NOT NULL,
    description VARCHAR(255) NOT NULL,
    icon VARCHAR(64) NOT NULL,
    max_level INT NOT NULL DEFAULT 5,
    prerequisite_id UUID REFERENCES skills(id) ON DELETE SET NULL,
    order_index INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_skills_category ON skills(category);
CREATE INDEX IF NOT EXISTS idx_skills_order ON skills(order_index);
CREATE INDEX IF NOT EXISTS idx_skills_code ON skills(code);

-- 4. Player Skills Junction Table
CREATE TABLE IF NOT EXISTS player_skills (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skill_id UUID NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    current_level INT NOT NULL DEFAULT 1,
    current_xp INT NOT NULL DEFAULT 0,
    mastery_percentage INT NOT NULL DEFAULT 20,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_player_skill UNIQUE (user_id, skill_id)
);

CREATE INDEX IF NOT EXISTS idx_player_skills_user ON player_skills(user_id);
CREATE INDEX IF NOT EXISTS idx_player_skills_level ON player_skills(current_level DESC);

-- 5. Additional Performance Indexes for Leaderboard & Analytics
CREATE INDEX IF NOT EXISTS idx_player_stats_rating_wins ON player_stats(rating DESC, wins DESC);
CREATE INDEX IF NOT EXISTS idx_player_progression_solved_xp ON player_progression(challenges_solved DESC, total_xp DESC);
CREATE INDEX IF NOT EXISTS idx_player_activities_user_created ON player_activities(user_id, created_at DESC);
