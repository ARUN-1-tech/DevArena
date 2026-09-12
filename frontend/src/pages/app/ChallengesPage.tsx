import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { challengeService } from '../../services/challengeService';
import {
  Challenge,
  ChallengeDifficulty,
  ChallengeCategory,
  ProblemType,
  ChallengeStats,
} from '../../types/arena';
import { Card } from '../../components/ui/Card';
import { Badge } from '../../components/ui/Badge';
import { Input } from '../../components/ui/Input';
import { Button } from '../../components/ui/Button';
import {
  Code2,
  Search,
  CheckCircle2,
  Clock,
  Sparkles,
  Play,
  Filter,
  ArrowRight,
  Loader2,
  Database,
  Cpu,
  HelpCircle,
  Layers,
  ChevronLeft,
  ChevronRight,
  BookOpen,
  ArrowUpDown,
  RotateCcw,
  Zap,
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

const DIFFICULTIES: { label: string; value?: ChallengeDifficulty; color: string }[] = [
  { label: 'All Difficulties', value: undefined, color: 'text-slate-600' },
  { label: 'Easy', value: 'EASY', color: 'text-emerald-700' },
  { label: 'Medium', value: 'MEDIUM', color: 'text-amber-700' },
  { label: 'Hard', value: 'HARD', color: 'text-rose-700' },
  { label: 'Expert', value: 'EXPERT', color: 'text-purple-700' },
];

const PROBLEM_TYPES: { label: string; value?: ProblemType; icon?: any }[] = [
  { label: 'All Types', value: undefined },
  { label: 'Coding', value: 'CODING', icon: Code2 },
  { label: 'MCQ', value: 'MCQ', icon: HelpCircle },
  { label: 'Aptitude', value: 'APTITUDE', icon: Zap },
  { label: 'SQL / DB', value: 'SQL', icon: Database },
  { label: 'OS & Core', value: 'OS', icon: Cpu },
  { label: 'Networking', value: 'NETWORKING', icon: Layers },
  { label: 'Puzzles', value: 'PUZZLE', icon: Sparkles },
  { label: 'Interview', value: 'INTERVIEW', icon: BookOpen },
];

const CATEGORIES: { label: string; value?: ChallengeCategory }[] = [
  { label: 'All Topic Categories', value: undefined },
  { label: 'Arrays & Two Pointers', value: 'ARRAYS' },
  { label: 'Strings & Parsing', value: 'STRINGS' },
  { label: 'Linked Lists', value: 'LINKED_LIST' },
  { label: 'Stack & Queue', value: 'STACK_QUEUE' },
  { label: 'Trees & BST', value: 'TREES' },
  { label: 'Graphs & BFS/DFS', value: 'GRAPHS' },
  { label: 'Dynamic Programming', value: 'DYNAMIC_PROGRAMMING' },
  { label: 'Binary Search', value: 'BINARY_SEARCH' },
  { label: 'Backtracking & Recursion', value: 'BACKTRACKING' },
  { label: 'Heaps & Priority Queues', value: 'HEAPS_PRIORITY_QUEUES' },
  { label: 'Bit Manipulation', value: 'BIT_MANIPULATION' },
  { label: 'Greedy Algorithms', value: 'GREEDY' },
  { label: 'SQL & Database Design', value: 'SQL' },
  { label: 'DBMS Architecture', value: 'DBMS' },
  { label: 'Operating Systems & Concurrency', value: 'OPERATING_SYSTEMS' },
  { label: 'Computer Networks & Protocols', value: 'NETWORKING' },
  { label: 'Mathematical Aptitude & Logic', value: 'MATH_APTITUDE' },
  { label: 'Brain Teasers & Puzzles', value: 'PUZZLES' },
  { label: 'Algorithms & Problem Solving', value: 'ALGORITHMS' },
  { label: 'System Design & Scalability', value: 'SYSTEM_DESIGN' },
  { label: 'General CS Foundations', value: 'GENERAL_CS' },
];

const SORT_OPTIONS = [
  { label: 'Recommended', value: 'recommended' },
  { label: 'Newest First', value: 'newest' },
  { label: 'XP (Highest First)', value: 'xp' },
  { label: 'Difficulty (Easy → Hard)', value: 'difficulty' },
];

const PAGE_SIZE = 25;

export const ChallengesPage: React.FC = () => {
  const navigate = useNavigate();

  const [challenges, setChallenges] = useState<Challenge[]>([]);
  const [stats, setStats] = useState<ChallengeStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [selectedDifficulty, setSelectedDifficulty] = useState<ChallengeDifficulty | undefined>(undefined);
  const [selectedCategory, setSelectedCategory] = useState<ChallengeCategory | undefined>(undefined);
  const [selectedType, setSelectedType] = useState<ProblemType | undefined>(undefined);
  const [sortBy, setSortBy] = useState('recommended');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalCount, setTotalCount] = useState(0);

  useEffect(() => {
    loadStats();
  }, []);

  useEffect(() => {
    loadChallenges();
  }, [search, selectedDifficulty, selectedCategory, selectedType, sortBy, page]);

  const loadStats = async () => {
    try {
      const s = await challengeService.getChallengeStats();
      setStats(s);
    } catch (err) {
      console.warn('Could not load challenge stats:', err);
    }
  };

  const loadChallenges = async () => {
    try {
      setLoading(true);
      const res = await challengeService.getChallenges({
        search: search.trim() || undefined,
        difficulty: selectedDifficulty,
        category: selectedCategory,
        problemType: selectedType,
        sortBy,
        page,
        size: PAGE_SIZE,
      });
      setChallenges(res.content || []);
      setTotalCount(res.totalElements || 0);
      setTotalPages(res.totalPages || 1);
    } catch (err) {
      console.error('Failed to load challenges:', err);
      setChallenges([]);
    } finally {
      setLoading(false);
    }
  };

  const resetFilters = () => {
    setSearch('');
    setSelectedDifficulty(undefined);
    setSelectedCategory(undefined);
    setSelectedType(undefined);
    setSortBy('recommended');
    setPage(0);
  };

  const hasActiveFilters = Boolean(
    search || selectedDifficulty || selectedCategory || selectedType || sortBy !== 'recommended'
  );

  return (
    <div className="space-y-8 max-w-7xl mx-auto pb-12">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <Badge variant="cyan" size="sm" className="mb-2">
            <Code2 className="w-3.5 h-3.5 mr-1" />
            PROBLEM ARCHIVE
          </Badge>
          <h1 className="text-3xl sm:text-4xl font-black text-slate-900 tracking-tight">
            COMPLETE PROBLEM ARCHIVE
          </h1>
          <p className="text-sm text-slate-600 mt-1">
            Practice, compete, solve and level up across all algorithmic katas, core CS topics, and interview questions.
          </p>
        </div>

        {/* Available Problems Stats Banner */}
        <div className="flex items-center gap-3 shrink-0">
          <div className="bg-white/95 backdrop-blur-md px-5 py-3 rounded-2xl border border-slate-200/90 shadow-premium font-mono flex items-center gap-4">
            <div>
              <p className="text-[10px] text-slate-400 uppercase font-bold tracking-wider">AVAILABLE PROBLEMS</p>
              <p className="text-xl font-black text-slate-900">
                {stats?.totalChallenges ?? totalCount} <span className="text-xs font-semibold text-slate-500">Challenges</span>
              </p>
            </div>
            {stats && (
              <div className="pl-4 border-l border-slate-100 flex flex-col justify-center">
                <span className="inline-flex items-center gap-1 text-[11px] font-bold text-emerald-700 bg-emerald-50 px-2.5 py-0.5 rounded-full border border-emerald-200">
                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
                  {stats.totalSolved} Solved
                </span>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Filter and Search Bar */}
      <div className="bg-white/95 backdrop-blur-xl p-5 rounded-3xl border border-slate-200/90 shadow-premium space-y-4">
        {/* Row 1: Search + Category + Sort */}
        <div className="flex flex-col md:flex-row gap-3">
          <div className="flex-1">
            <Input
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                setPage(0);
              }}
              placeholder="Search 100+ problems by title, description, or concept tags..."
              leftIcon={<Search className="w-4 h-4 text-slate-400" />}
              className="bg-slate-50/70 border-slate-200 focus:bg-white"
            />
          </div>

          <div className="md:w-64">
            <select
              value={selectedCategory || ''}
              onChange={(e) => {
                setSelectedCategory(e.target.value ? (e.target.value as ChallengeCategory) : undefined);
                setPage(0);
              }}
              aria-label="Filter by Topic Category"
              className="w-full h-11 px-3 rounded-xl border border-slate-200 bg-slate-50/70 text-sm text-slate-700 font-mono focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:bg-white transition-all cursor-pointer"
            >
              {CATEGORIES.map((cat) => (
                <option key={cat.label} value={cat.value || ''}>
                  {cat.label}
                </option>
              ))}
            </select>
          </div>

          <div className="md:w-56 flex items-center gap-1.5 bg-slate-50/70 border border-slate-200 rounded-xl px-2.5">
            <ArrowUpDown className="w-3.5 h-3.5 text-slate-400 shrink-0" />
            <select
              value={sortBy}
              onChange={(e) => {
                setSortBy(e.target.value);
                setPage(0);
              }}
              aria-label="Sort Problems"
              className="w-full h-11 bg-transparent text-sm text-slate-700 font-mono focus:outline-none cursor-pointer"
            >
              {SORT_OPTIONS.map((sort) => (
                <option key={sort.value} value={sort.value}>
                  {sort.label}
                </option>
              ))}
            </select>
          </div>

          {hasActiveFilters && (
            <Button
              variant="outline"
              size="md"
              onClick={resetFilters}
              leftIcon={<RotateCcw className="w-3.5 h-3.5" />}
              className="shrink-0 text-slate-600 hover:text-slate-900 border-dashed"
            >
              Reset
            </Button>
          )}
        </div>

        {/* Row 2: Problem Type Filter Pills */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 font-mono text-xs scrollbar-none">
          <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400 mr-1 shrink-0 flex items-center gap-1">
            <Layers className="w-3 h-3" /> TYPE:
          </span>
          {PROBLEM_TYPES.map((pt) => {
            const isSelected = selectedType === pt.value;
            const Icon = pt.icon;
            return (
              <button
                key={pt.label}
                onClick={() => {
                  setSelectedType(pt.value);
                  setPage(0);
                }}
                className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all shrink-0 cursor-pointer flex items-center gap-1.5 ${
                  isSelected
                    ? 'bg-slate-900 text-white shadow-sm border border-slate-800'
                    : 'bg-slate-100/80 hover:bg-slate-200/80 text-slate-600 border border-transparent'
                }`}
              >
                {Icon && <Icon className="w-3 h-3" />}
                {pt.label}
              </button>
            );
          })}
        </div>

        {/* Row 3: Difficulty Filter Pills */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 font-mono text-xs scrollbar-none pt-1 border-t border-slate-100">
          <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400 mr-1 shrink-0 flex items-center gap-1">
            <Filter className="w-3 h-3" /> DIFFICULTY:
          </span>
          {DIFFICULTIES.map((diff) => {
            const isSelected = selectedDifficulty === diff.value;
            return (
              <button
                key={diff.label}
                onClick={() => {
                  setSelectedDifficulty(diff.value);
                  setPage(0);
                }}
                className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all shrink-0 cursor-pointer ${
                  isSelected
                    ? 'bg-gradient-to-r from-indigo-600 to-violet-600 text-white shadow-sm shadow-indigo-500/25 border border-indigo-500/30'
                    : 'bg-slate-100/80 hover:bg-slate-200/80 text-slate-600 border border-transparent'
                }`}
              >
                {diff.label}
              </button>
            );
          })}
        </div>
      </div>

      {/* Challenges List */}
      {loading ? (
        <div className="flex flex-col items-center justify-center py-24 space-y-4">
          <Loader2 className="w-9 h-9 animate-spin text-indigo-600" />
          <p className="text-sm font-mono text-slate-500">Querying live database archive...</p>
        </div>
      ) : challenges.length === 0 ? (
        <Card className="p-12 text-center max-w-md mx-auto space-y-4 bg-white rounded-3xl border border-slate-200/90 shadow-premium">
          <div className="w-12 h-12 rounded-2xl bg-indigo-50 flex items-center justify-center mx-auto text-indigo-600">
            <Code2 className="w-6 h-6" />
          </div>
          <h3 className="text-base font-bold text-slate-800">No challenges match your criteria</h3>
          <p className="text-xs text-slate-500">
            Try adjusting your keywords, switching categories, or clearing difficulty and problem type filters.
          </p>
          <Button variant="outline" size="sm" onClick={resetFilters}>
            Clear All Filters
          </Button>
        </Card>
      ) : (
        <div className="space-y-3">
          <AnimatePresence mode="popLayout">
            {challenges.map((ch, index) => {
              const isSolved = ch.progressStatus === 'SOLVED';
              const isAttempted = ch.progressStatus === 'ATTEMPTED';
              const pType = ch.problemType || 'CODING';

              return (
                <motion.div
                  key={ch.id}
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, scale: 0.98 }}
                  transition={{ duration: 0.2, delay: Math.min(index * 0.02, 0.3) }}
                >
                  <Card
                    className="p-5 bg-white/95 backdrop-blur-xl border border-slate-200/80 shadow-premium hover:shadow-premium-hover hover:border-indigo-300/80 transition-all duration-300 rounded-2xl cursor-pointer flex flex-col md:flex-row md:items-center justify-between gap-4 group shimmer-card"
                    onClick={() => navigate(`/challenges/${ch.id}`)}
                  >
                    <div className="space-y-2 flex-1 min-w-0">
                      <div className="flex items-center gap-2 flex-wrap">
                        {/* Title */}
                        <span className="text-base font-black text-slate-900 group-hover:text-indigo-600 transition-colors">
                          {ch.title}
                        </span>

                        {/* Problem Type Badge */}
                        <span className="text-[10px] font-mono px-2 py-0.5 rounded-full font-bold uppercase tracking-wider bg-slate-100 text-slate-700 border border-slate-200 shadow-2xs">
                          {pType}
                        </span>

                        {/* Difficulty Badge */}
                        <span
                          className={`text-[10px] font-mono px-2 py-0.5 rounded-full font-bold uppercase shadow-2xs ${
                            ch.difficulty === 'EASY'
                              ? 'bg-emerald-50 text-emerald-800 border border-emerald-200'
                              : ch.difficulty === 'MEDIUM'
                              ? 'bg-amber-50 text-amber-800 border border-amber-200'
                              : ch.difficulty === 'HARD'
                              ? 'bg-rose-50 text-rose-800 border border-rose-200'
                              : 'bg-purple-50 text-purple-800 border border-purple-200'
                          }`}
                        >
                          {ch.difficulty}
                        </span>

                        {/* Status indicator */}
                        {isSolved ? (
                          <span className="inline-flex items-center gap-1 text-[10px] font-mono font-bold text-emerald-700 bg-emerald-50 px-2.5 py-0.5 rounded-full border border-emerald-200 shadow-2xs">
                            <CheckCircle2 className="w-3 h-3 text-emerald-600" />
                            SOLVED
                          </span>
                        ) : isAttempted ? (
                          <span className="inline-flex items-center gap-1 text-[10px] font-mono font-bold text-amber-800 bg-amber-50 px-2.5 py-0.5 rounded-full border border-amber-200 shadow-2xs">
                            <Play className="w-2.5 h-2.5 fill-amber-600 text-amber-600" />
                            ATTEMPTED
                          </span>
                        ) : null}
                      </div>

                      {/* Meta information row */}
                      <div className="flex items-center gap-2.5 text-xs font-mono text-slate-500 flex-wrap">
                        <span className="font-semibold text-slate-700 bg-slate-100/70 px-2 py-0.5 rounded-md">
                          {ch.category.replace(/_/g, ' ')}
                        </span>
                        {ch.source && (
                          <span className="text-[11px] text-indigo-600 font-semibold bg-indigo-50/70 px-2 py-0.5 rounded-md border border-indigo-100">
                            {ch.source}
                          </span>
                        )}
                        {ch.tags && (
                          <span className="text-slate-400 truncate max-w-[320px]">
                            {ch.tags.split(',').map((t) => `#${t.trim()}`).join(' ')}
                          </span>
                        )}
                      </div>
                    </div>

                    {/* Right: XP, time, and button */}
                    <div className="flex items-center justify-between md:justify-end gap-5 shrink-0 font-mono pt-2 md:pt-0 border-t md:border-t-0 border-slate-100">
                      <div className="text-left md:text-right">
                        <span className="text-sm font-black text-indigo-600 flex items-center md:justify-end gap-1">
                          <Sparkles className="w-3.5 h-3.5 text-amber-500" />
                          +{ch.xpReward} XP
                        </span>
                        <span className="text-[11px] text-slate-400 flex items-center md:justify-end gap-1 mt-0.5 font-medium">
                          <Clock className="w-3 h-3" />
                          {ch.estimatedMinutes || Math.round((ch.timeLimitSeconds || 900) / 60)}m
                        </span>
                      </div>

                      <div className="w-10 h-10 rounded-xl bg-slate-100/90 group-hover:bg-indigo-600 group-hover:text-white text-slate-400 flex items-center justify-center transition-all duration-200 group-hover:scale-105 group-hover:shadow-md">
                        <ArrowRight className="w-4 h-4 group-hover:translate-x-0.5 transition-transform" />
                      </div>
                    </div>
                  </Card>
                </motion.div>
              );
            })}
          </AnimatePresence>

          {/* Pagination Controls */}
          {totalPages > 1 && (
            <div className="flex flex-col sm:flex-row items-center justify-between gap-4 pt-6 font-mono">
              <p className="text-xs text-slate-500">
                Showing <span className="font-bold text-slate-700">{page * PAGE_SIZE + 1}</span> to{' '}
                <span className="font-bold text-slate-700">{Math.min((page + 1) * PAGE_SIZE, totalCount)}</span> of{' '}
                <span className="font-bold text-slate-700">{totalCount}</span> problems
              </p>

              <div className="flex items-center gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  disabled={page === 0}
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                  leftIcon={<ChevronLeft className="w-4 h-4" />}
                >
                  Previous
                </Button>

                <div className="flex items-center gap-1">
                  {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                    let pageNum = i;
                    if (totalPages > 5 && page > 2) {
                      pageNum = Math.min(totalPages - 1, page - 2 + i);
                    }
                    return (
                      <button
                        key={pageNum}
                        onClick={() => setPage(pageNum)}
                        className={`w-8 h-8 rounded-lg text-xs font-bold transition-all ${
                          page === pageNum
                            ? 'bg-indigo-600 text-white shadow-sm'
                            : 'bg-white hover:bg-slate-100 text-slate-700 border border-slate-200'
                        }`}
                      >
                        {pageNum + 1}
                      </button>
                    );
                  })}
                </div>

                <Button
                  variant="outline"
                  size="sm"
                  disabled={page >= totalPages - 1}
                  onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                  rightIcon={<ChevronRight className="w-4 h-4" />}
                >
                  Next
                </Button>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

