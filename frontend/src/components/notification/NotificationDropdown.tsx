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
} from 'lucide-react';
import { notificationService } from '../../services/notificationService';
import { webSocketService } from '../../services/webSocketService';
import { useAuth } from '../../contexts/AuthContext';
import { NotificationItem, NotificationType } from '../../types/social';

const TYPE_ICONS: Record<NotificationType, React.ComponentType<{ className?: string }>> = {
  FRIEND_REQUEST: UserPlus,
  FRIEND_ACCEPTED: Users,
  BATTLE_INVITE: Swords,
  BATTLE_RESULT: Trophy,
  ACHIEVEMENT_UNLOCKED: Award,
  LEVEL_UP: Zap,
  TEAM_INVITE: Shield,
  TEAM_JOINED: ShieldAlert,
  SYSTEM: Bell,
};

export const NotificationDropdown: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(false);
  const [unreadCount, setUnreadCount] = useState<number>(0);
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [loading, setLoading] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

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
      const res = await notificationService.getNotifications(0, 15);
      setNotifications(res.content);
    } catch (e) {
      console.debug('Failed to fetch notifications', e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUnreadCount();

    // WebSocket real-time subscription
    let unsubscribeUser: (() => void) | undefined;
    let unsubscribeTopic: (() => void) | undefined;

    if (user?.username) {
      unsubscribeUser = webSocketService.subscribe('/user/queue/notifications', (notif: NotificationItem) => {
        setNotifications((prev) => [notif, ...prev.filter((n) => n.id !== notif.id)]);
        setUnreadCount((c) => c + 1);
      });
    }

    if (user?.id) {
      unsubscribeTopic = webSocketService.subscribe(`/topic/notifications.${user.id}`, (notif: NotificationItem) => {
        setNotifications((prev) => [notif, ...prev.filter((n) => n.id !== notif.id)]);
        setUnreadCount((c) => c + 1);
      });
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
      default:
        break;
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

  return (
    <div className="relative" ref={dropdownRef}>
      {/* Bell Button */}
      <button
        onClick={handleToggle}
        className="relative p-2 rounded-xl text-[#868A94] hover:text-white hover:bg-[#1A1B1F] transition-colors focus:outline-hidden"
        title="Notifications"
        aria-label="Notifications"
      >
        <Bell className="w-5 h-5" />
        {unreadCount > 0 && (
          <span className="absolute top-1 right-1 flex h-4 min-w-[16px] items-center justify-center rounded-full bg-white px-1 text-[10px] font-bold text-[#0B0C0E] shadow-glow-white">
            {unreadCount > 9 ? '9+' : unreadCount}
          </span>
        )}
      </button>

      {/* Popover Dropdown */}
      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, y: 10, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 8, scale: 0.95 }}
            transition={{ duration: 0.15 }}
            className="absolute right-0 mt-2 w-80 sm:w-96 rounded-2xl bg-[#141518] border border-[#27292F] shadow-luxury z-50 overflow-hidden text-[#F0F1F3]"
          >
            {/* Header */}
            <div className="flex items-center justify-between px-4 py-3 border-b border-[#212328] bg-[#111215]">
              <div className="flex items-center gap-2">
                <span className="font-bold text-sm text-white">Notifications</span>
                {unreadCount > 0 && (
                  <span className="px-1.5 py-0.5 text-[10px] font-bold font-mono bg-[#27292F] text-white border border-[#3E4148] rounded-full">
                    {unreadCount} new
                  </span>
                )}
              </div>
              {unreadCount > 0 && (
                <button
                  onClick={handleMarkAllRead}
                  className="text-xs text-[#D4D7DC] hover:text-white font-semibold flex items-center gap-1 transition-colors"
                >
                  <CheckCheck className="w-3.5 h-3.5" />
                  Mark all read
                </button>
              )}
            </div>

            {/* List */}
            <div className="max-h-[380px] overflow-y-auto divide-y divide-[#212328]">
              {loading ? (
                <div className="py-12 text-center text-[#868A94]">
                  <Loader2 className="w-6 h-6 animate-spin mx-auto mb-2 text-white" />
                  <span className="text-xs">Loading alerts...</span>
                </div>
              ) : notifications.length === 0 ? (
                <div className="py-12 text-center text-[#868A94]">
                  <Bell className="w-8 h-8 mx-auto mb-2 text-[#3E4148]" />
                  <p className="text-xs font-medium text-[#D4D7DC]">All caught up!</p>
                  <p className="text-[10px] text-[#868A94]">No new alerts or challenges</p>
                </div>
              ) : (
                notifications.map((notif) => {
                  const Icon = TYPE_ICONS[notif.type] || Bell;
                  return (
                    <div
                      key={notif.id}
                      onClick={() => handleNotificationClick(notif)}
                      className={`p-3.5 flex items-start gap-3 cursor-pointer transition-colors text-left ${
                        !notif.read ? 'bg-[#1A1B1F] hover:bg-[#202227]' : 'bg-transparent hover:bg-[#1A1B1F]/50'
                      }`}
                    >
                      <div
                        className="w-8 h-8 rounded-xl shrink-0 flex items-center justify-center bg-[#27292F] text-white border border-[#3E4148]"
                      >
                        <Icon className="w-4 h-4" />
                      </div>

                      <div className="flex-1 min-w-0">
                        <div className="flex items-center justify-between gap-1 mb-0.5">
                          <p
                            className={`text-xs truncate ${
                              !notif.read ? 'font-bold text-white' : 'font-medium text-[#D4D7DC]'
                            }`}
                          >
                            {notif.title}
                          </p>
                          <span className="text-[10px] font-mono text-[#6C717B] shrink-0">
                            {formatTime(notif.createdAt)}
                          </span>
                        </div>
                        <p className="text-[11px] text-[#A3A7AF] line-clamp-2 leading-relaxed">
                          {notif.message}
                        </p>
                      </div>

                      {!notif.read && (
                        <div className="w-2 h-2 rounded-full bg-white shrink-0 mt-1.5 shadow-glow-white" />
                      )}
                    </div>
                  );
                })
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};
