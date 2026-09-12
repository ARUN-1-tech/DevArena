import React from 'react';
import { APP_NAME, APP_MOTTO, APP_VERSION } from '../../data/constants';
import { Terminal, Shield, Zap, Heart } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="border-t border-[#212328] bg-[#0B0C0E]/90 backdrop-blur-md mt-20 text-[#B2B6BD]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div className="md:col-span-2 space-y-3">
            <div className="flex items-center gap-2">
              <span className="font-extrabold text-white text-lg tracking-tight">{APP_NAME}</span>
              <span className="text-xs font-mono text-[#D4D7DC] bg-[#1A1B1F] px-2 py-0.5 rounded-full border border-[#27292F]">
                {APP_VERSION}
              </span>
            </div>
            <p className="text-sm text-[#868A94] max-w-sm">
              {APP_MOTTO} Real-time 1v1 coding battles, competitive ELO ladder, gamified XP, and interactive skill progression.
            </p>
          </div>

          <div>
            <h4 className="text-xs font-bold uppercase tracking-wider text-[#D4D7DC] mb-3 font-mono">
              Architecture
            </h4>
            <ul className="space-y-2 text-sm text-[#868A94]">
              <li className="flex items-center gap-2">
                <Terminal className="w-3.5 h-3.5 text-white" />
                <span>Spring Boot 3 + Java 21</span>
              </li>
              <li className="flex items-center gap-2">
                <Zap className="w-3.5 h-3.5 text-white" />
                <span>Redis + STOMP WebSockets</span>
              </li>
              <li className="flex items-center gap-2">
                <Shield className="w-3.5 h-3.5 text-white" />
                <span>PostgreSQL + Flyway</span>
              </li>
            </ul>
          </div>

          <div>
            <h4 className="text-xs font-bold uppercase tracking-wider text-[#D4D7DC] mb-3 font-mono">
              Platform Features
            </h4>
            <ul className="space-y-2 text-sm text-[#868A94]">
              <li>Real-time 1v1 Battles & Matchmaking</li>
              <li>Monaco Code Lab & Sandbox</li>
              <li>Skill Tree & Global Leaderboards</li>
              <li>Socratic AI Coding Assistant</li>
            </ul>
          </div>
        </div>

        <div className="mt-8 pt-6 border-t border-[#212328] flex flex-col sm:flex-row items-center justify-between text-xs text-[#5C6069] font-mono gap-4">
          <p>© 2026 DevArena. Production Engineering Foundation.</p>
          <p className="flex items-center gap-1">
            Built with <Heart className="w-3 h-3 text-[#D4D7DC] fill-[#D4D7DC]" /> for competitive software engineers.
          </p>
        </div>
      </div>
    </footer>
  );
};
