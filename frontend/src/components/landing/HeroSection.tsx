import React, { useState, useEffect } from 'react';
import { motion, useReducedMotion } from 'framer-motion';
import { Button } from '../ui/Button';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import {
  Swords,
  Play,
  Clock,
  CheckCircle2,
  Flame,
  Zap,
  Trophy,
  Shield,
  Sparkles,
} from 'lucide-react';

export const HeroSection: React.FC = () => {
  const shouldReduceMotion = useReducedMotion();
  const [countdown, setCountdown] = useState(222); // 03:42 in seconds
  const [isRunningTests, setIsRunningTests] = useState(false);
  const [testsPassed, setTestsPassed] = useState(3);
  const totalTests = 4;
  const [activeTab, setActiveTab] = useState<'solution' | 'tests'>('solution');

  // Countdown timer simulation
  useEffect(() => {
    const timer = setInterval(() => {
      setCountdown((prev) => (prev > 0 ? prev - 1 : 222));
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const formatCountdown = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const handleRunTests = () => {
    setIsRunningTests(true);
    setTimeout(() => {
      setIsRunningTests(false);
      setTestsPassed(4);
    }, 800);
  };

  // Animation variants respecting prefers-reduced-motion
  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: shouldReduceMotion ? 0 : 0.12,
        delayChildren: 0.1,
      },
    },
  };

  const itemVariants = {
    hidden: { opacity: 0, y: shouldReduceMotion ? 0 : 20 },
    visible: {
      opacity: 1,
      y: 0,
      transition: { duration: 0.5, ease: 'easeOut' },
    },
  };

  const floatingVariants = {
    float1: {
      y: shouldReduceMotion ? 0 : [0, -8, 0],
      transition: { duration: 4, repeat: Infinity, ease: 'easeInOut' },
    },
    float2: {
      y: shouldReduceMotion ? 0 : [0, 8, 0],
      transition: { duration: 4.5, repeat: Infinity, ease: 'easeInOut', delay: 0.5 },
    },
    float3: {
      y: shouldReduceMotion ? 0 : [0, -10, 0],
      transition: { duration: 5, repeat: Infinity, ease: 'easeInOut', delay: 1 },
    },
  };

  return (
    <section className="relative pt-6 pb-16 lg:pt-12 lg:pb-24 overflow-hidden">
      {/* Background ambient lighting */}
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[350px] bg-gradient-to-tr from-cyan-400/10 via-violet-400/10 to-emerald-400/10 blur-3xl pointer-events-none rounded-full" />

      <motion.div
        variants={containerVariants}
        initial="hidden"
        animate="visible"
        className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8"
      >
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-8 items-center">
          {/* ========================================================= */}
          {/* HERO LEFT SIDE: Copy & CTAs                                */}
          {/* ========================================================= */}
          <div className="lg:col-span-6 space-y-6 text-left">
            {/* Eyebrow */}
            <motion.div variants={itemVariants} className="inline-flex">
              <span className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full text-xs font-mono font-bold tracking-wider uppercase bg-cyan-50 text-cyan-800 border border-cyan-200/80 shadow-sm">
                <span className="w-2 h-2 rounded-full bg-cyan-500 animate-ping" />
                ⚡ THE DEVELOPER BATTLEFIELD
              </span>
            </motion.div>

            {/* Main Heading */}
            <motion.h1
              variants={itemVariants}
              className="text-5xl sm:text-7xl font-extrabold tracking-tight text-slate-900 leading-[1.05]"
            >
              CODE.
              <br />
              <span className="bg-gradient-to-r from-cyan-600 to-blue-600 bg-clip-text text-transparent">
                COMPETE.
              </span>
              <br />
              <span className="bg-gradient-to-r from-violet-600 to-emerald-600 bg-clip-text text-transparent">
                LEVEL UP.
              </span>
            </motion.h1>

            {/* Supporting Copy */}
            <motion.p
              variants={itemVariants}
              className="text-base sm:text-lg text-slate-600 leading-relaxed max-w-xl"
            >
              Turn coding into competition. Solve challenges, battle developers in real-time 1v1 duels,
              build your rating, and become the best in the arena.
            </motion.p>

            {/* CTA Buttons */}
            <motion.div
              variants={itemVariants}
              className="flex flex-wrap items-center gap-3.5 pt-2"
            >
              <Button
                variant="glow"
                size="lg"
                leftIcon={<Swords className="w-5 h-5" />}
                onClick={() => {
                  const el = document.getElementById('modes');
                  el?.scrollIntoView({ behavior: 'smooth' });
                }}
              >
                ENTER THE ARENA
              </Button>

              <Button
                variant="outline"
                size="lg"
                leftIcon={<Play className="w-4 h-4 text-cyan-600 fill-cyan-600" />}
                onClick={() => {
                  const el = document.getElementById('arena');
                  el?.scrollIntoView({ behavior: 'smooth' });
                }}
              >
                EXPLORE BATTLES
              </Button>
            </motion.div>

            {/* Supporting Statistics */}
            <motion.div
              variants={itemVariants}
              className="pt-6 border-t border-slate-200/80 grid grid-cols-3 gap-4"
            >
              <div>
                <p className="text-2xl sm:text-3xl font-extrabold font-mono text-slate-900">
                  10,000<span className="text-cyan-600">+</span>
                </p>
                <p className="text-xs font-semibold uppercase tracking-wider text-slate-500 font-mono mt-0.5">
                  Challenges
                </p>
              </div>

              <div>
                <p className="text-2xl sm:text-3xl font-extrabold font-mono text-slate-900">
                  Real-time
                </p>
                <p className="text-xs font-semibold uppercase tracking-wider text-slate-500 font-mono mt-0.5">
                  1v1 Battles
                </p>
              </div>

              <div>
                <p className="text-2xl sm:text-3xl font-extrabold font-mono text-slate-900">
                  Global
                </p>
                <p className="text-xs font-semibold uppercase tracking-wider text-slate-500 font-mono mt-0.5">
                  Rankings
                </p>
              </div>
            </motion.div>
          </div>

          {/* ========================================================= */}
          {/* HERO RIGHT SIDE: Interactive Live Battle Simulator         */}
          {/* ========================================================= */}
          <motion.div variants={itemVariants} className="lg:col-span-6 relative">
            {/* Floating Reward Badges */}
            <motion.div
              variants={floatingVariants}
              animate="float1"
              className="absolute -top-6 -left-4 sm:-left-6 z-20 hidden sm:flex items-center gap-2 bg-white/95 backdrop-blur-md px-3.5 py-2 rounded-xl shadow-lg border border-emerald-200/80 text-xs font-mono font-bold text-emerald-700"
            >
              <div className="w-5 h-5 rounded-md bg-emerald-100 flex items-center justify-center text-emerald-600">
                <Sparkles className="w-3.5 h-3.5" />
              </div>
              <span>+250 XP</span>
              <span className="text-[10px] text-slate-400 font-normal">Round Won</span>
            </motion.div>

            <motion.div
              variants={floatingVariants}
              animate="float2"
              className="absolute -top-8 right-2 sm:right-6 z-20 hidden sm:flex items-center gap-2 bg-white/95 backdrop-blur-md px-3.5 py-2 rounded-xl shadow-lg border border-cyan-200/80 text-xs font-mono font-bold text-cyan-700"
            >
              <div className="w-5 h-5 rounded-md bg-cyan-100 flex items-center justify-center text-cyan-600">
                <Shield className="w-3.5 h-3.5" />
              </div>
              <span>+42 Rating</span>
              <span className="text-[10px] text-slate-400 font-normal">MMR</span>
            </motion.div>

            <motion.div
              variants={floatingVariants}
              animate="float3"
              className="absolute -bottom-5 left-4 sm:left-8 z-20 flex items-center gap-2 bg-white/95 backdrop-blur-md px-3.5 py-2 rounded-xl shadow-lg border border-amber-200/80 text-xs font-mono font-bold text-amber-700"
            >
              <div className="w-5 h-5 rounded-md bg-amber-100 flex items-center justify-center text-amber-600">
                <Flame className="w-3.5 h-3.5" />
              </div>
              <span>COMBO ×3</span>
              <span className="text-[10px] text-slate-400 font-normal">Streak</span>
            </motion.div>

            <motion.div
              variants={floatingVariants}
              animate="float1"
              className="absolute -bottom-6 -right-2 sm:-right-4 z-20 hidden sm:flex items-center gap-2 bg-white/95 backdrop-blur-md px-3.5 py-2 rounded-xl shadow-lg border border-violet-200/80 text-xs font-mono font-bold text-violet-700"
            >
              <div className="w-5 h-5 rounded-md bg-violet-100 flex items-center justify-center text-violet-600">
                <Trophy className="w-3.5 h-3.5" />
              </div>
              <span>NEW MEDAL</span>
              <span className="text-[10px] text-slate-400 font-normal">Speed Demon</span>
            </motion.div>

            {/* Miniature Arena Battle Card */}
            <Card className="relative z-10 border-slate-200/90 shadow-2xl p-0 overflow-hidden bg-white/95 backdrop-blur-xl">
              {/* Arena Header: Status & Match Timer */}
              <div className="bg-slate-900 text-white px-5 py-3 flex items-center justify-between border-b border-slate-800">
                <div className="flex items-center gap-2">
                  <span className="w-2.5 h-2.5 rounded-full bg-rose-500 animate-pulse" />
                  <span className="text-xs font-mono font-bold tracking-widest text-slate-300 uppercase">
                    RANKED 1v1 DUEL
                  </span>
                </div>

                <div className="flex items-center gap-2 px-3 py-1 rounded-full bg-slate-800 border border-slate-700 text-xs font-mono font-bold text-cyan-400">
                  <Clock className="w-3.5 h-3.5 text-cyan-400" />
                  <span>{formatCountdown(countdown)}</span>
                </div>

                <Badge variant="purple" size="sm">
                  ROUND 1
                </Badge>
              </div>

              {/* VS Player Status Bar */}
              <div className="p-4 sm:p-5 bg-gradient-to-b from-slate-50 to-white border-b border-slate-200/80">
                <div className="grid grid-cols-12 items-center gap-2">
                  {/* Player 1: ARUN */}
                  <div className="col-span-5 space-y-1.5">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <div className="w-7 h-7 rounded-lg bg-gradient-to-tr from-cyan-600 to-blue-600 text-white font-bold text-xs flex items-center justify-center shadow-sm">
                          AR
                        </div>
                        <div>
                          <p className="text-xs font-bold text-slate-900 leading-none">ARUN</p>
                          <p className="text-[10px] font-mono text-cyan-600 font-semibold">
                            1842 MMR · DIA
                          </p>
                        </div>
                      </div>
                      <span className="text-[10px] font-mono font-bold text-emerald-600">
                        100% HP
                      </span>
                    </div>
                    {/* HP Bar */}
                    <div className="w-full h-2 bg-slate-200 rounded-full overflow-hidden">
                      <div className="h-full bg-gradient-to-r from-emerald-500 to-cyan-500 rounded-full w-full" />
                    </div>
                  </div>

                  {/* VS Badge */}
                  <div className="col-span-2 flex flex-col items-center justify-center">
                    <div className="w-8 h-8 rounded-full bg-slate-900 text-white text-[11px] font-mono font-extrabold flex items-center justify-center shadow-sm border-2 border-white">
                      VS
                    </div>
                  </div>

                  {/* Player 2: CODEWOLF */}
                  <div className="col-span-5 space-y-1.5 text-right">
                    <div className="flex items-center justify-between flex-row-reverse">
                      <div className="flex items-center gap-2 flex-row-reverse">
                        <div className="w-7 h-7 rounded-lg bg-gradient-to-tr from-violet-600 to-rose-600 text-white font-bold text-xs flex items-center justify-center shadow-sm">
                          CW
                        </div>
                        <div className="text-right">
                          <p className="text-xs font-bold text-slate-900 leading-none">CODEWOLF</p>
                          <p className="text-[10px] font-mono text-violet-600 font-semibold">
                            1817 MMR · DIA
                          </p>
                        </div>
                      </div>
                      <span className="text-[10px] font-mono font-bold text-amber-600">
                        75% HP
                      </span>
                    </div>
                    {/* HP Bar */}
                    <div className="w-full h-2 bg-slate-200 rounded-full overflow-hidden flex justify-end">
                      <div className="h-full bg-gradient-to-r from-amber-500 to-rose-500 rounded-full w-3/4" />
                    </div>
                  </div>
                </div>
              </div>

              {/* Challenge Overview Banner */}
              <div className="px-5 py-2.5 bg-cyan-50/50 border-b border-cyan-100 flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <Badge variant="warning" size="sm">
                    MEDIUM
                  </Badge>
                  <span className="text-xs font-semibold text-slate-800 truncate max-w-[240px] sm:max-w-xs">
                    Longest Substring Without Repeating Characters
                  </span>
                </div>
                <span className="text-[11px] font-mono text-slate-500">250 XP</span>
              </div>

              {/* Mini Code Editor Preview */}
              <div className="p-4 sm:p-5 bg-slate-950 font-mono text-xs text-slate-200">
                <div className="flex items-center justify-between pb-3 mb-3 border-b border-slate-800 text-[11px] text-slate-400">
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => setActiveTab('solution')}
                      className={`px-2 py-0.5 rounded transition-colors ${
                        activeTab === 'solution'
                          ? 'bg-slate-800 text-cyan-400 font-semibold'
                          : 'hover:text-slate-200'
                      }`}
                    >
                      solution.ts
                    </button>
                    <button
                      onClick={() => setActiveTab('tests')}
                      className={`px-2 py-0.5 rounded transition-colors ${
                        activeTab === 'tests'
                          ? 'bg-slate-800 text-cyan-400 font-semibold'
                          : 'hover:text-slate-200'
                      }`}
                    >
                      test_cases.json
                    </button>
                  </div>
                  <span className="text-emerald-400 flex items-center gap-1 font-semibold">
                    <Zap className="w-3 h-3" /> TypeScript 5.6
                  </span>
                </div>

                {activeTab === 'solution' ? (
                  <div className="space-y-1 leading-relaxed selection:bg-cyan-600">
                    <p className="text-slate-500">
                      <span className="text-slate-600 select-none mr-3">01</span>
                      <span className="text-violet-400">export function</span>{' '}
                      <span className="text-cyan-300">lengthOfLongestSubstring</span>(s:{' '}
                      <span className="text-amber-300">string</span>):{' '}
                      <span className="text-amber-300">number</span> {'{'}
                    </p>
                    <p className="text-slate-300">
                      <span className="text-slate-600 select-none mr-3">02</span>
                      &nbsp;&nbsp;<span className="text-violet-400">const</span> map ={' '}
                      <span className="text-violet-400">new</span>{' '}
                      <span className="text-cyan-300">Map</span>&lt;
                      <span className="text-amber-300">string</span>,{' '}
                      <span className="text-amber-300">number</span>&gt;();
                    </p>
                    <p className="text-slate-300">
                      <span className="text-slate-600 select-none mr-3">03</span>
                      &nbsp;&nbsp;<span className="text-violet-400">let</span> maxLen ={' '}
                      <span className="text-rose-400">0</span>, left ={' '}
                      <span className="text-rose-400">0</span>;
                    </p>
                    <p className="text-slate-300">
                      <span className="text-slate-600 select-none mr-3">04</span>
                      &nbsp;&nbsp;<span className="text-violet-400">for</span> (
                      <span className="text-violet-400">let</span> right ={' '}
                      <span className="text-rose-400">0</span>; right &lt; s.length; right++) {'{'}
                    </p>
                    <p className="text-slate-400">
                      <span className="text-slate-600 select-none mr-3">05</span>
                      &nbsp;&nbsp;&nbsp;&nbsp;
                      <span className="text-slate-500">// Sliding window index calculation</span>
                    </p>
                    <p className="text-slate-500">
                      <span className="text-slate-600 select-none mr-3">06</span>
                      &nbsp;&nbsp;{'}'}
                    </p>
                    <p className="text-slate-500">
                      <span className="text-slate-600 select-none mr-3">07</span>
                      {'}'}
                    </p>
                  </div>
                ) : (
                  <div className="space-y-1.5 text-slate-300 text-[11px]">
                    <p className="text-emerald-400">✔ Test 1: input = &quot;abcabcbb&quot; → expected = 3</p>
                    <p className="text-emerald-400">✔ Test 2: input = &quot;bbbbb&quot; → expected = 1</p>
                    <p className="text-emerald-400">✔ Test 3: input = &quot;pwwkew&quot; → expected = 3</p>
                    <p className={testsPassed === 4 ? 'text-emerald-400' : 'text-amber-400'}>
                      {testsPassed === 4 ? '✔ Test 4: input = "" → expected = 0' : '⏳ Test 4: input = "" → executing'}
                    </p>
                  </div>
                )}
              </div>

              {/* Arena Battle Controls */}
              <div className="px-5 py-3.5 bg-slate-50 border-t border-slate-200/80 flex flex-wrap items-center justify-between gap-3">
                {/* Test case progress */}
                <div className="flex items-center gap-2">
                  <span className="text-xs font-mono text-slate-600 font-semibold">
                    Test Suites:
                  </span>
                  <div className="flex items-center gap-1.5">
                    {Array.from({ length: totalTests }).map((_, i) => (
                      <span
                        key={i}
                        className={`w-2.5 h-2.5 rounded-full transition-colors ${
                          i < testsPassed ? 'bg-emerald-500' : 'bg-slate-300 animate-pulse'
                        }`}
                        title={`Test Case ${i + 1}`}
                      />
                    ))}
                  </div>
                  <span className="text-xs font-mono font-bold text-slate-800 ml-1">
                    {testsPassed}/{totalTests} Passed
                  </span>
                </div>

                {/* Buttons */}
                <div className="flex items-center gap-2">
                  <Button
                    size="sm"
                    variant="outline"
                    isLoading={isRunningTests}
                    leftIcon={<Play className="w-3.5 h-3.5 text-slate-700" />}
                    onClick={handleRunTests}
                  >
                    RUN
                  </Button>

                  <Button
                    size="sm"
                    variant="glow"
                    leftIcon={<CheckCircle2 className="w-3.5 h-3.5" />}
                    onClick={() => {
                      setTestsPassed(4);
                      alert('Solution Submitted! Flawless pass on all hidden test cases.');
                    }}
                  >
                    SUBMIT
                  </Button>
                </div>
              </div>
            </Card>
          </motion.div>
        </div>
      </motion.div>
    </section>
  );
};
