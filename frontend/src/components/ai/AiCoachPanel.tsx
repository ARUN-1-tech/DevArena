import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Bot,
  Lightbulb,
  Bug,
  HelpCircle,
  Clock,
  Send,
  X,
  RefreshCw,
  Zap,
} from 'lucide-react';
import { aiCoachService } from '../../services/aiCoachService';
import { AiCoachRequestType } from '../../types/ai';

interface AiCoachPanelProps {
  challengeId?: string;
  currentCode: string;
  isOpen: boolean;
  onClose: () => void;
}

interface Message {
  id: string;
  sender: 'ai' | 'user';
  text: string;
  timestamp: string;
  suggestedFollowUps?: string[];
  hintLevel?: number;
}

export const AiCoachPanel: React.FC<AiCoachPanelProps> = ({
  challengeId,
  currentCode,
  isOpen,
  onClose,
}) => {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: 'welcome',
      sender: 'ai',
      text: "👋 Hi! I'm your **DevArena AI Coach**.\n\nI'm here to guide your thinking with Socratic hints, complexity analysis, and debugging tips — without giving away answers.\n\nHow can I assist your problem-solving today?",
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      suggestedFollowUps: ['Give me a hint', 'Explain the problem', 'What is the target complexity?'],
    },
  ]);
  const [inputMessage, setInputMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [remainingQueries, setRemainingQueries] = useState<number>(50);
  const [currentHintLevel, setCurrentHintLevel] = useState<number>(1);

  const sendMessage = async (type: AiCoachRequestType, userText?: string) => {
    if (loading) return;

    const displayPrompt = userText || (
      type === 'HINT' ? `Requesting Hint ${currentHintLevel}` :
      type === 'EXPLAIN' ? 'Explain this problem to me' :
      type === 'DEBUG' ? 'Check my code for bugs' :
      type === 'COMPLEXITY' ? 'What complexity should I target?' :
      'Explain optimal approach'
    );

    const userMsg: Message = {
      id: Date.now().toString(),
      sender: 'user',
      text: displayPrompt,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    };

    setMessages((prev) => [...prev, userMsg]);
    setInputMessage('');
    setLoading(true);

    try {
      const response = await aiCoachService.askCoach({
        challengeId,
        requestType: type,
        currentCode,
        userMessage: userText,
      });

      setRemainingQueries(response.remainingDailyQueries);
      if (response.hintLevel) {
        setCurrentHintLevel(response.hintLevel);
      }

      const aiMsg: Message = {
        id: (Date.now() + 1).toString(),
        sender: 'ai',
        text: response.reply,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        suggestedFollowUps: response.suggestedFollowUps,
        hintLevel: response.hintLevel,
      };

      setMessages((prev) => [...prev, aiMsg]);
    } catch (err: unknown) {
      const errorText = err instanceof Error ? err.message : 'AI Coach is temporarily unavailable. Please try again.';
      const errorMsg: Message = {
        id: (Date.now() + 1).toString(),
        sender: 'ai',
        text: `⚠️ ${errorText}`,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      };
      setMessages((prev) => [...prev, errorMsg]);
    } finally {
      setLoading(false);
    }
  };

  const handleCustomSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputMessage.trim()) return;
    sendMessage('EXPLAIN', inputMessage.trim());
  };

  return (
    <AnimatePresence>
      {isOpen && (
        <motion.div
          initial={{ x: 400, opacity: 0 }}
          animate={{ x: 0, opacity: 1 }}
          exit={{ x: 400, opacity: 0 }}
          transition={{ type: 'spring', damping: 25, stiffness: 200 }}
          className="fixed inset-y-0 right-0 z-40 w-full max-w-md bg-slate-900/95 backdrop-blur-md border-l border-slate-800 flex flex-col shadow-2xl"
        >
          {/* Header */}
          <div className="p-4 border-b border-slate-800/80 flex items-center justify-between bg-slate-900/60">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-cyan-500 to-indigo-600 flex items-center justify-center text-white shadow-lg shadow-indigo-500/20">
                <Bot className="w-5 h-5" />
              </div>
              <div>
                <div className="flex items-center gap-2">
                  <h3 className="font-semibold text-slate-100 text-sm">AI Coach</h3>
                  <span className="px-1.5 py-0.5 rounded text-[10px] font-medium bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
                    Socratic Mode
                  </span>
                </div>
                <p className="text-xs text-slate-400 flex items-center gap-1.5 mt-0.5">
                  <Zap className="w-3 h-3 text-amber-400" />
                  <span>{remainingQueries} / 50 daily queries left</span>
                </p>
              </div>
            </div>
            <button
              onClick={onClose}
              className="p-1.5 rounded-lg text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition"
              aria-label="Close AI Coach"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          {/* Socratic Hint Progression Bar */}
          <div className="px-4 py-2.5 bg-slate-800/40 border-b border-slate-800/60 flex items-center justify-between text-xs">
            <span className="text-slate-400 font-medium">Hint Progression:</span>
            <div className="flex items-center gap-1.5">
              {[1, 2, 3].map((lvl) => (
                <div
                  key={lvl}
                  className={`flex items-center gap-1 px-2 py-0.5 rounded-full font-medium text-[11px] transition ${
                    currentHintLevel >= lvl
                      ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30'
                      : 'bg-slate-800 text-slate-500'
                  }`}
                >
                  <span>Level {lvl}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Quick Action Chips */}
          <div className="p-3 bg-slate-900/40 border-b border-slate-800/60 flex flex-wrap gap-2">
            <button
              disabled={loading}
              onClick={() => sendMessage('HINT')}
              className="px-2.5 py-1.5 rounded-lg text-xs font-medium bg-amber-500/10 text-amber-300 hover:bg-amber-500/20 border border-amber-500/20 flex items-center gap-1.5 transition disabled:opacity-50"
            >
              <Lightbulb className="w-3.5 h-3.5" />
              <span>Hint {currentHintLevel}</span>
            </button>
            <button
              disabled={loading}
              onClick={() => sendMessage('DEBUG')}
              className="px-2.5 py-1.5 rounded-lg text-xs font-medium bg-rose-500/10 text-rose-300 hover:bg-rose-500/20 border border-rose-500/20 flex items-center gap-1.5 transition disabled:opacity-50"
            >
              <Bug className="w-3.5 h-3.5" />
              <span>Debug Code</span>
            </button>
            <button
              disabled={loading}
              onClick={() => sendMessage('COMPLEXITY')}
              className="px-2.5 py-1.5 rounded-lg text-xs font-medium bg-purple-500/10 text-purple-300 hover:bg-purple-500/20 border border-purple-500/20 flex items-center gap-1.5 transition disabled:opacity-50"
            >
              <Clock className="w-3.5 h-3.5" />
              <span>Complexity</span>
            </button>
            <button
              disabled={loading}
              onClick={() => sendMessage('EXPLAIN')}
              className="px-2.5 py-1.5 rounded-lg text-xs font-medium bg-blue-500/10 text-blue-300 hover:bg-blue-500/20 border border-blue-500/20 flex items-center gap-1.5 transition disabled:opacity-50"
            >
              <HelpCircle className="w-3.5 h-3.5" />
              <span>Explain</span>
            </button>
          </div>

          {/* Chat Messages */}
          <div className="flex-1 overflow-y-auto p-4 space-y-4">
            {messages.map((msg) => (
              <div
                key={msg.id}
                className={`flex flex-col ${msg.sender === 'user' ? 'items-end' : 'items-start'}`}
              >
                <div
                  className={`max-w-[88%] rounded-2xl p-3.5 text-xs leading-relaxed ${
                    msg.sender === 'user'
                      ? 'bg-cyan-600 text-white shadow-md shadow-cyan-600/20'
                      : 'bg-slate-800/90 text-slate-200 border border-slate-700/60 shadow-md'
                  }`}
                >
                  <div className="whitespace-pre-wrap font-sans">{msg.text}</div>
                </div>

                <span className="text-[10px] text-slate-500 mt-1 px-1">{msg.timestamp}</span>

                {/* Suggested follow-up prompt chips */}
                {msg.suggestedFollowUps && msg.suggestedFollowUps.length > 0 && (
                  <div className="flex flex-wrap gap-1.5 mt-2">
                    {msg.suggestedFollowUps.map((chip, idx) => (
                      <button
                        key={idx}
                        disabled={loading}
                        onClick={() => sendMessage('EXPLAIN', chip)}
                        className="text-[11px] px-2.5 py-1 rounded-full bg-slate-800 hover:bg-slate-700 text-cyan-300 border border-slate-700 transition"
                      >
                        {chip}
                      </button>
                    ))}
                  </div>
                )}
              </div>
            ))}

            {loading && (
              <div className="flex items-center gap-2 text-xs text-slate-400 bg-slate-800/60 p-3 rounded-2xl w-fit border border-slate-700/40">
                <RefreshCw className="w-3.5 h-3.5 animate-spin text-cyan-400" />
                <span>AI Coach is analyzing...</span>
              </div>
            )}
          </div>

          {/* Message Input Footer */}
          <form onSubmit={handleCustomSubmit} className="p-3 border-t border-slate-800 bg-slate-900/80 flex gap-2">
            <input
              type="text"
              value={inputMessage}
              onChange={(e) => setInputMessage(e.target.value)}
              placeholder="Ask a question about the problem..."
              disabled={loading}
              className="flex-1 bg-slate-950 border border-slate-800 rounded-xl px-3.5 py-2 text-xs text-slate-200 placeholder-slate-500 focus:outline-none focus:border-cyan-500 transition disabled:opacity-50"
            />
            <button
              type="submit"
              disabled={loading || !inputMessage.trim()}
              className="px-3.5 py-2 rounded-xl bg-cyan-600 hover:bg-cyan-500 text-white transition disabled:opacity-40 flex items-center justify-center"
              aria-label="Send message"
            >
              <Send className="w-4 h-4" />
            </button>
          </form>
        </motion.div>
      )}
    </AnimatePresence>
  );
};
