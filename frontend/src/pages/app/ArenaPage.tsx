import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Badge } from '../../components/ui/Badge';
import { Swords, Zap, Trophy, ArrowRight, Shield } from 'lucide-react';
import { battleService } from '../../services/battleService';
import { BattleHistoryItem } from '../../types/battle';

export const ArenaPage: React.FC = () => {
  const navigate = useNavigate();
  const [history, setHistory] = useState<BattleHistoryItem[]>([]);
  const [loadingHistory, setLoadingHistory] = useState(true);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const res = await battleService.getBattleHistory(0, 5);
        setHistory(res.content || []);
      } catch {
        // ignore
      } finally {
        setLoadingHistory(false);
      }
    };

    fetchHistory();
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <Badge variant="cyan" size="sm" className="mb-2">
            <Swords className="w-3.5 h-3.5 mr-1" />
            LIVE COMBAT ARENA
          </Badge>
          <h1 className="text-3xl font-black text-slate-900 tracking-tight">
            THE BATTLE ARENA
          </h1>
          <p className="text-sm text-slate-600 mt-1">
            Real-time 1v1 algorithmic duels. Compete under server-authoritative timers to climb the leaderboard.
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card className="p-8 bg-white border-slate-200/90 shadow-md rounded-2xl space-y-4 flex flex-col justify-between">
          <div className="space-y-3">
            <div className="w-12 h-12 rounded-xl bg-cyan-50 text-cyan-600 flex items-center justify-center">
              <Zap className="w-6 h-6" />
            </div>
            <h3 className="text-xl font-bold text-slate-900">Ranked 1v1 Matchmaking</h3>
            <p className="text-sm text-slate-600 leading-relaxed">
              Match with competitors in your Elo rating bracket (1,000 ± 150 MMR). Code simultaneously in an isolated workspace with live test runners and anti-cheat validation.
            </p>
          </div>
          <Button
            variant="glow"
            size="md"
            onClick={() => navigate('/matchmaking')}
            rightIcon={<ArrowRight className="w-4 h-4" />}
            className="w-full font-bold"
          >
            ENTER MATCHMAKING QUEUE
          </Button>
        </Card>

        <Card className="p-8 bg-white border-slate-200/90 shadow-md rounded-2xl space-y-4 flex flex-col justify-between">
          <div className="space-y-3">
            <div className="w-12 h-12 rounded-xl bg-violet-50 text-violet-600 flex items-center justify-center">
              <Trophy className="w-6 h-6" />
            </div>
            <h3 className="text-xl font-bold text-slate-900">Private Custom Duel</h3>
            <p className="text-sm text-slate-600 leading-relaxed">
              Invite friends or teammates to a custom room with custom language choices, time limits, and test difficulty.
            </p>
          </div>
          <Button variant="outline" size="md" disabled className="w-full">
            CUSTOM DUELS (COMING SOON)
          </Button>
        </Card>
      </div>

      {/* Recent Battle History */}
      <div className="mt-8">
        <h2 className="text-lg font-bold text-slate-900 mb-4 flex items-center gap-2">
          <Trophy className="w-5 h-5 text-amber-500" />
          Recent Battle History
        </h2>

        {loadingHistory ? (
          <div className="text-sm text-slate-400">Loading battle logs...</div>
        ) : history.length === 0 ? (
          <Card className="p-8 text-center bg-white border-slate-200 text-slate-500 rounded-2xl">
            <Shield className="w-10 h-10 text-slate-300 mx-auto mb-2" />
            <p className="font-semibold text-slate-700">No combat encounters recorded yet.</p>
            <p className="text-xs text-slate-500 mt-1">Jump into ranked matchmaking to begin your competitive climb!</p>
          </Card>
        ) : (
          <div className="space-y-3">
            {history.map((item) => (
              <Card
                key={item.battleId}
                className="p-4 bg-white border-slate-200 hover:border-cyan-400 transition-colors rounded-xl flex items-center justify-between"
              >
                <div className="flex items-center gap-3">
                  <div
                    className={`w-10 h-10 rounded-xl flex items-center justify-center font-bold text-xs ${
                      item.outcome === 'WIN'
                        ? 'bg-emerald-50 text-emerald-600 border border-emerald-200'
                        : item.outcome === 'LOSS'
                        ? 'bg-rose-50 text-rose-600 border border-rose-200'
                        : 'bg-slate-100 text-slate-600 border border-slate-200'
                    }`}
                  >
                    {item.outcome}
                  </div>
                  <div>
                    <div className="font-bold text-sm text-slate-900">{item.challengeTitle}</div>
                    <div className="text-xs text-slate-500">
                      vs <span className="font-semibold text-slate-700">{item.opponentUsername}</span> • {item.difficulty}
                    </div>
                  </div>
                </div>

                <div className="flex items-center gap-4 text-right">
                  <div>
                    <div
                      className={`font-black text-sm ${
                        item.ratingDelta > 0
                          ? 'text-emerald-600'
                          : item.ratingDelta < 0
                          ? 'text-rose-600'
                          : 'text-slate-600'
                      }`}
                    >
                      {item.ratingDelta > 0 ? `+${item.ratingDelta}` : item.ratingDelta} MMR
                    </div>
                    <div className="text-xs font-semibold text-amber-600">+{item.xpEarned} XP</div>
                  </div>
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => navigate(`/battle/${item.battleId}/result`)}
                    className="text-xs"
                  >
                    View
                  </Button>
                </div>
              </Card>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default ArenaPage;
