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
      'inline-flex items-center justify-center font-medium rounded-xl transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-[#0D0506] disabled:opacity-40 disabled:cursor-not-allowed select-none active:scale-[0.98]';

    const variants = {
      primary:
        'bg-[#EFEFE1] text-[#0D0506] hover:bg-white border border-[#D1C7BD] font-semibold shadow-[0_0_20px_rgba(239,239,225,0.18)] focus:ring-[#D1C7BD]',
      secondary:
        'bg-[#220D0F] text-[#EFEFE1] border border-[#3A1417] hover:bg-[#321317] hover:border-[#5A1E22] shadow-sm focus:ring-[#72282D]',
      outline:
        'border border-[#3A1417] text-[#D9D9D9] hover:bg-[#220D0F] hover:text-[#EFEFE1] hover:border-[#AC9C8D]/50 bg-[#160809]/60 backdrop-blur-sm focus:ring-[#AC9C8D]',
      ghost:
        'text-[#D1C7BD] hover:bg-[#220D0F] hover:text-[#EFEFE1] focus:ring-[#5A1E22]',
      glow:
        'bg-gradient-to-r from-[#72282D] via-[#8E3239] to-[#72282D] text-[#EFEFE1] hover:brightness-110 border border-[#A6464E]/50 shadow-[0_0_25px_rgba(114,40,45,0.45)] focus:ring-[#72282D] font-semibold tracking-wide',
      danger:
        'bg-[#3A1417] text-[#FCA5A5] border border-[#72282D]/80 hover:bg-[#4E1B20] focus:ring-rose-500 shadow-sm',
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
