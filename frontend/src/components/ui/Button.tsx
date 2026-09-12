import React from 'react';
import { cn } from '../../lib/utils';
import { Loader2 } from 'lucide-react';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'glow' | 'premium' | 'danger';
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
      'inline-flex items-center justify-center font-semibold rounded-xl transition-all duration-200 cubic-bezier(0.16, 1, 0.3, 1) focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed select-none active:scale-[0.97] hover:-translate-y-0.5 cursor-pointer';

    const variants = {
      primary:
        'bg-gradient-to-r from-cyan-600 to-blue-600 text-white hover:from-cyan-500 hover:to-blue-500 focus:ring-cyan-500 shadow-sm hover:shadow-md hover:shadow-cyan-500/20 border border-cyan-500/30',
      premium:
        'bg-gradient-to-r from-indigo-600 via-indigo-500 to-violet-600 text-white hover:from-indigo-500 hover:to-violet-500 focus:ring-indigo-500 shadow-md shadow-indigo-500/25 hover:shadow-lg hover:shadow-indigo-500/35 border border-indigo-400/40 font-bold tracking-tight',
      secondary:
        'bg-slate-900 text-white hover:bg-slate-800 focus:ring-slate-900 shadow-sm hover:shadow-md border border-slate-700/50',
      outline:
        'border border-slate-200/90 text-slate-700 hover:bg-slate-50 hover:text-slate-900 hover:border-slate-300 focus:ring-slate-400 bg-white/90 backdrop-blur-md shadow-2xs hover:shadow-sm',
      ghost:
        'text-slate-600 hover:bg-indigo-50/60 hover:text-indigo-700 focus:ring-indigo-400',
      glow:
        'bg-gradient-to-r from-cyan-500 via-indigo-500 to-violet-600 text-white hover:brightness-105 shadow-glow-indigo focus:ring-indigo-400 font-bold tracking-wide border border-white/20',
      danger:
        'bg-gradient-to-r from-rose-600 to-red-600 text-white hover:from-rose-500 hover:to-red-500 focus:ring-rose-500 shadow-sm hover:shadow-rose-500/25 border border-rose-500/30',
    };

    const sizes = {
      sm: 'text-xs px-3 py-1.5 gap-1.5 rounded-lg',
      md: 'text-sm px-4 py-2 gap-2 rounded-xl',
      lg: 'text-base px-6 py-2.5 gap-2.5 rounded-xl',
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
