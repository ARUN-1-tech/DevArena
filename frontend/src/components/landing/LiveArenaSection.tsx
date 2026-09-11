import React from 'react';
import { motion } from 'framer-motion';
import { LIVE_ACTIVITIES } from '../../data/landingData';
import { Card } from '../ui/Card';

export const LiveArenaSection: React.FC = () => {
  return (
    <section id="arena" className="py-20 max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
      {/* Section Header */}
      <div className="text-center max-w-xl mx-auto mb-12 space-y-2">
        <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-slate-900">
          THE ARENA IS LIVE
        </h2>
        <p className="text-sm sm:text-base text-slate-600">
          Developers are battling right now.
        </p>

        {/* Friendly Single Counter */}
        <div className="pt-2">
          <span className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-emerald-50 border border-emerald-200 text-xs font-mono font-semibold text-emerald-800">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
            1,248 developers are battling right now
          </span>
        </div>
      </div>

      {/* Exactly 3 Larger, Clean Activity Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {LIVE_ACTIVITIES.map((activity, index) => (
          <motion.div
            key={activity.id}
            initial={{ opacity: 0, y: 15 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.3, delay: index * 0.1 }}
            whileHover={{ y: -4 }}
          >
            <Card className="p-6 bg-white border-slate-200/90 shadow-sm hover:shadow-md text-center flex flex-col items-center justify-between h-full space-y-3">
              <div className="w-12 h-12 rounded-2xl bg-slate-50 border border-slate-200 flex items-center justify-center text-2xl shadow-sm">
                {activity.icon}
              </div>

              <div>
                <h3 className="text-base font-bold text-slate-900 leading-snug">
                  {activity.title}
                </h3>
              </div>

              <span className="text-xs font-mono font-bold text-cyan-700 bg-cyan-50 px-2.5 py-1 rounded-full border border-cyan-200">
                {activity.tag}
              </span>
            </Card>
          </motion.div>
        ))}
      </div>
    </section>
  );
};
