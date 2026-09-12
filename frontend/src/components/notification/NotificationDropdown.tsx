import React, { useEffect, useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Bell,
  CheckCheck,
  UserPlus,
  Users,
  Swords,
  Trophy,
  Award,
  Zap,
  Shield,
  ShieldAlert,
  Loader2,
  Clock,
  Sparkles,
  ExternalLink,
} from 'lucide-react';
import { notificationService } from '../../services/notificationService';
import { webSocketService } from '../../services/webSocketService';
import { useAuth } from '../../contexts/AuthContext';
import { NotificationItem, NotificationType } from '../../types/social';

import { friendService } from '../../services/friendService';

const TYPE_CONFIG: Record<
  NotificationType,
  {
    icon: React.ComponentType<{ className?: string }>;
    gradient: string;
    badgeBg: string;
    badgeText: string;
  }
> = {
  FRIEND_REQUEST: {
    icon: UserPlus,
    gradient: 'from-cyan-500 to-blue-600',
    badgeBg: 'bg-cyan-50 border-cyan-200 text-cyan-700',
    badgeText: 'Social',
  },
  FRIEND_ACCEPTED: {
    icon: Users,
    gradient: 'from-cyan-500 to-emerald-600',
    badgeBg: 'bg-emerald-50 border-emerald-200 text-emerald-700',
    badgeText: 'Social',
  },
  BATTLE_INVITE: {
    icon: Swords,
    gradient: 'from-amber-500 to-orange-600',
    badgeBg: 'bg-amber-50 border-amber-200 text-amber-700',
    badgeText: 'Duel',
  },
  BATTLE_RESULT: {
    icon: Trophy,
    gradient: 'from-yellow-400 to-amber-600',
    badgeBg: 'bg-amber-50 border-amber-200 text-amber-800',
    badgeText: 'Result',
  },
  ACHIEVEMENT_UNLOCKED: {
    icon: Award,
    gradient: 'from-emerald-500 to-teal-600',
    badgeBg: 'bg-emerald-50 border-emerald-200 text-emerald-700',
    badgeText: 'Unlock',
  },
  LEVEL_UP: {
    icon: Zap,
    gradient: 'from-indigo-500 to-purple-600',
    badgeBg: 'bg-indigo-50 border-indigo-200 text-indigo-700',
    badgeText: 'Rank Up',
  },
  TEAM_INVITE: {
    icon: Shield,
    gradient: 'from-violet-500 to-indigo-600',
    badgeBg: 'bg-violet-50 border-violet-200 text-violet-700',
    badgeText: 'Clan',
  },
  TEAM_JOINED: {
    icon: ShieldAlert,
    gradient: 'from-purple-500 to-fuchsia-600',
    badgeBg: 'bg-purple-50 border-purple-200 text-purple-700',
    badgeText: 'Clan',
  },
  SYSTEM: {
    icon: Bell,
    gradient: 'from-indigo-600 to-cyan-600',
    badgeBg: 'bg-slate-100 border-slate-200 text-slate-700',
    badgeText: 'System',
  },
};

export const NotificationDropdown: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(false);
  const [activeTab, setActiveTab] = useState<'all' | 'unread'>('all');
  const [unreadCount, setUnreadCount] = useState<number>(0);
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [actionLoadingId, setActionLoadingId] = useState<string | null>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const processedNotifIds = useRef<Set<string>>(new Set());

  const fetchUnreadCount = async () => {
    try {
      const count = await notificationService.getUnreadCount();
      setUnreadCount(count);
    } catch (e) {
      console.debug('Failed to fetch unread count', e);
    }
  };

  const fetchNotifications = async () => {
    try {
      setLoading(true);
      const [res, count] = await Promise.all([
        notificationService.getNotifications(0, 20),
        notificationService.getUnreadCount(),
      ]);
      setNotifications(res.content);
      setUnreadCount(count);
    } catch (e) {
      console.debug('Failed to fetch notifications', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUnreadCount();

    const handleIncoming = (notif: NotificationItem) => {
      if (processedNotifIds.current.has(notif.id)) return;
      processedNotifIds.current.add(notif.id);

      setNotifications((prev) => {
        const filtered = prev.filter((n) => n.id !== notif.id);
        return [notif, ...filtered];
      });

      if (!notif.read) {
        setUnreadCount((c) => c + 1);
      }
    };

    let unsubscribeUser: (() => void) | undefined;
    let unsubscribeTopic: (() => void) | undefined;

    if (user?.username) {
      unsubscribeUser = webSocketService.subscribe('/user/queue/notifications', handleIncoming);
    }

    if (user?.id) {
      unsubscribeTopic = webSocketService.subscribe(`/topic/notifications.${user.id}`, handleIncoming);
    }

    return () => {
      if (unsubscribeUser) unsubscribeUser();
      if (unsubscribeTopic) unsubscribeTopic();
    };
  }, [user?.id, user?.username]);

  // Handle outside click
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };
    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen]);

  const handleToggle = () => {
    if (!isOpen) {
      fetchNotifications();
    }
    setIsOpen(!isOpen);
  };

  const handleMarkAllRead = async () => {
    try {
      await notificationService.markAllAsRead();
      setUnreadCount(0);
      setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
    } catch (e) {
      console.debug('Failed to mark all as read', e);
    }
  };

  const handleNotificationClick = async (notif: NotificationItem) => {
    if (!notif.read) {
      try {
        await notificationService.markAsRead(notif.id);
        setUnreadCount((c) => Math.max(0, c - 1));
        setNotifications((prev) =>
          prev.map((n) => (n.id === notif.id ? { ...n, read: true } : n))
        );
      } catch (e) {
        console.debug('Failed to mark notification as read', e);
      }
    }

    setIsOpen(false);

    // Route based on notification type
    switch (notif.type) {
      case 'FRIEND_REQUEST':
      case 'FRIEND_ACCEPTED':
      case 'BATTLE_INVITE':
        navigate('/friends');
        break;
      case 'BATTLE_RESULT':
        if (notif.referenceId) {
          navigate(`/battle/${notif.referenceId}/result`);
        } else {
          navigate('/arena');
        }
        break;
      case 'TEAM_INVITE':
      case 'TEAM_JOINED':
        navigate('/teams');
        break;
      case 'ACHIEVEMENT_UNLOCKED':
        navigate('/achievements');
        break;
      case 'LEVEL_UP':
        navigate('/profile');
        break;
      case 'SYSTEM':
        if (notif.referenceType === 'BATTLE' && notif.referenceId) {
          navigate(`/battle/${notif.referenceId}`);
        }
        break;
      default:
        break;
    }
  };

  const handleAcceptDuel = async (notif: NotificationItem) => {
    if (!notif.referenceId) return;
    try {
      setActionLoadingId(notif.id);
      await notificationService.markAsRead(notif.id);
      setNotifications((prev) =>
        prev.map((n) => (n.id === notif.id ? { ...n, read: true } : n))
      );
      setUnreadCount((c) => Math.max(0, c - 1));
      const res = await friendService.acceptChallenge(notif.referenceId);
      setIsOpen(false);
      navigate(`/battle/${res.battleId}`);
    } catch (err: any) {
      console.error('Failed to accept duel', err);
      navigate('/friends');
    } finally {
      setActionLoadingId(null);
    }
  };

  const handleDeclineDuel = async (notif: NotificationItem) => {
    if (!notif.referenceId) return;
    try {
      setActionLoadingId(notif.id);
      await notificationService.markAsRead(notif.id);
      await friendService.declineChallenge(notif.referenceId);
      setNotifications((prev) =>
        prev.map((n) => (n.id === notif.id ? { ...n, read: true } : n))
      );
      setUnreadCount((c) => Math.max(0, c - 1));
    } catch (err) {
      console.error('Failed to decline duel', err);
    } finally {
      setActionLoadingId(null);
    }
  };

  const formatTime = (dateStr: string) => {
    try {
      const diff = Date.now() - new Date(dateStr).getTime();
      const mins = Math.floor(diff / 60000);
      if (mins < 1) return 'Just now';
      if (mins < 60) return `${mins}m ago`;
      const hours = Math.floor(mins / 60);
      if (hours < 24) return `${hours}h ago`;
      return `${Math.floor(hours / 24)}d ago`;
    } catch {
      return '';
    }
  };

  const displayedNotifications =
    activeTab === 'unread' ? notifications.filter((n) => !n.read) : notifications;

  return (
    <div className="relative" ref={dropdownRef}>
      {/* Bell Button with Floating & Glow Animations */}
      <button
        onClick={handleToggle}
        className={`relative p-2.5 rounded-2xl transition-all duration-200 focus:outline-hidden group ${
          isOpen
            ? 'bg-indigo-50 text-indigo-700 shadow-xs border border-indigo-200/90'
            : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100/80'
        }`}
        title="Notifications"
        aria-label="Notifications"
      >
        <Bell className={`w-5 h-5 transition-transform duration-200 ${unreadCount > 0 ? 'group-hover:rotate-12' : ''}`} />

        {unreadCount > 0 && (
          <>
            {/* Pulsing Aura Ping */}
            <span className="absolute top-1.5 right-1.5 flex h-4 w-4">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-cyan-400 opacity-75" />
              <span className="relative inline-flex rounded-full h-4 w-4 bg-gradient-to-r from-cyan-600 to-indigo-600" />
            </span>

            {/* Badge Count */}
            <span className="absolute -top-0.5 -right-0.5 flex h-4.5 min-w-[18px] items-center justify-center rounded-full bg-gradient-to-r from-indigo-600 via-cyan-600 to-violet-600 px-1 text-[10px] font-black text-white shadow-md shadow-indigo-500/30 border border-white">
              {unreadCount > 9 ? '9+' : unreadCount}
            </span>
          </>
        )}
      </button>

      {/* Floating Glassmorphic Popover with Physics Animations */}
      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, y: -14, scale: 0.95, filter: 'blur(4px)' }}
            animate={{ opacity: 1, y: 0, scale: 1, filter: 'blur(0px)' }}
            exit={{ opacity: 0, y: -10, scale: 0.95, filter: 'blur(4px)' }}
            transition={{ type: 'spring', damping: 25, stiffness: 350 }}
            className="absolute right-0 mt-3 w-[calc(100vw-2rem)] sm:w-[440px] max-w-[450px] rounded-3xl bg-white/95 backdrop-blur-2xl border border-slate-200/90 shadow-2xl shadow-indigo-950/15 ring-1 ring-slate-900/5 z-50 overflow-hidden flex flex-col"
            style={{ maxHeight: 'calc(100vh - 100px)' }}
          >
            {/* Ambient subtle glowing gradients inside modal */}
            <div className="absolute -top-12 -right-12 w-44 h-44 bg-cyan-400/10 rounded-full blur-3xl pointer-events-none" />
            <div className="absolute -bottom-12 -left-12 w-44 h-44 bg-indigo-500/10 rounded-full blur-3xl pointer-events-none" />

            {/* Header */}
            <div className="relative z-10 px-5 pt-4 pb-3 border-b border-slate-100 bg-white/60 backdrop-blur-sm">
              <div className="flex items-center justify-between gap-3 mb-2.5">
                <div className="flex items-center gap-2">
                  <div className="w-7 h-7 rounded-lg bg-gradient-to-tr from-indigo-600 to-cyan-600 text-white flex items-center justify-center shadow-xs">
                    <Sparkles className="w-3.5 h-3.5" />
                  </div>
                  <div>
                    <h4 className="font-extrabold text-sm text-slate-900 tracking-tight leading-none">
                      Combat Dispatch
                    </h4>
                    <p className="text-[10px] font-mono text-slate-400 mt-0.5">
                      REAL-TIME PLATFORM EVENTS
                    </p>
                  </div>
                </div>

                {unreadCount > 0 && (
                  <button
                    onClick={handleMarkAllRead}
                    className="text-xs text-indigo-600 hover:text-indigo-800 font-semibold flex items-center gap-1.5 px-2.5 py-1 rounded-lg hover:bg-indigo-50/80 transition-colors cursor-pointer"
                  >
                    <CheckCheck className="w-3.5 h-3.5" />
                    <span>Mark all read</span>
                  </button>
                )}
              </div>

              {/* Filter Tabs */}
              <div className="flex items-center gap-2 pt-1">
                <button
                  onClick={() => setActiveTab('all')}
                  className={`text-xs font-semibold px-3 py-1.5 rounded-xl transition-all cursor-pointer ${
                    activeTab === 'all'
                      ? 'bg-slate-900 text-white shadow-xs'
                      : 'text-slate-500 hover:text-slate-800 hover:bg-slate-100'
                  }`}
                >
                  All ({notifications.length})
                </button>
                <button
                  onClick={() => setActiveTab('unread')}
                  className={`text-xs font-semibold px-3 py-1.5 rounded-xl flex items-center gap-1.5 transition-all cursor-pointer ${
                    activeTab === 'unread'
                      ? 'bg-indigo-600 text-white shadow-xs shadow-indigo-500/25'
                      : 'text-slate-500 hover:text-slate-800 hover:bg-slate-100'
                  }`}
                >
                  <span>Unread</span>
                  {notifications.filter((n) => !n.read).length > 0 && (
                    <span
                      className={`text-[10px] font-mono px-1.5 py-0.2 rounded-full font-bold ${
                        activeTab === 'unread' ? 'bg-white/25 text-white' : 'bg-indigo-100 text-indigo-700'
                      }`}
                    >
                      {notifications.filter((n) => !n.read).length}
                    </span>
                  )}
                </button>
              </div>
            </div>

            {/* List with generous spacing */}
            <div className="relative z-10 flex-1 overflow-y-auto p-2.5 space-y-1.5 max-h-[400px]">
              {loading ? (
                <div className="py-16 text-center text-slate-400">
                  <Loader2 className="w-7 h-7 animate-spin mx-auto mb-2.5 text-indigo-600" />
                  <span className="text-xs font-mono">Synchronizing alerts...</span>
                </div>
              ) : displayedNotifications.length === 0 ? (
                <div className="py-14 text-center px-4">
                  {/* Floating Bobbing Bell Animation */}
                  <div className="relative w-20 h-20 mx-auto mb-4 flex items-center justify-center">
                    <div className="absolute inset-0 rounded-full bg-indigo-500/5 animate-pulse" />
                    <div className="absolute inset-2 rounded-full bg-indigo-500/10" />
                    <motion.div
                      animate={{ y: [0, -7, 0], rotate: [0, 4, -4, 0] }}
                      transition={{ repeat: Infinity, duration: 3.5, ease: 'easeInOut' }}
                      className="relative z-10 w-12 h-12 rounded-2xl bg-gradient-to-tr from-indigo-50 to-cyan-50 border border-indigo-100 text-indigo-500 flex items-center justify-center shadow-xs"
                    >
                      <Bell className="w-6 h-6 text-indigo-500" />
                    </motion.div>
                  </div>

                  <p className="text-sm font-bold text-slate-800">
                    {activeTab === 'unread' ? 'No unread notifications' : 'All caught up, Champion!'}
                  </p>
                  <p className="text-xs text-slate-500 mt-1 max-w-xs mx-auto leading-relaxed">
                    {activeTab === 'unread'
                      ? 'You have inspected all combat and system notices.'
                      : 'No new battle challenges, friend pings, or rank alerts right now.'}
                  </p>
                </div>
              ) : (
                displayedNotifications.map((notif) => {
                  const cfg = TYPE_CONFIG[notif.type] || TYPE_CONFIG.SYSTEM;
                  const Icon = cfg.icon;

                  return (
                    <motion.div
                      key={notif.id}
                      whileHover={{ scale: 1.012, x: 2 }}
                      whileTap={{ scale: 0.99 }}
                      onClick={() => handleNotificationClick(notif)}
                      className={`p-3.5 rounded-2xl flex items-start gap-3 cursor-pointer transition-all duration-200 text-left border relative overflow-hidden group ${
                        !notif.read
                          ? 'bg-gradient-to-r from-indigo-50/70 via-cyan-50/40 to-white border-indigo-200/90 shadow-2xs'
                          : 'bg-white/70 hover:bg-slate-50/90 border-slate-100'
                      }`}
                    >
                      {/* Left Jewel Icon */}
                      <div
                        className={`w-9 h-9 rounded-xl shrink-0 flex items-center justify-center text-white bg-gradient-to-tr ${cfg.gradient} shadow-sm group-hover:scale-105 transition-transform duration-200`}
                      >
                        <Icon className="w-4.5 h-4.5" />
                      </div>

                      {/* Content */}
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center justify-between gap-1.5 mb-1">
                          <div className="flex items-center gap-1.5 min-w-0">
                            <span
                              className={`text-[9px] font-mono px-1.5 py-0.2 rounded-md font-bold border uppercase tracking-wider shrink-0 ${cfg.badgeBg}`}
                            >
                              {cfg.badgeText}
                            </span>
                            <p
                              className={`text-xs truncate ${
                                !notif.read ? 'font-extrabold text-slate-900' : 'font-semibold text-slate-700'
                              }`}
                            >
                              {notif.title}
                            </p>
                          </div>

                          <span className="text-[10px] font-mono text-slate-400 shrink-0 flex items-center gap-1">
                            <Clock className="w-2.5 h-2.5" />
                            {formatTime(notif.createdAt)}
                          </span>
                        </div>

                        <p className="text-xs text-slate-600 line-clamp-2 leading-relaxed">
                          {notif.message}
                        </p>

                        {/* Interactive Action for Duel Invites */}
                        {notif.type === 'BATTLE_INVITE' && notif.referenceId && !notif.read && (
                          <div
                            className="flex items-center gap-2 mt-2.5 pt-2 border-t border-amber-200/60"
                            onClick={(e) => e.stopPropagation()}
                          >
                            <button
                              onClick={() => handleAcceptDuel(notif)}
                              disabled={actionLoadingId === notif.id}
                              className="px-3 py-1 bg-gradient-to-r from-amber-500 to-orange-600 hover:from-amber-600 hover:to-orange-700 text-white text-[11px] font-bold rounded-lg shadow-xs shadow-amber-500/20 flex items-center gap-1.5 cursor-pointer disabled:opacity-50 transition-all hover:scale-105"
                            >
                              {actionLoadingId === notif.id ? (
                                <Loader2 className="w-3 h-3 animate-spin" />
                              ) : (
                                <Swords className="w-3 h-3" />
                              )}
                              <span>ACCEPT DUEL</span>
                            </button>
                            <button
                              onClick={() => handleDeclineDuel(notif)}
                              disabled={actionLoadingId === notif.id}
                              className="px-2.5 py-1 bg-slate-100 hover:bg-slate-200 text-slate-600 text-[11px] font-semibold rounded-lg cursor-pointer transition-colors"
                            >
                              Decline
                            </button>
                          </div>
                        )}
                      </div>

                      {/* Unread Glow Indicator */}
                      {!notif.read && (
                        <div className="w-2 h-2 rounded-full bg-indigo-600 shrink-0 mt-2 ring-2 ring-indigo-200 animate-pulse" />
                      )}
                    </motion.div>
                  );
                })
              )}
            </div>

            {/* Footer Status Bar */}
            <div className="relative z-10 px-4 py-2.5 border-t border-slate-100 bg-slate-50/80 backdrop-blur-sm flex items-center justify-between text-[11px] font-mono text-slate-500">
              <div className="flex items-center gap-2">
                <span className="relative flex h-2 w-2">
                  <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75" />
                  <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500" />
                </span>
                <span className="text-[10px] font-semibold text-slate-600">WebSocket Live</span>
              </div>

              <button
                onClick={() => {
                  setIsOpen(false);
                  navigate('/profile');
                }}
                className="text-[10px] font-bold text-indigo-600 hover:text-indigo-800 flex items-center gap-1 cursor-pointer"
              >
                <span>Activity Profile</span>
                <ExternalLink className="w-3 h-3" />
              </button>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

