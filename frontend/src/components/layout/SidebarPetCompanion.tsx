import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Heart,
  Moon,
  Coffee,
  Sparkles,
  Zap,
  HelpCircle,
  Flame,
  Check,
  ChevronDown,
} from 'lucide-react';

export type PetSpecies = 'kitsune' | 'kitten' | 'dragon' | 'bunny' | 'panda';

export type PetMood =
  | 'idle'
  | 'roaming'
  | 'sleeping'
  | 'question'
  | 'angry'
  | 'sad'
  | 'happy';

interface PetConfig {
  id: PetSpecies;
  name: string;
  badge: string;
  icon: string;
  themeColor: string;
  accentColor: string;
}

export const PET_SPECIES_LIST: PetConfig[] = [
  { id: 'kitsune', name: 'Baby Kitsune', badge: 'Mythic Fox', icon: '🦊', themeColor: '#FB923C', accentColor: '#EA580C' },
  { id: 'kitten', name: 'Cyber Kitten', badge: 'Speedy Cat', icon: '🐱', themeColor: '#F472B6', accentColor: '#DB2777' },
  { id: 'dragon', name: 'Fire Drake', badge: 'Arena Dragon', icon: '🐲', themeColor: '#34D399', accentColor: '#059669' },
  { id: 'bunny', name: 'Cloud Bunny', badge: 'Soft Jumper', icon: '🐰', themeColor: '#818CF8', accentColor: '#4F46E5' },
  { id: 'panda', name: 'Bonsai Panda', badge: 'Zen Master', icon: '🐼', themeColor: '#64748B', accentColor: '#334155' },
];

interface PetQuestion {
  question: string;
  options: { label: string; correct: boolean }[];
  rewardText: string;
}

const TRIVIA_QUESTIONS: PetQuestion[] = [
  {
    question: 'What is the average time complexity of Hash Map lookup?',
    options: [
      { label: 'O(1)', correct: true },
      { label: 'O(log n)', correct: false },
      { label: 'O(n)', correct: false },
    ],
    rewardText: 'Bingo! Constant time O(1) average lookup! +50 XP vibe! ✨',
  },
  {
    question: 'Which sorting algorithm has guaranteed O(n log n) worst-case?',
    options: [
      { label: 'Merge Sort', correct: true },
      { label: 'Quick Sort', correct: false },
      { label: 'Bubble Sort', correct: false },
    ],
    rewardText: 'Brilliant! Merge Sort guarantees O(n log n)! 🚀',
  },
  {
    question: 'Which data structure follows LIFO (Last In First Out)?',
    options: [
      { label: 'Stack', correct: true },
      { label: 'Queue', correct: false },
      { label: 'Heap', correct: false },
    ],
    rewardText: 'Awesome! Stack pops the most recent item! 🥞',
  },
  {
    question: 'Which HTTP method is idempotent and used to retrieve resources?',
    options: [
      { label: 'GET', correct: true },
      { label: 'POST', correct: false },
      { label: 'PATCH', correct: false },
    ],
    rewardText: 'Exact match! GET requests retrieve safely! 🌐',
  },
  {
    question: 'Can you defeat the next 1v1 challenger in the Arena?',
    options: [
      { label: 'Yes, absolutely!', correct: true },
      { label: 'Born ready!', correct: true },
      { label: '100% Victory!', correct: true },
    ],
    rewardText: 'That’s the champion mindset! Go claim your MMR! 🔥',
  },
];

const SPEECH_BY_MOOD: Record<PetMood, string[]> = {
  idle: [
    'Resting by your side, Champion! 🐾',
    'Ready for the next duel!',
    'Clean code, peaceful mind! ✨',
  ],
  roaming: [
    'Exploring your screen! Zoom zoom! 🏃💨',
    'Patrolling the viewport for bugs!',
    'Sprint mode: running across your code!',
    'Catch me if you can! 💨',
  ],
  sleeping: [
    'Zzz... dreaming of clean syntax...',
    'Zzz... garbage collector working...',
    'Zzz... 100% test coverage dreams...',
  ],
  question: [
    'Pop quiz time! Quick question for you: 🧠',
    'Can you answer this algorithm riddle? 🤔',
  ],
  angry: [
    'Grrr! Who committed to main without tests?! 💢',
    'Watch out! A wild NullPointerException appeared! ⚡',
    'Hmph! Unformatted indentation makes me fierce! 💥',
  ],
  sad: [
    'Sniff... did a test case fail? Don’t give up! 🥺',
    'Aww, off-by-one errors hurt... you’ve got this! 💧',
    'Give me a pet to cheer both of us up? ❤️',
  ],
  happy: [
    'Yay! You’re the best programmer ever! ⭐',
    '+100 Morale boost deployed! ✨',
    'Victory looks great on you! 🏆',
  ],
};

export const SidebarPetCompanion: React.FC = () => {
  // Saved pet choice
  const [selectedPet, setSelectedPet] = useState<PetSpecies>(() => {
    return (localStorage.getItem('devarena_pet_species') as PetSpecies) || 'kitsune';
  });
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  // Mood state
  const [mood, setMood] = useState<PetMood>('idle');
  const [speech, setSpeech] = useState<string>('Hi friend! Pick me or change my look! ✨');
  const [showSpeech, setShowSpeech] = useState(true);

  // Screen roaming state
  const [roamPos, setRoamPos] = useState<{ x: number; y: number }>({ x: 0, y: 0 });
  const [isRoamingScreen, setIsRoamingScreen] = useState(false);

  // Trivia state
  const [currentQuiz, setCurrentQuiz] = useState<PetQuestion | null>(null);
  const [quizAnswered, setQuizAnswered] = useState<boolean>(false);

  // Petting interaction
  const [hearts, setHearts] = useState<{ id: number; x: number }[]>([]);
  const [petCount, setPetCount] = useState<number>(() => {
    return parseInt(localStorage.getItem('devarena_pet_count') || '0', 10);
  });

  const petConfig = PET_SPECIES_LIST.find((p) => p.id === selectedPet) || PET_SPECIES_LIST[0];

  // Save selected pet
  const handleSelectPet = (id: PetSpecies) => {
    setSelectedPet(id);
    localStorage.setItem('devarena_pet_species', id);
    setIsDropdownOpen(false);
    setSpeech(`Yay! You chose ${id.toUpperCase()}! 🌟`);
    setShowSpeech(true);
    setMood('happy');
  };

  // Autonomous state machine: periodically switch moods & roam
  useEffect(() => {
    const moods: PetMood[] = ['idle', 'roaming', 'question', 'sleeping', 'angry', 'sad', 'happy'];

    const timer = setInterval(() => {
      // Pick a random new mood
      const nextMood = moods[Math.floor(Math.random() * moods.length)];
      setMood(nextMood);

      if (nextMood === 'roaming') {
        // Run around the screen
        setIsRoamingScreen(true);
        // Random target coordinates across viewport
        const maxW = Math.max(300, window.innerWidth - 180);
        const maxH = Math.max(200, window.innerHeight - 180);
        const targetX = Math.floor(Math.random() * (maxW - 300) + 280);
        const targetY = Math.floor(Math.random() * (maxH - 100) + 50);
        setRoamPos({ x: targetX, y: targetY });

        const lines = SPEECH_BY_MOOD.roaming;
        setSpeech(lines[Math.floor(Math.random() * lines.length)]);
        setShowSpeech(true);

        // After roaming for 7 seconds, return back home and sleep or rest!
        setTimeout(() => {
          setIsRoamingScreen(false);
          setMood('sleeping');
          setSpeech('Zzz... back home in my nest to sleep! 😴');
          setShowSpeech(true);
        }, 7000);
      } else if (nextMood === 'question') {
        setIsRoamingScreen(false);
        const q = TRIVIA_QUESTIONS[Math.floor(Math.random() * TRIVIA_QUESTIONS.length)];
        setCurrentQuiz(q);
        setQuizAnswered(false);
        setSpeech(q.question);
        setShowSpeech(true);
      } else {
        setIsRoamingScreen(false);
        setCurrentQuiz(null);
        const lines = SPEECH_BY_MOOD[nextMood];
        if (lines && lines.length > 0) {
          setSpeech(lines[Math.floor(Math.random() * lines.length)]);
          setShowSpeech(true);
        }
      }
    }, 14000);

    return () => clearInterval(timer);
  }, []);

  // Handle click / pet
  const handlePet = () => {
    const updated = petCount + 1;
    setPetCount(updated);
    localStorage.setItem('devarena_pet_count', updated.toString());

    const newHeart = { id: Date.now() + Math.random(), x: Math.random() * 40 - 20 };
    setHearts((prev) => [...prev.slice(-5), newHeart]);

    const reactions = [
      'Purr... thank you for the love! ❤️',
      'Yay! Friendship level UP! ⭐',
      '+100 Morale boost activated! 🔥',
      'You are my absolute hero! 🐾',
      'Bugs tremble before our bond! ✨',
    ];
    setSpeech(reactions[Math.floor(Math.random() * reactions.length)]);
    setShowSpeech(true);
    setMood('happy');

    setTimeout(() => {
      setHearts((prev) => prev.filter((h) => h.id !== newHeart.id));
    }, 1500);
  };

  // Answer trivia quiz
  const handleAnswerQuiz = (opt: { label: string; correct: boolean }) => {
    setQuizAnswered(true);
    if (opt.correct) {
      setMood('happy');
      setSpeech(currentQuiz?.rewardText || 'Correct! Great job!');
    } else {
      setMood('sad');
      setSpeech('Aww, not quite! But keep practicing, you are getting stronger! 💪');
    }
  };

  // Trigger manual roam
  const handleTriggerRoam = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (isRoamingScreen) {
      setIsRoamingScreen(false);
      setMood('idle');
      setSpeech('Back at my cozy dock! 🐾');
    } else {
      setIsRoamingScreen(true);
      setMood('roaming');
      const maxW = Math.max(300, window.innerWidth - 180);
      const maxH = Math.max(200, window.innerHeight - 180);
      setRoamPos({
        x: Math.floor(Math.random() * (maxW - 300) + 280),
        y: Math.floor(Math.random() * (maxH - 100) + 50),
      });
      setSpeech('Zooming around your screen! Wheee! 🏃💨');
      setShowSpeech(true);
      setTimeout(() => {
        setIsRoamingScreen(false);
        setMood('sleeping');
        setSpeech('Phew! What a run! Time for a nap... 😴');
      }, 7000);
    }
  };

  return (
    <>
      {/* 1. SIDEBAR DOCK (Standard anchor in sidebar) */}
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

        {/* Speech Bubble (when docked in sidebar) */}
        <AnimatePresence>
          {showSpeech && !isRoamingScreen && (
            <motion.div
              initial={{ opacity: 0, y: 6, scale: 0.9 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, y: -4, scale: 0.9 }}
              className="mb-2 relative bg-white/95 backdrop-blur-md px-3 py-1.5 rounded-2xl border border-indigo-100 shadow-sm text-center max-w-[210px] z-20"
            >
              <p className="text-[11px] font-semibold text-slate-700 leading-tight">
                {speech}
              </p>

              {/* In-bubble Quiz Buttons if in Question mode */}
              {mood === 'question' && currentQuiz && !quizAnswered && (
                <div className="mt-2 space-y-1">
                  {currentQuiz.options.map((opt, i) => (
                    <button
                      key={i}
                      onClick={() => handleAnswerQuiz(opt)}
                      className="w-full py-0.5 px-2 text-[10px] font-mono font-bold rounded-lg bg-indigo-50 hover:bg-indigo-100 text-indigo-700 border border-indigo-200 transition-colors block text-left truncate"
                    >
                      {opt.label}
                    </button>
                  ))}
                </div>
              )}

              {/* Bubble Tail */}
              <div className="absolute -bottom-1 left-1/2 -translate-x-1/2 w-2 h-2 bg-white border-b border-r border-indigo-100 rotate-45" />
            </motion.div>
          )}
        </AnimatePresence>

        {/* Pet Island & Selector */}
        <div
          onClick={handlePet}
          role="button"
          tabIndex={0}
          title="Click to pet! Or use the menu to change species!"
          className="relative w-full max-w-[210px] py-2.5 px-3 rounded-2xl bg-gradient-to-b from-indigo-50/60 via-purple-50/40 to-cyan-50/40 border border-indigo-100/70 hover:border-indigo-200 transition-all cursor-pointer shadow-2xs hover:shadow-xs flex flex-col items-center group/pet"
        >
          {/* Pet Selector Bar */}
          <div className="w-full flex items-center justify-between mb-1">
            {/* Pet Species Dropdown Button */}
            <div className="relative">
              <button
                type="button"
                onClick={(e) => {
                  e.stopPropagation();
                  setIsDropdownOpen(!isDropdownOpen);
                }}
                className="flex items-center gap-1 text-[10px] font-mono font-bold text-indigo-700 hover:text-indigo-900 bg-white/90 px-2 py-0.5 rounded-md border border-indigo-100 shadow-2xs hover:bg-white transition-all"
              >
                <span>{petConfig.icon}</span>
                <span className="truncate max-w-[80px]">{petConfig.name}</span>
                <ChevronDown className="w-2.5 h-2.5 text-indigo-500" />
              </button>

              {/* Dropdown Menu */}
              <AnimatePresence>
                {isDropdownOpen && (
                  <motion.div
                    initial={{ opacity: 0, y: -4, scale: 0.95 }}
                    animate={{ opacity: 1, y: 0, scale: 1 }}
                    exit={{ opacity: 0, y: -4, scale: 0.95 }}
                    className="absolute left-0 top-full mt-1 w-44 bg-white rounded-xl shadow-xl border border-slate-200 py-1.5 z-50 text-left"
                    onClick={(e) => e.stopPropagation()}
                  >
                    <div className="px-2.5 py-1 text-[9px] font-mono uppercase font-bold text-slate-400 border-b border-slate-100">
                      Choose Your Pet
                    </div>
                    {PET_SPECIES_LIST.map((p) => (
                      <button
                        key={p.id}
                        onClick={() => handleSelectPet(p.id)}
                        className={`w-full flex items-center justify-between px-2.5 py-1.5 text-xs text-slate-700 hover:bg-indigo-50 hover:text-indigo-900 transition-colors ${
                          selectedPet === p.id ? 'bg-indigo-50/80 font-bold text-indigo-700' : ''
                        }`}
                      >
                        <div className="flex items-center gap-2 min-w-0">
                          <span className="text-sm">{p.icon}</span>
                          <span className="truncate text-xs">{p.name}</span>
                        </div>
                        {selectedPet === p.id && <Check className="w-3.5 h-3.5 text-indigo-600 shrink-0" />}
                      </button>
                    ))}
                  </motion.div>
                )}
              </AnimatePresence>
            </div>

            {/* Mood pill */}
            <span className={`text-[9px] font-mono font-bold uppercase px-1.5 py-0.2 rounded-full border ${
              mood === 'angry'
                ? 'bg-rose-50 text-rose-700 border-rose-200'
                : mood === 'sad'
                ? 'bg-sky-50 text-sky-700 border-sky-200'
                : mood === 'question'
                ? 'bg-amber-50 text-amber-700 border-amber-200'
                : mood === 'sleeping'
                ? 'bg-slate-100 text-slate-600 border-slate-200'
                : mood === 'roaming'
                ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                : 'bg-indigo-50 text-indigo-700 border-indigo-200'
            }`}>
              {mood}
            </span>
          </div>

          {/* SVG Creature (Visible when docked in sidebar) */}
          <div className="relative w-28 h-20 flex items-center justify-center">
            {isRoamingScreen ? (
              <div className="text-xs font-mono text-indigo-600/70 text-center animate-pulse">
                <span>🏃 Running around screen!</span>
                <span className="text-[10px] text-slate-400 block mt-1">Will return to sleep soon</span>
              </div>
            ) : (
              <PetAvatar species={selectedPet} mood={mood} />
            )}
          </div>

          {/* Action Toolbar */}
          <div className="flex items-center gap-1.5 mt-1.5">
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

            {/* Run / Roam toggle */}
            <button
              type="button"
              onClick={handleTriggerRoam}
              title={isRoamingScreen ? 'Call pet back' : 'Let pet run across screen!'}
              className="flex items-center gap-1 px-2 py-0.5 rounded-full bg-white/80 hover:bg-white text-[10px] font-bold text-emerald-700 border border-emerald-100 shadow-2xs transition-all hover:scale-105"
            >
              <Zap className="w-2.5 h-2.5 text-emerald-600" />
              <span>{isRoamingScreen ? 'Recall' : 'Run'}</span>
            </button>

            {/* Sleep toggle */}
            <button
              type="button"
              onClick={(e) => {
                e.stopPropagation();
                setMood((m) => (m === 'sleeping' ? 'idle' : 'sleeping'));
              }}
              className="p-1 rounded-full bg-white/80 hover:bg-white text-slate-500 hover:text-indigo-600 border border-indigo-100 shadow-2xs transition-all"
              title={mood === 'sleeping' ? 'Wake up!' : 'Nap time'}
            >
              {mood === 'sleeping' ? <Coffee className="w-2.5 h-2.5" /> : <Moon className="w-2.5 h-2.5" />}
            </button>
          </div>
        </div>
      </div>

      {/* 2. FLOATING SCREEN ROAMER (Runs around the screen and comes back!) */}
      <AnimatePresence>
        {isRoamingScreen && (
          <motion.div
            initial={{ left: 240, top: 400, opacity: 0, scale: 0.7 }}
            animate={{
              left: [260, roamPos.x, roamPos.x + 80, roamPos.x - 40, 240],
              top: [400, roamPos.y, roamPos.y - 60, roamPos.y + 40, 420],
              opacity: 1,
              scale: 1,
            }}
            exit={{ left: 240, top: 400, opacity: 0, scale: 0.6 }}
            transition={{
              duration: 7,
              ease: 'easeInOut',
            }}
            className="fixed z-50 pointer-events-auto cursor-pointer drop-shadow-2xl"
            onClick={handlePet}
            title="Click me while I am running!"
          >
            {/* Roaming Speech Bubble */}
            <div className="absolute -top-10 left-1/2 -translate-x-1/2 whitespace-nowrap bg-white/95 backdrop-blur-md px-3 py-1 rounded-xl border border-indigo-200 shadow-md text-slate-800 text-[11px] font-bold font-mono">
              🏃 {speech}
            </div>

            {/* The live running Pet */}
            <div className="w-24 h-20 bg-white/80 backdrop-blur-sm p-1 rounded-2xl border border-indigo-100 shadow-lg flex items-center justify-center">
              <PetAvatar species={selectedPet} mood="roaming" />
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  );
};

// Sub-component: Distinct SVG Avatars for Kitsune, Kitten, Dragon, Bunny, Panda
const PetAvatar: React.FC<{ species: PetSpecies; mood: PetMood }> = ({ species, mood }) => {
  // Motion variations based on mood
  const motionProps =
    mood === 'roaming'
      ? {
          animate: { x: [-10, 10, -10], y: [0, -8, 0, -8, 0], rotate: [-4, 4, -4] },
          transition: { repeat: Infinity, duration: 0.8, ease: 'easeInOut' },
        }
      : mood === 'sleeping'
      ? {
          animate: { y: [0, 2, 0], scale: [1, 0.98, 1] },
          transition: { repeat: Infinity, duration: 3, ease: 'easeInOut' },
        }
      : mood === 'angry'
      ? {
          animate: { x: [-2, 2, -2, 2, 0], y: [0, -2, 0] },
          transition: { repeat: Infinity, duration: 0.25 },
        }
      : mood === 'sad'
      ? {
          animate: { y: [0, 3, 0], rotate: [-2, 2, -2] },
          transition: { repeat: Infinity, duration: 3 },
        }
      : mood === 'question'
      ? {
          animate: { rotate: [-8, 8, -8], y: [0, -4, 0] },
          transition: { repeat: Infinity, duration: 2 },
        }
      : {
          animate: { y: [0, -4, 0], scale: [1, 1.02, 1] },
          transition: { repeat: Infinity, duration: 2.2, ease: 'easeInOut' },
        };

  return (
    <motion.div {...motionProps} className="relative">
      {/* 1. KITSUNE (Fox) */}
      {species === 'kitsune' && (
        <svg viewBox="0 0 120 100" className="w-24 h-20 drop-shadow-sm filter" fill="none">
          <motion.path
            d="M 28 65 C 10 55 5 35 18 25 C 26 18 36 28 32 46 C 30 52 28 60 28 65 Z"
            fill="#FB923C"
            stroke="#EA580C"
            strokeWidth="1.5"
            animate={mood === 'sleeping' ? { rotate: [0, 4, 0] } : { rotate: [-10, 15, -10] }}
            transition={{ repeat: Infinity, duration: 1.8, ease: 'easeInOut' }}
            style={{ transformOrigin: '28px 65px' }}
          />
          <path d="M 18 25 C 24 20 30 26 28 35 C 22 35 18 30 18 25 Z" fill="#FFF7ED" />
          <ellipse cx="58" cy="64" rx="26" ry="20" fill="#FB923C" stroke="#EA580C" strokeWidth="1.5" />
          <ellipse cx="60" cy="66" rx="15" ry="13" fill="#FFF7ED" />
          {/* Ears */}
          <path d="M 46 38 C 40 18 36 10 46 8 C 54 8 55 24 54 36 Z" fill="#FB923C" stroke="#EA580C" strokeWidth="1.5" />
          <path d="M 45 32 C 42 20 40 15 46 13 C 50 13 51 22 50 30 Z" fill="#FECDD3" />
          <path d="M 72 36 C 72 24 73 8 81 8 C 91 10 87 18 81 38 Z" fill="#FB923C" stroke="#EA580C" strokeWidth="1.5" />
          <path d="M 76 30 C 76 22 77 13 81 13 C 86 15 84 20 81 32 Z" fill="#FECDD3" />
          {/* Head & Face */}
          <circle cx="64" cy="44" r="20" fill="#FB923C" stroke="#EA580C" strokeWidth="1.5" />
          <path d="M 50 48 C 50 56 56 60 64 60 C 72 60 78 56 78 48 C 78 45 74 44 64 44 C 54 44 50 45 50 48 Z" fill="#FFF7ED" />
          <polygon points="62,50 66,50 64,53" fill="#1E293B" />
          {/* Mood Expression */}
          <MoodEyesAndMouth mood={mood} cx1={56} cx2={72} cy={42} />
        </svg>
      )}

      {/* 2. KITTEN */}
      {species === 'kitten' && (
        <svg viewBox="0 0 120 100" className="w-24 h-20 drop-shadow-sm filter" fill="none">
          {/* Tail */}
          <motion.path
            d="M 35 70 Q 20 75 15 60 Q 12 50 20 45 Q 26 55 35 62"
            fill="#F472B6"
            stroke="#DB2777"
            strokeWidth="1.5"
            animate={{ rotate: [-8, 12, -8] }}
            transition={{ repeat: Infinity, duration: 1.5 }}
            style={{ transformOrigin: '35px 70px' }}
          />
          {/* Body */}
          <ellipse cx="60" cy="65" rx="24" ry="18" fill="#F472B6" stroke="#DB2777" strokeWidth="1.5" />
          <ellipse cx="60" cy="67" rx="14" ry="11" fill="#FFF1F2" />
          {/* Cat Ears */}
          <polygon points="46,38 38,15 56,26" fill="#F472B6" stroke="#DB2777" strokeWidth="1.5" />
          <polygon points="45,34 40,20 52,28" fill="#FDA4AF" />
          <polygon points="74,38 82,15 64,26" fill="#F472B6" stroke="#DB2777" strokeWidth="1.5" />
          <polygon points="75,34 80,20 68,28" fill="#FDA4AF" />
          {/* Head */}
          <circle cx="60" cy="44" r="19" fill="#F472B6" stroke="#DB2777" strokeWidth="1.5" />
          <ellipse cx="60" cy="50" rx="9" ry="6" fill="#FFF1F2" />
          {/* Whiskers */}
          <line x1="42" y1="48" x2="34" y2="46" stroke="#9D174D" strokeWidth="1" />
          <line x1="42" y1="52" x2="33" y2="53" stroke="#9D174D" strokeWidth="1" />
          <line x1="78" y1="48" x2="86" y2="46" stroke="#9D174D" strokeWidth="1" />
          <line x1="78" y1="52" x2="87" y2="53" stroke="#9D174D" strokeWidth="1" />
          {/* Nose */}
          <polygon points="58,48 62,48 60,50" fill="#E11D48" />
          <MoodEyesAndMouth mood={mood} cx1={53} cx2={67} cy={42} />
        </svg>
      )}

      {/* 3. FIRE DRAKE (Baby Dragon) */}
      {species === 'dragon' && (
        <svg viewBox="0 0 120 100" className="w-24 h-20 drop-shadow-sm filter" fill="none">
          {/* Dragon Wings */}
          <motion.path
            d="M 38 48 Q 20 30 18 18 Q 30 24 38 36"
            fill="#34D399"
            stroke="#059669"
            strokeWidth="1.5"
            animate={{ rotate: [-6, 10, -6] }}
            transition={{ repeat: Infinity, duration: 1.2 }}
            style={{ transformOrigin: '38px 48px' }}
          />
          <motion.path
            d="M 82 48 Q 100 30 102 18 Q 90 24 82 36"
            fill="#34D399"
            stroke="#059669"
            strokeWidth="1.5"
            animate={{ rotate: [6, -10, 6] }}
            transition={{ repeat: Infinity, duration: 1.2 }}
            style={{ transformOrigin: '82px 48px' }}
          />
          {/* Dragon Horns */}
          <path d="M 50 30 Q 42 12 48 10 Q 53 14 54 28" fill="#FBBF24" stroke="#D97706" strokeWidth="1.5" />
          <path d="M 70 30 Q 78 12 72 10 Q 67 14 66 28" fill="#FBBF24" stroke="#D97706" strokeWidth="1.5" />
          {/* Body */}
          <ellipse cx="60" cy="65" rx="23" ry="18" fill="#34D399" stroke="#059669" strokeWidth="1.5" />
          <ellipse cx="60" cy="66" rx="14" ry="12" fill="#FEF08A" />
          {/* Head */}
          <circle cx="60" cy="44" r="18" fill="#34D399" stroke="#059669" strokeWidth="1.5" />
          <ellipse cx="60" cy="49" rx="10" ry="7" fill="#A7F3D0" />
          <MoodEyesAndMouth mood={mood} cx1={54} cx2={66} cy={42} />
        </svg>
      )}

      {/* 4. CLOUD BUNNY */}
      {species === 'bunny' && (
        <svg viewBox="0 0 120 100" className="w-24 h-20 drop-shadow-sm filter" fill="none">
          {/* Tall Ears */}
          <motion.path
            d="M 48 35 C 44 10 40 4 48 4 C 54 4 56 12 55 35 Z"
            fill="#818CF8"
            stroke="#4F46E5"
            strokeWidth="1.5"
            animate={mood === 'question' ? { rotate: [-10, 0, -10] } : {}}
            style={{ transformOrigin: '50px 35px' }}
          />
          <path d="M 47 30 C 45 16 43 10 48 9 C 52 9 53 16 52 30 Z" fill="#EEF2FF" />
          <motion.path
            d="M 70 35 C 66 12 68 4 74 4 C 82 4 78 10 74 35 Z"
            fill="#818CF8"
            stroke="#4F46E5"
            strokeWidth="1.5"
            animate={mood === 'question' ? { rotate: [0, 10, 0] } : {}}
            style={{ transformOrigin: '72px 35px' }}
          />
          <path d="M 70 30 C 69 16 70 9 74 9 C 78 10 76 16 73 30 Z" fill="#EEF2FF" />
          {/* Fluffy tail */}
          <circle cx="34" cy="68" r="7" fill="#EEF2FF" stroke="#4F46E5" strokeWidth="1" />
          {/* Body */}
          <ellipse cx="60" cy="65" rx="23" ry="18" fill="#818CF8" stroke="#4F46E5" strokeWidth="1.5" />
          <ellipse cx="60" cy="67" rx="14" ry="12" fill="#EEF2FF" />
          {/* Head */}
          <circle cx="60" cy="44" r="18" fill="#818CF8" stroke="#4F46E5" strokeWidth="1.5" />
          <ellipse cx="60" cy="48" rx="8" ry="6" fill="#EEF2FF" />
          <polygon points="58,46 62,46 60,48" fill="#EC4899" />
          <MoodEyesAndMouth mood={mood} cx1={54} cx2={66} cy={41} />
        </svg>
      )}

      {/* 5. BONSAI PANDA */}
      {species === 'panda' && (
        <svg viewBox="0 0 120 100" className="w-24 h-20 drop-shadow-sm filter" fill="none">
          {/* Black Ears */}
          <circle cx="44" cy="28" r="8" fill="#1E293B" />
          <circle cx="76" cy="28" r="8" fill="#1E293B" />
          {/* Body */}
          <ellipse cx="60" cy="65" rx="24" ry="19" fill="#F8FAFC" stroke="#1E293B" strokeWidth="1.5" />
          <path d="M 38 56 Q 60 70 82 56 L 80 68 Q 60 80 40 68 Z" fill="#1E293B" />
          {/* Head */}
          <circle cx="60" cy="44" r="19" fill="#F8FAFC" stroke="#1E293B" strokeWidth="1.5" />
          {/* Black Eye Patches */}
          <ellipse cx="52" cy="42" rx="6" ry="7" fill="#1E293B" transform="rotate(-15 52 42)" />
          <ellipse cx="68" cy="42" rx="6" ry="7" fill="#1E293B" transform="rotate(15 68 42)" />
          {/* Nose */}
          <ellipse cx="60" cy="49" rx="4" ry="2.5" fill="#1E293B" />
          <MoodEyesAndMouth mood={mood} cx1={52} cx2={68} cy={42} eyeColor="#FFFFFF" />
        </svg>
      )}

      {/* Special Mood Effect Overlays */}
      {mood === 'sleeping' && (
        <motion.div
          initial={{ opacity: 0, y: 0 }}
          animate={{ opacity: [0, 1, 0], y: [-6, -24], x: [0, 8] }}
          transition={{ repeat: Infinity, duration: 2.2, ease: 'easeOut' }}
          className="absolute top-1 right-1 text-indigo-500 font-bold font-mono text-xs pointer-events-none"
        >
          Zzz...
        </motion.div>
      )}

      {mood === 'angry' && (
        <motion.div
          animate={{ scale: [1, 1.25, 1], rotate: [-8, 8, -8] }}
          transition={{ repeat: Infinity, duration: 0.6 }}
          className="absolute -top-1.5 -right-1 text-rose-600 font-black pointer-events-none"
        >
          <Flame className="w-4 h-4 fill-rose-500" />
        </motion.div>
      )}

      {mood === 'sad' && (
        <motion.div
          animate={{ y: [0, 8, 0], opacity: [0, 1, 0] }}
          transition={{ repeat: Infinity, duration: 1.5 }}
          className="absolute top-8 left-2 text-sky-500 font-bold text-xs pointer-events-none"
        >
          💧
        </motion.div>
      )}

      {mood === 'question' && (
        <motion.div
          animate={{ y: [-2, -8, -2], scale: [1, 1.15, 1] }}
          transition={{ repeat: Infinity, duration: 1.2 }}
          className="absolute -top-3 left-1/2 -translate-x-1/2 text-amber-500 pointer-events-none"
        >
          <HelpCircle className="w-4 h-4 fill-amber-400 text-amber-900" />
        </motion.div>
      )}

      {mood === 'happy' && (
        <motion.div
          animate={{ rotate: 360, scale: [0.8, 1.2, 0.8] }}
          transition={{ repeat: Infinity, duration: 3, ease: 'linear' }}
          className="absolute -top-1 -left-1 text-amber-400 pointer-events-none"
        >
          <Sparkles className="w-3.5 h-3.5" />
        </motion.div>
      )}
    </motion.div>
  );
};

// Helper: dynamic eyes and mouth according to mood
const MoodEyesAndMouth: React.FC<{
  mood: PetMood;
  cx1: number;
  cx2: number;
  cy: number;
  eyeColor?: string;
}> = ({ mood, cx1, cx2, cy, eyeColor = '#0F172A' }) => {
  if (mood === 'sleeping') {
    return (
      <>
        <path d={`M ${cx1 - 3} ${cy} Q ${cx1} ${cy + 3} ${cx1 + 3} ${cy}`} stroke={eyeColor} strokeWidth="1.8" strokeLinecap="round" />
        <path d={`M ${cx2 - 3} ${cy} Q ${cx2} ${cy + 3} ${cx2 + 3} ${cy}`} stroke={eyeColor} strokeWidth="1.8" strokeLinecap="round" />
        <path d={`M ${(cx1 + cx2) / 2 - 2} ${cy + 10} Q ${(cx1 + cx2) / 2} ${cy + 11} ${(cx1 + cx2) / 2 + 2} ${cy + 10}`} stroke={eyeColor} strokeWidth="1" strokeLinecap="round" />
      </>
    );
  }

  if (mood === 'angry') {
    return (
      <>
        {/* Slanted Angry Brows */}
        <line x1={cx1 - 4} y1={cy - 5} x2={cx1 + 4} y2={cy - 2} stroke={eyeColor} strokeWidth="2" strokeLinecap="round" />
        <line x1={cx2 + 4} y1={cy - 5} x2={cx2 - 4} y2={cy - 2} stroke={eyeColor} strokeWidth="2" strokeLinecap="round" />
        <circle cx={cx1} cy={cy} r="2.5" fill={eyeColor} />
        <circle cx={cx2} cy={cy} r="2.5" fill={eyeColor} />
        {/* Gritting / sharp mouth */}
        <path d={`M ${(cx1 + cx2) / 2 - 4} ${cy + 11} Q ${(cx1 + cx2) / 2} ${cy + 9} ${(cx1 + cx2) / 2 + 4} ${cy + 11}`} stroke={eyeColor} strokeWidth="1.6" strokeLinecap="round" />
      </>
    );
  }

  if (mood === 'sad') {
    return (
      <>
        {/* Sad Drooping Brows */}
        <line x1={cx1 - 4} y1={cy - 2} x2={cx1 + 4} y2={cy - 5} stroke={eyeColor} strokeWidth="1.5" strokeLinecap="round" />
        <line x1={cx2 + 4} y1={cy - 2} x2={cx2 - 4} y2={cy - 5} stroke={eyeColor} strokeWidth="1.5" strokeLinecap="round" />
        <circle cx={cx1} cy={cy} r="2.8" fill={eyeColor} />
        <circle cx={cx2} cy={cy} r="2.8" fill={eyeColor} />
        {/* Frown Mouth */}
        <path d={`M ${(cx1 + cx2) / 2 - 4} ${cy + 11} Q ${(cx1 + cx2) / 2} ${cy + 8} ${(cx1 + cx2) / 2 + 4} ${cy + 11}`} stroke={eyeColor} strokeWidth="1.5" strokeLinecap="round" />
      </>
    );
  }

  // Normal / Happy / Roaming / Question
  return (
    <>
      <circle cx={cx1} cy={cy} r="3" fill={eyeColor} />
      <circle cx={cx1 - 1} cy={cy - 1} r="1.1" fill="#FFFFFF" />
      <circle cx={cx2} cy={cy} r="3" fill={eyeColor} />
      <circle cx={cx2 - 1} cy={cy - 1} r="1.1" fill="#FFFFFF" />
      {/* Happy Smile Mouth */}
      <path d={`M ${(cx1 + cx2) / 2 - 3} ${cy + 9} Q ${(cx1 + cx2) / 2} ${cy + 12} ${(cx1 + cx2) / 2 + 3} ${cy + 9}`} stroke={eyeColor} strokeWidth="1.4" strokeLinecap="round" />
    </>
  );
};
