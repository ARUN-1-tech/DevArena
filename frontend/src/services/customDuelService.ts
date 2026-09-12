import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/api';
import { CustomDuelRoom } from '../types/social';

export interface CreateCustomDuelRequest {
  challengeId?: string;
  difficulty?: string;
  durationSeconds?: number;
}

export const customDuelService = {
  async createRoom(request?: CreateCustomDuelRequest): Promise<CustomDuelRoom> {
    const res = await apiClient.post<ApiResponse<CustomDuelRoom>>('/battles/custom/create', request || {});
    return res.data.data;
  },

  async joinRoom(roomCode: string): Promise<CustomDuelRoom> {
    const res = await apiClient.post<ApiResponse<CustomDuelRoom>>('/battles/custom/join', {
      roomCode: roomCode.trim().toUpperCase(),
    });
    return res.data.data;
  },

  async getRoom(roomCode: string): Promise<CustomDuelRoom> {
    const res = await apiClient.get<ApiResponse<CustomDuelRoom>>(`/battles/custom/room/${roomCode.trim().toUpperCase()}`);
    return res.data.data;
  },

  async cancelRoom(roomCode: string): Promise<void> {
    await apiClient.post<ApiResponse<void>>(`/battles/custom/room/${roomCode.trim().toUpperCase()}/cancel`);
  },
};
