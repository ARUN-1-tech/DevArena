import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/api';
import { AiCoachRequest, AiCoachResponse, PersonalizedRecommendation } from '../types/ai';

export const aiCoachService = {
  async askCoach(request: AiCoachRequest): Promise<AiCoachResponse> {
    const res = await apiClient.post<ApiResponse<AiCoachResponse>>('/ai/coach', request);
    return res.data.data;
  },

  async getRecommendations(): Promise<PersonalizedRecommendation> {
    const res = await apiClient.get<ApiResponse<PersonalizedRecommendation>>('/ai/recommendations');
    return res.data.data;
  },
};
