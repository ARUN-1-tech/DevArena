import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/auth';
import {
  Challenge,
  ChallengeCategory,
  ChallengeDifficulty,
  ChallengeProgressStatus,
  ChallengeStats,
  ProblemType,
  SubmitAnswerResponse,
  XpRewardResult,
} from '../types/arena';

export interface PageResponse<T> {
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
  problemType?: ProblemType;
  sortBy?: 'recommended' | 'newest' | 'xp' | 'difficulty' | string;
  page?: number;
  size?: number;
}

export const challengeService = {
  async getChallenges(params?: ChallengeFilterParams): Promise<PageResponse<Challenge>> {
    const response = await apiClient.get<ApiResponse<PageResponse<Challenge>>>('/challenges', {
      params,
    });
    return response.data.data;
  },

  async getChallengeStats(): Promise<ChallengeStats> {
    const response = await apiClient.get<ApiResponse<ChallengeStats>>('/challenges/stats');
    return response.data.data;
  },

  async getChallenge(id: string): Promise<Challenge> {
    const response = await apiClient.get<ApiResponse<Challenge>>(`/challenges/${id}`);
    return response.data.data;
  },

  async startChallenge(id: string): Promise<{ challengeId: string; status: ChallengeProgressStatus }> {
    const response = await apiClient.post<ApiResponse<{ challengeId: string; status: ChallengeProgressStatus }>>(`/challenges/${id}/start`);
    return response.data.data;
  },

  async completeChallenge(id: string): Promise<XpRewardResult> {
    const response = await apiClient.post<ApiResponse<XpRewardResult>>(`/challenges/${id}/complete`);
    return response.data.data;
  },

  async submitAnswer(id: string, answer: string): Promise<SubmitAnswerResponse> {
    const response = await apiClient.post<ApiResponse<SubmitAnswerResponse>>(`/challenges/${id}/answer`, {
      answer,
    });
    return response.data.data;
  },
};

