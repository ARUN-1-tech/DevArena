import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/api';
import { PlayerSearchResult, PublicPlayerProfile } from '../types/social';

export const playerService = {
  async searchPlayers(query: string): Promise<PlayerSearchResult[]> {
    if (!query.trim()) return [];
    const res = await apiClient.get<ApiResponse<PlayerSearchResult[]>>(
      `/players/search?q=${encodeURIComponent(query.trim())}`
    );
    return res.data.data;
  },

  async getPublicProfile(username: string): Promise<PublicPlayerProfile> {
    const res = await apiClient.get<ApiResponse<PublicPlayerProfile>>(
      `/players/${encodeURIComponent(username)}`
    );
    return res.data.data;
  },
};
