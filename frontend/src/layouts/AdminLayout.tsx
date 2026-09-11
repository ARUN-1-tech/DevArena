import React from 'react';
import { NavLink, Outlet, Link } from 'react-router-dom';
import {
  LayoutDashboard,
  Users,
  Code2,
  Flag,
  ShieldCheck,
  History,
  ArrowLeft,
  Shield,
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';

export const AdminLayout: React.FC = () => {
  const { user } = useAuth();

  const navItems = [
    { to: '/admin', end: true, label: 'Overview', icon: LayoutDashboard },
    { to: '/admin/players', end: false, label: 'Players', icon: Users },
    { to: '/admin/challenges', end: false, label: 'Challenges', icon: Code2 },
    { to: '/admin/reports', end: false, label: 'Reports', icon: Flag },
    { to: '/admin/integrity', end: false, label: 'Anti-Cheat', icon: ShieldCheck },
    { to: '/admin/audit', end: false, label: 'Audit Logs', icon: History },
  ];

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col">
      {/* Top Admin Header */}
      <header className="h-16 border-b border-slate-800 bg-slate-900/80 backdrop-blur-md px-6 flex items-center justify-between sticky top-0 z-30">
        <div className="flex items-center gap-4">
          <Link
            to="/home"
            className="flex items-center gap-1.5 text-xs text-slate-400 hover:text-slate-200 transition px-2.5 py-1.5 rounded-lg border border-slate-800 hover:bg-slate-800"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Arena HQ</span>
          </Link>

          <div className="h-4 w-px bg-slate-800" />

          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-indigo-600 flex items-center justify-center text-white shadow-md">
              <Shield className="w-4 h-4" />
            </div>
            <div>
              <h1 className="text-sm font-bold tracking-tight text-white flex items-center gap-2">
                DevArena Command HQ
                <span className="px-1.5 py-0.5 rounded text-[10px] font-semibold bg-rose-500/10 text-rose-400 border border-rose-500/20">
                  ADMIN
                </span>
              </h1>
            </div>
          </div>
        </div>

        <div className="flex items-center gap-3 text-xs text-slate-400">
          <span>Logged in as <strong className="text-slate-200">{user?.username}</strong></span>
        </div>
      </header>

      <div className="flex-1 flex flex-col md:flex-row">
        {/* Navigation Sidebar */}
        <aside className="w-full md:w-64 border-b md:border-b-0 md:border-r border-slate-800 bg-slate-900/40 p-4 space-y-1">
          <div className="text-[11px] uppercase tracking-wider font-semibold text-slate-500 px-3 py-2">
            Administration
          </div>
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                className={({ isActive }) =>
                  `flex items-center gap-3 px-3 py-2.5 rounded-xl text-xs font-medium transition ${
                    isActive
                      ? 'bg-indigo-600 text-white shadow-md shadow-indigo-600/20'
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                  }`
                }
              >
                <Icon className="w-4 h-4" />
                <span>{item.label}</span>
              </NavLink>
            );
          })}
        </aside>

        {/* Main Content Area */}
        <main className="flex-1 p-6 lg:p-8 max-w-7xl mx-auto w-full overflow-x-hidden">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
