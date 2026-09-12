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
    <div className="min-h-screen bg-[#0D0506] text-[#EFEFE1] flex flex-col md:flex-row selection:bg-[#72282D] selection:text-white">
      {/* ========================================================= */}
      {/* MOBILE TOP HEADER (md:hidden)                             */}
      {/* ========================================================= */}
      <header className="md:hidden sticky top-0 z-40 bg-[#160809]/95 backdrop-blur-xl border-b border-[#3A1417] px-4 h-14 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 rounded-xl bg-gradient-to-br from-[#72282D] to-[#8E3239] text-[#EFEFE1] flex items-center justify-center shadow-glow-wine font-bold border border-[#A6464E]/40">
            <Swords className="w-4 h-4" />
          </div>
          <span className="font-black text-[#EFEFE1] tracking-tight">
            {APP_NAME}
          </span>
        </div>

        <div className="flex items-center gap-2">
          <NotificationDropdown />
          <div className="flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-[#220D0F] border border-[#3A1417] text-[#D1C7BD] text-xs font-mono font-bold">
            <Flame className="w-3 h-3 text-[#72282D]" />
            {rating} MMR
          </div>

          <button
            onClick={() => setMobileDrawerOpen(!mobileDrawerOpen)}
            className="p-2 rounded-xl text-[#D1C7BD] hover:text-white hover:bg-[#220D0F]"
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
          <div className="w-72 bg-[#160809] border-r border-[#3A1417] h-full shadow-2xl flex flex-col p-4">
            <div className="flex items-center justify-between pb-4 border-b border-[#3A1417]">
              <div className="flex items-center gap-2">
                <div className="w-8 h-8 rounded-xl bg-gradient-to-br from-[#72282D] to-[#8E3239] text-[#EFEFE1] flex items-center justify-center font-bold">
                  <Swords className="w-4 h-4" />
                </div>
                <span className="font-black text-[#EFEFE1]">{APP_NAME}</span>
              </div>
              <button
                onClick={() => setMobileDrawerOpen(false)}
                className="p-1.5 rounded-lg text-[#AC9C8D] hover:text-white hover:bg-[#220D0F]"
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
                          ? 'bg-[#220D0F] text-[#EFEFE1] font-bold border border-[#5A1E22] shadow-xs'
                          : 'text-[#AC9C8D] hover:bg-[#1C0B0D] hover:text-[#EFEFE1]'
                      }`
                    }
                  >
                    <div className="flex items-center gap-3">
                      <IconComponent className="w-4 h-4" />
                      <span>{item.name}</span>
                    </div>
                    {item.badge && (
                      <span className="text-[10px] font-mono font-bold bg-[#3A1417] text-[#D1C7BD] px-1.5 py-0.5 rounded">
                        {item.badge}
                      </span>
                    )}
                  </NavLink>
                );
              })}
            </nav>

            {/* Mobile Bottom Profile */}
            <div className="pt-4 border-t border-[#3A1417]">
              <div className="flex items-center justify-between mb-3">
                <div className="flex items-center gap-2.5">
                  <div className="w-9 h-9 rounded-xl bg-[#220D0F] border border-[#3A1417] text-[#EFEFE1] flex items-center justify-center font-bold text-xs shadow-sm">
                    {displayName.slice(0, 2).toUpperCase()}
                  </div>
                  <div>
                    <p className="text-xs font-bold text-[#EFEFE1] leading-tight">
                      {displayName}
                    </p>
                    <p className="text-[10px] font-mono text-[#AC9C8D]">Lvl {level} Novice</p>
                  </div>
                </div>
                <button
                  onClick={handleLogout}
                  className="p-2 text-[#AC9C8D] hover:text-rose-400 hover:bg-[#3A1417] rounded-xl transition-colors"
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
      <aside className="hidden md:flex flex-col w-64 bg-[#160809] border-r border-[#3A1417] h-screen sticky top-0 shrink-0">
        {/* Brand */}
        <div className="p-4 border-b border-[#3A1417] flex items-center justify-between">
          <div className="flex items-center gap-3 min-w-0">
            <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-[#72282D] to-[#8E3239] text-[#EFEFE1] flex items-center justify-center shadow-glow-wine shrink-0 font-bold border border-[#A6464E]/40">
              <Swords className="w-5 h-5" />
            </div>
            <div className="min-w-0">
              <span className="text-lg font-black tracking-tight text-[#EFEFE1] block leading-tight truncate">
                {APP_NAME}
              </span>
              <span className="text-[10px] font-mono text-[#AC9C8D] uppercase font-semibold tracking-wider block">
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
                      ? 'bg-[#220D0F] text-[#EFEFE1] font-bold border border-[#5A1E22] shadow-xs'
                      : 'text-[#AC9C8D] hover:bg-[#1E0C0E] hover:text-[#EFEFE1]'
                  }`
                }
              >
                <div className="flex items-center gap-3">
                  <IconComponent className="w-4 h-4" />
                  <span>{item.name}</span>
                </div>
                {item.badge && (
                  <span className="text-[9px] font-mono font-bold bg-[#3A1417] text-[#D1C7BD] px-2 py-0.5 rounded-full border border-[#5A1E22]/60">
                    {item.badge}
                  </span>
                )}
              </NavLink>
            );
          })}
        </nav>

        {/* Mini Player Profile at Bottom */}
        <div className="p-3 border-t border-[#3A1417] bg-[#100607]">
          <div className="p-3 rounded-2xl bg-[#1C0B0D] border border-[#3A1417] shadow-luxury-card">
            <div className="flex items-center justify-between mb-2">
              <div className="flex items-center gap-2.5 min-w-0">
                <div className="w-9 h-9 shrink-0 rounded-xl bg-[#220D0F] border border-[#5A1E22] text-[#EFEFE1] flex items-center justify-center font-bold text-xs shadow-sm">
                  {displayName.slice(0, 2).toUpperCase()}
                </div>
                <div className="min-w-0">
                  <p className="text-xs font-bold text-[#EFEFE1] truncate">
                    {displayName}
                  </p>
                  <p className="text-[10px] font-mono text-[#D1C7BD] font-medium flex items-center gap-1">
                    <Sparkles className="w-2.5 h-2.5 text-[#72282D]" />
                    Lvl {level} • {rating} MMR
                  </p>
                </div>
              </div>

              <button
                onClick={handleLogout}
                className="p-1.5 text-[#8C7A70] hover:text-rose-400 hover:bg-[#3A1417] rounded-lg transition-colors shrink-0"
                title="Log Out"
                aria-label="Log Out"
              >
                <LogOut className="w-4 h-4" />
              </button>
            </div>

            {/* XP progress bar */}
            <div className="space-y-1">
              <div className="flex justify-between text-[9px] font-mono text-[#AC9C8D]">
                <span>XP Progress</span>
                <span className="text-[#D1C7BD] font-semibold">{xpPct}%</span>
              </div>
              <div className="h-1.5 w-full bg-[#3A1417] rounded-full overflow-hidden">
                <div
                  className="h-full bg-gradient-to-r from-[#72282D] via-[#8E3239] to-[#D1C7BD] rounded-full transition-all duration-300 shadow-[0_0_8px_rgba(114,40,45,0.6)]"
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
      <main className="flex-1 overflow-y-auto bg-[#0D0506]">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <Outlet />
        </div>
      </main>
    </div>
  );
};
