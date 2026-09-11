import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/auth';
import { DailyQuest, QuestClaimResponse } from '../types/arena';

export const questService = {
  async getDailyQuests(): Promise<DailyQuest[]> {
    const response = await apiClient.get<ApiResponse<DailyQuest[]>>('/quests/daily');
    return response.data.data;
  },

  async claimQuest(id: string): Promise<QuestClaimResponse> {
    const response = await apiClient.post<ApiResponse<QuestClaimResponse>>(`/quests/${id}/claim`);
    return response.data.data;
  },
};
