import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { RANK_TIERS, RankTierInfo } from '../../data/landingData';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Shield, Zap, Sparkles, Check, ChevronRight } from 'lucide-react';

export const ProgressionSection: React.FC = () => {
  const [selectedRank, setSelectedRank] = useState<string>('DIAMOND');

  const currentTier =
    RANK_TIERS.find((t) => t.rank === selectedRank) || RANK_TIERS[5]; // Diamond

  return (
    <section id="progression" className="py-20 bg-slate-100/60 border-y border-slate-200/80">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Section Header */}
        <div className="text-center max-w-2xl mx-auto mb-14 space-y-3">
          <Badge variant="purple">RANKED LADDER & XP</Badge>
          <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-slate-900">
            LEVEL UP YOUR DEVELOPER DNA
          </h2>
          <p className="text-base text-slate-600">
            Every test passed, duel won, and challenge cracked grants XP. Progress from Novice to
            Grandmaster and claim your place in the global developer hierarchy.
          </p>
        </div>

        {/* Competitive Rank Tiers Stepper */}
        <div className="mb-10">
          <div className="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-8 gap-2">
            {RANK_TIERS.map((tier: RankTierInfo) => {
              const isSelected = selectedRank === tier.rank;

              return (
                <button
                  key={tier.rank}
                  onClick={() => setSelectedRank(tier.rank)}
                  className={`p-3 rounded-xl border text-center transition-all flex flex-col items-center justify-between ${
                    isSelected
                      ? 'bg-white border-cyan-500 shadow-md ring-2 ring-cyan-500/20 scale-105'
                      : 'bg-white/70 border-slate-200 hover:bg-white hover:border-slate-300'
                  }`}
                >
                  <div
                    className={`w-8 h-8 rounded-lg ${tier.bgLight} border ${tier.borderColor} flex items-center justify-center mb-2`}
                  >
                    <Shield className={`w-4 h-4 ${tier.color}`} />
                  </div>
                  <div>
                    <p className="text-xs font-bold text-slate-900">{tier.title}</p>
                    <p className="text-[10px] font-mono text-slate-500 mt-0.5">
                      {tier.minRating}+ MMR
                    </p>
                  </div>
                </button>
              );
            })}
          </div>
        </div>

        {/* Deep Dive on Selected Rank & Level Showcase */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-stretch">
          {/* Selected Tier Details */}
          <Card className="lg:col-span-7 bg-white p-6 sm:p-8 flex flex-col justify-between">
            <div>
              <div className="flex flex-wrap items-center justify-between gap-3 mb-4">
                <div className="flex items-center gap-3">
                  <div
                    className={`w-12 h-12 rounded-xl ${currentTier.bgLight} border ${currentTier.borderColor} flex items-center justify-center shadow-sm`}
                  >
                    <Shield className={`w-6 h-6 ${currentTier.color}`} />
                  </div>
                  <div>
                    <h3 className="text-xl font-bold text-slate-900">
                      {currentTier.title} Tier Division
                    </h3>
                    <p className="text-xs font-mono text-cyan-600 font-semibold">
                      Rating Range: {currentTier.minRating} - {currentTier.maxRating} MMR
                    </p>
                  </div>
                </div>

                <Badge variant="purple" size="sm">
                  {currentTier.playerPercentile} of Developers
                </Badge>
              </div>

              <div className="space-y-4 mt-6">
                <h4 className="text-xs font-mono font-bold uppercase tracking-wider text-slate-700">
                  Division Perks & Unlocks
                </h4>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  {currentTier.perks.map((perk, i) => (
                    <div
                      key={i}
                      className="flex items-start gap-2.5 p-3 rounded-lg bg-slate-50 border border-slate-200 text-xs text-slate-700 font-medium"
                    >
                      <Check className="w-4 h-4 text-emerald-600 mt-0.5 shrink-0" />
                      <span>{perk}</span>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            <div className="pt-6 mt-6 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500 font-mono">
              <span>Season 1 Closes in 42 Days</span>
              <span className="text-cyan-700 font-semibold flex items-center gap-1 cursor-pointer">
                View Division Standings <ChevronRight className="w-3.5 h-3.5" />
              </span>
            </div>
          </Card>

          {/* Interactive Player XP & Level Simulator Widget */}
          <Card className="lg:col-span-5 bg-gradient-to-br from-slate-900 to-slate-950 text-white p-6 sm:p-8 flex flex-col justify-between">
            <div className="space-y-5">
              <div className="flex items-center justify-between border-b border-slate-800 pb-4">
                <div className="flex items-center gap-3">
                  <div className="w-11 h-11 rounded-xl bg-gradient-to-tr from-cyan-500 to-violet-600 flex items-center justify-center text-white font-extrabold text-base shadow-sm">
                    AR
                  </div>
                  <div>
                    <h4 className="text-base font-bold text-white">Arun</h4>
                    <span className="text-xs font-mono text-cyan-400">
                      Title: Algorithm Architect
                    </span>
                  </div>
                </div>
                <div className="text-right">
                  <span className="text-xs font-mono text-slate-400 block">Current Rank</span>
                  <span className="text-xs font-mono font-bold text-amber-400">DIAMOND I</span>
                </div>
              </div>

              {/* Level & XP Gauge */}
              <div className="space-y-2">
                <div className="flex justify-between items-baseline text-xs font-mono">
                  <span className="text-slate-300 font-semibold flex items-center gap-1.5">
                    <Sparkles className="w-3.5 h-3.5 text-cyan-400" />
                    Level 28
                  </span>
                  <span className="text-cyan-400 font-bold">8,450 / 10,000 XP (84.5%)</span>
                </div>

                <div className="w-full h-3 bg-slate-800 rounded-full overflow-hidden p-0.5 border border-slate-700">
                  <motion.div
                    initial={{ width: 0 }}
                    whileInView={{ width: '84.5%' }}
                    transition={{ duration: 1, ease: 'easeOut' }}
                    className="h-full bg-gradient-to-r from-cyan-500 via-violet-500 to-emerald-400 rounded-full"
                  />
                </div>
                <p className="text-[11px] font-mono text-slate-400 text-right">
                  +1,550 XP needed for Level 29
                </p>
              </div>

              {/* Combat Stats Grid */}
              <div className="grid grid-cols-2 gap-3 pt-2 text-xs font-mono">
                <div className="p-3 rounded-lg bg-slate-800/80 border border-slate-700/80">
                  <span className="text-slate-400 text-[11px] block">Duel Win Rate</span>
                  <span className="text-emerald-400 text-base font-bold">72.8%</span>
                  <span className="text-[10px] text-slate-500 block">198 Wins / 74 Losses</span>
                </div>

                <div className="p-3 rounded-lg bg-slate-800/80 border border-slate-700/80">
                  <span className="text-slate-400 text-[11px] block">Current Streak</span>
                  <span className="text-amber-400 text-base font-bold">5 Wins</span>
                  <span className="text-[10px] text-slate-500 block">Flame Aura Active</span>
                </div>
              </div>
            </div>

            <div className="pt-4 mt-4 border-t border-slate-800 flex items-center justify-between text-xs text-slate-400 font-mono">
              <span className="flex items-center gap-1.5 text-cyan-400">
                <Zap className="w-3.5 h-3.5 text-cyan-400" />
                Active XP Multiplier: 1.5x
              </span>
              <span>1842 ELO</span>
            </div>
          </Card>
        </div>
      </div>
    </section>
  );
};
