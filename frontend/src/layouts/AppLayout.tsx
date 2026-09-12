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
    <div className="min-h-screen bg-[#0B0C0E] text-[#F0F1F3] flex flex-col md:flex-row selection:bg-[#3E4148] selection:text-white">
      {/* ========================================================= */}
      {/* MOBILE TOP HEADER (md:hidden)                             */}
      {/* ========================================================= */}
      <header className="md:hidden sticky top-0 z-40 bg-[#111215]/95 backdrop-blur-xl border-b border-[#212328] px-4 h-14 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-xl bg-white text-[#0B0C0E] flex items-center justify-center shadow-glow-white font-bold">
            <Swords className="w-4 h-4" />
          </div>
          <span className="font-black text-white tracking-tight">
            {APP_NAME}
          </span>
        </div>

        <div className="flex items-center gap-2">
          <NotificationDropdown />
          <div className="flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-[#1A1B1F] border border-[#27292F] text-[#D4D7DC] text-xs font-mono font-bold">
            <Flame className="w-3 h-3 text-white" />
            {rating} MMR
          </div>

          <button
            onClick={() => setMobileDrawerOpen(!mobileDrawerOpen)}
            className="p-2 rounded-xl text-[#B2B6BD] hover:text-white hover:bg-[#1A1B1F]"
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
        <div className="md:hidden fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex">
          <div className="w-72 bg-[#111215] border-r border-[#212328] h-full shadow-2xl flex flex-col p-4">
            <div className="flex items-center justify-between pb-4 border-b border-[#212328]">
              <div className="flex items-center gap-2">
                <div className="w-8 h-8 rounded-xl bg-white text-[#0B0C0E] flex items-center justify-center font-bold">
                  <Swords className="w-4 h-4" />
                </div>
                <span className="font-black text-white">{APP_NAME}</span>
              </div>
              <button
                onClick={() => setMobileDrawerOpen(false)}
                className="p-1.5 rounded-lg text-[#868A94] hover:text-white hover:bg-[#1A1B1F]"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Mobile Nav items */}
            <nav className="flex-1 py-4 space-y-1 overflow-y-auto">
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
                          ? 'bg-[#1E2025] text-white font-bold border border-[#3E4148] shadow-xs'
                          : 'text-[#868A94] hover:bg-[#17181C] hover:text-[#F0F1F3]'
                      }`
                    }
                  >
                    <div className="flex items-center gap-3">
                      <IconComponent className="w-4 h-4" />
                      <span>{item.name}</span>
                    </div>
                    {item.badge && (
                      <span className="text-[10px] font-mono font-bold bg-[#27292F] text-[#D4D7DC] px-1.5 py-0.5 rounded">
                        {item.badge}
                      </span>
                    )}
                  </NavLink>
                );
              })}
            </nav>

            {/* Mobile Bottom Profile */}
            <div className="pt-4 border-t border-[#212328]">
              <div className="flex items-center justify-between mb-3">
                <div className="flex items-center gap-2.5">
                  <div className="w-9 h-9 rounded-xl bg-[#27292F] border border-[#3E4148] text-white flex items-center justify-center font-bold text-xs shadow-sm">
                    {displayName.slice(0, 2).toUpperCase()}
                  </div>
                  <div>
                    <p className="text-xs font-bold text-white leading-tight">
                      {displayName}
                    </p>
                    <p className="text-[10px] font-mono text-[#A3A7AF]">Lvl {level} Novice</p>
                  </div>
                </div>
                <button
                  onClick={handleLogout}
                  className="p-2 text-[#868A94] hover:text-rose-400 hover:bg-[#2A1215] rounded-xl transition-colors"
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
      <aside className="hidden md:flex flex-col w-64 bg-[#101114] border-r border-[#212328] h-screen sticky top-0 shrink-0">
        {/* Brand */}
        <div className="p-4 border-b border-[#212328] flex items-center justify-between">
          <div className="flex items-center gap-3 min-w-0">
            <div className="w-9 h-9 rounded-xl bg-white text-[#0B0C0E] flex items-center justify-center shadow-glow-white shrink-0 font-bold">
              <Swords className="w-5 h-5" />
            </div>
            <div className="min-w-0">
              <span className="text-lg font-black tracking-tight text-white block leading-tight truncate">
                {APP_NAME}
              </span>
              <span className="text-[10px] font-mono text-[#868A94] uppercase font-semibold tracking-wider block">
                Battle Engine v1.0
              </span>
            </div>
          </div>
          <NotificationDropdown />
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
                  `flex items-center justify-between px-3 py-2.5 rounded-xl text-xs font-semibold tracking-wide transition-all ${
                    isActive
                      ? 'bg-[#1E2025] text-white font-bold border border-[#3E4148] shadow-xs'
                      : 'text-[#868A94] hover:bg-[#16171B] hover:text-[#F0F1F3]'
                  }`
                }
              >
                <div className="flex items-center gap-3">
                  <IconComponent className="w-4 h-4" />
                  <span>{item.name}</span>
                </div>
                {item.badge && (
                  <span className="text-[9px] font-mono font-bold bg-[#27292F] text-[#D4D7DC] px-2 py-0.5 rounded-full border border-[#3E4148]/60">
                    {item.badge}
                  </span>
                )}
              </NavLink>
            );
          })}
        </nav>

        {/* Mini Player Profile at Bottom */}
        <div className="p-3 border-t border-[#212328] bg-[#0E0F12]">
          <div className="p-3 rounded-2xl bg-[#16171B] border border-[#27292F] shadow-luxury-card">
            <div className="flex items-center justify-between mb-2">
              <div className="flex items-center gap-2.5 min-w-0">
                <div className="w-9 h-9 shrink-0 rounded-xl bg-[#27292F] border border-[#3E4148] text-white flex items-center justify-center font-bold text-xs shadow-sm">
                  {displayName.slice(0, 2).toUpperCase()}
                </div>
                <div className="min-w-0">
                  <p className="text-xs font-bold text-white truncate">
                    {displayName}
                  </p>
                  <p className="text-[10px] font-mono text-[#B2B6BD] font-medium flex items-center gap-1">
                    <Sparkles className="w-2.5 h-2.5 text-white" />
                    Lvl {level} • {rating} MMR
                  </p>
                </div>
              </div>

              <button
                onClick={handleLogout}
                className="p-1.5 text-[#6C717B] hover:text-rose-400 hover:bg-[#2A1215] rounded-lg transition-colors shrink-0"
                title="Log Out"
                aria-label="Log Out"
              >
                <LogOut className="w-4 h-4" />
              </button>
            </div>

            {/* XP progress bar */}
            <div className="space-y-1">
              <div className="flex justify-between text-[9px] font-mono text-[#868A94]">
                <span>XP Progress</span>
                <span className="text-[#D4D7DC] font-semibold">{xpPct}%</span>
              </div>
              <div className="h-1.5 w-full bg-[#27292F] rounded-full overflow-hidden">
                <div
                  className="h-full bg-gradient-to-r from-white via-[#D4D7DC] to-[#868A94] rounded-full transition-all duration-300"
                  style={{ width: `${Math.max(5, xpPct)}%` }}
                />
              </div>
            </div>
          </div>
        </div>
      </aside>

      {/* ========================================================= */}
      {/* MAIN CONTENT WORKSPACE                                    */}
      {/* ========================================================= */}
      <main className="flex-1 overflow-y-auto bg-[#0B0C0E]">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <Outlet />
        </div>
      </main>
    </div>
  );
};
