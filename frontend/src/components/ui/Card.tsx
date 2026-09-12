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
    silver: 'hover:shadow-glow-silver hover:border-[#4A4E57]',
    cyan: 'hover:shadow-[0_0_20px_-5px_rgba(255,255,255,0.15)] hover:border-[#5C6069]',
    violet: 'hover:shadow-[0_0_20px_-5px_rgba(255,255,255,0.15)] hover:border-[#5C6069]',
    emerald: 'hover:shadow-[0_0_20px_-5px_rgba(16,185,129,0.25)] hover:border-[#10B981]/50',
  };

  return (
    <div
      className={cn(
        'bg-[#141518]/90 backdrop-blur-xl rounded-2xl border border-[#27292F] shadow-luxury-card p-6 transition-all duration-200 text-[#F0F1F3]',
        hoverEffect && 'hover:-translate-y-0.5 hover:border-[#3E4148] hover:shadow-luxury cursor-pointer',
        glowStyles[glow],
        className
      )}
      {...props}
    >
      {children}
    </div>
  );
};
