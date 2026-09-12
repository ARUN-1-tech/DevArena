import React from 'react';
import { cn } from '../../lib/utils';

export interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?: 'default' | 'neutral' | 'success' | 'warning' | 'info' | 'purple' | 'danger' | 'cyan' | 'platinum';
  size?: 'sm' | 'md';
}

export const Badge: React.FC<BadgeProps> = ({
  className,
  children,
  variant = 'default',
  size = 'md',
  ...props
}) => {
  const variants = {
    default: 'bg-[#220D0F] text-[#D1C7BD] border-[#3A1417]',
    neutral: 'bg-[#220D0F] text-[#D1C7BD] border-[#3A1417]',
    platinum: 'bg-[#EFEFE1]/15 text-[#EFEFE1] border-[#EFEFE1]/30 shadow-xs',
    success: 'bg-[#0E2018] text-[#34D399] border-[#065F46]/60',
    warning: 'bg-[#24180A] text-[#FBBF24] border-[#78350F]/70',
    info: 'bg-[#121E2A] text-[#7DD3FC] border-[#0369A1]/60',
    cyan: 'bg-[#161D24] text-[#E2E8F0] border-[#38BDF8]/40',
    purple: 'bg-[#240C12] text-[#EFEFE1] border-[#72282D]/70',
    danger: 'bg-[#3A1417] text-[#FCA5A5] border-[#72282D]/80',
  };

  const sizes = {
    sm: 'text-[10px] px-2 py-0.5',
    md: 'text-xs px-2.5 py-0.5',
  };

  return (
    <span
      className={cn(
        'inline-flex items-center gap-1 font-medium border rounded-full font-mono uppercase tracking-wider',
        variants[variant],
        sizes[size],
        className
      )}
      {...props}
    >
      {children}
    </span>
  );
};
