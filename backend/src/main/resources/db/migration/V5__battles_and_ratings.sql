-- ============================================================================
-- DevArena Database Migration V5: 1v1 Battles, Matchmaking, and Ratings
-- ============================================================================

CREATE TABLE IF NOT EXISTS battles (
    id UUID PRIMARY KEY,
    player1_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    player2_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    challenge_id UUID NOT NULL REFERENCES challenges(id) ON DELETE RESTRICT,
    status VARCHAR(32) NOT NULL,
    player1_status VARCHAR(32) NOT NULL,
    player2_status VARCHAR(32) NOT NULL,
    winner_id UUID REFERENCES users(id) ON DELETE SET NULL,
    started_at TIMESTAMP WITH TIME ZONE,
    ended_at TIMESTAMP WITH TIME ZONE,
    duration_seconds INT NOT NULL DEFAULT 900,
    player1_xp_delta INT NOT NULL DEFAULT 0,
    player2_xp_delta INT NOT NULL DEFAULT 0,
    player1_rating_delta INT NOT NULL DEFAULT 0,
    player2_rating_delta INT NOT NULL DEFAULT 0,
    player1_submission_id UUID REFERENCES submissions(id) ON DELETE SET NULL,
    player2_submission_id UUID REFERENCES submissions(id) ON DELETE SET NULL,
    finish_reason VARCHAR(64),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_battles_distinct_players CHECK (player1_id != player2_id)
);

CREATE INDEX IF NOT EXISTS idx_battles_players ON battles (player1_id, player2_id);
CREATE INDEX IF NOT EXISTS idx_battles_status ON battles (status);
CREATE INDEX IF NOT EXISTS idx_battles_created_at ON battles (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_battles_player1 ON battles (player1_id);
CREATE INDEX IF NOT EXISTS idx_battles_player2 ON battles (player2_id);
