export type ChallengeDifficulty = 'EASY' | 'MEDIUM' | 'HARD' | 'EXPERT';

export type ChallengeCategory =
  | 'ARRAYS'
  | 'STRINGS'
  | 'LINKED_LIST'
  | 'STACK_QUEUE'
  | 'TREES'
  | 'GRAPHS'
  | 'DYNAMIC_PROGRAMMING'
  | 'BINARY_SEARCH'
  | 'HEAPS'
  | 'BIT_MANIPULATION'
  | 'TWO_POINTERS'
  | 'SLIDING_WINDOW'
  | 'BACKTRACKING'
  | 'GREEDY'
  | 'SORTING'
  | 'DATABASE'
  | 'SQL_DB'
  | 'OPERATING_SYSTEMS'
  | 'NETWORKING'
  | 'ALGORITHMS'
  | 'DEBUGGING'
  | 'APTITUDE'
  | 'PUZZLES'
  | 'INTERVIEW'
  | 'GENERAL';

export type ProblemType =
  | 'CODING'
  | 'MCQ'
  | 'APTITUDE'
  | 'SQL'
  | 'DBMS'
  | 'OS'
  | 'NETWORKING'
  | 'PUZZLE'
  | 'INTERVIEW'
  | 'GENERAL';

export type ChallengeProgressStatus = 'NOT_STARTED' | 'ATTEMPTED' | 'SOLVED';

export interface Challenge {
  id: string;
  title: string;
  slug: string;
  description?: string;
  difficulty: ChallengeDifficulty;
  category: ChallengeCategory;
  problemType: ProblemType;
  xpReward: number;
  estimatedMinutes: number;
  tags?: string;
  supportedLanguages?: string;
  sourceReference?: string;
  progressStatus: ChallengeProgressStatus;
  completedAt?: string;
  // Detail-only fields
  sampleTestCases?: TestCaseSummary[];
  starterTemplates?: Record<string, string>;
}

export interface TestCaseSummary {
  id: string;
  orderIndex: number;
  input: string;
  expectedOutput: string;
  explanation?: string;
}


export type QuestType =
  | 'COMPLETE_CHALLENGE'
  | 'COMPLETE_EASY'
  | 'PRACTICE_PROBLEMS'
  | 'MAINTAIN_STREAK'
  | 'EARN_XP';

export type PlayerQuestStatus = 'IN_PROGRESS' | 'COMPLETED' | 'CLAIMED';

export interface DailyQuest {
  id: string;
  questId: string;
  title: string;
  description: string;
  questType: QuestType;
  currentCount: number;
  targetCount: number;
  xpReward: number;
  status: PlayerQuestStatus;
  completed: boolean;
  claimed: boolean;
}

export interface PlayerProgression {
  level: number;
  currentXp: number;
  xpToNextLevel: number;
  totalXp: number;
  currentStreak: number;
  longestStreak: number;
  challengesSolved: number;
  questsCompleted: number;
  xpPercentage: number;
}

export interface PlayerStats {
  rating: number;
  wins: number;
  losses: number;
  draws: number;
  winStreak: number;
  highestRating: number;
}

export type ActivityType =
  | 'CHALLENGE_SOLVED'
  | 'QUEST_COMPLETED'
  | 'LEVEL_UP'
  | 'STREAK_MILESTONE';

export interface PlayerActivity {
  id: string;
  activityType: ActivityType;
  title: string;
  description?: string;
  xpEarned: number;
  createdAt: string;
}

export interface ProgressionMilestone {
  title: string;
  description: string;
  requiredLevel: number;
  unlocked: boolean;
}

export interface PlayerSummary {
  id: string;
  username: string;
  displayName: string;
  avatar: string;
  bio?: string;
}

export interface ArenaHomeData {
  player: PlayerSummary;
  progression: PlayerProgression;
  stats: PlayerStats;
  dailyQuests: DailyQuest[];
  recommendedChallenges: Challenge[];
  recentActivity: PlayerActivity[];
  nextMilestone: ProgressionMilestone;
}

export interface XpRewardResult {
  previousXp: number;
  newXp: number;
  previousLevel: number;
  newLevel: number;
  totalXp: number;
  xpEarned: number;
  xpToNextLevel: number;
  leveledUp: boolean;
}

export interface QuestClaimResponse {
  id: string;
  status: PlayerQuestStatus;
  xpEarned: number;
  xpResult: XpRewardResult;
}
