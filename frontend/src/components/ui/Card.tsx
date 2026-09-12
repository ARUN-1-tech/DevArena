import React from 'react';
import { cn } from '../../lib/utils';

export interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  hoverEffect?: boolean;
  glow?: 'cyan' | 'violet' | 'emerald' | 'gold' | 'indigo' | 'none';
  interactive?: boolean;
}

export const Card: React.FC<CardProps> = ({
  className,
  children,
  hoverEffect = false,
  interactive = false,
  glow = 'none',
  ...props
}) => {
  const glowStyles = {
    none: '',
    cyan: 'hover:shadow-glow-cyan border-cyan-200/70 hover:border-cyan-300',
    violet: 'hover:shadow-glow-violet border-violet-200/70 hover:border-violet-300',
    emerald: 'hover:shadow-glow-emerald border-emerald-200/70 hover:border-emerald-300',
    gold: 'hover:shadow-glow-gold border-amber-200/70 hover:border-amber-300',
    indigo: 'hover:shadow-glow-indigo border-indigo-200/70 hover:border-indigo-300',
  };

  const isInteractive = hoverEffect || interactive;

  return (
    <div
      className={cn(
        'bg-white/90 backdrop-blur-xl rounded-2xl border border-slate-200/80 shadow-premium p-6 transition-all duration-300 cubic-bezier(0.16, 1, 0.3, 1)',
        isInteractive &&
          'shimmer-card cursor-pointer hover:-translate-y-1 hover:shadow-premium-hover hover:border-indigo-300/80',
        glowStyles[glow],
        className
      )}
      {...props}
    >
      {children}
    </div>
  );
};
