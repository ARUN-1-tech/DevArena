-- ==============================================================================
-- DevArena Database Migration: V4 Code Execution, Submissions & Test Cases
-- ==============================================================================

-- 1. Challenge Starter Codes
CREATE TABLE IF NOT EXISTS challenge_starter_codes (
    id UUID PRIMARY KEY,
    challenge_id UUID NOT NULL,
    language VARCHAR(32) NOT NULL,
    starter_code TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_starter_codes_challenge FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE,
    CONSTRAINT uq_challenge_starter_code UNIQUE (challenge_id, language)
);

CREATE INDEX IF NOT EXISTS idx_starter_codes_challenge ON challenge_starter_codes(challenge_id);
CREATE INDEX IF NOT EXISTS idx_starter_codes_lang ON challenge_starter_codes(challenge_id, language);

-- 2. Challenge Test Cases
CREATE TABLE IF NOT EXISTS challenge_test_cases (
    id UUID PRIMARY KEY,
    challenge_id UUID NOT NULL,
    input TEXT NOT NULL,
    expected_output TEXT NOT NULL,
    hidden BOOLEAN DEFAULT FALSE NOT NULL,
    order_index INT DEFAULT 0 NOT NULL,
    explanation TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_test_cases_challenge FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_test_cases_challenge ON challenge_test_cases(challenge_id);
CREATE INDEX IF NOT EXISTS idx_test_cases_challenge_hidden ON challenge_test_cases(challenge_id, hidden);

-- 3. Submissions Table
CREATE TABLE IF NOT EXISTS submissions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    challenge_id UUID NOT NULL,
    language VARCHAR(32) NOT NULL,
    source_code TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    passed_tests INT DEFAULT 0 NOT NULL,
    total_tests INT DEFAULT 0 NOT NULL,
    execution_time_ms BIGINT DEFAULT 0 NOT NULL,
    memory_used_bytes BIGINT DEFAULT 0 NOT NULL,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_submissions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_submissions_challenge FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_submissions_user ON submissions(user_id);
CREATE INDEX IF NOT EXISTS idx_submissions_challenge ON submissions(challenge_id);
CREATE INDEX IF NOT EXISTS idx_submissions_user_challenge ON submissions(user_id, challenge_id);
CREATE INDEX IF NOT EXISTS idx_submissions_created_at ON submissions(created_at);

-- 4. Extend player_challenges with attempts and best_result tracking
ALTER TABLE player_challenges ADD COLUMN IF NOT EXISTS attempts INT DEFAULT 0 NOT NULL;
ALTER TABLE player_challenges ADD COLUMN IF NOT EXISTS best_result VARCHAR(32);
ALTER TABLE player_challenges ADD COLUMN IF NOT EXISTS last_submission_at TIMESTAMP WITH TIME ZONE;
