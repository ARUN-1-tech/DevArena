import React, { useEffect, useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { Badge } from '../../components/ui/Badge';
import {
  User,
  Check,
  Save,
  Trophy,
  Swords,
  Flame,
  CheckCircle2,
  TrendingUp,
  Sparkles,
  Loader2,
} from 'lucide-react';
import { analyticsService } from '../../services/analyticsService';
import { PlayerAnalytics } from '../../types/progression';

type ActiveSection = 'overview' | 'analytics' | 'settings';

export const ProfilePage: React.FC = () => {
  const { user, updateProfile } = useAuth();

  const [activeSection, setActiveSection] = useState<ActiveSection>('overview');
  const [displayName, setDisplayName] = useState(user?.displayName || '');
  const [bio, setBio] = useState(user?.bio || '');
  const [isSaving, setIsSaving] = useState(false);
  const [saveSuccess, setSaveSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [analytics, setAnalytics] = useState<PlayerAnalytics | null>(null);
  const [loadingAnalytics, setLoadingAnalytics] = useState(true);

  useEffect(() => {
    const fetchAnalytics = async () => {
      try {
        setLoadingAnalytics(true);
        const data = await analyticsService.getMyAnalytics();
        setAnalytics(data);
      } catch (err) {
        console.error('Failed to load player analytics:', err);
      } finally {
        setLoadingAnalytics(false);
      }
    };

    fetchAnalytics();
  }, []);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setIsSaving(true);
      setError(null);
      await updateProfile({
        displayName: displayName.trim(),
        bio: bio.trim(),
      });
      setSaveSuccess(true);
      setTimeout(() => setSaveSuccess(false), 3000);
    } catch (err: unknown) {
      const msg =
        err && typeof err === 'object' && 'message' in err
          ? (err as { message: string }).message
          : 'Failed to update profile.';
      setError(msg);
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="space-y-8 max-w-5xl mx-auto pb-16">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4">
        <div>
          <Badge variant="cyan" size="sm" className="mb-2">
            <User className="w-3.5 h-3.5 mr-1" />
            CHALLENGER DOSSIER
          </Badge>
          <h1 className="text-3xl font-black text-slate-900 tracking-tight">
            PLAYER PROFILE & ANALYTICS
          </h1>
          <p className="text-sm text-slate-600 mt-1">
            Track your competitive progression metrics, kata statistics, and combat logs.
          </p>
        </div>

        {/* Section Navigation */}
        <div className="flex items-center bg-slate-100 p-1.5 rounded-xl border border-slate-200/80 self-start sm:self-auto">
          {[
            { id: 'overview', label: 'Overview' },
            { id: 'analytics', label: 'Performance Analytics' },
            { id: 'settings', label: 'Account Settings' },
          ].map((sec) => (
            <button
              key={sec.id}
              onClick={() => setActiveSection(sec.id as ActiveSection)}
              className={`px-3.5 py-1.5 text-xs font-bold rounded-lg transition-all ${
                activeSection === sec.id
                  ? 'bg-white text-slate-900 shadow-sm'
                  : 'text-slate-500 hover:text-slate-800'
              }`}
            >
              {sec.label}
            </button>
          ))}
        </div>
      </div>

      {/* OVERVIEW SECTION */}
      {activeSection === 'overview' && (
        <div className="space-y-6">
          {/* Identity Banner */}
          <Card className="p-6 sm:p-8 bg-gradient-to-r from-slate-900 via-slate-900 to-cyan-950 text-white border-slate-800 rounded-3xl shadow-xl flex flex-col sm:flex-row items-center sm:items-start gap-6 relative overflow-hidden">
            <div className="w-24 h-24 rounded-2xl bg-gradient-to-tr from-cyan-500 to-blue-600 flex items-center justify-center font-black text-3xl shadow-lg border-2 border-cyan-400/40 text-white shrink-0">
              {(displayName || user?.username || 'P').slice(0, 2).toUpperCase()}
            </div>
            <div className="flex-1 text-center sm:text-left">
              <div className="flex flex-wrap items-center justify-center sm:justify-start gap-2">
                <h2 className="text-2xl font-black text-white">{displayName || user?.username}</h2>
                <Badge variant="cyan" size="sm">Level {analytics?.currentLevel || user?.progression?.level || 1}</Badge>
              </div>
              <p className="text-xs font-mono text-cyan-300 mt-1">@{user?.username}</p>
              <p className="text-xs text-slate-300 mt-3 max-w-xl leading-relaxed">
                {user?.bio || 'Ready for real-time competitive duels and kata challenges in DevArena.'}
              </p>
            </div>
            <div className="text-center sm:text-right border-t sm:border-t-0 sm:border-l border-slate-800 pt-4 sm:pt-0 sm:pl-6">
              <span className="text-[11px] font-mono uppercase text-slate-400 block font-bold">Current Rating</span>
              <span className="text-3xl font-black font-mono text-cyan-400">
                {analytics?.currentMmr || user?.stats?.rating || 1000} MMR
              </span>
            </div>
          </Card>

          {/* Quick KPI Cards Grid */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
            <Card className="p-5 bg-white border-slate-200 rounded-2xl shadow-sm text-center">
              <Trophy className="w-5 h-5 text-amber-500 mx-auto mb-2" />
              <span className="text-[10px] uppercase font-mono text-slate-400 font-bold block">Career XP</span>
              <span className="text-xl font-black font-mono text-slate-900">
                {analytics?.totalXp || 0} XP
              </span>
            </Card>

            <Card className="p-5 bg-white border-slate-200 rounded-2xl shadow-sm text-center">
              <CheckCircle2 className="w-5 h-5 text-emerald-500 mx-auto mb-2" />
              <span className="text-[10px] uppercase font-mono text-slate-400 font-bold block">Solved Katas</span>
              <span className="text-xl font-black font-mono text-slate-900">
                {analytics?.totalChallengesSolved || 0}
              </span>
            </Card>

            <Card className="p-5 bg-white border-slate-200 rounded-2xl shadow-sm text-center">
              <Swords className="w-5 h-5 text-cyan-500 mx-auto mb-2" />
              <span className="text-[10px] uppercase font-mono text-slate-400 font-bold block">Duel Win Rate</span>
              <span className="text-xl font-black font-mono text-slate-900">
                {analytics?.battleWinRate || 0}%
              </span>
            </Card>

            <Card className="p-5 bg-white border-slate-200 rounded-2xl shadow-sm text-center">
              <Flame className="w-5 h-5 text-rose-500 mx-auto mb-2" />
              <span className="text-[10px] uppercase font-mono text-slate-400 font-bold block">Active Streak</span>
              <span className="text-xl font-black font-mono text-slate-900">
                {analytics?.currentStreak || 0} 🔥
              </span>
            </Card>
          </div>

          {/* Top Mastered Skills Highlight */}
          {analytics?.topSkills && analytics.topSkills.length > 0 && (
            <Card className="p-6 bg-white border-slate-200 rounded-2xl shadow-sm">
              <h3 className="text-sm font-bold uppercase tracking-wider text-slate-400 font-mono mb-4 flex items-center gap-2">
                <Sparkles className="w-4 h-4 text-cyan-500" />
                TOP MASTERED DISCIPLINES
              </h3>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                {analytics.topSkills.map((sk) => (
                  <div key={sk.name} className="p-4 rounded-xl bg-slate-50 border border-slate-100">
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-xs font-bold text-slate-900">{sk.name}</span>
                      <Badge variant="cyan" size="sm">Lvl {sk.level}</Badge>
                    </div>
                    <div className="w-full bg-slate-200 rounded-full h-1.5 overflow-hidden mt-2">
                      <div
                        className="bg-cyan-600 h-1.5 rounded-full"
                        style={{ width: `${sk.masteryPercentage}%` }}
                      />
                    </div>
                    <span className="text-[10px] font-mono text-slate-400 mt-1 block text-right font-medium">
                      {sk.masteryPercentage}% mastery
                    </span>
                  </div>
                ))}
              </div>
            </Card>
          )}
        </div>
      )}

      {/* ANALYTICS & PERFORMANCE SECTION */}
      {activeSection === 'analytics' && (
        <div className="space-y-6">
          {loadingAnalytics ? (
            <div className="py-20 text-center text-slate-400">
              <Loader2 className="w-8 h-8 animate-spin mx-auto mb-2 text-cyan-600" />
              <span className="text-sm">Calculating performance telemetry...</span>
            </div>
          ) : analytics ? (
            <>
              {/* Challenge Breakdown & Battle Win Rate */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* Challenge Distribution */}
                <Card className="p-6 bg-white border-slate-200 rounded-2xl shadow-sm">
                  <h3 className="text-sm font-bold uppercase tracking-wider text-slate-400 font-mono mb-4">
                    KATA DIFFICULTY SPREAD
                  </h3>
                  <div className="space-y-4">
                    <div>
                      <div className="flex justify-between text-xs font-mono mb-1.5">
                        <span className="font-bold text-emerald-600">EASY</span>
                        <span className="font-bold text-slate-700">{analytics.difficultyDistribution['EASY'] || 0} solved</span>
                      </div>
                      <div className="w-full bg-slate-100 rounded-full h-2">
                        <div
                          className="bg-emerald-500 h-2 rounded-full"
                          style={{
                            width: `${Math.min(100, ((analytics.difficultyDistribution['EASY'] || 0) / Math.max(1, analytics.totalChallengesSolved)) * 100)}%`,
                          }}
                        />
                      </div>
                    </div>

                    <div>
                      <div className="flex justify-between text-xs font-mono mb-1.5">
                        <span className="font-bold text-amber-600">MEDIUM</span>
                        <span className="font-bold text-slate-700">{analytics.difficultyDistribution['MEDIUM'] || 0} solved</span>
                      </div>
                      <div className="w-full bg-slate-100 rounded-full h-2">
                        <div
                          className="bg-amber-500 h-2 rounded-full"
                          style={{
                            width: `${Math.min(100, ((analytics.difficultyDistribution['MEDIUM'] || 0) / Math.max(1, analytics.totalChallengesSolved)) * 100)}%`,
                          }}
                        />
                      </div>
                    </div>

                    <div>
                      <div className="flex justify-between text-xs font-mono mb-1.5">
                        <span className="font-bold text-rose-600">HARD</span>
                        <span className="font-bold text-slate-700">{analytics.difficultyDistribution['HARD'] || 0} solved</span>
                      </div>
                      <div className="w-full bg-slate-100 rounded-full h-2">
                        <div
                          className="bg-rose-500 h-2 rounded-full"
                          style={{
                            width: `${Math.min(100, ((analytics.difficultyDistribution['HARD'] || 0) / Math.max(1, analytics.totalChallengesSolved)) * 100)}%`,
                          }}
                        />
                      </div>
                    </div>
                  </div>

                  <div className="mt-6 pt-4 border-t border-slate-100 grid grid-cols-2 text-center text-xs font-mono">
                    <div>
                      <span className="text-slate-400 block text-[10px] uppercase">Attempted</span>
                      <span className="font-bold text-slate-800 text-sm">{analytics.totalChallengesAttempted}</span>
                    </div>
                    <div>
                      <span className="text-slate-400 block text-[10px] uppercase">Solve Efficiency</span>
                      <span className="font-bold text-cyan-700 text-sm">{analytics.challengeSolveRate}%</span>
                    </div>
                  </div>
                </Card>

                {/* 1v1 Battle Record */}
                <Card className="p-6 bg-white border-slate-200 rounded-2xl shadow-sm">
                  <h3 className="text-sm font-bold uppercase tracking-wider text-slate-400 font-mono mb-4">
                    1v1 ARENA COMBAT LOG
                  </h3>
                  <div className="grid grid-cols-3 gap-3 text-center mb-6">
                    <div className="p-3 bg-emerald-50 rounded-xl border border-emerald-100">
                      <span className="text-[10px] font-mono uppercase text-emerald-600 font-bold block">Victories</span>
                      <span className="text-xl font-black font-mono text-emerald-700">{analytics.battleWins}</span>
                    </div>
                    <div className="p-3 bg-rose-50 rounded-xl border border-rose-100">
                      <span className="text-[10px] font-mono uppercase text-rose-600 font-bold block">Defeats</span>
                      <span className="text-xl font-black font-mono text-rose-700">{analytics.battleLosses}</span>
                    </div>
                    <div className="p-3 bg-slate-100 rounded-xl border border-slate-200">
                      <span className="text-[10px] font-mono uppercase text-slate-500 font-bold block">Draws</span>
                      <span className="text-xl font-black font-mono text-slate-700">{analytics.battleDraws}</span>
                    </div>
                  </div>

                  <div className="p-4 bg-slate-50 rounded-xl border border-slate-100 space-y-2 text-xs font-mono">
                    <div className="flex justify-between">
                      <span className="text-slate-500">Overall Win Rate</span>
                      <span className="font-bold text-slate-900">{analytics.battleWinRate}%</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-slate-500">MMR Delta (30 Days)</span>
                      <span className={`font-bold ${analytics.mmrChange30Days >= 0 ? 'text-emerald-600' : 'text-rose-600'}`}>
                        {analytics.mmrChange30Days >= 0 ? `+${analytics.mmrChange30Days}` : analytics.mmrChange30Days} MMR
                      </span>
                    </div>
                  </div>
                </Card>
              </div>

              {/* 14-Day Activity Sparkline/Bar Chart */}
              <Card className="p-6 bg-white border-slate-200 rounded-2xl shadow-sm">
                <div className="flex items-center justify-between mb-5">
                  <h3 className="text-sm font-bold uppercase tracking-wider text-slate-400 font-mono flex items-center gap-2">
                    <TrendingUp className="w-4 h-4 text-cyan-600" />
                    14-DAY XP VELOCITY
                  </h3>
                  <span className="text-xs font-mono text-slate-400">Daily Gain</span>
                </div>

                <div className="flex items-end justify-between gap-2 h-32 pt-6 px-2">
                  {analytics.dailyXpTrend.map((d) => {
                    const max = Math.max(100, ...analytics.dailyXpTrend.map((t) => t.xpEarned));
                    const heightPercent = Math.max(8, (d.xpEarned / max) * 100);
                    return (
                      <div key={d.date} className="flex-1 flex flex-col items-center gap-1 group relative">
                        {/* Tooltip on hover */}
                        <div className="absolute -top-7 opacity-0 group-hover:opacity-100 transition-opacity bg-slate-900 text-white text-[10px] font-mono px-1.5 py-0.5 rounded pointer-events-none whitespace-nowrap z-10">
                          {d.xpEarned} XP
                        </div>
                        <div
                          className={`w-full rounded-t-md transition-all ${
                            d.xpEarned > 0
                              ? 'bg-gradient-to-t from-cyan-600 to-cyan-400 group-hover:from-cyan-500 group-hover:to-cyan-300'
                              : 'bg-slate-100'
                          }`}
                          style={{ height: `${heightPercent}%` }}
                        />
                        <span className="text-[9px] font-mono text-slate-400 truncate max-w-[28px]">
                          {d.date.slice(5)}
                        </span>
                      </div>
                    );
                  })}
                </div>
              </Card>

              {/* Recent Battles List */}
              {analytics.recentBattles && analytics.recentBattles.length > 0 && (
                <Card className="p-6 bg-white border-slate-200 rounded-2xl shadow-sm">
                  <h3 className="text-sm font-bold uppercase tracking-wider text-slate-400 font-mono mb-4">
                    RECENT DUEL ENGAGEMENTS
                  </h3>
                  <div className="divide-y divide-slate-100">
                    {analytics.recentBattles.map((b, idx) => (
                      <div key={idx} className="py-3 flex items-center justify-between text-xs">
                        <div className="flex items-center gap-3">
                          <span
                            className={`px-2 py-0.5 rounded font-mono font-bold uppercase text-[10px] ${
                              b.outcome === 'WIN'
                                ? 'bg-emerald-100 text-emerald-800'
                                : b.outcome === 'LOSS'
                                ? 'bg-rose-100 text-rose-800'
                                : 'bg-slate-100 text-slate-700'
                            }`}
                          >
                            {b.outcome}
                          </span>
                          <div>
                            <span className="font-bold text-slate-900 block truncate max-w-[200px] sm:max-w-none">
                              vs. @{b.opponentUsername}
                            </span>
                            <span className="text-slate-400 text-[11px] font-mono">{b.challengeTitle}</span>
                          </div>
                        </div>
                        <div className="text-right font-mono">
                          <span className={`font-bold ${b.ratingDelta >= 0 ? 'text-emerald-600' : 'text-rose-600'}`}>
                            {b.ratingDelta >= 0 ? `+${b.ratingDelta}` : b.ratingDelta} MMR
                          </span>
                          <span className="text-slate-400 text-[10px] block">+{b.xpEarned} XP</span>
                        </div>
                      </div>
                    ))}
                  </div>
                </Card>
              )}
            </>
          ) : null}
        </div>
      )}

      {/* SETTINGS SECTION */}
      {activeSection === 'settings' && (
        <Card className="p-6 sm:p-8 bg-white border-slate-200/90 shadow-sm rounded-2xl">
          <form onSubmit={handleSave} className="space-y-5">
            <div className="flex items-center gap-4 pb-6 border-b border-slate-100">
              <div className="w-16 h-16 rounded-2xl bg-gradient-to-tr from-cyan-600 to-violet-600 text-white flex items-center justify-center text-xl font-black shadow-md">
                {(displayName || user?.username || 'P').slice(0, 2).toUpperCase()}
              </div>
              <div>
                <h3 className="text-lg font-bold text-slate-900">
                  {user?.displayName || user?.username}
                </h3>
                <p className="text-xs text-slate-500 font-mono">
                  {user?.email} • Member since Season 01
                </p>
              </div>
            </div>

            {error && (
              <div className="p-3 bg-rose-50 border border-rose-200 text-rose-700 text-xs rounded-xl font-medium">
                {error}
              </div>
            )}

            {saveSuccess && (
              <div className="p-3 bg-emerald-50 border border-emerald-200 text-emerald-700 text-xs rounded-xl font-medium flex items-center gap-1.5">
                <Check className="w-4 h-4" />
                Profile updated successfully.
              </div>
            )}

            <div className="space-y-1.5">
              <label className="text-xs font-bold font-mono text-slate-700 uppercase tracking-wider">
                Display Name
              </label>
              <Input
                type="text"
                value={displayName}
                onChange={(e) => setDisplayName(e.target.value)}
                placeholder="e.g. CodeSensei"
                maxLength={50}
                required
              />
            </div>

            <div className="space-y-1.5">
              <label className="text-xs font-bold font-mono text-slate-700 uppercase tracking-wider">
                Bio / Status Motto
              </label>
              <textarea
                value={bio}
                onChange={(e) => setBio(e.target.value)}
                placeholder="Brief intro for opponents and leaderboard..."
                maxLength={255}
                rows={3}
                className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 bg-white text-slate-900 text-sm focus:outline-none focus:ring-2 focus:ring-cyan-500/20 focus:border-cyan-500 transition-all resize-none"
              />
            </div>

            <div className="pt-4 flex justify-end">
              <Button
                type="submit"
                variant="primary"
                isLoading={isSaving}
                className="gap-2 px-6"
              >
                <Save className="w-4 h-4" />
                Save Changes
              </Button>
            </div>
          </form>
        </Card>
      )}
    </div>
  );
};
