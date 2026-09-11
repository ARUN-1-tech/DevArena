import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/api';
import { PlayerAnalytics } from '../types/progression';

export const analyticsService = {
  async getMyAnalytics(): Promise<PlayerAnalytics> {
    const res = await apiClient.get<ApiResponse<PlayerAnalytics>>('/analytics/me');
    return res.data.data;
  },
};
