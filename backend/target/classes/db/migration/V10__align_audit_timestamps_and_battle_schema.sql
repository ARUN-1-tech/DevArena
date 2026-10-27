-- ==============================================================================
-- DevArena Database Migration V10: Align Audit Timestamps for Battles, Submissions, and Player Activities
-- ==============================================================================

-- 1. Add missing updated_at audit column to battles table
ALTER TABLE battles ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL;

-- 2. Add missing updated_at audit column to submissions table
ALTER TABLE submissions ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL;

-- 3. Add missing updated_at audit column to player_activities table
ALTER TABLE player_activities ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL;
