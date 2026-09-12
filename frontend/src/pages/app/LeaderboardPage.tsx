import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { Trophy, Medal, ChevronLeft, ChevronRight, Loader2 } from 'lucide-react';
import { Card } from '../../components/ui/Card';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { useAuth } from '../../contexts/AuthContext';
import { leaderboardService } from '../../services/leaderboardService';
import { LeaderboardResponse } from '../../types/progression';

type TabType = 'global' | 'weekly' | 'monthly';

export const LeaderboardPage: React.FC = () => {
  const { user } = useAuth();

  const [activeTab, setActiveTab] = useState<TabType>('global');
  const [page, setPage] = useState(0);
  const [data, setData] = useState<LeaderboardResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchLeaderboard = async (tab: TabType, pageNum: number) => {
    try {
      setLoading(true);
      setError(null);
      const res = await leaderboardService.getLeaderboard(tab, pageNum, 20);
      setData(res);
    } catch (err: any) {
      setError(err?.message || 'Failed to load leaderboard.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLeaderboard(activeTab, page);
  }, [activeTab, page]);

  const handleTabChange = (tab: TabType) => {
    if (tab !== activeTab) {
      setActiveTab(tab);
      setPage(0);
    }
  };

  const top3 = data?.top3 || [];
  const p1 = top3[0];
  const p2 = top3[1];
  const p3 = top3[2];

  return (
    <div className="space-y-8 max-w-5xl mx-auto pb-16">
      {/* Header & Tabs */}
      <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <Badge variant="cyan" size="sm" className="mb-2">
            <Trophy className="w-3.5 h-3.5 mr-1" />
            SEASON 01 LEADERBOARD
          </Badge>
          <h1 className="text-3xl font-black text-slate-900 tracking-tight">
            GLOBAL CODING RANKINGS
          </h1>
          <p className="text-sm text-slate-600 mt-1">
            Real competitive ratings determined by verified 1v1 Arena Duels and Katas.
          </p>
        </div>

        {/* Tab Selector */}
        <div className="flex items-center bg-slate-100 p-1.5 rounded-xl self-start sm:self-auto border border-slate-200/80">
          {(['global', 'weekly', 'monthly'] as TabType[]).map((tab) => (
            <button
              key={tab}
              onClick={() => handleTabChange(tab)}
              className={`relative px-4 py-1.5 text-xs font-bold uppercase tracking-wider rounded-lg transition-all ${
                activeTab === tab
                  ? 'text-slate-900 shadow-sm'
                  : 'text-slate-500 hover:text-slate-800'
              }`}
            >
              {activeTab === tab && (
                <motion.div
                  layoutId="leaderboardTabPill"
                  className="absolute inset-0 bg-white rounded-lg shadow-sm border border-slate-200/50"
                  transition={{ type: 'spring', bounce: 0.2, duration: 0.4 }}
                />
              )}
              <span className="relative z-10">{tab}</span>
            </button>
          ))}
        </div>
      </div>

      {loading && !data ? (
        <div className="py-24 flex flex-col items-center justify-center text-slate-400 gap-3">
          <Loader2 className="w-8 h-8 animate-spin text-cyan-600" />
          <span className="text-sm font-medium">Summoning arena rankings...</span>
        </div>
      ) : error ? (
        <Card className="p-8 text-center text-rose-600 bg-rose-50/50 border-rose-200 rounded-2xl">
          <p className="text-sm font-medium">{error}</p>
          <Button variant="secondary" size="sm" onClick={() => fetchLeaderboard(activeTab, page)} className="mt-4">
            Retry
          </Button>
        </Card>
      ) : (
        <>
          {/* PODIUM TOP 3 (Shown on first page) */}
          {page === 0 && top3.length > 0 && (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end pt-6 pb-2">
              {/* #2 Silver (Left) */}
              {p2 && (
                <motion.div
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.1 }}
                  className="order-2 md:order-1"
                >
                  <Card className="p-6 bg-gradient-to-b from-slate-100/90 to-white border-slate-300 rounded-2xl text-center shadow-sm relative overflow-hidden">
                    <div className="absolute top-3 left-3">
                      <span className="w-7 h-7 rounded-lg bg-slate-200 text-slate-700 font-mono font-black text-xs flex items-center justify-center">
                        #2
                      </span>
                    </div>
                    <div className="w-16 h-16 rounded-2xl bg-gradient-to-tr from-slate-400 to-slate-200 text-slate-800 mx-auto flex items-center justify-center font-black text-xl shadow-inner border border-slate-300">
                      {p2.username.slice(0, 2).toUpperCase()}
                    </div>
                    <h3 className="text-base font-black text-slate-900 mt-3 truncate">{p2.displayName || p2.username}</h3>
                    <p className="text-xs font-mono text-slate-500">@{p2.username}</p>
                    <div className="mt-3 inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-slate-200/60 text-slate-800 text-xs font-mono font-bold">
                      <Trophy className="w-3.5 h-3.5 text-slate-600" />
                      {p2.rating} MMR
                    </div>
                    <div className="grid grid-cols-2 gap-2 mt-4 pt-4 border-t border-slate-100 text-center">
                      <div>
                        <span className="text-[10px] text-slate-400 uppercase font-mono block">Win Rate</span>
                        <span className="text-xs font-bold font-mono text-slate-700">{p2.winRate}%</span>
                      </div>
                      <div>
                        <span className="text-[10px] text-slate-400 uppercase font-mono block">Solved</span>
                        <span className="text-xs font-bold font-mono text-slate-700">{p2.solvedChallenges}</span>
                      </div>
                    </div>
                  </Card>
                </motion.div>
              )}

              {/* #1 Gold (Center) */}
              {p1 && (
                <motion.div
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.05 }}
                  className="order-1 md:order-2"
                >
                  <Card className="p-7 bg-gradient-to-b from-amber-500/10 via-amber-50/50 to-white border-2 border-amber-400 rounded-3xl text-center shadow-lg shadow-amber-500/10 relative overflow-hidden">
                    <div className="absolute top-3 left-3">
                      <span className="w-8 h-8 rounded-lg bg-gradient-to-tr from-amber-500 to-yellow-400 text-slate-950 font-mono font-black text-xs flex items-center justify-center shadow-sm">
                        #1
                      </span>
                    </div>
                    <div className="w-20 h-20 rounded-2xl bg-gradient-to-tr from-amber-400 to-yellow-300 text-slate-950 mx-auto flex items-center justify-center font-black text-2xl shadow-md border-2 border-amber-300">
                      {p1.username.slice(0, 2).toUpperCase()}
                    </div>
                    <div className="inline-flex items-center gap-1 mt-2 text-[11px] font-mono font-bold text-amber-700 uppercase">
                      <Medal className="w-3.5 h-3.5 text-amber-500" />
                      Grandmaster
                    </div>
                    <h3 className="text-lg font-black text-slate-900 mt-0.5 truncate">{p1.displayName || p1.username}</h3>
                    <p className="text-xs font-mono text-slate-500">@{p1.username}</p>
                    <div className="mt-3 inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full bg-amber-400 text-slate-950 text-sm font-mono font-black shadow-sm">
                      <Trophy className="w-4 h-4" />
                      {p1.rating} MMR
                    </div>
                    <div className="grid grid-cols-2 gap-2 mt-4 pt-4 border-t border-amber-100 text-center">
                      <div>
                        <span className="text-[10px] text-slate-500 uppercase font-mono block">Win Rate</span>
                        <span className="text-xs font-black font-mono text-slate-900">{p1.winRate}%</span>
                      </div>
                      <div>
                        <span className="text-[10px] text-slate-500 uppercase font-mono block">Solved</span>
                        <span className="text-xs font-black font-mono text-slate-900">{p1.solvedChallenges} katas</span>
                      </div>
                    </div>
                  </Card>
                </motion.div>
              )}

              {/* #3 Bronze (Right) */}
              {p3 && (
                <motion.div
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.15 }}
                  className="order-3 md:order-3"
                >
                  <Card className="p-6 bg-gradient-to-b from-amber-900/5 to-white border-amber-200/90 rounded-2xl text-center shadow-sm relative overflow-hidden">
                    <div className="absolute top-3 left-3">
                      <span className="w-7 h-7 rounded-lg bg-amber-100 text-amber-800 font-mono font-black text-xs flex items-center justify-center">
                        #3
                      </span>
                    </div>
                    <div className="w-16 h-16 rounded-2xl bg-gradient-to-tr from-amber-600 to-amber-400 text-white mx-auto flex items-center justify-center font-black text-xl shadow-inner">
                      {p3.username.slice(0, 2).toUpperCase()}
                    </div>
                    <h3 className="text-base font-black text-slate-900 mt-3 truncate">{p3.displayName || p3.username}</h3>
                    <p className="text-xs font-mono text-slate-500">@{p3.username}</p>
                    <div className="mt-3 inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-amber-100 text-amber-900 text-xs font-mono font-bold">
                      <Trophy className="w-3.5 h-3.5 text-amber-700" />
                      {p3.rating} MMR
                    </div>
                    <div className="grid grid-cols-2 gap-2 mt-4 pt-4 border-t border-slate-100 text-center">
                      <div>
                        <span className="text-[10px] text-slate-400 uppercase font-mono block">Win Rate</span>
                        <span className="text-xs font-bold font-mono text-slate-700">{p3.winRate}%</span>
                      </div>
                      <div>
                        <span className="text-[10px] text-slate-400 uppercase font-mono block">Solved</span>
                        <span className="text-xs font-bold font-mono text-slate-700">{p3.solvedChallenges}</span>
                      </div>
                    </div>
                  </Card>
                </motion.div>
              )}
            </div>
          )}

          {/* AUTHENTICATED USER'S PINNED RANK BANNER */}
          {data?.myRank && (
            <Card className="p-4 bg-gradient-to-r from-cyan-900/10 via-cyan-500/10 to-slate-900/5 border-2 border-cyan-400/80 rounded-2xl shadow-sm flex items-center justify-between">
              <div className="flex items-center gap-4">
                <span className="w-10 h-10 rounded-xl bg-cyan-600 text-white font-mono font-black text-sm flex items-center justify-center shadow-sm">
                  #{data.myRank.rank}
                </span>
                <div>
                  <div className="flex items-center gap-2">
                    <span className="text-sm font-black text-slate-900">{data.myRank.displayName}</span>
                    <Badge variant="cyan" size="sm">YOU</Badge>
                  </div>
                  <p className="text-xs font-mono text-slate-500">
                    Level {data.myRank.level} • {data.myRank.solvedChallenges} Solved • {data.myRank.wins}W / {data.myRank.losses}L
                  </p>
                </div>
              </div>
              <div className="text-right">
                <span className="text-lg font-black font-mono text-cyan-700">{data.myRank.rating} MMR</span>
                <span className="text-[11px] font-mono text-slate-500 block">{data.myRank.winRate}% win rate</span>
              </div>
            </Card>
          )}

          {/* MAIN RANKED TABLE */}
          <Card className="p-0 border-slate-200/90 shadow-sm overflow-hidden bg-white rounded-2xl">
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-slate-100 bg-slate-50/70 text-[11px] font-mono font-bold uppercase tracking-wider text-slate-400">
                    <th className="py-3.5 px-5">Rank</th>
                    <th className="py-3.5 px-4">Player</th>
                    <th className="py-3.5 px-4 text-center">Level</th>
                    <th className="py-3.5 px-4 text-right">Rating (MMR)</th>
                    <th className="py-3.5 px-4 text-center">Win Rate</th>
                    <th className="py-3.5 px-5 text-right">Solved Katas</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100 text-sm">
                  {data?.rankings.map((p) => {
                    const isCurrentUser = user?.id === p.userId;
                    return (
                      <tr
                        key={p.userId}
                        className={`transition-colors ${
                          isCurrentUser
                            ? 'bg-cyan-50/60 font-semibold text-slate-900'
                            : 'hover:bg-slate-50/80 text-slate-700'
                        }`}
                      >
                        <td className="py-4 px-5 font-mono">
                          <span
                            className={`w-7 h-7 rounded-lg inline-flex items-center justify-center text-xs font-black ${
                              p.rank === 1
                                ? 'bg-amber-100 text-amber-800'
                                : p.rank === 2
                                ? 'bg-slate-200 text-slate-700'
                                : p.rank === 3
                                ? 'bg-amber-50 text-amber-700'
                                : 'text-slate-500'
                            }`}
                          >
                            #{p.rank}
                          </span>
                        </td>
                        <td className="py-4 px-4">
                          <div className="flex items-center gap-3">
                            <div className="w-9 h-9 rounded-xl bg-slate-100 border border-slate-200 flex items-center justify-center font-bold text-xs text-slate-700 shrink-0">
                              {p.username.slice(0, 2).toUpperCase()}
                            </div>
                            <div>
                              <div className="flex items-center gap-2">
                                <span className="font-bold text-slate-900 truncate max-w-[150px] sm:max-w-none">
                                  {p.displayName || p.username}
                                </span>
                                {isCurrentUser && (
                                  <span className="text-[10px] font-mono px-1.5 py-0.5 rounded bg-cyan-100 text-cyan-800 font-bold">
                                    YOU
                                  </span>
                                )}
                              </div>
                              <span className="text-xs font-mono text-slate-400">@{p.username}</span>
                            </div>
                          </div>
                        </td>
                        <td className="py-4 px-4 text-center font-mono">
                          <Badge variant="purple" size="sm">Lvl {p.level}</Badge>
                        </td>
                        <td className="py-4 px-4 text-right font-mono font-black text-slate-900">
                          {p.rating}
                        </td>
                        <td className="py-4 px-4 text-center font-mono text-xs">
                          <span className={p.winRate >= 50 ? 'text-emerald-600 font-bold' : 'text-slate-500'}>
                            {p.winRate}%
                          </span>
                          <span className="text-[10px] text-slate-400 block font-normal">
                            ({p.wins}W / {p.losses}L)
                          </span>
                        </td>
                        <td className="py-4 px-5 text-right font-mono font-bold text-slate-700">
                          {p.solvedChallenges}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>

            {/* Pagination Controls */}
            {data && data.totalPages > 1 && (
              <div className="p-4 border-t border-slate-100 flex items-center justify-between bg-slate-50/50">
                <span className="text-xs font-mono text-slate-500">
                  Page {data.page + 1} of {data.totalPages} ({data.totalElements} contenders)
                </span>
                <div className="flex items-center gap-2">
                  <Button
                    variant="secondary"
                    size="sm"
                    disabled={page === 0}
                    onClick={() => setPage((p) => Math.max(0, p - 1))}
                  >
                    <ChevronLeft className="w-4 h-4 mr-1" /> Prev
                  </Button>
                  <Button
                    variant="secondary"
                    size="sm"
                    disabled={page >= data.totalPages - 1}
                    onClick={() => setPage((p) => p + 1)}
                  >
                    Next <ChevronRight className="w-4 h-4 ml-1" />
                  </Button>
                </div>
              </div>
            )}
          </Card>
        </>
      )}
    </div>
  );
};
