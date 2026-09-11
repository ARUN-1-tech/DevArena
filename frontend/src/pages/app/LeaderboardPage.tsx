import React from 'react';
import { Card } from '../../components/ui/Card';
import { Badge } from '../../components/ui/Badge';
import { Trophy } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';

const LEADERBOARD_USERS = [
  { rank: 1, name: 'AlgoGod', rating: 2480, tier: 'Grandmaster', wins: 312, streak: 14 },
  { rank: 2, name: 'BitShift', rating: 2390, tier: 'Master', wins: 284, streak: 8 },
  { rank: 3, name: 'CodeViper', rating: 2315, tier: 'Master', wins: 251, streak: 6 },
  { rank: 4, name: 'PixelSamurai', rating: 2190, tier: 'Diamond', wins: 198, streak: 5 },
  { rank: 5, name: 'ZeroIndex', rating: 2085, tier: 'Diamond', wins: 182, streak: 3 },
];

export const LeaderboardPage: React.FC = () => {
  const { user } = useAuth();

  return (
    <div className="space-y-6">
      <div>
        <Badge variant="cyan" size="sm" className="mb-2">
          <Trophy className="w-3.5 h-3.5 mr-1" />
          GLOBAL RANKINGS
        </Badge>
        <h1 className="text-3xl font-black text-slate-900 tracking-tight">
          ARENA LEADERBOARD
        </h1>
        <p className="text-sm text-slate-600 mt-1">
          Top competitive coders ranked by MMR in Season 01.
        </p>
      </div>

      <Card className="p-0 border-slate-200/90 shadow-sm overflow-hidden bg-white rounded-2xl">
        <div className="divide-y divide-slate-100">
          {LEADERBOARD_USERS.map((p) => (
            <div
              key={p.rank}
              className="p-4 sm:p-5 flex items-center justify-between hover:bg-slate-50 transition-colors"
            >
              <div className="flex items-center gap-4">
                <span
                  className={`w-8 h-8 rounded-xl font-mono font-bold flex items-center justify-center text-xs ${
                    p.rank === 1
                      ? 'bg-amber-100 text-amber-800'
                      : p.rank === 2
                      ? 'bg-slate-200 text-slate-700'
                      : p.rank === 3
                      ? 'bg-amber-50 text-amber-700'
                      : 'bg-slate-100 text-slate-500'
                  }`}
                >
                  #{p.rank}
                </span>
                <div>
                  <p className="text-sm font-bold text-slate-900">{p.name}</p>
                  <p className="text-xs font-mono text-slate-500">
                    {p.tier} • {p.wins} wins • {p.streak}🔥 streak
                  </p>
                </div>
              </div>

              <div className="text-right font-mono">
                <span className="text-base font-extrabold text-cyan-600">{p.rating}</span>
                <span className="text-[10px] text-slate-400 block uppercase">MMR</span>
              </div>
            </div>
          ))}

          {/* Current player rank row */}
          <div className="p-4 sm:p-5 bg-cyan-50/60 border-t-2 border-cyan-200 flex items-center justify-between">
            <div className="flex items-center gap-4">
              <span className="w-8 h-8 rounded-xl font-mono font-bold flex items-center justify-center text-xs bg-cyan-600 text-white shadow-xs">
                YOU
              </span>
              <div>
                <p className="text-sm font-bold text-cyan-950">
                  {user?.displayName || 'Challenger'}
                </p>
                <p className="text-xs font-mono text-cyan-700">
                  Novice League • {user?.stats?.wins || 0} wins
                </p>
              </div>
            </div>

            <div className="text-right font-mono">
              <span className="text-base font-extrabold text-cyan-700">
                {user?.stats?.rating || 1000}
              </span>
              <span className="text-[10px] text-cyan-600 block uppercase">MMR</span>
            </div>
          </div>
        </div>
      </Card>
    </div>
  );
};
