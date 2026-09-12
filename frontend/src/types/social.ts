// ==============================================================================
// DevArena Module 08 Social, Friends, Teams, and Notification Types
// ==============================================================================

export interface PlayerSearchResult {
  id: string;
  username: string;
  displayName: string;
  avatar: string;
  level: number;
  rating: number;
  rankBadge: string;
  online: boolean;
  isFriend: boolean;
  hasPendingRequest: boolean;
  skillHighlights: string[];
}

export interface PublicSkill {
  code: string;
  name: string;
  category: string;
  icon: string;
  level: number;
  masteryPercentage: number;
}

export interface PublicAchievement {
  code: string;
  name: string;
  description: string;
  icon: string;
  rarity: string;
  unlockedAt: string;
}

export interface PublicActivity {
  type: string;
  description: string;
  timestamp: string;
}

export interface PublicPlayerProfile {
  id: string;
  username: string;
  displayName: string;
  avatar: string;
  bio?: string | null;
  level: number;
  rating: number;
  rank: number;
  totalXp: number;
  solvedChallenges: number;
  battleWins: number;
  battleLosses: number;
  battleWinRate: number;
  winStreak: number;
  topSkills: PublicSkill[];
  achievements: PublicAchievement[];
  recentActivities: PublicActivity[];
  isFriend: boolean;
  hasPendingRequest: boolean;
  online: boolean;
}

export interface Friend {
  id: string;
  friendId: string;
  username: string;
  displayName: string;
  avatar: string;
  level: number;
  rating: number;
  online: boolean;
  friendsSince: string;
}

export type FriendRequestStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'CANCELLED';

export interface FriendRequest {
  id: string;
  senderId: string;
  senderUsername: string;
  senderDisplayName: string;
  senderAvatar: string;
  senderLevel: number;
  senderRating: number;
  receiverId: string;
  receiverUsername: string;
  status: FriendRequestStatus;
  createdAt: string;
}

export interface FriendRequestsResponse {
  incoming: FriendRequest[];
  outgoing: FriendRequest[];
}

export interface FriendBattleInvite {
  id: string;
  inviterId: string;
  inviterUsername: string;
  inviterDisplayName: string;
  inviterAvatar: string;
  inviteeId: string;
  inviteeUsername: string;
  challengeId?: string | null;
  challengeTitle?: string | null;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'EXPIRED';
  battleId?: string | null;
  createdAt: string;
  expiresAt: string;
}

export interface CustomDuelRoom {
  roomCode: string;
  hostId: string;
  hostUsername: string;
  hostDisplayName: string;
  hostAvatar: string;
  hostRating: number;
  challengeId: string;
  challengeTitle: string;
  difficulty: string;
  durationSeconds: number;
  status: 'WAITING' | 'STARTED' | 'CANCELLED' | 'EXPIRED';
  battleId?: string | null;
  createdAt: string;
}

export type NotificationType =
  | 'FRIEND_REQUEST'
  | 'FRIEND_ACCEPTED'
  | 'BATTLE_INVITE'
  | 'BATTLE_RESULT'
  | 'ACHIEVEMENT_UNLOCKED'
  | 'LEVEL_UP'
  | 'TEAM_INVITE'
  | 'TEAM_JOINED'
  | 'SYSTEM';

export interface NotificationItem {
  id: string;
  type: NotificationType;
  title: string;
  message: string;
  referenceType?: string | null;
  referenceId?: string | null;
  read: boolean;
  createdAt: string;
}

export type TeamRole = 'OWNER' | 'CAPTAIN' | 'MEMBER';

export interface TeamMember {
  id: string;
  userId: string;
  username: string;
  displayName: string;
  avatar: string;
  role: TeamRole;
  level: number;
  rating: number;
  online: boolean;
  joinedAt: string;
}

export interface Team {
  id: string;
  name: string;
  slug: string;
  description?: string | null;
  avatar?: string | null;
  ownerId: string;
  ownerUsername: string;
  maxMembers: number;
  memberCount: number;
  rating: number;
  wins: number;
  losses: number;
  battles: number;
  winRate: number;
  members: TeamMember[];
  createdAt: string;
}

export interface CreateTeamPayload {
  name: string;
  description?: string;
  avatar?: string;
  maxMembers?: number;
}

export interface TeamInvite {
  id: string;
  teamId: string;
  teamName: string;
  teamAvatar: string;
  inviterId: string;
  inviterUsername: string;
  inviteeId: string;
  inviteeUsername: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'CANCELLED';
  createdAt: string;
}

export interface TeamLeaderboardItem {
  rank: number;
  id: string;
  name: string;
  slug: string;
  avatar: string;
  ownerUsername: string;
  memberCount: number;
  maxMembers: number;
  rating: number;
  wins: number;
  losses: number;
  battles: number;
  winRate: number;
}
