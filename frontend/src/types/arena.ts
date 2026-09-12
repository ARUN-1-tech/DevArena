export type ChallengeDifficulty = 'EASY' | 'MEDIUM' | 'HARD' | 'EXPERT';

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

export type ChallengeCategory =
  | 'ARRAYS'
  | 'STRINGS'
  | 'LINKED_LIST'
  | 'STACK_QUEUE'
  | 'TREES'
  | 'GRAPHS'
  | 'DYNAMIC_PROGRAMMING'
  | 'BINARY_SEARCH'
  | 'BACKTRACKING'
  | 'HEAPS_PRIORITY_QUEUES'
  | 'BIT_MANIPULATION'
  | 'GREEDY'
  | 'DATABASE'
  | 'SQL'
  | 'DBMS'
  | 'OPERATING_SYSTEMS'
  | 'NETWORKING'
  | 'MATH_APTITUDE'
  | 'PUZZLES'
  | 'SYSTEM_DESIGN'
  | 'ALGORITHMS'
  | 'DEBUGGING'
  | 'INTERVIEW_PREP'
  | 'GENERAL_CS';

export type ChallengeProgressStatus = 'NOT_STARTED' | 'ATTEMPTED' | 'SOLVED';

export interface Challenge {
  id: string;
  title: string;
  slug: string;
  description?: string;
  difficulty: ChallengeDifficulty;
  category: ChallengeCategory;
  problemType?: ProblemType;
  xpReward: number;
  estimatedMinutes: number;
  timeLimitSeconds?: number;
  tags?: string;
  options?: string;
  correctAnswer?: string;
  hints?: string;
  solutionApproach?: string;
  source?: string;
  progressStatus: ChallengeProgressStatus;
  completedAt?: string;
  sampleTestCases?: Array<{
    id?: string;
    orderIndex?: number;
    input: string;
    expectedOutput: string;
    explanation?: string;
  }>;
  starterTemplates?: Record<string, string>;
}

export interface ChallengeStats {
  totalChallenges: number;
  totalSolved: number;
  byDifficulty: Record<string, number>;
  byCategory: Record<string, number>;
  byProblemType: Record<string, number>;
}

export interface SubmitAnswerResponse {
  correct: boolean;
  submittedAnswer: string;
  correctAnswer: string;
  explanation: string;
  message: string;
  rewardResult?: XpRewardResult;
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
  level: number;
  rating: number;
  rank: number;
  totalXp: number;
  currentStreak: number;
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

export interface QuestClaimResponse {
  questId: string;
  xpEarned: number;
  xpResult: XpRewardResult;
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

