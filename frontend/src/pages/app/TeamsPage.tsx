import React, { useEffect, useState, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Shield,
  UserPlus,
  Crown,
  Search,
  Check,
  X,
  LogOut,
  UserX,
  PlusCircle,
  Loader2,
  Flame,
} from 'lucide-react';
import { Card } from '../../components/ui/Card';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { teamService } from '../../services/teamService';
import { playerService } from '../../services/playerService';
import { useAuth } from '../../contexts/AuthContext';
import { Team, TeamLeaderboardItem, PlayerSearchResult } from '../../types/social';

type TeamTab = 'my-team' | 'browse' | 'leaderboard';

export const TeamsPage: React.FC = () => {
  const { user } = useAuth();

  const [activeTab, setActiveTab] = useState<TeamTab>('my-team');
  const [myTeam, setMyTeam] = useState<Team | null>(null);
  const [loadingMyTeam, setLoadingMyTeam] = useState(true);

  // Create Team modal/form state
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [teamName, setTeamName] = useState('');
  const [teamDesc, setTeamDesc] = useState('');
  const [creating, setCreating] = useState(false);

  // Invite player state
  const [showInviteModal, setShowInviteModal] = useState(false);
  const [inviteSearch, setInviteSearch] = useState('');
  const [inviteSearchResults, setInviteSearchResults] = useState<PlayerSearchResult[]>([]);
  const [invitingId, setInvitingId] = useState<string | null>(null);

  // Browse & Leaderboard state
  const [browseQuery, setBrowseQuery] = useState('');
  const [browseTeams, setBrowseTeams] = useState<Team[]>([]);
  const [loadingBrowse, setLoadingBrowse] = useState(false);
  const [leaderboard, setLeaderboard] = useState<TeamLeaderboardItem[]>([]);
  const [loadingLeaderboard, setLoadingLeaderboard] = useState(false);

  // Notification Toast
  const [statusMessage, setStatusMessage] = useState<string | null>(null);

  const fetchMyTeam = useCallback(async () => {
    try {
      setLoadingMyTeam(true);
      const team = await teamService.getMyTeam();
      setMyTeam(team);
    } catch (e) {
      console.error('Failed to fetch my team', e);
    } finally {
      setLoadingMyTeam(false);
    }
  }, []);

  const fetchBrowseTeams = useCallback(async (q = '') => {
    try {
      setLoadingBrowse(true);
      const teams = await teamService.searchTeams(q, 0, 20);
      setBrowseTeams(teams);
    } catch (e) {
      console.error('Failed to browse teams', e);
    } finally {
      setLoadingBrowse(false);
    }
  }, []);

  const fetchLeaderboard = useCallback(async () => {
    try {
      setLoadingLeaderboard(true);
      const data = await teamService.getTeamLeaderboard(0, 25);
      setLeaderboard(data.content);
    } catch (e) {
      console.error('Failed to fetch team leaderboard', e);
    } finally {
      setLoadingLeaderboard(false);
    }
  }, []);

  useEffect(() => {
    fetchMyTeam();
  }, [fetchMyTeam]);

  useEffect(() => {
    if (activeTab === 'browse') {
      fetchBrowseTeams(browseQuery);
    } else if (activeTab === 'leaderboard') {
      fetchLeaderboard();
    }
  }, [activeTab, browseQuery, fetchBrowseTeams, fetchLeaderboard]);

  // Handle player search for invite
  useEffect(() => {
    if (!inviteSearch.trim()) {
      setInviteSearchResults([]);
      return;
    }
    const timer = setTimeout(async () => {
      try {
        const results = await playerService.searchPlayers(inviteSearch.trim());
        setInviteSearchResults(results);
      } catch (e) {
        console.error('Invite search failed', e);
      }
    }, 300);
    return () => clearTimeout(timer);
  }, [inviteSearch]);

  const handleCreateTeam = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!teamName.trim()) return;
    try {
      setCreating(true);
      const newTeam = await teamService.createTeam({
        name: teamName.trim(),
        description: teamDesc.trim(),
        maxMembers: 10,
      });
      setMyTeam(newTeam);
      setShowCreateForm(false);
      setStatusMessage(`Team "${newTeam.name}" founded successfully!`);
    } catch (e: any) {
      setStatusMessage(e.response?.data?.message || 'Failed to create team');
    } finally {
      setCreating(false);
      setTimeout(() => setStatusMessage(null), 4000);
    }
  };

  const handleInvitePlayer = async (playerId: string) => {
    if (!myTeam) return;
    try {
      setInvitingId(playerId);
      await teamService.invitePlayer(myTeam.id, playerId);
      setStatusMessage('Invitation sent to player!');
      setShowInviteModal(false);
    } catch (e: any) {
      setStatusMessage(e.response?.data?.message || 'Failed to send invite');
    } finally {
      setInvitingId(null);
      setTimeout(() => setStatusMessage(null), 4000);
    }
  };

  const handleRemoveMember = async (memberUserId: string, username: string) => {
    if (!myTeam) return;
    if (!window.confirm(`Are you sure you want to remove ${username} from the team?`)) return;
    try {
      await teamService.removeMember(myTeam.id, memberUserId);
      setStatusMessage(`Removed ${username} from team.`);
      fetchMyTeam();
    } catch (e: any) {
      setStatusMessage(e.response?.data?.message || 'Failed to remove member');
    } finally {
      setTimeout(() => setStatusMessage(null), 4000);
    }
  };

  const handleLeaveTeam = async () => {
    if (!myTeam) return;
    if (!window.confirm('Are you sure you want to leave this team?')) return;
    try {
      await teamService.leaveTeam(myTeam.id);
      setStatusMessage('You have left the team.');
      setMyTeam(null);
    } catch (e: any) {
      setStatusMessage(e.response?.data?.message || 'Failed to leave team');
    } finally {
      setTimeout(() => setStatusMessage(null), 4000);
    }
  };

  const currentMember = myTeam?.members.find((m) => m.userId === user?.id);
  const isOwner = currentMember?.role === 'OWNER';
  const isCaptain = currentMember?.role === 'CAPTAIN';
  const canManage = isOwner || isCaptain;

  return (
    <div className="space-y-8 max-w-6xl mx-auto pb-12">
      {/* Header */}
      <div>
        <Badge variant="neutral" size="sm" className="mb-2 bg-sandwich-800 text-sandwich-200 border-sandwich-700">
          <Shield className="w-3.5 h-3.5 mr-1 text-sandwich-100" />
          CLAN & SQUADRONS
        </Badge>
        <h1 className="text-3xl font-black text-sandwich-100 tracking-tight">
          TEAMS & GUILDS
        </h1>
        <p className="text-sm text-sandwich-400 mt-1">
          Form elite developer guilds, compete for clan dominance, and train together for team scrims.
        </p>
      </div>

      {/* Status Toast */}
      <AnimatePresence>
        {statusMessage && (
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            className="p-3 bg-sandwich-800 border border-sandwich-700 text-sandwich-100 text-xs font-semibold rounded-xl flex items-center gap-2 shadow-luxury"
          >
            <Check className="w-4 h-4 text-sandwich-100" />
            <span>{statusMessage}</span>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Tab Switcher */}
      <div className="flex items-center gap-2 border-b border-sandwich-800 pb-2">
        <button
          onClick={() => setActiveTab('my-team')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
            activeTab === 'my-team'
              ? 'bg-sandwich-50 text-sandwich-950 shadow-glow-white'
              : 'text-sandwich-400 hover:text-sandwich-200 hover:bg-sandwich-800'
          }`}
        >
          My Guild
        </button>
        <button
          onClick={() => setActiveTab('browse')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
            activeTab === 'browse'
              ? 'bg-sandwich-50 text-sandwich-950 shadow-glow-white'
              : 'text-sandwich-400 hover:text-sandwich-200 hover:bg-sandwich-800'
          }`}
        >
          Browse Guilds
        </button>
        <button
          onClick={() => setActiveTab('leaderboard')}
          className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
            activeTab === 'leaderboard'
              ? 'bg-sandwich-50 text-sandwich-950 shadow-glow-white'
              : 'text-sandwich-400 hover:text-sandwich-200 hover:bg-sandwich-800'
          }`}
        >
          Guild Leaderboard
        </button>
      </div>

      {/* TAB 1: MY TEAM */}
      {activeTab === 'my-team' && (
        <div className="space-y-6">
          {loadingMyTeam ? (
            <div className="py-20 text-center text-sandwich-400">
              <Loader2 className="w-8 h-8 animate-spin mx-auto mb-2 text-sandwich-200" />
              <span className="text-xs">Locating your guild banner...</span>
            </div>
          ) : myTeam ? (
            /* ACTIVE TEAM VIEW */
            <div className="space-y-6">
              {/* Guild Banner Card */}
              <Card className="p-6 sm:p-8 bg-sandwich-900/95 border-sandwich-700 rounded-3xl shadow-luxury backdrop-blur-xl">
                <div className="flex flex-col md:flex-row items-center md:items-start gap-6">
                  <div className="w-20 h-20 rounded-2xl bg-sandwich-800 border border-sandwich-700 text-sandwich-100 font-black text-2xl flex items-center justify-center shadow-glow-silver shrink-0">
                    <Shield className="w-10 h-10" />
                  </div>

                  <div className="flex-1 min-w-0 text-center md:text-left">
                    <div className="flex flex-wrap items-center justify-center md:justify-start gap-2 mb-1">
                      <h2 className="text-2xl font-black text-sandwich-100 tracking-tight">{myTeam.name}</h2>
                      <span className="text-xs font-mono text-sandwich-500">[{myTeam.slug}]</span>
                    </div>

                    {myTeam.description && (
                      <p className="text-xs text-sandwich-300 max-w-xl mb-3 leading-relaxed">
                        {myTeam.description}
                      </p>
                    )}

                    <div className="flex flex-wrap items-center justify-center md:justify-start gap-3 text-xs font-mono">
                      <div className="px-3 py-1 rounded-xl bg-sandwich-800 border border-sandwich-700 text-sandwich-100 font-bold flex items-center gap-1.5 shadow-sm">
                        <Flame className="w-3.5 h-3.5 text-sandwich-200" />
                        <span>{myTeam.rating} Clan MMR</span>
                      </div>
                      <div className="px-3 py-1 rounded-xl bg-sandwich-950 border border-sandwich-800 text-sandwich-300 font-semibold">
                        {myTeam.memberCount} / {myTeam.maxMembers} Members
                      </div>
                      <div className="px-3 py-1 rounded-xl bg-sandwich-950 border border-sandwich-800 text-sandwich-300 font-semibold">
                        {myTeam.wins}W - {myTeam.losses}L ({myTeam.winRate}% WR)
                      </div>
                    </div>
                  </div>

                  {/* Top Action Buttons */}
                  <div className="flex flex-col gap-2 shrink-0 w-full sm:w-auto">
                    {canManage && myTeam.memberCount < myTeam.maxMembers && (
                      <Button
                        variant="primary"
                        size="sm"
                        onClick={() => setShowInviteModal(true)}
                        leftIcon={<UserPlus className="w-4 h-4" />}
                      >
                        Invite Player
                      </Button>
                    )}

                    <Button
                      variant="secondary"
                      size="sm"
                      onClick={handleLeaveTeam}
                      leftIcon={<LogOut className="w-4 h-4 text-rose-400" />}
                    >
                      Leave Clan
                    </Button>
                  </div>
                </div>
              </Card>

              {/* Members Roster */}
              <Card className="p-6 bg-sandwich-900/90 border-sandwich-800 rounded-2xl space-y-4 shadow-luxury">
                <div className="flex items-center justify-between">
                  <h3 className="text-sm font-bold text-sandwich-200 uppercase tracking-wider font-mono">
                    Guild Roster ({myTeam.members.length})
                  </h3>
                </div>

                <div className="divide-y divide-sandwich-800/80">
                  {myTeam.members.map((member) => (
                    <div
                      key={member.id}
                      className="py-3 flex items-center justify-between gap-3 hover:bg-sandwich-800/40 px-2 rounded-xl transition-colors"
                    >
                      <div className="flex items-center gap-3 min-w-0">
                        <div className="relative">
                          <div className="w-10 h-10 rounded-xl bg-sandwich-800 border border-sandwich-700 text-sandwich-100 font-bold flex items-center justify-center text-sm shadow-sm">
                            {member.displayName ? member.displayName.slice(0, 2).toUpperCase() : member.username.slice(0, 2).toUpperCase()}
                          </div>
                          {member.online && (
                            <span className="absolute -bottom-0.5 -right-0.5 w-3 h-3 rounded-full bg-emerald-500 border-2 border-sandwich-950 ring-1 ring-emerald-400/50" />
                          )}
                        </div>

                        <div className="min-w-0">
                          <div className="flex items-center gap-2">
                            <span className="font-bold text-xs text-sandwich-100 truncate">
                              {member.displayName || member.username}
                            </span>
                            <span className="text-[10px] font-mono text-sandwich-500">@{member.username}</span>
                            {member.role === 'OWNER' && (
                              <span className="px-1.5 py-0.5 rounded-md bg-sandwich-800 text-sandwich-100 border border-sandwich-600 text-[9px] font-bold font-mono flex items-center gap-1 shadow-sm">
                                <Crown className="w-2.5 h-2.5 text-sandwich-200" />
                                OWNER
                              </span>
                            )}
                            {member.role === 'CAPTAIN' && (
                              <span className="px-1.5 py-0.5 rounded-md bg-sandwich-800 text-sandwich-200 border border-sandwich-700 text-[9px] font-bold font-mono">
                                CAPTAIN
                              </span>
                            )}
                          </div>

                          <div className="flex items-center gap-2 text-[10px] font-mono text-sandwich-400 mt-0.5">
                            <span className="text-sandwich-200 font-semibold">Lvl {member.level}</span>
                            <span>•</span>
                            <span className="font-semibold">{member.rating} MMR</span>
                            <span>•</span>
                            <span>Joined {new Date(member.joinedAt).toLocaleDateString()}</span>
                          </div>
                        </div>
                      </div>

                      {/* Management Actions */}
                      <div className="shrink-0 flex items-center gap-2">
                        {canManage && member.role === 'MEMBER' && member.userId !== user?.id && (
                          <button
                            onClick={() => handleRemoveMember(member.userId, member.username)}
                            className="p-1.5 text-sandwich-500 hover:text-rose-400 rounded-lg hover:bg-sandwich-800 transition-colors"
                            title="Remove Member"
                          >
                            <UserX className="w-4 h-4" />
                          </button>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </Card>
            </div>
          ) : (
            /* NO TEAM - CREATION / ONBOARDING VIEW */
            <div className="space-y-6">
              <Card className="p-10 text-center bg-sandwich-900/90 border-sandwich-800 shadow-luxury backdrop-blur-xl rounded-3xl space-y-4 max-w-xl mx-auto">
                <div className="w-16 h-16 rounded-2xl bg-sandwich-800 border border-sandwich-700 text-sandwich-100 flex items-center justify-center mx-auto shadow-sm">
                  <Shield className="w-8 h-8" />
                </div>
                <h3 className="text-xl font-bold text-sandwich-100">You Are Not in a Guild</h3>
                <p className="text-xs text-sandwich-400 max-w-sm mx-auto leading-relaxed">
                  Join forces with fellow gladiators. Found your own team or browse existing squads to climb the leaderboard together.
                </p>

                <div className="pt-2 flex flex-col sm:flex-row items-center justify-center gap-3">
                  <Button
                    variant="primary"
                    onClick={() => setShowCreateForm(true)}
                    leftIcon={<PlusCircle className="w-4 h-4" />}
                  >
                    Found a Team
                  </Button>
                  <Button variant="secondary" onClick={() => setActiveTab('browse')}>
                    Browse Guilds
                  </Button>
                </div>
              </Card>

              {/* CREATE TEAM FORM MODAL */}
              {showCreateForm && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-sandwich-950/80 backdrop-blur-md">
                  <div className="bg-sandwich-900 border border-sandwich-700 max-w-md w-full rounded-2xl p-6 shadow-luxury space-y-4 text-left">
                    <div className="flex items-center justify-between pb-2 border-b border-sandwich-800">
                      <div className="flex items-center gap-2">
                        <Shield className="w-5 h-5 text-sandwich-100" />
                        <h3 className="text-base font-bold text-sandwich-100">Found a New Guild</h3>
                      </div>
                      <button
                        onClick={() => setShowCreateForm(false)}
                        className="p-1 text-sandwich-400 hover:text-sandwich-100 rounded-lg"
                      >
                        <X className="w-4 h-4" />
                      </button>
                    </div>

                    <form onSubmit={handleCreateTeam} className="space-y-4">
                      <div className="space-y-1.5">
                        <label className="text-xs font-bold text-sandwich-200">Team Name</label>
                        <Input
                          placeholder="e.g. Code Ninjas, Algorithm Avengers..."
                          value={teamName}
                          onChange={(e) => setTeamName(e.target.value)}
                          required
                          maxLength={50}
                        />
                      </div>

                      <div className="space-y-1.5">
                        <label className="text-xs font-bold text-sandwich-200">Motto / Description</label>
                        <textarea
                          placeholder="Tell challengers what your clan is all about..."
                          value={teamDesc}
                          onChange={(e) => setTeamDesc(e.target.value)}
                          rows={3}
                          className="w-full rounded-xl border border-sandwich-700 bg-sandwich-950 p-3 text-xs text-sandwich-100 placeholder-sandwich-500 focus:outline-hidden focus:border-sandwich-400"
                        />
                      </div>

                      <div className="pt-2 flex justify-end gap-2">
                        <Button variant="secondary" type="button" onClick={() => setShowCreateForm(false)}>
                          Cancel
                        </Button>
                        <Button variant="primary" type="submit" isLoading={creating}>
                          Found Guild
                        </Button>
                      </div>
                    </form>
                  </div>
                </div>
              )}
            </div>
          )}

          {/* INVITE PLAYER MODAL */}
          {showInviteModal && (
            <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-sandwich-950/80 backdrop-blur-md">
              <div className="bg-sandwich-900 border border-sandwich-700 max-w-md w-full rounded-2xl p-6 shadow-luxury space-y-4 text-left">
                <div className="flex items-center justify-between pb-2 border-b border-sandwich-800">
                  <div className="flex items-center gap-2">
                    <UserPlus className="w-5 h-5 text-sandwich-100" />
                    <h3 className="text-base font-bold text-sandwich-100">Invite Gladiator</h3>
                  </div>
                  <button
                    onClick={() => setShowInviteModal(false)}
                    className="p-1 text-sandwich-400 hover:text-sandwich-100 rounded-lg"
                  >
                    <X className="w-4 h-4" />
                  </button>
                </div>

                <div className="space-y-3">
                  <Input
                    placeholder="Search gladiator by username..."
                    value={inviteSearch}
                    onChange={(e) => setInviteSearch(e.target.value)}
                    leftIcon={<Search className="w-4 h-4 text-sandwich-400" />}
                  />

                  <div className="max-h-60 overflow-y-auto divide-y divide-sandwich-800/80">
                    {inviteSearchResults.length === 0 ? (
                      <p className="py-8 text-center text-xs text-sandwich-500">
                        {inviteSearch.trim() ? 'No players found' : 'Type a username above'}
                      </p>
                    ) : (
                      inviteSearchResults.map((player) => (
                        <div
                          key={player.id}
                          className="py-2.5 flex items-center justify-between gap-2"
                        >
                          <div className="flex items-center gap-2.5 min-w-0">
                            <div className="w-8 h-8 rounded-lg bg-sandwich-800 border border-sandwich-700 text-sandwich-100 font-bold flex items-center justify-center text-xs">
                              {player.username.slice(0, 2).toUpperCase()}
                            </div>
                            <div className="min-w-0">
                              <p className="text-xs font-bold text-sandwich-100 truncate">{player.username}</p>
                              <p className="text-[10px] font-mono text-sandwich-400">
                                Lvl {player.level} • {player.rating} MMR
                              </p>
                            </div>
                          </div>

                          <Button
                            size="sm"
                            variant="primary"
                            isLoading={invitingId === player.id}
                            onClick={() => handleInvitePlayer(player.id)}
                            leftIcon={<UserPlus className="w-3.5 h-3.5" />}
                          >
                            Invite
                          </Button>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {/* TAB 2: BROWSE GUILDS */}
      {activeTab === 'browse' && (
        <div className="space-y-4">
          <Input
            placeholder="Search guilds by name..."
            value={browseQuery}
            onChange={(e) => setBrowseQuery(e.target.value)}
            leftIcon={<Search className="w-4 h-4 text-sandwich-400" />}
          />

          {loadingBrowse ? (
            <div className="py-16 text-center text-sandwich-400">
              <Loader2 className="w-8 h-8 animate-spin mx-auto mb-2 text-sandwich-200" />
              <span className="text-xs">Searching guilds...</span>
            </div>
          ) : browseTeams.length === 0 ? (
            <p className="py-12 text-center text-xs text-sandwich-500">No guilds match your query.</p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {browseTeams.map((team) => (
                <Card key={team.id} className="p-5 bg-sandwich-900/90 border-sandwich-800 hover:border-sandwich-700 rounded-2xl space-y-3 shadow-luxury transition-all">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-xl bg-sandwich-800 border border-sandwich-700 text-sandwich-100 flex items-center justify-center font-bold">
                      <Shield className="w-5 h-5" />
                    </div>
                    <div className="min-w-0">
                      <h4 className="text-xs font-bold text-sandwich-100 truncate">{team.name}</h4>
                      <p className="text-[10px] font-mono text-sandwich-500">Owner: @{team.ownerUsername}</p>
                    </div>
                  </div>

                  {team.description && (
                    <p className="text-[11px] text-sandwich-400 line-clamp-2">{team.description}</p>
                  )}

                  <div className="pt-2 border-t border-sandwich-800 flex items-center justify-between text-[11px] font-mono">
                    <span className="text-sandwich-100 font-bold">{team.rating} MMR</span>
                    <span className="text-sandwich-500">{team.memberCount} / {team.maxMembers} Members</span>
                  </div>
                </Card>
              ))}
            </div>
          )}
        </div>
      )}

      {/* TAB 3: GUILD LEADERBOARD */}
      {activeTab === 'leaderboard' && (
        <Card className="p-6 bg-sandwich-900/90 border-sandwich-800 rounded-2xl space-y-4 shadow-luxury backdrop-blur-xl">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-bold text-sandwich-200 uppercase tracking-wider font-mono">
              Clan Dominance Rankings
            </h3>
          </div>

          {loadingLeaderboard ? (
            <div className="py-16 text-center text-sandwich-400">
              <Loader2 className="w-8 h-8 animate-spin mx-auto mb-2 text-sandwich-200" />
              <span className="text-xs">Calculating clan rankings...</span>
            </div>
          ) : leaderboard.length === 0 ? (
            <p className="py-12 text-center text-xs text-sandwich-500">No ranked clans yet.</p>
          ) : (
            <div className="divide-y divide-sandwich-800/80">
              {leaderboard.map((item) => (
                <div
                  key={item.id}
                  className="py-3.5 flex items-center justify-between gap-3 px-2 rounded-xl hover:bg-sandwich-800/40 transition-colors"
                >
                  <div className="flex items-center gap-3.5 min-w-0">
                    <div className="w-7 text-center font-black font-mono text-xs">
                      {item.rank === 1 ? (
                        <span className="text-sandwich-50 font-black">#1</span>
                      ) : item.rank === 2 ? (
                        <span className="text-sandwich-300">#2</span>
                      ) : item.rank === 3 ? (
                        <span className="text-sandwich-400">#3</span>
                      ) : (
                        <span className="text-sandwich-500">#{item.rank}</span>
                      )}
                    </div>

                    <div className="w-9 h-9 rounded-xl bg-sandwich-800 border border-sandwich-700 text-sandwich-100 flex items-center justify-center shrink-0">
                      <Shield className="w-4 h-4" />
                    </div>

                    <div className="min-w-0">
                      <p className="font-bold text-xs text-sandwich-100 truncate">{item.name}</p>
                      <p className="text-[10px] font-mono text-sandwich-400">
                        Leader: @{item.ownerUsername} • {item.memberCount} members
                      </p>
                    </div>
                  </div>

                  <div className="text-right font-mono shrink-0">
                    <span className="text-xs font-bold text-sandwich-100">{item.rating} MMR</span>
                    <span className="text-[10px] text-sandwich-500 block">{item.wins}W - {item.losses}L</span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      )}
    </div>
  );
};
