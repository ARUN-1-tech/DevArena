import React, { useState } from 'react';
import { motion } from 'framer-motion';

export type PetSpecies = 'kitsune' | 'kitten' | 'dragon' | 'bunny' | 'panda';

const SPECIES_LIST: { id: PetSpecies; name: string }[] = [
  { id: 'kitsune', name: 'Fox' },
  { id: 'kitten', name: 'Cat' },
  { id: 'dragon', name: 'Dragon' },
  { id: 'bunny', name: 'Bunny' },
  { id: 'panda', name: 'Panda' },
];

export const SidebarPetCompanion: React.FC = () => {
  const [selectedPet, setSelectedPet] = useState<PetSpecies>(() => {
    return (localStorage.getItem('devarena_pet_species') as PetSpecies) || 'kitsune';
  });

  const handleCyclePet = () => {
    const currentIndex = SPECIES_LIST.findIndex((p) => p.id === selectedPet);
    const nextSpecies = SPECIES_LIST[(currentIndex + 1) % SPECIES_LIST.length].id;
    setSelectedPet(nextSpecies);
    localStorage.setItem('devarena_pet_species', nextSpecies);
  };

  return (
    <div
      onClick={handleCyclePet}
      role="button"
      tabIndex={0}
      title="Click to switch pet!"
      className="w-full py-1.5 px-3 flex items-center justify-center cursor-pointer select-none overflow-hidden relative group"
    >
      {/* Subtle track line */}
      <div className="absolute bottom-1 left-4 right-4 h-0.5 bg-gradient-to-r from-transparent via-indigo-200/50 to-transparent dark:via-indigo-500/20 rounded-full" />

      {/* Running Pet container with smooth horizontal run cycle */}
      <motion.div
        animate={{
          x: [-60, 60, -60],
          scaleX: [1, 1, -1, -1, 1],
        }}
        transition={{
          x: { repeat: Infinity, duration: 8, ease: 'easeInOut' },
          scaleX: {
            repeat: Infinity,
            duration: 8,
            times: [0, 0.48, 0.5, 0.98, 1],
            ease: 'linear',
          },
        }}
        className="relative z-10 flex items-center justify-center"
      >
        <RunningPetAvatar species={selectedPet} />
      </motion.div>
    </div>
  );
};

const RunningPetAvatar: React.FC<{ species: PetSpecies }> = ({ species }) => {
  // Bobbing / galloping vertical bounce
  return (
    <motion.div
      animate={{
        y: [0, -5, 0, -5, 0],
        rotate: [-3, 3, -3],
      }}
      transition={{
        repeat: Infinity,
        duration: 0.45,
        ease: 'easeInOut',
      }}
      className="relative filter drop-shadow-sm"
    >
      {/* 1. KITSUNE (Fox) */}
      {species === 'kitsune' && (
        <svg viewBox="0 0 100 70" className="w-16 h-12" fill="none">
          {/* Animated Tail */}
          <motion.path
            d="M 22 45 C 8 38 4 22 14 15 C 20 10 28 17 25 31 C 24 36 22 42 22 45 Z"
            fill="#FB923C"
            stroke="#EA580C"
            strokeWidth="1.2"
            animate={{ rotate: [-15, 20, -15] }}
            transition={{ repeat: Infinity, duration: 0.5, ease: 'easeInOut' }}
            style={{ transformOrigin: '22px 45px' }}
          />
          <path d="M 14 15 C 19 11 24 16 22 23 C 17 23 14 19 14 15 Z" fill="#FFF7ED" />

          {/* Back Leg */}
          <motion.path
            d="M 32 46 Q 26 56 24 64"
            stroke="#EA580C"
            strokeWidth="2.5"
            strokeLinecap="round"
            animate={{ rotate: [-25, 25, -25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '32px 46px' }}
          />
          {/* Front Leg (Left) */}
          <motion.path
            d="M 58 46 Q 66 56 68 64"
            stroke="#EA580C"
            strokeWidth="2.5"
            strokeLinecap="round"
            animate={{ rotate: [25, -25, 25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '58px 46px' }}
          />

          {/* Body */}
          <ellipse cx="46" cy="42" rx="20" ry="14" fill="#FB923C" stroke="#EA580C" strokeWidth="1.2" />
          <ellipse cx="48" cy="44" rx="11" ry="9" fill="#FFF7ED" />

          {/* Back Leg (Right) */}
          <motion.path
            d="M 38 46 Q 44 56 46 64"
            stroke="#EA580C"
            strokeWidth="2.5"
            strokeLinecap="round"
            animate={{ rotate: [25, -25, 25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '38px 46px' }}
          />
          {/* Front Leg (Right) */}
          <motion.path
            d="M 52 46 Q 46 56 44 64"
            stroke="#EA580C"
            strokeWidth="2.5"
            strokeLinecap="round"
            animate={{ rotate: [-25, 25, -25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '52px 46px' }}
          />

          {/* Ears */}
          <path d="M 58 26 C 53 11 50 5 57 3 C 63 3 64 15 63 24 Z" fill="#FB923C" stroke="#EA580C" strokeWidth="1.2" />
          <path d="M 57 21 C 55 12 53 8 57 7 C 60 7 61 14 60 20 Z" fill="#FECDD3" />
          <path d="M 76 24 C 76 15 77 3 83 3 C 90 5 87 11 83 26 Z" fill="#FB923C" stroke="#EA580C" strokeWidth="1.2" />
          <path d="M 79 20 C 79 14 80 7 83 7 C 87 8 85 12 83 21 Z" fill="#FECDD3" />

          {/* Head & Face */}
          <circle cx="70" cy="30" r="14" fill="#FB923C" stroke="#EA580C" strokeWidth="1.2" />
          <path d="M 60 33 C 60 39 64 42 70 42 C 76 42 80 39 80 33 C 80 31 77 30 70 30 C 63 30 60 31 60 33 Z" fill="#FFF7ED" />
          <polygon points="68,34 72,34 70,36" fill="#1E293B" />
          {/* Eye */}
          <circle cx="74" cy="28" r="2" fill="#1E293B" />
          <circle cx="74.5" cy="27.5" r="0.8" fill="#FFF" />
        </svg>
      )}

      {/* 2. KITTEN */}
      {species === 'kitten' && (
        <svg viewBox="0 0 100 70" className="w-16 h-12" fill="none">
          {/* Tail */}
          <motion.path
            d="M 26 46 Q 14 50 10 38 Q 8 30 14 26 Q 18 34 26 40"
            fill="#F472B6"
            stroke="#DB2777"
            strokeWidth="1.2"
            animate={{ rotate: [-20, 20, -20] }}
            transition={{ repeat: Infinity, duration: 0.45 }}
            style={{ transformOrigin: '26px 46px' }}
          />

          {/* Back Leg */}
          <motion.path
            d="M 32 46 Q 25 56 22 63"
            stroke="#DB2777"
            strokeWidth="2.5"
            strokeLinecap="round"
            animate={{ rotate: [-25, 25, -25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '32px 46px' }}
          />
          {/* Front Leg */}
          <motion.path
            d="M 58 46 Q 66 56 68 63"
            stroke="#DB2777"
            strokeWidth="2.5"
            strokeLinecap="round"
            animate={{ rotate: [25, -25, 25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '58px 46px' }}
          />

          {/* Body */}
          <ellipse cx="46" cy="42" rx="19" ry="13" fill="#F472B6" stroke="#DB2777" strokeWidth="1.2" />
          <ellipse cx="46" cy="44" rx="10" ry="8" fill="#FFF1F2" />

          {/* Opposite Legs */}
          <motion.path
            d="M 38 46 Q 44 56 46 63"
            stroke="#DB2777"
            strokeWidth="2.5"
            strokeLinecap="round"
            animate={{ rotate: [25, -25, 25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '38px 46px' }}
          />
          <motion.path
            d="M 52 46 Q 45 56 42 63"
            stroke="#DB2777"
            strokeWidth="2.5"
            strokeLinecap="round"
            animate={{ rotate: [-25, 25, -25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '52px 46px' }}
          />

          {/* Cat Ears */}
          <polygon points="58,26 52,9 66,17" fill="#F472B6" stroke="#DB2777" strokeWidth="1.2" />
          <polygon points="76,26 82,9 68,17" fill="#F472B6" stroke="#DB2777" strokeWidth="1.2" />

          {/* Head */}
          <circle cx="68" cy="30" r="14" fill="#F472B6" stroke="#DB2777" strokeWidth="1.2" />
          <ellipse cx="68" cy="34" rx="6" ry="4" fill="#FFF1F2" />
          <polygon points="66,33 70,33 68,35" fill="#E11D48" />
          {/* Eye */}
          <circle cx="72" cy="28" r="2" fill="#1E293B" />
          <circle cx="72.5" cy="27.5" r="0.8" fill="#FFF" />
        </svg>
      )}

      {/* 3. DRAGON */}
      {species === 'dragon' && (
        <svg viewBox="0 0 100 70" className="w-16 h-12" fill="none">
          {/* Flapping Wing */}
          <motion.path
            d="M 45 35 Q 30 18 28 8 Q 38 13 45 23"
            fill="#34D399"
            stroke="#059669"
            strokeWidth="1.2"
            animate={{ rotate: [-20, 25, -20] }}
            transition={{ repeat: Infinity, duration: 0.35 }}
            style={{ transformOrigin: '45px 35px' }}
          />

          {/* Legs */}
          <motion.path
            d="M 34 46 Q 27 56 24 63"
            stroke="#059669"
            strokeWidth="2.5"
            strokeLinecap="round"
            animate={{ rotate: [-25, 25, -25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '34px 46px' }}
          />
          <motion.path
            d="M 56 46 Q 64 56 66 63"
            stroke="#059669"
            strokeWidth="2.5"
            strokeLinecap="round"
            animate={{ rotate: [25, -25, 25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '56px 46px' }}
          />

          {/* Body */}
          <ellipse cx="46" cy="42" rx="18" ry="13" fill="#34D399" stroke="#059669" strokeWidth="1.2" />
          <ellipse cx="46" cy="43" rx="10" ry="8" fill="#FEF08A" />

          {/* Other legs */}
          <motion.path
            d="M 40 46 Q 46 56 48 63"
            stroke="#059669"
            strokeWidth="2.5"
            strokeLinecap="round"
            animate={{ rotate: [25, -25, 25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '40px 46px' }}
          />
          <motion.path
            d="M 50 46 Q 44 56 42 63"
            stroke="#059669"
            strokeWidth="2.5"
            strokeLinecap="round"
            animate={{ rotate: [-25, 25, -25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '50px 46px' }}
          />

          {/* Horns */}
          <path d="M 60 20 Q 54 6 59 4 Q 63 7 64 18" fill="#FBBF24" stroke="#D97706" strokeWidth="1.2" />
          <path d="M 74 20 Q 80 6 75 4 Q 71 7 70 18" fill="#FBBF24" stroke="#D97706" strokeWidth="1.2" />

          {/* Head */}
          <circle cx="68" cy="30" r="13" fill="#34D399" stroke="#059669" strokeWidth="1.2" />
          <ellipse cx="68" cy="34" rx="7" ry="5" fill="#A7F3D0" />
          {/* Eye */}
          <circle cx="72" cy="28" r="2" fill="#0F172A" />
          <circle cx="72.5" cy="27.5" r="0.8" fill="#FFF" />
        </svg>
      )}

      {/* 4. BUNNY */}
      {species === 'bunny' && (
        <svg viewBox="0 0 100 70" className="w-16 h-12" fill="none">
          {/* Tall Ears Fluttering */}
          <motion.path
            d="M 56 25 C 52 7 49 2 55 2 C 60 2 61 8 61 25 Z"
            fill="#818CF8"
            stroke="#4F46E5"
            strokeWidth="1.2"
            animate={{ rotate: [-15, 5, -15] }}
            transition={{ repeat: Infinity, duration: 0.4 }}
            style={{ transformOrigin: '58px 25px' }}
          />
          <motion.path
            d="M 72 25 C 69 9 70 2 75 2 C 81 2 78 7 75 25 Z"
            fill="#818CF8"
            stroke="#4F46E5"
            strokeWidth="1.2"
            animate={{ rotate: [-5, 15, -5] }}
            transition={{ repeat: Infinity, duration: 0.4 }}
            style={{ transformOrigin: '73px 25px' }}
          />

          {/* Legs */}
          <motion.path
            d="M 32 46 Q 24 56 20 63"
            stroke="#4F46E5"
            strokeWidth="2.5"
            strokeLinecap="round"
            animate={{ rotate: [-25, 25, -25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '32px 46px' }}
          />
          <motion.path
            d="M 58 46 Q 66 56 68 63"
            stroke="#4F46E5"
            strokeWidth="2.5"
            strokeLinecap="round"
            animate={{ rotate: [25, -25, 25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '58px 46px' }}
          />

          {/* Fluffy tail */}
          <circle cx="26" cy="44" r="5" fill="#EEF2FF" stroke="#4F46E5" strokeWidth="1" />

          {/* Body */}
          <ellipse cx="46" cy="42" rx="18" ry="13" fill="#818CF8" stroke="#4F46E5" strokeWidth="1.2" />
          <ellipse cx="46" cy="44" rx="10" ry="8" fill="#EEF2FF" />

          {/* Other Legs */}
          <motion.path
            d="M 38 46 Q 44 56 46 63"
            stroke="#4F46E5"
            strokeWidth="2.5"
            strokeLinecap="round"
            animate={{ rotate: [25, -25, 25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '38px 46px' }}
          />
          <motion.path
            d="M 52 46 Q 45 56 42 63"
            stroke="#4F46E5"
            strokeWidth="2.5"
            strokeLinecap="round"
            animate={{ rotate: [-25, 25, -25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '52px 46px' }}
          />

          {/* Head */}
          <circle cx="68" cy="30" r="13" fill="#818CF8" stroke="#4F46E5" strokeWidth="1.2" />
          <ellipse cx="68" cy="33" rx="6" ry="4" fill="#EEF2FF" />
          <polygon points="66,32 70,32 68,34" fill="#EC4899" />
          {/* Eye */}
          <circle cx="72" cy="28" r="2" fill="#0F172A" />
          <circle cx="72.5" cy="27.5" r="0.8" fill="#FFF" />
        </svg>
      )}

      {/* 5. PANDA */}
      {species === 'panda' && (
        <svg viewBox="0 0 100 70" className="w-16 h-12" fill="none">
          {/* Black Ears */}
          <circle cx="56" cy="18" r="5" fill="#1E293B" />
          <circle cx="78" cy="18" r="5" fill="#1E293B" />

          {/* Legs */}
          <motion.path
            d="M 32 46 Q 24 56 21 63"
            stroke="#1E293B"
            strokeWidth="3"
            strokeLinecap="round"
            animate={{ rotate: [-25, 25, -25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '32px 46px' }}
          />
          <motion.path
            d="M 58 46 Q 66 56 68 63"
            stroke="#1E293B"
            strokeWidth="3"
            strokeLinecap="round"
            animate={{ rotate: [25, -25, 25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '58px 46px' }}
          />

          {/* Body */}
          <ellipse cx="46" cy="42" rx="19" ry="14" fill="#F8FAFC" stroke="#1E293B" strokeWidth="1.2" />
          <path d="M 30 35 Q 46 45 62 35 L 60 44 Q 46 52 32 44 Z" fill="#1E293B" />

          {/* Other Legs */}
          <motion.path
            d="M 38 46 Q 44 56 46 63"
            stroke="#1E293B"
            strokeWidth="3"
            strokeLinecap="round"
            animate={{ rotate: [25, -25, 25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '38px 46px' }}
          />
          <motion.path
            d="M 52 46 Q 45 56 42 63"
            stroke="#1E293B"
            strokeWidth="3"
            strokeLinecap="round"
            animate={{ rotate: [-25, 25, -25] }}
            transition={{ repeat: Infinity, duration: 0.45, ease: 'easeInOut' }}
            style={{ transformOrigin: '52px 46px' }}
          />

          {/* Head */}
          <circle cx="68" cy="30" r="14" fill="#F8FAFC" stroke="#1E293B" strokeWidth="1.2" />
          <ellipse cx="64" cy="28" rx="4" ry="5" fill="#1E293B" transform="rotate(-15 64 28)" />
          <ellipse cx="74" cy="28" rx="4" ry="5" fill="#1E293B" transform="rotate(15 74 28)" />
          <ellipse cx="69" cy="34" rx="3" ry="2" fill="#1E293B" />
          {/* Eye */}
          <circle cx="74" cy="28" r="1.5" fill="#FFF" />
        </svg>
      )}
    </motion.div>
  );
};

