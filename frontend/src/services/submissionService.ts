import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/api';
import {
  RunCodeRequest,
  RunCodeResponse,
  SubmitCodeRequest,
  SubmitCodeResponse,
  SubmissionDetail,
  SubmissionSummary,
  ChallengeDetailWithCode,
  ChallengeProgressDetail,
} from '../types/submission';

export const submissionService = {
  async runCode(payload: RunCodeRequest): Promise<RunCodeResponse> {
    const res = await apiClient.post<ApiResponse<RunCodeResponse>>('/code/run', payload);
    return res.data.data;
  },

  async submitCode(payload: SubmitCodeRequest): Promise<SubmitCodeResponse> {
    const res = await apiClient.post<ApiResponse<SubmitCodeResponse>>('/submissions', payload);
    return res.data.data;
  },

  async getSubmission(id: string): Promise<SubmissionDetail> {
    const res = await apiClient.get<ApiResponse<SubmissionDetail>>(`/submissions/${id}`);
    return res.data.data;
  },

  async getChallengeSubmissions(
    challengeId: string,
    page: number = 0,
    size: number = 20
  ): Promise<{ content: SubmissionSummary[]; totalElements: number }> {
    const res = await apiClient.get<ApiResponse<{ content: SubmissionSummary[]; totalElements: number }>>(
      `/challenges/${challengeId}/submissions?page=${page}&size=${size}`
    );
    return res.data.data;
  },

  async getChallengeProgress(challengeId: string): Promise<ChallengeProgressDetail> {
    const res = await apiClient.get<ApiResponse<ChallengeProgressDetail>>(`/challenges/${challengeId}/progress`);
    return res.data.data;
  },

  async getChallengeCodeLab(challengeId: string): Promise<ChallengeDetailWithCode> {
    const res = await apiClient.get<ApiResponse<ChallengeDetailWithCode>>(`/challenges/${challengeId}`);
    return res.data.data;
  },
};
