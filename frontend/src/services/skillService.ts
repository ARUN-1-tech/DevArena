import { apiClient } from '../lib/api-client';
import { ApiResponse } from '../types/api';
import { PlayerSkill, Skill } from '../types/progression';

export const skillService = {
  async getSkillTree(): Promise<Skill[]> {
    const res = await apiClient.get<ApiResponse<Skill[]>>('/skills/tree');
    return res.data.data;
  },

  async getMySkills(): Promise<PlayerSkill[]> {
    const res = await apiClient.get<ApiResponse<PlayerSkill[]>>('/skills/me');
    return res.data.data;
  },
};
