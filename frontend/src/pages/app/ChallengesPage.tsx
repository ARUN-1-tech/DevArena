import React, { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { challengeService, ChallengeFilterParams } from '../../services/challengeService';
import { Challenge, ChallengeDifficulty, ChallengeCategory, ProblemType } from '../../types/arena';
import { Card } from '../../components/ui/Card';
import { Input } from '../../components/ui/Input';
import { Button } from '../../components/ui/Button';
import {
  Code2, Search, CheckCircle2, Clock, Sparkles, Play, Filter,
  ArrowRight, Loader2, Database, Brain, Globe, Cpu, Puzzle,
  ChevronLeft, ChevronRight, X, SlidersHorizontal, BookOpen,
  Hash, Trophy,
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';

// ─────────────────────────────────────────────────────────────────────────────
// Config
// ─────────────────────────────────────────────────────────────────────────────
const DIFFICULTIES: { label: string; value?: ChallengeDifficulty }[] = [
  { label: 'All', value: undefined },
  { label: 'Easy', value: 'EASY' },
  { label: 'Medium', value: 'MEDIUM' },
  { label: 'Hard', value: 'HARD' },
  { label: 'Expert', value: 'EXPERT' },
];

const PROBLEM_TYPES: { label: string; value?: ProblemType; icon: React.ElementType }[] = [
  { label: 'All Types', value: undefined, icon: Hash },
  { label: 'Coding', value: 'CODING', icon: Code2 },
  { label: 'MCQ', value: 'MCQ', icon: BookOpen },
  { label: 'SQL', value: 'SQL', icon: Database },
  { label: 'Aptitude', value: 'APTITUDE', icon: Brain },
  { label: 'OS', value: 'OS', icon: Cpu },
  { label: 'Networking', value: 'NETWORKING', icon: Globe },
  { label: 'Puzzle', value: 'PUZZLE', icon: Puzzle },
  { label: 'Interview', value: 'INTERVIEW', icon: Trophy },
];

const CATEGORIES: { label: string; value?: ChallengeCategory }[] = [
  { label: 'All Categories', value: undefined },
  { label: 'Arrays', value: 'ARRAYS' },
  { label: 'Strings', value: 'STRINGS' },
  { label: 'Two Pointers', value: 'TWO_POINTERS' },
  { label: 'Sliding Window', value: 'SLIDING_WINDOW' },
  { label: 'Linked List', value: 'LINKED_LIST' },
  { label: 'Stack & Queue', value: 'STACK_QUEUE' },
  { label: 'Trees', value: 'TREES' },
  { label: 'Graphs', value: 'GRAPHS' },
  { label: 'Dynamic Programming', value: 'DYNAMIC_PROGRAMMING' },
  { label: 'Binary Search', value: 'BINARY_SEARCH' },
  { label: 'Heaps', value: 'HEAPS' },
  { label: 'Backtracking', value: 'BACKTRACKING' },
  { label: 'Greedy', value: 'GREEDY' },
  { label: 'Bit Manipulation', value: 'BIT_MANIPULATION' },
  { label: 'Sorting', value: 'SORTING' },
  { label: 'SQL / DB', value: 'SQL_DB' },
  { label: 'Operating Systems', value: 'OPERATING_SYSTEMS' },
  { label: 'Networking', value: 'NETWORKING' },
  { label: 'Algorithms', value: 'ALGORITHMS' },
  { label: 'Aptitude', value: 'APTITUDE' },
  { label: 'Puzzles', value: 'PUZZLES' },
  { label: 'Interview', value: 'INTERVIEW' },
];

const SORT_OPTIONS = [
  { label: 'Newest First', value: 'newest' },
  { label: 'XP: High → Low', value: 'xp_desc' },
  { label: 'XP: Low → High', value: 'xp_asc' },
  { label: 'Easiest First', value: 'difficulty_asc' },
  { label: 'Hardest First', value: 'difficulty_desc' },
  { label: 'Title A–Z', value: 'title_asc' },
] as const;

const PAGE_SIZE = 20;

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────
function difficultyColor(d: ChallengeDifficulty) {
  return {
    EASY: 'bg-emerald-50 text-emerald-700 border-emerald-200',
    MEDIUM: 'bg-amber-50 text-amber-700 border-amber-200',
    HARD: 'bg-rose-50 text-rose-700 border-rose-200',
    EXPERT: 'bg-purple-50 text-purple-700 border-purple-200',
  }[d];
}

function problemTypeBadge(t: ProblemType) {
  const map: Record<string, string> = {
    CODING: 'bg-indigo-50 text-indigo-700 border-indigo-200',
    MCQ: 'bg-cyan-50 text-cyan-700 border-cyan-200',
    SQL: 'bg-teal-50 text-teal-700 border-teal-200',
    APTITUDE: 'bg-orange-50 text-orange-700 border-orange-200',
    OS: 'bg-slate-100 text-slate-700 border-slate-300',
    NETWORKING: 'bg-blue-50 text-blue-700 border-blue-200',
    PUZZLE: 'bg-pink-50 text-pink-700 border-pink-200',
    INTERVIEW: 'bg-violet-50 text-violet-700 border-violet-200',
    DBMS: 'bg-teal-50 text-teal-700 border-teal-200',
    GENERAL: 'bg-slate-50 text-slate-600 border-slate-200',
  };
  return map[t] || 'bg-slate-50 text-slate-600 border-slate-200';
}

function formatCategory(cat: string) {
  return cat.replace(/_/g, ' ');
}

// ─────────────────────────────────────────────────────────────────────────────
// Component
// ─────────────────────────────────────────────────────────────────────────────
export const ChallengesPage: React.FC = () => {
  const navigate = useNavigate();

  const [challenges, setChallenges] = useState<Challenge[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalCount, setTotalCount] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);

  const [search, setSearch] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const [selectedDifficulty, setSelectedDifficulty] = useState<ChallengeDifficulty | undefined>(undefined);
  const [selectedCategory, setSelectedCategory] = useState<ChallengeCategory | undefined>(undefined);
  const [selectedType, setSelectedType] = useState<ProblemType | undefined>(undefined);
  const [sort, setSort] = useState<ChallengeFilterParams['sort']>('newest');

  // Debounce search
  useEffect(() => {
    const t = setTimeout(() => setDebouncedSearch(search), 350);
    return () => clearTimeout(t);
  }, [search]);

  // Reset page on filter change
  useEffect(() => {
    setCurrentPage(0);
  }, [debouncedSearch, selectedDifficulty, selectedCategory, selectedType, sort]);

  const loadChallenges = useCallback(async () => {
    try {
      setLoading(true);
      const res = await challengeService.getChallenges({
        search: debouncedSearch.trim() || undefined,
        difficulty: selectedDifficulty,
        category: selectedCategory,
        type: selectedType,
        sort,
        page: currentPage,
        size: PAGE_SIZE,
      });
      setChallenges(res.content);
      setTotalCount(res.totalElements);
      setTotalPages(res.totalPages);
    } catch (err) {
      console.error('Failed to load challenges', err);
    } finally {
      setLoading(false);
    }
  }, [debouncedSearch, selectedDifficulty, selectedCategory, selectedType, sort, currentPage]);

  useEffect(() => {
    loadChallenges();
  }, [loadChallenges]);

  const clearFilters = () => {
    setSearch('');
    setSelectedDifficulty(undefined);
    setSelectedCategory(undefined);
    setSelectedType(undefined);
    setSort('newest');
    setCurrentPage(0);
  };

  const hasFilters = !!(search || selectedDifficulty || selectedCategory || selectedType || sort !== 'newest');

  // ── Rendered ──────────────────────────────────────────────────────────────
  return (
    <div className="space-y-6 max-w-6xl mx-auto">
      {/* ── Header ── */}
      <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-4">
        <div>
          <div className="inline-flex items-center gap-1.5 text-[10px] font-mono font-bold text-indigo-600 bg-indigo-50 border border-indigo-200/80 px-2.5 py-1 rounded-full mb-2 uppercase tracking-wider">
            <BookOpen className="w-3 h-3" />
            Problem Archive
          </div>
          <h1 className="text-3xl sm:text-4xl font-black text-slate-900 tracking-tight">
            PROBLEM ARCHIVE
          </h1>
          <p className="text-sm text-slate-500 mt-1">
            Practice, compete, solve and level up. Coding · SQL · MCQ · Aptitude · Interview · Puzzles
          </p>
        </div>

        <div className="bg-white px-4 py-3 rounded-2xl border border-slate-200/90 shadow-sm shrink-0 text-right font-mono">
          <p className="text-[10px] text-slate-400 uppercase font-semibold tracking-wider">Available Problems</p>
          <p className="text-2xl font-black text-slate-900 leading-tight">
            {totalCount.toLocaleString()}
            <span className="text-sm font-bold text-slate-400 ml-1">Challenges</span>
          </p>
        </div>
      </div>

      {/* ── Filters Panel ── */}
      <div className="bg-white rounded-2xl border border-slate-200/90 shadow-sm p-4 space-y-4">
        {/* Row 1: Search + Category + Sort */}
        <div className="flex flex-col sm:flex-row gap-3">
          <div className="flex-1">
            <Input
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search by title, concept, or tag..."
              leftIcon={<Search className="w-4 h-4 text-slate-400" />}
            />
          </div>

          <select
            value={selectedCategory || ''}
            onChange={(e) => setSelectedCategory(e.target.value ? (e.target.value as ChallengeCategory) : undefined)}
            aria-label="Filter by Category"
            className="sm:w-52 h-11 px-3 rounded-xl border border-slate-200 bg-white text-sm text-slate-700 font-mono focus:outline-none focus:ring-2 focus:ring-indigo-400 cursor-pointer"
          >
            {CATEGORIES.map((c) => (
              <option key={c.label} value={c.value || ''}>{c.label}</option>
            ))}
          </select>

          <select
            value={sort}
            onChange={(e) => setSort(e.target.value as ChallengeFilterParams['sort'])}
            aria-label="Sort challenges"
            className="sm:w-44 h-11 px-3 rounded-xl border border-slate-200 bg-white text-sm text-slate-700 font-mono focus:outline-none focus:ring-2 focus:ring-indigo-400 cursor-pointer"
          >
            {SORT_OPTIONS.map((o) => (
              <option key={o.value} value={o.value}>{o.label}</option>
            ))}
          </select>
        </div>

        {/* Row 2: Problem Type pills */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 no-scrollbar">
          <SlidersHorizontal className="w-3.5 h-3.5 text-slate-400 shrink-0 mr-1" />
          {PROBLEM_TYPES.map(({ label, value, icon: Icon }) => {
            const active = selectedType === value;
            return (
              <button
                key={label}
                onClick={() => setSelectedType(value)}
                className={`inline-flex items-center gap-1 px-3 py-1.5 rounded-xl text-xs font-bold font-mono shrink-0 transition-all cursor-pointer ${
                  active
                    ? 'bg-gradient-to-r from-indigo-600 to-violet-600 text-white shadow-sm shadow-indigo-400/30'
                    : 'bg-slate-50 border border-slate-200 text-slate-600 hover:border-indigo-300 hover:text-indigo-600 hover:bg-indigo-50'
                }`}
              >
                <Icon className="w-3 h-3" />
                {label}
              </button>
            );
          })}
        </div>

        {/* Row 3: Difficulty pills + Clear */}
        <div className="flex items-center justify-between gap-2">
          <div className="flex items-center gap-1.5 overflow-x-auto no-scrollbar">
            <Filter className="w-3.5 h-3.5 text-slate-400 shrink-0 mr-1" />
            {DIFFICULTIES.map(({ label, value }) => {
              const active = selectedDifficulty === value;
              const colorMap: Record<string, string> = {
                EASY: 'from-emerald-500 to-teal-500',
                MEDIUM: 'from-amber-500 to-orange-500',
                HARD: 'from-rose-500 to-red-600',
                EXPERT: 'from-purple-600 to-violet-600',
              };
              return (
                <button
                  key={label}
                  onClick={() => setSelectedDifficulty(value)}
                  className={`px-3 py-1.5 rounded-xl text-xs font-bold font-mono shrink-0 transition-all cursor-pointer ${
                    active
                      ? `bg-gradient-to-r ${colorMap[value || ''] || 'from-slate-600 to-slate-700'} text-white shadow-sm`
                      : 'bg-slate-50 border border-slate-200 text-slate-600 hover:bg-slate-100'
                  }`}
                >
                  {label}
                </button>
              );
            })}
          </div>

          {hasFilters && (
            <button
              onClick={clearFilters}
              className="inline-flex items-center gap-1 text-xs font-bold font-mono text-rose-600 bg-rose-50 hover:bg-rose-100 border border-rose-200 px-2.5 py-1.5 rounded-xl shrink-0 transition-all cursor-pointer"
            >
              <X className="w-3 h-3" />
              Clear
            </button>
          )}
        </div>
      </div>

      {/* ── Challenge List ── */}
      {loading ? (
        <div className="flex flex-col items-center justify-center py-24 space-y-3">
          <Loader2 className="w-8 h-8 animate-spin text-indigo-500" />
          <p className="text-sm font-mono text-slate-400">Loading problem archive...</p>
        </div>
      ) : challenges.length === 0 ? (
        <Card className="p-14 text-center max-w-md mx-auto space-y-3">
          <BookOpen className="w-10 h-10 text-slate-300 mx-auto" />
          <h3 className="text-base font-bold text-slate-700">No problems found</h3>
          <p className="text-xs text-slate-400">
            Try adjusting your search or clearing the filters.
          </p>
          <Button variant="outline" size="sm" onClick={clearFilters}>
            Clear All Filters
          </Button>
        </Card>
      ) : (
        <AnimatePresence mode="wait">
          <motion.div
            key={`${currentPage}-${selectedType}-${selectedDifficulty}-${sort}`}
            initial={{ opacity: 0, y: 6 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.18 }}
            className="space-y-2.5"
          >
            {challenges.map((ch, index) => (
              <ChallengeRow key={ch.id} ch={ch} index={index} navigate={navigate} />
            ))}
          </motion.div>
        </AnimatePresence>
      )}

      {/* ── Pagination ── */}
      {!loading && totalPages > 1 && (
        <div className="flex items-center justify-between pt-2 font-mono">
          <p className="text-xs text-slate-400">
            Showing {currentPage * PAGE_SIZE + 1}–{Math.min((currentPage + 1) * PAGE_SIZE, totalCount)} of {totalCount.toLocaleString()} problems
          </p>

          <div className="flex items-center gap-1.5">
            <button
              onClick={() => setCurrentPage(p => Math.max(0, p - 1))}
              disabled={currentPage === 0}
              className="w-8 h-8 rounded-xl border border-slate-200 bg-white text-slate-600 flex items-center justify-center disabled:opacity-40 hover:bg-slate-50 transition-all cursor-pointer"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>

            {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
              let page = i;
              if (totalPages > 5) {
                if (currentPage <= 2) page = i;
                else if (currentPage >= totalPages - 3) page = totalPages - 5 + i;
                else page = currentPage - 2 + i;
              }
              return (
                <button
                  key={page}
                  onClick={() => setCurrentPage(page)}
                  className={`w-8 h-8 rounded-xl text-xs font-bold transition-all cursor-pointer ${
                    page === currentPage
                      ? 'bg-indigo-600 text-white shadow-sm'
                      : 'border border-slate-200 bg-white text-slate-600 hover:bg-slate-50'
                  }`}
                >
                  {page + 1}
                </button>
              );
            })}

            <button
              onClick={() => setCurrentPage(p => Math.min(totalPages - 1, p + 1))}
              disabled={currentPage === totalPages - 1}
              className="w-8 h-8 rounded-xl border border-slate-200 bg-white text-slate-600 flex items-center justify-center disabled:opacity-40 hover:bg-slate-50 transition-all cursor-pointer"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

// ─────────────────────────────────────────────────────────────────────────────
// Challenge Row Card
// ─────────────────────────────────────────────────────────────────────────────
interface ChallengeRowProps {
  ch: Challenge;
  index: number;
  navigate: (path: string) => void;
}

const ChallengeRow: React.FC<ChallengeRowProps> = ({ ch, index, navigate }) => {
  const isSolved = ch.progressStatus === 'SOLVED';
  const isAttempted = ch.progressStatus === 'ATTEMPTED';

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.2, delay: index * 0.025 }}
    >
      <Card
        className="p-4 bg-white/95 border border-slate-200/80 hover:border-indigo-300/70 hover:shadow-md transition-all duration-250 rounded-2xl cursor-pointer flex flex-col sm:flex-row sm:items-center justify-between gap-4 group"
        onClick={() => navigate(`/challenges/${ch.id}`)}
      >
        {/* Left: Title + badges + meta */}
        <div className="space-y-1.5 flex-1 min-w-0">
          <div className="flex items-center gap-2 flex-wrap">
            {/* Title */}
            <span className="text-sm font-black text-slate-900 group-hover:text-indigo-600 transition-colors leading-snug">
              {ch.title}
            </span>

            {/* Difficulty */}
            <span className={`text-[9px] font-mono font-bold px-2 py-0.5 rounded-full border uppercase ${difficultyColor(ch.difficulty)}`}>
              {ch.difficulty}
            </span>

            {/* Problem Type */}
            {ch.problemType && ch.problemType !== 'CODING' && (
              <span className={`text-[9px] font-mono font-bold px-2 py-0.5 rounded-full border uppercase ${problemTypeBadge(ch.problemType)}`}>
                {ch.problemType}
              </span>
            )}

            {/* Progress Status */}
            {isSolved ? (
              <span className="inline-flex items-center gap-1 text-[9px] font-mono font-bold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-200">
                <CheckCircle2 className="w-2.5 h-2.5" /> SOLVED
              </span>
            ) : isAttempted ? (
              <span className="inline-flex items-center gap-1 text-[9px] font-mono font-bold text-amber-700 bg-amber-50 px-2 py-0.5 rounded-full border border-amber-200">
                <Play className="w-2.5 h-2.5 fill-amber-600" /> IN PROGRESS
              </span>
            ) : null}

            {/* Source Reference badge */}
            {ch.sourceReference && (
              <span className="text-[9px] font-mono text-slate-400 bg-slate-50 px-2 py-0.5 rounded-full border border-slate-200 hidden sm:inline">
                {ch.sourceReference}
              </span>
            )}
          </div>

          {/* Category + tags */}
          <div className="flex items-center gap-2 text-xs font-mono text-slate-400 min-w-0">
            <span className="font-semibold text-slate-600 shrink-0">
              {formatCategory(ch.category)}
            </span>
            {ch.tags && (
              <>
                <span className="shrink-0">·</span>
                <span className="text-slate-400 truncate">
                  {ch.tags.split(',').slice(0, 4).map((t) => `#${t.trim()}`).join(' ')}
                </span>
              </>
            )}
          </div>
        </div>

        {/* Right: XP + time + arrow */}
        <div className="flex items-center justify-between sm:justify-end gap-5 shrink-0 font-mono">
          <div className="text-right space-y-0.5">
            <div className="flex items-center justify-end gap-1 text-sm font-black text-indigo-600">
              <Sparkles className="w-3.5 h-3.5 text-amber-500" />
              +{ch.xpReward} XP
            </div>
            <div className="flex items-center justify-end gap-1 text-[11px] text-slate-400 font-medium">
              <Clock className="w-3 h-3" />
              {ch.estimatedMinutes}m
            </div>
          </div>

          <div className="w-8 h-8 rounded-xl bg-slate-100 group-hover:bg-indigo-50 group-hover:text-indigo-600 text-slate-400 flex items-center justify-center transition-all duration-200">
            <ArrowRight className="w-4 h-4 group-hover:translate-x-0.5 transition-transform" />
          </div>
        </div>
      </Card>
    </motion.div>
  );
};
