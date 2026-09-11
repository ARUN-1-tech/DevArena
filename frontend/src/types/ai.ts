export type AiCoachRequestType = 'HINT' | 'EXPLAIN' | 'DEBUG' | 'COMPLEXITY' | 'SOLUTION_EXPLANATION';

export interface AiCoachRequest {
  challengeId?: string;
  requestType: AiCoachRequestType;
  currentCode?: string;
  userMessage?: string;
}

export interface AiCoachResponse {
  reply: string;
  hintLevel: number;
  suggestedFollowUps: string[];
  remainingDailyQueries: number;
}

export interface RecommendedChallenge {
  id: string;
  title: string;
  slug: string;
  difficulty: string;
  category: string;
  xpReward: number;
}

export interface PersonalizedRecommendation {
  weakSkill: string;
  reason: string;
  recommendedChallenges: RecommendedChallenge[];
}
