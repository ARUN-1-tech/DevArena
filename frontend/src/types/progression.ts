// ==============================================================================
// DevArena Module 07 Progression, Achievements, Skills & Analytics Types
// ==============================================================================

export interface LeaderboardItem {
  rank: number;
  userId: string;
  username: string;
  displayName: string;
  avatar: string;
  level: number;
  rating: number;
  totalXp: number;
  wins: number;
  losses: number;
  solvedChallenges: number;
  winRate: number;
}

export interface LeaderboardResponse {
  type: 'GLOBAL' | 'WEEKLY' | 'MONTHLY';
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  top3: LeaderboardItem[];
  rankings: LeaderboardItem[];
  myRank?: LeaderboardItem | null;
}

export type AchievementRarity = 'COMMON' | 'RARE' | 'EPIC' | 'LEGENDARY';
export type AchievementCategory = 'CHALLENGE' | 'BATTLE' | 'STREAK' | 'MASTERY' | 'SPECIAL';

export interface Achievement {
  id: string;
  code: string;
  name: string;
  description: string;
  icon: string;
  category: AchievementCategory;
  requirementType: string;
  requirementValue: number;
  xpReward: number;
  rarity: AchievementRarity;
  isSecret: boolean;
}

export interface PlayerAchievement {
  id: string;
  code: string;
  name: string;
  description: string;
  icon: string;
  category: AchievementCategory;
  rarity: AchievementRarity;
  xpReward: number;
  unlocked: boolean;
  unlockedAt?: string | null;
  currentProgress: number;
  targetProgress: number;
  progressPercentage: number;
}

export type SkillCategory =
  | 'DATA_STRUCTURES'
  | 'ALGORITHMS'
  | 'DATABASE'
  | 'WEB_DEVELOPMENT'
  | 'DEBUGGING';

export interface Skill {
  id: string;
  code: string;
  name: string;
  category: SkillCategory;
  description: string;
  icon: string;
  maxLevel: number;
  prerequisiteId?: string | null;
  prerequisiteName?: string | null;
  orderIndex: number;
}

export interface PlayerSkill {
  id?: string | null;
  skillId: string;
  code: string;
  name: string;
  category: SkillCategory;
  description: string;
  icon: string;
  currentLevel: number;
  maxLevel: number;
  currentXp: number;
  xpToNextLevel: number;
  masteryPercentage: number;
  unlocked: boolean;
  prerequisiteId?: string | null;
  prerequisiteName?: string | null;
}

export interface DailyXpTrend {
  date: string;
  xpEarned: number;
}

export interface RecentBattleSummary {
  opponentUsername: string;
  outcome: 'WIN' | 'LOSS' | 'DRAW';
  ratingDelta: number;
  xpEarned: number;
  challengeTitle: string;
  endedAt: string;
}

export interface SkillProgressSummary {
  name: string;
  category: string;
  icon: string;
  level: number;
  masteryPercentage: number;
}

export interface PlayerAnalytics {
  currentLevel: number;
  totalXp: number;
  currentMmr: number;
  mmrChange30Days: number;
  currentStreak: number;
  longestStreak: number;
  totalChallengesAttempted: number;
  totalChallengesSolved: number;
  challengeSolveRate: number;
  totalSubmissions: number;
  successfulSubmissions: number;
  submissionSuccessRate: number;
  difficultyDistribution: Record<string, number>;
  categoryDistribution: Record<string, number>;
  battleWins: number;
  battleLosses: number;
  battleDraws: number;
  battleWinRate: number;
  strongestSkill: string;
  weakestSkill: string;
  topSkills: SkillProgressSummary[];
  dailyXpTrend: DailyXpTrend[];
  recentBattles: RecentBattleSummary[];
}
