import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/api';
import { CreateTeamPayload, Team, TeamInvite, TeamLeaderboardItem } from '../types/social';

interface TeamLeaderboardPage {
  content: TeamLeaderboardItem[];
  totalElements: number;
  totalPages: number;
  number: number;
}

export const teamService = {
  async getMyTeam(): Promise<Team | null> {
    const res = await apiClient.get<ApiResponse<Team | null>>('/teams/me');
    return res.data.data;
  },

  async getTeam(id: string): Promise<Team> {
    const res = await apiClient.get<ApiResponse<Team>>(`/teams/${id}`);
    return res.data.data;
  },

  async createTeam(payload: CreateTeamPayload): Promise<Team> {
    const res = await apiClient.post<ApiResponse<Team>>('/teams', payload);
    return res.data.data;
  },

  async searchTeams(query = '', page = 0, size = 20): Promise<Team[]> {
    const res = await apiClient.get<ApiResponse<Team[]>>(
      `/teams/search?q=${encodeURIComponent(query)}&page=${page}&size=${size}`
    );
    return res.data.data;
  },

  async invitePlayer(teamId: string, playerId: string): Promise<TeamInvite> {
    const res = await apiClient.post<ApiResponse<TeamInvite>>(`/teams/${teamId}/invite/${playerId}`);
    return res.data.data;
  },

  async acceptInvite(inviteId: string): Promise<Team> {
    const res = await apiClient.post<ApiResponse<Team>>(`/teams/invites/${inviteId}/accept`);
    return res.data.data;
  },

  async rejectInvite(inviteId: string): Promise<void> {
    await apiClient.post<ApiResponse<void>>(`/teams/invites/${inviteId}/reject`);
  },

  async removeMember(teamId: string, playerId: string): Promise<void> {
    await apiClient.delete<ApiResponse<void>>(`/teams/${teamId}/members/${playerId}`);
  },

  async leaveTeam(teamId: string): Promise<void> {
    await apiClient.post<ApiResponse<void>>(`/teams/${teamId}/leave`);
  },

  async getTeamLeaderboard(page = 0, size = 20): Promise<TeamLeaderboardPage> {
    const res = await apiClient.get<ApiResponse<TeamLeaderboardPage>>(
      `/teams/leaderboard?page=${page}&size=${size}`
    );
    return res.data.data;
  },
};
