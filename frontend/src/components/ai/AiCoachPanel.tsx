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
          className="fixed inset-y-0 right-0 z-40 w-full max-w-md bg-white/95 backdrop-blur-2xl border-l border-slate-200/90 flex flex-col shadow-premium-hover"
        >
          {/* Header */}
          <div className="p-4 border-b border-slate-200/80 flex items-center justify-between bg-white/80">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-indigo-600 to-cyan-600 flex items-center justify-center text-white shadow-sm shadow-indigo-500/25">
                <Bot className="w-5 h-5" />
              </div>
              <div>
                <div className="flex items-center gap-2">
                  <h3 className="font-black text-slate-900 text-sm">AI Coach</h3>
                  <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-indigo-50 text-indigo-700 border border-indigo-200">
                    Socratic Mode
                  </span>
                </div>
                <p className="text-xs text-slate-500 flex items-center gap-1.5 mt-0.5">
                  <Zap className="w-3 h-3 text-amber-500" />
                  <span className="font-mono">{remainingQueries} / 50 daily queries left</span>
                </p>
              </div>
            </div>
            <button
              onClick={onClose}
              className="p-1.5 rounded-xl text-slate-400 hover:text-slate-800 hover:bg-slate-100 transition"
              aria-label="Close AI Coach"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          {/* Socratic Hint Progression Bar */}
          <div className="px-4 py-2.5 bg-slate-50/80 border-b border-slate-200/80 flex items-center justify-between text-xs">
            <span className="text-slate-500 font-bold">Hint Progression:</span>
            <div className="flex items-center gap-1.5">
              {[1, 2, 3].map((lvl) => (
                <div
                  key={lvl}
                  className={`flex items-center gap-1 px-2.5 py-0.5 rounded-full font-bold text-[11px] font-mono transition ${
                    currentHintLevel >= lvl
                      ? 'bg-amber-100 text-amber-900 border border-amber-300'
                      : 'bg-slate-100 text-slate-400'
                  }`}
                >
                  <span>Level {lvl}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Quick Action Chips */}
          <div className="p-3 bg-white/60 border-b border-slate-200/80 flex flex-wrap gap-2">
            <button
              disabled={loading}
              onClick={() => sendMessage('HINT')}
              className="px-2.5 py-1.5 rounded-xl text-xs font-bold bg-amber-50 text-amber-800 hover:bg-amber-100 border border-amber-200 flex items-center gap-1.5 transition disabled:opacity-50 shadow-2xs"
            >
              <Lightbulb className="w-3.5 h-3.5 text-amber-600" />
              <span>Hint {currentHintLevel}</span>
            </button>
            <button
              disabled={loading}
              onClick={() => sendMessage('DEBUG')}
              className="px-2.5 py-1.5 rounded-xl text-xs font-bold bg-rose-50 text-rose-800 hover:bg-rose-100 border border-rose-200 flex items-center gap-1.5 transition disabled:opacity-50 shadow-2xs"
            >
              <Bug className="w-3.5 h-3.5 text-rose-600" />
              <span>Debug Code</span>
            </button>
            <button
              disabled={loading}
              onClick={() => sendMessage('COMPLEXITY')}
              className="px-2.5 py-1.5 rounded-xl text-xs font-bold bg-purple-50 text-purple-800 hover:bg-purple-100 border border-purple-200 flex items-center gap-1.5 transition disabled:opacity-50 shadow-2xs"
            >
              <Clock className="w-3.5 h-3.5 text-purple-600" />
              <span>Complexity</span>
            </button>
            <button
              disabled={loading}
              onClick={() => sendMessage('EXPLAIN')}
              className="px-2.5 py-1.5 rounded-xl text-xs font-bold bg-indigo-50 text-indigo-800 hover:bg-indigo-100 border border-indigo-200 flex items-center gap-1.5 transition disabled:opacity-50 shadow-2xs"
            >
              <HelpCircle className="w-3.5 h-3.5 text-indigo-600" />
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
                      ? 'bg-gradient-to-r from-indigo-600 to-cyan-600 text-white shadow-sm shadow-indigo-500/25'
                      : 'bg-slate-50 text-slate-800 border border-slate-200/90 shadow-2xs'
                  }`}
                >
                  <div className="whitespace-pre-wrap font-sans">{msg.text}</div>
                </div>

                <span className="text-[10px] text-slate-400 mt-1 px-1 font-mono">{msg.timestamp}</span>

                {/* Suggested follow-up prompt chips */}
                {msg.suggestedFollowUps && msg.suggestedFollowUps.length > 0 && (
                  <div className="flex flex-wrap gap-1.5 mt-2">
                    {msg.suggestedFollowUps.map((chip, idx) => (
                      <button
                        key={idx}
                        disabled={loading}
                        onClick={() => sendMessage('EXPLAIN', chip)}
                        className="text-[11px] px-2.5 py-1 rounded-full bg-white hover:bg-slate-50 text-indigo-700 border border-indigo-200 shadow-2xs font-semibold transition"
                      >
                        {chip}
                      </button>
                    ))}
                  </div>
                )}
              </div>
            ))}

            {loading && (
              <div className="flex items-center gap-2 text-xs text-slate-500 bg-slate-50 p-3 rounded-2xl w-fit border border-slate-200 shadow-2xs">
                <RefreshCw className="w-3.5 h-3.5 animate-spin text-indigo-600" />
                <span>AI Coach is analyzing...</span>
              </div>
            )}
          </div>

          {/* Message Input Footer */}
          <form onSubmit={handleCustomSubmit} className="p-3 border-t border-slate-200/80 bg-white flex gap-2">
            <input
              type="text"
              value={inputMessage}
              onChange={(e) => setInputMessage(e.target.value)}
              placeholder="Ask a question about the problem..."
              disabled={loading}
              className="flex-1 bg-slate-50 border border-slate-200 rounded-xl px-3.5 py-2 text-xs text-slate-800 placeholder-slate-400 focus:outline-none focus:border-indigo-600 transition disabled:opacity-50"
            />
            <button
              type="submit"
              disabled={loading || !inputMessage.trim()}
              className="px-3.5 py-2 rounded-xl bg-gradient-to-r from-indigo-600 to-cyan-600 hover:from-indigo-700 hover:to-cyan-700 text-white transition disabled:opacity-40 flex items-center justify-center shadow-sm shadow-indigo-500/25"
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
