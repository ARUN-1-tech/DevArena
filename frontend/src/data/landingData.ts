export interface LiveActivity {
  id: string;
  icon: string;
  title: string;
  tag: string;
}

export interface GameModeItem {
  id: string;
  icon: string;
  title: string;
  description: string;
  accent: 'cyan' | 'amber' | 'emerald' | 'violet';
}

export interface SkillNodeItem {
  id: string;
  label: string;
  icon: string;
  category: string;
  mastery: number;
  bonus: string;
  description: string;
}

export interface PodiumPlayer {
  rank: number;
  name: string;
  rating: number;
  badge: string;
  avatarBg: string;
}

export interface AchievementMedal {
  id: string;
  icon: string;
  title: string;
  description: string;
  color: string;
  border: string;
}

// -----------------------------------------------------------------------------
// 02. Live Arena: Exactly 3 readable activities + 1 counter
// -----------------------------------------------------------------------------
export const LIVE_ACTIVITIES: LiveActivity[] = [
  {
    id: 'act-1',
    icon: '⚔️',
    title: 'Arun defeated ByteMaster',
    tag: '+240 XP',
  },
  {
    id: 'act-2',
    icon: '🔥',
    title: 'CodeNinja is on a 12 win streak',
    tag: 'Flame Bonus',
  },
  {
    id: 'act-3',
    icon: '🏆',
    title: 'DevGhost reached Diamond',
    tag: 'Tier Up',
  },
];

// -----------------------------------------------------------------------------
// 03. Game Modes: Exactly 4 friendly primary modes
// -----------------------------------------------------------------------------
export const PRIMARY_GAME_MODES: GameModeItem[] = [
  {
    id: 'duel',
    icon: '⚔️',
    title: '1v1 DUEL',
    description: 'Battle another developer in a live head-to-head coding race.',
    accent: 'cyan',
  },
  {
    id: 'blitz',
    icon: '⚡',
    title: 'SPEED BLITZ',
    description: 'Solve fast. 5-minute sudden death sprints for rapid points.',
    accent: 'amber',
  },
  {
    id: 'bug-hunt',
    icon: '🐛',
    title: 'BUG HUNT',
    description: 'Find and fix edge-case bugs hiding in production code.',
    accent: 'emerald',
  },
  {
    id: 'team-battle',
    icon: '👥',
    title: 'TEAM BATTLE',
    description: 'Fight together in cooperative relay challenges with friends.',
    accent: 'violet',
  },
];

// -----------------------------------------------------------------------------
// 04. Progression Journey: 5 steps + Arun's profile
// -----------------------------------------------------------------------------
export const PROGRESSION_STEPS = [
  { rank: 'NOVICE', rating: '0+' },
  { rank: 'DEVELOPER', rating: '1200+' },
  { rank: 'ENGINEER', rating: '1500+' },
  { rank: 'ARCHITECT', rating: '1800+' },
  { rank: 'MASTER', rating: '2200+' },
];

export const FEATURED_PLAYER = {
  name: 'ARUN',
  level: 28,
  currentXp: 8450,
  targetXp: 10000,
  winRate: 72.8,
  rating: 1842,
  tier: 'ARCHITECT',
};

// -----------------------------------------------------------------------------
// 05. Skill Tree: Compact visual node preview + selectable node details
// -----------------------------------------------------------------------------
export const SKILL_NODES: SkillNodeItem[] = [
  {
    id: 'dp',
    label: 'Dynamic Programming',
    icon: '🧠',
    category: 'Algorithms',
    mastery: 72,
    bonus: '+35% XP',
    description: 'Subproblem memoization, state transition matrices, and knapsack optimizations.',
  },
  {
    id: 'arrays',
    label: 'Arrays & Strings',
    icon: '📦',
    category: 'Data Structures',
    mastery: 95,
    bonus: '+20% XP',
    description: 'Sliding windows, two-pointer traversals, prefix sums, and fast substring hashes.',
  },
  {
    id: 'graphs',
    label: 'Graphs & Networks',
    icon: '🌐',
    category: 'Algorithms',
    mastery: 84,
    bonus: '+40% XP',
    description: 'Shortest paths, topological sorting, connected components, and BFS/DFS.',
  },
  {
    id: 'trees',
    label: 'Trees & Heaps',
    icon: '🌲',
    category: 'Data Structures',
    mastery: 68,
    bonus: '+25% XP',
    description: 'Trie lookups, binary search trees, segment trees, and priority heaps.',
  },
];

// -----------------------------------------------------------------------------
// 06. Leaderboard: Friendly Top 3 Podium
// -----------------------------------------------------------------------------
export const LEADERBOARD_PODIUM: PodiumPlayer[] = [
  {
    rank: 1,
    name: 'CodeNinja',
    rating: 2482,
    badge: '🥇 Arena Champion',
    avatarBg: 'bg-amber-100 text-amber-900 border-amber-300',
  },
  {
    rank: 2,
    name: 'ByteMaster',
    rating: 2391,
    badge: '🥈 Speed Legend',
    avatarBg: 'bg-slate-100 text-slate-800 border-slate-300',
  },
  {
    rank: 3,
    name: 'Arun',
    rating: 2318,
    badge: '🥉 Duel Master',
    avatarBg: 'bg-amber-50 text-amber-800 border-amber-200',
  },
];

// -----------------------------------------------------------------------------
// 07. Achievements: Exactly 4 collectible game medals
// -----------------------------------------------------------------------------
export const ACHIEVEMENTS_MEDALS: AchievementMedal[] = [
  {
    id: 'on-fire',
    icon: '🔥',
    title: 'ON FIRE',
    description: '5 win streak in 1v1 ranked duels',
    color: 'bg-amber-50 text-amber-700',
    border: 'border-amber-200',
  },
  {
    id: 'speed-demon',
    icon: '⚡',
    title: 'SPEED DEMON',
    description: 'Solve a challenge under 2 minutes',
    color: 'bg-cyan-50 text-cyan-700',
    border: 'border-cyan-200',
  },
  {
    id: 'bug-slayer',
    icon: '🐛',
    title: 'BUG SLAYER',
    description: 'Fix 100 hidden bugs and edge cases',
    color: 'bg-emerald-50 text-emerald-700',
    border: 'border-emerald-200',
  },
  {
    id: 'arena-king',
    icon: '👑',
    title: 'ARENA KING',
    description: 'Reach Grandmaster ranking division',
    color: 'bg-violet-50 text-violet-700',
    border: 'border-violet-200',
  },
];
