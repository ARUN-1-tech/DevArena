import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/auth';
import { Challenge, ChallengeCategory, ChallengeDifficulty, ChallengeProgressStatus, XpRewardResult } from '../types/arena';

interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface ChallengeFilterParams {
  search?: string;
  difficulty?: ChallengeDifficulty;
  category?: ChallengeCategory;
  page?: number;
  size?: number;
}

export const challengeService = {
  async getChallenges(params?: ChallengeFilterParams): Promise<PageResponse<Challenge>> {
    const response = await apiClient.get<ApiResponse<PageResponse<Challenge>>>('/api/v1/challenges', {
      params,
    });
    return response.data.data;
  },

  async getChallenge(id: string): Promise<Challenge> {
    const response = await apiClient.get<ApiResponse<Challenge>>(`/api/v1/challenges/${id}`);
    return response.data.data;
  },

  async startChallenge(id: string): Promise<{ challengeId: string; status: ChallengeProgressStatus }> {
    const response = await apiClient.post<ApiResponse<{ challengeId: string; status: ChallengeProgressStatus }>>(`/api/v1/challenges/${id}/start`);
    return response.data.data;
  },

  async completeChallenge(id: string): Promise<XpRewardResult> {
    const response = await apiClient.post<ApiResponse<XpRewardResult>>(`/api/v1/challenges/${id}/complete`);
    return response.data.data;
  },
};
