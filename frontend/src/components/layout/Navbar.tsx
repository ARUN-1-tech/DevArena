import React from 'react';
import { Link } from 'react-router-dom';
import { NAV_LINKS, APP_NAME } from '../../data/constants';
import { Swords, Terminal } from 'lucide-react';
import { Button } from '../ui/Button';

export interface NavbarProps {
  backendConnected?: boolean;
}

export const Navbar: React.FC<NavbarProps> = ({ backendConnected = false }) => {
  const handleScrollTo = (e: React.MouseEvent<HTMLAnchorElement>, path: string) => {
    if (path.startsWith('#')) {
      e.preventDefault();
      const el = document.getElementById(path.substring(1));
      el?.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <header className="sticky top-0 z-50 bg-white/85 backdrop-blur-md border-b border-slate-200/80 transition-all">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Brand */}
        <Link to="/" className="flex items-center gap-3 group">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-cyan-600 to-violet-600 flex items-center justify-center text-white shadow-sm group-hover:scale-105 transition-transform">
            <Swords className="w-5 h-5" />
          </div>
          <div>
            <span className="text-lg font-bold text-slate-900 tracking-tight flex items-center gap-1.5">
              {APP_NAME}
              <span className="text-[10px] font-mono font-bold px-2 py-0.5 bg-cyan-100 text-cyan-800 rounded-full border border-cyan-200">
                M02
              </span>
            </span>
            <p className="text-[10px] font-mono text-slate-500 tracking-wide uppercase">
              Code. Compete. Level Up.
            </p>
          </div>
        </Link>

        {/* Navigation Links */}
        <nav className="hidden lg:flex items-center gap-1">
          {NAV_LINKS.map((link) => (
            <a
              key={link.path}
              href={link.path}
              onClick={(e) => handleScrollTo(e, link.path)}
              className="px-3 py-1.5 rounded-lg text-xs font-mono font-semibold uppercase tracking-wider text-slate-600 hover:text-slate-950 hover:bg-slate-100/80 transition-colors"
            >
              {link.label}
            </a>
          ))}
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
            onClick={() => {
              const el = document.getElementById('modes');
              el?.scrollIntoView({ behavior: 'smooth' });
            }}
          >
            Launch Arena
          </Button>
        </div>
      </div>
    </header>
  );
};
