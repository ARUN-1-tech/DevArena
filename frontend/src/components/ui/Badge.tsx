import React from 'react';
import { cn } from '../../lib/utils';

export interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?: 'default' | 'success' | 'warning' | 'info' | 'purple' | 'danger' | 'cyan' | 'gold' | 'indigo';
  size?: 'sm' | 'md' | 'lg';
  dot?: boolean;
}

export const Badge: React.FC<BadgeProps> = ({
  className,
  children,
  variant = 'default',
  size = 'md',
  dot = false,
  ...props
}) => {
  const variants = {
    default: 'bg-slate-100/90 text-slate-700 border-slate-200/90 shadow-2xs',
    success: 'bg-emerald-50/90 text-emerald-800 border-emerald-200/80 shadow-2xs shadow-emerald-500/10',
    warning: 'bg-amber-50/90 text-amber-800 border-amber-200/80 shadow-2xs shadow-amber-500/10',
    gold: 'bg-gradient-to-r from-amber-50 to-yellow-50 text-amber-900 border-amber-300/80 shadow-2xs shadow-amber-500/15',
    info: 'bg-cyan-50/90 text-cyan-800 border-cyan-200/80 shadow-2xs shadow-cyan-500/10',
    cyan: 'bg-cyan-50/90 text-cyan-800 border-cyan-200/80 shadow-2xs shadow-cyan-500/10',
    indigo: 'bg-indigo-50/90 text-indigo-800 border-indigo-200/80 shadow-2xs shadow-indigo-500/10',
    purple: 'bg-violet-50/90 text-violet-800 border-violet-200/80 shadow-2xs shadow-violet-500/10',
    danger: 'bg-rose-50/90 text-rose-800 border-rose-200/80 shadow-2xs shadow-rose-500/10',
  };

  const dotColors = {
    default: 'bg-slate-500',
    success: 'bg-emerald-500',
    warning: 'bg-amber-500',
    gold: 'bg-amber-500',
    info: 'bg-cyan-500',
    cyan: 'bg-cyan-500',
    indigo: 'bg-indigo-500',
    purple: 'bg-violet-500',
    danger: 'bg-rose-500',
  };

  const sizes = {
    sm: 'text-[10px] px-2 py-0.5 gap-1 font-semibold tracking-wider',
    md: 'text-xs px-2.5 py-0.5 gap-1.5 font-semibold tracking-wide',
    lg: 'text-sm px-3 py-1 gap-2 font-bold tracking-tight',
  };

  return (
    <span
      className={cn(
        'inline-flex items-center font-mono uppercase border rounded-full transition-all duration-200 backdrop-blur-xs select-none',
        variants[variant],
        sizes[size],
        className
      )}
      {...props}
    >
      {dot && (
        <span className="relative flex h-1.5 w-1.5 shrink-0">
          <span className={cn('animate-ping absolute inline-flex h-full w-full rounded-full opacity-75', dotColors[variant])} />
          <span className={cn('relative inline-flex rounded-full h-1.5 w-1.5', dotColors[variant])} />
        </span>
      )}
      {children}
    </span>
  );
};
