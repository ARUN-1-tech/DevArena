import React from 'react';
import { motion } from 'framer-motion';
import { LEADERBOARD_PODIUM } from '../../data/landingData';
import { Card } from '../ui/Card';
import { Button } from '../ui/Button';
import { ArrowRight, Trophy } from 'lucide-react';

export const LeaderboardSection: React.FC = () => {
  // Ordered: 2nd place (left), 1st place (center), 3rd place (right)
  const podiumOrder = [
    LEADERBOARD_PODIUM[1],
    LEADERBOARD_PODIUM[0],
    LEADERBOARD_PODIUM[2],
  ];

  return (
    <section id="leaderboard" className="py-20 max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      {/* Section Header */}
      <div className="text-center max-w-xl mx-auto mb-14 space-y-2">
        <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-sandwich-50">
          ARENA LEADERBOARD
        </h2>
        <p className="text-sm sm:text-base text-sandwich-300">
          The top developers climbing the ranks this season.
        </p>
      </div>

      {/* Friendly 3-Player Podium */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 items-end mb-10">
        {podiumOrder.map((player) => {
          const isFirst = player.rank === 1;

          return (
            <motion.div
              key={player.name}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.4, delay: player.rank * 0.1 }}
              whileHover={{ y: -4 }}
            >
              <Card
                className={`p-6 bg-sandwich-900/90 border-sandwich-700/80 text-center flex flex-col justify-between transition-all ${
                  isFirst
                    ? 'border-sandwich-400 ring-2 ring-sandwich-400/20 shadow-glow-silver md:-translate-y-4'
                    : 'shadow-luxury-card hover:border-sandwich-600'
                }`}
              >
                <div>
                  {/* Rank Crown / Medal */}
                  <div className="flex justify-center mb-3">
                    <div
                      className={`w-10 h-10 rounded-full flex items-center justify-center font-bold text-sm shadow-sm ${
                        isFirst
                          ? 'bg-sandwich-50 text-sandwich-950 ring-4 ring-sandwich-700 shadow-glow-white'
                          : player.rank === 2
                          ? 'bg-sandwich-800 text-sandwich-100 border border-sandwich-600'
                          : 'bg-sandwich-800 text-sandwich-300 border border-sandwich-700'
                      }`}
                    >
                      {isFirst ? <Trophy className="w-5 h-5 text-sandwich-950" /> : `#${player.rank}`}
                    </div>
                  </div>

                  <h3 className="text-lg font-bold text-sandwich-100">{player.name}</h3>
                  <p className="text-xs font-mono text-sandwich-400 font-semibold mb-4">
                    {player.badge}
                  </p>
                </div>

                <div className="pt-4 border-t border-sandwich-800">
                  <span className="text-[11px] font-mono text-sandwich-400 block uppercase tracking-wider">
                    Rating
                  </span>
                  <span className="text-xl font-extrabold font-mono text-sandwich-100">
                    {player.rating} MMR
                  </span>
                </div>
              </Card>
            </motion.div>
          );
        })}
      </div>

      {/* View Full Leaderboard Action */}
      <div className="text-center">
        <Button
          variant="outline"
          size="md"
          rightIcon={<ArrowRight className="w-4 h-4 text-sandwich-300" />}
          onClick={() => {
            window.location.href = '/leaderboard';
          }}
        >
          VIEW FULL LEADERBOARD
        </Button>
      </div>
    </section>
  );
};
