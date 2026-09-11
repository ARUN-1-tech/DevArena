import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { LIVE_ARENA_ACTIVITIES, LiveActivity } from '../../data/landingData';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Radio, Users, Activity, ExternalLink } from 'lucide-react';

export const LiveArenaSection: React.FC = () => {
  const [activities, setActivities] = useState<LiveActivity[]>(LIVE_ARENA_ACTIVITIES);
  const [activeBattlers] = useState(1248);

  // Periodic simulated live pulse
  useEffect(() => {
    const interval = setInterval(() => {
      setActivities((prev) => {
        const first = prev[0];
        const rest = prev.slice(1);
        return [...rest, { ...first, id: `act-${Date.now()}` }];
      });
    }, 4000);
    return () => clearInterval(interval);
  }, []);

  return (
    <section id="arena" className="py-16 border-y border-slate-200/80 bg-white/60 relative">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Section Header */}
        <div className="flex flex-col md:flex-row md:items-end justify-between mb-8 gap-4">
          <div className="space-y-1">
            <div className="flex items-center gap-2">
              <span className="flex h-2.5 w-2.5 relative">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75" />
                <span className="relative inline-flex rounded-full h-2.5 w-2.5 bg-emerald-500" />
              </span>
              <span className="text-xs font-mono font-bold uppercase tracking-widest text-emerald-700">
                LIVE TELEMETRY
              </span>
            </div>
            <h2 className="text-3xl font-extrabold tracking-tight text-slate-900">
              THE ARENA IS LIVE
            </h2>
            <p className="text-sm text-slate-600">
              Developers are battling, advancing tiers, and leveling up right now.
            </p>
          </div>

          {/* Real-time battle counter pill */}
          <div className="flex items-center gap-4 bg-slate-900 text-white px-4 py-2 rounded-xl text-xs font-mono shadow-sm">
            <div className="flex items-center gap-2">
              <Users className="w-4 h-4 text-cyan-400" />
              <span>
                <strong className="text-cyan-400">{activeBattlers.toLocaleString()}</strong> in Battle
              </span>
            </div>
            <span className="text-slate-600">|</span>
            <div className="flex items-center gap-1.5 text-emerald-400">
              <Activity className="w-3.5 h-3.5" />
              <span>Matchmaking &lt; 4s</span>
            </div>
          </div>
        </div>

        {/* Live Activity Cards Stream */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <AnimatePresence mode="popLayout">
            {activities.slice(0, 6).map((activity) => (
              <motion.div
                key={activity.id}
                layout
                initial={{ opacity: 0, scale: 0.95, y: 10 }}
                animate={{ opacity: 1, scale: 1, y: 0 }}
                exit={{ opacity: 0, scale: 0.9, transition: { duration: 0.2 } }}
                transition={{ duration: 0.3 }}
              >
                <Card hoverEffect className="p-4 bg-white border-slate-200/90 h-full flex flex-col justify-between">
                  <div className="flex items-start justify-between gap-3">
                    <div className="flex items-center gap-2.5">
                      <span className="text-xl select-none" role="img" aria-label="Event icon">
                        {activity.icon}
                      </span>
                      <div>
                        <h4 className="text-xs font-bold text-slate-900 line-clamp-1">
                          {activity.title}
                        </h4>
                        <p className="text-[11px] font-mono text-cyan-700 font-semibold mt-0.5">
                          {activity.highlight}
                        </p>
                      </div>
                    </div>

                    {activity.badgeText && (
                      <Badge variant={activity.badgeVariant || 'default'} size="sm">
                        {activity.badgeText}
                      </Badge>
                    )}
                  </div>

                  <div className="flex items-center justify-between mt-3 pt-2.5 border-t border-slate-100 text-[10px] font-mono text-slate-400">
                    <span className="flex items-center gap-1">
                      <Radio className="w-3 h-3 text-emerald-500" />
                      {activity.timestamp}
                    </span>
                    <span className="hover:text-slate-700 flex items-center gap-0.5 cursor-pointer">
                      Inspect Duel <ExternalLink className="w-2.5 h-2.5" />
                    </span>
                  </div>
                </Card>
              </motion.div>
            ))}
          </AnimatePresence>
        </div>
      </div>
    </section>
  );
};
