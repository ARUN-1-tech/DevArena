import React from 'react';
import { APP_NAME, APP_MOTTO, APP_VERSION } from '../../data/constants';
import { Terminal, Shield, Zap, Heart } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="border-t border-[#3A1417] bg-[#0D0506]/90 backdrop-blur-md mt-20 text-[#D1C7BD]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div className="md:col-span-2 space-y-3">
            <div className="flex items-center gap-2">
              <span className="font-extrabold text-[#EFEFE1] text-lg tracking-tight">{APP_NAME}</span>
              <span className="text-xs font-mono text-[#D1C7BD] bg-[#220D0F] px-2 py-0.5 rounded-full border border-[#3A1417]">
                {APP_VERSION}
              </span>
            </div>
            <p className="text-sm text-[#AC9C8D] max-w-sm">
              {APP_MOTTO} Real-time 1v1 coding battles, competitive ELO ladder, gamified XP, and interactive skill progression.
            </p>
          </div>

          <div>
            <h4 className="text-xs font-bold uppercase tracking-wider text-[#D1C7BD] mb-3 font-mono">
              Architecture
            </h4>
            <ul className="space-y-2 text-sm text-[#AC9C8D]">
              <li className="flex items-center gap-2">
                <Terminal className="w-3.5 h-3.5 text-[#72282D]" />
                <span>Spring Boot 3 + Java 21</span>
              </li>
              <li className="flex items-center gap-2">
                <Zap className="w-3.5 h-3.5 text-[#72282D]" />
                <span>Redis + STOMP WebSockets</span>
              </li>
              <li className="flex items-center gap-2">
                <Shield className="w-3.5 h-3.5 text-[#72282D]" />
                <span>PostgreSQL + Flyway</span>
              </li>
            </ul>
          </div>

          <div>
            <h4 className="text-xs font-bold uppercase tracking-wider text-[#D1C7BD] mb-3 font-mono">
              Platform Features
            </h4>
            <ul className="space-y-2 text-sm text-[#AC9C8D]">
              <li>Real-time 1v1 Battles & Matchmaking</li>
              <li>Monaco Code Lab & Sandbox</li>
              <li>Skill Tree & Global Leaderboards</li>
              <li>Socratic AI Coding Assistant</li>
            </ul>
          </div>
        </div>

        <div className="mt-8 pt-6 border-t border-[#3A1417] flex flex-col sm:flex-row items-center justify-between text-xs text-[#AC9C8D]/70 font-mono gap-4">
          <p>© 2026 DevArena. Production Engineering Foundation.</p>
          <p className="flex items-center gap-1">
            Built with <Heart className="w-3 h-3 text-[#72282D] fill-[#72282D]" /> for competitive software engineers.
          </p>
        </div>
      </div>
    </footer>
  );
};
