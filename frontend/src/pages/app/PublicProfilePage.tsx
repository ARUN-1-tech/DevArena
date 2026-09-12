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
          className="inline-flex items-center gap-1.5 text-xs font-semibold text-sandwich-400 hover:text-sandwich-100 transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back</span>
        </button>
      </div>

      {/* Action Notification */}
      {actionMessage && (
        <div className="p-3 bg-sandwich-800 border border-sandwich-700 text-sandwich-100 text-xs font-semibold rounded-xl flex items-center gap-2 shadow-luxury">
          <Sparkles className="w-4 h-4 text-sandwich-200 shrink-0" />
          <span>{actionMessage}</span>
        </div>
      )}

      {/* HERO BANNER */}
      <Card className="p-8 bg-sandwich-900/95 border-sandwich-700 shadow-luxury rounded-3xl relative overflow-hidden backdrop-blur-xl">
        <div className="flex flex-col md:flex-row items-center md:items-start gap-6 relative z-10 text-center md:text-left">
          {/* Avatar with presence */}
          <div className="relative">
            <div className="w-24 h-24 rounded-3xl bg-sandwich-800 border-2 border-sandwich-600 text-sandwich-100 font-black text-3xl flex items-center justify-center shadow-glow-silver">
              {profile.displayName ? profile.displayName.slice(0, 2).toUpperCase() : profile.username.slice(0, 2).toUpperCase()}
            </div>
            {profile.online && (
              <span
                className="absolute bottom-1 right-1 w-5 h-5 rounded-full bg-emerald-500 border-3 border-sandwich-950 ring-2 ring-emerald-400/50"
                title="Online"
              />
            )}
          </div>

          {/* Details */}
          <div className="flex-1 min-w-0">
            <div className="flex flex-wrap items-center justify-center md:justify-start gap-2 mb-1">
              <h1 className="text-2xl font-black text-sandwich-100 tracking-tight">
                {profile.displayName || profile.username}
              </h1>
              <span className="text-sm font-mono text-sandwich-400">@{profile.username}</span>
              <Badge variant="neutral" size="sm" className="bg-sandwich-800 text-sandwich-200 border-sandwich-700">
                Rank #{profile.rank}
              </Badge>
            </div>

            {profile.bio && (
              <p className="text-xs text-sandwich-300 max-w-xl mb-3 leading-relaxed">
                {profile.bio}
              </p>
            )}

            {/* Quick Metrics */}
            <div className="flex flex-wrap items-center justify-center md:justify-start gap-4 text-xs font-mono text-sandwich-300 pt-1">
              <div className="flex items-center gap-1.5 px-3 py-1 rounded-xl bg-sandwich-800 border border-sandwich-700">
                <Sparkles className="w-3.5 h-3.5 text-sandwich-200" />
                <span className="font-bold text-sandwich-100">Level {profile.level}</span>
              </div>
              <div className="flex items-center gap-1.5 px-3 py-1 rounded-xl bg-sandwich-800 border border-sandwich-700 text-sandwich-100">
                <Flame className="w-3.5 h-3.5 text-sandwich-200" />
                <span className="font-bold">{profile.rating} MMR</span>
              </div>
              <div className="flex items-center gap-1.5 px-3 py-1 rounded-xl bg-sandwich-800 border border-sandwich-700">
                <Trophy className="w-3.5 h-3.5 text-sandwich-200" />
                <span className="font-bold text-sandwich-100">{profile.totalXp.toLocaleString()} XP</span>
              </div>
            </div>
          </div>

          {/* Actions */}
          <div className="flex flex-col gap-2 shrink-0 w-full sm:w-auto">
            <Button
              variant="primary"
              onClick={handleChallenge}
              isLoading={actionLoading}
              leftIcon={<Swords className="w-4 h-4" />}
            >
              Challenge
            </Button>

            {profile.isFriend ? (
              <Button variant="secondary" disabled leftIcon={<UserCheck className="w-4 h-4 text-emerald-400" />}>
                Friends
              </Button>
            ) : profile.hasPendingRequest ? (
              <Button variant="secondary" disabled leftIcon={<Clock className="w-4 h-4 text-sandwich-500" />}>
                Request Sent
              </Button>
            ) : (
              <Button
                variant="secondary"
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
        <Card className="p-4 bg-sandwich-900/90 border-sandwich-800 rounded-2xl shadow-luxury">
          <span className="text-[10px] font-mono text-sandwich-500 font-bold block mb-1">
            KATAS SOLVED
          </span>
          <div className="flex items-baseline gap-2">
            <span className="text-2xl font-black text-sandwich-100">{profile.solvedChallenges}</span>
            <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          </div>
        </Card>

        <Card className="p-4 bg-sandwich-900/90 border-sandwich-800 rounded-2xl shadow-luxury">
          <span className="text-[10px] font-mono text-sandwich-500 font-bold block mb-1">
            BATTLES WON
          </span>
          <div className="flex items-baseline gap-2">
            <span className="text-2xl font-black text-emerald-400">{profile.battleWins}</span>
            <span className="text-xs text-sandwich-500 font-mono">/ {profile.battleWins + profile.battleLosses}</span>
          </div>
        </Card>

        <Card className="p-4 bg-sandwich-900/90 border-sandwich-800 rounded-2xl shadow-luxury">
          <span className="text-[10px] font-mono text-sandwich-500 font-bold block mb-1">
            WIN RATE
          </span>
          <div className="flex items-baseline gap-2">
            <span className="text-2xl font-black text-sandwich-100">{profile.battleWinRate}%</span>
          </div>
        </Card>

        <Card className="p-4 bg-sandwich-900/90 border-sandwich-800 rounded-2xl shadow-luxury">
          <span className="text-[10px] font-mono text-sandwich-500 font-bold block mb-1">
            WIN STREAK
          </span>
          <div className="flex items-baseline gap-2">
            <span className="text-2xl font-black text-sandwich-100">{profile.winStreak}</span>
            <Flame className="w-4 h-4 text-sandwich-200" />
          </div>
        </Card>
      </div>

      {/* SKILLS & ACHIEVEMENTS TABS */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Top Skills */}
        <Card className="p-6 bg-sandwich-900/90 border-sandwich-800 rounded-2xl space-y-4 shadow-luxury">
          <div className="flex items-center gap-2">
            <Zap className="w-4 h-4 text-sandwich-200" />
            <h3 className="text-sm font-bold text-sandwich-100 uppercase tracking-wider font-mono">
              Top Technical Disciplines
            </h3>
          </div>

          {profile.topSkills.length === 0 ? (
            <p className="text-xs text-sandwich-500 py-6 text-center">No skill progression unlocked yet.</p>
          ) : (
            <div className="space-y-3">
              {profile.topSkills.map((s) => (
                <div key={s.code} className="space-y-1.5">
                  <div className="flex justify-between text-xs font-mono">
                    <span className="font-bold text-sandwich-200">{s.name}</span>
                    <span className="text-sandwich-100 font-bold">Lvl {s.level} ({s.masteryPercentage}%)</span>
                  </div>
                  <div className="w-full bg-sandwich-950 rounded-full h-2 border border-sandwich-800/50">
                    <div
                      className="bg-sandwich-100 h-2 rounded-full transition-all shadow-glow-white"
                      style={{ width: `${s.masteryPercentage}%` }}
                    />
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>

        {/* Unlocked Achievements */}
        <Card className="p-6 bg-sandwich-900/90 border-sandwich-800 rounded-2xl space-y-4 shadow-luxury">
          <div className="flex items-center gap-2">
            <Award className="w-4 h-4 text-sandwich-200" />
            <h3 className="text-sm font-bold text-sandwich-100 uppercase tracking-wider font-mono">
              Achievements ({profile.achievements.length})
            </h3>
          </div>

          {profile.achievements.length === 0 ? (
            <p className="text-xs text-sandwich-500 py-6 text-center">No achievements unlocked yet.</p>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5">
              {profile.achievements.map((a) => (
                <div
                  key={a.code}
                  className="p-2.5 rounded-xl border border-sandwich-800 bg-sandwich-950/80 flex items-center gap-2.5"
                >
                  <div className="w-8 h-8 rounded-lg bg-sandwich-800 border border-sandwich-700 text-sandwich-100 flex items-center justify-center shrink-0">
                    <Award className="w-4 h-4" />
                  </div>
                  <div className="min-w-0">
                    <p className="text-xs font-bold text-sandwich-100 truncate">{a.name}</p>
                    <span className="text-[9px] font-mono text-sandwich-500 uppercase font-bold">{a.rarity}</span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      </div>

      {/* RECENT PUBLIC ACTIVITY */}
      {profile.recentActivities.length > 0 && (
        <Card className="p-6 bg-sandwich-900/90 border-sandwich-800 rounded-2xl space-y-3 shadow-luxury">
          <h3 className="text-sm font-bold text-sandwich-100 uppercase tracking-wider font-mono">
            Recent Public Feats
          </h3>
          <div className="divide-y divide-sandwich-800/80">
            {profile.recentActivities.map((act, idx) => (
              <div key={idx} className="py-2.5 flex items-center justify-between gap-2 text-xs">
                <div className="flex items-center gap-2 text-sandwich-300">
                  <span className="w-1.5 h-1.5 rounded-full bg-sandwich-100" />
                  <span>{act.description}</span>
                </div>
                <span className="text-[10px] font-mono text-sandwich-500 shrink-0">
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
