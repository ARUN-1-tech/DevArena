import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/api';
import { LeaderboardResponse } from '../types/progression';

export const leaderboardService = {
  async getLeaderboard(
    type: 'global' | 'weekly' | 'monthly' = 'global',
    page = 0,
    size = 20
  ): Promise<LeaderboardResponse> {
    const res = await apiClient.get<ApiResponse<LeaderboardResponse>>(
      `/leaderboard?type=${type}&page=${page}&size=${size}`
    );
    return res.data.data;
  },
};
