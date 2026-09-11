import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/api';
import { Friend, FriendBattleInvite, FriendRequest, FriendRequestsResponse } from '../types/social';

export const friendService = {
  async getFriends(): Promise<Friend[]> {
    const res = await apiClient.get<ApiResponse<Friend[]>>('/friends');
    return res.data.data;
  },

  async getFriendRequests(): Promise<FriendRequestsResponse> {
    const res = await apiClient.get<ApiResponse<FriendRequestsResponse>>('/friends/requests');
    return res.data.data;
  },

  async sendFriendRequest(playerId: string): Promise<FriendRequest> {
    const res = await apiClient.post<ApiResponse<FriendRequest>>(`/friends/requests/${playerId}`);
    return res.data.data;
  },

  async acceptFriendRequest(requestId: string): Promise<Friend> {
    const res = await apiClient.post<ApiResponse<Friend>>(`/friends/requests/${requestId}/accept`);
    return res.data.data;
  },

  async rejectFriendRequest(requestId: string): Promise<void> {
    await apiClient.post<ApiResponse<void>>(`/friends/requests/${requestId}/reject`);
  },

  async removeFriend(playerId: string): Promise<void> {
    await apiClient.delete<ApiResponse<void>>(`/friends/${playerId}`);
  },

  async challengeFriend(friendId: string, challengeId?: string): Promise<FriendBattleInvite> {
    const url = challengeId
      ? `/friends/challenge/${friendId}?challengeId=${challengeId}`
      : `/friends/challenge/${friendId}`;
    const res = await apiClient.post<ApiResponse<FriendBattleInvite>>(url);
    return res.data.data;
  },

  async acceptChallenge(inviteId: string): Promise<{ battleId: string; status: string; challengeTitle: string }> {
    const res = await apiClient.post<ApiResponse<{ battleId: string; status: string; challengeTitle: string }>>(
      `/friends/challenge/${inviteId}/accept`
    );
    return res.data.data;
  },
};
