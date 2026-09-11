import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Swords,
  UserPlus,
  UserCheck,
  Clock,
  Trophy,
  CheckCircle2,
  Flame,
  Award,
  Zap,
  Sparkles,
  Loader2,
} from 'lucide-react';
import { Card } from '../../components/ui/Card';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { playerService } from '../../services/playerService';
import { friendService } from '../../services/friendService';
import { PublicPlayerProfile } from '../../types/social';

export const PublicProfilePage: React.FC = () => {
  const { username } = useParams<{ username: string }>();
  const navigate = useNavigate();

  const [profile, setProfile] = useState<PublicPlayerProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actionLoading, setActionLoading] = useState(false);
  const [actionMessage, setActionMessage] = useState<string | null>(null);

  useEffect(() => {
    if (!username) return;

    const fetchProfile = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await playerService.getPublicProfile(username);
        setProfile(data);
      } catch (err: any) {
        setError(err.response?.data?.message || 'Player profile not found');
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [username]);

  const handleSendFriendRequest = async () => {
    if (!profile) return;
    try {
      setActionLoading(true);
      await friendService.sendFriendRequest(profile.id);
      setProfile((prev) => (prev ? { ...prev, hasPendingRequest: true } : null));
      setActionMessage('Friend request sent!');
    } catch (e: any) {
      setActionMessage(e.response?.data?.message || 'Failed to send friend request');
    } finally {
      setActionLoading(false);
      setTimeout(() => setActionMessage(null), 4000);
    }
  };

  const handleChallenge = async () => {
    if (!profile) return;
    try {
      setActionLoading(true);
      await friendService.challengeFriend(profile.id);
      setActionMessage(`1v1 Duel invitation dispatched to ${profile.username}!`);
    } catch (e: any) {
      setActionMessage(e.response?.data?.message || 'Failed to challenge player');
    } finally {
      setActionLoading(false);
      setTimeout(() => setActionMessage(null), 5000);
    }
  };

  if (loading) {
    return (
      <div className="py-24 text-center text-slate-400">
        <Loader2 className="w-8 h-8 animate-spin mx-auto mb-3 text-cyan-600" />
        <p className="text-sm font-medium">Summoning gladiator dossier...</p>
      </div>
    );
  }

  if (error || !profile) {
    return (
      <div className="max-w-md mx-auto py-20 text-center space-y-4">
        <div className="w-16 h-16 rounded-2xl bg-rose-50 text-rose-600 flex items-center justify-center mx-auto">
          <Swords className="w-8 h-8" />
        </div>
        <h2 className="text-xl font-bold text-slate-900">Player Not Found</h2>
        <p className="text-xs text-slate-500">{error || "This gladiator doesn't exist in the arena."}</p>
        <Button variant="outline" onClick={() => navigate('/friends')} leftIcon={<ArrowLeft className="w-4 h-4" />}>
          Back to Friends
        </Button>
      </div>
    );
  }

  return (
    <div className="space-y-8 max-w-5xl mx-auto pb-12">
      {/* Back button */}
      <div>
        <button
          onClick={() => navigate(-1)}
          className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-500 hover:text-slate-900 transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back</span>
        </button>
      </div>

      {/* Action Notification */}
      {actionMessage && (
        <div className="p-3 bg-cyan-50 border border-cyan-200 text-cyan-800 text-xs font-semibold rounded-xl flex items-center gap-2 shadow-xs">
          <Sparkles className="w-4 h-4 text-cyan-600 shrink-0" />
          <span>{actionMessage}</span>
        </div>
      )}

      {/* HERO BANNER */}
      <Card className="p-8 bg-gradient-to-br from-white via-slate-50/60 to-cyan-50/30 border-slate-200 shadow-sm rounded-3xl relative overflow-hidden">
        <div className="flex flex-col md:flex-row items-center md:items-start gap-6 relative z-10 text-center md:text-left">
          {/* Avatar with presence */}
          <div className="relative">
            <div className="w-24 h-24 rounded-3xl bg-gradient-to-tr from-cyan-600 to-violet-600 text-white font-black text-3xl flex items-center justify-center shadow-lg border-4 border-white">
              {profile.displayName ? profile.displayName.slice(0, 2).toUpperCase() : profile.username.slice(0, 2).toUpperCase()}
            </div>
            {profile.online && (
              <span
                className="absolute bottom-1 right-1 w-5 h-5 rounded-full bg-emerald-500 border-3 border-white ring-2 ring-emerald-400/50"
                title="Online"
              />
            )}
          </div>

          {/* Details */}
          <div className="flex-1 min-w-0">
            <div className="flex flex-wrap items-center justify-center md:justify-start gap-2 mb-1">
              <h1 className="text-2xl font-black text-slate-900 tracking-tight">
                {profile.displayName || profile.username}
              </h1>
              <span className="text-sm font-mono text-slate-400">@{profile.username}</span>
              <Badge variant="cyan" size="sm">
                Rank #{profile.rank}
              </Badge>
            </div>

            {profile.bio && (
              <p className="text-xs text-slate-600 max-w-xl mb-3 leading-relaxed">
                {profile.bio}
              </p>
            )}

            {/* Quick Metrics */}
            <div className="flex flex-wrap items-center justify-center md:justify-start gap-4 text-xs font-mono text-slate-600 pt-1">
              <div className="flex items-center gap-1.5 px-3 py-1 rounded-xl bg-slate-100 border border-slate-200">
                <Sparkles className="w-3.5 h-3.5 text-cyan-600" />
                <span className="font-bold text-slate-900">Level {profile.level}</span>
              </div>
              <div className="flex items-center gap-1.5 px-3 py-1 rounded-xl bg-cyan-50 border border-cyan-200 text-cyan-800">
                <Flame className="w-3.5 h-3.5 text-cyan-600" />
                <span className="font-bold">{profile.rating} MMR</span>
              </div>
              <div className="flex items-center gap-1.5 px-3 py-1 rounded-xl bg-slate-100 border border-slate-200">
                <Trophy className="w-3.5 h-3.5 text-amber-500" />
                <span className="font-bold">{profile.totalXp.toLocaleString()} XP</span>
              </div>
            </div>
          </div>

          {/* Actions */}
          <div className="flex flex-col gap-2 shrink-0 w-full sm:w-auto">
            <Button
              variant="glow"
              onClick={handleChallenge}
              isLoading={actionLoading}
              leftIcon={<Swords className="w-4 h-4" />}
            >
              Challenge
            </Button>

            {profile.isFriend ? (
              <Button variant="outline" disabled leftIcon={<UserCheck className="w-4 h-4 text-emerald-600" />}>
                Friends
              </Button>
            ) : profile.hasPendingRequest ? (
              <Button variant="outline" disabled leftIcon={<Clock className="w-4 h-4 text-slate-400" />}>
                Request Sent
              </Button>
            ) : (
              <Button
                variant="outline"
                onClick={handleSendFriendRequest}
                isLoading={actionLoading}
                leftIcon={<UserPlus className="w-4 h-4" />}
              >
                Add Friend
              </Button>
            )}
          </div>
        </div>
      </Card>

      {/* STATS OVERVIEW GRID */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <Card className="p-4 bg-white border-slate-200 rounded-2xl">
          <span className="text-[10px] font-mono text-slate-400 font-bold block mb-1">
            KATAS SOLVED
          </span>
          <div className="flex items-baseline gap-2">
            <span className="text-2xl font-black text-slate-900">{profile.solvedChallenges}</span>
            <CheckCircle2 className="w-4 h-4 text-emerald-500" />
          </div>
        </Card>

        <Card className="p-4 bg-white border-slate-200 rounded-2xl">
          <span className="text-[10px] font-mono text-slate-400 font-bold block mb-1">
            BATTLES WON
          </span>
          <div className="flex items-baseline gap-2">
            <span className="text-2xl font-black text-emerald-600">{profile.battleWins}</span>
            <span className="text-xs text-slate-400 font-mono">/ {profile.battleWins + profile.battleLosses}</span>
          </div>
        </Card>

        <Card className="p-4 bg-white border-slate-200 rounded-2xl">
          <span className="text-[10px] font-mono text-slate-400 font-bold block mb-1">
            WIN RATE
          </span>
          <div className="flex items-baseline gap-2">
            <span className="text-2xl font-black text-cyan-700">{profile.battleWinRate}%</span>
          </div>
        </Card>

        <Card className="p-4 bg-white border-slate-200 rounded-2xl">
          <span className="text-[10px] font-mono text-slate-400 font-bold block mb-1">
            WIN STREAK
          </span>
          <div className="flex items-baseline gap-2">
            <span className="text-2xl font-black text-amber-500">{profile.winStreak}</span>
            <Flame className="w-4 h-4 text-amber-500" />
          </div>
        </Card>
      </div>

      {/* SKILLS & ACHIEVEMENTS TABS */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Top Skills */}
        <Card className="p-6 bg-white border-slate-200 rounded-2xl space-y-4">
          <div className="flex items-center gap-2">
            <Zap className="w-4 h-4 text-cyan-600" />
            <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wider font-mono">
              Top Technical Disciplines
            </h3>
          </div>

          {profile.topSkills.length === 0 ? (
            <p className="text-xs text-slate-400 py-6 text-center">No skill progression unlocked yet.</p>
          ) : (
            <div className="space-y-3">
              {profile.topSkills.map((s) => (
                <div key={s.code} className="space-y-1.5">
                  <div className="flex justify-between text-xs font-mono">
                    <span className="font-bold text-slate-800">{s.name}</span>
                    <span className="text-cyan-700 font-bold">Lvl {s.level} ({s.masteryPercentage}%)</span>
                  </div>
                  <div className="w-full bg-slate-100 rounded-full h-2">
                    <div
                      className="bg-cyan-600 h-2 rounded-full transition-all"
                      style={{ width: `${s.masteryPercentage}%` }}
                    />
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>

        {/* Unlocked Achievements */}
        <Card className="p-6 bg-white border-slate-200 rounded-2xl space-y-4">
          <div className="flex items-center gap-2">
            <Award className="w-4 h-4 text-amber-500" />
            <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wider font-mono">
              Achievements ({profile.achievements.length})
            </h3>
          </div>

          {profile.achievements.length === 0 ? (
            <p className="text-xs text-slate-400 py-6 text-center">No achievements unlocked yet.</p>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5">
              {profile.achievements.map((a) => (
                <div
                  key={a.code}
                  className="p-2.5 rounded-xl border border-slate-200 bg-slate-50/50 flex items-center gap-2.5"
                >
                  <div className="w-8 h-8 rounded-lg bg-amber-100 text-amber-700 flex items-center justify-center shrink-0">
                    <Award className="w-4 h-4" />
                  </div>
                  <div className="min-w-0">
                    <p className="text-xs font-bold text-slate-900 truncate">{a.name}</p>
                    <span className="text-[9px] font-mono text-slate-400 uppercase font-bold">{a.rarity}</span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      </div>

      {/* RECENT PUBLIC ACTIVITY */}
      {profile.recentActivities.length > 0 && (
        <Card className="p-6 bg-white border-slate-200 rounded-2xl space-y-3">
          <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wider font-mono">
            Recent Public Feats
          </h3>
          <div className="divide-y divide-slate-100">
            {profile.recentActivities.map((act, idx) => (
              <div key={idx} className="py-2.5 flex items-center justify-between gap-2 text-xs">
                <div className="flex items-center gap-2 text-slate-700">
                  <span className="w-1.5 h-1.5 rounded-full bg-cyan-500" />
                  <span>{act.description}</span>
                </div>
                <span className="text-[10px] font-mono text-slate-400 shrink-0">
                  {new Date(act.timestamp).toLocaleDateString()}
                </span>
              </div>
            ))}
          </div>
        </Card>
      )}
    </div>
  );
};
