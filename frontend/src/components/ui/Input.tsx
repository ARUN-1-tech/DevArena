import React from 'react';
import { cn } from '../../lib/utils';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  leftIcon?: React.ReactNode;
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ className, label, error, leftIcon, id, ...props }, ref) => {
    const inputId = id || (label ? label.toLowerCase().replace(/\s+/g, '-') : undefined);

    return (
      <div className="w-full space-y-1.5">
        {label && (
          <label htmlFor={inputId} className="block text-xs font-semibold text-[#D4D7DC] uppercase tracking-wider">
            {label}
          </label>
        )}
        <div className="relative flex items-center">
          {leftIcon && (
            <div className="absolute left-3 text-[#6C717B] pointer-events-none flex items-center">
              {leftIcon}
            </div>
          )}
          <input
            id={inputId}
            ref={ref}
            className={cn(
              'w-full bg-[#111215] border border-[#27292F] rounded-xl py-2.5 text-sm text-white placeholder:text-[#5C6069] transition-all duration-150',
              'focus:outline-none focus:ring-2 focus:ring-white/30 focus:border-[#4A4E57] focus:bg-[#141518]',
              leftIcon ? 'pl-9 pr-4' : 'px-4',
              error && 'border-[#7F1D1D] focus:ring-rose-500',
              className
            )}
            {...props}
          />
        </div>
        {error && <p className="text-xs text-[#F87171] font-medium">{error}</p>}
      </div>
    );
  }
);

Input.displayName = 'Input';
