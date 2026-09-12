import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Sparkles, Heart, Moon, Coffee } from 'lucide-react';

type PetMood = 'happy' | 'running' | 'sleeping' | 'curious' | 'coding';

const SPEECH_LINES: Record<PetMood, string[]> = {
  happy: [
    'You got this, Champion! ✨',
    'Ready for the next duel!',
    'Clean code, happy soul! 🐾',
    'Keep coding! 🚀',
  ],
  running: [
    'Zooming through test cases! 💨',
    'Speed run mode: ON!',
    'Catch me if you can! 🏃',
    'Running the benchmarks!',
  ],
  sleeping: [
    'Zzz... dreaming of clean syntax...',
    'Zzz... garbage collector working...',
    'Resting up for ranked arena...',
    'Zzz... 100% test coverage...',
  ],
  curious: [
    'Sniff sniff... smells like an O(1) algorithm!',
    'What are you coding today? 👀',
    'Look at those MMR points rise!',
    'Checking your battle streak!',
  ],
  coding: [
    'Typing at 200 WPM! ⌨️',
    'Git commit: pet the baby animal! ❤️',
    'No bugs detected in your spirit!',
    'Paws on keyboard!',
  ],
};

export const SidebarPetCompanion: React.FC = () => {
  const [mood, setMood] = useState<PetMood>('happy');
  const [speech, setSpeech] = useState<string>('Hi friend! Coding today? ✨');
  const [showSpeech, setShowSpeech] = useState(true);
  const [hearts, setHearts] = useState<{ id: number; x: number }[]>([]);
  const [petCount, setPetCount] = useState(0);

  // Cycle moods naturally over time
  useEffect(() => {
    const moods: PetMood[] = ['happy', 'running', 'curious', 'sleeping', 'coding'];
    const interval = setInterval(() => {
      setMood((prevMood) => {
        const remaining = moods.filter((m) => m !== prevMood);
        const next = remaining[Math.floor(Math.random() * remaining.length)];
        const lines = SPEECH_LINES[next];
        const randomLine = lines[Math.floor(Math.random() * lines.length)];
        setSpeech(randomLine);
        setShowSpeech(true);
        setTimeout(() => setShowSpeech(false), 4500);
        return next;
      });
    }, 12000);

    return () => clearInterval(interval);
  }, []);

  // Handle clicking / petting the animal
  const handlePet = () => {
    setPetCount((prev) => prev + 1);
    const newHeart = { id: Date.now() + Math.random(), x: Math.random() * 40 - 20 };
    setHearts((prev) => [...prev.slice(-5), newHeart]);

    const reactions = [
      'Purr... thank you for the petting! ❤️',
      'Yay! Level up friendship! ⭐',
      '+100 Morale Boost activated! 🔥',
      'You are my favorite developer! 🐾',
      'Bugs vanish when you smile! ✨',
    ];
    setSpeech(reactions[Math.floor(Math.random() * reactions.length)]);
    setShowSpeech(true);
    setMood('happy');

    setTimeout(() => {
      setHearts((prev) => prev.filter((h) => h.id !== newHeart.id));
    }, 1500);
  };

  return (
    <div className="px-3 py-2 flex flex-col items-center select-none relative group">
      {/* Floating Hearts Animation */}
      <AnimatePresence>
        {hearts.map((h) => (
          <motion.div
            key={h.id}
            initial={{ opacity: 1, y: 0, scale: 0.8, x: h.x }}
            animate={{ opacity: 0, y: -45, scale: 1.3 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 1.2, ease: 'easeOut' }}
            className="absolute pointer-events-none z-30 text-rose-500"
          >
            <Heart className="w-4 h-4 fill-rose-500" />
          </motion.div>
        ))}
      </AnimatePresence>

      {/* Cute Interactive Speech Bubble */}
      <AnimatePresence>
        {showSpeech && (
          <motion.div
            initial={{ opacity: 0, y: 6, scale: 0.9 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: -4, scale: 0.9 }}
            className="mb-2 relative bg-white/95 backdrop-blur-md px-3 py-1.5 rounded-2xl border border-indigo-100 shadow-sm text-center max-w-[210px] z-20 cursor-pointer"
            onClick={() => setShowSpeech(false)}
          >
            <p className="text-[11px] font-semibold text-slate-700 leading-tight">
              {speech}
            </p>
            {/* Bubble Tail */}
            <div className="absolute -bottom-1 left-1/2 -translate-x-1/2 w-2 h-2 bg-white border-b border-r border-indigo-100 rotate-45" />
          </motion.div>
        )}
      </AnimatePresence>

      {/* Pet Island & Creature */}
      <div
        onClick={handlePet}
        role="button"
        tabIndex={0}
        title="Click to pet your DevArena companion!"
        className="relative w-full max-w-[210px] py-2.5 px-3 rounded-2xl bg-gradient-to-b from-indigo-50/60 via-purple-50/40 to-cyan-50/40 border border-indigo-100/70 hover:border-indigo-200 transition-all cursor-pointer shadow-2xs hover:shadow-xs flex flex-col items-center group/pet"
      >
        {/* Status indicator badge */}
        <div className="w-full flex items-center justify-between mb-1 text-[9px] font-mono font-bold text-indigo-600/80">
          <span className="flex items-center gap-1">
            <span className="relative flex h-1.5 w-1.5">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75" />
              <span className="relative inline-flex rounded-full h-1.5 w-1.5 bg-emerald-500" />
            </span>
            Kitsune Companion
          </span>
          <span className="capitalize text-slate-400">{mood}</span>
        </div>

        {/* The Baby Animal: Hand-crafted, ultra-cute vector Baby Fox / Bunny */}
        <div className="relative w-28 h-20 flex items-center justify-center">
          {/* Animated Pet SVG Creature */}
          <motion.div
            animate={
              mood === 'running'
                ? {
                    x: [-14, 14, -14],
                    y: [0, -6, 0, -6, 0],
                    rotate: [-3, 3, -3],
                  }
                : mood === 'sleeping'
                ? {
                    y: [0, 2, 0],
                    scale: [1, 0.98, 1],
                  }
                : mood === 'curious'
                ? {
                    rotate: [-6, 6, -6],
                    y: [0, -3, 0],
                  }
                : {
                    y: [0, -4, 0],
                    scale: [1, 1.02, 1],
                  }
            }
            transition={{
              repeat: Infinity,
              duration: mood === 'running' ? 2 : mood === 'sleeping' ? 3.5 : 2.5,
              ease: 'easeInOut',
            }}
            className="relative"
          >
            <svg
              viewBox="0 0 120 100"
              className="w-24 h-20 drop-shadow-sm filter"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              {/* Fluffy Tail */}
              <motion.path
                d="M 28 65 C 10 55 5 35 18 25 C 26 18 36 28 32 46 C 30 52 28 60 28 65 Z"
                fill="#FB923C"
                stroke="#EA580C"
                strokeWidth="1.5"
                animate={
                  mood === 'sleeping'
                    ? { rotate: [0, 4, 0] }
                    : { rotate: [-10, 15, -10] }
                }
                transition={{ repeat: Infinity, duration: 1.8, ease: 'easeInOut' }}
                style={{ transformOrigin: '28px 65px' }}
              />
              <path
                d="M 18 25 C 24 20 30 26 28 35 C 22 35 18 30 18 25 Z"
                fill="#FFF7ED"
              />

              {/* Body */}
              <ellipse
                cx="58"
                cy="64"
                rx="26"
                ry="20"
                fill="#FB923C"
                stroke="#EA580C"
                strokeWidth="1.5"
              />
              {/* White Belly */}
              <ellipse
                cx="60"
                cy="66"
                rx="15"
                ry="13"
                fill="#FFF7ED"
              />

              {/* Ears */}
              {/* Left Ear */}
              <motion.path
                d="M 46 38 C 40 18 36 10 46 8 C 54 8 55 24 54 36 Z"
                fill="#FB923C"
                stroke="#EA580C"
                strokeWidth="1.5"
                animate={mood === 'curious' ? { rotate: [-8, 0, -8] } : {}}
                style={{ transformOrigin: '48px 36px' }}
              />
              <path
                d="M 45 32 C 42 20 40 15 46 13 C 50 13 51 22 50 30 Z"
                fill="#FECDD3"
              />

              {/* Right Ear */}
              <motion.path
                d="M 72 36 C 72 24 73 8 81 8 C 91 10 87 18 81 38 Z"
                fill="#FB923C"
                stroke="#EA580C"
                strokeWidth="1.5"
                animate={mood === 'curious' ? { rotate: [0, 10, 0] } : {}}
                style={{ transformOrigin: '78px 36px' }}
              />
              <path
                d="M 76 30 C 76 22 77 13 81 13 C 86 15 84 20 81 32 Z"
                fill="#FECDD3"
              />

              {/* Head */}
              <circle
                cx="64"
                cy="44"
                r="20"
                fill="#FB923C"
                stroke="#EA580C"
                strokeWidth="1.5"
              />

              {/* Cheeks / White muzzle */}
              <path
                d="M 50 48 C 50 56 56 60 64 60 C 72 60 78 56 78 48 C 78 45 74 44 64 44 C 54 44 50 45 50 48 Z"
                fill="#FFF7ED"
              />

              {/* Rosy Blush */}
              <circle cx="51" cy="49" r="3.5" fill="#FDA4AF" opacity="0.8" />
              <circle cx="77" cy="49" r="3.5" fill="#FDA4AF" opacity="0.8" />

              {/* Nose */}
              <polygon points="62,50 66,50 64,53" fill="#1E293B" />

              {/* Mouth */}
              <path
                d="M 61 54 Q 64 56 67 54"
                stroke="#1E293B"
                strokeWidth="1.2"
                strokeLinecap="round"
              />

              {/* Eyes */}
              {mood === 'sleeping' ? (
                // Sleeping curved eyes
                <>
                  <path
                    d="M 53 43 Q 56 46 59 43"
                    stroke="#1E293B"
                    strokeWidth="1.8"
                    strokeLinecap="round"
                  />
                  <path
                    d="M 69 43 Q 72 46 75 43"
                    stroke="#1E293B"
                    strokeWidth="1.8"
                    strokeLinecap="round"
                  />
                </>
              ) : (
                // Cute open sparkle eyes
                <>
                  <circle cx="56" cy="42" r="3" fill="#0F172A" />
                  <circle cx="55" cy="41" r="1.1" fill="#FFFFFF" />
                  <circle cx="72" cy="42" r="3" fill="#0F172A" />
                  <circle cx="71" cy="41" r="1.1" fill="#FFFFFF" />
                </>
              )}

              {/* Paws */}
              {mood === 'running' ? (
                <>
                  <ellipse cx="50" cy="79" rx="5" ry="3" fill="#FFF7ED" stroke="#EA580C" strokeWidth="1" />
                  <ellipse cx="76" cy="77" rx="5" ry="3" fill="#FFF7ED" stroke="#EA580C" strokeWidth="1" />
                </>
              ) : (
                <>
                  <ellipse cx="53" cy="80" rx="5" ry="3.5" fill="#FFF7ED" stroke="#EA580C" strokeWidth="1" />
                  <ellipse cx="73" cy="80" rx="5" ry="3.5" fill="#FFF7ED" stroke="#EA580C" strokeWidth="1" />
                </>
              )}
            </svg>

            {/* Sleeping Zzz Particle Generator */}
            {mood === 'sleeping' && (
              <motion.div
                initial={{ opacity: 0, y: 0, scale: 0.6 }}
                animate={{ opacity: [0, 1, 0], y: [-6, -24], x: [0, 8], scale: [0.7, 1.2] }}
                transition={{ repeat: Infinity, duration: 2.2, ease: 'easeOut' }}
                className="absolute top-1 right-2 text-indigo-500 font-bold font-mono text-xs pointer-events-none"
              >
                Zzz...
              </motion.div>
            )}

            {/* Coding / Happy Sparkles */}
            {(mood === 'coding' || mood === 'happy') && (
              <motion.div
                animate={{ rotate: 360, scale: [0.8, 1.2, 0.8] }}
                transition={{ repeat: Infinity, duration: 3, ease: 'linear' }}
                className="absolute -top-1 -left-1 text-amber-400 pointer-events-none"
              >
                <Sparkles className="w-3.5 h-3.5" />
              </motion.div>
            )}
          </motion.div>
        </div>

        {/* Small Action Strip */}
        <div className="flex items-center gap-1.5 mt-1">
          <button
            type="button"
            onClick={(e) => {
              e.stopPropagation();
              handlePet();
            }}
            className="flex items-center gap-1 px-2.5 py-0.5 rounded-full bg-white/90 hover:bg-white text-[10px] font-bold text-indigo-700 border border-indigo-100 shadow-2xs hover:scale-105 transition-all"
          >
            <Heart className="w-2.5 h-2.5 text-rose-500 fill-rose-500" />
            <span>Pet ({petCount})</span>
          </button>

          <button
            type="button"
            onClick={(e) => {
              e.stopPropagation();
              setMood((m) => (m === 'sleeping' ? 'happy' : 'sleeping'));
            }}
            className="p-1 rounded-full bg-white/80 hover:bg-white text-slate-500 hover:text-indigo-600 border border-indigo-100 shadow-2xs transition-all"
            title={mood === 'sleeping' ? 'Wake up!' : 'Sleep time'}
          >
            {mood === 'sleeping' ? <Coffee className="w-2.5 h-2.5" /> : <Moon className="w-2.5 h-2.5" />}
          </button>
        </div>
      </div>
    </div>
  );
};
