-- ==============================================================================
-- DevArena Module 09: AI Coach, Moderation, Anti-Cheat, and Security Audit Migration
-- ==============================================================================

-- 1. AI Usage Records Table
CREATE TABLE IF NOT EXISTS ai_usage_records (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    challenge_id UUID REFERENCES challenges(id) ON DELETE SET NULL,
    request_type VARCHAR(32) NOT NULL, -- HINT, EXPLAIN, DEBUG, COMPLEXITY, SOLUTION_EXPLANATION
    tokens_used INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ai_usage_user_date ON ai_usage_records(user_id, created_at DESC);

-- 2. Platform Reports / Moderation Table
CREATE TABLE IF NOT EXISTS reports (
    id UUID PRIMARY KEY,
    reporter_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_type VARCHAR(32) NOT NULL, -- PLAYER, BATTLE, CHALLENGE, BEHAVIOR
    target_id VARCHAR(64) NOT NULL,
    reason VARCHAR(64) NOT NULL, -- CHEATING, HARASSMENT, OFFENSIVE_NAME, EXPLOIT, OTHER
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN', -- OPEN, REVIEWING, RESOLVED, DISMISSED
    resolution_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP WITH TIME ZONE,
    resolved_by_id UUID REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_reports_status ON reports(status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_reports_reporter ON reports(reporter_id);
CREATE INDEX IF NOT EXISTS idx_reports_target ON reports(target_type, target_id);

-- 3. Anti-Cheat / Integrity Events Table
CREATE TABLE IF NOT EXISTS integrity_events (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    event_type VARCHAR(64) NOT NULL, -- IMPOSSIBLE_TIMING, CODE_SIMILARITY, REPEATED_FAILURES, RAPID_EXECUTION, BATTLE_ANOMALY, UNUSUAL_ACCOUNT_ACTIVITY
    severity VARCHAR(20) NOT NULL, -- LOW, MEDIUM, HIGH, CRITICAL
    risk_score INT NOT NULL DEFAULT 10,
    metadata TEXT,
    reviewed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_integrity_user_severity ON integrity_events(user_id, severity, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_integrity_reviewed ON integrity_events(reviewed, created_at DESC);

-- 4. Admin Audit Logs Table (Immutable Audit Trail)
CREATE TABLE IF NOT EXISTS admin_audit_logs (
    id UUID PRIMARY KEY,
    actor_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    actor_username VARCHAR(100) NOT NULL,
    action VARCHAR(64) NOT NULL, -- PLAYER_SUSPENDED, PLAYER_RESTORED, CHALLENGE_CREATED, CHALLENGE_UPDATED, CHALLENGE_PUBLISHED, CHALLENGE_ARCHIVED, REPORT_RESOLVED, REPORT_DISMISSED
    target_type VARCHAR(32) NOT NULL,
    target_id VARCHAR(64) NOT NULL,
    metadata TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_admin_audit_created ON admin_audit_logs(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_admin_audit_actor ON admin_audit_logs(actor_id);
