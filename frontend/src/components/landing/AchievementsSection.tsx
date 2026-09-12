import React from 'react';
import { motion } from 'framer-motion';
import { ACHIEVEMENTS_MEDALS } from '../../data/landingData';
import { Card } from '../ui/Card';

export const AchievementsSection: React.FC = () => {
  return (
    <section id="achievements" className="py-20 max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
      {/* Section Header */}
      <div className="text-center max-w-xl mx-auto mb-14 space-y-2">
        <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-slate-900">
          EARN GAME MEDALS
        </h2>
        <p className="text-sm sm:text-base text-slate-600">
          Unlock rewards as you hit milestones and win battles.
        </p>
      </div>

      {/* Exactly 4 Collectible Game Medal Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {ACHIEVEMENTS_MEDALS.map((medal, index) => (
          <motion.div
            key={medal.id}
            initial={{ opacity: 0, y: 15 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.3, delay: index * 0.08 }}
            whileHover={{ y: -6 }}
            className="group cursor-pointer"
          >
            <Card className="p-6 bg-white border-slate-200/90 shadow-sm hover:shadow-md text-center flex flex-col items-center justify-between h-full space-y-3">
              {/* Medal Emblem with playful hover rotation */}
              <motion.div
                whileHover={{ rotate: [-4, 8, -4, 0], scale: 1.1 }}
                transition={{ duration: 0.4 }}
                className={`w-14 h-14 rounded-2xl flex items-center justify-center text-3xl border shadow-sm ${medal.color} ${medal.border}`}
              >
                {medal.icon}
              </motion.div>

              <div>
                <h3 className="text-base font-bold text-slate-900 group-hover:text-cyan-700 transition-colors">
                  {medal.title}
                </h3>
                <p className="text-xs text-slate-500 mt-1 leading-relaxed">
                  {medal.description}
                </p>
              </div>

              <span className="text-[10px] font-mono uppercase tracking-wider font-semibold text-slate-400">
                Collectible
              </span>
            </Card>
          </motion.div>
        ))}
      </div>
    </section>
  );
};
