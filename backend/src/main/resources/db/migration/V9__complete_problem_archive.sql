-- ==============================================================================
-- DevArena Database Migration: V9 Complete Problem Archive
-- ==============================================================================

-- 1. Add problem_type column to challenges
ALTER TABLE challenges ADD COLUMN IF NOT EXISTS problem_type VARCHAR(32) DEFAULT 'CODING' NOT NULL;

-- 2. Add supported_languages column (comma-separated)
ALTER TABLE challenges ADD COLUMN IF NOT EXISTS supported_languages VARCHAR(512) DEFAULT 'JAVA,PYTHON,JAVASCRIPT' NOT NULL;

-- 3. Add source_reference column (e.g. LeetCode #1, Rising Brain DSA Sheet)
ALTER TABLE challenges ADD COLUMN IF NOT EXISTS source_reference VARCHAR(255);

-- 4. Add indexes for efficient filtering
CREATE INDEX IF NOT EXISTS idx_challenges_problem_type ON challenges(problem_type);
CREATE INDEX IF NOT EXISTS idx_challenges_title ON challenges(title);
CREATE INDEX IF NOT EXISTS idx_challenges_difficulty_category ON challenges(difficulty, category);
CREATE INDEX IF NOT EXISTS idx_challenges_type_difficulty ON challenges(problem_type, difficulty);

-- 5. Update system metadata
UPDATE system_metadata SET value = 'MODULE_11' WHERE key = 'active_module';
