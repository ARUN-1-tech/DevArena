import React, { useState } from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import {
  Swords,
  Home,
  Code2,
  Trophy,
  Zap,
  Award,
  Users,
  User,
  LogOut,
  Menu,
  X,
  Sparkles,
  Flame,
  Shield,
  ShieldAlert,
} from 'lucide-react';
import { APP_NAME } from '../data/constants';
import { NotificationDropdown } from '../components/notification/NotificationDropdown';

interface NavItem {
  name: string;
  href: string;
  icon: React.ComponentType<{ className?: string }>;
  badge?: string;
}

const NAV_ITEMS: NavItem[] = [
  { name: 'Home', href: '/home', icon: Home },
  { name: 'Arena', href: '/arena', icon: Swords, badge: 'Live' },
  { name: 'Challenges', href: '/challenges', icon: Code2 },
  { name: 'Leaderboard', href: '/leaderboard', icon: Trophy },
  { name: 'Skills', href: '/skills', icon: Zap },
  { name: 'Achievements', href: '/achievements', icon: Award },
  { name: 'Friends', href: '/friends', icon: Users },
  { name: 'Teams', href: '/teams', icon: Shield },
  { name: 'Profile', href: '/profile', icon: User },
];

export const AppLayout: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [mobileDrawerOpen, setMobileDrawerOpen] = useState(false);

  const isAdmin = user?.roles?.includes('ROLE_ADMIN');
  const navItems = [
    ...NAV_ITEMS,
    ...(isAdmin ? [{ name: 'Admin HQ', href: '/admin', icon: ShieldAlert, badge: 'Staff' }] : []),
  ];

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const displayName = user?.displayName || user?.username || 'Player';
  const level = user?.progression?.level || 1;
  const rating = user?.stats?.rating || 1000;
  const currentXp = user?.progression?.currentXp || 0;
  const xpToNext = user?.progression?.xpToNextLevel || 1000;
  const xpPct = Math.min(100, Math.round((currentXp / Math.max(1, xpToNext)) * 100));

  return (
    <div className="min-h-screen bg-transparent text-slate-900 flex flex-col md:flex-row">
      {/* ========================================================= */}
      {/* MOBILE TOP HEADER (md:hidden)                             */}
      {/* ========================================================= */}
      <header className="md:hidden sticky top-0 z-40 bg-white/85 backdrop-blur-xl border-b border-slate-200/80 px-4 h-14 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-lg bg-gradient-to-tr from-indigo-600 via-cyan-600 to-violet-600 text-white flex items-center justify-center shadow-sm shadow-indigo-500/20">
            <Swords className="w-4 h-4" />
          </div>
          <span className="font-extrabold text-slate-900 tracking-tight">
            {APP_NAME}
          </span>
        </div>

        <div className="flex items-center gap-2">
          <NotificationDropdown />
          <div className="flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-gradient-to-r from-amber-50 to-orange-50 border border-amber-200 text-amber-900 text-xs font-mono font-bold shadow-2xs">
            <Flame className="w-3 h-3 text-amber-500 animate-pulse" />
            {rating} MMR
          </div>

          <button
            onClick={() => setMobileDrawerOpen(!mobileDrawerOpen)}
            className="p-2 rounded-lg text-slate-600 hover:bg-slate-100"
            aria-label="Toggle Menu"
          >
            {mobileDrawerOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
          </button>
        </div>
      </header>

      {/* ========================================================= */}
      {/* MOBILE DRAWER OVERLAY                                     */}
      {/* ========================================================= */}
      {mobileDrawerOpen && (
        <div className="md:hidden fixed inset-0 z-50 bg-slate-900/40 backdrop-blur-sm flex">
          <div className="w-72 bg-white h-full shadow-2xl flex flex-col p-4">
            <div className="flex items-center justify-between pb-4 border-b border-slate-100">
              <div className="flex items-center gap-2">
                <div className="w-8 h-8 rounded-lg bg-cyan-600 text-white flex items-center justify-center">
                  <Swords className="w-4 h-4" />
                </div>
                <span className="font-bold text-slate-900">{APP_NAME}</span>
              </div>
              <button
                onClick={() => setMobileDrawerOpen(false)}
                className="p-1.5 rounded-lg text-slate-500 hover:bg-slate-100"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Mobile Nav items */}
            <nav className="flex-1 py-4 space-y-1">
              {navItems.map((item) => {
                const IconComponent = item.icon;
                return (
                  <NavLink
                    key={item.name}
                    to={item.href}
                    onClick={() => setMobileDrawerOpen(false)}
                    className={({ isActive }) =>
                      `flex items-center justify-between px-3.5 py-2.5 rounded-xl font-medium text-sm transition-colors ${
                        isActive
                          ? 'bg-cyan-50 text-cyan-700 font-bold'
                          : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
                      }`
                    }
                  >
                    <div className="flex items-center gap-3">
                      <IconComponent className="w-4 h-4" />
                      <span>{item.name}</span>
                    </div>
                    {item.badge && (
                      <span className="text-[10px] font-mono font-bold bg-cyan-100 text-cyan-800 px-1.5 py-0.5 rounded">
                        {item.badge}
                      </span>
                    )}
                  </NavLink>
                );
              })}
            </nav>

            {/* Mobile Bottom Profile */}
            <div className="pt-4 border-t border-slate-100">
              <div className="flex items-center justify-between mb-3">
                <div className="flex items-center gap-2.5">
                  <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-cyan-600 to-violet-600 text-white flex items-center justify-center font-bold text-xs shadow-sm">
                    {displayName.slice(0, 2).toUpperCase()}
                  </div>
                  <div>
                    <p className="text-xs font-bold text-slate-900 leading-tight">
                      {displayName}
                    </p>
                    <p className="text-[10px] font-mono text-cyan-600">Lvl {level} Novice</p>
                  </div>
                </div>
                <button
                  onClick={handleLogout}
                  className="p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
                  title="Sign Out"
                >
                  <LogOut className="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>
          <div className="flex-1" onClick={() => setMobileDrawerOpen(false)} />
        </div>
      )}

      {/* ========================================================= */}
      {/* DESKTOP SIDEBAR (hidden on mobile, fixed width on md+)    */}
      {/* ========================================================= */}
      <aside className="hidden md:flex flex-col w-64 bg-white/85 backdrop-blur-2xl border-r border-slate-200/80 h-screen sticky top-0 shrink-0 shadow-2xs">
        {/* Brand */}
        <div className="p-4 border-b border-slate-100/90 flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-indigo-600 via-cyan-600 to-violet-600 text-white flex items-center justify-center shadow-md shadow-indigo-500/20 shrink-0 hover:scale-105 transition-transform duration-300">
            <Swords className="w-5 h-5" />
          </div>
          <div className="min-w-0 flex-1">
            <span className="text-lg font-black tracking-tight text-slate-900 block leading-tight truncate">
              {APP_NAME}
            </span>
            <span className="text-[10px] font-mono text-indigo-600 uppercase font-bold tracking-wider block">
              Battle Engine v1.0
            </span>
          </div>
        </div>

        {/* Navigation list */}
        <nav className="flex-1 p-3 space-y-1 overflow-y-auto">
          {navItems.map((item) => {
            const IconComponent = item.icon;
            return (
              <NavLink
                key={item.name}
                to={item.href}
                className={({ isActive }) =>
                  `group flex items-center justify-between px-3.5 py-2.5 rounded-xl text-xs font-semibold tracking-wide transition-all duration-200 ${
                    isActive
                      ? 'bg-gradient-to-r from-indigo-50 via-cyan-50/40 to-white text-indigo-700 font-bold border border-indigo-200/80 shadow-xs'
                      : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900 hover:translate-x-0.5'
                  }`
                }
              >
                <div className="flex items-center gap-3">
                  <IconComponent className="w-4 h-4 transition-transform duration-200 group-hover:scale-110" />
                  <span>{item.name}</span>
                </div>
                {item.badge && (
                  <span
                    className={`inline-flex items-center gap-1 text-[9px] font-mono font-bold px-2 py-0.5 rounded-full ${
                      item.badge === 'Live'
                        ? 'bg-rose-50 text-rose-700 border border-rose-200/80 shadow-2xs'
                        : 'bg-indigo-50 text-indigo-700 border border-indigo-200/80'
                    }`}
                  >
                    {item.badge === 'Live' && (
                      <span className="relative flex h-1.5 w-1.5">
                        <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-rose-400 opacity-75" />
                        <span className="relative inline-flex rounded-full h-1.5 w-1.5 bg-rose-500" />
                      </span>
                    )}
                    {item.badge}
                  </span>
                )}
              </NavLink>
            );
          })}
        </nav>

        {/* Mini Player Profile at Bottom */}
        <div className="p-3 border-t border-slate-100/90 bg-slate-50/50 backdrop-blur-sm">
          <div className="p-3.5 rounded-xl bg-white/95 border border-slate-200/90 shadow-2xs hover:shadow-xs transition-shadow">
            <div className="flex items-center justify-between mb-2.5">
              <div className="flex items-center gap-2.5 min-w-0">
                <div className="w-9 h-9 shrink-0 rounded-xl bg-gradient-to-tr from-indigo-600 to-cyan-600 text-white flex items-center justify-center font-black text-xs shadow-sm shadow-indigo-500/20">
                  {displayName.slice(0, 2).toUpperCase()}
                </div>
                <div className="min-w-0">
                  <p className="text-xs font-bold text-slate-900 truncate">
                    {displayName}
                  </p>
                  <p className="text-[10px] font-mono text-indigo-600 font-bold flex items-center gap-1">
                    <Sparkles className="w-2.5 h-2.5 text-amber-500" />
                    Lvl {level} • {rating} MMR
                  </p>
                </div>
              </div>

              <button
                onClick={handleLogout}
                className="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors shrink-0 cursor-pointer"
                title="Log Out"
                aria-label="Log Out"
              >
                <LogOut className="w-4 h-4" />
              </button>
            </div>

            {/* XP progress bar */}
            <div className="space-y-1">
              <div className="flex justify-between text-[9px] font-mono font-medium text-slate-500">
                <span>XP Mastery</span>
                <span className="font-bold text-indigo-600">{xpPct}%</span>
              </div>
              <div className="h-1.5 w-full bg-slate-100 rounded-full overflow-hidden p-0.5">
                <div
                  className="h-full bg-gradient-to-r from-indigo-500 via-cyan-500 to-violet-500 rounded-full transition-all duration-500"
                  style={{ width: `${Math.max(5, xpPct)}%` }}
                />
              </div>
            </div>
          </div>
        </div>
      </aside>

      {/* ========================================================= */}
      {/* DESKTOP TOP HEADER + MAIN CONTENT WORKSPACE               */}
      {/* ========================================================= */}
      <div className="flex-1 flex flex-col min-w-0 h-screen overflow-hidden">
        {/* Desktop Sticky Header */}
        <header className="hidden md:flex h-16 bg-white/80 backdrop-blur-xl border-b border-slate-200/80 px-6 lg:px-8 items-center justify-between shrink-0 z-30 shadow-2xs">
          <div className="flex items-center gap-3">
            <div className="flex items-center gap-2 px-3 py-1 rounded-full bg-gradient-to-r from-indigo-50 via-cyan-50 to-white border border-indigo-100/90 text-indigo-800 text-xs font-mono font-bold shadow-2xs">
              <Sparkles className="w-3.5 h-3.5 text-indigo-500" />
              <span>ARENA HQ</span>
              <span className="text-slate-300">•</span>
              <span className="text-slate-600 font-medium">BATTLE SYSTEM V1.0</span>
            </div>
          </div>

          <div className="flex items-center gap-3">
            {/* Competitive MMR Pill */}
            <div className="flex items-center gap-2 px-3 py-1.5 rounded-xl bg-gradient-to-r from-amber-50 to-orange-50 border border-amber-200/80 text-amber-900 text-xs font-mono font-bold shadow-2xs">
              <Flame className="w-3.5 h-3.5 text-amber-500 animate-pulse" />
              <span>{rating} MMR</span>
            </div>

            {/* Notification Dropdown with Floating Glassmorphic Popover */}
            <NotificationDropdown />
          </div>
        </header>

        {/* Smooth Scrolling Main Workspace */}
        <main className="flex-1 overflow-y-auto scroll-smooth">
          <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
};

