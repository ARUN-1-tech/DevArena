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
          <label htmlFor={inputId} className="block text-xs font-semibold text-[#D1C7BD] uppercase tracking-wider">
            {label}
          </label>
        )}
        <div className="relative flex items-center">
          {leftIcon && (
            <div className="absolute left-3 text-[#AC9C8D] pointer-events-none flex items-center">
              {leftIcon}
            </div>
          )}
          <input
            id={inputId}
            ref={ref}
            className={cn(
              'w-full bg-[#160809] border border-[#3A1417] rounded-xl py-2.5 text-sm text-[#EFEFE1] placeholder:text-[#8C7A70] transition-all duration-150',
              'focus:outline-none focus:ring-2 focus:ring-[#72282D]/40 focus:border-[#72282D] focus:bg-[#1E0C0E]',
              leftIcon ? 'pl-9 pr-4' : 'px-4',
              error && 'border-[#72282D] focus:ring-rose-500',
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
