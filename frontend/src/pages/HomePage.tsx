import React from 'react';
import { MotionContainer } from '../components/animation/MotionContainer';
import { GlowBadge } from '../components/animation/GlowBadge';
import { Button } from '../components/ui/Button';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { PlayerBadge } from '../components/player/PlayerBadge';
import { ChallengeCardStub } from '../components/challenge/ChallengeCardStub';
import { MODULE_MILESTONES } from '../data/constants';
import { useSystemHealth } from '../hooks/useSystemHealth';
import {
  Swords,
  Trophy,
  Cpu,
  Layers,
  CheckCircle2,
  Clock,
  RefreshCw,
  Server,
} from 'lucide-react';

export const HomePage: React.FC = () => {
  const { health, isConnected, isLoading, refetch, error } = useSystemHealth();

  return (
    <div className="space-y-16">
      {/* Hero Section */}
      <MotionContainer className="text-center max-w-3xl mx-auto pt-6 pb-2 space-y-6">
        <div className="flex justify-center">
          <GlowBadge label="MODULE 01 // ENGINEERING FOUNDATION" glowColor="cyan" />
        </div>

        <h1 className="text-4xl sm:text-6xl font-extrabold tracking-tight text-slate-900 leading-tight">
          Code. Compete.{' '}
          <span className="bg-gradient-to-r from-cyan-600 via-violet-600 to-emerald-500 bg-clip-text text-transparent">
            Level Up.
          </span>
        </h1>

        <p className="text-base sm:text-lg text-slate-600 leading-relaxed max-w-2xl mx-auto">
          The next-generation competitive programming platform. Duel in real-time 1v1 coding battles,
          earn XP, develop skills on a dynamic tech radar, and climb the competitive ladder.
        </p>

        <div className="flex flex-wrap items-center justify-center gap-3 pt-2">
          <Button
            variant="glow"
            size="lg"
            leftIcon={<Swords className="w-5 h-5" />}
            onClick={() => {
              const el = document.getElementById('roadmap');
              el?.scrollIntoView({ behavior: 'smooth' });
            }}
          >
            Explore Roadmap
          </Button>

          <Button
            variant="outline"
            size="lg"
            leftIcon={<Server className="w-5 h-5" />}
            onClick={() => {
              const el = document.getElementById('diagnostics');
              el?.scrollIntoView({ behavior: 'smooth' });
            }}
          >
            System Diagnostics
          </Button>
        </div>
      </MotionContainer>

      {/* Interactive System Diagnostics */}
      <MotionContainer delay={0.1} id="diagnostics">
        <Card className="border-cyan-200/80 shadow-glow-cyan/20">
          <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 border-b border-slate-100 pb-4 mb-5">
            <div className="flex items-center gap-3">
              <div className="p-2.5 rounded-lg bg-cyan-50 text-cyan-600 border border-cyan-200">
                <Cpu className="w-5 h-5" />
              </div>
              <div>
                <h3 className="font-semibold text-slate-900">Module 01 System Diagnostics</h3>
                <p className="text-xs text-slate-500 font-mono">
                  Real-time connectivity with Spring Boot backend (/api/v1/health)
                </p>
              </div>
            </div>

            <Button
              variant="outline"
              size="sm"
              isLoading={isLoading}
              leftIcon={<RefreshCw className="w-3.5 h-3.5" />}
              onClick={() => refetch()}
            >
              Check Health
            </Button>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4 text-sm font-mono">
            <div className="p-3.5 rounded-lg bg-slate-50 border border-slate-200">
              <span className="text-xs text-slate-500 block mb-1">Backend API Status</span>
              <div className="flex items-center gap-2">
                <span
                  className={`w-2.5 h-2.5 rounded-full ${
                    isConnected ? 'bg-emerald-500 animate-pulse' : 'bg-amber-400'
                  }`}
                />
                <span className="font-semibold text-slate-800">
                  {isConnected ? health?.status : 'STANDBY'}
                </span>
              </div>
            </div>

            <div className="p-3.5 rounded-lg bg-slate-50 border border-slate-200">
              <span className="text-xs text-slate-500 block mb-1">Active Architecture</span>
              <span className="font-semibold text-cyan-700">
                {health?.module || 'MODULE_01 FOUNDATION'}
              </span>
            </div>

            <div className="p-3.5 rounded-lg bg-slate-50 border border-slate-200">
              <span className="text-xs text-slate-500 block mb-1">Database & Migrations</span>
              <span className="font-semibold text-emerald-700">PostgreSQL + Flyway V1</span>
            </div>

            <div className="p-3.5 rounded-lg bg-slate-50 border border-slate-200">
              <span className="text-xs text-slate-500 block mb-1">Real-Time & Cache</span>
              <span className="font-semibold text-violet-700">Redis + WebSocket /ws</span>
            </div>
          </div>

          {error && (
            <div className="mt-4 p-3 rounded-lg bg-amber-50 border border-amber-200 text-xs text-amber-800 font-mono">
              <strong>Info:</strong> {error} — Run{' '}
              <code className="bg-amber-100 px-1 py-0.5 rounded">.\mvnw.cmd spring-boot:run</code> in{' '}
              <code className="bg-amber-100 px-1 py-0.5 rounded">backend/</code> to connect.
            </div>
          )}
        </Card>
      </MotionContainer>

      {/* Component & UI Architecture Preview */}
      <MotionContainer delay={0.2} className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-xl font-bold text-slate-900 tracking-tight">
              Design & Component Foundation
            </h2>
            <p className="text-xs text-slate-500">
              Modular UI primitives designed for seamless visual integration with Bolt.new
            </p>
          </div>
          <Badge variant="purple">Bolt.new Compatible</Badge>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <ChallengeCardStub
            title="Two Sum Problem: Arena Edition"
            category="Algorithms & Data Structures"
            difficulty="EASY"
            xpReward={150}
          />
          <ChallengeCardStub
            title="Concurrent Rate Limiter"
            category="System Design & Concurrency"
            difficulty="HARD"
            xpReward={450}
          />
          <div className="space-y-4">
            <h4 className="text-xs font-semibold text-slate-500 uppercase tracking-wider font-mono">
              Player Identity Primitives
            </h4>
            <PlayerBadge
              player={{
                id: '1',
                username: 'ShadowCoder',
                rating: 1840,
                rank: 'DIAMOND',
                level: 24,
                xp: 14250,
              }}
            />
          </div>
        </div>
      </MotionContainer>

      {/* Modular Engineering Roadmap */}
      <MotionContainer delay={0.3} id="roadmap" className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-xl font-bold text-slate-900 tracking-tight">
              DevArena Engineering Roadmap
            </h2>
            <p className="text-xs text-slate-500">
              Structured progressive modules leading to full production deployment
            </p>
          </div>
          <Badge variant="success">Module 01 Complete</Badge>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {MODULE_MILESTONES.map((module) => {
            const isDone = module.status === 'COMPLETED';
            return (
              <Card
                key={module.id}
                className={isDone ? 'border-emerald-300 bg-emerald-50/30' : 'border-slate-200'}
              >
                <div className="flex items-start justify-between gap-3">
                  <div className="flex items-center gap-2">
                    <span className="font-mono text-xs font-bold px-2 py-0.5 rounded bg-slate-100 text-slate-800 border border-slate-200">
                      {module.id}
                    </span>
                    <h3 className="font-semibold text-slate-900 text-sm">{module.name}</h3>
                  </div>

                  {isDone ? (
                    <span className="flex items-center gap-1 text-xs font-mono text-emerald-600 font-semibold">
                      <CheckCircle2 className="w-4 h-4" /> Ready
                    </span>
                  ) : (
                    <span className="flex items-center gap-1 text-xs font-mono text-slate-400">
                      <Clock className="w-3.5 h-3.5" /> Next
                    </span>
                  )}
                </div>
                <p className="text-xs text-slate-600 mt-2 font-mono">{module.desc}</p>
              </Card>
            );
          })}
        </div>
      </MotionContainer>

      {/* Tech Pillars */}
      <MotionContainer delay={0.4} className="grid grid-cols-1 md:grid-cols-3 gap-6 pt-4">
        <Card hoverEffect glow="cyan">
          <div className="w-10 h-10 rounded-lg bg-cyan-100 text-cyan-700 flex items-center justify-center mb-4">
            <Swords className="w-5 h-5" />
          </div>
          <h3 className="font-bold text-slate-900 mb-2">Real-Time Duels</h3>
          <p className="text-xs text-slate-600 leading-relaxed">
            Engineered with Spring WebSocket STOMP and Redis state management for millisecond-level synchronization during 1v1 coding duels.
          </p>
        </Card>

        <Card hoverEffect glow="violet">
          <div className="w-10 h-10 rounded-lg bg-violet-100 text-violet-700 flex items-center justify-center mb-4">
            <Trophy className="w-5 h-5" />
          </div>
          <h3 className="font-bold text-slate-900 mb-2">Gamified Ranking</h3>
          <p className="text-xs text-slate-600 leading-relaxed">
            MMR matchmaking engine, ELO rating updates, seasonal ladders, and achievement unlock trees driving long-term developer engagement.
          </p>
        </Card>

        <Card hoverEffect glow="emerald">
          <div className="w-10 h-10 rounded-lg bg-emerald-100 text-emerald-700 flex items-center justify-center mb-4">
            <Layers className="w-5 h-5" />
          </div>
          <h3 className="font-bold text-slate-900 mb-2">Production Architecture</h3>
          <p className="text-xs text-slate-600 leading-relaxed">
            Strict domain boundaries, unified API envelopes, centralized RFC-7807 error schema, and containerized PostgreSQL + Redis.
          </p>
        </Card>
      </MotionContainer>
    </div>
  );
};
