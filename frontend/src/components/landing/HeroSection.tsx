import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, useReducedMotion, AnimatePresence } from 'framer-motion';
import { Button } from '../ui/Button';
import { Card } from '../ui/Card';
import { Swords, Clock, CheckCircle2 } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';

export const HeroSection: React.FC = () => {
  const shouldReduceMotion = useReducedMotion();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [countdown, setCountdown] = useState(222); // 03:42
  const [testsPassed, setTestsPassed] = useState(3);
  const [showXpReward, setShowXpReward] = useState(false);

  // Timer countdown simulation
  useEffect(() => {
    const timer = setInterval(() => {
      setCountdown((prev) => (prev > 0 ? prev - 1 : 222));
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const formatTime = (seconds: number) => {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  const handleSubmit = () => {
    setTestsPassed(4);
    setShowXpReward(true);
    setTimeout(() => setShowXpReward(false), 2400);
  };

  return (
    <section className="relative pt-8 pb-16 lg:pt-16 lg:pb-24 overflow-hidden">
      {/* Soft decorative background circles */}
      <div className="absolute top-10 left-1/2 -translate-x-1/2 w-[500px] h-[300px] bg-gradient-to-r from-cyan-400/10 via-violet-400/10 to-amber-400/10 blur-3xl pointer-events-none rounded-full" />

      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-8 items-center">
          {/* ========================================================= */}
          {/* LEFT: Heading, short copy, 2 clean CTAs                   */}
          {/* ========================================================= */}
          <motion.div
            initial={{ opacity: 0, y: shouldReduceMotion ? 0 : 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, ease: 'easeOut' }}
            className="lg:col-span-6 space-y-6 text-left"
          >
            {/* Eyebrow */}
            <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full text-xs font-mono font-bold uppercase tracking-wider bg-cyan-50 text-cyan-800 border border-cyan-200">
              <span className="w-2 h-2 rounded-full bg-cyan-500 animate-pulse" />
              ⚡ THE DEVELOPER BATTLEFIELD
            </div>

            {/* Main Heading */}
            <h1 className="text-5xl sm:text-6xl lg:text-7xl font-extrabold tracking-tight text-slate-900 leading-[1.05]">
              CODE.
              <br />
              <span className="bg-gradient-to-r from-cyan-600 to-blue-600 bg-clip-text text-transparent">
                COMPETE.
              </span>
              <br />
              <span className="bg-gradient-to-r from-violet-600 to-emerald-600 bg-clip-text text-transparent">
                LEVEL UP.
              </span>
            </h1>

            {/* Short Supporting Text */}
            <p className="text-base sm:text-lg text-slate-600 leading-relaxed max-w-md">
              Turn coding into a game. Solve challenges, battle developers, and level up your skills.
            </p>

            {/* Only TWO clean buttons */}
            <div className="flex flex-wrap items-center gap-3 pt-2">
              <Button
                variant="glow"
                size="lg"
                leftIcon={<Swords className="w-5 h-5" />}
                onClick={() => {
                  if (isAuthenticated) {
                    navigate('/home');
                  } else {
                    navigate('/login');
                  }
                }}
              >
                ENTER THE ARENA
              </Button>

              <Button
                variant="outline"
                size="lg"
                onClick={() => {
                  const el = document.getElementById('arena');
                  el?.scrollIntoView({ behavior: 'smooth' });
                }}
              >
                SEE HOW IT WORKS
              </Button>
            </div>
          </motion.div>

          {/* ========================================================= */}
          {/* RIGHT: Simplified, friendly interactive battle card       */}
          {/* ========================================================= */}
          <motion.div
            initial={{ opacity: 0, scale: 0.96 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.5, delay: 0.15 }}
            className="lg:col-span-6 relative"
          >
            {/* Animated Floating XP Pop on Submit */}
            <AnimatePresence>
              {showXpReward && (
                <motion.div
                  initial={{ opacity: 0, y: 10, scale: 0.8 }}
                  animate={{ opacity: 1, y: -24, scale: 1.05 }}
                  exit={{ opacity: 0, y: -40, scale: 0.9 }}
                  transition={{ duration: 0.6 }}
                  className="absolute -top-6 right-8 z-30 flex items-center gap-1.5 px-4 py-2 rounded-xl bg-emerald-500 text-white font-mono font-bold text-sm shadow-lg shadow-emerald-500/30 pointer-events-none"
                >
                  <CheckCircle2 className="w-4 h-4" />
                  <span>+250 XP</span>
                </motion.div>
              )}
            </AnimatePresence>

            <Card className="p-0 border-slate-200/90 shadow-xl overflow-hidden bg-white">
              {/* Card Header: Players & Countdown */}
              <div className="bg-slate-900 text-white p-5">
                <div className="flex items-center justify-between">
                  {/* Player 1: ARUN */}
                  <div className="flex items-center gap-2.5">
                    <motion.div
                      animate={shouldReduceMotion ? {} : { y: [0, -3, 0] }}
                      transition={{ duration: 2.5, repeat: Infinity, ease: 'easeInOut' }}
                      className="w-10 h-10 rounded-xl bg-cyan-500 text-white font-bold flex items-center justify-center text-sm shadow-sm"
                    >
                      AR
                    </motion.div>
                    <div>
                      <p className="text-sm font-bold leading-tight">ARUN</p>
                      <p className="text-xs font-mono text-cyan-400 font-semibold">1842</p>
                    </div>
                  </div>

                  {/* VS + Countdown */}
                  <div className="text-center">
                    <span className="text-xs font-mono font-bold px-2 py-0.5 rounded bg-slate-800 text-slate-400">
                      VS
                    </span>
                    <p className="text-xs font-mono text-cyan-300 font-semibold flex items-center justify-center gap-1 mt-1">
                      <Clock className="w-3 h-3" />
                      {formatTime(countdown)}
                    </p>
                  </div>

                  {/* Player 2: CODEWOLF */}
                  <div className="flex items-center gap-2.5 text-right">
                    <div>
                      <p className="text-sm font-bold leading-tight">CODEWOLF</p>
                      <p className="text-xs font-mono text-violet-400 font-semibold">1817</p>
                    </div>
                    <motion.div
                      animate={shouldReduceMotion ? {} : { y: [0, -3, 0] }}
                      transition={{ duration: 2.5, repeat: Infinity, ease: 'easeInOut', delay: 0.3 }}
                      className="w-10 h-10 rounded-xl bg-violet-600 text-white font-bold flex items-center justify-center text-sm shadow-sm"
                    >
                      CW
                    </motion.div>
                  </div>
                </div>
              </div>

              {/* Challenge Title */}
              <div className="px-5 py-3 bg-slate-50 border-b border-slate-100 flex items-center justify-between">
                <span className="text-xs font-semibold text-slate-700">
                  Longest Substring Without Repeating Characters
                </span>
                <span className="text-[11px] font-mono text-cyan-700 bg-cyan-50 px-2 py-0.5 rounded font-semibold border border-cyan-200">
                  Medium
                </span>
              </div>

              {/* Clean Code Preview */}
              <div className="p-5 bg-slate-950 font-mono text-xs text-slate-300 leading-relaxed">
                <p>
                  <span className="text-violet-400">function</span>{' '}
                  <span className="text-cyan-300">lengthOfLongestSubstring</span>(s:{' '}
                  <span className="text-amber-300">string</span>) {'{'}
                </p>
                <p className="pl-4 text-slate-400">
                  <span className="text-violet-400">let</span> maxLen = 0, left = 0;
                </p>
                <p className="pl-4 text-slate-400">
                  <span className="text-violet-400">const</span> seen ={' '}
                  <span className="text-violet-400">new</span> Set();
                </p>
                <p className="pl-4 text-slate-500">// Sliding window evaluation</p>
                <p>{'}'}</p>
              </div>

              {/* Card Footer: Simple Tests Status & Submit Button */}
              <div className="px-5 py-3.5 bg-white flex items-center justify-between border-t border-slate-100">
                <div className="flex items-center gap-2">
                  <span
                    className={`w-2.5 h-2.5 rounded-full ${
                      testsPassed === 4 ? 'bg-emerald-500' : 'bg-amber-400'
                    }`}
                  />
                  <span className="text-xs font-mono font-medium text-slate-600">
                    {testsPassed} / 4 tests passed
                  </span>
                </div>

                <Button
                  size="sm"
                  variant="glow"
                  onClick={handleSubmit}
                  leftIcon={<CheckCircle2 className="w-4 h-4" />}
                >
                  SUBMIT
                </Button>
              </div>
            </Card>
          </motion.div>
        </div>
      </div>
    </section>
  );
};
