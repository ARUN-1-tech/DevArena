import React from 'react';
import { cn } from '../../lib/utils';

export interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  hoverEffect?: boolean;
  glow?: 'cyan' | 'violet' | 'emerald' | 'none';
}

export const Card: React.FC<CardProps> = ({
  className,
  children,
  hoverEffect = false,
  glow = 'none',
  ...props
}) => {
  const glowStyles = {
    none: '',
    cyan: 'hover:shadow-glow-cyan border-cyan-200/60',
    violet: 'hover:shadow-glow-violet border-violet-200/60',
    emerald: 'hover:shadow-glow-emerald border-emerald-200/60',
  };

  return (
    <div
      className={cn(
        'bg-white/90 backdrop-blur-md rounded-xl border border-slate-200/80 shadow-sm p-6 transition-all duration-200',
        hoverEffect && 'hover:-translate-y-1 hover:shadow-md cursor-pointer',
        glowStyles[glow],
        className
      )}
      {...props}
    >
      {children}
    </div>
  );
};
