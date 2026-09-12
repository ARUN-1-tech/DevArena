import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { APP_NAME } from '../../data/constants';
import { Swords, Menu, X } from 'lucide-react';
import { Button } from '../ui/Button';
import { useAuth } from '../../contexts/AuthContext';

export interface NavbarProps {
  backendConnected?: boolean;
}

export const Navbar: React.FC<NavbarProps> = ({ backendConnected = false }) => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

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

  const handleArenaAction = () => {
    setMobileMenuOpen(false);
    if (isAuthenticated) {
      navigate('/home');
    } else {
      navigate('/login');
    }
  };

  return (
    <header className="sticky top-0 z-50 bg-[#0B0C0E]/85 backdrop-blur-xl border-b border-[#212328] transition-all">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Brand */}
        <Link to="/" className="flex items-center gap-2.5 group">
          <div className="w-9 h-9 rounded-xl bg-white text-[#0B0C0E] flex items-center justify-center shadow-glow-white group-hover:scale-105 transition-transform font-bold">
            <Swords className="w-4 h-4" />
          </div>
          <span className="text-lg font-black text-white tracking-tight">
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
              className="text-sm font-medium text-[#B2B6BD] hover:text-white transition-colors"
            >
              {link.label}
            </a>
          ))}
        </nav>

        {/* Right CTA & Mobile Toggle */}
        <div className="flex items-center gap-3">
          <div
            className={`w-2 h-2 rounded-full hidden sm:block ${
              backendConnected ? 'bg-emerald-400 shadow-[0_0_8px_rgba(52,211,153,0.6)]' : 'bg-[#3E4148]'
            }`}
            title={backendConnected ? 'API Connected' : 'API Standby'}
          />

          {!isAuthenticated && (
            <Link
              to="/login"
              className="hidden sm:inline-block text-xs font-semibold text-[#D4D7DC] hover:text-white transition-colors px-2 py-1"
            >
              Sign In
            </Link>
          )}

          <Button
            size="sm"
            variant="primary"
            onClick={handleArenaAction}
          >
            {isAuthenticated ? 'GO TO ARENA' : 'ENTER ARENA'}
          </Button>

          {/* Mobile hamburger button */}
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="md:hidden p-2 rounded-xl text-[#B2B6BD] hover:text-white hover:bg-[#1A1B1F]"
            aria-label="Toggle Navigation Menu"
          >
            {mobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
          </button>
        </div>
      </div>

      {/* Mobile Menu Dropdown */}
      {mobileMenuOpen && (
        <div className="md:hidden bg-[#111215] border-b border-[#212328] px-4 py-3 space-y-2">
          {navLinks.map((link) => (
            <a
              key={link.label}
              href={link.href}
              onClick={(e) => handleScrollTo(e, link.href)}
              className="block py-2 text-sm font-medium text-[#D4D7DC] hover:text-white"
            >
              {link.label}
            </a>
          ))}
          <div className="pt-2 border-t border-[#212328] flex flex-col gap-2">
            {!isAuthenticated ? (
              <>
                <Link
                  to="/login"
                  onClick={() => setMobileMenuOpen(false)}
                  className="py-2 text-sm font-medium text-[#D4D7DC] hover:text-white"
                >
                  Sign In
                </Link>
                <Link
                  to="/register"
                  onClick={() => setMobileMenuOpen(false)}
                  className="py-2 text-sm font-semibold text-white"
                >
                  Create Account
                </Link>
              </>
            ) : (
              <button
                onClick={handleArenaAction}
                className="w-full text-left py-2 text-sm font-semibold text-white"
              >
                Launch DevArena HQ
              </button>
            )}
          </div>
        </div>
      )}
    </header>
  );
};
