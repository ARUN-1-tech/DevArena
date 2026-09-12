import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Users,
  UserPlus,
  Search,
  Check,
  X,
  Swords,
  ExternalLink,
  Loader2,
  Clock,
  UserCheck,
  Trash2,
} from 'lucide-react';
import { Card } from '../../components/ui/Card';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { friendService } from '../../services/friendService';
import { playerService } from '../../services/playerService';
import { webSocketService } from '../../services/webSocketService';
import { Friend, FriendBattleInvite, FriendRequest, PlayerSearchResult } from '../../types/social';

export const FriendsPage: React.FC = () => {
  const navigate = useNavigate();

  // Friends & Requests state
  const [friends, setFriends] = useState<Friend[]>([]);
  const [incomingRequests, setIncomingRequests] = useState<FriendRequest[]>([]);
  const [outgoingRequests, setOutgoingRequests] = useState<FriendRequest[]>([]);
  const [pendingChallenges, setPendingChallenges] = useState<FriendBattleInvite[]>([]);
  const [outgoingInvite, setOutgoingInvite] = useState<FriendBattleInvite | null>(null);
  const [outgoingTimer, setOutgoingTimer] = useState<number>(60);
  const [loading, setLoading] = useState(true);

  // Search state
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<PlayerSearchResult[]>([]);
  const [searching, setSearching] = useState(false);
  const [actionLoadingId, setActionLoadingId] = useState<string | null>(null);
  const [statusMessage, setStatusMessage] = useState<string | null>(null);

  // Fetch friends and requests
  const loadSocialData = useCallback(async () => {
    try {
      setLoading(true);
      const [friendsData, requestsData, challengesData] = await Promise.all([
        friendService.getFriends(),
        friendService.getFriendRequests(),
        friendService.getPendingChallenges().catch(() => [] as FriendBattleInvite[]),
      ]);
      setFriends(friendsData);
      setIncomingRequests(requestsData.incoming);
      setOutgoingRequests(requestsData.outgoing);
      setPendingChallenges(challengesData);
    } catch (e) {
      console.error('Failed to load friends/requests', e);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadSocialData();

    // Listen to real-time presence updates
    const unsubPresence = webSocketService.subscribe('/topic/presence', (data: { userId: string; online: boolean }) => {
      setFriends((prev) =>
        prev.map((f) => (f.friendId === data.userId ? { ...f, online: data.online } : f))
      );
      setSearchResults((prev) =>
        prev.map((p) => (p.id === data.userId ? { ...p, online: data.online } : p))
      );
    });

    const unsubNotifs = webSocketService.subscribe('/user/queue/notifications', (notif: any) => {
      if (notif.type === 'BATTLE_INVITE' || notif.type === 'FRIEND_REQUEST') {
        loadSocialData();
      }
    });

    return () => {
      if (unsubPresence) unsubPresence();
      if (unsubNotifs) unsubNotifs();
    };
  }, [loadSocialData]);

  // Outgoing Challenge Polling & Countdown
  useEffect(() => {
    if (!outgoingInvite) return;
    setOutgoingTimer(60);

    const pollInterval = setInterval(async () => {
      try {
        const status = await friendService.getChallengeStatus(outgoingInvite.id);
        if (status.status === 'ACCEPTED' && status.battleId) {
          clearInterval(pollInterval);
          setOutgoingInvite(null);
          navigate(`/battle/${status.battleId}`);
        } else if (status.status === 'REJECTED' || status.status === 'EXPIRED') {
          clearInterval(pollInterval);
          setStatusMessage(`Duel challenge was ${status.status.toLowerCase()}.`);
          setOutgoingInvite(null);
        }
      } catch (err) {
        console.debug('Failed to poll invite status', err);
      }
    }, 2000);

    const countdown = setInterval(() => {
      setOutgoingTimer((t) => {
        if (t <= 1) {
          clearInterval(countdown);
          clearInterval(pollInterval);
          setOutgoingInvite(null);
          setStatusMessage('Duel challenge timed out.');
          return 0;
        }
        return t - 1;
      });
    }, 1000);

    const unsubTopic = webSocketService.subscribe(`/topic/battle-invite.${outgoingInvite.id}`, (event: any) => {
      if (event.type === 'INVITE_ACCEPTED' && event.battleId) {
        clearInterval(pollInterval);
        clearInterval(countdown);
        setOutgoingInvite(null);
        navigate(`/battle/${event.battleId}`);
      } else if (event.type === 'INVITE_DECLINED') {
        clearInterval(pollInterval);
        clearInterval(countdown);
        setOutgoingInvite(null);
        setStatusMessage('Duel challenge was declined.');
      }
    });

    return () => {
      clearInterval(pollInterval);
      clearInterval(countdown);
      if (unsubTopic) unsubTopic();
    };
  }, [outgoingInvite, navigate]);

  // Handle live search
  useEffect(() => {
    if (!searchQuery.trim()) {
      setSearchResults([]);
      return;
    }
    const timer = setTimeout(async () => {
      try {
        setSearching(true);
        const results = await playerService.searchPlayers(searchQuery.trim());
        setSearchResults(results);
      } catch (e) {
        console.error('Search failed', e);
      } finally {
        setSearching(false);
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [searchQuery]);

  // Send Friend Request
  const handleSendRequest = async (player: PlayerSearchResult) => {
    try {
      setActionLoadingId(player.id);
      await friendService.sendFriendRequest(player.id);
      setSearchResults((prev) =>
        prev.map((p) => (p.id === player.id ? { ...p, hasPendingRequest: true } : p))
      );
      setStatusMessage(`Friend request sent to ${player.username}!`);
      loadSocialData();
    } catch (e: any) {
      setStatusMessage(e.response?.data?.message || 'Failed to send request');
    } finally {
      setActionLoadingId(null);
      setTimeout(() => setStatusMessage(null), 4000);
    }
  };

  // Accept Friend Request
  const handleAcceptRequest = async (req: FriendRequest) => {
    try {
      setActionLoadingId(req.id);
      const newFriend = await friendService.acceptFriendRequest(req.id);
      setIncomingRequests((prev) => prev.filter((r) => r.id !== req.id));
      setFriends((prev) => [newFriend, ...prev]);
      setStatusMessage(`Accepted friend request from ${req.senderUsername}!`);
    } catch (e: any) {
      setStatusMessage(e.response?.data?.message || 'Failed to accept request');
    } finally {
      setActionLoadingId(null);
      setTimeout(() => setStatusMessage(null), 4000);
    }
  };

  // Decline Friend Request
  const handleDeclineRequest = async (req: FriendRequest) => {
    try {
      setActionLoadingId(req.id);
      await friendService.rejectFriendRequest(req.id);
      setIncomingRequests((prev) => prev.filter((r) => r.id !== req.id));
      setStatusMessage(`Declined friend request from ${req.senderUsername}`);
    } catch (e: any) {
      setStatusMessage(e.response?.data?.message || 'Failed to decline request');
    } finally {
      setActionLoadingId(null);
      setTimeout(() => setStatusMessage(null), 4000);
    }
  };

  // Remove Friend
  const handleRemoveFriend = async (friend: Friend) => {
    if (!window.confirm(`Are you sure you want to remove ${friend.username} from your friends?`)) {
      return;
    }
    try {
      setActionLoadingId(friend.id);
      await friendService.removeFriend(friend.friendId);
      setFriends((prev) => prev.filter((f) => f.id !== friend.id));
      setStatusMessage(`Removed ${friend.username} from friends`);
    } catch (e: any) {
      setStatusMessage(e.response?.data?.message || 'Failed to remove friend');
    } finally {
      setActionLoadingId(null);
      setTimeout(() => setStatusMessage(null), 4000);
    }
  };

  // Challenge Friend to 1v1 Battle
  const handleChallengeFriend = async (friend: Friend) => {
    try {
      setActionLoadingId(friend.id);
      const invite = await friendService.challengeFriend(friend.friendId);
      setOutgoingInvite(invite);
      setStatusMessage(`1v1 Duel challenge dispatched to ${friend.username}!`);
    } catch (e: any) {
      setStatusMessage(e.response?.data?.message || 'Failed to dispatch duel challenge');
    } finally {
      setActionLoadingId(null);
    }
  };

  // Accept incoming 1v1 Duel Challenge
  const handleAcceptChallenge = async (invite: FriendBattleInvite) => {
    try {
      setActionLoadingId(invite.id);
      const res = await friendService.acceptChallenge(invite.id);
      setPendingChallenges((prev) => prev.filter((i) => i.id !== invite.id));
      navigate(`/battle/${res.battleId}`);
    } catch (e: any) {
      setStatusMessage(e.response?.data?.message || 'Failed to accept duel challenge');
    } finally {
      setActionLoadingId(null);
    }
  };

  // Decline incoming 1v1 Duel Challenge
  const handleDeclineChallenge = async (invite: FriendBattleInvite) => {
    try {
      setActionLoadingId(invite.id);
      await friendService.declineChallenge(invite.id);
      setPendingChallenges((prev) => prev.filter((i) => i.id !== invite.id));
      setStatusMessage('Declined duel challenge');
    } catch (e: any) {
      setStatusMessage(e.response?.data?.message || 'Failed to decline challenge');
    } finally {
      setActionLoadingId(null);
      setTimeout(() => setStatusMessage(null), 3000);
    }
  };

  return (
    <div className="space-y-8 max-w-6xl mx-auto pb-12">
      {/* Header */}
      <div>
        <Badge variant="cyan" size="sm" className="mb-2">
          <Users className="w-3.5 h-3.5 mr-1" />
          SOCIAL GUILD & RIVALS
        </Badge>
        <h1 className="text-3xl font-black text-slate-900 tracking-tight">
          FRIENDS & RIVALS
        </h1>
        <p className="text-sm text-slate-600 mt-1">
          Connect with gladiators, challenge friends to synchronized 1v1 scrims, and climb together.
        </p>
      </div>

      {/* Status Toast */}
      <AnimatePresence>
        {statusMessage && (
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            className="p-3 bg-cyan-50 border border-cyan-200 text-cyan-800 text-xs font-semibold rounded-xl flex items-center gap-2 shadow-xs"
          >
            <Check className="w-4 h-4 text-cyan-600" />
            <span>{statusMessage}</span>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Outgoing Duel Challenge Active Modal / Banner */}
      <AnimatePresence>
        {outgoingInvite && (
          <motion.div
            initial={{ opacity: 0, scale: 0.96 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.96 }}
            className="p-5 rounded-2xl bg-gradient-to-r from-amber-50 via-orange-50/50 to-white border-2 border-amber-300 shadow-xl relative overflow-hidden"
          >
            <div className="absolute top-0 right-0 w-36 h-36 bg-amber-400/10 rounded-full blur-2xl pointer-events-none" />
            <div className="flex flex-col sm:flex-row items-center justify-between gap-4 relative z-10">
              <div className="flex items-center gap-4">
                <div className="relative">
                  <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-amber-500 to-orange-600 text-white flex items-center justify-center shadow-md">
                    <Swords className="w-6 h-6 animate-pulse" />
                  </div>
                  <span className="absolute -top-1 -right-1 flex h-3.5 w-3.5">
                    <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-amber-400 opacity-75" />
                    <span className="relative inline-flex rounded-full h-3.5 w-3.5 bg-amber-500" />
                  </span>
                </div>

                <div>
                  <div className="flex items-center gap-2">
                    <Badge variant="gold" size="sm">WAITING FOR OPPONENT</Badge>
                    <span className="text-xs font-mono font-bold text-amber-800 flex items-center gap-1">
                      <Clock className="w-3.5 h-3.5" />
                      {outgoingTimer}s
                    </span>
                  </div>
                  <h3 className="text-base font-extrabold text-slate-900 mt-1">
                    Challenging {outgoingInvite.inviteeUsername}
                  </h3>
                  <p className="text-xs text-slate-600">
                    Kata: <span className="font-semibold text-slate-900">{outgoingInvite.challengeTitle || 'Algorithmic Duel'}</span>. Waiting for gladiator to accept...
                  </p>
                </div>
              </div>

              <Button
                variant="outline"
                size="sm"
                className="text-slate-600 border-slate-300 hover:bg-slate-100 shrink-0"
                onClick={() => setOutgoingInvite(null)}
              >
                Cancel Challenge
              </Button>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* SECTION: INCOMING 1v1 DUEL INVITATIONS */}
      {pendingChallenges.length > 0 && (
        <Card className="p-5 bg-gradient-to-br from-amber-500/10 via-orange-500/5 to-white border-2 border-amber-300 shadow-md rounded-2xl space-y-3">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 rounded-xl bg-gradient-to-tr from-amber-500 to-orange-600 text-white flex items-center justify-center shadow-xs">
                <Swords className="w-4 h-4" />
              </div>
              <div>
                <h2 className="text-base font-extrabold text-slate-900">
                  Incoming 1v1 Duel Challenges ({pendingChallenges.length})
                </h2>
                <p className="text-xs text-slate-500">
                  Gladiators have challenged you to direct battle. Accept to start immediately!
                </p>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-3 pt-1">
            {pendingChallenges.map((invite) => (
              <div
                key={invite.id}
                className="p-4 rounded-xl bg-white border border-amber-200/90 shadow-sm flex flex-col sm:flex-row sm:items-center justify-between gap-3"
              >
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-amber-500 to-orange-600 text-white font-bold flex items-center justify-center text-sm shadow-xs">
                    {invite.inviterDisplayName ? invite.inviterDisplayName.slice(0, 2).toUpperCase() : invite.inviterUsername.slice(0, 2).toUpperCase()}
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="font-bold text-sm text-slate-900">
                        {invite.inviterDisplayName || invite.inviterUsername}
                      </span>
                      <span className="text-[10px] font-mono text-slate-400">@{invite.inviterUsername}</span>
                    </div>
                    <p className="text-xs text-slate-600 mt-0.5">
                      Challenge: <span className="font-semibold text-amber-700">{invite.challengeTitle || 'Algorithmic Kata'}</span>
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2 shrink-0">
                  <Button
                    size="sm"
                    variant="glow"
                    className="bg-gradient-to-r from-amber-500 to-orange-600 hover:from-amber-600 hover:to-orange-700 text-white font-bold text-xs shadow-xs"
                    isLoading={actionLoadingId === invite.id}
                    onClick={() => handleAcceptChallenge(invite)}
                    leftIcon={<Swords className="w-3.5 h-3.5" />}
                  >
                    ACCEPT DUEL
                  </Button>
                  <Button
                    size="sm"
                    variant="outline"
                    className="text-xs text-slate-600 border-slate-200 hover:bg-slate-100"
                    disabled={actionLoadingId === invite.id}
                    onClick={() => handleDeclineChallenge(invite)}
                  >
                    Decline
                  </Button>
                </div>
              </div>
            ))}
          </div>
        </Card>
      )}

      {/* SECTION 1: FIND PLAYERS */}
      <Card className="p-6 bg-white border-slate-200/90 shadow-sm rounded-2xl space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-base font-bold text-slate-900">Find Challengers</h2>
            <p className="text-xs text-slate-500">
              Search gladiators by username or handle across the DevArena network.
            </p>
          </div>
        </div>

        <div className="relative">
          <Input
            placeholder="Search by username (e.g. alice, arun)..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            leftIcon={<Search className="w-4 h-4 text-slate-400" />}
          />
          {searching && (
            <div className="absolute right-3 top-2.5">
              <Loader2 className="w-4 h-4 animate-spin text-cyan-600" />
            </div>
          )}
        </div>

        {/* Search Results */}
        {searchResults.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3 pt-2">
            {searchResults.map((player) => (
              <div
                key={player.id}
                className="p-3.5 rounded-xl border border-slate-200 hover:border-slate-300 bg-slate-50/50 flex items-center justify-between gap-3 transition-all"
              >
                <div className="flex items-center gap-3 min-w-0">
                  <div className="relative">
                    <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-cyan-600 to-violet-600 text-white font-bold flex items-center justify-center text-sm shadow-xs">
                      {player.displayName ? player.displayName.slice(0, 2).toUpperCase() : player.username.slice(0, 2).toUpperCase()}
                    </div>
                    {player.online && (
                      <span className="absolute -bottom-0.5 -right-0.5 w-3 h-3 rounded-full bg-emerald-500 border-2 border-white ring-1 ring-emerald-400/50" />
                    )}
                  </div>

                  <div className="min-w-0">
                    <div className="flex items-center gap-1.5">
                      <span className="font-bold text-xs text-slate-900 truncate">
                        {player.displayName || player.username}
                      </span>
                      <span className="text-[10px] text-slate-400 font-mono">@{player.username}</span>
                    </div>
                    <div className="flex items-center gap-2 text-[10px] font-mono text-slate-500 mt-0.5">
                      <span className="text-cyan-700 font-semibold">Lvl {player.level}</span>
                      <span>•</span>
                      <span className="font-semibold">{player.rating} MMR</span>
                      <span>•</span>
                      <span className="text-slate-400">{player.rankBadge}</span>
                    </div>
                  </div>
                </div>

                <div className="shrink-0 flex items-center gap-1.5">
                  <button
                    onClick={() => navigate(`/players/${player.username}`)}
                    className="p-1.5 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition-colors"
                    title="View Profile"
                  >
                    <ExternalLink className="w-4 h-4" />
                  </button>

                  {player.isFriend ? (
                    <span className="px-2.5 py-1 rounded-lg bg-emerald-50 text-emerald-700 border border-emerald-200 text-[10px] font-bold font-mono flex items-center gap-1">
                      <UserCheck className="w-3 h-3" />
                      FRIENDS
                    </span>
                  ) : player.hasPendingRequest ? (
                    <span className="px-2.5 py-1 rounded-lg bg-slate-100 text-slate-600 border border-slate-200 text-[10px] font-bold font-mono flex items-center gap-1">
                      <Clock className="w-3 h-3" />
                      SENT
                    </span>
                  ) : (
                    <Button
                      size="sm"
                      variant="glow"
                      isLoading={actionLoadingId === player.id}
                      onClick={() => handleSendRequest(player)}
                      leftIcon={<UserPlus className="w-3.5 h-3.5" />}
                    >
                      ADD
                    </Button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </Card>

      {/* SECTION 2: FRIEND REQUESTS (Incoming & Outgoing) */}
      {(incomingRequests.length > 0 || outgoingRequests.length > 0) && (
        <div className="space-y-4">
          {incomingRequests.length > 0 && (
            <Card className="p-6 bg-white border-cyan-200/80 shadow-sm rounded-2xl space-y-3">
              <div className="flex items-center gap-2">
                <span className="font-bold text-sm text-slate-900">
                  Incoming Requests ({incomingRequests.length})
                </span>
                <span className="w-2 h-2 rounded-full bg-cyan-600 animate-pulse" />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                <AnimatePresence>
                  {incomingRequests.map((req) => (
                    <motion.div
                      key={req.id}
                      initial={{ opacity: 0, y: 5 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, scale: 0.95 }}
                      className="p-3.5 rounded-xl border border-cyan-100 bg-cyan-50/30 flex items-center justify-between gap-3"
                    >
                      <div className="flex items-center gap-3 min-w-0">
                        <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-cyan-600 to-violet-600 text-white font-bold flex items-center justify-center text-xs shadow-xs shrink-0">
                          {req.senderUsername.slice(0, 2).toUpperCase()}
                        </div>
                        <div className="min-w-0">
                          <p className="font-bold text-xs text-slate-900 truncate">
                            {req.senderDisplayName || req.senderUsername}
                          </p>
                          <p className="text-[10px] font-mono text-slate-500">
                            Lvl {req.senderLevel} • {req.senderRating} MMR
                          </p>
                        </div>
                      </div>

                      <div className="flex items-center gap-1.5 shrink-0">
                        <Button
                          size="sm"
                          variant="glow"
                          isLoading={actionLoadingId === req.id}
                          onClick={() => handleAcceptRequest(req)}
                          leftIcon={<Check className="w-3 h-3" />}
                        >
                          Accept
                        </Button>
                        <Button
                          size="sm"
                          variant="outline"
                          disabled={actionLoadingId === req.id}
                          onClick={() => handleDeclineRequest(req)}
                        >
                          <X className="w-3.5 h-3.5" />
                        </Button>
                      </div>
                    </motion.div>
                  ))}
                </AnimatePresence>
              </div>
            </Card>
          )}

          {outgoingRequests.length > 0 && (
            <Card className="p-4 bg-white border-slate-200/80 rounded-xl">
              <span className="font-bold text-xs text-slate-600 mb-2 block">
                Pending Requests Sent ({outgoingRequests.length})
              </span>
              <div className="flex flex-wrap gap-2">
                {outgoingRequests.map((req) => (
                  <div
                    key={req.id}
                    className="px-3 py-1.5 rounded-lg bg-slate-50 border border-slate-200 text-xs font-mono text-slate-600 flex items-center gap-2"
                  >
                    <Clock className="w-3.5 h-3.5 text-slate-400" />
                    <span>@{req.receiverUsername}</span>
                    <span className="text-[10px] text-slate-400">pending</span>
                  </div>
                ))}
              </div>
            </Card>
          )}
        </div>
      )}

      {/* SECTION 3: FRIENDS ROSTER */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <h2 className="text-lg font-black text-slate-900 tracking-tight">
              My Friends ({friends.length})
            </h2>
            <span className="text-xs font-mono text-slate-400">
              • {friends.filter((f) => f.online).length} online
            </span>
          </div>
        </div>

        {loading ? (
          <div className="py-16 text-center text-slate-400">
            <Loader2 className="w-8 h-8 animate-spin mx-auto mb-2 text-cyan-600" />
            <span className="text-xs">Loading friend roster...</span>
          </div>
        ) : friends.length === 0 ? (
          <Card className="p-12 text-center bg-white border-slate-200/90 shadow-sm rounded-2xl space-y-3">
            <div className="w-14 h-14 rounded-2xl bg-cyan-50 text-cyan-600 flex items-center justify-center mx-auto">
              <Users className="w-7 h-7" />
            </div>
            <h3 className="text-base font-bold text-slate-900">No Rivalries Yet</h3>
            <p className="text-xs text-slate-500 max-w-sm mx-auto">
              Search gladiators above and send friend requests to populate your rival list.
            </p>
          </Card>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {friends.map((friend) => (
              <Card
                key={friend.id}
                className="p-4 bg-white border-slate-200/90 hover:border-cyan-300 shadow-xs hover:shadow-md transition-all rounded-2xl space-y-3"
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3 min-w-0">
                    <div className="relative">
                      <div className="w-11 h-11 rounded-xl bg-gradient-to-tr from-cyan-600 to-violet-600 text-white font-bold flex items-center justify-center text-sm shadow-xs">
                        {friend.displayName ? friend.displayName.slice(0, 2).toUpperCase() : friend.username.slice(0, 2).toUpperCase()}
                      </div>
                      {friend.online ? (
                        <span
                          className="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 rounded-full bg-emerald-500 border-2 border-white ring-1 ring-emerald-400/50"
                          title="Online"
                        />
                      ) : (
                        <span
                          className="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 rounded-full bg-slate-300 border-2 border-white"
                          title="Offline"
                        />
                      )}
                    </div>

                    <div className="min-w-0">
                      <h4 className="text-xs font-bold text-slate-900 truncate">
                        {friend.displayName || friend.username}
                      </h4>
                      <p className="text-[10px] font-mono text-slate-400 truncate">
                        @{friend.username}
                      </p>
                      <div className="flex items-center gap-2 text-[10px] font-mono mt-0.5">
                        <span className="text-cyan-700 font-bold">Lvl {friend.level}</span>
                        <span className="text-slate-300">•</span>
                        <span className="font-semibold text-slate-600">{friend.rating} MMR</span>
                      </div>
                    </div>
                  </div>

                  <div className="shrink-0 flex flex-col gap-1 items-end">
                    <button
                      onClick={() => handleRemoveFriend(friend)}
                      className="p-1 text-slate-300 hover:text-rose-600 transition-colors"
                      title="Remove Friend"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>

                {/* Actions */}
                <div className="pt-2 border-t border-slate-100 flex items-center gap-2">
                  <Button
                    size="sm"
                    variant="outline"
                    className="flex-1 text-[11px] h-8"
                    onClick={() => navigate(`/players/${friend.username}`)}
                    leftIcon={<ExternalLink className="w-3 h-3" />}
                  >
                    Profile
                  </Button>

                  <Button
                    size="sm"
                    variant="glow"
                    className="flex-1 text-[11px] h-8"
                    isLoading={actionLoadingId === friend.id}
                    onClick={() => handleChallengeFriend(friend)}
                    leftIcon={<Swords className="w-3 h-3" />}
                  >
                    Challenge
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
