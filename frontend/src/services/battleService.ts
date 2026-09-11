import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/api';
import {
  BattleDetail,
  BattleHistoryItem,
  BattleResult,
  MatchmakingStatus,
} from '../types/battle';
import { SubmissionStatus } from '../types/submission';

export interface BattleSubmitPayload {
  language: string;
  sourceCode: string;
}

export interface BattleSubmitResult {
  status: SubmissionStatus;
  passedTests: number;
  totalTests: number;
  executionTimeMs: number;
  battleFinished: boolean;
  winnerId?: string | null;
  finishReason?: string | null;
  errorMessage?: string | null;
}

export const battleService = {
  // Matchmaking
  async joinMatchmaking(): Promise<MatchmakingStatus> {
    const res = await apiClient.post<ApiResponse<MatchmakingStatus>>('/matchmaking/join');
    return res.data.data;
  },

  async leaveMatchmaking(): Promise<void> {
    await apiClient.delete<ApiResponse<void>>('/matchmaking/leave');
  },

  async getMatchmakingStatus(): Promise<MatchmakingStatus> {
    const res = await apiClient.get<ApiResponse<MatchmakingStatus>>('/matchmaking/status');
    return res.data.data;
  },

  // Battles
  async getBattle(id: string): Promise<BattleDetail> {
    const res = await apiClient.get<ApiResponse<BattleDetail>>(`/battles/${id}`);
    return res.data.data;
  },

  async getBattleResult(id: string): Promise<BattleResult> {
    const res = await apiClient.get<ApiResponse<BattleResult>>(`/battles/${id}/result`);
    return res.data.data;
  },

  async getBattleHistory(page = 0, size = 10): Promise<{ content: BattleHistoryItem[]; totalElements: number }> {
    const res = await apiClient.get<ApiResponse<{ content: BattleHistoryItem[]; totalElements: number }>>(
      `/battles/history?page=${page}&size=${size}`
    );
    return res.data.data;
  },

  async submitBattleCode(id: string, payload: BattleSubmitPayload): Promise<BattleSubmitResult> {
    const res = await apiClient.post<ApiResponse<BattleSubmitResult>>(`/battles/${id}/submit`, payload);
    return res.data.data;
  },

  async markReady(id: string): Promise<BattleDetail> {
    const res = await apiClient.post<ApiResponse<BattleDetail>>(`/battles/${id}/ready`);
    return res.data.data;
  },

  async forfeitBattle(id: string): Promise<void> {
    await apiClient.post<ApiResponse<void>>(`/battles/${id}/forfeit`);
  },
};
