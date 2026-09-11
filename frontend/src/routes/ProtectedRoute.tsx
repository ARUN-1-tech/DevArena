import React from 'react';
import { Navigate, useLocation, Outlet } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { Swords } from 'lucide-react';

export const ProtectedRoute: React.FC<{ children?: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-slate-50 text-slate-800 space-y-4">
        <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-cyan-600 to-violet-600 flex items-center justify-center text-white shadow-md animate-bounce">
          <Swords className="w-6 h-6" />
        </div>
        <p className="text-sm font-mono font-semibold text-slate-600 animate-pulse">
          Entering the arena...
        </p>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return children ? <>{children}</> : <Outlet />;
};
