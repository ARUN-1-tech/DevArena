export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: string;
}

export interface UserSummary {
  id: string;
  username: string;
  email: string;
  displayName: string;
  avatar: string;
  roles: string[];
}

export interface PlayerStats {
  rating: number;
  wins: number;
  losses: number;
  draws: number;
  winStreak: number;
  highestRating: number;
}

export interface PlayerProgression {
  level: number;
  currentXp: number;
  xpToNextLevel: number;
}

export interface PlayerProfile extends UserSummary {
  bio?: string;
  stats: PlayerStats;
  progression: PlayerProgression;
}

export interface AuthResponseData {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: UserSummary;
}

export interface RegisterPayload {
  email: string;
  password: string;
  username: string;
  displayName: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface UpdateProfilePayload {
  displayName?: string;
  avatar?: string;
  bio?: string;
}
