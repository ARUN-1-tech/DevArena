import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { authService } from '../services/authService';
import { PlayerProfile, LoginPayload, RegisterPayload, UpdateProfilePayload } from '../types/auth';

interface AuthContextType {
  user: PlayerProfile | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (payload: LoginPayload) => Promise<void>;
  register: (payload: RegisterPayload) => Promise<void>;
  updateProfile: (payload: UpdateProfilePayload) => Promise<PlayerProfile>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<PlayerProfile | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('devarena_token'));
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const fetchCurrentUser = useCallback(async () => {
    try {
      setIsLoading(true);
      const profile = await authService.getMe();
      setUser(profile);
    } catch {
      localStorage.removeItem('devarena_token');
      localStorage.removeItem('devarena_refresh_token');
      setToken(null);
      setUser(null);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    if (token) {
      fetchCurrentUser();
    } else {
      setIsLoading(false);
    }
  }, [token, fetchCurrentUser]);

  const login = async (payload: LoginPayload) => {
    const authData = await authService.login(payload);
    localStorage.setItem('devarena_token', authData.accessToken);
    localStorage.setItem('devarena_refresh_token', authData.refreshToken);
    setToken(authData.accessToken);
    // Fetch full profile (including stats and progression)
    const profile = await authService.getMe();
    setUser(profile);
  };

  const register = async (payload: RegisterPayload) => {
    const authData = await authService.register(payload);
    localStorage.setItem('devarena_token', authData.accessToken);
    localStorage.setItem('devarena_refresh_token', authData.refreshToken);
    setToken(authData.accessToken);
    const profile = await authService.getMe();
    setUser(profile);
  };

  const updateProfile = async (payload: UpdateProfilePayload) => {
    const updated = await authService.updateProfile(payload);
    setUser(updated);
    return updated;
  };

  const logout = async () => {
    await authService.logout();
    localStorage.removeItem('devarena_token');
    localStorage.removeItem('devarena_refresh_token');
    setToken(null);
    setUser(null);
  };

  const refreshUser = async () => {
    if (token) {
      await fetchCurrentUser();
    }
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!user,
        isLoading,
        login,
        register,
        updateProfile,
        logout,
        refreshUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
