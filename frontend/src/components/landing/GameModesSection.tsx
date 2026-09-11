import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { GAME_MODES, GameMode } from '../../data/landingData';
import { Card } from '../ui/Card';
import { Button } from '../ui/Button';
import { Badge } from '../ui/Badge';
import { Swords, Zap, ShieldAlert, Code, Trophy, Clock, Users, ArrowRight } from 'lucide-react';

export const GameModesSection: React.FC = () => {
  const [selectedMode, setSelectedMode] = useState<string>('ranked-duel');

  const iconMap: Record<string, React.ReactNode> = {
    Swords: <Swords className="w-6 h-6 text-cyan-600" />,
    Zap: <Zap className="w-6 h-6 text-amber-500" />,
    ShieldAlert: <ShieldAlert className="w-6 h-6 text-rose-500" />,
    Code: <Code className="w-6 h-6 text-emerald-600" />,
    Trophy: <Trophy className="w-6 h-6 text-violet-600" />,
  };

  return (
    <section id="modes" className="py-20 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      {/* Section Header */}
      <div className="text-center max-w-2xl mx-auto mb-14 space-y-3">
        <Badge variant="info">COMPETITIVE FORMATS</Badge>
        <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-slate-900">
          CHOOSE YOUR BATTLE
        </h2>
        <p className="text-base text-slate-600">
          Whether you crave high-stakes 1v1 duels, rapid 5-minute sprints, or titanic boss encounters,
          there is an arena configured for your combat style.
        </p>
      </div>

      {/* Game Mode Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {GAME_MODES.map((mode: GameMode) => {
          const isSelected = selectedMode === mode.id;

          return (
            <motion.div
              key={mode.id}
              whileHover={{ y: -4 }}
              transition={{ duration: 0.2 }}
              onClick={() => setSelectedMode(mode.id)}
            >
              <Card
                className={`h-full flex flex-col justify-between transition-all cursor-pointer ${
                  isSelected
                    ? 'border-cyan-500 ring-2 ring-cyan-500/20 shadow-glow-cyan'
                    : 'border-slate-200/90 hover:border-slate-300'
                }`}
              >
                <div>
                  {/* Top Bar: Icon + Players Pill */}
                  <div className="flex items-center justify-between mb-4">
                    <div className="p-3 rounded-xl bg-slate-100/90 border border-slate-200">
                      {iconMap[mode.iconName] || <Swords className="w-6 h-6" />}
                    </div>
                    <span className="text-xs font-mono font-semibold px-2.5 py-1 rounded-full bg-slate-100 text-slate-700 flex items-center gap-1.5">
                      <Users className="w-3 h-3 text-slate-500" />
                      {mode.players}
                    </span>
                  </div>

                  {/* Title & Tagline */}
                  <h3 className="text-lg font-bold text-slate-900">{mode.title}</h3>
                  <p className="text-xs font-mono text-cyan-700 font-semibold mb-2.5">
                    {mode.tagline}
                  </p>
                  <p className="text-sm text-slate-600 leading-relaxed mb-4">
                    {mode.description}
                  </p>
                </div>

                {/* Specs & Footer */}
                <div className="space-y-4 pt-4 border-t border-slate-100">
                  <div className="grid grid-cols-2 gap-2 text-xs font-mono text-slate-500">
                    <span className="flex items-center gap-1.5">
                      <Clock className="w-3.5 h-3.5 text-slate-400" />
                      {mode.duration}
                    </span>
                    <span className="text-right font-semibold text-slate-700">
                      {mode.ratingImpact}
                    </span>
                  </div>

                  {/* Tags */}
                  <div className="flex flex-wrap gap-1.5">
                    {mode.tags.map((tag) => (
                      <span
                        key={tag}
                        className="text-[10px] font-mono px-2 py-0.5 rounded bg-slate-100 text-slate-600"
                      >
                        {tag}
                      </span>
                    ))}
                  </div>

                  <Button
                    variant={isSelected ? 'glow' : 'outline'}
                    size="sm"
                    className="w-full"
                    rightIcon={<ArrowRight className="w-3.5 h-3.5" />}
                  >
                    Select {mode.title}
                  </Button>
                </div>
              </Card>
            </motion.div>
          );
        })}
      </div>
    </section>
  );
};
