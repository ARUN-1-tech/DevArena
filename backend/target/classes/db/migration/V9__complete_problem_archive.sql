-- ==============================================================================
-- DevArena Database Migration: V9 Complete Problem Archive & Types Extension
-- ==============================================================================

-- 1. Extend challenges table with problem types, options, solution, and source
ALTER TABLE challenges ADD COLUMN IF NOT EXISTS problem_type VARCHAR(32) DEFAULT 'CODING' NOT NULL;
ALTER TABLE challenges ADD COLUMN IF NOT EXISTS options TEXT;
ALTER TABLE challenges ADD COLUMN IF NOT EXISTS correct_answer TEXT;
ALTER TABLE challenges ADD COLUMN IF NOT EXISTS hints TEXT;
ALTER TABLE challenges ADD COLUMN IF NOT EXISTS solution_approach TEXT;
ALTER TABLE challenges ADD COLUMN IF NOT EXISTS source VARCHAR(255);
ALTER TABLE challenges ADD COLUMN IF NOT EXISTS time_limit_seconds INT DEFAULT 900 NOT NULL;

-- 2. Create search indexes for fast lookup and filtering across large datasets
CREATE INDEX IF NOT EXISTS idx_challenges_problem_type ON challenges(problem_type);
CREATE INDEX IF NOT EXISTS idx_challenges_title ON challenges(title);
CREATE INDEX IF NOT EXISTS idx_challenges_time_limit ON challenges(time_limit_seconds);
