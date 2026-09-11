-- ==============================================================================
-- DevArena Module 08: Social Arena, Friends, Teams, and Notifications Migration
-- ==============================================================================

-- 1. Friend Requests Table
CREATE TABLE IF NOT EXISTS friend_requests (
    id UUID PRIMARY KEY,
    sender_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    receiver_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL, -- PENDING, ACCEPTED, REJECTED, CANCELLED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_friend_req_receiver ON friend_requests(receiver_id, status);
CREATE INDEX IF NOT EXISTS idx_friend_req_sender ON friend_requests(sender_id, status);

-- 2. Friendships Table (Bidirectional Friend Connections)
CREATE TABLE IF NOT EXISTS friendships (
    id UUID PRIMARY KEY,
    user1_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user2_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_friendship_pair UNIQUE (user1_id, user2_id)
);

CREATE INDEX IF NOT EXISTS idx_friendship_user1 ON friendships(user1_id);
CREATE INDEX IF NOT EXISTS idx_friendship_user2 ON friendships(user2_id);

-- 3. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    recipient_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(32) NOT NULL, -- FRIEND_REQUEST, FRIEND_ACCEPTED, BATTLE_INVITE, BATTLE_RESULT, ACHIEVEMENT_UNLOCKED, LEVEL_UP, TEAM_INVITE, TEAM_JOINED, SYSTEM
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    reference_type VARCHAR(64),
    reference_id VARCHAR(64),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_notifications_recipient ON notifications(recipient_id, is_read, created_at DESC);

-- 4. Teams Table
CREATE TABLE IF NOT EXISTS teams (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    avatar VARCHAR(255) DEFAULT 'team-avatar-1',
    owner_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    max_members INT NOT NULL DEFAULT 10,
    rating INT NOT NULL DEFAULT 1000,
    wins INT NOT NULL DEFAULT 0,
    losses INT NOT NULL DEFAULT 0,
    battles INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_teams_rating ON teams(rating DESC);
CREATE INDEX IF NOT EXISTS idx_teams_owner ON teams(owner_id);

-- 5. Team Members Table
CREATE TABLE IF NOT EXISTS team_members (
    id UUID PRIMARY KEY,
    team_id UUID NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL, -- OWNER, CAPTAIN, MEMBER
    joined_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_team_member_user UNIQUE (user_id),
    CONSTRAINT uq_team_member_team_user UNIQUE (team_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_team_members_team ON team_members(team_id);
CREATE INDEX IF NOT EXISTS idx_team_members_user ON team_members(user_id);

-- 6. Team Invites Table
CREATE TABLE IF NOT EXISTS team_invites (
    id UUID PRIMARY KEY,
    team_id UUID NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    inviter_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    invitee_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL, -- PENDING, ACCEPTED, REJECTED, CANCELLED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_team_invites_invitee ON team_invites(invitee_id, status);
CREATE INDEX IF NOT EXISTS idx_team_invites_team ON team_invites(team_id, status);

-- 7. Friend Battle Invites Table
CREATE TABLE IF NOT EXISTS friend_battle_invites (
    id UUID PRIMARY KEY,
    inviter_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    invitee_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    challenge_id UUID REFERENCES challenges(id) ON DELETE SET NULL,
    battle_id UUID REFERENCES battles(id) ON DELETE SET NULL,
    status VARCHAR(20) NOT NULL, -- PENDING, ACCEPTED, REJECTED, EXPIRED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_friend_battle_invitee ON friend_battle_invites(invitee_id, status);
