import React from 'react';
import { cn } from '../../lib/utils';

export interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  hoverEffect?: boolean;
  glow?: 'cyan' | 'violet' | 'emerald' | 'silver' | 'none';
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
    silver: 'hover:shadow-glow-wine hover:border-[#72282D]/60',
    cyan: 'hover:shadow-[0_0_20px_-5px_rgba(114,40,45,0.4)] hover:border-[#72282D]',
    violet: 'hover:shadow-[0_0_20px_-5px_rgba(114,40,45,0.4)] hover:border-[#72282D]',
    emerald: 'hover:shadow-[0_0_20px_-5px_rgba(16,185,129,0.25)] hover:border-[#10B981]/50',
  };

  return (
    <div
      className={cn(
        'bg-[#190C0E]/90 backdrop-blur-xl rounded-2xl border border-[#3A1417] shadow-luxury-card p-6 transition-all duration-200 text-[#EFEFE1]',
        hoverEffect && 'hover:-translate-y-0.5 hover:border-[#5A1E22] hover:shadow-luxury cursor-pointer',
        glowStyles[glow],
        className
      )}
      {...props}
    >
      {children}
    </div>
  );
};
