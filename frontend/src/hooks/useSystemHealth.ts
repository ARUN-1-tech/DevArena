import { useState, useEffect } from 'react';
import { healthService } from '../services/healthService';
import { SystemHealthData, SystemStatusData } from '../types/api';

export function useSystemHealth() {
  const [health, setHealth] = useState<SystemHealthData | null>(null);
  const [status, setStatus] = useState<SystemStatusData | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchHealth = async () => {
    try {
      setIsLoading(true);
      const [healthData, statusData] = await Promise.all([
        healthService.getHealth(),
        healthService.getStatus(),
      ]);
      setHealth(healthData);
      setStatus(statusData);
      setError(null);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Unable to connect to DevArena backend';
      setError(msg);
      setHealth(null);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchHealth();
    // Poll every 30 seconds
    const interval = setInterval(fetchHealth, 30000);
    return () => clearInterval(interval);
  }, []);

  return {
    health,
    status,
    isLoading,
    error,
    refetch: fetchHealth,
    isConnected: !!health && health.status === 'UP',
  };
}
