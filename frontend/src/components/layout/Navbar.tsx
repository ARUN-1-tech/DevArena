import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { APP_NAME } from '../../data/constants';
import { Swords, Menu, X } from 'lucide-react';
import { Button } from '../ui/Button';

export interface NavbarProps {
  backendConnected?: boolean;
}

export const Navbar: React.FC<NavbarProps> = ({ backendConnected = false }) => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const navLinks = [
    { label: 'Arena', href: '#arena' },
    { label: 'Challenges', href: '#modes' },
    { label: 'Leaderboard', href: '#leaderboard' },
    { label: 'About', href: '#progression' },
  ];

  const handleScrollTo = (e: React.MouseEvent<HTMLAnchorElement>, href: string) => {
    e.preventDefault();
    setMobileMenuOpen(false);
    const el = document.getElementById(href.substring(1));
    el?.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <header className="sticky top-0 z-50 bg-white/90 backdrop-blur-md border-b border-slate-200/80 transition-all">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Brand */}
        <Link to="/" className="flex items-center gap-2.5 group">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-cyan-600 to-violet-600 flex items-center justify-center text-white shadow-sm group-hover:scale-105 transition-transform">
            <Swords className="w-5 h-5" />
          </div>
          <span className="text-lg font-bold text-slate-900 tracking-tight">
            {APP_NAME}
          </span>
        </Link>

        {/* Desktop Navigation */}
        <nav className="hidden md:flex items-center gap-6">
          {navLinks.map((link) => (
            <a
              key={link.label}
              href={link.href}
              onClick={(e) => handleScrollTo(e, link.href)}
              className="text-sm font-medium text-slate-600 hover:text-slate-950 transition-colors"
            >
              {link.label}
            </a>
          ))}
        </nav>

        {/* Right CTA & Mobile Toggle */}
        <div className="flex items-center gap-3">
          <div
            className={`w-2 h-2 rounded-full hidden sm:block ${
              backendConnected ? 'bg-emerald-500' : 'bg-slate-300'
            }`}
            title={backendConnected ? 'API Connected' : 'API Standby'}
          />

          <Button
            size="sm"
            variant="glow"
            onClick={() => {
              const el = document.getElementById('modes');
              el?.scrollIntoView({ behavior: 'smooth' });
            }}
          >
            ENTER ARENA
          </Button>

          {/* Mobile hamburger button */}
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="md:hidden p-2 rounded-lg text-slate-600 hover:text-slate-900 hover:bg-slate-100"
            aria-label="Toggle Navigation Menu"
          >
            {mobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
          </button>
        </div>
      </div>

      {/* Mobile Menu Dropdown */}
      {mobileMenuOpen && (
        <div className="md:hidden bg-white border-b border-slate-200 px-4 py-3 space-y-2">
          {navLinks.map((link) => (
            <a
              key={link.label}
              href={link.href}
              onClick={(e) => handleScrollTo(e, link.href)}
              className="block py-2 text-sm font-medium text-slate-700 hover:text-cyan-600"
            >
              {link.label}
            </a>
          ))}
        </div>
      )}
    </header>
  );
};
