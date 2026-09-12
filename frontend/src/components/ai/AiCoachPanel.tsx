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
          className="fixed inset-y-0 right-0 z-40 w-full max-w-md bg-sandwich-950/95 backdrop-blur-xl border-l border-sandwich-800 flex flex-col shadow-luxury"
        >
          {/* Header */}
          <div className="p-4 border-b border-sandwich-800 flex items-center justify-between bg-sandwich-950/80">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-xl bg-sandwich-800 border border-sandwich-700 flex items-center justify-center text-sandwich-100 shadow-glow-silver">
                <Bot className="w-5 h-5 text-sandwich-100" />
              </div>
              <div>
                <div className="flex items-center gap-2">
                  <h3 className="font-semibold text-sandwich-100 text-sm">AI Coach</h3>
                  <span className="px-1.5 py-0.5 rounded text-[10px] font-mono font-bold bg-sandwich-800 text-sandwich-200 border border-sandwich-700">
                    Socratic Mode
                  </span>
                </div>
                <p className="text-xs text-sandwich-400 flex items-center gap-1.5 mt-0.5 font-mono">
                  <Zap className="w-3 h-3 text-sandwich-300" />
                  <span>{remainingQueries} / 50 daily queries left</span>
                </p>
              </div>
            </div>
            <button
              onClick={onClose}
              className="p-1.5 rounded-lg text-sandwich-400 hover:text-sandwich-100 hover:bg-sandwich-800 transition"
              aria-label="Close AI Coach"
            >
              <X className="w-5 h-5" />
            </button>
          </div>

          {/* Socratic Hint Progression Bar */}
          <div className="px-4 py-2.5 bg-sandwich-900/60 border-b border-sandwich-800 flex items-center justify-between text-xs">
            <span className="text-sandwich-400 font-medium">Hint Progression:</span>
            <div className="flex items-center gap-1.5">
              {[1, 2, 3].map((lvl) => (
                <div
                  key={lvl}
                  className={`flex items-center gap-1 px-2.5 py-0.5 rounded-full font-mono font-bold text-[11px] transition ${
                    currentHintLevel >= lvl
                      ? 'bg-sandwich-100 text-sandwich-950 shadow-glow-white'
                      : 'bg-sandwich-800 text-sandwich-500'
                  }`}
                >
                  <span>Level {lvl}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Quick Action Chips */}
          <div className="p-3 bg-sandwich-950/60 border-b border-sandwich-800 flex flex-wrap gap-2">
            <button
              disabled={loading}
              onClick={() => sendMessage('HINT')}
              className="px-2.5 py-1.5 rounded-lg text-xs font-mono font-medium bg-sandwich-900 text-sandwich-200 hover:bg-sandwich-800 border border-sandwich-700 flex items-center gap-1.5 transition disabled:opacity-50"
            >
              <Lightbulb className="w-3.5 h-3.5 text-sandwich-300" />
              <span>Hint {currentHintLevel}</span>
            </button>
            <button
              disabled={loading}
              onClick={() => sendMessage('DEBUG')}
              className="px-2.5 py-1.5 rounded-lg text-xs font-mono font-medium bg-sandwich-900 text-sandwich-200 hover:bg-sandwich-800 border border-sandwich-700 flex items-center gap-1.5 transition disabled:opacity-50"
            >
              <Bug className="w-3.5 h-3.5 text-sandwich-300" />
              <span>Debug Code</span>
            </button>
            <button
              disabled={loading}
              onClick={() => sendMessage('COMPLEXITY')}
              className="px-2.5 py-1.5 rounded-lg text-xs font-mono font-medium bg-sandwich-900 text-sandwich-200 hover:bg-sandwich-800 border border-sandwich-700 flex items-center gap-1.5 transition disabled:opacity-50"
            >
              <Clock className="w-3.5 h-3.5 text-sandwich-300" />
              <span>Complexity</span>
            </button>
            <button
              disabled={loading}
              onClick={() => sendMessage('EXPLAIN')}
              className="px-2.5 py-1.5 rounded-lg text-xs font-mono font-medium bg-sandwich-900 text-sandwich-200 hover:bg-sandwich-800 border border-sandwich-700 flex items-center gap-1.5 transition disabled:opacity-50"
            >
              <HelpCircle className="w-3.5 h-3.5 text-sandwich-300" />
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
                      ? 'bg-sandwich-100 text-sandwich-950 font-medium shadow-glow-white'
                      : 'bg-sandwich-900 text-sandwich-200 border border-sandwich-800 shadow-luxury'
                  }`}
                >
                  <div className="whitespace-pre-wrap font-sans">{msg.text}</div>
                </div>

                <span className="text-[10px] font-mono text-sandwich-500 mt-1 px-1">{msg.timestamp}</span>

                {/* Suggested follow-up prompt chips */}
                {msg.suggestedFollowUps && msg.suggestedFollowUps.length > 0 && (
                  <div className="flex flex-wrap gap-1.5 mt-2">
                    {msg.suggestedFollowUps.map((chip, idx) => (
                      <button
                        key={idx}
                        disabled={loading}
                        onClick={() => sendMessage('EXPLAIN', chip)}
                        className="text-[11px] font-mono px-2.5 py-1 rounded-full bg-sandwich-850 hover:bg-sandwich-800 text-sandwich-200 border border-sandwich-700 transition"
                      >
                        {chip}
                      </button>
                    ))}
                  </div>
                )}
              </div>
            ))}

            {loading && (
              <div className="flex items-center gap-2 text-xs text-sandwich-300 bg-sandwich-900 p-3 rounded-2xl w-fit border border-sandwich-800">
                <RefreshCw className="w-3.5 h-3.5 animate-spin text-sandwich-200" />
                <span>AI Coach is analyzing...</span>
              </div>
            )}
          </div>

          {/* Message Input Footer */}
          <form onSubmit={handleCustomSubmit} className="p-3 border-t border-sandwich-800 bg-sandwich-950/80 flex gap-2">
            <input
              type="text"
              value={inputMessage}
              onChange={(e) => setInputMessage(e.target.value)}
              placeholder="Ask a question about the problem..."
              disabled={loading}
              className="flex-1 bg-sandwich-900 border border-sandwich-700 rounded-xl px-3.5 py-2 text-xs text-sandwich-100 placeholder-sandwich-500 focus:outline-none focus:border-sandwich-400 transition disabled:opacity-50"
            />
            <button
              type="submit"
              disabled={loading || !inputMessage.trim()}
              className="px-3.5 py-2 rounded-xl bg-sandwich-50 hover:bg-white text-sandwich-950 font-bold transition disabled:opacity-40 flex items-center justify-center shadow-glow-white"
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
