import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/auth';
import { ArenaHomeData } from '../types/arena';

export const arenaService = {
  async getHomeData(): Promise<ArenaHomeData> {
    const response = await apiClient.get<ApiResponse<ArenaHomeData>>('/arena/home');
    return response.data.data;
  },
};
