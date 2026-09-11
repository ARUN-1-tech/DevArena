import React from 'react';
import { motion } from 'framer-motion';
import { cn } from '../../lib/utils';

export interface GlowBadgeProps {
  label: string;
  className?: string;
  glowColor?: 'cyan' | 'violet' | 'emerald';
}

export const GlowBadge: React.FC<GlowBadgeProps> = ({
  label,
  className,
  glowColor = 'cyan',
}) => {
  const colorMap = {
    cyan: 'bg-cyan-500/10 text-cyan-700 border-cyan-300 shadow-glow-cyan',
    violet: 'bg-violet-500/10 text-violet-700 border-violet-300 shadow-glow-violet',
    emerald: 'bg-emerald-500/10 text-emerald-700 border-emerald-300 shadow-glow-emerald',
  };

  return (
    <motion.span
      whileHover={{ scale: 1.05 }}
      className={cn(
        'inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-mono font-semibold border backdrop-blur-sm transition-shadow',
        colorMap[glowColor],
        className
      )}
    >
      <span className="w-2 h-2 rounded-full bg-current animate-pulse" />
      {label}
    </motion.span>
  );
};
