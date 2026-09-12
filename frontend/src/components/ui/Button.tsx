import React from 'react';
import { cn } from '../../lib/utils';
import { Loader2 } from 'lucide-react';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'glow' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  isLoading?: boolean;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
}

export const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      className,
      children,
      variant = 'primary',
      size = 'md',
      isLoading = false,
      leftIcon,
      rightIcon,
      disabled,
      ...props
    },
    ref
  ) => {
    const baseStyles =
      'inline-flex items-center justify-center font-medium rounded-xl transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-[#0B0C0E] disabled:opacity-40 disabled:cursor-not-allowed select-none active:scale-[0.98]';

    const variants = {
      primary:
        'bg-white text-[#0B0C0E] hover:bg-[#E5E7EB] border border-white/80 font-semibold shadow-[0_0_20px_rgba(255,255,255,0.12)] focus:ring-white',
      secondary:
        'bg-[#1A1B1F] text-[#F0F1F3] border border-[#27292F] hover:bg-[#27292F] hover:border-[#3E4148] shadow-sm focus:ring-[#5C6069]',
      outline:
        'border border-[#3E4148] text-[#D4D7DC] hover:bg-[#1A1B1F] hover:text-white hover:border-[#5C6069] bg-[#111215]/60 backdrop-blur-sm focus:ring-[#5C6069]',
      ghost:
        'text-[#B2B6BD] hover:bg-[#1A1B1F] hover:text-white focus:ring-[#3E4148]',
      glow:
        'bg-gradient-to-r from-white via-[#E5E7EB] to-[#B2B6BD] text-[#0B0C0E] hover:brightness-105 shadow-[0_0_25px_rgba(255,255,255,0.2)] focus:ring-white font-semibold tracking-wide',
      danger:
        'bg-[#2A1215] text-[#FCA5A5] border border-[#7F1D1D]/70 hover:bg-[#3B181C] focus:ring-rose-500 shadow-sm',
    };

    const sizes = {
      sm: 'text-xs px-3 py-1.5 gap-1.5',
      md: 'text-sm px-4 py-2 gap-2',
      lg: 'text-base px-6 py-3 gap-2.5',
    };

    return (
      <button
        ref={ref}
        disabled={disabled || isLoading}
        className={cn(baseStyles, variants[variant], sizes[size], className)}
        {...props}
      >
        {isLoading ? (
          <Loader2 className="w-4 h-4 animate-spin text-current" />
        ) : (
          leftIcon
        )}
        {children}
        {!isLoading && rightIcon}
      </button>
    );
  }
);

Button.displayName = 'Button';
