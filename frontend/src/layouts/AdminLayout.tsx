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
    <div className="min-h-screen bg-slate-50 text-slate-800 flex flex-col">
      {/* Top Admin Header */}
      <header className="h-16 border-b border-slate-200/80 bg-white/85 backdrop-blur-xl px-6 flex items-center justify-between sticky top-0 z-30 shadow-2xs">
        <div className="flex items-center gap-4">
          <Link
            to="/home"
            className="flex items-center gap-1.5 text-xs text-slate-600 hover:text-indigo-600 transition px-2.5 py-1.5 rounded-xl border border-slate-200 hover:bg-slate-50 shadow-2xs font-semibold"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Arena HQ</span>
          </Link>

          <div className="h-4 w-px bg-slate-200" />

          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-xl bg-gradient-to-tr from-indigo-600 to-cyan-600 flex items-center justify-center text-white shadow-sm shadow-indigo-500/25">
              <Shield className="w-4 h-4" />
            </div>
            <div>
              <h1 className="text-sm font-black tracking-tight text-slate-900 flex items-center gap-2">
                DevArena Command HQ
                <span className="px-1.5 py-0.5 rounded-full text-[10px] font-bold bg-rose-50 text-rose-700 border border-rose-200">
                  ADMIN
                </span>
              </h1>
            </div>
          </div>
        </div>

        <div className="flex items-center gap-3 text-xs text-slate-500">
          <span>Logged in as <strong className="text-slate-800 font-bold">{user?.username}</strong></span>
        </div>
      </header>

      <div className="flex-1 flex flex-col md:flex-row">
        {/* Navigation Sidebar */}
        <aside className="w-full md:w-64 border-b md:border-b-0 md:border-r border-slate-200/80 bg-white/70 backdrop-blur-lg p-4 space-y-1">
          <div className="text-[11px] uppercase tracking-wider font-bold text-slate-400 px-3 py-2 font-mono">
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
                  `flex items-center gap-3 px-3 py-2.5 rounded-xl text-xs font-bold transition-all ${
                    isActive
                      ? 'bg-gradient-to-r from-indigo-600 to-cyan-600 text-white shadow-sm shadow-indigo-500/25'
                      : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
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
        <main className="flex-1 p-6 lg:p-8 max-w-7xl mx-auto w-full overflow-x-hidden scroll-smooth">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
