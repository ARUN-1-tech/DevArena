/**
 * Competitive ranks in DevArena.
 */
export type CompetitiveRank =
  | 'NOVICE'
  | 'BRONZE'
  | 'SILVER'
  | 'GOLD'
  | 'PLATINUM'
  | 'DIAMOND'
  | 'MASTER'
  | 'GRANDMASTER';

/**
 * Challenge difficulty classification.
 */
export type ChallengeDifficulty = 'EASY' | 'MEDIUM' | 'HARD' | 'EXPERT';

/**
 * Baseline player summary for UI widgets.
 */
export interface PlayerSummary {
  id: string;
  username: string;
  avatarUrl?: string;
  rating: number;
  rank: CompetitiveRank;
  level: number;
  xp: number;
}
