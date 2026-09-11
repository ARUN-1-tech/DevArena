-- ==============================================================================
-- DevArena Database Migration: V3 Arena Progression, Challenges & Daily Quests
-- ==============================================================================

-- 1. Extend player_progression with streak and metric tracking
ALTER TABLE player_progression ADD COLUMN IF NOT EXISTS total_xp INT DEFAULT 0 NOT NULL;
ALTER TABLE player_progression ADD COLUMN IF NOT EXISTS current_streak INT DEFAULT 0 NOT NULL;
ALTER TABLE player_progression ADD COLUMN IF NOT EXISTS longest_streak INT DEFAULT 0 NOT NULL;
ALTER TABLE player_progression ADD COLUMN IF NOT EXISTS last_activity_date DATE;
ALTER TABLE player_progression ADD COLUMN IF NOT EXISTS challenges_solved INT DEFAULT 0 NOT NULL;
ALTER TABLE player_progression ADD COLUMN IF NOT EXISTS quests_completed INT DEFAULT 0 NOT NULL;

-- 2. Challenges Table
CREATE TABLE IF NOT EXISTS challenges (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    difficulty VARCHAR(32) NOT NULL,
    category VARCHAR(64) NOT NULL,
    xp_reward INT NOT NULL,
    estimated_minutes INT NOT NULL,
    tags VARCHAR(255),
    status VARCHAR(32) DEFAULT 'PUBLISHED' NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_challenges_difficulty ON challenges(difficulty);
CREATE INDEX IF NOT EXISTS idx_challenges_category ON challenges(category);
CREATE INDEX IF NOT EXISTS idx_challenges_status ON challenges(status);
CREATE INDEX IF NOT EXISTS idx_challenges_slug ON challenges(slug);

-- 3. Player Challenge Progress Table
CREATE TABLE IF NOT EXISTS player_challenges (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    challenge_id UUID NOT NULL,
    status VARCHAR(32) DEFAULT 'NOT_STARTED' NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_player_challenges_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_player_challenges_challenge FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE,
    CONSTRAINT uq_player_challenges UNIQUE (user_id, challenge_id)
);

CREATE INDEX IF NOT EXISTS idx_player_challenges_user ON player_challenges(user_id);
CREATE INDEX IF NOT EXISTS idx_player_challenges_challenge ON player_challenges(challenge_id);
CREATE INDEX IF NOT EXISTS idx_player_challenges_status ON player_challenges(status);

-- 4. Daily Quests Table
CREATE TABLE IF NOT EXISTS daily_quests (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    quest_type VARCHAR(64) NOT NULL,
    target_count INT DEFAULT 1 NOT NULL,
    xp_reward INT NOT NULL,
    active_date DATE NOT NULL,
    status VARCHAR(32) DEFAULT 'ACTIVE' NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_daily_quests_date ON daily_quests(active_date);
CREATE INDEX IF NOT EXISTS idx_daily_quests_status ON daily_quests(status);

-- 5. Player Daily Quests Table
CREATE TABLE IF NOT EXISTS player_daily_quests (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    quest_id UUID NOT NULL,
    current_count INT DEFAULT 0 NOT NULL,
    target_count INT NOT NULL,
    status VARCHAR(32) DEFAULT 'IN_PROGRESS' NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE,
    claimed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_player_quests_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_player_quests_quest FOREIGN KEY (quest_id) REFERENCES daily_quests(id) ON DELETE CASCADE,
    CONSTRAINT uq_player_daily_quests UNIQUE (user_id, quest_id)
);

CREATE INDEX IF NOT EXISTS idx_player_quests_user ON player_daily_quests(user_id);
CREATE INDEX IF NOT EXISTS idx_player_quests_quest ON player_daily_quests(quest_id);
CREATE INDEX IF NOT EXISTS idx_player_quests_status ON player_daily_quests(status);

-- 6. Player Activities Table
CREATE TABLE IF NOT EXISTS player_activities (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    activity_type VARCHAR(64) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    xp_earned INT DEFAULT 0 NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_player_activities_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_player_activities_user ON player_activities(user_id, created_at DESC);

-- Update active module metadata
UPDATE system_metadata SET value = 'MODULE_04' WHERE key = 'active_module';
