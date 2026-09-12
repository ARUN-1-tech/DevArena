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
          <Badge variant="default" size="sm" className="mb-2 bg-sandwich-900 border-sandwich-700 text-sandwich-200">
            <Trophy className="w-3.5 h-3.5 mr-1 text-sandwich-200" />
            SEASON 01 LEADERBOARD
          </Badge>
          <h1 className="text-3xl font-black text-sandwich-50 tracking-tight">
            GLOBAL CODING RANKINGS
          </h1>
          <p className="text-sm text-sandwich-300 mt-1">
            Real competitive ratings determined by verified 1v1 Arena Duels and Katas.
          </p>
        </div>

        {/* Tab Selector */}
        <div className="flex items-center bg-sandwich-950 p-1.5 rounded-xl self-start sm:self-auto border border-sandwich-800">
          {(['global', 'weekly', 'monthly'] as TabType[]).map((tab) => (
            <button
              key={tab}
              onClick={() => handleTabChange(tab)}
              className={`relative px-4 py-1.5 text-xs font-bold uppercase tracking-wider rounded-lg transition-all ${
                activeTab === tab
                  ? 'text-sandwich-50 shadow-sm'
                  : 'text-sandwich-400 hover:text-sandwich-200'
              }`}
            >
              {activeTab === tab && (
                <motion.div
                  layoutId="leaderboardTabPill"
                  className="absolute inset-0 bg-sandwich-800 rounded-lg shadow-glow-silver border border-sandwich-600"
                  transition={{ type: 'spring', bounce: 0.2, duration: 0.4 }}
                />
              )}
              <span className="relative z-10">{tab}</span>
            </button>
          ))}
        </div>
      </div>

      {loading && !data ? (
        <div className="py-24 flex flex-col items-center justify-center text-sandwich-400 gap-3">
          <Loader2 className="w-8 h-8 animate-spin text-sandwich-300" />
          <span className="text-sm font-medium">Summoning arena rankings...</span>
        </div>
      ) : error ? (
        <Card className="p-8 text-center text-red-300 bg-red-950/40 border-red-800/80 rounded-2xl">
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
                  <Card className="p-6 bg-sandwich-900/90 border-sandwich-700 rounded-2xl text-center shadow-luxury-card relative overflow-hidden">
                    <div className="absolute top-3 left-3">
                      <span className="w-7 h-7 rounded-lg bg-sandwich-800 text-sandwich-200 font-mono font-black text-xs flex items-center justify-center border border-sandwich-700">
                        #2
                      </span>
                    </div>
                    <div className="w-16 h-16 rounded-2xl bg-sandwich-800 text-sandwich-200 mx-auto flex items-center justify-center font-black text-xl shadow-inner border border-sandwich-600">
                      {p2.username.slice(0, 2).toUpperCase()}
                    </div>
                    <h3 className="text-base font-black text-sandwich-100 mt-3 truncate">{p2.displayName || p2.username}</h3>
                    <p className="text-xs font-mono text-sandwich-400">@{p2.username}</p>
                    <div className="mt-3 inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-sandwich-800 text-sandwich-200 text-xs font-mono font-bold border border-sandwich-700">
                      <Trophy className="w-3.5 h-3.5 text-sandwich-300" />
                      {p2.rating} MMR
                    </div>
                    <div className="grid grid-cols-2 gap-2 mt-4 pt-4 border-t border-sandwich-800 text-center">
                      <div>
                        <span className="text-[10px] text-sandwich-400 uppercase font-mono block">Win Rate</span>
                        <span className="text-xs font-bold font-mono text-sandwich-200">{p2.winRate}%</span>
                      </div>
                      <div>
                        <span className="text-[10px] text-sandwich-400 uppercase font-mono block">Solved</span>
                        <span className="text-xs font-bold font-mono text-sandwich-200">{p2.solvedChallenges}</span>
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
                  <Card className="p-7 bg-sandwich-900/95 border-2 border-sandwich-400 rounded-3xl text-center shadow-glow-silver relative overflow-hidden">
                    <div className="absolute top-3 left-3">
                      <span className="w-8 h-8 rounded-lg bg-sandwich-50 text-sandwich-950 font-mono font-black text-xs flex items-center justify-center shadow-glow-white">
                        #1
                      </span>
                    </div>
                    <div className="w-20 h-20 rounded-2xl bg-sandwich-800 text-sandwich-50 mx-auto flex items-center justify-center font-black text-2xl shadow-md border-2 border-sandwich-500">
                      {p1.username.slice(0, 2).toUpperCase()}
                    </div>
                    <div className="inline-flex items-center gap-1 mt-2 text-[11px] font-mono font-bold text-sandwich-200 uppercase">
                      <Medal className="w-3.5 h-3.5 text-sandwich-300" />
                      Grandmaster
                    </div>
                    <h3 className="text-lg font-black text-sandwich-50 mt-0.5 truncate">{p1.displayName || p1.username}</h3>
                    <p className="text-xs font-mono text-sandwich-400">@{p1.username}</p>
                    <div className="mt-3 inline-flex items-center gap-1.5 px-3.5 py-1.5 rounded-full bg-sandwich-50 text-sandwich-950 text-sm font-mono font-black shadow-glow-white">
                      <Trophy className="w-4 h-4 text-sandwich-950" />
                      {p1.rating} MMR
                    </div>
                    <div className="grid grid-cols-2 gap-2 mt-4 pt-4 border-t border-sandwich-800 text-center">
                      <div>
                        <span className="text-[10px] text-sandwich-400 uppercase font-mono block">Win Rate</span>
                        <span className="text-xs font-black font-mono text-sandwich-100">{p1.winRate}%</span>
                      </div>
                      <div>
                        <span className="text-[10px] text-sandwich-400 uppercase font-mono block">Solved</span>
                        <span className="text-xs font-black font-mono text-sandwich-100">{p1.solvedChallenges} katas</span>
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
                  <Card className="p-6 bg-sandwich-900/90 border-sandwich-700/80 rounded-2xl text-center shadow-luxury-card relative overflow-hidden">
                    <div className="absolute top-3 left-3">
                      <span className="w-7 h-7 rounded-lg bg-sandwich-800 text-sandwich-300 font-mono font-black text-xs flex items-center justify-center border border-sandwich-750">
                        #3
                      </span>
                    </div>
                    <div className="w-16 h-16 rounded-2xl bg-sandwich-800 text-sandwich-200 mx-auto flex items-center justify-center font-black text-xl shadow-inner border border-sandwich-700">
                      {p3.username.slice(0, 2).toUpperCase()}
                    </div>
                    <h3 className="text-base font-black text-sandwich-100 mt-3 truncate">{p3.displayName || p3.username}</h3>
                    <p className="text-xs font-mono text-sandwich-400">@{p3.username}</p>
                    <div className="mt-3 inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-sandwich-800 text-sandwich-300 text-xs font-mono font-bold border border-sandwich-700">
                      <Trophy className="w-3.5 h-3.5 text-sandwich-400" />
                      {p3.rating} MMR
                    </div>
                    <div className="grid grid-cols-2 gap-2 mt-4 pt-4 border-t border-sandwich-800 text-center">
                      <div>
                        <span className="text-[10px] text-sandwich-400 uppercase font-mono block">Win Rate</span>
                        <span className="text-xs font-bold font-mono text-sandwich-200">{p3.winRate}%</span>
                      </div>
                      <div>
                        <span className="text-[10px] text-sandwich-400 uppercase font-mono block">Solved</span>
                        <span className="text-xs font-bold font-mono text-sandwich-200">{p3.solvedChallenges}</span>
                      </div>
                    </div>
                  </Card>
                </motion.div>
              )}
            </div>
          )}

          {/* AUTHENTICATED USER'S PINNED RANK BANNER */}
          {data?.myRank && (
            <Card className="p-4 bg-sandwich-900/95 border-2 border-sandwich-400 rounded-2xl shadow-luxury-card flex items-center justify-between">
              <div className="flex items-center gap-4">
                <span className="w-10 h-10 rounded-xl bg-sandwich-800 border border-sandwich-600 text-sandwich-50 font-mono font-black text-sm flex items-center justify-center shadow-glow-silver">
                  #{data.myRank.rank}
                </span>
                <div>
                  <div className="flex items-center gap-2">
                    <span className="text-sm font-black text-sandwich-50">{data.myRank.displayName}</span>
                    <Badge variant="default" size="sm" className="bg-sandwich-800 border border-sandwich-600 text-sandwich-200">YOU</Badge>
                  </div>
                  <p className="text-xs font-mono text-sandwich-400">
                    Level {data.myRank.level} • {data.myRank.solvedChallenges} Solved • {data.myRank.wins}W / {data.myRank.losses}L
                  </p>
                </div>
              </div>
              <div className="text-right">
                <span className="text-lg font-black font-mono text-sandwich-100">{data.myRank.rating} MMR</span>
                <span className="text-[11px] font-mono text-sandwich-400 block">{data.myRank.winRate}% win rate</span>
              </div>
            </Card>
          )}

          {/* MAIN RANKED TABLE */}
          <Card className="p-0 border-sandwich-800 shadow-luxury overflow-hidden bg-sandwich-900/90 rounded-2xl backdrop-blur-xl">
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-sandwich-800 bg-sandwich-950/80 text-[11px] font-mono font-bold uppercase tracking-wider text-sandwich-400">
                    <th className="py-3.5 px-5">Rank</th>
                    <th className="py-3.5 px-4">Player</th>
                    <th className="py-3.5 px-4 text-center">Level</th>
                    <th className="py-3.5 px-4 text-right">Rating (MMR)</th>
                    <th className="py-3.5 px-4 text-center">Win Rate</th>
                    <th className="py-3.5 px-5 text-right">Solved Katas</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-sandwich-800/80 text-sm">
                  {data?.rankings.map((p) => {
                    const isCurrentUser = user?.id === p.userId;
                    return (
                      <tr
                        key={p.userId}
                        className={`transition-colors ${
                          isCurrentUser
                            ? 'bg-sandwich-800/70 font-semibold text-sandwich-50'
                            : 'hover:bg-sandwich-800/40 text-sandwich-200'
                        }`}
                      >
                        <td className="py-4 px-5 font-mono">
                          <span
                            className={`w-7 h-7 rounded-lg inline-flex items-center justify-center text-xs font-black ${
                              p.rank === 1
                                ? 'bg-sandwich-100 text-sandwich-950 shadow-glow-white font-black'
                                : p.rank === 2
                                ? 'bg-sandwich-700 text-sandwich-100 border border-sandwich-600'
                                : p.rank === 3
                                ? 'bg-sandwich-800 text-sandwich-200 border border-sandwich-700'
                                : 'text-sandwich-400'
                            }`}
                          >
                            #{p.rank}
                          </span>
                        </td>
                        <td className="py-4 px-4">
                          <div className="flex items-center gap-3">
                            <div className="w-9 h-9 rounded-xl bg-sandwich-800 border border-sandwich-700 flex items-center justify-center font-bold text-xs text-sandwich-200 shrink-0">
                              {p.username.slice(0, 2).toUpperCase()}
                            </div>
                            <div>
                              <div className="flex items-center gap-2">
                                <span className="font-bold text-sandwich-100 truncate max-w-[150px] sm:max-w-none">
                                  {p.displayName || p.username}
                                </span>
                                {isCurrentUser && (
                                  <span className="text-[10px] font-mono px-1.5 py-0.5 rounded bg-sandwich-50 text-sandwich-950 font-black">
                                    YOU
                                  </span>
                                )}
                              </div>
                              <span className="text-xs font-mono text-sandwich-400">@{p.username}</span>
                            </div>
                          </div>
                        </td>
                        <td className="py-4 px-4 text-center font-mono">
                          <Badge variant="neutral" size="sm">Lvl {p.level}</Badge>
                        </td>
                        <td className="py-4 px-4 text-right font-mono font-black text-sandwich-100">
                          {p.rating}
                        </td>
                        <td className="py-4 px-4 text-center font-mono text-xs">
                          <span className={p.winRate >= 50 ? 'text-sandwich-100 font-bold' : 'text-sandwich-400'}>
                            {p.winRate}%
                          </span>
                          <span className="text-[10px] text-sandwich-500 block font-normal">
                            ({p.wins}W / {p.losses}L)
                          </span>
                        </td>
                        <td className="py-4 px-5 text-right font-mono font-bold text-sandwich-300">
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
              <div className="p-4 border-t border-sandwich-800 flex items-center justify-between bg-sandwich-950/70">
                <span className="text-xs font-mono text-sandwich-400">
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
