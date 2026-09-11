import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/auth';
import { DailyQuest, QuestClaimResponse } from '../types/arena';

export const questService = {
  async getDailyQuests(): Promise<DailyQuest[]> {
    const response = await apiClient.get<ApiResponse<DailyQuest[]>>('/api/v1/quests/daily');
    return response.data.data;
  },

  async claimQuest(id: string): Promise<QuestClaimResponse> {
    const response = await apiClient.post<ApiResponse<QuestClaimResponse>>(`/api/v1/quests/${id}/claim`);
    return response.data.data;
  },
};
