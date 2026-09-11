import React, { useState } from 'react';
import { LEADERBOARD_PLAYERS, LeaderboardEntry } from '../../data/landingData';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Button } from '../ui/Button';
import { Trophy, Flame, Shield, ArrowRight } from 'lucide-react';

export const LeaderboardSection: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'1v1' | 'blitz' | 'solo'>('1v1');

  const top3 = LEADERBOARD_PLAYERS.slice(0, 3);
  const rest = LEADERBOARD_PLAYERS.slice(3);

  const podiumOrder = [top3[1], top3[0], top3[2]]; // Rank 2, Rank 1, Rank 3

  const tierVariant = {
    GRANDMASTER: 'danger',
    MASTER: 'purple',
    DIAMOND: 'info',
    PLATINUM: 'default',
    GOLD: 'warning',
    SILVER: 'default',
    BRONZE: 'default',
    NOVICE: 'default',
  } as const;

  return (
    <section id="leaderboard" className="py-20 bg-slate-100/60 border-y border-slate-200/80">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Section Header & Tab Controls */}
        <div className="flex flex-col md:flex-row md:items-end justify-between mb-12 gap-6">
          <div className="space-y-2">
            <Badge variant="warning">GLOBAL STANDINGS</Badge>
            <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-slate-900">
              GLOBAL ARENA LEADERBOARD
            </h2>
            <p className="text-sm sm:text-base text-slate-600">
              The highest-rated developers on the planet. Updated in real-time after every duel.
            </p>
          </div>

          {/* Mode Switcher Tabs */}
          <div className="flex items-center p-1 bg-white border border-slate-200 rounded-xl shadow-sm">
            <button
              onClick={() => setActiveTab('1v1')}
              className={`px-3.5 py-1.5 text-xs font-mono font-semibold rounded-lg transition-all ${
                activeTab === '1v1'
                  ? 'bg-slate-900 text-white shadow-sm'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              1v1 Ranked Duels
            </button>
            <button
              onClick={() => setActiveTab('blitz')}
              className={`px-3.5 py-1.5 text-xs font-mono font-semibold rounded-lg transition-all ${
                activeTab === 'blitz'
                  ? 'bg-slate-900 text-white shadow-sm'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              Speed Blitz
            </button>
            <button
              onClick={() => setActiveTab('solo')}
              className={`px-3.5 py-1.5 text-xs font-mono font-semibold rounded-lg transition-all ${
                activeTab === 'solo'
                  ? 'bg-slate-900 text-white shadow-sm'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              Global Solos
            </button>
          </div>
        </div>

        {/* Podium: Top 3 Visual Showcase */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12 items-end">
          {podiumOrder.map((player: LeaderboardEntry) => {
            const isFirst = player.rank === 1;

            return (
              <Card
                key={player.username}
                hoverEffect
                className={`text-center p-6 bg-white flex flex-col justify-between ${
                  isFirst
                    ? 'border-amber-400 ring-2 ring-amber-400/20 shadow-xl md:-translate-y-4'
                    : 'border-slate-200'
                }`}
              >
                <div>
                  {/* Rank Crown / Medal */}
                  <div className="flex justify-center mb-3">
                    <div
                      className={`w-10 h-10 rounded-full flex items-center justify-center font-bold text-sm shadow-sm ${
                        player.rank === 1
                          ? 'bg-amber-400 text-slate-950 ring-4 ring-amber-100'
                          : player.rank === 2
                          ? 'bg-slate-200 text-slate-800'
                          : 'bg-amber-100 text-amber-800'
                      }`}
                    >
                      {player.rank === 1 ? <Trophy className="w-5 h-5" /> : `#${player.rank}`}
                    </div>
                  </div>

                  <div className="w-16 h-16 mx-auto rounded-2xl bg-gradient-to-tr from-cyan-600 via-violet-600 to-rose-600 flex items-center justify-center text-white font-extrabold text-lg shadow-md mb-3">
                    {player.avatarSeed}
                  </div>

                  <h3 className="text-lg font-bold text-slate-900 flex items-center justify-center gap-1.5">
                    {player.username}
                  </h3>
                  <p className="text-xs font-mono text-amber-600 font-semibold mb-3">
                    {player.badge || 'Grandmaster Elite'}
                  </p>
                </div>

                <div className="pt-4 border-t border-slate-100 space-y-2">
                  <div className="flex justify-between text-xs font-mono">
                    <span className="text-slate-500">Arena Rating</span>
                    <span className="font-extrabold text-slate-900 text-sm">
                      {player.rating} MMR
                    </span>
                  </div>
                  <div className="flex justify-between text-xs font-mono">
                    <span className="text-slate-500">Win Rate</span>
                    <span className="font-semibold text-emerald-600">{player.winRate}%</span>
                  </div>
                  <div className="flex justify-between text-xs font-mono">
                    <span className="text-slate-500">Current Streak</span>
                    <span className="font-semibold text-amber-600 flex items-center gap-1">
                      <Flame className="w-3.5 h-3.5" /> {player.streak} Wins
                    </span>
                  </div>
                </div>
              </Card>
            );
          })}
        </div>

        {/* Ladder Table: Ranks 4 - 10 */}
        <div className="bg-white rounded-2xl border border-slate-200/90 shadow-sm overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 border-b border-slate-200/80 text-xs font-mono uppercase text-slate-500">
                <tr>
                  <th className="py-3.5 px-5">Rank</th>
                  <th className="py-3.5 px-5">Developer</th>
                  <th className="py-3.5 px-5">Division</th>
                  <th className="py-3.5 px-5 text-right">Rating</th>
                  <th className="py-3.5 px-5 text-right">Win Rate</th>
                  <th className="py-3.5 px-5 text-right">W / L Record</th>
                  <th className="py-3.5 px-5 text-right">Streak</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 font-mono text-xs">
                {rest.map((player: LeaderboardEntry) => (
                  <tr key={player.username} className="hover:bg-slate-50/80 transition-colors">
                    <td className="py-4 px-5 font-bold text-slate-700">#{player.rank}</td>
                    <td className="py-4 px-5">
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-lg bg-slate-900 text-white font-bold text-xs flex items-center justify-center shrink-0">
                          {player.avatarSeed}
                        </div>
                        <div>
                          <p className="font-bold text-slate-900 font-sans text-sm">
                            {player.username}
                          </p>
                          {player.badge && (
                            <span className="text-[10px] text-cyan-700 font-semibold block">
                              {player.badge}
                            </span>
                          )}
                        </div>
                      </div>
                    </td>
                    <td className="py-4 px-5">
                      <Badge variant={tierVariant[player.tier]} size="sm">
                        <Shield className="w-3 h-3" /> {player.tier}
                      </Badge>
                    </td>
                    <td className="py-4 px-5 text-right font-extrabold text-slate-900 text-sm">
                      {player.rating}
                    </td>
                    <td className="py-4 px-5 text-right text-emerald-600 font-bold">
                      {player.winRate}%
                    </td>
                    <td className="py-4 px-5 text-right text-slate-500">
                      {player.wins}W / {player.losses}L
                    </td>
                    <td className="py-4 px-5 text-right">
                      <span className="inline-flex items-center gap-1 font-bold text-amber-600 bg-amber-50 px-2 py-0.5 rounded border border-amber-200">
                        <Flame className="w-3 h-3" /> {player.streak}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div className="p-4 bg-slate-50/60 border-t border-slate-100 flex items-center justify-between text-xs font-mono text-slate-500">
            <span>Showing Top 10 of 124,590 Ranked Developers</span>
            <Button
              variant="outline"
              size="sm"
              rightIcon={<ArrowRight className="w-3.5 h-3.5" />}
              onClick={() => alert('Full global leaderboards will open in Module 08!')}
            >
              View Full Standings
            </Button>
          </div>
        </div>
      </div>
    </section>
  );
};
