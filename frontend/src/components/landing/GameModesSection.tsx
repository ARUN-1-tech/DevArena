import React from 'react';
import { motion } from 'framer-motion';
import { PRIMARY_GAME_MODES, GameModeItem } from '../../data/landingData';
import { Card } from '../ui/Card';
import { ArrowRight } from 'lucide-react';

export const GameModesSection: React.FC = () => {
  const accentGradients = {
    cyan: 'hover:border-cyan-300 hover:bg-gradient-to-b hover:from-white hover:to-cyan-50/40',
    amber: 'hover:border-amber-300 hover:bg-gradient-to-b hover:from-white hover:to-amber-50/40',
    emerald: 'hover:border-emerald-300 hover:bg-gradient-to-b hover:from-white hover:to-emerald-50/40',
    violet: 'hover:border-violet-300 hover:bg-gradient-to-b hover:from-white hover:to-violet-50/40',
  };

  return (
    <section id="modes" className="py-20 max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
      {/* Section Header */}
      <div className="text-center max-w-xl mx-auto mb-14 space-y-2">
        <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-slate-900">
          CHOOSE YOUR BATTLE
        </h2>
        <p className="text-sm sm:text-base text-slate-600">
          Four distinct modes designed for fun, speed, and real-time coding glory.
        </p>
      </div>

      {/* Exactly 4 Playful Mode Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {PRIMARY_GAME_MODES.map((mode: GameModeItem, index) => (
          <motion.div
            key={mode.id}
            initial={{ opacity: 0, y: 15 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.3, delay: index * 0.08 }}
            whileHover={{ y: -6 }}
            className="group cursor-pointer"
          >
            <Card
              className={`p-6 bg-white border-slate-200/90 shadow-sm transition-all flex flex-col justify-between h-full ${
                accentGradients[mode.accent]
              }`}
            >
              <div>
                <motion.div
                  whileHover={{ rotate: [0, -6, 6, 0] }}
                  transition={{ duration: 0.3 }}
                  className="w-12 h-12 rounded-2xl bg-slate-100 flex items-center justify-center text-2xl mb-4 group-hover:scale-110 transition-transform"
                >
                  {mode.icon}
                </motion.div>

                <h3 className="text-lg font-bold text-slate-900 mb-1.5">{mode.title}</h3>
                <p className="text-sm text-slate-600 leading-relaxed">{mode.description}</p>
              </div>

              <div className="pt-6 mt-4 flex items-center text-xs font-mono font-semibold text-slate-700 group-hover:text-cyan-600 transition-colors">
                <span>Play Mode</span>
                <ArrowRight className="w-3.5 h-3.5 ml-1.5 group-hover:translate-x-1.5 transition-transform" />
              </div>
            </Card>
          </motion.div>
        ))}
      </div>
    </section>
  );
};
