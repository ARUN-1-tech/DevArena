import React from 'react';
import { motion, useReducedMotion } from 'framer-motion';
import { PROGRESSION_STEPS, FEATURED_PLAYER } from '../../data/landingData';
import { Card } from '../ui/Card';
import { Shield, Sparkles } from 'lucide-react';

export const ProgressionSection: React.FC = () => {
  const shouldReduceMotion = useReducedMotion();

  return (
    <section id="progression" className="py-20 max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      {/* Section Header */}
      <div className="text-center max-w-xl mx-auto mb-14 space-y-2">
        <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-slate-900">
          YOUR DEVELOPER JOURNEY
        </h2>
        <p className="text-sm sm:text-base text-slate-600">
          Gain XP with every solved challenge and climb through 5 distinguished ranks.
        </p>
      </div>

      {/* Visual Journey Stepper: NOVICE -> DEVELOPER -> ENGINEER -> ARCHITECT -> MASTER */}
      <div className="relative mb-14">
        {/* Connecting Track Line */}
        <div className="hidden sm:block absolute top-1/2 left-8 right-8 -translate-y-1/2 h-1 bg-slate-200 z-0">
          <motion.div
            initial={{ width: 0 }}
            whileInView={{ width: '80%' }}
            viewport={{ once: true }}
            transition={{ duration: 1, ease: 'easeOut' }}
            className="h-full bg-gradient-to-r from-cyan-500 via-violet-500 to-emerald-500"
          />
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-5 gap-4 relative z-10">
          {PROGRESSION_STEPS.map((step, idx) => {
            const isCompleted = idx <= 3; // Arun is Architect (idx 3)
            const isCurrent = idx === 3;

            return (
              <div
                key={step.rank}
                className="flex flex-col items-center text-center p-3 rounded-xl bg-white border border-slate-200/80 shadow-sm"
              >
                <div
                  className={`w-10 h-10 rounded-full flex items-center justify-center font-mono font-bold text-xs mb-2 transition-transform ${
                    isCurrent
                      ? 'bg-cyan-600 text-white ring-4 ring-cyan-100 scale-110'
                      : isCompleted
                      ? 'bg-emerald-50 text-emerald-700 border border-emerald-300'
                      : 'bg-slate-100 text-slate-400'
                  }`}
                >
                  {idx + 1}
                </div>
                <span className="text-xs font-bold text-slate-900">{step.rank}</span>
                <span className="text-[10px] font-mono text-slate-500">{step.rating} MMR</span>
              </div>
            );
          })}
        </div>
      </div>

      {/* ONE Featured Friendly Player Card */}
      <Card className="p-6 sm:p-8 bg-white border-slate-200 shadow-md max-w-lg mx-auto">
        <div className="flex items-center justify-between pb-4 border-b border-slate-100">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-cyan-500 to-violet-600 text-white font-bold text-base flex items-center justify-center shadow-sm">
              AR
            </div>
            <div>
              <h3 className="text-lg font-bold text-slate-900">{FEATURED_PLAYER.name}</h3>
              <p className="text-xs font-mono text-cyan-600 font-semibold flex items-center gap-1">
                <Sparkles className="w-3.5 h-3.5" /> Level {FEATURED_PLAYER.level} · {FEATURED_PLAYER.tier}
              </p>
            </div>
          </div>

          <div className="text-right">
            <span className="text-[10px] font-mono uppercase tracking-wider text-slate-400 block">Rating</span>
            <span className="text-sm font-mono font-extrabold text-slate-900 flex items-center gap-1">
              <Shield className="w-4 h-4 text-cyan-600" />
              {FEATURED_PLAYER.rating} ELO
            </span>
          </div>
        </div>

        {/* Animated XP Progress Bar */}
        <div className="mt-5 space-y-2">
          <div className="flex justify-between text-xs font-mono">
            <span className="text-slate-600">XP Progress</span>
            <span className="font-bold text-slate-900">
              {FEATURED_PLAYER.currentXp.toLocaleString()} / {FEATURED_PLAYER.targetXp.toLocaleString()} XP
            </span>
          </div>

          <div className="w-full h-3 bg-slate-100 rounded-full overflow-hidden p-0.5">
            <motion.div
              initial={{ width: 0 }}
              whileInView={{ width: '84.5%' }}
              viewport={{ once: true }}
              transition={{ duration: shouldReduceMotion ? 0 : 1, ease: 'easeOut' }}
              className="h-full bg-gradient-to-r from-cyan-500 to-violet-500 rounded-full"
            />
          </div>

          <div className="flex justify-between items-center pt-2 text-xs font-mono text-slate-500">
            <span>Win Rate: <strong className="text-emerald-600 font-bold">{FEATURED_PLAYER.winRate}%</strong></span>
            <span>+1,550 XP to Level 29</span>
          </div>
        </div>
      </Card>
    </section>
  );
};
