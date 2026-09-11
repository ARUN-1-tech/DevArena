import React from 'react';
import { APP_NAME, APP_MOTTO, APP_VERSION } from '../../data/constants';
import { Terminal, Shield, Zap, Heart } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="border-t border-slate-200 bg-white/70 backdrop-blur-sm mt-20">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div className="md:col-span-2 space-y-3">
            <div className="flex items-center gap-2">
              <span className="font-bold text-slate-900 text-lg">{APP_NAME}</span>
              <span className="text-xs font-mono text-cyan-600 bg-cyan-50 px-2 py-0.5 rounded border border-cyan-200">
                {APP_VERSION}
              </span>
            </div>
            <p className="text-sm text-slate-600 max-w-sm">
              {APP_MOTTO} Real-time 1v1 coding battles, competitive ELO ladder, gamified XP, and interactive skill progression.
            </p>
          </div>

          <div>
            <h4 className="text-xs font-semibold uppercase tracking-wider text-slate-900 mb-3 font-mono">
              Architecture
            </h4>
            <ul className="space-y-2 text-sm text-slate-600">
              <li className="flex items-center gap-2">
                <Terminal className="w-3.5 h-3.5 text-cyan-600" />
                <span>Spring Boot 3 + Java 21</span>
              </li>
              <li className="flex items-center gap-2">
                <Zap className="w-3.5 h-3.5 text-amber-500" />
                <span>Redis + STOMP WebSockets</span>
              </li>
              <li className="flex items-center gap-2">
                <Shield className="w-3.5 h-3.5 text-emerald-600" />
                <span>PostgreSQL + Flyway</span>
              </li>
            </ul>
          </div>

          <div>
            <h4 className="text-xs font-semibold uppercase tracking-wider text-slate-900 mb-3 font-mono">
              Engineering Specs
            </h4>
            <ul className="space-y-2 text-sm text-slate-600">
              <li>Module 01: Scaffolding Complete</li>
              <li>REST Envelope & Error Format</li>
              <li>Docker & Compose Ready</li>
              <li>Design Compatible (Bolt.new)</li>
            </ul>
          </div>
        </div>

        <div className="mt-8 pt-6 border-t border-slate-200/80 flex flex-col sm:flex-row items-center justify-between text-xs text-slate-500 font-mono gap-4">
          <p>© 2026 DevArena. Production Engineering Foundation.</p>
          <p className="flex items-center gap-1">
            Built with <Heart className="w-3 h-3 text-rose-500 fill-rose-500" /> for competitive software engineers.
          </p>
        </div>
      </div>
    </footer>
  );
};
