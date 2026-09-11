import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { NAV_LINKS, APP_NAME } from '../../data/constants';
import { Swords, Terminal } from 'lucide-react';
import { Button } from '../ui/Button';

export interface NavbarProps {
  backendConnected?: boolean;
}

export const Navbar: React.FC<NavbarProps> = ({ backendConnected = false }) => {
  const location = useLocation();

  return (
    <header className="sticky top-0 z-50 bg-white/80 backdrop-blur-md border-b border-slate-200/80 transition-all">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Brand */}
        <Link to="/" className="flex items-center gap-3 group">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-cyan-600 to-violet-600 flex items-center justify-center text-white shadow-sm group-hover:scale-105 transition-transform">
            <Swords className="w-5 h-5" />
          </div>
          <div>
            <span className="text-lg font-bold text-slate-900 tracking-tight flex items-center gap-1">
              {APP_NAME}
              <span className="text-[10px] font-mono font-semibold px-1.5 py-0.5 bg-cyan-100 text-cyan-800 rounded">
                M01
              </span>
            </span>
            <p className="text-[10px] font-mono text-slate-500 tracking-wide uppercase">
              Code. Compete. Level Up.
            </p>
          </div>
        </Link>

        {/* Navigation Links */}
        <nav className="hidden md:flex items-center gap-1">
          {NAV_LINKS.map((link) => {
            const isActive = location.pathname === link.path;
            return (
              <Link
                key={link.path}
                to={link.path}
                className={`px-3 py-1.5 rounded-lg text-sm font-medium transition-colors flex items-center gap-1.5 ${
                  isActive
                    ? 'bg-slate-100 text-slate-900 font-semibold'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                }`}
              >
                {link.label}
                {link.badge && (
                  <span className="text-[9px] font-mono bg-slate-200/70 text-slate-600 px-1.5 py-0.2 rounded font-semibold">
                    {link.badge}
                  </span>
                )}
              </Link>
            );
          })}
        </nav>

        {/* Right Actions & Health Pulse */}
        <div className="flex items-center gap-3">
          <div className="flex items-center gap-2 px-2.5 py-1 rounded-full border border-slate-200 bg-white/60 text-xs font-mono">
            <span
              className={`w-2 h-2 rounded-full ${
                backendConnected ? 'bg-emerald-500 animate-pulse' : 'bg-amber-400'
              }`}
            />
            <span className="text-slate-600 hidden sm:inline">
              {backendConnected ? 'API Connected' : 'API Standby'}
            </span>
          </div>

          <Button
            size="sm"
            variant="glow"
            leftIcon={<Terminal className="w-4 h-4" />}
            onClick={() => alert('Battle Arena matches will open in Module 06!')}
          >
            Launch Arena
          </Button>
        </div>
      </div>
    </header>
  );
};
