import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/api';
import { Achievement, PlayerAchievement } from '../types/progression';

export const achievementService = {
  async getPublicAchievements(): Promise<Achievement[]> {
    const res = await apiClient.get<ApiResponse<Achievement[]>>('/achievements');
    return res.data.data;
  },

  async getMyAchievements(): Promise<PlayerAchievement[]> {
    const res = await apiClient.get<ApiResponse<PlayerAchievement[]>>('/achievements/me');
    return res.data.data;
  },
};
