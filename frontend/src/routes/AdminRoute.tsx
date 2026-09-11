import React from 'react';
import { Navigate, useLocation, Outlet } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { ShieldAlert, Shield } from 'lucide-react';

export const AdminRoute: React.FC<{ children?: React.ReactNode }> = ({ children }) => {
  const { user, isAuthenticated, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-slate-950 text-slate-200 space-y-4">
        <div className="w-12 h-12 rounded-2xl bg-indigo-600 flex items-center justify-center text-white shadow-lg animate-pulse">
          <Shield className="w-6 h-6" />
        </div>
        <p className="text-sm font-mono font-medium text-slate-400">Verifying administrative credentials...</p>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  const isAdmin = user?.roles?.includes('ROLE_ADMIN');

  if (!isAdmin) {
    return (
      <div className="min-h-[80vh] flex flex-col items-center justify-center text-center p-6">
        <div className="w-16 h-16 rounded-2xl bg-rose-500/10 border border-rose-500/20 text-rose-400 flex items-center justify-center mb-4">
          <ShieldAlert className="w-8 h-8" />
        </div>
        <h2 className="text-2xl font-bold text-slate-900 dark:text-white">403 — Access Restricted</h2>
        <p className="text-sm text-slate-500 dark:text-slate-400 max-w-md mt-2">
          You do not have administrative privileges to access this area of DevArena. If you believe this is an error, please contact system administration.
        </p>
        <a
          href="/home"
          className="mt-6 px-5 py-2.5 rounded-xl bg-cyan-600 hover:bg-cyan-500 text-white text-xs font-semibold shadow-md transition"
        >
          Return to Arena HQ
        </a>
      </div>
    );
  }

  return children ? <>{children}</> : <Outlet />;
};
