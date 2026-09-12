import { ChallengeDetailWithCode } from './submission';
import { ChallengeDifficulty } from './arena';

export type BattleStatus = 'WAITING' | 'READY' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';

export type PlayerBattleStatus =
  | 'CONNECTED'
  | 'DISCONNECTED'
  | 'SUBMITTED'
  | 'PASSED'
  | 'FAILED'
  | 'FORFEITED';

export interface BattlePlayer {
  userId: string;
  username: string;
  avatarUrl: string;
  level: number;
  rating: number;
  status: PlayerBattleStatus;
  ready: boolean;
}

export interface BattleDetail {
  id: string;
  status: BattleStatus;
  player1: BattlePlayer;
  player2: BattlePlayer;
  challenge: ChallengeDetailWithCode;
  durationSeconds: number;
  startedAt?: string | null;
  endedAt?: string | null;
  winnerId?: string | null;
  finishReason?: string | null;
  isPlayer1: boolean;
}

export interface BattleResult {
  battleId: string;
  status: BattleStatus;
  outcome: 'WIN' | 'LOSS' | 'DRAW';
  winnerId?: string | null;
  finishReason?: string | null;
  durationSeconds: number;
  xpEarned: number;
  ratingDelta: number;
  newRating: number;
  opponentUsername: string;
  opponentAvatarUrl: string;
  opponentRating: number;
  opponentRatingDelta: number;
  challengeTitle: string;
  endedAt?: string | null;
}

export interface BattleHistoryItem {
  battleId: string;
  challengeTitle: string;
  difficulty: ChallengeDifficulty;
  opponentUsername: string;
  opponentAvatarUrl: string;
  outcome: 'WIN' | 'LOSS' | 'DRAW';
  ratingDelta: number;
  xpEarned: number;
  completedAt: string;
}

export interface MatchmakingStatus {
  inQueue: boolean;
  waitTimeSeconds: number;
  searchRadius: number;
  playerRating: number;
  matchedBattleId?: string | null;
}

export interface BattleEvent<T = any> {
  type:
    | 'MATCH_FOUND'
    | 'BATTLE_READY'
    | 'BATTLE_STARTED'
    | 'PLAYER_STATUS'
    | 'PLAYER_SUBMITTED'
    | 'BATTLE_FINISHED'
    | 'OPPONENT_DISCONNECTED'
    | 'BATTLE_CANCELLED';
  battleId: string;
  payload: T;
  timestamp: string;
}

export interface MatchFoundPayload {
  battleId: string;
  challengeTitle: string;
  difficulty: string;
  player1: {
    username: string;
    rating: number;
    avatarUrl?: string;
  };
  player2: {
    username: string;
    rating: number;
    avatarUrl?: string;
  };
}
